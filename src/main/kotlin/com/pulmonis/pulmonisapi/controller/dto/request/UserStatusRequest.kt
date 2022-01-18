package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.pulmonis.pulmonisapi.enums.UserStatus

@JsonIgnoreProperties(ignoreUnknown = true)
class UserStatusRequest {
    var status: UserStatus? = null;
}
