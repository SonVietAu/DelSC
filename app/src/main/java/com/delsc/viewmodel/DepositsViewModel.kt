package com.delsc.viewmodel

import android.telephony.SmsManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.delsc.model.Deposit
import com.delsc.model.DepositStatus
import com.delsc.model.Driver
import com.delsc.model.Order
import com.delsc.model.PaymentType
import java.util.ArrayList

class DepositsViewModel: ViewModel() {

    val depositsMLD = MutableLiveData<List<Pair<String, Deposit>>>()
    val undepositedOrdersMLD = MutableLiveData<List<Pair<String, Order>>>()
    var undepositedTotal = 0

    fun loadDeposites(driver: Driver) {
        myRef.child("deposits")
            .orderByChild("depositedDatetime")
            .limitToLast(100)
            .get().addOnSuccessListener {
                val deposits = it.children.mapNotNull {
                    Pair(it.key!!, it.getValue(Deposit::class.java)!!)
                }.sortedByDescending {
                    it.second.depositedDatetime
                }

                val filteredDeposits =
                    if (driver.name == "Ha")
                        deposits
                else deposits.filter { it.second.driverName == driver.name }

                depositsMLD.value = filteredDeposits
            }
    }

    fun loadUndepositedOrders(driver: Driver) {
        myRef.child("orders")
            .orderByChild("depositKey")
            .equalTo("Undeposited")
            .get().addOnSuccessListener {
                undepositedOrdersMLD.value = it.children.mapNotNull {
                    Pair(it.key!!, it.getValue(Order::class.java)!!)
                }.filter {
                    it.second.driverName == driver.name
                }.sortedByDescending {
                    it.second.deliveredDatetime
                }

                undepositedTotal = 0
                undepositedOrdersMLD.value?.forEach {
                    undepositedTotal += it.second.paidAmt.toInt()
                }

            }.addOnFailureListener {
                it.printStackTrace()
            }

    }

    fun recordDeposit(deposit: Deposit, currentUserDriver: Driver, depositingUserDriver: Driver) {

        val confirmedKey = myRef.child("deposits").push().key!!

        val cashOrderKeys = mutableListOf<String>()
        undepositedOrdersMLD.value?.forEach {
            cashOrderKeys.add(it.first)
            it.second.depositKey = confirmedKey
        }

        deposit.cashOrderKeys = cashOrderKeys

        val mapped = undepositedOrdersMLD.value?.associate {
            it
        }

        if (mapped != null)
            myRef.child("orders").updateChildren(mapped)
        myRef.child("deposits").child(confirmedKey).setValue(deposit)

        sendSMS(deposit, currentUserDriver, depositingUserDriver)

        // add to old deposits list
        val oldDeposits = mutableListOf<Pair<String, Deposit>>()
        if (depositsMLD.value != null)
            oldDeposits.addAll(depositsMLD.value!!)

        oldDeposits.add(0, Pair(confirmedKey, deposit))

        depositsMLD.value = oldDeposits
    }

    fun sendSMS(deposit: Deposit, currentUserDriver: Driver, depositingUserDriver: Driver) {
        try {
            val smsManager = SmsManager.getDefault()

            val messageSB = StringBuffer()
            messageSB.append("Deposit for ${displayDelDateFormat.format(simpleDateFormat.parse(deposit.depositedDatetime))}: \n")
            messageSB.append("Amount: ${deposit.amount}")
            // messageSB.append("Earning Taken: ${deposit.amount}")
            messageSB.append("Petty Cash: ${deposit.pettyCash}")
            messageSB.append("Previous Bal: ${deposit.previousBalance}")
            messageSB.append("Collected: ${deposit.newlyCollectedToDateAmount}")
            messageSB.append("Current Bal: ${deposit.currentBalance}")


            val parts: ArrayList<String> = smsManager.divideMessage(messageSB.toString())
            smsManager.sendMultipartTextMessage(if (currentUserDriver.name == "Ha") depositingUserDriver.number!! else "0411020535", null, parts, null, null)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun update(key: String, deposit: Deposit) {
        myRef.child("deposits").child(key).setValue(deposit)
    }

    // null is all good,
    fun delete(key: String, deposit: Deposit): Pair<Int, String?> {

        // Can not delete confirmed deposit
        return if (deposit.status == DepositStatus.Confirmed)
            Pair(1, "Cannot delete a confirmed deposit")
        else {

            disassociatedOrders(key)
            myRef.child("deposits").child(key).removeValue()
            Pair(0, null)
        }
    }

    private fun disassociatedOrders(depositKey: String) {
        myRef.child("orders")
            .orderByChild("depositKey")
            .equalTo(depositKey)
            .get().addOnSuccessListener {
                val associatedOrders = it.children.associate {
                    val associatedKeyOrderPair = Pair(it.key!!, it.getValue(Order::class.java)!!)
                    associatedKeyOrderPair.second.depositKey = "Undeposited"
                    associatedKeyOrderPair
                }
                myRef.child("orders").updateChildren(associatedOrders)

            }.addOnFailureListener {
                it.printStackTrace()
            }
    }
}