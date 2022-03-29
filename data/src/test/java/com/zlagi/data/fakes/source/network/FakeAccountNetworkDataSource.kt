package com.zlagi.data.fakes.source.network

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.source.network.account.AccountNetworkDataSource

class FakeAccountNetworkDataSource(
    val account: DataResult<AccountDataModel>,
    val updatedPassword: DataResult<String>
) : AccountNetworkDataSource {

    override suspend fun getAccount(): DataResult<AccountDataModel> {
        return account
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String> {
        return updatedPassword
    }
}
