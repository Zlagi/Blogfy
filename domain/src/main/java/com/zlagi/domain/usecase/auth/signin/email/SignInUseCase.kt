package com.zlagi.domain.usecase.auth.signin.email

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.validator.AuthValidator
import com.zlagi.common.utils.validator.result.SignInResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(email: String, password: String): SignInResult {

        val emailError = when {
            email.isEmpty() -> AuthError.EmptyField
            !AuthValidator.isValidEmail(email) -> AuthError.InvalidEmail
            else -> null
        }

        val passwordError =
            if (!AuthValidator.isValidPassword(password)) AuthError.InvalidPassword else null

        if (emailError != null || passwordError != null) {
            return SignInResult(emailError, passwordError)
        }

        return when (val result =
            withContext(dispatcher) { repository.signIn(email, password) }) {
            is DataResult.Success -> {
                repository.storeTokens(result.data)
                SignInResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                SignInResult(result = DataResult.Error(result.exception))
            }
        }
    }
}
