package com.zlagi.domain.repository.account

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.AccountDomainModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun requestAccount(): DataResult<AccountDomainModel>
    suspend fun storeAccount(account: AccountDomainModel)
    fun getAccount(): Flow<AccountDomainModel>
    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String>
    suspend fun deleteAccount()
}