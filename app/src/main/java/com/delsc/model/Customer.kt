package com.delsc.model

// TODO: Save a working version onto Github before implementing 'Invoice' payment type
enum class PaymentType {
    COD,
    Transfer
}
class Customer(
    var name: String? = null,
    var address: String? = null,
    var priceGroup: PriceGroup = PriceGroup.Cabramatta,
    var paymentType: PaymentType = PaymentType.COD,
    var contactName: String? = null,
    var mobileNumber: String? = null,
    var toNotifyOfDelivery: Boolean = false,

    var isAchived: Boolean = false,

    ) {

    override fun toString(): String {
        return "$name, $address, $priceGroup, $paymentType, $contactName, $mobileNumber, $toNotifyOfDelivery, $isAchived"
    }

}