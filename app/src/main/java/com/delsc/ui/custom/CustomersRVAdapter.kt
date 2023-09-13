package com.delsc.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.model.Customer
import com.delsc.model.PaymentType
import com.delsc.model.PriceGroup
import com.delsc.viewmodel.alternatingColor1

class CustomersRVAdapter(val context: Context, val customers: Map<String, Customer>, val editCust: (String, Customer) -> Unit): RecyclerView.Adapter<CustomerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_view_holder, parent, false)
        return CustomerHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerHolder, position: Int) {

        val key = customers.keys.elementAt(position)
        val customer = customers.get(key)!!
        holder.custNameTV.text = customer.name
        if (customer.priceGroup == PriceGroup.Cabramatta)
            holder.priceGroupSpn.setSelection(0)
        else holder.priceGroupSpn.setSelection(1)

        if (customer.paymentType == PaymentType.COD)
            holder.paymentTypeSpn.setSelection(0)
        else holder.paymentTypeSpn.setSelection(1)

        val bgDrawable = context.getResources().getDrawable(R.drawable.background_unassigned_btn,null) as GradientDrawable


        if (position % 2 == 0)
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(Color.CYAN, Color.CYAN)
            }
        else
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(alternatingColor1, alternatingColor1)
            }

        holder.itemView.background = bgDrawable

        holder.editBtn.setOnClickListener {
            editCust(key, customer)
        }
    }

    override fun getItemCount(): Int = customers.size

}

class CustomerHolder(holderView: View): RecyclerView.ViewHolder(holderView) {
    val custNameTV = holderView.findViewById<TextView>(R.id.custNameTV)
    val priceGroupSpn = holderView.findViewById<Spinner>(R.id.priceGroupSpn)
    val paymentTypeSpn = holderView.findViewById<Spinner>(R.id.paymentTypeSpn)
    val editBtn = holderView.findViewById<TextView>(R.id.editBtn)
    val historyBtn = holderView.findViewById<TextView>(R.id.historyBtn)

    init {
        priceGroupSpn.isEnabled = false
        paymentTypeSpn.isEnabled = false
    }
}
