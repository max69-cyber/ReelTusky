package com.rayxaus.reeltusky.di

import com.rayxaus.reeltusky.data.InMemoryTasksRepository
import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.data.TasksRepository
import java.util.UUID

class AppContainer {
    // `lazy` allows creating the repository only when it's needed, not class initialization moment
    val tasksRepository: TasksRepository by lazy { InMemoryTasksRepository(initialTasks) }

    private val initialTasks = listOf(
        Task(
            id = UUID.randomUUID().toString(),
            title = "Buy groceries",
            createdAt = 1_760_000_000_000,
            description = "Milk, eggs, bread, coffee",
            isDone = true,
        ),
        Task(
            id = UUID.randomUUID().toString(),
            title = "Finish ViewModel for the task list",
            createdAt = 1_760_003_600_000,
            isDone = true,
        ),
        Task(
            id = UUID.randomUUID().toString(),
            title = "Rotate the screen and check that tasks survive",
            createdAt = 1_760_007_200_000,
        ),
        Task(
            id = UUID.randomUUID().toString(),
            title = "Read about Room and KSP",
            createdAt = 1_760_010_800_000,
            description = "developer.android.com/training/data-storage/room",
        ),
        Task(
            id = UUID.randomUUID().toString(),
            title = "Call Stacy's mom",
            createdAt = 1_760_014_400_000,
        ),
    )
}