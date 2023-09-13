package com.delsc.ui.custom

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.model.*
import com.delsc.viewmodel.simpleDateFormat
import com.delsc.viewmodel.simpleDatetimeFormat

import java.util.*

class OrdersRVAdapter(
    orders: Map<String, Order>,
    val drivers: List<Driver>,
    val customers: List<Customer>,
    val prices: List<Price>,
    val deleteOrder: (String, Order) -> Unit,
    val completeOrder: (String, Order) -> Unit,
    var driver: Driver,
    var deliveringDatetime: Date,
    var showDeleteBtns: Boolean = false,
    var isShowing15kg: Boolean = true,
    var isShowingBothKgs: Boolean = false,
): RecyclerView.Adapter<OrderRVViewHolder>() {

    private var keys = orders.keys.toList()
    private var _orders = orders

    fun changeOrdersList(orders: Map<String, Order>) {
        keys = orders.keys.toList()
        _orders = orders
        notifyDataSetChanged()
    }

    fun changeGUIForBundleSize(bundleSize: BundleSize, firstVisIndex: Int, lastVisIndex: Int) {
        this.isShowing15kg = bundleSize == BundleSize.FifteenKg
        for (i in firstVisIndex..lastVisIndex) {
            notifyItemChanged(i)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_rv_view_holder, parent, false)

        return OrderRVViewHolder(view, drivers)
    }

    override fun onBindViewHolder(holder: OrderRVViewHolder, position: Int) {
        val order = _orders[keys[position]]!!

        val cust = customers.first {
            order.custName == it.name
        }
        holder.custNameTV.text = order.custName!!
        holder.ordered15kgTV.setText(" ${order.ordered15KgQnty} ")
        holder.ordered10kgTV.setText(" ${order.ordered10KgQnty} ")

        holder.reqDelDateTV.text = " Requested for: ${displayDelDateFormat.format(simpleDateFormat.parse(order.requestedDeliveryDate))} "

        holder.delivered15kgET.setText("${order.delivered15kgQnty}")
        holder.delivered10kgET.setText("${order.delivered10kgQnty}")

        if (isShowingBothKgs)
            holder.showBoth15And10Kgs()
        else
            holder.showHide15And10(isShowing15kg)

        if (position == 0) {
            holder.itemView.findViewById<View>(R.id.doneCL).visibility = View.VISIBLE
        }

        holder.custNameTV.setOnClickListener {
            if (holder.doneCL.visibility == View.VISIBLE) {
                holder.doneCL.visibility = View.GONE
            } else {
                holder.doneCL.visibility = View.VISIBLE
            }
        }

        holder.reqDelDateTV.setOnClickListener {
            if (holder.doneCL.visibility == View.VISIBLE) {
                holder.doneCL.visibility = View.GONE
            } else {
                holder.doneCL.visibility = View.VISIBLE
            }
        }

        if (order.delivered15kgQnty + order.delivered10kgQnty > 0)
            holder.doneBtn.visibility = View.VISIBLE
        else
            holder.doneBtn.visibility = View.INVISIBLE

        holder.ordered15kgTV.setOnClickListener {
            if (order.ordered15KgQnty != 0) {
                holder.itemView.findViewById<View>(R.id.doneCL).visibility = View.VISIBLE
                order.delivered15kgQnty = order.ordered15KgQnty
                holder.delivered15kgET.setText("${order.ordered15KgQnty}")
            }
        }

        holder.ordered10kgTV.setOnClickListener {
            if (order.ordered10KgQnty != 0) {
                holder.itemView.findViewById<View>(R.id.doneCL).visibility = View.VISIBLE
                order.delivered10kgQnty = order.ordered10KgQnty
                holder.delivered10kgET.setText("${order.ordered10KgQnty}")
            }
        }

        holder.delivered15kgET.doAfterTextChanged {
            order.delivered15kgQnty = if (it.isNullOrEmpty()) 0 else it.toString().toInt()
            val fifteenKgPrice = prices.first {
                cust.priceGroup == it.priceGroupApplication &&
                        it.bundleSize == BundleSize.FifteenKg
            }
            order.fifteenKgDollarValue = order.delivered15kgQnty * fifteenKgPrice.dollarValue
            holder.fifteenKgDollarValueTV.text = "$${order.fifteenKgDollarValue}"
            if (holder.paidSwt.isChecked) {
                holder.paidSwt.text = "Paid $${order.fifteenKgDollarValue + order.tenKgDollarValue}"
            }

            if (order.delivered15kgQnty + order.delivered10kgQnty > 0)
                holder.doneBtn.visibility = View.VISIBLE
            else
                holder.doneBtn.visibility = View.INVISIBLE
        }

        holder.delivered10kgET.doAfterTextChanged {
            order.delivered10kgQnty = if (it.isNullOrEmpty()) 0 else it.toString().toInt()
            val tenKgPrice = prices.first {
                cust.priceGroup == it.priceGroupApplication &&
                        it.bundleSize == BundleSize.TenKg
            }
            order.tenKgDollarValue = order.delivered10kgQnty * tenKgPrice.dollarValue
            holder.tenKgDollarValueTV.text = "$${order.tenKgDollarValue}"
            if (holder.paidSwt.isChecked) {
                holder.paidSwt.text = "Paid $${order.fifteenKgDollarValue + order.tenKgDollarValue}"
            }
            if (order.delivered15kgQnty + order.delivered10kgQnty > 0)
                holder.doneBtn.visibility = View.VISIBLE
            else
                holder.doneBtn.visibility = View.INVISIBLE
        }

        if (isShowingBothKgs)
            holder.delDateTimeTV.text = "Delivered: ${order.deliveredDatetime}"
        else
            holder.delDateTimeTV.text = "Del date time: ${displayDelDateFormat.format(deliveringDatetime)}"

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

        if (cust.paymentType == PaymentType.COD) {
            val paidCol = holder.paidSwt.context.getColor(R.color.paidCol)
            holder.paidSwt.isChecked = true
            holder.paidSwt.isEnabled = true
            holder.paidSwt.setTextColor(paidCol)
            holder.fifteenKgDollarValueTV.setTextColor(paidCol)
            holder.tenKgDollarValueTV.setTextColor(paidCol)
            holder.paidSwt.text = "Paid $${order.fifteenKgDollarValue + order.tenKgDollarValue}"
            holder.paidSwt.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    holder.paidSwt.setTextColor(paidCol)
                    holder.fifteenKgDollarValueTV.setTextColor(paidCol)
                    holder.tenKgDollarValueTV.setTextColor(paidCol)
                    holder.paidSwt.text = "Paid $${order.fifteenKgDollarValue + order.tenKgDollarValue}"
                } else {
                    holder.paidSwt.text = "COD Unpaid"
                    val unpaidCol = holder.paidSwt.context.getColor(R.color.unpaidCol)
                    holder.paidSwt.setTextColor(unpaidCol)
                    holder.fifteenKgDollarValueTV.setTextColor(unpaidCol)
                    holder.tenKgDollarValueTV.setTextColor(unpaidCol)
                }
            }
        } else {
            val text_color_primary = holder.paidSwt.context.getColor(R.color.text_color_primary)
            holder.paidSwt.setTextColor(text_color_primary)
            holder.paidSwt.text = "Transfer"
            holder.paidSwt.setOnCheckedChangeListener(null)
            holder.paidSwt.isChecked = false
            holder.paidSwt.isEnabled = false
        }

        if (showDeleteBtns) {
            holder.deletedBtn.visibility = View.VISIBLE
            holder.doneBtn.visibility = View.INVISIBLE
            holder.deletedBtn.setOnClickListener {
                deleteOrder(keys[position], order)
            }
        } else {
            holder.deletedBtn.visibility = View.INVISIBLE
        }

        holder.doneBtn.setOnClickListener {
            // Maybe a check that a value is entered into the deliveredET
            if ( (order.delivered15kgQnty + order.delivered10kgQnty) == 0) {
                Toast.makeText(holder.doneBtn.context, "Delivered amount is zero, please enter a 15kg or 10kg amount.", Toast.LENGTH_LONG).show()
            } else {

                val isForToday = simpleDateFormat.format(deliveringDatetime).equals(
                    simpleDateFormat.format(Date()))
                order.driverName = driver.name

                if (isForToday)
                    order.deliveredDatetime = simpleDatetimeFormat.format(Date())
                else
                    order.deliveredDatetime = simpleDatetimeFormat.format(deliveringDatetime)

                if (holder.paidSwt.isChecked) {
                    order.paidAmt = order.fifteenKgDollarValue + order.tenKgDollarValue
                }
                completeOrder(keys[position], order)
            }
        }

    }

    override fun getItemCount(): Int {
        return _orders.count()
    }

}

