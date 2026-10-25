package com.rayxaus.reeltusky.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rayxaus.reeltusky.data.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    val description: String,
    @ColumnInfo(name = "is_done")
    val isDone: Boolean,
)

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    createdAt = createdAt,
    isDone = isDone,
)

// extension function equals `fun toTaskEntity(task: Task)` signature, where the Task is receiver
// with extension function here `domain` layer (Task) doesn't depend on `data` layer (TaskEntity)
fun Task.toTaskEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    createdAt = createdAt,
    isDone = isDone,
)