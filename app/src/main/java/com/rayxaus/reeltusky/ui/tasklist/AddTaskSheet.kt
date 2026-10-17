package com.rayxaus.reeltusky.ui.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.rayxaus.reeltusky.data.normalizeTaskTitleOrNull
import com.rayxaus.reeltusky.ui.taskMock
import com.rayxaus.reeltusky.ui.taskMockWithLongText
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onAdd: (title: String, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        val coroutineScope = rememberCoroutineScope()

        val titleState = rememberTextFieldState()
        val descriptionState = rememberTextFieldState()

        val isCreateButtonEnabled =
            normalizeTaskTitleOrNull(titleState.text.toString()) != null
                    && sheetState.targetValue != SheetValue.Hidden

        AddTaskForm(
            titleState = titleState,
            descriptionState = descriptionState,
            isCreateButtonEnabled = isCreateButtonEnabled,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            onAddClick = {
                onAdd(titleState.text.toString(), descriptionState.text.toString())
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            },
        )
    }
}

@Composable
private fun AddTaskForm(
    titleState: TextFieldState,
    descriptionState: TextFieldState,
    isCreateButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.verticalScroll(scrollState)
    ) {
        TextField(
            state = titleState,
            label = { Text("Title") },
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            state = descriptionState,
            label = { Text("Description") },
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 7),
            modifier = Modifier.fillMaxWidth()
        )
        FilledTonalButton(
            onClick = onAddClick,
            enabled = isCreateButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create")
        }
    }
}

@PreviewLightDark
@Composable
private fun AddTaskFormPreview() {
    val titleState = TextFieldState(initialText = taskMock.title)
    val descriptionState = TextFieldState(initialText = taskMock.description)

    ReelTuskyTheme {
        Surface {
            AddTaskForm(
                titleState = titleState,
                descriptionState = descriptionState,
                isCreateButtonEnabled = true,
                onAddClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddTaskFormEmptyPreview() {
    val titleState = TextFieldState()
    val descriptionState = TextFieldState()

    ReelTuskyTheme {
        Surface {
            AddTaskForm(
                titleState = titleState,
                descriptionState = descriptionState,
                isCreateButtonEnabled = false,
                onAddClick = {}
            )
        }
    }
}

@PreviewFontScale
@Composable
private fun AddTaskFormLongTextFontScalePreview() {
    val titleState = TextFieldState(initialText = taskMockWithLongText.title)
    // repeated to exceed maxHeightInLines of the description field
    val descriptionState = TextFieldState(initialText = taskMockWithLongText.description.repeat(4))

    ReelTuskyTheme {
        Surface {
            AddTaskForm(
                titleState = titleState,
                descriptionState = descriptionState,
                isCreateButtonEnabled = true,
                onAddClick = {}
            )
        }
    }
}