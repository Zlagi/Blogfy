package com.zlagi.domain.usecase.account.detail

import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke() = accountRepository.getAccount().first()
}
