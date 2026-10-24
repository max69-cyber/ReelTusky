package com.rayxaus.reeltusky.ui.taskedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.rayxaus.reeltusky.R
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit task") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
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
                            onSave(
                                titleFieldState.text.toString(),
                                descriptionFieldState.text.toString()
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                        end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    )
                )
                .verticalScroll(scrollState)
        ) {
            TextField(
                state = titleFieldState,
                label = { Text("Title") },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                state = descriptionFieldState,
                label = { Text("Description") },
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier.fillMaxWidth()
            )
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
@PreviewScreenSizes
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
@PreviewScreenSizes
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