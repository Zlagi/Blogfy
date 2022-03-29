package com.zlagi.blogfy.di

import com.zlagi.data.taskmanager.DefaultTaskManager
import com.zlagi.domain.taskmanager.TaskManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskManagerModule {

    @Binds
    abstract fun provideTaskManager(defaultTaskManager: DefaultTaskManager): TaskManager
}
