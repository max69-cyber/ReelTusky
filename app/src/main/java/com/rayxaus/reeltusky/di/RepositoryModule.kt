package com.rayxaus.reeltusky.di

import com.rayxaus.reeltusky.data.TasksRepository
import com.rayxaus.reeltusky.data.local.RoomTasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTasksRepository(repository: RoomTasksRepository): TasksRepository
}