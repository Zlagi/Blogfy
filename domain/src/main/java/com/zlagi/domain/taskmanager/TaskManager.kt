package com.zlagi.domain.taskmanager

import kotlinx.coroutines.flow.Flow
import java.util.*

interface TaskManager {
    fun syncAccount(): UUID
    fun observeTask(taskId: UUID): Flow<TaskState>
    fun abortAllTasks()
}