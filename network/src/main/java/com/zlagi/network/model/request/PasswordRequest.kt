package com.zlagi.network.model.request

data class PasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)