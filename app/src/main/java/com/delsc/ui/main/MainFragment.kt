package com.delsc.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.delsc.MainActivity
import com.delsc.viewmodel.MainViewModel
import com.delsc.R
import com.delsc.ui.custom.OrdersSummariesAdapter

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    lateinit var thongTV: TextView
    lateinit var haTV: TextView
    lateinit var sonTV: TextView
    lateinit var alekTV: TextView
    lateinit var pleaseWaitTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thongTV = view.findViewById(R.id.thongTV)
        haTV = view.findViewById(R.id.haTV)
        sonTV = view.findViewById(R.id.sonTV)
        alekTV = view.findViewById(R.id.alekTV)
        pleaseWaitTV = view.findViewById(R.id.pleaseWaitTV)

        thongTV.setOnClickListener {
            openDeliveriesForDriver(it.id)
        }

        haTV.setOnClickListener {
            openDeliveriesForDriver(it.id)
        }

        sonTV.setOnClickListener {
            openDeliveriesForDriver(it.id)
        }

        alekTV.setOnClickListener {
            openDeliveriesForDriver(it.id)
        }

        Log.d("Text", "pleaseWaitTV ${System.identityHashCode(pleaseWaitTV)} created")
    }

    fun openDeliveriesForDriver(viewId: Int) {
        Log.d("Text", "openDeliveriesForDriver")
        viewModel.currentDriverUser = when (viewId) {
            R.id.thongTV -> viewModel.drivers.values.first {
                it.name == "Thong"
            }
            R.id.haTV -> viewModel.drivers.values.first {
                it.name == "Ha"
            }
            R.id.alekTV -> viewModel.drivers.values.first {
                it.name == "Alek"
            }
            else -> viewModel.drivers.values.first {
                it.name == "Son"
            }
        }

        (this.activity as MainActivity).isToShowMenuItems(true, viewModel.currentDriverUser!!.isAdmin)

        val fragment: Fragment = DeliveriesFragment()

        val transaction: FragmentTransaction =
            requireActivity().getSupportFragmentManager().beginTransaction()

        transaction.replace(R.id.container, fragment).addToBackStack(null).commit()
        Log.d("Text", "openDeliveriesForDriver ended")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("Text", "onActCreated")

        viewModel = ViewModelProvider(this.activity as MainActivity).get(MainViewModel::class.java)

        viewModel.undeliveredOrdersMLD.observe(this.viewLifecycleOwner, {
            // Display order summaries
            Log.d("Text", "undeliveredOrdersMLD observed")
            val ordersSummaryLV = view?.findViewById<ListView>(R.id.ordersSummaryLV)
            ordersSummaryLV?.adapter =
                OrdersSummariesAdapter(
                    this.requireContext(),
                    viewModel.ordersSummaries,
                    viewModel.drivers.values.toList()
                )

            thongTV.isEnabled = true
            haTV.isEnabled = true
            sonTV.isEnabled = true
            alekTV.isEnabled = true
            pleaseWaitTV.visibility = View.GONE
        })

    }

}