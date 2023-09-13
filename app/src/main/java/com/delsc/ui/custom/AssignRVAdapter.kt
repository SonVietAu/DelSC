package com.delsc.ui.custom

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.model.Driver
import com.delsc.model.Order
import com.delsc.viewmodel.simpleDateFormat

class AssignRVAdapter(
    var undeliveredOrders: List<Pair<String, Order>>,
    val drivers: List<Driver>,
): RecyclerView.Adapter<AssignRVVH>() {

    override fun getItemCount(): Int  = undeliveredOrders.size

    var isAllSelected = false
    private var currentSelected = BooleanArray(undeliveredOrders.size, {false})

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignRVVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.assign_rv_view_holder, parent, false)

        return AssignRVVH(view)

    }

    fun selectDeselectAll() {
        if (isAllSelected) {
            for (i in 0..currentSelected.size-1) {
                currentSelected[i] = false
            }
            isAllSelected = false
        } else {
            for (i in 0..currentSelected.size-1) {
                currentSelected[i] = true
            }
            isAllSelected = true
        }
        notifyDataSetChanged()
    }

    fun changeOrdersList(orders: List<Pair<String, Order>>) {
        undeliveredOrders = orders
        currentSelected = BooleanArray(undeliveredOrders.size, {false})
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AssignRVVH, position: Int) {

        val order = undeliveredOrders[position].second

        holder.selectionChb.setOnCheckedChangeListener(null)
        holder.selectionChb.isChecked = currentSelected[position]
        holder.selectionChb.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("Text", "setting $isChecked to $position")
            currentSelected[position] = isChecked
        }

        holder.custNameTV.text = order.custName!!
        holder.ordered15kgTV.setText(" ${order.ordered15KgQnty} ")
        holder.ordered10kgTV.setText(" ${order.ordered10KgQnty} ")

        holder.reqDelDateTV.text = " Requested for: ${displayDelDateFormat.format(simpleDateFormat.parse(order.requestedDeliveryDate))} "

        val bg = holder.itemView.background as GradientDrawable
        if (order.driverName != null) {
            val driver = drivers.first {
                it.name == order.driverName
            }
            holder.assigneeTV.text = " ${order.driverName} "
            bg.run {
                mutate()
                colors = intArrayOf(Color.WHITE, driver.color!!)
            }
        } else {
            holder.assigneeTV.text = " -- "
            bg.run {
                mutate()
                colors = intArrayOf(Color.WHITE, holder.assigneeTV.context.getColor(R.color.unassigned))
            }
        }

    }

    fun getSelectedOrders(): Map<String, Order> {
        return undeliveredOrders.filterIndexed { index, pair ->
            currentSelected[index]
        }.associate {
            it
        }
    }

}

class AssignRVVH(view: View): RecyclerView.ViewHolder(view) {
    val selectionChb: CheckBox = itemView.findViewById(R.id.selectionChb)

    val custNameTV: TextView = itemView.findViewById(R.id.custNameTV)
    val ordered15kgTV: TextView = itemView.findViewById(R.id.ordered15kgTV)
    val ordered10kgTV: TextView = itemView.findViewById(R.id.ordered10kgTV)

    val assigneeTV: TextView = itemView.findViewById(R.id.assigneeTV)

    val reqDelDateTV: TextView = itemView.findViewById(R.id.reqDelDateLbl)

}