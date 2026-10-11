package com.rayxaus.reeltusky.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryTasksRepository : TasksRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // asStateFlow makes Flow read-only from outside
    override fun observeTasks(): Flow<List<Task>> =
        _tasks.asStateFlow()

    override suspend fun getTaskById(id: String): Task? =
        _tasks.value.find { it.id == id }

    override suspend fun addTask(title: String, description: String): String {
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            createdAt = System.currentTimeMillis(),
            description = description,
        )
        _tasks.update { it + newTask }

        return newTask.id
    }

    override suspend fun updateTask(
        id: String,
        title: String,
        description: String
    ) {
        _tasks.update { tasks ->
            tasks.map { task ->
                if (task.id == id) {
                    task.copy(title = title, description = description)
                } else {
                    task
                }
            }
        }
    }

    override suspend fun setTaskDone(id: String, isDone: Boolean) {
        _tasks.update { tasks ->
            tasks.map { task ->
                if (task.id == id) {
                    task.copy(isDone = isDone)
                } else {
                    task
                }
            }
        }
    }

    override suspend fun deleteTask(id: String) {
        _tasks.update { tasks ->
            tasks.filter { it.id != id }
        }
    }
}