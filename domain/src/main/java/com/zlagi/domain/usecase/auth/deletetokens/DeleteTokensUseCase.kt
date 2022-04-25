package com.zlagi.domain.usecase.auth.deletetokens

import com.zlagi.domain.repository.auth.AuthRepository
import javax.inject.Inject

class DeleteTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.fetchTokens().refreshToken.let {
            authRepository.revokeToken(it)
        }
        authRepository.deleteTokens()
    }
}
