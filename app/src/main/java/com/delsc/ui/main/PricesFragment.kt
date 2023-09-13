package com.delsc.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.model.Price

class PricesFragment : Fragment(){

    companion object {
        fun newInstance() = PricesFragment()
    }

    lateinit var viewModel: MainViewModel

    lateinit var  cancelTV: TextView
    lateinit var  saveTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_prices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelTV = view.findViewById(R.id.cancelTV)
        saveTV = view.findViewById(R.id.saveTV)

        cancelTV.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        saveTV.setOnClickListener {

            val pricesLV = view.findViewById<ListView>(R.id.pricesLV)

            Log.d("Text", "Start")
            viewModel.prices.values.forEachIndexed { index, price ->
                price.dollarValue = (pricesLV.adapter as PricesAdapter).getNewPrice(index)

            }

            viewModel.savePrices()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        val pricesLV = view?.findViewById<ListView>(R.id.pricesLV)
        pricesLV?.adapter = PricesAdapter(this.requireContext(), viewModel.prices.values.toList())

    }

    class PricesAdapter(val context: Context, val prices: List<Price>): BaseAdapter() {

        val tempPriceHolder = Array(prices.size, {index -> prices[index].dollarValue})

        override fun getCount(): Int {
            return prices.count()
        }

        override fun getItem(p0: Int): Price {
            return prices.get(p0)
        }

        fun getNewPrice(p0: Int): Double {
            return tempPriceHolder[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, viewHolder: View?, parentView: ViewGroup?): View {
            if (viewHolder == null) {
                val priceVH = LayoutInflater.from(context)
                    .inflate(R.layout.price_view_holder, parentView, false)

                val price = prices[p0]
                priceVH.findViewById<TextView>(R.id.labelTV).text = "${price.bundleSize} - ${price.priceGroupApplication}: "
                priceVH.findViewById<EditText>(R.id.priceET).setText("${price.dollarValue}")

                priceVH.findViewById<EditText>(R.id.priceET).doAfterTextChanged {
                    val newPriceText = it?.toString()
                    if (!newPriceText.isNullOrEmpty())
                        tempPriceHolder[p0] = newPriceText.toDouble()
                }

                return priceVH

            } else {
                return viewHolder
            }
        }

    }

}