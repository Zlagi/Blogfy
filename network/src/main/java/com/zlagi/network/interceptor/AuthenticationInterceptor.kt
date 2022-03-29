package com.zlagi.network.interceptor

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.data.source.preferences.PreferencesDataSource
import com.zlagi.network.apiservice.AuthApiService
import com.zlagi.network.mapper.TokensNetworkDataMapper
import com.zlagi.network.model.NetworkParameters.AUTH_HEADER
import com.zlagi.network.model.NetworkParameters.CUSTOM_HEADER
import com.zlagi.network.model.NetworkParameters.NO_AUTH
import com.zlagi.network.model.NetworkParameters.TOKEN_TYPE
import com.zlagi.network.model.request.UpdateTokenRequest
import com.zlagi.network.model.response.TokensNetworkModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthenticationInterceptor @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val tokensMapper: TokensNetworkDataMapper,
    private val authApi: AuthApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (NO_AUTH in request.headers.values(CUSTOM_HEADER)) {
            return chain.proceedWithToken(request, null)
        }

        val accessToken = preferencesDataSource.getAccessToken()

        val response = chain.proceedWithToken(request, accessToken)

        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response
        }

        val newToken: String? = runBlocking {
            mutex.withLock {
                val tokenResponse = getUpdatedToken()
                tokenResponse.access_token.also {
                    with(preferencesDataSource) {
                        storeTokens(tokensMapper.from(tokenResponse))
                    }
                }
            }
        }
        return if (newToken !== null) {
            response.close()
            chain.proceedWithToken(request, newToken)
        } else {
            response
        }
    }

    private fun Interceptor.Chain.proceedWithToken(req: Request, token: String?): Response {
        return  req.newBuilder()
            .apply {
                if (token !== null) {
                    addHeader(AUTH_HEADER, "$TOKEN_TYPE$token")
                }
            }
            .removeHeader(CUSTOM_HEADER)
            .build()
            .let(::proceed)
    }

    private suspend fun getUpdatedToken(): TokensNetworkModel {
        val refreshToken = preferencesDataSource.getRefreshToken()
        return withContext(dispatcher) {
            authApi.updateToken(updateTokenRequest = UpdateTokenRequest(refreshToken))
        }
    }
}