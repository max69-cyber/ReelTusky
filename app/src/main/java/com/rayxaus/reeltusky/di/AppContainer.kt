package com.rayxaus.reeltusky.di

import android.content.Context
import androidx.room.Room
import com.rayxaus.reeltusky.data.TasksRepository
import com.rayxaus.reeltusky.data.local.AppDatabase
import com.rayxaus.reeltusky.data.local.RoomTasksRepository

class AppContainer(context: Context) {
    // guaranteed applicationContext (100% not from Activity)
    private val applicationContext = context.applicationContext

    private val database by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
            name = "reeltusky.db"
        ).build()
    }

    // `lazy` allows creating the repository only when it's needed, not class initialization moment
    val tasksRepository: TasksRepository by lazy { RoomTasksRepository(database.taskDao()) }
}