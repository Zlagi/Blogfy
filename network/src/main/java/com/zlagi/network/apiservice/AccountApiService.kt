package com.zlagi.network.apiservice

import com.zlagi.network.model.NetworkConstants
import com.zlagi.network.model.request.PasswordRequest
import com.zlagi.network.model.response.AccountNetworkModel
import com.zlagi.network.model.response.GenericResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface AccountApiService  {

    @GET(NetworkConstants.ACCOUNT_ENDPOINT)
    suspend fun getAccount(
    ): AccountNetworkModel

    @PUT(NetworkConstants.PASSWORD_ENDPOINT)
    suspend fun updatePassword(
        @Body passwordRequest: PasswordRequest
    ): GenericResponse
}