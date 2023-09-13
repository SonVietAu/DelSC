package com.delsc.model

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.delsc.R

enum class BundleSize {
    FifteenKg {
        var color = -1
        override fun getColor(context: Context): Int {
            if (color == -1) {
                color = context.getColor(R.color.fifteen_kg_col)
            }
            return color
        }

        override fun getShortName(): String {
            return "15kg"
        }
              },
    TenKg {
        var color = -1
        override fun getColor(context: Context): Int {
            if (color == -1) {
                color = context.getColor(R.color.ten_kg_col)
            }
            return color
        }

        override fun getShortName(): String {
            return "10kg"
        }
    };
    abstract fun getColor(context: Context): Int
    abstract fun getShortName(): String
}

enum class PriceGroup {
    Cabramatta,
    Other
}
class Price(
    var dollarValue: Double = 45.0,
    val bundleSize: BundleSize = BundleSize.FifteenKg,
    val priceGroupApplication: PriceGroup = PriceGroup.Cabramatta) {
}