class OrderRVViewHolder(itemView: View, val drivers: List<Driver>): RecyclerView.ViewHolder(itemView) {
    val custNameTV: TextView
    val ordered15kgTV: TextView
    val ordered10kgTV: TextView

    val reqDelDateTV: TextView

    val doneCL: View

    val delivered15KgTV: TextView
    val delivered15kgET: EditText
    val fifteenKgDollarValueTV: TextView

    val delivered10kgTV: TextView
    val delivered10kgET: EditText
    val tenKgDollarValueTV: TextView

    val delDateTimeTV: TextView

    val assigneeTV: TextView
    val paidSwt: Switch

    val deletedBtn: TextView
    val doneBtn: TextView

    init {
        // Define click listener for the ViewHolder's View.
        custNameTV = itemView.findViewById(R.id.custNameTV)
        ordered15kgTV = itemView.findViewById(R.id.ordered15kgTV)
        ordered10kgTV = itemView.findViewById(R.id.ordered10kgTV)
        reqDelDateTV = itemView.findViewById(R.id.reqDelDateLbl)
        doneCL = itemView.findViewById(R.id.doneCL)
        delivered15KgTV = itemView.findViewById(R.id.delivered15KgTV)
        delivered15kgET = itemView.findViewById(R.id.delivered15kgET)
        delivered10kgTV = itemView.findViewById(R.id.delivered10kgTV)
        delivered10kgET = itemView.findViewById(R.id.delivered10kgET)
        fifteenKgDollarValueTV = itemView.findViewById(R.id.fifteenKgDollarValueTV)
        tenKgDollarValueTV = itemView.findViewById(R.id.tenKgDollarValueTV)
        delDateTimeTV = itemView.findViewById(R.id.delDateTimeTV)
        paidSwt = itemView.findViewById(R.id.paidSwt)
        assigneeTV = itemView.findViewById(R.id.assigneeTV)
        deletedBtn = itemView.findViewById(R.id.deleteBtn)
        doneBtn = itemView.findViewById(R.id.doneBtn)
    }

