package com.delsc.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.delsc.R
import com.delsc.model.Driver

class DriversSpnAdapterWithAnEmpty(val context: Context, val drivers: List<Driver>, val isShortView: Boolean = false): BaseAdapter() {
    override fun getCount(): Int {
        return drivers.count() + 1
    }

    override fun getItem(p0: Int): Driver? {
        return if (p0 == 0) null else drivers.get(p0-1)
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
            newViewHolder.textSize = 26f

            if (p0 == 0) {
                if (isShortView)
                    newViewHolder.setText(" -- ")
                else
                    newViewHolder.setText("  --  ")
                newViewHolder.background = context.getResources().getDrawable(R.drawable.background_unassigned_btn,null)

            } else {
                val driver = drivers.get(p0 - 1)
                if (isShortView)
                    newViewHolder.setText(driver.name!!.substring(0, 1))
                else
                    newViewHolder.setText(driver.name)
                val bgDrawable = context.getResources().getDrawable(R.drawable.background_unassigned_btn,null) as GradientDrawable
                bgDrawable.run {
                    mutate()
                    colors =
                        intArrayOf(driver.color ?: Color.CYAN, driver.color ?: Color.CYAN)
                }
                newViewHolder.background = bgDrawable
            }

            return newViewHolder

        } else {
            return viewHolder
        }
    }

}
