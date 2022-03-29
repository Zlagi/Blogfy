package com.zlagi.data.source.network.account

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.AccountDataModel

interface AccountNetworkDataSource {
    suspend fun getAccount(): DataResult<AccountDataModel>
    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String>
}
