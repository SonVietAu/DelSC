package com.delsc.ui.custom

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.delsc.R
import com.delsc.model.BundleSize

class UglyBundleSizeWithAllOptionAdapter(val context: Context) : BaseAdapter() {

    private val options = MutableList(2, {index -> BundleSize.values().get(index).name })
    private val optionColors = MutableList(2, {index ->  BundleSize.values().get(index).getColor(context)})

    init {
        options.add(0, "All")
        optionColors.add(0, context.getColor(R.color.unassigned))
    }

    override fun getCount() = options.size

    override fun getItem(position: Int) = options.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, viewHolder: View?, parent: ViewGroup?): View {

        if (viewHolder == null) {
            val  textView = TextView(context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            textView.text = options.get(position)
            textView.setBackgroundColor(optionColors.get(position))
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            return textView
        } else {
            return viewHolder
        }
    }

}