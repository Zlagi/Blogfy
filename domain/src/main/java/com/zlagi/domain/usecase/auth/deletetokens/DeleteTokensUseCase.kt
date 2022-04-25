package com.zlagi.domain.usecase.auth.deletetokens

import com.zlagi.domain.repository.auth.AuthRepository
import javax.inject.Inject

class DeleteTokensUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.fetchTokens().refreshToken.let {
            repository.revokeToken(it)
        }
        repository.deleteTokens()
    }
}
