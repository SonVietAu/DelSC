package com.delsc.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.viewmodel.alternatingColor1
import com.delsc.model.Driver

class DriversRVAdapter(val context: Context, val drivers: Map<String, Driver>): RecyclerView.Adapter<DriverHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.driver_view_holder, parent, false)
        return DriverHolder(view)
    }

    override fun onBindViewHolder(holder: DriverHolder, position: Int) {

        val key = drivers.keys.elementAt(position)
        val driver = drivers.get(key)!!
        holder.driverNameTV.text = driver.name

        val bgDrawable = context.getResources().getDrawable(R.drawable.background_unassigned_btn,null) as GradientDrawable


        if (position % 2 == 0)
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(Color.CYAN, Color.CYAN)
            }
        else
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(alternatingColor1, alternatingColor1)
            }

        holder.itemView.background = bgDrawable
    }

    override fun getItemCount(): Int = drivers.size

}

class DriverHolder(holderView: View): RecyclerView.ViewHolder(holderView) {
    val driverNameTV = holderView.findViewById<TextView>(R.id.driverNameTV)

}
