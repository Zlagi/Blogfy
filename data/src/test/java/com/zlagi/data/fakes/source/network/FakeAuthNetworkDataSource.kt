package com.zlagi.data.fakes.source.network

import android.content.Intent
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.TokensDataModel
import com.zlagi.data.source.network.auth.AuthNetworkDataSource

class FakeAuthNetworkDataSource(
    private val signInResponse: DataResult<TokensDataModel>,
) : AuthNetworkDataSource {

    override suspend fun signIn(email: String, password: String): DataResult<TokensDataModel> {
        return signInResponse
    }

    override suspend fun signUp(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): DataResult<TokensDataModel> {
        return signInResponse
    }

    override suspend fun googleIdpAuthentication(data: Intent): DataResult<TokensDataModel> {
        return signInResponse
    }

    override suspend fun revokeToken(token: String) {
        //
    }
}
