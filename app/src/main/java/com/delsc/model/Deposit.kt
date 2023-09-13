package com.delsc.model

import android.content.Context
import com.delsc.R


enum class DepositStatus {
    Draft {
        var color = -1
        override fun getColor(context: Context): Int {
            if (color == -1) {
                color = context.getColor(R.color.unassigned)
            }
            return color
        }

        override fun getShortName(): String {
            return "D"
        }
    },
    Unconfirmed {
        var color = -1
        override fun getColor(context: Context): Int {
            if (color == -1) {
                color = context.getColor(R.color.unpaidCol)
            }
            return color
        }

        override fun getShortName(): String {
            return "U"
        }
    },
    Confirmed {
        var color = -1
        override fun getColor(context: Context): Int {
            if (color == -1) {
                color = context.getColor(R.color.paidCol)
            }
            return color
        }

        override fun getShortName(): String {
            return "C"
        }
    };
    abstract fun getColor(context: Context): Int
    abstract fun getShortName(): String
}

// TODO: Ask Ha if driver is to talk mo from delivery intake as payment?

class Deposit(

    var driverName: String? = null,
    val depositedDatetime: String? = null,
    var amount: Int = 0,
    var earningTaken: Int = 0,
    var pettyCash: Int = 0,
    var status: DepositStatus = DepositStatus.Draft,
    val previousBalance: Int = 0,
    var newlyCollectedToDateAmount: Int = 0,
    var currentBalance: Int = 0,

    var cashOrderKeys: List<String>? = null

    ) {

    override fun toString(): String {
        return "$driverName, $amount, $pettyCash, $earningTaken, $depositedDatetime, $status, $previousBalance, $currentBalance, $newlyCollectedToDateAmount"
    }
}