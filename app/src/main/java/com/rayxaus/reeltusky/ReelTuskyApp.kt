package com.rayxaus.reeltusky

import androidx.compose.runtime.Composable
import com.rayxaus.reeltusky.ui.manyTasksMock
import com.rayxaus.reeltusky.ui.tasklist.TaskListContent
import com.rayxaus.reeltusky.ui.tasklist.TaskListUiState

@Composable
fun ReelTuskyApp() {
    TaskListContent(
        uiState = TaskListUiState(manyTasksMock),
        onDelete = {},
        onToggle = { _, _ -> },
        onTaskClick = {},
        onAddClick = {}
    )
}