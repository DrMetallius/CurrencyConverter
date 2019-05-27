package com.malcolmsoft.currencyconverter

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicReference

private fun <T, R> makeInputNullable(fn: (T) -> R?): (T?) -> R? = { input -> input?.let { fn(input) } }

fun <T, R> LiveData<T>.map(fn: (T) -> R?): LiveData<R> = Transformations.map(this, makeInputNullable(fn))

fun <T, R> LiveData<T>.switchMap(fn: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, makeInputNullable(fn))

fun <T> MutableLiveData<T>.setValueFromAnyThread(value: T?) {
	if (Looper.getMainLooper() == Looper.myLooper()) {
		this.value = value
	} else {
		postValue(value)
	}
}

fun <T, U, R> combine(first: LiveData<T>, second: LiveData<U>, executor: ExecutorService? = null, merger: (T?, U?) -> R?): MediatorLiveData<R> =
	combine<T, U, Nothing, Nothing, R>(first, second, executor = executor) { firstValue, secondValue, _, _ ->
		merger(firstValue, secondValue)
	}

fun <T, U, V, W, R> combine(
	first: LiveData<T>,
	second: LiveData<U>,
	third: LiveData<V>? = null,
	fourth: LiveData<W>? = null,
	executor: ExecutorService? = null,
	merger: (T?, U?, V?, W?) -> R?
): MediatorLiveData<R> {
	fun MediatorLiveData<R>.calculate(firstValue: T?, secondValue: U?, thirdValue: V?, fourthValue: W?) {
		if (executor != null) {
			executor.submit {
				merger(firstValue, secondValue, thirdValue, fourthValue)?.let { result ->
					postValue(result)
				}
			}
		} else {
			merger(firstValue, secondValue, thirdValue, fourthValue)?.let { result ->
				value = result
			}
		}
	}

	return MediatorLiveData<R>().apply {
		addSource(first) { calculate(it, second.value, third?.value, fourth?.value) }
		addSource(second) { calculate(first.value, it, third?.value, fourth?.value) }
		third?.let {
			addSource(third) { calculate(first.value, second.value, it, fourth?.value) }
		}
		fourth?.let {
			addSource(fourth) { calculate(first.value, second.value, third?.value, it) }
		}
	}
}

class UiMessage<T>(value: T, val tag: Any? = null) {
	private val reference = AtomicReference<T>(value)

	fun takeValue(): T? = reference.getAndSet(null)
}