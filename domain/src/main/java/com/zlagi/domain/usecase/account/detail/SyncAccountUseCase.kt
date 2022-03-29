package com.zlagi.domain.usecase.account.detail

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): DataResult<Unit> {
        return when (val result = withContext(dispatcher) { accountRepository.requestAccount() }) {
            is DataResult.Success -> {
                accountRepository.storeAccount(result.data)
                DataResult.Success(Unit)
            }
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }
}