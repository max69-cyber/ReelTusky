package com.rayxaus.reeltusky.ui.taskedit

import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.testing.FakeTasksRepository
import com.rayxaus.reeltusky.testing.MainDispatcherRule
import com.rayxaus.reeltusky.testing.generateTaskFixture
import com.rayxaus.reeltusky.ui.taskedit.TaskEditUiState.Loaded.SavingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class TaskEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var repository: FakeTasksRepository
    private val task = generateTaskFixture()

    private fun createViewModel(taskId: String = task.id) =
        TaskEditViewModel(repository, taskId)

    @Before
    fun setUp() {
        repository = FakeTasksRepository(listOf(task))
    }

    // --- uiState + init ---

    @Test
    fun `starts with Loading before init coroutine runs`() = runTest {
        val viewModel = createViewModel()

        // no runCurrent() = before init
        assertEquals(TaskEditUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `sets NotFound if the task doesn't exist`() = runTest {
        val viewModel = createViewModel(taskId = "not_existing_task_id")

        runCurrent()
        assertEquals(TaskEditUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `sets Loaded with Idle state and task from the repository`() = runTest {
        val viewModel = createViewModel()

        runCurrent()
        assertEquals(
            TaskEditUiState.Loaded(
                task = task,
                savingState = SavingState.Idle
            ),
            viewModel.uiState.value
        )
    }

    // --- onSave ---

    @Test
    fun `onSave sets Saving before the repository call`() = runTest {
        val viewModel = createViewModel()

        // completes init
        runCurrent()

        viewModel.onSave(title = "New title", description = "New description")

        // without runCurrent repository.updateTask isn't calling:
        assertEquals(listOf(task), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = task,
                savingState = SavingState.Saving
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSave after success update sets Saved state and updated task`() = runTest {
        val viewModel = createViewModel()
        val updatedTask = task.copy(title = "New title", description = "New description")

        // completes init
        runCurrent()

        viewModel.onSave(title = updatedTask.title, description = updatedTask.description)
        runCurrent()

        assertEquals(listOf(updatedTask), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = updatedTask,
                savingState = SavingState.Saved
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSave sets state to NotFound if the task was deleted while editing`() = runTest {
        val viewModel = createViewModel()

        // completes init
        runCurrent()

        repository.deleteTask(task.id)

        viewModel.onSave(title = "New title", description = "New description")
        runCurrent()

        assertEquals(emptyList<Task>(), repository.tasks)
        assertEquals(TaskEditUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `onSave does nothing if task isn't loaded`() = runTest {
        val viewModel = createViewModel()

        viewModel.onSave(title = "New title", description = "New description")

        assertEquals(listOf(task), repository.tasks)
        assertEquals(TaskEditUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `onSave not initiating update more than one time`() = runTest {
        val viewModel = createViewModel()
        val updatedTask = task.copy(title = "New title", description = "New description")
        val updatedTask2 = task.copy(title = "New title 2", description = "New description 2")

        // completes init
        runCurrent()

        viewModel.onSave(title = updatedTask.title, description = updatedTask.description)
        viewModel.onSave(title = updatedTask2.title, description = updatedTask2.description)

        assertEquals(listOf(task), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = task,
                savingState = SavingState.Saving
            ),
            viewModel.uiState.value
        )

        runCurrent()

        assertEquals(listOf(updatedTask), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = updatedTask,
                savingState = SavingState.Saved
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSave trims title and description`() = runTest {
        val viewModel = createViewModel()
        val untrimmedTask = task.copy(title = "\tNew title ", description = " New description\n")
        val trimmedTask = untrimmedTask.copy(
            title = untrimmedTask.title.trim(),
            description = untrimmedTask.description.trim(),
        )

        // completes init
        runCurrent()

        viewModel.onSave(title = untrimmedTask.title, description = untrimmedTask.description)
        runCurrent()

        assertEquals(listOf(trimmedTask), repository.tasks)
    }

    @Test
    fun `onSave does nothing when task have blank title`() = runTest {
        val viewModel = createViewModel()

        // completes init
        runCurrent()

        viewModel.onSave(title = " \t \n ", description = "New description")
        runCurrent()

        assertEquals(listOf(task), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = task,
                savingState = SavingState.Idle,
            ),
            viewModel.uiState.value
        )
    }
}