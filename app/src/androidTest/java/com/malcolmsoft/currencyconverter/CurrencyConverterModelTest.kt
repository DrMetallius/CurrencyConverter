package com.malcolmsoft.currencyconverter

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class CurrencyConverterModelTest {
	@JvmField
	@Rule
	val activityTestRule = ActivityTestRule<CurrencyConverter>(CurrencyConverter::class.java)

	@Test
	fun testGetDataAndSetUp() {
		val application = ApplicationProvider.getApplicationContext<Application>()
		val model = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(CurrencyConverterModel::class.java)

		activityTestRule.runOnUiThread {
			model.getDataAndSetUp()
		}

		fun checkSelectedCurrency(currencyToSelect: String, valueToSelect: Double) {
			val latch = CountDownLatch(1)

			activityTestRule.runOnUiThread {
				model.shownCurrencyData.observeForever { currencyData ->
					val (currency, value) = currencyData.asIterable().first()
					if (currencyToSelect == currency && abs(valueToSelect - value) <  0.1) latch.countDown()
				}
			}

			Assert.assertTrue(latch.await(5, TimeUnit.SECONDS))
		}

		checkSelectedCurrency("EUR", 1.0)

		val latch = CountDownLatch(1) // Could be rewritten with couroutines for simpler code
		activityTestRule.runOnUiThread {
			model.setSelectedCurrency("RUB")
			model.setSelectedCurrencyValue(100.0)
			latch.countDown()
		}
		Assert.assertTrue(latch.await(100, TimeUnit.MILLISECONDS))

		checkSelectedCurrency("RUB", 100.0)
	}
}