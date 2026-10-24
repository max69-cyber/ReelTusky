package com.rayxaus.reeltusky.ui.taskedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.rayxaus.reeltusky.R
import com.rayxaus.reeltusky.data.normalizeTaskTitleOrNull
import com.rayxaus.reeltusky.ui.taskMock
import com.rayxaus.reeltusky.ui.taskMockWithLongText
import com.rayxaus.reeltusky.ui.taskedit.TaskEditUiState.Loaded.SavingState
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditContent(
    uiState: TaskEditUiState,
    onSave: (title: String, description: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current

    val titleFieldState = rememberTextFieldState()
    val descriptionFieldState = rememberTextFieldState()

    val scrollState = rememberScrollState()

    var initializedTaskId by rememberSaveable { mutableStateOf<String?>(null) }

    val title = titleFieldState.text.toString()
    val description = descriptionFieldState.text.toString()

    val loadedState = uiState as? TaskEditUiState.Loaded
    val loadedTask = loadedState?.task

    val isTitleValid = normalizeTaskTitleOrNull(title) != null
    val isIdle = loadedState?.savingState == SavingState.Idle
    val isSaving = loadedState?.savingState == SavingState.Saving
    val hasChanges =
        loadedTask != null && (loadedTask.title != title || loadedTask.description != description)
    val isSaveButtonEnabled = isIdle && isTitleValid && hasChanges

    LaunchedEffect(loadedTask?.id) {
        if (loadedTask != null && loadedTask.id != initializedTaskId) {
            titleFieldState.setTextAndPlaceCursorAtEnd(loadedTask.title)
            descriptionFieldState.setTextAndPlaceCursorAtEnd(loadedTask.description)

            initializedTaskId = loadedTask.id
        }
    }

    LaunchedEffect(loadedState?.savingState) {
        if (loadedState?.savingState == SavingState.Saved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                TopAppBar(
                    title = { Text("Edit task") },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            enabled = !isSaving
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onSave(title, description)
                            },
                            enabled = isSaveButtonEnabled
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_save),
                                contentDescription = "Save"
                            )
                        }
                    }
                )
                if (isSaving) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        },
        modifier = modifier
    ) { innerPadding ->
        when (uiState) {
            TaskEditUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            TaskEditUiState.NotFound -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Task is not found",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            is TaskEditUiState.Loaded -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(
                            PaddingValues(
                                top = innerPadding.calculateTopPadding() + 16.dp,
                                bottom = innerPadding.calculateBottomPadding(),
                                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                            )
                        )
                        .verticalScroll(scrollState)
                ) {
                    TextField(
                        state = titleFieldState,
                        enabled = isIdle,
                        label = { Text("Title") },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        state = descriptionFieldState,
                        enabled = isIdle,
                        label = { Text("Description") },
                        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 7),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun TaskEditContentPreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.Loaded(task = taskMock),
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskEditContentSavingPreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Saving),
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskEditContentSavedPreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Saved),
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskEditContentNotFoundPreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.NotFound,
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun TaskEditContentLoadingPreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.Loading,
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}

@PreviewFontScale
@Composable
private fun TaskEditContentFontScalePreview() {
    ReelTuskyTheme {
        TaskEditContent(
            uiState = TaskEditUiState.Loaded(task = taskMockWithLongText),
            onSave = { _, _ -> },
            onBack = {},
        )
    }
}