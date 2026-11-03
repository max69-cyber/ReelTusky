package com.rayxaus.reeltusky.ui.taskedit

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.rayxaus.reeltusky.ui.taskMock
import com.rayxaus.reeltusky.ui.taskedit.TaskEditUiState.Loaded.SavingState
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TaskEditContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun setContentWithStateAndCallbacks(
        uiState: () -> TaskEditUiState,
        onSave: (title: String, description: String) -> Unit = { _, _ -> },
        onSaved: () -> Unit = {},
    ) = composeRule.setContent {
        ReelTuskyTheme {
            TaskEditContent(
                uiState = uiState(),
                onSave = onSave,
                onSaved = onSaved,
                onBack = {},
            )
        }
    }

    @Test
    fun saveButton_disabledWhenTitleIsBlank() {
        setContentWithStateAndCallbacks(
            uiState = {
                TaskEditUiState.Loaded(
                    task = taskMock,
                    savingState = SavingState.Idle
                )
            },
        )

        composeRule.onNodeWithText(taskMock.title).performTextReplacement(" \t\n ")
        composeRule.onNodeWithContentDescription("Save").assertIsNotEnabled()
    }

    @Test
    fun saveButton_disabledWhenTitleIsNotChanged() {
        setContentWithStateAndCallbacks(
            uiState = {
                TaskEditUiState.Loaded(
                    task = taskMock,
                    savingState = SavingState.Idle
                )
            },
        )

        composeRule.onNodeWithContentDescription("Save").assertIsNotEnabled()
    }

    @Test
    fun saveButton_enabledWhenTitleIsChanged() {
        setContentWithStateAndCallbacks(
            uiState = {
                TaskEditUiState.Loaded(
                    task = taskMock,
                    savingState = SavingState.Idle
                )
            },
        )

        composeRule.onNodeWithText(taskMock.title).performTextReplacement("\tUpdated title ")
        composeRule.onNodeWithContentDescription("Save").assertIsEnabled()
    }

    @Test
    fun saveButton_enabledWhenDescriptionIsChanged() {
        setContentWithStateAndCallbacks(
            uiState = {
                TaskEditUiState.Loaded(
                    task = taskMock,
                    savingState = SavingState.Idle
                )
            },
        )

        composeRule.onNodeWithText(taskMock.description)
            .performTextReplacement("Updated description\n")

        composeRule.onNodeWithContentDescription("Save").assertIsEnabled()
    }

    @Test
    fun fieldsAndButtons_disabledWhenSaving() {
        val uiStateMock =
            mutableStateOf(TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Idle))
        val updatedTitle = "Updated title"

        setContentWithStateAndCallbacks(uiState = { uiStateMock.value })

        composeRule.onNodeWithText(taskMock.title).performTextReplacement(updatedTitle)
        composeRule.onNodeWithContentDescription("Save").assertIsEnabled()

        uiStateMock.value = uiStateMock.value.copy(savingState = SavingState.Saving)

        composeRule.onNodeWithText(updatedTitle).assertIsNotEnabled()
        composeRule.onNodeWithText(taskMock.description).assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Save").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Back").assertIsNotEnabled()
    }

    @Test
    fun fieldsAndSaveButton_disabledWhenSaved() {
        val uiStateMock =
            mutableStateOf(TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Idle))
        val updatedTitle = "Updated title"

        setContentWithStateAndCallbacks(uiState = { uiStateMock.value })

        composeRule.onNodeWithText(taskMock.title).performTextReplacement(updatedTitle)
        composeRule.onNodeWithContentDescription("Save").assertIsEnabled()

        uiStateMock.value = uiStateMock.value.copy(savingState = SavingState.Saved)

        composeRule.onNodeWithText(updatedTitle).assertIsNotEnabled()
        composeRule.onNodeWithText(taskMock.description).assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Save").assertIsNotEnabled()
    }

    @Test
    fun saveButton_disabledWhenTaskIsNotLoaded() {
        setContentWithStateAndCallbacks(
            uiState = { TaskEditUiState.Loading },
        )

        composeRule.onNodeWithContentDescription("Save").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Back").assertIsEnabled()
    }

    @Test
    fun onSaved_calledOneTimeAfterSaving() {
        var onSavedCallCounter = 0
        val uiStateMock =
            mutableStateOf(TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Idle))

        setContentWithStateAndCallbacks(
            uiState = { uiStateMock.value },
            onSaved = { onSavedCallCounter++ },
        )
        val updatedTitle = "Updated title"

        composeRule.onNodeWithText(taskMock.title).performTextReplacement(updatedTitle)
        uiStateMock.value = uiStateMock.value.copy(savingState = SavingState.Saved)

        // not Compose-based assertEquals needs to wait for recomposition manually
        composeRule.runOnIdle {
            assertEquals(1, onSavedCallCounter)
        }
    }

    @Test
    fun saveButton_callsOnSaveWithTypedValues() {
        var onSaveCallCounter = 0
        var savedTitle: String? = null
        var savedDescription: String? = null

        setContentWithStateAndCallbacks(
            uiState = { TaskEditUiState.Loaded(task = taskMock, savingState = SavingState.Idle) },
            onSave = { title, description ->
                onSaveCallCounter++
                savedTitle = title
                savedDescription = description
            },
        )
        val updatedTitle = "Updated title"
        val updatedDescription = "Updated description"

        composeRule.onNodeWithText(taskMock.title).performTextReplacement(updatedTitle)
        composeRule.onNodeWithText(taskMock.description).performTextReplacement(updatedDescription)

        composeRule.onNodeWithContentDescription("Save").performClick()

        assertEquals(1, onSaveCallCounter)
        assertEquals(updatedTitle, savedTitle)
        assertEquals(updatedDescription, savedDescription)
    }
}