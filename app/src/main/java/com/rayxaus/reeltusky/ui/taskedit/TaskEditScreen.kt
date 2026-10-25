package com.rayxaus.reeltusky.ui.taskedit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rayxaus.reeltusky.di.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEditViewModel = viewModel<TaskEditViewModel>(factory = AppViewModelProvider.Factory)
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