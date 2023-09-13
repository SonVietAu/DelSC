package com.delsc.ui.main.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.ui.custom.OrdersRVAdapter

class DeleteOrdersDF : DialogFragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var ordersRV: RecyclerView
    lateinit var backBtn: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersRV = view.findViewById(R.id.ordersRV)
        backBtn = view.findViewById(R.id.backBtn)

        ordersRV.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        backBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthHeightPercent(95, 80)
        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        viewModel.undeliveredOrdersMLD.observe(this.viewLifecycleOwner, {
            ordersRV.adapter = OrdersRVAdapter(
                it,
                viewModel.drivers.values.toList(),
                viewModel.customers.values.toList(),
                viewModel.prices.values.toList(),
                viewModel::deleteOrder,
                viewModel::updateOrder,
                viewModel.currentDriverUser!!,
                viewModel.deliveringDate,
                true,
                isShowingBothKgs = true,
            )
        })
    }
}
