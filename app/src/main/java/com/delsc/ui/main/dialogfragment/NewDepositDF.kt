package com.delsc.ui.main.dialogfragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.R
import com.delsc.model.Deposit
import com.delsc.model.DepositStatus
import com.delsc.model.Driver
import com.delsc.ui.custom.DeliveredRVAdapter
import com.delsc.ui.custom.DriversSpnAdapterWithAnEmpty
import com.delsc.ui.custom.DriversSpnAdapterWithoutAnEmpty
import com.delsc.ui.main.editDelivered
import com.delsc.viewmodel.DepositsViewModel
import com.delsc.viewmodel.MainViewModel
import com.delsc.viewmodel.alternatingColor1
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat
import java.util.Calendar
import java.util.Date

class NewDepositDF: DialogFragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var depositsViewModel: DepositsViewModel

    lateinit var driversSpn: Spinner
    lateinit var depositedDatetimeSpn: Spinner
    lateinit var undepositedOrdersRV: RecyclerView

    lateinit var depositBtn: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthHeightPercent(100, 96)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)
        depositsViewModel = ViewModelProvider(this.activity as MainActivity).get(DepositsViewModel::class.java)

        if (viewModel.currentDriverUser!!.name == "Ha") {
            driversSpn.adapter =
                DriversSpnAdapterWithAnEmpty(requireContext(), viewModel.drivers.values.toList())
        } else {
            driversSpn.adapter =
                DriversSpnAdapterWithoutAnEmpty(requireContext(), listOf(viewModel.currentDriverUser!!))

        }

        depositsViewModel.undepositedOrdersMLD.observe(this.viewLifecycleOwner, Observer {

            if (undepositedOrdersRV.adapter is DeliveredRVAdapter) {
                val deliveredRVAdapter = undepositedOrdersRV.adapter as DeliveredRVAdapter
                deliveredRVAdapter.changeDeliveredList(it)
            } else {
                undepositedOrdersRV.adapter = DeliveredRVAdapter(
                    it,
                    viewModel.drivers.values.toList(),
                    viewModel.customers.values.toList(),
                    this::editDelivered,
                )
            }

            calculateAndShowCurrentBalances()

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_deposit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driversSpn = view.findViewById(R.id.driversSpn)
        depositedDatetimeSpn = view.findViewById(R.id.depositedDatetimeSpn)
        undepositedOrdersRV = view.findViewById(R.id.undepositedOrdersRV)

        depositBtn = view.findViewById(R.id.saveBtn)

        depositedDatetimeSpn.adapter = DepositeDatetimeAdt(view.context)
        depositedDatetimeSpn.setSelection(DepositeDatetimeAdt.todayIndex)

        undepositedOrdersRV.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<EditText>(R.id.amountET).doAfterTextChanged {
            calculateAndShowCurrentBalances()
        }
        view.findViewById<EditText>(R.id.earningTakenET).doAfterTextChanged {
            calculateAndShowCurrentBalances()
        }
        view.findViewById<EditText>(R.id.pettyCashET).doAfterTextChanged {
            calculateAndShowCurrentBalances()
        }

        view.findViewById<View>(R.id.cancelTV).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.saveBtn).setOnClickListener {
            completeDeposit()
            dismiss()
        }

    }

    private fun completeDeposit() {

        val driverName = (driversSpn.selectedItem as Driver).name

        val lastDeposit = depositsViewModel.depositsMLD.value?.firstOrNull()
        val previousBal = if (lastDeposit!=null) {
            lastDeposit.second.currentBalance
        } else 0

        val depositAmount = requireView().findViewById<EditText>(R.id.amountET).text.toString().toInt()
        val earningTaken = requireView().findViewById<EditText>(R.id.earningTakenET).text.toString().toInt()
        val pettyCash = requireView().findViewById<EditText>(R.id.pettyCashET).text.toString().toInt()

        val depositedDatetime = depositedDatetimeSpn.selectedItem as String
        Log.d("Text", "newlyCollectedToDateAmount: $depositedDatetime")

        val currBal = depositsViewModel.undepositedTotal + previousBal -
                (depositAmount + earningTaken + pettyCash)

        val deposit = Deposit(
            driverName,
            depositedDatetime,
            depositAmount,
            pettyCash,
            earningTaken,
            DepositStatus.Unconfirmed,
            previousBal,
            depositsViewModel.undepositedTotal,
            currBal
            )

        depositsViewModel.recordDeposit(deposit, viewModel.currentDriverUser!!, (driversSpn.selectedItem as Driver))

    }

    private fun calculateAndShowCurrentBalances() {

        val lastDeposit = depositsViewModel.depositsMLD.value?.firstOrNull()
        val previousBal = if (lastDeposit!=null) {
            lastDeposit.second.currentBalance
        } else 0

        requireView().findViewById<TextView>(R.id.previousBalTV).text = "$previousBal"
        requireView().findViewById<TextView>(R.id.currentCollectionTV).text = "${depositsViewModel.undepositedTotal}"

        val depositAmount = requireView().findViewById<EditText>(R.id.amountET).text.toString().toInt()
        val earningTaken = requireView().findViewById<EditText>(R.id.earningTakenET).text.toString().toInt()
        val pettyCash = requireView().findViewById<EditText>(R.id.pettyCashET).text.toString().toInt()

        val currBal = depositsViewModel.undepositedTotal + previousBal -
                (depositAmount + earningTaken + pettyCash)

        requireView().findViewById<TextView>(R.id.currentBalTV).text = "$currBal"

    }

    private class DepositeDatetimeAdt(val context: Context) : BaseAdapter() {

        companion object {
            val todayIndex =20
        }

        val dates = buildList<Date> {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -21)
            for (i in 1..21) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                this.add(cal.time)
            }

        }

        override fun getCount(): Int {
            return dates.size
        }

        override fun getItem(p0: Int): String {
            return simpleDateFormat.format(dates[p0])
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
                if (p0 == todayIndex)
                    newViewHolder.setText("Today, ${displayDelDateFormat.format(dates[p0])}")
                else
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0]))
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
                if (p0 == todayIndex)
                    newViewHolder.setText("Today, ${displayDelDateFormat.format(dates[p0])}")
                else
                    newViewHolder.setText(displayDelDateFormat.format(dates[p0]))
                newViewHolder.textSize = 28f
                return newViewHolder
            }

        }

    }

}