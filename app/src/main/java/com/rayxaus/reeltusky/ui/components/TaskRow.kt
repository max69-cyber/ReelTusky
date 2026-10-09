package com.rayxaus.reeltusky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.ui.doneTaskMock
import com.rayxaus.reeltusky.ui.doneTaskMockWithLongText
import com.rayxaus.reeltusky.ui.taskMock
import com.rayxaus.reeltusky.ui.taskMockWithLongText
import com.rayxaus.reeltusky.ui.taskMockWithoutDescription
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme
import java.time.Instant
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

@PreviewLightDark
@Composable
private fun TaskRowWithoutDescriptionPreview() {
    ReelTuskyTheme {
        TaskRow(taskMockWithoutDescription, onToggle = {}, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun TaskRowPreview() {
    ReelTuskyTheme {
        TaskRow(taskMock, onToggle = {}, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun TaskRowDonePreview() {
    ReelTuskyTheme {
        TaskRow(doneTaskMock, onToggle = {}, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun TaskRowLongTextPreview() {
    ReelTuskyTheme {
        TaskRow(taskMockWithLongText, onToggle = {}, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun TaskRowDoneLongTextPreview() {
    ReelTuskyTheme {
        TaskRow(doneTaskMockWithLongText, onToggle = {}, onClick = {})
    }
}

@PreviewFontScale
@Composable
private fun TaskRowLongTextFontScalePreview() {
    ReelTuskyTheme {
        TaskRow(taskMockWithLongText, onToggle = {}, onClick = {})
    }
}

@PreviewFontScale
@Composable
private fun TaskRowDoneLongTextFontScalePreview() {
    ReelTuskyTheme {
        TaskRow(doneTaskMockWithLongText, onToggle = {}, onClick = {})
    }
}