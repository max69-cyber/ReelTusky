package com.rayxaus.reeltusky.data

import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    /** emits a list of tasks sorted by [Task.createdAt] (oldest first) */
    fun observeTasks(): Flow<List<Task>>

    /** returns the task with the given [id]; returns `null` if there's none */
    suspend fun getTaskById(id: String): Task?

    /** creates a new task with generated [Task.id] and [Task.createdAt]; returns its [Task.id] */
    suspend fun addTask(title: String, description: String): String

    /** updates the task [title] and [description]; does nothing if there's no task with the given [id] */
    suspend fun updateTask(id: String, title: String, description: String)

    /** updates the completion state of the task; does nothing if there's no task with the given [id] */
    suspend fun setTaskDone(id: String, isDone: Boolean)

    /**
     * deletes the task with provided [id]
     *
     * @return the deleted task, or `null` if there's no task with the given [id]
     */
    suspend fun deleteTask(id: String): Task?

    /**
     * restores provided task; placement of task is defined by [Task.createdAt] value (asc);
     * if task with provided [Task.id] already exists replaces it
     */
    suspend fun restoreTask(task: Task)
}