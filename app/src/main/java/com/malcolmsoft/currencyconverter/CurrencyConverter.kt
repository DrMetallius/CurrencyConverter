package com.malcolmsoft.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import kotlin.math.abs

class CurrencyConverter : AppCompatActivity() {
	private val model by lazy {
		ViewModelProviders.of(this)[CurrencyConverterModel::class.java]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val recyclerView = RecyclerView(this)
		recyclerView.layoutManager = LinearLayoutManager(this)

		val diffCallback = object : DiffUtil.ItemCallback<CurrencyEntry>() {
			override fun areItemsTheSame(oldItem: CurrencyEntry, newItem: CurrencyEntry) = oldItem.currency == newItem.currency

			override fun areContentsTheSame(oldItem: CurrencyEntry, newItem: CurrencyEntry) = oldItem == newItem

			override fun getChangePayload(oldItem: CurrencyEntry, newItem: CurrencyEntry) =
				newItem.value.takeIf { oldItem.currency == newItem.currency }
		}

		val adapter = object : ListAdapter<CurrencyEntry, CurrencyViewHolder>(diffCallback) {
			private val inflater = LayoutInflater.from(this@CurrencyConverter)

			fun CurrencyViewHolder.setFormattedValue(value: Double) {
				val formattedValue = String.format(Locale.ENGLISH, "%4f", value)
				field.setText(formattedValue)
			}

			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
				return CurrencyViewHolder(inflater.inflate(R.layout.currency_converter_entry, parent, false)).apply {
					field.addTextChangedListener(object : TextWatcher {
						override fun afterTextChanged(s: Editable) {
							if (adapterPosition > 0) return
							val value = s.toString().toDoubleOrNull() ?: return
							model.setSelectedCurrencyValue(value)
						}

						override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

						override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
					})

					field.setOnFocusChangeListener { _, hasFocus ->
						val item = getItem(adapterPosition)
						if (hasFocus) model.setSelectedCurrency(item.currency)
					}
				}
			}

			override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int, payloads: MutableList<Any>) {
				payloads.firstOrNull()?.let {
					val newValue = it as Double
					val currValue = holder.field.text.toString().toDoubleOrNull()
					if (currValue == null || abs(currValue - newValue) > 0.00001) holder.setFormattedValue(newValue)
				} ?: run {
					super.onBindViewHolder(holder, position, payloads)
				}
			}

			override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
				val (currency, value) = getItem(position)
				holder.label.text = currency

				holder.setFormattedValue(value)
			}
		}
		recyclerView.adapter = adapter

		setContentView(recyclerView)

		model.getDataAndSetUp()
		model.shownCurrencyData.observe(this, Observer(adapter::submitList))
	}

	private class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val label: TextView = itemView.findViewById(android.R.id.text1)
		val field: EditText = itemView.findViewById(android.R.id.edit)
	}
}

