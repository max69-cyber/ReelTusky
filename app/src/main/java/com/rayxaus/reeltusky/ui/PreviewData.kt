package com.rayxaus.reeltusky.ui

import com.rayxaus.reeltusky.data.Task
import java.time.LocalDateTime
import java.time.ZoneId

private fun createDateTimeInEpochMillis(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) =
    LocalDateTime
        .of(year, month, dayOfMonth, hour, minute)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

internal val taskMockWithoutDescription = Task(
    id = "00000000-0000-0000-0000-000000000000",
    title = "Learn Jetpack Compose",
    createdAt = createDateTimeInEpochMillis(2026, 10, 9, 11, 23)
)
internal val doneTaskMockWithDescription = Task(
    id = "00000000-0000-0000-0000-000000000001",
    title = "Learn about coroutines in Kotlin",
    description = "ASAP",
    createdAt = createDateTimeInEpochMillis(2026, 8, 30, 23, 0),
    isDone = true
)
internal val taskMockWithDescription = Task(
    id = "00000000-0000-0000-0000-000000000001",
    title = "Learn about coroutines in Kotlin",
    description = "ASAP",
    createdAt = createDateTimeInEpochMillis(2026, 8, 30, 23, 0),
)
internal val taskMockWithLongDescriptionAndTitle = Task(
    id = "00000000-0000-0000-0000-000000000002",
    title = "Go deeper in scope functions in Kotlin. And about coroutines. And about trailing lambdas.",
    description = "Its very looooooooooong looooooooongy description. It must not fit in ONE line!",
    createdAt = createDateTimeInEpochMillis(2024, 2, 13, 11, 23)
)

internal val tasksMock = listOf(
    taskMockWithoutDescription,
    doneTaskMockWithDescription,
    taskMockWithDescription,
    taskMockWithLongDescriptionAndTitle,
)