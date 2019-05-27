package com.malcolmsoft.currencyconverter

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RatesApiTest {
	@Test
	fun testGetRates() {
		val (base, rates) = getServerRates()

		Assert.assertEquals("EUR", base)
		Assert.assertFalse("EUR" in rates.keys)
	}
}