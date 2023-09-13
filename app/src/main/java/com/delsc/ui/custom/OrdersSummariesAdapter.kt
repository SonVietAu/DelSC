package com.delsc.ui.custom

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.delsc.R
import com.delsc.model.*

class OrdersSummariesAdapter(
    val context: Context,
    val ordersSummaries: Map<OrdersSummary.Key, OrdersSummary>,
    val drivers: List<Driver>
) : BaseAdapter() {
    override fun getCount(): Int {
        return ordersSummaries.count()
    }

    override fun getItem(p0: Int): OrdersSummary {
        val keys = ordersSummaries.keys
        return ordersSummaries.get(keys.elementAt(p0))!!
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, viewHolder: View?, parentView: ViewGroup?): View {
        if (viewHolder == null) {
            val newViewHolder =
                LayoutInflater.from(context).inflate(R.layout.order_summary_view_holder, null)

            val driverNameAndReqDelDateTV =
                newViewHolder.findViewById<TextView>(R.id.driverNameAndReqDelDateTV)
            val delsCountTV = newViewHolder.findViewById<TextView>(R.id.delsCountTV)
            val ordered15kgTV = newViewHolder.findViewById<TextView>(R.id.ordered15kgTV)
            val ordered10kgTV = newViewHolder.findViewById<TextView>(R.id.ordered10kgTV)

            val key = ordersSummaries.keys.elementAt(p0)
            val ordersSummary = ordersSummaries.get(key)!!

            val driver = drivers.firstOrNull() { it.name == key.driverName }

            driverNameAndReqDelDateTV.setText(key.toDisplayStr())

            delsCountTV.text = "Dels: ${ordersSummary.deliveriesCount}"

            ordered15kgTV.text = " ${ordersSummary.ordered15KgQnty} x 15kg "
            ordered10kgTV.text = " ${ordersSummary.ordered10KgQnty} x 10kg "

            newViewHolder.setBackgroundColor(driver?.color ?: context.getColor(R.color.unassigned))

            newViewHolder.isFocusable = false
            newViewHolder.isClickable = false

            return newViewHolder

        } else {
            return viewHolder
        }
    }

}