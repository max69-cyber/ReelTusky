package com.rayxaus.reeltusky.ui.taskedit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskEditScreen(
    taskId: String,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEditViewModel = hiltViewModel(creationCallback = { factory: TaskEditViewModelFactory ->
        factory.create(taskId)
    })
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    TaskEditContent(
        uiState = uiState.value,
        onSave = viewModel::onSave,
        onSaved = onSaved,
        onBack = onBack,
        modifier = modifier,
    )
}