package com.delsc.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.model.Order
import com.delsc.ui.custom.DeliveredRVAdapter
import com.delsc.ui.main.dialogfragment.ReportFragment
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat
import java.util.*

class DeliveredFragment: Fragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var deliveredRV: RecyclerView
    lateinit var titleTV: TextView
    lateinit var deliveriesSummaryTV: TextView
    lateinit var deliveringDateTV: TextView

    lateinit var minusADayBtn: TextView
    lateinit var plusADayBtn: TextView
    lateinit var reportBtn: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        titleTV.text = "${viewModel.currentDriverUser?.name} Delivered Orders"

        deliveriesSummaryTV.text = viewModel.currentDriverDeliveredSummary.toSummaryString()

        viewModel.deliveredOrdersMLD.observe(this.viewLifecycleOwner, {
            deliveriesSummaryTV.text = viewModel.currentDriverDeliveredSummary.toSummaryString()

            val currentDriverDeliveries = it.filter {
                it.second.driverName == viewModel.currentDriverUser!!.name
            }
            if (deliveredRV.adapter is DeliveredRVAdapter) {
                val deliveredRVAdapter = deliveredRV.adapter as DeliveredRVAdapter
                deliveredRVAdapter.changeDeliveredList(currentDriverDeliveries)
            } else {
                deliveredRV.adapter = DeliveredRVAdapter(
                    currentDriverDeliveries,
                    viewModel.drivers.values.toList(),
                    viewModel.customers.values.toList(),
                    this::editDelivered
                )
            }

            updateGUIInteractions()
        })
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_delivered, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deliveredRV = view.findViewById(R.id.deliveredRV)
        titleTV = view.findViewById(R.id.titleTV)
        deliveriesSummaryTV = view.findViewById(R.id.deliveriesSummaryTV)
        deliveringDateTV = view.findViewById(R.id.deliveringDateTV)
        minusADayBtn = view.findViewById(R.id.minusADayBtn)
        plusADayBtn = view.findViewById(R.id.plusADayBtn)
        reportBtn = view.findViewById(R.id.reportBtn)

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

        reportBtn.setOnClickListener {
            ReportFragment().show(requireActivity().supportFragmentManager, null)
        }

        deliveredRV.layoutManager = LinearLayoutManager(requireContext())

    }

}

fun Fragment.editDelivered(orderKey: String, order: Order) {

    // TODO: Be able to edit delivered orders but not reported orders
    if (!order.isReported) {
//            viewModel.setDeliveredOrderToEdit(orderKey, order)
//            DeliveredOrderEditorDF().show(requireActivity().supportFragmentManager, null)
    }

}