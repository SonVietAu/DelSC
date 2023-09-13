package com.delsc.model

class DeliveriesSummary() {
    var totalDel = 0
    var totalPaidDel = 0
    var total15kg = 0
    var total10kg = 0
    var totalMoneyCollected = 0.0
    var totalMoneyUnpaid = 0.0

    fun toSummaryString(): String {
        val strBfr = StringBuffer()
        if (totalDel == 0) {
            strBfr.append("No deliveries made")
        } else {
            strBfr.append("Deliveries: ${totalDel}")
            if (total15kg != 0)
                strBfr.append(", 15kg: ${total15kg}")
            if (total10kg != 0)
                strBfr.append(", 10kg: ${total10kg}")
            strBfr.append(",\nPaid: $${totalMoneyCollected} ($totalPaidDel customers)")
        }
        return strBfr.toString()
    }
}