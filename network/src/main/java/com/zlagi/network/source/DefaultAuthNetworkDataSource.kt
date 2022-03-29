package com.zlagi.network.source

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.connectivity.ConnectivityChecker
import com.zlagi.data.model.TokensDataModel
import com.zlagi.data.source.network.auth.AuthNetworkDataSource
import com.zlagi.network.apiservice.AuthApiService
import com.zlagi.network.mapper.TokensNetworkDataMapper
import com.zlagi.network.model.NetworkParameters
import com.zlagi.network.model.request.GoogleSignInRequest
import com.zlagi.network.model.request.SignInRequest
import com.zlagi.network.model.request.SignUpRequest
import com.zlagi.network.model.request.UpdateTokenRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultAuthNetworkDataSource @Inject constructor(
    private val authApiService: AuthApiService,
    private val firebaseAuth: FirebaseAuth,
    private val tokensNetworkDataMapper: TokensNetworkDataMapper,
    private val connectivityChecker: ConnectivityChecker,
    private val exceptionMapper: ExceptionMapper
) : AuthNetworkDataSource {

    override suspend fun signIn(email: String, password: String): DataResult<TokensDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                authApiService.signIn(SignInRequest(email, password)).let {
                    tokensNetworkDataMapper.from(it)
                }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun signUp(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): DataResult<TokensDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                authApiService.signUp(SignUpRequest(email, username, password, confirmPassword))
                    .let {
                        tokensNetworkDataMapper.from(it)
                    }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun googleIdpAuthentication(data: Intent): DataResult<TokensDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(data).await()
            val firebaseToken = getTokenResultFirebase(googleAccount)
            DataResult.Success(
                authApiService.googleSignIn(
                    NetworkParameters.TOKEN_TYPE + firebaseToken?.token,
                    GoogleSignInRequest(googleAccount?.displayName)
                ).let {
                    tokensNetworkDataMapper.from(it)
                }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun revokeToken(token: String) {
        authApiService.revokeToken(UpdateTokenRequest(token))
    }

    private suspend fun getTokenResultFirebase(
        googleAccount: GoogleSignInAccount
    ): GetTokenResult? {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
        return firebaseAuth.currentUser?.getIdToken(false)?.await()
    }
}
