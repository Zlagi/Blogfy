package com.zlagi.network.model.request

data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String
)
