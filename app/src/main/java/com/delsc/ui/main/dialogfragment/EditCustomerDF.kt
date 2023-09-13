package com.delsc.ui.main.dialogfragment

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.delsc.MainActivity
import com.delsc.R
import com.delsc.model.Customer
import com.delsc.model.PaymentType
import com.delsc.model.PriceGroup
import com.delsc.viewmodel.MainViewModel

class EditCustomerDF: DialogFragment() {

    private lateinit var viewModel: MainViewModel
    val customer: Customer by lazy { viewModel.customerToEditPair!!.second }

    val customerNameET by lazy { requireView().findViewById<EditText>(R.id.custNameET) }
    val priceGroupSpn by lazy { requireView().findViewById<Spinner>(R.id.priceGroupSpn) }
    val paymentTypeSpn by lazy { requireView().findViewById<Spinner>(R.id.paymentTypeSpn) }
    val addressET by lazy { requireView().findViewById<EditText>(R.id.addressET) }
    val contactNameET by lazy { requireView().findViewById<EditText>(R.id.contactNameET) }
    val mobileNumET by lazy { requireView().findViewById<EditText>(R.id.mobileNumET) }
    val isToBeNotifiedOfDelSwt by lazy { requireView().findViewById<Switch>(R.id.isToBeNotifiedOfDelSwt) }
    val isArchivedSwt by lazy { requireView().findViewById<Switch>(R.id.isArchivedSwt) }
    val archiveDisableReasonTV by lazy { requireView().findViewById<TextView>(R.id.archiveDisableReasonTV) }
    val saveTV by lazy { requireView().findViewById<TextView>(R.id.saveTV) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_customer_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveTV.setOnClickListener {
            customer.name = customerNameET.text.toString()

            if (priceGroupSpn.selectedItemPosition == 0)
                customer.priceGroup = PriceGroup.Cabramatta
            else customer.priceGroup = PriceGroup.Other

            if (paymentTypeSpn.selectedItemPosition == 0)
                customer.paymentType = PaymentType.COD
            else customer.paymentType = PaymentType.Transfer

            customer.address = addressET.text.toString()
            customer.contactName = contactNameET.text.toString()
            customer.mobileNumber = mobileNumET.text.toString()

            customer.toNotifyOfDelivery = isToBeNotifiedOfDelSwt.isChecked
            customer.isAchived = isArchivedSwt.isChecked

            viewModel.saveCustomer()

            viewModel.loadCustomers()

            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        customerNameET.setText(customer.name)
        customerNameET.isEnabled = customer.name.isNullOrEmpty()

        if (customer.priceGroup == PriceGroup.Cabramatta)
            priceGroupSpn.setSelection(0)
        else priceGroupSpn.setSelection(1)

        if (customer.paymentType == PaymentType.COD)
            paymentTypeSpn.setSelection(0)
        else paymentTypeSpn.setSelection(1)

        addressET.setText(customer.address)
        contactNameET.setText(customer.contactName)
        mobileNumET.setText(customer.mobileNumber)

        isToBeNotifiedOfDelSwt.isChecked = customer.toNotifyOfDelivery

        isArchivedSwt.isChecked = customer.isAchived
        if (viewModel.customerToEditPair == null) // New Customer
            isArchivedSwt.visibility = View.GONE
        else
            isArchivedSwt.visibility = View.VISIBLE

        val currentOrderCount = viewModel.undeliveredOrdersMLD.value?.count {
            it.value.custName == customer.name
        }

        isArchivedSwt.isEnabled = currentOrderCount == 0
        if (isArchivedSwt.isEnabled) {
            archiveDisableReasonTV.visibility = View.INVISIBLE
        } else {
            archiveDisableReasonTV.visibility = View.VISIBLE
            archiveDisableReasonTV.text = "$currentOrderCount current ord"
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setWidthHeightPercent(98, 75)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

    }
}

fun DialogFragment.setWidthHeightPercent(percentWidth: Int, percentHeight: Int) {
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val displayW = rect.width() * percentWidth.toFloat() / 100
    val displayH = rect.height() * percentHeight.toFloat() / 100
    dialog?.window?.setLayout(displayW.toInt(), displayH.toInt())
}