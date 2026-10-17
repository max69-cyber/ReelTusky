package com.rayxaus.reeltusky.ui.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rayxaus.reeltusky.di.AppViewModelProvider

@Composable
fun TaskListScreen(
    // onToggle and onDelete are handled by ViewModel, only navigation callbacks are going upper
    onTaskClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    // viewModel in signature for testing purposes
    viewModel: TaskListViewModel = viewModel<TaskListViewModel>(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    TaskListContent(
        uiState = uiState.value,
        onToggle = viewModel::onToggle,
        onAdd = viewModel::onAdd,
        onDelete = viewModel::onDelete,
        onTaskClick = onTaskClick,
        modifier = modifier
    )
}