package com.delsc.ui.main.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R

class EditDriverDF : DialogFragment() {

    private lateinit var viewModel: MainViewModel

    val driverNameET by lazy { requireView().findViewById<EditText>(R.id.driverNameTV) }
    val mobileNumET by lazy { requireView().findViewById<EditText>(R.id.mobileNumET) }
    val saveTV by lazy { requireView().findViewById<TextView>(R.id.saveTV) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_driver_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveTV.setOnClickListener {

            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setWidthHeightPercent(98, 75)

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

    }
}

