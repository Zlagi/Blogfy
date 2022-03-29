package com.zlagi.domain.usecase.auth.signup

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.validator.AuthValidator
import com.zlagi.common.utils.validator.AuthValidator.isAlphaNumeric
import com.zlagi.common.utils.validator.result.SignUpResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): SignUpResult {

        val emailError = when {
            email.isEmpty() -> AuthError.EmptyField
            !AuthValidator.isValidEmail(email) -> AuthError.InvalidEmail
            else -> null
        }
        val usernameError = when {
            username.isEmpty() -> AuthError.EmptyField
            username.count() < 2 -> AuthError.InputTooShort
            !username.isAlphaNumeric() -> AuthError.InvalidUsername
            else -> null
        }
        val passwordError = when {
            password.isEmpty() -> AuthError.EmptyField
            !AuthValidator.isValidPassword(password) -> AuthError.InputTooShort
            else -> null
        }
        val confirmPasswordError = when {
            confirmPassword.isEmpty() -> AuthError.EmptyField
            !AuthValidator.isValidPassword(confirmPassword) -> AuthError.InputTooShort
            !AuthValidator.passwordMatches(
                password,
                confirmPassword
            ) -> AuthError.UnmatchedPassword
            else -> null
        }

        if (emailError != null || usernameError != null || passwordError != null || confirmPasswordError != null) {
            return SignUpResult(emailError, usernameError, passwordError, confirmPasswordError)
        }

        return when (val result =
            withContext(dispatcher) {
                repository.signUp(
                    email,
                    username,
                    password,
                    confirmPassword
                )
            }) {
            is DataResult.Success -> {
                repository.storeTokens(result.data)
                SignUpResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                SignUpResult(result = DataResult.Error(result.exception))
            }
        }
    }
}
