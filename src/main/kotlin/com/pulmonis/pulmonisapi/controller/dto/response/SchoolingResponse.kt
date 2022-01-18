package com.pulmonis.pulmonisapi.controller.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

class SchoolingResponse {
    var id: Long? = null
    var title: String? = null
    var description: String? = null
    var address: String? = null
    var city: String? = null
    var category: String? = null
    var eventDateTime: LocalDateTime? = null
    var free: Boolean? = null
    var price: BigDecimal? = null
    var registrationDeadline: LocalDateTime? = null
    var published: Boolean? = false
}
