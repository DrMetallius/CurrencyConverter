package com.malcolmsoft.currencyconverter

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors
import kotlin.math.abs

class CurrencyConverterModel(application: Application) : AndroidViewModel(application) {
	private val executor = Executors.newSingleThreadExecutor()

	private val ratesDataMutable = MutableLiveData<Map<String, Double>>()
	private val selectedCurrencyDataMutable = MutableLiveData<Pair<String, Double>>()

	val shownCurrencyData = combine(ratesDataMutable, selectedCurrencyDataMutable, executor, merger = { data, selectedCurrencyData ->
		if (data == null || selectedCurrencyData == null) return@combine null

		val (selectedCurrency, selectedValue) = selectedCurrencyData
		val sortedCurrencies = listOf(selectedCurrency) + (data.keys - selectedCurrency).sorted()
		sortedCurrencies.map { currency ->
			CurrencyEntry(currency, data.getValue(currency) * selectedValue / data.getValue(selectedCurrency))
		}
	})

	@MainThread
	fun getDataAndSetUp() {
		if (ratesDataMutable.value != null) return

		executor.submit {
			val (base, rates) = getServerRates()

			val baseCurrencyData = Pair(base, 1.0)
			ratesDataMutable.postValue(rates + baseCurrencyData)
			selectedCurrencyDataMutable.postValue(baseCurrencyData)
		}
	}

	@MainThread
	fun setSelectedCurrency(currency: String) {
		val (oldCurrency, _) = selectedCurrencyDataMutable.value ?: return
		if (currency != oldCurrency) {
			val newValue = shownCurrencyData.value?.find { it.currency == currency }?.value ?: 1.0
			selectedCurrencyDataMutable.value = Pair(currency, newValue)
		}
	}

	@MainThread
	fun setSelectedCurrencyValue(value: Double) {
		val (currency, oldValue) = selectedCurrencyDataMutable.value ?: return
		if (abs(oldValue - value) > 0.000001) selectedCurrencyDataMutable.value = Pair(currency, value)
	}

	override fun onCleared() {
		executor.shutdown()
	}
}

data class CurrencyEntry(val currency: String, val value: Double)