    fun showBoth15And10Kgs() {
        ordered15kgTV.visibility = View.VISIBLE
        ordered10kgTV.visibility = View.VISIBLE

        fifteenKgDollarValueTV.visibility = View.VISIBLE
        delivered15KgTV.visibility = View.VISIBLE
        delivered15kgET.visibility = View.VISIBLE
        tenKgDollarValueTV.visibility = View.VISIBLE
        delivered10kgTV.visibility = View.VISIBLE
        delivered10kgET.visibility = View.VISIBLE
    }

    fun showHide15And10(show15kg: Boolean) {
        if (show15kg) {
            ordered15kgTV.visibility = View.VISIBLE
            ordered10kgTV.visibility = View.INVISIBLE

            fifteenKgDollarValueTV.visibility = View.VISIBLE
            delivered15KgTV.visibility = View.VISIBLE
            delivered15kgET.visibility = View.VISIBLE
            tenKgDollarValueTV.visibility = View.GONE
            delivered10kgTV.visibility = View.GONE
            delivered10kgET.visibility = View.GONE
        } else {
            ordered15kgTV.visibility = View.INVISIBLE
            ordered10kgTV.visibility = View.VISIBLE

            fifteenKgDollarValueTV.visibility = View.INVISIBLE
            delivered15KgTV.visibility = View.INVISIBLE
            delivered15kgET.visibility = View.INVISIBLE
            tenKgDollarValueTV.visibility = View.VISIBLE
            delivered10kgTV.visibility = View.VISIBLE
            delivered10kgET.visibility = View.VISIBLE
        }

        if (show15kg) {
            doneBtn.background = doneBtn.context.getDrawable(R.drawable.background_done_btn)
            doneBtn.isEnabled = true
        } else {
            doneBtn.background = doneBtn.context.getDrawable(R.drawable.background_unassigned_btn)
            doneBtn.isEnabled = false
        }
    }
}