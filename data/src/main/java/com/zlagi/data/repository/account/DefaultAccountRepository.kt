package com.zlagi.data.repository.account

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.mapper.AccountDataDomainMapper
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import com.zlagi.data.source.network.account.AccountNetworkDataSource
import com.zlagi.domain.model.AccountDomainModel
import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountCacheDataSource: AccountCacheDataSource,
    private val accountDataDomainMapper: AccountDataDomainMapper
) : AccountRepository {

    override suspend fun requestAccount(): DataResult<AccountDomainModel> {
        return when (val result = accountNetworkDataSource.getAccount()) {
            is DataResult.Success -> DataResult.Success(
                accountDataDomainMapper.from(result.data)
            )
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override suspend fun storeAccount(account: AccountDomainModel) {
        accountDataDomainMapper.to(account).let {
            accountCacheDataSource.storeAccount(it)
        }
    }

    override fun getAccount(): Flow<AccountDomainModel> {
        return accountCacheDataSource.fetchAccount().map {
            accountDataDomainMapper.from(it)
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String> {
        return when (
            val result = accountNetworkDataSource.updatePassword(
                currentPassword,
                newPassword,
                confirmNewPassword
            )
        ) {
            is DataResult.Success -> DataResult.Success(result.data)
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override suspend fun deleteAccount() {
        accountCacheDataSource.deleteAccount()
    }
}
