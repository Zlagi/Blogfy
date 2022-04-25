package com.zlagi.domain.usecase.auth.signup

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.domain.validator.AuthValidator
import com.zlagi.common.utils.result.SignUpResult
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

        val emailError = AuthValidator.emailError(email)
        val usernameError = AuthValidator.usernameError(username)
        val passwordError = AuthValidator.passwordError(password)
        val confirmPasswordError = AuthValidator.confirmPasswordError(password, confirmPassword)

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
