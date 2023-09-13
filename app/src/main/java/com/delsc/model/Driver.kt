package com.delsc.model

import android.graphics.Color

class Driver(
    val name: String? = null,
    val number: String? = null,
    val color: Int? = null,
    /** Not of much used:
     * Was current load in the van
    var fifteenKg: Int = 0,
    var tenKg: Int = 0,
    */
    var isToReceiveNewOrderSms: Boolean = false,
    var isAdmin: Boolean = false,
    var isAccountant: Boolean = false,
) {
    override fun toString(): String {
        return "Driver: {$name, $number, $isToReceiveNewOrderSms, $isAdmin}"
    }
}