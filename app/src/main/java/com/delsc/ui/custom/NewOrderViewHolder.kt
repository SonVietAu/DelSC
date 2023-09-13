package com.delsc.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.delsc.R
import com.delsc.model.BundleSize
import com.delsc.model.Customer
import com.delsc.model.Driver
import com.delsc.model.Order
import com.delsc.model.PaymentType
import com.delsc.model.Price
import com.delsc.viewmodel.alternatingColor1
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat
import com.delsc.viewmodel.simpleDatetimeFormat
import java.util.Calendar
import java.util.Date

class NewOrderViewHolder(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    val mainChildView = LayoutInflater.from(context)
        .inflate(R.layout.new_order_view, this, true).findViewById<View>(R.id.newOrderCL)

    private var isUpdating15kgOrder = true // Not updating order then is updatign stock

    var isNewOrderCLPinned = true

    val newOrderCL: ConstraintLayout by lazy { findViewById<ConstraintLayout>(R.id.newOrderCL) }

    val customerNameSpn: Spinner by lazy { newOrderCL.findViewById(R.id.customerNameSpn) }
    val fifteenKgOrderQntyTV: TextView by lazy {
        findViewById<ViewGroup>(R.id.newOrderCL).findViewById(
            R.id.fifteenKgOrderQntyTV
        )
    }
    val tenKgOrderQntyTV: TextView by lazy { newOrderCL.findViewById(R.id.tenKgOrderQntyTV) }
    val minusOneBtn: TextView by lazy { newOrderCL.findViewById(R.id.minusOneBtn) }
    val plusOneBtn: TextView by lazy { newOrderCL.findViewById(R.id.plusOneBtn) }
    val minusFiveBtn: TextView by lazy { newOrderCL.findViewById(R.id.minusFiveBtn) }
    val plusFiveBtn: TextView by lazy { newOrderCL.findViewById(R.id.plusFiveBtn) }
    val reqDelDateLbl: TextView by lazy { newOrderCL.findViewById(R.id.reqDelDateLbl) }
    val deliveredSwt: Switch by lazy { newOrderCL.findViewById(R.id.deliveredSwt) }
    val paidSwt: Switch by lazy { newOrderCL.findViewById(R.id.paidSwt) }
    val requestDeliveryDateSpn: Spinner by lazy {
        newOrderCL.findViewById(
            R.id.requestDeliveryDateSpn
        )
    }
    val cancelTV: TextView by lazy { newOrderCL.findViewById(R.id.cancelTV) }
    val saveTV: TextView by lazy { newOrderCL.findViewById(R.id.saveTV) }
    val pinTV: TextView by lazy { newOrderCL.findViewById(R.id.pinTV) }
    val assigneeSpn: Spinner by lazy { newOrderCL.findViewById(R.id.assigneeSpn) }

    init {

        customerNameSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updatePayment()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        fifteenKgOrderQntyTV.setOnClickListener {
            isUpdating15kgOrder = true
            val newBg = context.getDrawable(R.drawable.background_fifteen_kg_btn)
            minusOneBtn.background = newBg
            plusOneBtn.background = newBg
            minusFiveBtn.background = newBg
            plusFiveBtn.background = newBg
        }

        tenKgOrderQntyTV.setOnClickListener {
            isUpdating15kgOrder = false
            val newBg = context.getDrawable(R.drawable.background_ten_kg_btn)
            minusOneBtn.background = newBg
            plusOneBtn.background = newBg
            minusFiveBtn.background = newBg
            plusFiveBtn.background = newBg
        }

        minusOneBtn.setOnClickListener {
            updateOrderOrStockAmount(-1)
        }

        plusOneBtn.setOnClickListener {
            updateOrderOrStockAmount(1)
        }

        minusFiveBtn.setOnClickListener {
            updateOrderOrStockAmount(-5)
        }

        plusFiveBtn.setOnClickListener {
            updateOrderOrStockAmount(5)
        }

        requestDeliveryDateSpn.adapter = RequestDelDateAdt(context)
        requestDeliveryDateSpn.setSelection(RequestDelDateAdt.asapIndex)
        requestDeliveryDateSpn.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateDeliveredAmt(deliveredSwt.isChecked)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        deliveredSwt.setOnCheckedChangeListener { compoundButton, isChecked ->
            updateDeliveredAmt(isChecked)
            if (!isChecked)
                paidSwt.isChecked = false
            else
                paidSwt.isEnabled = true
        }

        paidSwt.setOnCheckedChangeListener { compoundButton, b ->
            updatePayment()
        }

        pinTV.setOnClickListener {
            if (isNewOrderCLPinned) {
                pinTV.background = context.getDrawable(R.drawable.background_button)
                pinTV.setText("   Pin   ")
            } else {
                pinTV.background = context.getDrawable(R.drawable.background_button_pinned)
                pinTV.setText(" Unpin ")
            }
            isNewOrderCLPinned = !isNewOrderCLPinned
        }

    }

    private var _order: Order? = null
    val order: Order
        get() {
            if (_order == null)
                _order = Order(
                    orderedDatetime = simpleDatetimeFormat.format(Date()),
                )
            val result = _order!!

            result.apply {

                custName = (customerNameSpn.selectedItem as Customer).name

                requestedDeliveryDate = requestDeliveryDateSpn.selectedItem as String

                if (assigneeSpn.selectedItemPosition > 0) {
                    val driver = assigneeSpn.selectedItem as Driver
                    driverName =
                        driver.name
                } else if (deliveredSwt.isChecked && currentDriver != null) {
                    driverName = currentDriver!!.name
                } else {
                    driverName = null
                }

            }

            return result
        }

    fun populateCustomers(customers: Map<String, Customer>) {
        customerNameSpn.adapter = CustomersSpnAdaptor(context, customers)
    }

    private lateinit var prices: List<Price>
    fun populatePrices(prices: List<Price>) {
        this.prices = prices
    }

    fun populateDrivers(drivers: List<Driver>) {
        assigneeSpn.adapter =
            DriversSpnAdapterWithAnEmpty(context, drivers)
        assigneeSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val bg = mainChildView.background as GradientDrawable
                if (position > 0) {
                    val driver = assigneeSpn.selectedItem as Driver
                    bg.run {
                        mutate()
                        colors = intArrayOf(Color.WHITE, driver.color!!)
                    }
                } else {
                    bg.run {
                        mutate()
                        colors = intArrayOf(Color.WHITE, context.getColor(R.color.unassigned))
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private var currentDriver: Driver? = null
    fun setCurrentDriver(driver: Driver?) {
        currentDriver = driver
    }

    fun setOrder(newOrder: Order?) {

        _order = newOrder

        if (_order == null) {
            fifteenKgOrderQntyTV.text = " 0 x15kg "
            tenKgOrderQntyTV.text = " 0 x10kg "
            deliveredSwt.isChecked = false
        } else {
            // TODO: Editing an undelivered order
            for (i in 0..customerNameSpn.count - 1) {
                val customer = customerNameSpn.getItemAtPosition(i) as Customer
                if (customer.name.equals(order.custName)) {
                    customerNameSpn.setSelection(i)
                    break
                }
            }
            fifteenKgOrderQntyTV.text = " ${_order!!.ordered15KgQnty} x15kg "
            tenKgOrderQntyTV.text = " ${_order!!.ordered10KgQnty} x10kg "

            if (_order!!.delivered15kgQnty + _order!!.delivered10kgQnty > 0) {
                // TODO: What is the idea here?
            }

        }

    }

    private fun updateOrderOrStockAmount(changeQnty: Int) {

        if (isUpdating15kgOrder) {
            order.ordered15KgQnty += changeQnty
            if (order.ordered15KgQnty < 0)
                order.ordered15KgQnty = 0

            fifteenKgOrderQntyTV.setText(" ${order.ordered15KgQnty} x15kg ")
        } else {
            order.ordered10KgQnty += changeQnty
            if (order.ordered10KgQnty < 0) order.ordered10KgQnty = 0

            tenKgOrderQntyTV.setText(" ${order.ordered10KgQnty} x10kg ")
        }

        if (order.ordered15KgQnty == 0 && order.ordered10KgQnty == 0) {
            deliveredSwt.isChecked = false
            deliveredSwt.isEnabled = false
        } else {
            deliveredSwt.isEnabled = true
        }

        updateDeliveredAmt(deliveredSwt.isChecked)
        updatePayment()
    }

    fun updateDeliveredAmt(isChecked: Boolean) {

        if (isChecked) {

            val customer = customerNameSpn.selectedItem as Customer

            val strBuf = StringBuffer("Del: ")
            if (order.ordered15KgQnty > 0) {
                order.delivered15kgQnty = order.ordered15KgQnty
                val fifteenKgPrice = prices.first {
                    it.priceGroupApplication == customer.priceGroup &&
                            it.bundleSize == BundleSize.FifteenKg
                }
                order.fifteenKgDollarValue = order.delivered15kgQnty * fifteenKgPrice.dollarValue
                strBuf.append("${order.ordered15KgQnty} x15kg")
                if (order.ordered10KgQnty > 0) {
                    strBuf.append(", ")
                }
            }
            if (order.ordered10KgQnty > 0) {
                order.delivered10kgQnty = order.ordered10KgQnty
                val tenKgPrice = prices.first {
                    it.priceGroupApplication == customer.priceGroup &&
                            it.bundleSize == BundleSize.TenKg
                }
                order.tenKgDollarValue = order.delivered10kgQnty * tenKgPrice.dollarValue
                strBuf.append("${order.ordered10KgQnty} x10kg")
            }
            order.deliveredDatetime = requestDeliveryDateSpn.selectedItem as String
            reqDelDateLbl.text = "Del Date: "
            deliveredSwt.text = strBuf.toString()
            deliveredSwt.setTextColor(context.getColor(R.color.paidCol))
            saveTV.text = " Delivered "
        } else {
            reqDelDateLbl.text = "Req Del Date: "
            deliveredSwt.text = "Undelivered: "
            order.delivered15kgQnty = 0
            order.delivered10kgQnty = 0
            order.fifteenKgDollarValue = 0.0
            order.tenKgDollarValue = 0.0
            order.deliveredDatetime = null
            deliveredSwt.setTextColor(context.getColor(R.color.text_color_primary))
            paidSwt.isEnabled = false
            saveTV.text = " Save "
        }
    }

    fun updatePayment() {
        val customer = customerNameSpn.selectedItem as Customer

        if (customer.paymentType == PaymentType.Transfer) {
            paidSwt.text = "Transfer"
            paidSwt.isEnabled = false
            paidSwt.isChecked = false
            paidSwt.setTextColor(context.getColor(R.color.text_color_primary))
        } else {
            val strBuf = StringBuffer()
            if (paidSwt.isChecked) {

                order.paidAmt = order.fifteenKgDollarValue + order.tenKgDollarValue
                order.depositKey = "Undeposited"

                strBuf.append("Paid ")
                strBuf.append("$${order.paidAmt}: ")
                paidSwt.setTextColor(context.getColor(R.color.paidCol))
            } else {
                strBuf.append("Unpaid:")
                order.paidAmt = 0.0
                paidSwt.setTextColor(context.getColor(R.color.unpaidCol))
            }
            paidSwt.text = strBuf.toString()
        }
    }

    fun setOnSaveClickedListener(saveClickedFunction: () -> Unit) {
        saveTV.setOnClickListener {
            if (order.ordered15KgQnty + order.ordered10KgQnty == 0) {
                Toast.makeText(
                    context,
                    "Need at least one 15kg or 10kg bundle for new order",
                    Toast.LENGTH_LONG
                ).show()
            } else if (deliveredSwt.isChecked &&
                simpleDateFormat.parse(requestDeliveryDateSpn.selectedItem as String)!!
                    .after(Date())
            ) {
                Toast.makeText(
                    context,
                    "Delivery Date can not be in the future",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                saveClickedFunction.invoke()
                // Reset GUI
                setOrder(null)
            }
        }
    }

    fun setOnCancelClickListener(clickFunction: () -> Unit) {
        cancelTV.setOnClickListener {
            clickFunction.invoke()
        }
    }

    private class RequestDelDateAdt(val context: Context) : BaseAdapter() {

        companion object {
            val asapIndex = 7
        }

        val dates = buildList<Date> {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -7)
            for (i in 1..20) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                this.add(cal.time)
            }

        }

        init {
            for (date in dates) {
                Log.d("Text", simpleDateFormat.format(date))
            }
        }

        override fun getCount(): Int {
            return dates.size + 1
        }

        override fun getItem(p0: Int): String {
            return if (p0 < asapIndex) simpleDateFormat.format(dates[p0])
            else if (p0 == asapIndex) simpleDateFormat.format(Date())
            else simpleDateFormat.format(dates[p0 - 1])
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, viewHolder: View?, parentView: ViewGroup?): View {
            if (viewHolder == null) {
                val newViewHolder = TextView(context)
                newViewHolder.setLayoutParams(
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
                newViewHolder.textAlignment = View.TEXT_ALIGNMENT_CENTER
                if (p0 < asapIndex)
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0]))
                else if (p0 == asapIndex)
                    newViewHolder.setText(" A.s.a.p ")
                else
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0 - 1]))
                newViewHolder.textSize = 28f

                val bgDrawable = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.background_unassigned_btn,
                    null
                ) as GradientDrawable

                if (p0 % 2 == 0) {
                    if (p0 != 0)
                        bgDrawable.run {
                            mutate()
                            colors =
                                intArrayOf(Color.CYAN, Color.CYAN)
                        }
                } else
                    bgDrawable.run {
                        mutate()
                        colors =
                            intArrayOf(alternatingColor1, alternatingColor1)
                    }

                newViewHolder.background = bgDrawable

                return newViewHolder
            } else {
                val newViewHolder = viewHolder as TextView
                if (p0 < asapIndex)
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0]))
                else if (p0 == asapIndex)
                    newViewHolder.setText(" A.s.a.p ")
                else
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0 - 1]))
                newViewHolder.textSize = 28f
                return newViewHolder
            }

        }

    }

}