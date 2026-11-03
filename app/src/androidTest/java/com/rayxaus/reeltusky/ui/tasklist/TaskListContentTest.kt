package com.rayxaus.reeltusky.ui.tasklist

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.rayxaus.reeltusky.ui.taskMockWithoutDescription
import com.rayxaus.reeltusky.ui.tasksMock
import com.rayxaus.reeltusky.ui.theme.ReelTuskyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TaskListContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun setContentWithStateAndCallbacks(
        uiState: () -> TaskListUiState = { TaskListUiState() },
        onDelete: (id: String) -> Unit = {},
        onRestoreDeletedTask: () -> Unit = {},
        onTaskClick: (id: String) -> Unit = {},
    ) = composeRule.setContent {
        ReelTuskyTheme {
            TaskListContent(
                uiState = uiState(),
                onToggle = { _, _ -> },
                onAdd = { _, _ -> },
                onDelete = onDelete,
                onTaskClick = onTaskClick,
                onRestoreDeletedTask = onRestoreDeletedTask,
                onRestoreDeletedTaskDismiss = {},
            )
        }
    }

    @Test
    fun restoreTask_deletesAndRestoresTask() {
        val uiState = mutableStateOf(TaskListUiState(tasks = tasksMock))
        val taskToDelete = taskMockWithoutDescription
        var onDeleteCallsCount = 0

        setContentWithStateAndCallbacks(
            uiState = { uiState.value },
            onDelete = { id ->
                uiState.value =
                    uiState.value.copy(
                        tasks = uiState.value.tasks.filter { it.id != id },
                        lastDeletedTask = taskToDelete
                    )
                onDeleteCallsCount++
            },
            onRestoreDeletedTask = {
                uiState.value =
                    uiState.value.copy(
                        tasks = (uiState.value.tasks + taskToDelete).sortedBy { it.createdAt },
                        lastDeletedTask = null
                    )
            }
        )

        composeRule.onNodeWithText(taskToDelete.title).performTouchInput { swipeLeft() }
        composeRule.onNodeWithText(taskToDelete.title).assertDoesNotExist()

        composeRule.onNodeWithText("Undo").performClick()
        composeRule.onNodeWithText(taskToDelete.title).assertIsDisplayed()

        assertEquals(1, onDeleteCallsCount)
    }

    @Test
    fun onTaskClick_calledOneTimeWithCorrectId() {
        val taskToClick = taskMockWithoutDescription
        var onTaskClickCallsCount = 0
        var onTaskClickCalledId: String? = null

        setContentWithStateAndCallbacks(
            uiState = { TaskListUiState(tasks = tasksMock) },
            onTaskClick = { id ->
                onTaskClickCalledId = id
                onTaskClickCallsCount++
            }
        )

        composeRule.onNodeWithText(taskToClick.title).performClick()

        assertEquals(taskToClick.id, onTaskClickCalledId)
        assertEquals(1, onTaskClickCallsCount)
    }
}