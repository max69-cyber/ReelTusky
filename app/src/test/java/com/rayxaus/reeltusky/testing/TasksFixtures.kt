package com.rayxaus.reeltusky.testing

import com.rayxaus.reeltusky.data.Task

fun generateTaskListFixture(taskCount: Int) = List(taskCount) { index ->
    Task(
        id = index.toString(),
        title = "Task $index",
        createdAt = index.toLong(),
        description = "Description $index",
        isDone = index % 2 == 0
    )
}

fun generateTaskFixture(id: String = "1", isDone: Boolean = false) = Task(
    id = id,
    title = "Task 1",
    createdAt = 1.toLong(),
    description = "Description 1",
    isDone = isDone,
)