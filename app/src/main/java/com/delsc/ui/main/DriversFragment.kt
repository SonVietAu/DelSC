package com.delsc.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.ui.custom.DriversRVAdapter
import com.delsc.ui.main.dialogfragment.EditDriverDF

open class DriversFragment : Fragment() {

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_drivers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.driversRV).layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        view.findViewById<View>(R.id.backBtn).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        view.findViewById<View>(R.id.newBtn).setOnClickListener {
            EditDriverDF().show(requireActivity().supportFragmentManager, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)
        setDriversToObserve()
    }

    open fun setDriversToObserve() {
        requireView().findViewById<RecyclerView>(R.id.driversRV).adapter =
            DriversRVAdapter(requireContext(), viewModel.drivers)
    }
}

