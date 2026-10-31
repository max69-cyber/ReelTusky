package com.rayxaus.reeltusky.testing

import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.data.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class FakeTasksRepository(initialTasks: List<Task> = emptyList()) : TasksRepository {
    private var counter = 0
    private var lastUsedCreatedAt: Long = initialTasks.maxOfOrNull { it.createdAt } ?: 0

    /** tasks should be sorted by [Task.createdAt] field ASC */
    private val _tasks = MutableStateFlow<List<Task>>(initialTasks.sortedBy { it.createdAt })

    /** public field of tasks for testing */
    val tasks: List<Task> get() = _tasks.value

    // asStateFlow makes Flow read-only from outside
    override fun observeTasks(): Flow<List<Task>> =
        _tasks.asStateFlow()

    override suspend fun getTaskById(id: String): Task? =
        _tasks.value.find { it.id == id }

    override suspend fun addTask(title: String, description: String): String {
        val newTask = Task(
            id = "fake_${counter++}",
            title = title,
            createdAt = ++lastUsedCreatedAt,
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