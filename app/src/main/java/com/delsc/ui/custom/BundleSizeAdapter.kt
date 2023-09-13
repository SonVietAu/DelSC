package com.delsc.ui.custom

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.delsc.model.BundleSize

class BundleSizeAdapter(val context: Context) : BaseAdapter() {

    override fun getCount() = BundleSize.values().size

    override fun getItem(position: Int) = BundleSize.values()[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, viewHolder: View?, parent: ViewGroup?): View {

        if (viewHolder == null) {
            val  textView = TextView(context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val bundleSize = getItem(position)
            textView.text = bundleSize.getShortName()
            textView.setBackgroundColor(bundleSize.getColor(context))
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            return textView
        } else {
            return viewHolder
        }
    }

}