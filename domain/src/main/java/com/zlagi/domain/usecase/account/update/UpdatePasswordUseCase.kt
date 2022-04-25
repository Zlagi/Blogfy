package com.zlagi.domain.usecase.account.update

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.result.UpdatePasswordResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.account.AccountRepository
import com.zlagi.domain.validator.AuthValidator
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

        val currentPasswordError = AuthValidator.passwordError(currentPassword)
        val newPasswordError = AuthValidator.passwordError(newPassword)
        val confirmNewPasswordError =
            AuthValidator.confirmPasswordError(newPassword, confirmNewPassword)

        if (currentPasswordError != null || newPasswordError != null || confirmNewPasswordError != null) {
            return UpdatePasswordResult(
                currentPasswordError,
                newPasswordError,
                confirmNewPasswordError
            )
        }

        return when (
            val result = withContext(dispatcher) {
                accountRepository.updatePassword(
                    currentPassword, newPassword, confirmNewPassword
                )
            }) {
            is DataResult.Success -> {
                UpdatePasswordResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                UpdatePasswordResult(result = DataResult.Error(result.exception))
            }
        }
    }
}
