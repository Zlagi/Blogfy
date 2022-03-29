package com.zlagi.network.source

import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.connectivity.ConnectivityChecker
import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.source.network.account.AccountNetworkDataSource
import com.zlagi.network.apiservice.AccountApiService
import com.zlagi.network.mapper.AccountNetworkDataMapper
import com.zlagi.network.model.request.PasswordRequest
import javax.inject.Inject

class DefaultAccountNetworkDataSource @Inject constructor(
    private val accountApiService: AccountApiService,
    private val accountNetworkDataMapper: AccountNetworkDataMapper,
    private val connectivityChecker: ConnectivityChecker,
    private val exceptionMapper: ExceptionMapper
) : AccountNetworkDataSource {

    override suspend fun getAccount(): DataResult<AccountDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                accountApiService.getAccount().let {
                    accountNetworkDataMapper.from(it)
                }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                accountApiService.updatePassword(
                    PasswordRequest(
                        currentPassword,
                        newPassword,
                        confirmNewPassword
                    )
                ).message
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }
}

