package com.delsc.viewmodel

import android.graphics.Color
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.delsc.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

val alternatingColor1 = Color.rgb(200, 200, 0)
val displayDelDateFormat = SimpleDateFormat("EEE dd MMM")
val simpleDatetimeFormat = SimpleDateFormat("yyMMdd HH:mm")
val simpleDateFormat = SimpleDateFormat("yyMMdd")

class MainViewModel : ViewModel() {

    var currentDriverUser: Driver? = null
        set(value) {
            field = value
            loadDelivered()
            loadLast1000Delivered()
        }

    var deliveringDate = Date()

    val undeliveredOrdersMLD = MutableLiveData<Map<String, Order>>()
    val ordersSummaries = HashMap<OrdersSummary.Key, OrdersSummary>()

    val deliveredOrdersMLD = MutableLiveData<List<Pair<String, Order>>>()
    val last100DeliveredOrdersMLD = MutableLiveData<List<Pair<String, Order>>>()
    val filteredDeliveredOrdersMLD = MutableLiveData<List<Pair<String, Order>>>()


    lateinit var drivers: Map<String, Driver>
    lateinit var customers: Map<String, Customer>
    lateinit var archivedCustomers: Map<String, Customer>
    lateinit var prices: Map<String, Price>

    var customerToEditPair: Pair<String?, Customer>? = null

    lateinit var currentDriverDeliveredSummary: DeliveriesSummary

    init {
        loadDrivers({
            loadCustomers({
                loadPrices({
                    loadOrders()
                    listenForChanges()
                })
            })})

    }


    fun loadOrders() {
        myRef.child("orders")
            .orderByChild("deliveredDatetime")
            .endAt("1")
            .get().addOnSuccessListener {
                val orders = it.children.associate {
                    Pair(it.key!!, it.getValue(Order::class.java)!!)
                }

                calculateUndeliveredSummaries(orders.values.toList())
                undeliveredOrdersMLD.value = orders
            }

    }

