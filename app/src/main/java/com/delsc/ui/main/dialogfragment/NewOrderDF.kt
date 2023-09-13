package com.delsc.ui.main.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.ui.custom.NewOrderViewHolder

class NewOrderDF: DialogFragment() {

    lateinit var viewModel: MainViewModel

    lateinit var newOrderViewHolder: NewOrderViewHolder

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthHeightPercent(100, 96)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        newOrderViewHolder.setCurrentDriver(viewModel.currentDriverUser)
        newOrderViewHolder.populatePrices(viewModel.prices.values.toList())
        newOrderViewHolder.populateCustomers(viewModel.customers)
        newOrderViewHolder.populateDrivers(viewModel.drivers.values.toList())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newOrderViewHolder = requireView().findViewById(R.id.newOrderViewHolder)

        newOrderViewHolder.setOnCancelClickListener{dismiss()}
        newOrderViewHolder.setOnSaveClickedListener(this::insertNewOrder)
    }

    private fun insertNewOrder() {

        // TODO: Notify other drivers of delivery by SMS

        val newOrder = newOrderViewHolder.order
        viewModel.insertNewOrder(newOrder)

        // TODO: Consider 'SMS notification to other drivers'
//        if (newOrder.deliveredDatetime == null)
//            notifyDriversOfOrder(newOrder)

        // Should only close once successfully stored on to Firebase
        if (!newOrderViewHolder.isNewOrderCLPinned) {
            dismiss()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_order, container, false)

    }

}