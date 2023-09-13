package com.delsc.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.model.Customer
import com.delsc.ui.custom.CustomersRVAdapter
import com.delsc.ui.main.dialogfragment.EditCustomerDF

open class CustomersFragment : Fragment(){

    companion object {
        fun newInstance() = CustomersFragment()
    }

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_customers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.customersRV).layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        view.findViewById<View>(R.id.archivedBtn).setOnClickListener {

            val fragment: Fragment = ArchivedCustomersFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(R.id.container, fragment).addToBackStack(null).commit()
        }
        view.findViewById<View>(R.id.backBtn).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        view.findViewById<View>(R.id.newBtn).setOnClickListener {
            viewModel.customerToEditPair = Pair(null, Customer())
            EditCustomerDF().show(requireActivity().supportFragmentManager, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)
        setCustomersToObserve()
    }

    open fun setCustomersToObserve() {
        requireView().findViewById<RecyclerView>(R.id.customersRV).adapter =
            CustomersRVAdapter(requireContext(), viewModel.customers, this::editCustomer)
    }

    fun editCustomer(key: String, customer: Customer) {
        viewModel.customerToEditPair = Pair(key, customer)
        EditCustomerDF().show(requireActivity().supportFragmentManager, null)
    }
}

class ArchivedCustomersFragment: CustomersFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.titleTV).text = "Archived"

        view.findViewById<View>(R.id.archivedBtn).visibility = View.INVISIBLE
        view.findViewById<View>(R.id.newBtn).visibility = View.INVISIBLE

    }

    override fun setCustomersToObserve() {
        requireView().findViewById<RecyclerView>(R.id.customersRV).adapter =
            CustomersRVAdapter(requireContext(), viewModel.archivedCustomers, this::editCustomer)
    }


}
