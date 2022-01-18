package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
class SchoolingRequestBody {
    var title: String? = null
    var description: String? = null
    var address: String? = null
    var city: String? = null
    var category: String? = null
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var eventDateTime: LocalDateTime? = null
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var registrationDeadline: LocalDateTime? = null
    var free: Boolean? = null
    var price: BigDecimal? = null
    var published: Boolean? = false
}
