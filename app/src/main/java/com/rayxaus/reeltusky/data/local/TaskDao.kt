package com.rayxaus.reeltusky.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY created_at ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: String): TaskEntity?

    @Insert
    suspend fun insert(taskEntity: TaskEntity)

    @Query("UPDATE tasks SET title = :title, description = :description WHERE id = :id")
    suspend fun updateTitleAndDescription(id: String, title: String, description: String)

    @Transaction
    suspend fun updateAndGet(id: String, title: String, description: String): TaskEntity? {
        updateTitleAndDescription(id, title, description)
        return getById(id)
    }

    @Query("UPDATE tasks SET is_done = :isDone WHERE id = :id")
    suspend fun updateIsDone(id: String, isDone: Boolean)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    suspend fun getAndDelete(id: String): TaskEntity? {
        val task = getById(id) ?: return null
        deleteById(id)
        return task
    }

    @Upsert
    suspend fun upsert(taskEntity: TaskEntity)
}