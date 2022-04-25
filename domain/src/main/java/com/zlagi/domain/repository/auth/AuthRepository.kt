package com.zlagi.domain.repository.auth

import android.content.Intent
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.TokensDomainModel

interface AuthRepository {
    suspend fun signIn(email: String, password: String): DataResult<TokensDomainModel>
    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): DataResult<TokensDomainModel>

    suspend fun googleIdpAuthentication(data: Intent): DataResult<TokensDomainModel>
    suspend fun fetchTokens(): TokensDomainModel
    suspend fun storeTokens(tokens: TokensDomainModel)
    suspend fun deleteTokens()
    suspend fun revokeToken(token: String)
    fun authenticationStatus(): Boolean
}
