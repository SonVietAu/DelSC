package com.delsc.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.model.Driver
import com.delsc.ui.custom.AssignRVAdapter
import com.delsc.ui.custom.DriversSpnAdapterWithAnEmpty

class AssignFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var assigningOrdersRV: RecyclerView
    private lateinit var assigneeSpn: Spinner
    private lateinit var selectAllBtn: TextView
    private lateinit var assignBtn: TextView
    private lateinit var doneBtn: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        if (assigningOrdersRV.adapter != null && assigningOrdersRV.adapter is AssignRVAdapter) {
            (assigningOrdersRV.adapter as AssignRVAdapter)
                .changeOrdersList(viewModel.undeliveredOrdersMLD.value!!.toList())
        } else {
            assigningOrdersRV.adapter = AssignRVAdapter(
                viewModel.undeliveredOrdersMLD.value!!.toList(),
                viewModel.drivers.values.toList(),
            )
        }

        assigneeSpn.adapter =
            DriversSpnAdapterWithAnEmpty(
                requireContext(), viewModel.drivers.values.toList()
            )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assigningOrdersRV = requireView().findViewById(R.id.assigningOrdersRV)
        assigneeSpn = requireView().findViewById(R.id.assigneeSpn)
        selectAllBtn = requireView().findViewById(R.id.selectAllBtn)
        assignBtn = requireView().findViewById(R.id.assignBtn)
        doneBtn = requireView().findViewById(R.id.doneBtn)

        assigningOrdersRV.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        selectAllBtn.setOnClickListener {
            val adaptor = (assigningOrdersRV.adapter as AssignRVAdapter)
            adaptor.selectDeselectAll()
            if (adaptor.isAllSelected) {
                selectAllBtn.text = "Deselect All"
            } else {
                selectAllBtn.text = "Select All"
            }
        }

        assignBtn.setOnClickListener { doAssigning() }

        doneBtn.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

    }

    private fun doAssigning() {
        val selectedDriver =
            if (assigneeSpn.selectedItemPosition > 0)
                assigneeSpn.selectedItem as Driver
            else null

        val ordersRVAdapter = (assigningOrdersRV.adapter as AssignRVAdapter)

        val selectedOrders = ordersRVAdapter.getSelectedOrders()

        if (!selectedOrders.isEmpty()) {
            selectedOrders.forEach {
                it.value.driverName = selectedDriver?.name
            }

            viewModel.saveOrders(selectedOrders)
        }

        val firstVisibleIndex =
            (assigningOrdersRV.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastVisibleIndex =
            (assigningOrdersRV.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        ordersRVAdapter.notifyItemRangeChanged(
            firstVisibleIndex,
            lastVisibleIndex - firstVisibleIndex + 1
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_assign, container, false)
    }
}