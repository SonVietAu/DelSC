package com.delsc.ui.main

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.ui.custom.DepositsRVAdapter
import com.delsc.ui.custom.OrdersRVAdapter
import com.delsc.ui.main.dialogfragment.NewDepositDF
import com.delsc.ui.main.dialogfragment.NewOrderDF
import com.delsc.viewmodel.DepositsViewModel
import com.delsc.viewmodel.alternatingColor1

/**
 * TODO: Create 'Banked' objects for the amount of payment made to Ha.
 * The Backed objects to have: Amount, Date, CurrentBalance, NewlyCollectedToDateAmount, BankConfirmationStatus
 * TODO: Banked can be created by the bank(Ha, Rob) or the user. If created by the bank, status is Confirmed. If created by non-bank user, status is Required.
 */

class DepositsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    lateinit var depositsViewModel: DepositsViewModel

    lateinit var depositsRV: RecyclerView
    lateinit var noDepositsMsgTV: TextView

    lateinit var newDepositBtn: TextView
    lateinit var deleteBtn: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        depositsRV = view.findViewById(R.id.depositsRV)
        noDepositsMsgTV = view.findViewById(R.id.noDepositsMsgTV)
        newDepositBtn = view.findViewById(R.id.newDepositBtn)
        deleteBtn = view.findViewById(R.id.deleteBtn)

        depositsRV.layoutManager = LinearLayoutManager(view.context)

        newDepositBtn.setOnClickListener {
            val newDepositDF = NewDepositDF()
            newDepositDF.show(requireActivity().supportFragmentManager, null)
        }

        deleteBtn.setOnClickListener {
            if (deleteBtn.text.toString() == "Delete") {
                deleteBtn.text = "Disable"
                deleteBtn.setTextColor(view.context.getColor(R.color.button_color))
                (depositsRV.adapter as? DepositsRVAdapter)?.enableDeletionMode(true)
            } else {
                deleteBtn.text = "Delete"
                deleteBtn.setTextColor(view.context.getColor(R.color.unpaidCol))
                (depositsRV.adapter as? DepositsRVAdapter)?.enableDeletionMode(false)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)
        depositsViewModel = ViewModelProvider(this.activity as MainActivity).get(DepositsViewModel::class.java)

        depositsViewModel.depositsMLD.observe(viewLifecycleOwner, Observer {
            if (!it.isEmpty()) {
                noDepositsMsgTV.visibility = View.GONE
            } else {
                noDepositsMsgTV.text = "No deposits made"
            }

            if (depositsRV.adapter != null && depositsRV.adapter is DepositsRVAdapter) {
                (depositsRV.adapter as DepositsRVAdapter).changeOrdersList(it)
            } else {
                depositsRV.adapter = DepositsRVAdapter(it, depositsViewModel)
            }
        })
        depositsViewModel.loadDeposites(viewModel.currentDriverUser!!)
        depositsViewModel.loadUndepositedOrders(viewModel.currentDriverUser!!)
    }
}