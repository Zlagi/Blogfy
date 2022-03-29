package com.zlagi.network.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountNetworkModel(
    @field:Json(name = "id") val id: Int?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "username") val username: String?
)
