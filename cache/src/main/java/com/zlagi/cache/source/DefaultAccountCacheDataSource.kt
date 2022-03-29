package com.zlagi.cache.source

import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.mapper.AccountCacheDataMapper
import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountCacheDataSource @Inject constructor(
    private val accountDao: AccountDao,
    private val accountCacheDataMapper: AccountCacheDataMapper,
) : AccountCacheDataSource {

    override fun fetchAccount(): Flow<AccountDataModel> =
        accountDao.fetchAccount().map {
            accountCacheDataMapper.from(it)
        }

    override suspend fun storeAccount(account: AccountDataModel) {
        accountCacheDataMapper.to(account).let {
            accountDao.storeAccount(it)
        }
    }

    override suspend fun deleteAccount() {
        accountDao.deleteAccount()
    }
}
