/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zlagi.data.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.account.detail.SyncAccountUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncAccountUseCase: SyncAccountUseCase

) : CoroutineWorker(appContext, params) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        return when (syncAccountUseCase()) {
            is DataResult.Success -> {
                Result.success()
            }
            is DataResult.Error -> {
                Result.Failure()
            }
        }
    }
}
