package com.delsc.ui.custom

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.model.*
import com.delsc.viewmodel.simpleDateFormat

class DeliveredRVAdapter(deliveries: List<Pair<String, Order>>, val drivers: List<Driver>, val customers: List<Customer>, val editDelivered: (String, Order) -> Unit, val isShowingDelDate: Boolean = false): RecyclerView.Adapter<DeliveredRVViewHolder>() {

    private var _deliveries = deliveries

    fun changeDeliveredList(deliveries: List<Pair<String, Order>>) {
        _deliveries = deliveries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveredRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivered_rv_view_holder, parent, false)

        return DeliveredRVViewHolder(view, drivers, isShowingDelDate)
    }

    override fun onBindViewHolder(holder: DeliveredRVViewHolder, position: Int) {
        val order = _deliveries[position].second
        val customer = customers.first { it.name == order.custName }
        holder.custNameTV.text = order.custName!!

        holder.delivered15KgAmtTV.setText(" ${order.delivered15kgQnty} ")
        holder.delivered10KgAmtTV.setText(" ${order.delivered10kgQnty} ")

        holder.itemView.setOnClickListener {
            editDelivered(_deliveries[position].first, order)
        }

        if (customer.paymentType == PaymentType.Transfer) {
            holder.paidTV.setText(" T: $${order.fifteenKgDollarValue + order.tenKgDollarValue} ")
            holder.paidTV.background = holder.paidTV.context.getDrawable(R.drawable.background_unpaid_background)
        } else if (order.paidAmt > 0) {
            holder.paidTV.setText(" ${order.paidAmt} ")
            holder.paidTV.background = holder.paidTV.context.getDrawable(R.drawable.background_done_background)
        } else {
            holder.paidTV.setText(" U: $${order.fifteenKgDollarValue + order.tenKgDollarValue} ")
            holder.paidTV.background = holder.paidTV.context.getDrawable(R.drawable.background_unpaid_background)
        }

        if (order.deliveredDatetime == null) {
            Log.d("Text", "Null Del date: $order")
            holder.deliveryDateTV.text = "Null Del date"
        } else {
            holder.deliveryDateTV.text =
                displayDelDateFormat.format(simpleDateFormat.parse(order.deliveredDatetime!!))
        }

        for (driver in drivers) {
            if (driver.name == order.driverName) {
                val bg = holder.itemView.background as GradientDrawable
                bg.run {
                    mutate()
                    colors = intArrayOf(Color.WHITE, driver.color!!)
                }
                break
            }
        }

    }

    override fun getItemCount(): Int {
        return _deliveries.count()
    }

}

class DeliveredRVViewHolder(itemView: View, val drivers: List<Driver>, val isShowingDelDate: Boolean = false): RecyclerView.ViewHolder(itemView) {
    val custNameTV: TextView = itemView.findViewById(R.id.custNameTV)
    val delivered15KgAmtTV: TextView = itemView.findViewById(R.id.delivered15KgAmtTV)
    val delivered10KgAmtTV: TextView = itemView.findViewById(R.id.delivered10KgAmtTV)
    val paidTV: TextView = itemView.findViewById(R.id.paidTV)
    val deliveryDateTV: TextView = itemView.findViewById(R.id.deliveryDateTV)

    init {
        if (isShowingDelDate) {
            paidTV.visibility = View.GONE
            deliveryDateTV.visibility = View.VISIBLE
        } // else is defaulted other way
    }
}