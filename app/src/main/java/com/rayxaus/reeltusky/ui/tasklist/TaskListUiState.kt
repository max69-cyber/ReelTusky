package com.rayxaus.reeltusky.ui.tasklist

import com.rayxaus.reeltusky.data.Task

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
)
