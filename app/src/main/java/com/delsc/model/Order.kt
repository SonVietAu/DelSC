package com.delsc.model

//TODO: Have a way to update payment status as defined in the Flutter project (OrderMade to PaymentConfirmed)

class Order(
    var custName: String? = null,

    // TODO: var orderStatus: String? = null,

    var ordered15KgQnty: Int = 0,
    var ordered10KgQnty: Int = 0,
    var requestedDeliveryDate: String? = null,

    var delivered15kgQnty: Int = 0,
    var delivered10kgQnty: Int = 0,

    var fifteenKgDollarValue: Double = 0.0,
    var tenKgDollarValue: Double = 0.0,
    val orderedDatetime: String? = null,
    var requiredByDatetime: String? = null,
    var driverName: String? = null,
    var deliveredDatetime: String? = null,
    var paidAmt: Double = 0.0,

    var isReported: Boolean = false,
    var depositKey: String = "N/A", //"Undeposited" is another option, otherwise it should be the key itself

) {
    override fun toString(): String {
        return "$custName, $driverName, $ordered15KgQnty, $ordered10KgQnty, del: $delivered15kgQnty, $delivered10kgQnty, delDate: $deliveredDatetime"
    }
}
