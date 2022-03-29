package com.zlagi.data.fakes.source.cache

import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAccountCacheDataSource : AccountCacheDataSource {

    override fun fetchAccount(): Flow<AccountDataModel> {
        return flow {
            emit(FakeDataGenerator.account)
        }
    }

    override suspend fun storeAccount(account: AccountDataModel) {
        //
    }

    override suspend fun deleteAccount() {
        //
    }
}