    private fun listenForChanges() {
        myRef.child("orders")
            .orderByChild("deliveredDatetime")
            .endAt("1")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    undeliveredOrdersMLD.value = snapshot.children.associate { doc ->
                        val driver = doc.getValue(Order::class.java)!!
                        Pair(doc.key!!, driver)
                    }
                    // TODO: Needed? this undelivered orders for specific date?
//                    undeliveredOrdersMLD.value = associates.filter {
//                        (it.value.requestedDeliveryDate ?: "") == futureDateString
//                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("drivers value event error", error.message)
                }

            })
    }

    private fun calculateDeliveredSummaries(deliveredOrders: List<Order>) {

        currentDriverDeliveredSummary = DeliveriesSummary()
        deliveredOrders.forEach { oneDeliveredOrder ->
            if (currentDriverUser!!.isAdmin ||
                oneDeliveredOrder.driverName == currentDriverUser!!.name
            ) {
                currentDriverDeliveredSummary.apply {

                    totalDel += 1
                    total10kg += oneDeliveredOrder.delivered10kgQnty
                    total15kg += oneDeliveredOrder.delivered15kgQnty
                    if (oneDeliveredOrder.paidAmt == 0.0) {
                        val customer =
                            customers.values.first { it.name == oneDeliveredOrder.custName }
                        val fifteenKgPrice = prices.values.first {
                            it.priceGroupApplication == customer.priceGroup &&
                                    it.bundleSize == BundleSize.FifteenKg
                        }
                        val tenKgPrice = prices.values.first {
                            it.priceGroupApplication == customer.priceGroup &&
                                    it.bundleSize == BundleSize.TenKg
                        }
                        totalMoneyUnpaid +=
                            (oneDeliveredOrder.delivered15kgQnty * fifteenKgPrice.dollarValue +
                                    oneDeliveredOrder.delivered10kgQnty * tenKgPrice.dollarValue)
                    } else {
                        totalPaidDel += 1
                        totalMoneyCollected += (oneDeliveredOrder.paidAmt)
                    }
                }
            }
        }
    }

    private fun calculateUndeliveredSummaries(undeliveredOrders: List<Order>) {

        ordersSummaries.clear()
        val ordersSumKeys = ordersSummaries.keys

        undeliveredOrders.forEach {

            val order = it

            val driverName =
                if (order.driverName.isNullOrEmpty()) "Unassigned" else order.driverName

            val summaryKey: OrdersSummary.Key = ordersSumKeys.firstOrNull { oldKey ->
                oldKey.driverName == driverName &&
                        oldKey.requestedDeliveryDate == order.requestedDeliveryDate
            } ?: OrdersSummary.Key(driverName, order.requestedDeliveryDate)

            val summary = ordersSummaries.get(summaryKey) ?: OrdersSummary()

            summary.deliveriesCount += 1
            summary.ordered10KgQnty += order.ordered10KgQnty
            summary.ordered15KgQnty += order.ordered15KgQnty

            ordersSummaries.put(summaryKey, summary)
        }
    }

    fun loadDelivered() {
        val deliveryDateString = simpleDateFormat.format(deliveringDate)

        myRef.child("orders")
            .orderByChild("deliveredDatetime")
            .startAt(deliveryDateString)
            .endAt("${deliveryDateString}Z")
            .get().addOnSuccessListener {
                Log.d("Text", it.value.toString())
                Log.d("Text", currentDriverUser.toString())
                val deliveredOrders = it.children.mapNotNull {

                    val oneDeliveredOrder = it.getValue(Order::class.java)!!
                    Pair(it.key!!, oneDeliveredOrder)
                }
/*
                    .filter {
                    currentDriverUser!!.isAdmin ||
                    it.second.driverName == currentDriverUser!!.name
                }
*/
                calculateDeliveredSummaries(deliveredOrders.map {
                    it.second
                })

                deliveredOrdersMLD.value = deliveredOrders
            }
    }

    fun loadLast1000Delivered(custName: String? = null) {

        currentDriverDeliveredSummary = DeliveriesSummary()

        Log.d("Text", "Loading last 100 deliveries")

        myRef.child("orders")
            .orderByChild("deliveredDatetime")
            .limitToLast(1000)
            .get().addOnSuccessListener {
                val last1000 = it.children.mapNotNull {
                    Pair(it.key!!, it.getValue(Order::class.java)!!)
                }.filter {
                    currentDriverUser!!.isAdmin ||
                    it.second.driverName == currentDriverUser!!.name
                }.sortedByDescending {
                    it.second.deliveredDatetime
                }
                last100DeliveredOrdersMLD.value = if (custName != null) {
                    last1000.filter {
                        it.second.custName == custName
                    }
                } else {
                    last1000
                }
                filteredDeliveredOrdersMLD.value = last100DeliveredOrdersMLD.value
            }
    }

    fun loadDrivers(nextTask: (() -> Unit)? = null) {
        myRef.child("drivers").get().addOnSuccessListener {
            drivers = it.children.associate { doc ->
                Pair(doc.key!!, doc.getValue(Driver::class.java)!!)
            }
            nextTask?.invoke()
        }
    }

    fun loadCustomers(nextTask: (() -> Unit)? = null) {
        myRef.child("customers")
            .orderByChild("name")
            .get().addOnSuccessListener {
            val associated = it.children.associate { doc ->
                Pair(doc.key!!, doc.getValue(Customer::class.java)!!)
            }
            customers = associated.filter {
                !it.value.isAchived
            }
            archivedCustomers = associated.filter {
                it.value.isAchived
            }
            nextTask?.invoke()

        }
    }

    fun loadPrices(nextTask: (() -> Unit)? = null) {
        myRef.child("prices").get().addOnSuccessListener {
            prices = it.children.associate { doc ->
                Pair(doc.key!!, doc.getValue(Price::class.java)!!)
            }
            nextTask?.invoke()
        }
    }

    fun saveCustomer() {
        val key = customerToEditPair!!.first ?: myRef.child("customers").push().key!!
        myRef.child("customers").child(key).setValue(customerToEditPair!!.second)
    }

    fun saveDriver(driver: Driver) {
        val key = myRef.child("drivers").push().key!!
        myRef.child("drivers").child(key).setValue(driver)
    }

    fun savePrices() {
        myRef.child("prices").updateChildren(prices)
    }

    fun insertNewOrder(order: Order) {
        val confirmedKey = myRef.child("orders").push().key!!
        myRef.child("orders").child(confirmedKey).setValue(order)
        val customer = customers.values.first {
            it.name == order.custName
        }
        notifyCustomerOfDeliveredOrder(customer, order)
        updateDeliveredOrders(confirmedKey, order)
    }

    private fun notifyCustomerOfDeliveredOrder(customer: Customer, order: Order) {
        if (order.delivered15kgQnty + order.delivered10kgQnty > 0
            && !customer.mobileNumber.isNullOrBlank() && customer.toNotifyOfDelivery
        ) {
            try {
                val smsManager = SmsManager.getDefault()

                val messageSB = StringBuffer()
                if (order.delivered15kgQnty > 0 && order.delivered10kgQnty > 0)
                    messageSB.append("${order.delivered15kgQnty} x 15kg and\n${order.delivered10kgQnty} x 10kg\nwere delivered,\n")
                else if (order.delivered15kgQnty > 0)
                    messageSB.append("${order.delivered15kgQnty} x 15kg were delivered,\n")
                else if (order.delivered10kgQnty > 0)
                    messageSB.append("${order.delivered10kgQnty} x 10kg were delivered,\n")
                messageSB.append("Please reply with 'Confirmed' or 'Disagree'")

                if (customer.paymentType == PaymentType.Transfer) {
                    messageSB.append("\n\nPlease notify myself (${currentDriverUser?.name}) or Ha once payment has been transferred. \nCheers,\n${currentDriverUser?.name}.")
                }

                val parts: ArrayList<String> = smsManager.divideMessage(messageSB.toString())
                smsManager.sendMultipartTextMessage(customer.mobileNumber, null, parts, null, null)

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun deleteOrder(key: String, order: Order) {

        if (order.depositKey != "Undeposited" && order.depositKey != "N/A")
            return

        myRef.child("orders").child(key).removeValue()

        val deliveredOrdersMap: List<Pair<String, Order>> =
            if (deliveredOrdersMLD.value?.isNotEmpty() ?: false) {
                deliveredOrdersMLD.value!!.filter {
                    it.first != key
                }
            } else {
                emptyList()
            }

        calculateDeliveredSummaries(deliveredOrdersMap.map {
            it.second
        })
        deliveredOrdersMLD.value = deliveredOrdersMap

        val last100Del = last100DeliveredOrdersMLD.value!!.filter {
            it.first != key
        }
        last100DeliveredOrdersMLD.value = last100Del
        val filteredLast100Del = filteredDeliveredOrdersMLD.value!!.filter {
            it.first != key
        }
        filteredDeliveredOrdersMLD.value = filteredLast100Del
    }

    fun updateOrder(
        key: String,
        order: Order) {
        myRef.child("orders").child(key).setValue(order)
        val customer = customers.values.first {
            it.name == order.custName
        }
        notifyCustomerOfDeliveredOrder(customer, order)
        if (order.deliveredDatetime != null)
            updateDeliveredOrders(key, order)
    }
    private fun updateDeliveredOrders(
        key: String,
        order: Order,
        ) {

        val deliveredOrdersMap: MutableList<Pair<String, Order>> =
            if (deliveredOrdersMLD.value?.isEmpty() ?: true) {
                MutableList(1, {
                    Pair(key, order)
                })
            } else {
                val mutableList = MutableList(deliveredOrdersMLD.value!!.size, {
                    deliveredOrdersMLD.value!!.get(it)
                })
                mutableList.add(Pair(key, order))
                mutableList
            }
        val ordersMap = undeliveredOrdersMLD.value?.filter {
            it.key != key
        }

        calculateDeliveredSummaries(deliveredOrdersMap.map {
            it.second
        })
        calculateUndeliveredSummaries(ordersMap!!.values.toList())
        deliveredOrdersMLD.value = deliveredOrdersMap
        undeliveredOrdersMLD.value = ordersMap!!

        val last100Del: List<Pair<String, Order>> =
            if (last100DeliveredOrdersMLD.value?.isEmpty() ?: true) {
                List(1, {
                    Pair(key, order)
                })
            } else {
                val mutableList = MutableList(last100DeliveredOrdersMLD.value!!.size, {
                    last100DeliveredOrdersMLD.value!!.get(it)
                })
                mutableList.add(0, Pair(key, order))
                mutableList
            }
        last100DeliveredOrdersMLD.value = last100Del
        filterDeliveredOrders()

    }

    fun saveOrders(orders: Map<String, Order>) {
        myRef.child("orders").updateChildren(orders)
    }

    fun saveDeliveredOrdersToFBBD() {
        myRef.child("orders").updateChildren(deliveredOrdersMLD.value!!.toMap())

    }

    var customer: Customer? = null
    var bundleSizeFiler:String? = null
    var dateFilter: String? = null
    fun filterDeliveredOrders(customer:Customer?, bundleSizeFiler:String?, dateFilter: String?) {
        this.customer = customer
        this.bundleSizeFiler = bundleSizeFiler
        this.dateFilter = dateFilter
        filterDeliveredOrders()
    }
    private fun filterDeliveredOrders() {

        val filteredDeliveredOrders =
            last100DeliveredOrdersMLD.value!!.filter {
                (customer == null || it.second.custName == customer?.name)
                        &&
                        (bundleSizeFiler == "All" ||
                                (bundleSizeFiler.equals(BundleSize.FifteenKg.name) && it.second.delivered15kgQnty != 0) ||
                                (bundleSizeFiler.equals(BundleSize.TenKg.name) && it.second.delivered10kgQnty != 0))
                        &&
                        (dateFilter == "Del Date: All" ||
                                it.second.deliveredDatetime!!.startsWith(dateFilter ?: "XXXShouldNotMatch"))
            }

        filteredDeliveredOrdersMLD.value = filteredDeliveredOrders
    }

}