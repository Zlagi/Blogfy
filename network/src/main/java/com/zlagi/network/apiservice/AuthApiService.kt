package com.zlagi.network.apiservice

import com.zlagi.network.model.NetworkConstants
import com.zlagi.network.model.NetworkParameters
import com.zlagi.network.model.request.SignInRequest
import com.zlagi.network.model.request.GoogleSignInRequest
import com.zlagi.network.model.request.SignUpRequest
import com.zlagi.network.model.request.UpdateTokenRequest
import com.zlagi.network.model.response.TokensNetworkModel
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {

    @Headers("${NetworkParameters.CUSTOM_HEADER}: ${NetworkParameters.NO_AUTH}")
    @POST(NetworkConstants.SIGNIN_ENDPOINT)
    suspend fun signIn(
        @Body signInRequest: SignInRequest
    ): TokensNetworkModel

    @Headers("${NetworkParameters.CUSTOM_HEADER}: ${NetworkParameters.NO_AUTH}")
    @POST(NetworkConstants.SIGNUP_ENDPOINT)
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): TokensNetworkModel

    @Headers("${NetworkParameters.CUSTOM_HEADER}: ${NetworkParameters.NO_AUTH}")
    @POST(NetworkConstants.GOOGLE_AUTHENTICATION_ENDPOINT)
    suspend fun googleSignIn(
        @Header(NetworkParameters.AUTH_HEADER) header: String,
        @Body googleSignInRequest: GoogleSignInRequest
    ): TokensNetworkModel

    @Headers("${NetworkParameters.CUSTOM_HEADER}: ${NetworkParameters.NO_AUTH}")
    @POST(NetworkConstants.REFRESH_TOKEN_ENDPOINT)
    suspend fun updateToken(
        @Body updateTokenRequest: UpdateTokenRequest
    ): TokensNetworkModel

    @Headers("${NetworkParameters.CUSTOM_HEADER}: ${NetworkParameters.NO_AUTH}")
    @POST(NetworkConstants.REVOKE_TOKEN_ENDPOINT)
    suspend fun revokeToken(
        @Body updateTokenRequest: UpdateTokenRequest
    ): TokensNetworkModel
}