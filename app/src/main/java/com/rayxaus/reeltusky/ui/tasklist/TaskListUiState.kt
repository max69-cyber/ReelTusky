package com.rayxaus.reeltusky.ui.tasklist

import com.rayxaus.reeltusky.data.Task

/** all data that ui needs to render */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    /** `true` until the first list of tasks is loaded */
    val isLoading: Boolean = false,
)
