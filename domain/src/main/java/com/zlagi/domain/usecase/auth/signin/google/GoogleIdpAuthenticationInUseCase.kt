package com.zlagi.domain.usecase.auth.signin.google

import android.content.Intent
import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.auth.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleIdpAuthenticationInUseCase @Inject constructor(
    private val repository: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(data: Intent): DataResult<Unit> {
        when (val result = withContext(dispatcher) { repository.googleIdpAuthentication(data) }) {
            is DataResult.Success -> {
                repository.storeTokens(result.data)
                return DataResult.Success(Unit)
            }
            is DataResult.Error -> {
                return DataResult.Error(result.exception)
            }
        }
    }
}