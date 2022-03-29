package com.zlagi.domain.usecase.account.update

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.*
import com.zlagi.common.utils.validator.AuthValidator
import com.zlagi.common.utils.validator.result.UpdatePasswordResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.account.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        currentPassword: String, newPassword: String, confirmNewPassword: String
    ): UpdatePasswordResult {

        val currentPasswordError =
            if (!AuthValidator.isValidPassword(currentPassword)) AuthError.InputTooShort else null

        val newPasswordError =
            if (!AuthValidator.isValidPassword(newPassword)) AuthError.InputTooShort else null

        val confirmNewPasswordError = when {
            !AuthValidator.isValidPassword(confirmNewPassword) -> AuthError.InputTooShort
            !AuthValidator.passwordMatches(
                newPassword,
                confirmNewPassword
            ) -> AuthError.UnmatchedPassword
            else -> null
        }

        if (currentPasswordError != null || newPasswordError != null || confirmNewPasswordError != null) {
            return UpdatePasswordResult(
                currentPasswordError,
                newPasswordError,
                confirmNewPasswordError
            )
        }

        return when (val result = withContext(dispatcher) {
            accountRepository.updatePassword(
                currentPassword, newPassword, confirmNewPassword
            )
        }
        ) {
            is DataResult.Success -> {
                UpdatePasswordResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                UpdatePasswordResult(result = DataResult.Error(result.exception))
            }
        }
    }
}