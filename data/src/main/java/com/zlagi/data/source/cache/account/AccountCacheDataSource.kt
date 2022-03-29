package com.zlagi.data.source.cache.account

import com.zlagi.data.model.AccountDataModel
import kotlinx.coroutines.flow.Flow

interface AccountCacheDataSource {
    fun fetchAccount(): Flow<AccountDataModel>
    suspend fun storeAccount(account: AccountDataModel)
    suspend fun deleteAccount()
}
