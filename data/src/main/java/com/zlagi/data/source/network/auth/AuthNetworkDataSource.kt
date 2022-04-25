package com.zlagi.data.source.network.auth

import android.content.Intent
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.TokensDataModel

interface AuthNetworkDataSource {
    suspend fun signIn(email: String, password: String): DataResult<TokensDataModel>
    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): DataResult<TokensDataModel>

    suspend fun googleIdpAuthentication(data: Intent): DataResult<TokensDataModel>
    suspend fun revokeToken(token: String)
}
