package com.zlagi.domain.usecase.auth.status

import com.zlagi.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthenticationStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.authenticationStatus()
    }
}
