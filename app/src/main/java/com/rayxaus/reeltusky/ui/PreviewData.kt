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

internal val taskMock = Task(
    id = "00000000-0000-0000-0000-000000000000",
    title = "Learn about coroutines in Kotlin",
    description = "ASAP",
    createdAt = createDateTimeInEpochMillis(2026, 8, 30, 23, 0),
)

internal val taskMockWithLongText = Task(
    id = "00000000-0000-0000-0000-000000000001",
    title = "Go deeper in scope functions in Kotlin. And about coroutines. And about trailing lambdas.",
    description = "Its very looooooooooong looooooooongy description. It must not fit in ONE line!",
    createdAt = createDateTimeInEpochMillis(2024, 2, 13, 11, 23)
)

internal val taskMockWithoutDescription = Task(
    id = "00000000-0000-0000-0000-000000000002",
    title = "Learn Jetpack Compose",
    createdAt = createDateTimeInEpochMillis(2026, 10, 9, 11, 23)
)

internal val doneTaskMock =
    taskMock.copy(id = "00000000-0000-0000-0000-000000000003", isDone = true)

internal val doneTaskMockWithLongText =
    taskMockWithLongText.copy(id = "00000000-0000-0000-0000-000000000004", isDone = true)

internal val tasksMock = listOf(
    taskMock,
    taskMockWithLongText,
    taskMockWithoutDescription,
    doneTaskMock,
    doneTaskMockWithLongText,
)

internal val singleTaskMock = listOf(
    taskMock
)

internal val manyTasksMock = listOf(
    taskMock,
    taskMockWithLongText,
    taskMockWithoutDescription,
    doneTaskMock,
    doneTaskMockWithLongText,
    taskMock.copy(id = "00000000-0000-0000-0000-000000000005"),
    taskMockWithLongText.copy(id = "00000000-0000-0000-0000-000000000006"),
    taskMockWithoutDescription.copy(id = "00000000-0000-0000-0000-000000000007"),
    doneTaskMock.copy(id = "00000000-0000-0000-0000-000000000008"),
    doneTaskMockWithLongText.copy(id = "00000000-0000-0000-0000-000000000009"),
    taskMock.copy(id = "00000000-0000-0000-0000-000000000010"),
    taskMockWithLongText.copy(id = "00000000-0000-0000-0000-000000000011"),
    taskMockWithoutDescription.copy(id = "00000000-0000-0000-0000-000000000012"),
    doneTaskMock.copy(id = "00000000-0000-0000-0000-000000000013"),
    doneTaskMockWithLongText.copy(id = "00000000-0000-0000-0000-000000000014")
)