package com.zlagi.domain.usecase.account.delete

import com.zlagi.domain.repository.account.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke() = accountRepository.deleteAccount()
}
