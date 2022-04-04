package com.zlagi.data.fakes.source.cache

import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAccountCacheDataSource : AccountCacheDataSource {
    private val blogs = arrayListOf<AccountDataModel>()

    override fun fetchAccount(): Flow<AccountDataModel> {
        return flow {
            emit(blogs[0])
        }
    }

    override suspend fun storeAccount(account: AccountDataModel) {
        blogs.add(account)
    }

    override suspend fun deleteAccount() {
        blogs.clear()
    }
}
