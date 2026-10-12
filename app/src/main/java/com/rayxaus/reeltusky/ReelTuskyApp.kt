package com.rayxaus.reeltusky

import androidx.compose.runtime.Composable
import com.rayxaus.reeltusky.ui.manyTasksMock
import com.rayxaus.reeltusky.ui.tasklist.TaskListContent

@Composable
fun ReelTuskyApp() {
    TaskListContent(
        tasks = manyTasksMock,
        onToggle = { _, _ -> },
        onTaskClick = {},
        onAddClick = {}
    )
}