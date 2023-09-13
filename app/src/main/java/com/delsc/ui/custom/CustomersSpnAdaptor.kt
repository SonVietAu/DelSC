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
import com.delsc.model.Customer
import com.delsc.viewmodel.alternatingColor1

class CustomersSpnAdaptor(val context: Context, val customers: Map<String, Customer>, val isShowingAnEmpty: Boolean = false) : BaseAdapter() {
    override fun getCount(): Int {
        return if (isShowingAnEmpty)
            customers.count() + 1
        else
            customers.count()
    }

    override fun getItem(p0: Int): Customer? {
        return when {
            isShowingAnEmpty && p0 == 0 -> null
            isShowingAnEmpty -> {
                val key = customers.keys.elementAt(p0 - 1)
                customers.get(key)!!
            }
            else -> {
                val key = customers.keys.elementAt(p0)
                customers.get(key)!!
            }
        }
//        if (isShowingAnEmpty) {
//            if(p0 == 0)
//                return null
//            else {
//                val key = customers.keys.elementAt(p0 - 1)
//                return customers.get(key)!!
//            }
//        } else {
//            val key = customers.keys.elementAt(p0)
//            return customers.get(key)!!
//        }
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, viewHolder: View?, parentView: ViewGroup?): View {
        val editingVH: TextView =
            if (viewHolder == null) {
                val newViewHolder = TextView(context)
                newViewHolder.setLayoutParams(
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
                newViewHolder.textAlignment = View.TEXT_ALIGNMENT_CENTER
                newViewHolder.textSize = 28f
                newViewHolder
            } else
                viewHolder as TextView

        editingVH.setText(getItem(p0)?.name ?: "All Customers")
        val bgDrawable = context.getResources().getDrawable(R.drawable.background_unassigned_btn,null) as GradientDrawable


        if (isShowingAnEmpty && p0 == 0)
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(Color.LTGRAY, Color.LTGRAY)
            }
        else if (p0 % 2 == 0)
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

        editingVH.background = bgDrawable


        return editingVH

    }

}