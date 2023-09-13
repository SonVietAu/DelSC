package com.delsc.model

import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat
import java.util.*

class OrdersSummary(

    var deliveriesCount: Int = 0,
    var ordered15KgQnty: Int = 0,
    var ordered10KgQnty: Int = 0,

    ) {

    class Key(
        var driverName: String? = null,
        var requestedDeliveryDate: String? = null,
    ) {

        fun toDisplayStr(): String {
            val reqDelDateStr = if (requestedDeliveryDate != null)
                displayDelDateFormat.format(simpleDateFormat.parse(requestedDeliveryDate))
            else displayDelDateFormat.format(Date())
            return "${driverName}, $reqDelDateStr: "
        }
    }

}
