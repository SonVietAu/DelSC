package com.delsc.ui.main

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.*
import com.delsc.model.BundleSize
import com.delsc.model.Customer
import com.delsc.ui.custom.*
import com.delsc.ui.main.dialogfragment.DeleteDeliveredOrdersDF
import com.delsc.ui.main.dialogfragment.DeleteOrdersDF
import com.delsc.ui.main.dialogfragment.NewOrderDF
import com.delsc.viewmodel.MainViewModel
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat
import java.util.*


class DeliveriesFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var ordersRV: RecyclerView
    lateinit var deliveredRV: RecyclerView

    lateinit var customerNameSpn: Spinner
    lateinit var bundleSizeSpn: Spinner
    lateinit var bundleSizeFilterSpn: Spinner

    lateinit var deliveringDateTV: TextView
    lateinit var driverNameTV: TextView
    lateinit var noOrdersMsgTV: TextView
    lateinit var assignBtn: TextView
    lateinit var deliveriesSummaryTV: TextView
    lateinit var minusADayBtn: TextView
    lateinit var plusADayBtn: TextView
    lateinit var deleteOrdersBtn: TextView
    lateinit var newOrderBtn: TextView

    lateinit var dateFilterTV: TextView

    lateinit var currentRB: RadioButton
    lateinit var historyRB: RadioButton
    lateinit var historyCL: ConstraintLayout


    private var dateFilter = "Del Date: All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_deliveries, container, false)
    }

    override fun onDestroy() {
        (this.activity as MainActivity).isToShowMenuItems(false, false)
        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        val displayingDateStr = displayDelDateFormat.format(viewModel.deliveringDate)
        deliveringDateTV.text = "Today, $displayingDateStr"

        driverNameTV.text = viewModel.currentDriverUser?.name
        driverNameTV.setBackgroundColor(viewModel.currentDriverUser!!.color!!)

        viewModel.undeliveredOrdersMLD.observe(this.viewLifecycleOwner, {
            if (it.isEmpty()) {
                noOrdersMsgTV.text = "No Current Orders"
                assignBtn.isEnabled = false
            } else {
                noOrdersMsgTV.text = ""
                assignBtn.isEnabled = true
            }

            if (ordersRV.adapter != null && ordersRV.adapter is OrdersRVAdapter) {
                (ordersRV.adapter as OrdersRVAdapter).changeOrdersList(it)
            } else {
                ordersRV.adapter = OrdersRVAdapter(
                    it,
                    viewModel.drivers.values.toList(),
                    viewModel.customers.values.toList(),
                    viewModel.prices.values.toList(),
                    viewModel::deleteOrder,
                    viewModel::updateOrder,
                    viewModel.currentDriverUser!!,
                    viewModel.deliveringDate,
                    isShowing15kg = true
                )
            }

        })

        viewModel.deliveredOrdersMLD.observe(this.viewLifecycleOwner, {
            deliveriesSummaryTV.text = viewModel.currentDriverDeliveredSummary.toSummaryString()
            updateGUIInteractions()
            updateOrdersForTodayOrFutureDate()
        })

        customerNameSpn.adapter = CustomersSpnAdaptor(requireContext(), viewModel.customers, true)

        viewModel.filteredDeliveredOrdersMLD.observe(this.viewLifecycleOwner, {

            // TODO: Make this acceptable for Alek usage (ie, show only her deliveries and ensure the list is uptodate with every completed orders)

            if (deliveredRV.adapter is DeliveredRVAdapter) {
                val deliveredRVAdapter = deliveredRV.adapter as DeliveredRVAdapter
                deliveredRVAdapter.changeDeliveredList(it)
            } else {
                deliveredRV.adapter = DeliveredRVAdapter(
                    it,
                    viewModel.drivers.values.toList(),
                    viewModel.customers.values.toList(),
                    this::editDelivered,
                    true
                )
            }

            customerNameSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    filterHistory()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersRV = view.findViewById(R.id.ordersRV)
        deliveredRV = view.findViewById(R.id.deliveredRV)
        customerNameSpn = view.findViewById(R.id.customerNameSpn)
        bundleSizeSpn = view.findViewById(R.id.bundleSizeSpn)
        bundleSizeFilterSpn = view.findViewById(R.id.bundleSizeFilterSpn)

        deliveringDateTV = view.findViewById(R.id.deliveringDateTV)
        driverNameTV = view.findViewById(R.id.driverNameTV)
        noOrdersMsgTV = view.findViewById(R.id.noOrdersMsgTV)
        assignBtn = view.findViewById(R.id.assignBtn)
        deliveriesSummaryTV = view.findViewById(R.id.deliveriesSummaryTV)

        minusADayBtn = view.findViewById(R.id.minusADayBtn)
        plusADayBtn = view.findViewById(R.id.plusADayBtn)
        deleteOrdersBtn = view.findViewById(R.id.deleteOrdersBtn)
        newOrderBtn = view.findViewById(R.id.newOrderBtn)
        dateFilterTV = view.findViewById(R.id.dateFilterTV)

        currentRB = view.findViewById(R.id.currentRB)
        historyRB = view.findViewById(R.id.historyRB)
        historyCL = view.findViewById(R.id.historyCL)

        bundleSizeSpn.adapter = BundleSizeAdapter(requireContext())
        bundleSizeSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val bundleSize = bundleSizeSpn.selectedItem as BundleSize
                if (ordersRV.adapter is OrdersRVAdapter) {
                    val ordersRVAdapter = ordersRV.adapter as OrdersRVAdapter
                    val firstVisibleIndex =
                        (ordersRV.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val lastVisibleIndex =
                        (ordersRV.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    ordersRVAdapter.changeGUIForBundleSize(
                        bundleSize,
                        firstVisibleIndex,
                        lastVisibleIndex
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        minusADayBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = viewModel.deliveringDate
            cal.add(Calendar.DAY_OF_YEAR, -1)
            viewModel.deliveringDate = cal.time
            viewModel.loadDelivered()
        }
        plusADayBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = viewModel.deliveringDate
            cal.add(Calendar.DAY_OF_YEAR, 1)
            viewModel.deliveringDate = cal.time
            viewModel.loadDelivered()
        }

        deliveriesSummaryTV.setOnClickListener {
            val fragment: Fragment = DeliveredFragment()

            val transaction: FragmentTransaction =
                requireActivity().getSupportFragmentManager().beginTransaction()

            transaction.replace(R.id.container, fragment).addToBackStack(null).commit()
        }

        currentRB.setOnClickListener {
            ordersRV.visibility = View.VISIBLE
            historyCL.visibility = View.GONE
        }
        historyRB.setOnClickListener {
            ordersRV.visibility = View.GONE
            historyCL.visibility = View.VISIBLE
        }
        currentRB.isChecked = true

        ordersRV.layoutManager = LinearLayoutManager(requireContext())

        deliveredRV.layoutManager = LinearLayoutManager(requireContext())

        bundleSizeFilterSpn.adapter = UglyBundleSizeWithAllOptionAdapter(requireContext())

        bundleSizeFilterSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterHistory()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        dateFilterTV.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance();
            val mYear = c.get(Calendar.YEAR);
            val mMonth = c.get(Calendar.MONTH);
            val mDay = c.get(Calendar.DAY_OF_MONTH);

            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateFilterTV.setText(
                        "Del Date: ${dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year}"
                    )
                    val monthOfYearStr = if (monthOfYear < 9) "0${(monthOfYear + 1)}" else "${(monthOfYear + 1)}"
                    val dayOfMonthStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    dateFilter = "${(year % 100)}$monthOfYearStr$dayOfMonthStr"
                    Log.d("Text", dateFilter)
                    filterHistory()
                }, mYear, mMonth, mDay
            )

            datePickerDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "All"
            ) { dialog, which ->
                dateFilterTV.setText("Del Date: All")
                dateFilter = "Del Date: All"
                filterHistory()
            }

            datePickerDialog.show()
        }

        deleteOrdersBtn.setOnClickListener {

            // TODO: move filteredDeliveredOrders and its filtering into the MainViewModel and have everything observing it to fix DeleteDeliveredOrdersDF not removing deleted orders from view

            if (historyRB.isChecked)
                DeleteDeliveredOrdersDF().show(requireActivity().supportFragmentManager, null)
            else
                DeleteOrdersDF().show(requireActivity().supportFragmentManager, null)
        }

        assignBtn.setOnClickListener {

            val fragment: Fragment = AssignFragment()

            val transaction: FragmentTransaction =
                requireActivity().getSupportFragmentManager().beginTransaction()

            transaction.replace(R.id.container, fragment).addToBackStack(null).commit()
        }

        newOrderBtn.setOnClickListener {
            NewOrderDF().show(requireActivity().supportFragmentManager, null)
        }

    }

    private fun filterHistory() {
        val customer = customerNameSpn.selectedItem as Customer?
        val bundleSizeFiler = bundleSizeFilterSpn.selectedItem as String
        viewModel.filterDeliveredOrders(customer, bundleSizeFiler, dateFilter)
    }

    private fun updateGUIInteractions() {

        val changedDateStr = simpleDateFormat.format(viewModel.deliveringDate)
        val today = Date()
        if (changedDateStr.equals(simpleDateFormat.format(today))) {
            deliveringDateTV.text =
                "Today, ${displayDelDateFormat.format(viewModel.deliveringDate)}"
            deliveringDateTV.setTextColor(requireContext().getColor(R.color.text_color_primary))
            deliveriesSummaryTV.setTextColor(requireContext().getColor(R.color.text_color_primary))
            deliveriesSummaryTV.isEnabled = true
        } else {
            deliveringDateTV.text = displayDelDateFormat.format(viewModel.deliveringDate)
            if (viewModel.deliveringDate.before(today)) {
                deliveringDateTV.setTextColor(requireContext().getColor(R.color.pastDateCol))
                deliveriesSummaryTV.setTextColor(requireContext().getColor(R.color.pastDateCol))
            } else {
                deliveringDateTV.setTextColor(requireContext().getColor(R.color.futureDateCol))
                deliveriesSummaryTV.setTextColor(requireContext().getColor(R.color.futureDateCol))
                deliveriesSummaryTV.isEnabled = false
            }
        }

    }

    private fun updateOrdersForTodayOrFutureDate() {
//        val changedDateStr = simpleDateFormat.format(viewModel.deliveringDate)
//        val today = Date()
//        if (changedDateStr.equals(simpleDateFormat.format(today))) {
//            (ordersRV.adapter as OrdersRVAdapter).changeDeliveringDatetime(today)
//            viewModel.loadCurrentOrders()
//        } else if (viewModel.deliveringDate.after(today)) {
//            viewModel.loadOrdersForSpecificDate(viewModel.deliveringDate)
//        } else {
//            (ordersRV.adapter as OrdersRVAdapter).changeDeliveringDatetime(viewModel.deliveringDate)
//            (ordersRV.adapter as OrdersRVAdapter).notifyDataSetChanged()
//        }
    }

}