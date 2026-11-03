package com.rayxaus.reeltusky.di

import com.rayxaus.reeltusky.data.TasksRepository
import com.rayxaus.reeltusky.data.local.RoomTasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindTasksRepository(repository: RoomTasksRepository): TasksRepository
}