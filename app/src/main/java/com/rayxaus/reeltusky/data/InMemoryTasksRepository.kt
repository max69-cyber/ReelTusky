package com.rayxaus.reeltusky.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import java.util.UUID

/**
 * @param initialTasks should be provided in ascending order by [Task.createdAt]
 */
class InMemoryTasksRepository(initialTasks: List<Task> = emptyList()) : TasksRepository {
    /** tasks should be sorted by [Task.createdAt] field ASC */
    private val _tasks = MutableStateFlow<List<Task>>(initialTasks)

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
    ): Task? {
        return _tasks.updateAndGet { tasks ->
            tasks.map { task ->
                if (task.id == id) {
                    task.copy(title = title, description = description)
                } else {
                    task
                }
            }
        }.find { it.id == id }
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

    override suspend fun deleteTask(id: String): Task? {
        val previousTasks = _tasks.getAndUpdate { tasks ->
            tasks.filter { it.id != id }
        }

        return previousTasks.find { task -> task.id == id }
    }

    override suspend fun restoreTask(task: Task) {
        _tasks.update { tasks ->
            (tasks.filter { it.id != task.id } + task).sortedBy { it.createdAt }
        }
    }
}