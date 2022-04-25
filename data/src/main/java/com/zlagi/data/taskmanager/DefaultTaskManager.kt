package com.zlagi.data.taskmanager

import androidx.lifecycle.asFlow
import androidx.work.*
import com.zlagi.data.worker.RefreshDataWorker
import com.zlagi.domain.taskmanager.TaskManager
import com.zlagi.domain.taskmanager.TaskState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DefaultTaskManager @Inject constructor(
    private val workManager: WorkManager
) : TaskManager {

    companion object {
        const val workName = "blogfy"
        const val repeatInterval: Long = 15
        const val backOffDelay: Long = 1
    }

    override fun syncAccount(): UUID {
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            repeatInterval,
            TimeUnit.MINUTES
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            backOffDelay,
            TimeUnit.MINUTES
        ).setConstraints(
            workerConstraints
        ).setInitialDelay(
            repeatInterval,
            TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workerRequest
        )
        return workerRequest.id
    }

    override fun observeTask(taskId: UUID): Flow<TaskState> {
        return workManager.getWorkInfoByIdLiveData(taskId)
            .asFlow()
            .map { mapWorkInfoStateToTaskState(it.state) }
    }

    override fun abortAllTasks() {
        workManager.cancelAllWork()
    }

    private fun mapWorkInfoStateToTaskState(state: WorkInfo.State): TaskState = when (state) {
        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> TaskState.SCHEDULED
        WorkInfo.State.CANCELLED -> TaskState.CANCELLED
        WorkInfo.State.FAILED -> TaskState.FAILED
        WorkInfo.State.SUCCEEDED, WorkInfo.State.RUNNING -> TaskState.COMPLETED
    }
}
