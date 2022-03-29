package com.zlagi.data.fakes.repository

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.domain.model.AccountDomainModel
import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeAccountRepository @Inject constructor() : AccountRepository {

    override suspend fun requestAccount(): DataResult<AccountDomainModel> {
        return DataResult.Success(FakeDataGenerator.accountDomain)
    }

    override suspend fun storeAccount(account: AccountDomainModel) {
        //
    }

    override fun getAccount(): Flow<AccountDomainModel> {
        return flow {
            emit(FakeDataGenerator.accountDomain)
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): DataResult<String> {
        return DataResult.Success("Updated")
    }

    override suspend fun deleteAccount() {
        //
    }
}
