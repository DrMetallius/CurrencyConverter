package com.malcolmsoft.currencyconverter

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URL

val CLIENT = OkHttpClient()

private val API_URL = URL("https://revolut.duckdns.org/latest?base=EUR")

private const val KEY_BASE = "base"
private const val KEY_RATES = "rates"

fun getServerRates(): RatesData {
	val request = Request.Builder()
		.get()
		.url(API_URL)
		.build()
	val response = CLIENT.newCall(request).execute()

	if (!response.isSuccessful) throw IOException("Rates service is unavailable")

	val body = response.body() ?: throw IOException("Missing server response body")
	val rootObject = JSONObject(body.string())
	val base = rootObject.getString(KEY_BASE)
	val ratesObject = rootObject.getJSONObject(KEY_RATES)
	val rates = ratesObject.keys()
		.asSequence()
		.associateWith(ratesObject::getDouble)

	return RatesData(base, rates)
}

data class RatesData(val base: String, val rates: Map<String, Double>)