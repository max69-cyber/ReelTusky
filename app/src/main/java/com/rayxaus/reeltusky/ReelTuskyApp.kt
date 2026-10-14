package com.rayxaus.reeltusky

import androidx.compose.runtime.Composable
import com.rayxaus.reeltusky.ui.tasklist.TaskListScreen

@Composable
fun ReelTuskyApp() {
    TaskListScreen(
        onTaskClick = {},
        onAddClick = {},
    )
}