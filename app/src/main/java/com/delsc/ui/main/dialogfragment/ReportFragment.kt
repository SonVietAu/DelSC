package com.delsc.ui.main.dialogfragment

import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.model.Order

class ReportFragment : DialogFragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var includeDriverNameSwt: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        includeDriverNameSwt = view.findViewById(R.id.includeDriverNameSwt)

        includeDriverNameSwt.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.deliveredOrdersMLD.observe(this.viewLifecycleOwner, Observer {
                createMessage(it, isChecked)
            })
        }

        view.findViewById<TextView>(R.id.sendTV).setOnClickListener {

            try {
                val smsManager = SmsManager.getDefault()

                val message = view.findViewById<EditText>(R.id.reportEt).text.toString()
                val receiverNumber =
                    view.findViewById<EditText>(R.id.receiverNumberEt).text.toString()

                val parts: ArrayList<String> = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(receiverNumber, null, parts, null, null)

                for (member in viewModel.deliveredOrdersMLD.value!!) {
                    member.second.isReported = true
                }
                viewModel.saveDeliveredOrdersToFBBD()
                dismiss()
            } catch (ex: Exception) {
                Toast.makeText(
                    requireContext(), ex.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
                ex.printStackTrace()
            }

        }

        view.findViewById<TextView>(R.id.cancelTV).setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setWidthHeightPercent(95, 65)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        if (!viewModel.currentDriverUser!!.isAdmin) {
            requireView().findViewById<View>(R.id.includeDriverNameSwt).visibility = View.GONE
            requireView().findViewById<View>(R.id.spacer).visibility = View.GONE
        }

        viewModel.deliveredOrdersMLD.observe(this.viewLifecycleOwner, Observer {
            createMessage(it)
        })

    }

    private fun createMessage(
        deliveredOrdersList: List<Pair<String, Order>>,
        isShowingDriverName: Boolean = false
    ) {
        val fifteenKgDelStrBuf = StringBuffer()
        val tenKgDelStrBuf = StringBuffer()
        for (member in deliveredOrdersList) {

            val delivered = member.second
            if (
                viewModel.currentDriverUser!!.isAdmin ||
                delivered.driverName == viewModel.currentDriverUser!!.name
            ) {
                if (delivered.delivered15kgQnty > 0) {
                    fifteenKgDelStrBuf.append("${delivered.custName}, ${delivered.delivered15kgQnty}, ")
                    if (delivered.paidAmt > 0) {
                        fifteenKgDelStrBuf.append("Paid")
                    } else {
                        fifteenKgDelStrBuf.append("Unpaid")
                    }

                    if (isShowingDriverName && viewModel.currentDriverUser!!.isAdmin) {
                        fifteenKgDelStrBuf.append(", driver: ${delivered.driverName}")
                    }

                    fifteenKgDelStrBuf.append("\n")
                }

                if (delivered.delivered10kgQnty > 0) {
                    tenKgDelStrBuf.append("${delivered.custName}, ${delivered.delivered10kgQnty}, ")
                    if (delivered.paidAmt > 0) {
                        tenKgDelStrBuf.append("Paid")
                    } else {
                        tenKgDelStrBuf.append("Unpaid")
                    }

                    if (isShowingDriverName && viewModel.currentDriverUser!!.isAdmin) {
                        tenKgDelStrBuf.append(", driver: ${delivered.driverName}")
                    }

                    tenKgDelStrBuf.append("\n")
                }
            }
        }

        val displayDateStr = displayDelDateFormat.format(viewModel.deliveringDate)

        val resultStrBuf = StringBuffer("$displayDateStr deliveries:\n")
        if (fifteenKgDelStrBuf.length > 0) {
            resultStrBuf.append("\n15kg:\n")
            resultStrBuf.append(fifteenKgDelStrBuf)
        }
        if (tenKgDelStrBuf.length > 0) {
            resultStrBuf.append("\n10kg:\n")
            resultStrBuf.append(tenKgDelStrBuf)
        }

        resultStrBuf.append("\n\nTotal:\n")
        resultStrBuf.append(viewModel.currentDriverDeliveredSummary.toSummaryString())

        requireView().findViewById<EditText>(R.id.reportEt).setText(resultStrBuf.toString())

    }
}
