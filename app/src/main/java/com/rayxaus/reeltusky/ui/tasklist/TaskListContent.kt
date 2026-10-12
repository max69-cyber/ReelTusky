package com.rayxaus.reeltusky.ui.tasklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.rayxaus.reeltusky.R
import com.rayxaus.reeltusky.ui.components.TaskRow
import com.rayxaus.reeltusky.ui.manyTasksMock
import com.rayxaus.reeltusky.ui.singleTaskMock
import com.rayxaus.reeltusky.ui.tasksMock
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme

// TODO: two-pane layout for tablets?
private val maxListWidth = 650.dp
private val fabPadding = 80.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListContent(
    uiState: TaskListUiState,
    onToggle: (id: String, isDone: Boolean) -> Unit,
    onTaskClick: (id: String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tasks") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add task"
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(innerPadding).fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.tasks.isEmpty() -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(innerPadding).fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                    ) {
                        Text(
                            text = "No tasks yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap \"+\" to add a new task",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            else -> {
                // full-size LazyColumn allows scrolling from the screen edges on tablets
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding() + fabPadding,
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                    )
                ) {
                    items(uiState.tasks, key = { task -> task.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = onToggle,
                            onClick = onTaskClick,
                            modifier = Modifier.widthIn(max = maxListWidth)
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun TaskListContentPreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(tasks = tasksMock),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskListContentEmptyPreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(tasks = emptyList()),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskListContentSingleItemPreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(tasks = singleTaskMock),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskListContentManyItemsPreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(tasks = manyTasksMock),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskListContentLoadingPreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(isLoading = true),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}

@PreviewFontScale
@Composable
private fun TaskListContentEmptyFontScalePreview() {
    ReelTuskyTheme {
        TaskListContent(
            uiState = TaskListUiState(tasks = emptyList()),
            onToggle = { _, _ -> },
            onTaskClick = {},
            onAddClick = {}
        )
    }
}