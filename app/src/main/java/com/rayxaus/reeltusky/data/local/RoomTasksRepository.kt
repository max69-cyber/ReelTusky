package com.rayxaus.reeltusky.data.local

import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.data.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomTasksRepository(private val taskDao: TaskDao) : TasksRepository {
    override fun observeTasks(): Flow<List<Task>> = taskDao.observeAll().map { entities ->
        entities.map { entity -> entity.toTask() }
    }

    override suspend fun getTaskById(id: String): Task? = taskDao.getById(id)?.toTask()

    override suspend fun addTask(title: String, description: String): String {
        val newTaskEntity = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            createdAt = System.currentTimeMillis(),
            description = description,
        ).toTaskEntity()

        taskDao.insert(newTaskEntity)

        return newTaskEntity.id
    }

    override suspend fun updateTask(id: String, title: String, description: String): Task? =
        taskDao.updateAndGet(id, title, description)?.toTask()

    override suspend fun setTaskDone(id: String, isDone: Boolean) {
        taskDao.updateIsDone(id, isDone)
    }

    override suspend fun deleteTask(id: String): Task? =
        taskDao.getAndDelete(id)?.toTask()

    override suspend fun restoreTask(task: Task) {
        taskDao.upsert(task.toTaskEntity())
    }
}