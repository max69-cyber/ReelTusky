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