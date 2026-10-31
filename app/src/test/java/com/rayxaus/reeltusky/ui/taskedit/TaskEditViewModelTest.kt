package com.rayxaus.reeltusky.ui.taskedit

import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.testing.FakeTasksRepository
import com.rayxaus.reeltusky.testing.MainDispatcherRule
import com.rayxaus.reeltusky.testing.generateTaskFixture
import com.rayxaus.reeltusky.ui.taskedit.TaskEditUiState.Loaded.SavingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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

    private fun TestScope.createAndInitViewModel(taskId: String = task.id): TaskEditViewModel {
        val viewModel = createViewModel(taskId)
        // completes init coroutine
        runCurrent()

        return viewModel
    }

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
        val viewModel = createAndInitViewModel(taskId = "not_existing_task_id")
        assertEquals(TaskEditUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `sets Loaded with Idle state and task from the repository`() = runTest {
        val viewModel = createAndInitViewModel()

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
        val viewModel = createAndInitViewModel()

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
        val viewModel = createAndInitViewModel()
        val updatedTask = task.copy(title = "New title", description = "New description")

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
        val viewModel = createAndInitViewModel()

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
    fun `onSave does nothing if task isn't found`() = runTest {
        val viewModel = createAndInitViewModel(taskId = "not_existing_task_id")

        viewModel.onSave(title = "New title", description = "New description")

        assertEquals(listOf(task), repository.tasks)
        assertEquals(TaskEditUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `onSave does nothing if task already saved`() = runTest {
        val viewModel = createAndInitViewModel()
        val savedTask = task.copy(title = "New title", description = "New description")

        viewModel.onSave(title = savedTask.title, description = savedTask.description)
        runCurrent()

        viewModel.onSave(title = "Second update title", description = "Second update description")

        assertEquals(listOf(savedTask), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = savedTask,
                savingState = SavingState.Saved
            ),
            viewModel.uiState.value
        )

        runCurrent()

        assertEquals(listOf(savedTask), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = savedTask,
                savingState = SavingState.Saved
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSave ignores second call while saving`() = runTest {
        val viewModel = createAndInitViewModel()
        val updatedTask = task.copy(title = "New title", description = "New description")
        val updatedTask2 = task.copy(title = "New title 2", description = "New description 2")

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
        val viewModel = createAndInitViewModel()
        val untrimmedTask = task.copy(title = "\tNew title ", description = " New description\n")
        val trimmedTask = untrimmedTask.copy(
            title = untrimmedTask.title.trim(),
            description = untrimmedTask.description.trim(),
        )

        viewModel.onSave(title = untrimmedTask.title, description = untrimmedTask.description)
        runCurrent()

        assertEquals(listOf(trimmedTask), repository.tasks)
        assertEquals(
            TaskEditUiState.Loaded(
                task = trimmedTask,
                savingState = SavingState.Saved
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSave does nothing when task has a blank title`() = runTest {
        val viewModel = createAndInitViewModel()

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