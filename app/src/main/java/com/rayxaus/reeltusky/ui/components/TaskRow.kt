package com.rayxaus.reeltusky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

// private instance of DateTimeFormatter, so a new one isn't created on every `formatTime` call
private val formatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

// formats the time in a human-readable form and in the device's time zone. here's the chain:
// epoch millis from task.createdAt or any other source (as Long)
// -> Instant via ofEpochMilli() (Java type, describing a point on the timeline)
// -> ZonedDateTime via atZone() (now with time zone - the system default zone)
// -> String via format() (with formatter declared above). also rechecks the system locale on every call.
private fun formatTime(millis: Long): String =
    Instant
        .ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(formatter.withLocale(Locale.getDefault()))


@Composable
fun TaskRow(
    task: Task,
    onToggle: (id: String) -> Unit,
    onClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable(onClick = { onClick(task.id) }),
        overlineContent = { Text(formatTime(task.createdAt)) },
        headlineContent = {
            Text(
                text = task.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )
        },
        supportingContent = {
            if (task.description.isNotBlank())
                Text(task.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle(task.id) }
            )
        }
    )
}

val taskMockWithoutDescription = Task(
    id = "00000000-0000-0000-0000-000000000000",
    title = "Learn Jetpack Compose",
    createdAt = LocalDateTime
        .of(2026, 10, 9, 9, 45)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
)
val taskMockWithDescription = Task(
    id = "00000000-0000-0000-0000-000000000001",
    title = "Learn about coroutines in Kotlin",
    description = "ASAP",
    createdAt = LocalDateTime
        .of(2026, 8, 30, 23, 0)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    isDone = true
)
val taskMockWithLongDescriptionAndTitle = Task(
    id = "00000000-0000-0000-0000-000000000002",
    title = "Go deeper in scope functions in Kotlin. And about coroutines. And about trailing lambdas.",
    description = "Its very looooooooooong looooooooongy description. It must not fit in ONE line!",
    createdAt = LocalDateTime
        .of(2024, 2, 13, 11, 23)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
)

@Preview(showBackground = true)
@Composable
private fun TaskRowPreview() {
    ReelTuskyTheme {
        Column {
            TaskRow(taskMockWithoutDescription, onToggle = {}, onClick = {})
            TaskRow(taskMockWithDescription, onToggle = {}, onClick = {})
            TaskRow(taskMockWithLongDescriptionAndTitle, onToggle = {}, onClick = {})
        }
    }
}