package com.rayxaus.reeltusky.ui.tasklist

import com.rayxaus.reeltusky.data.Task
import com.rayxaus.reeltusky.testing.FakeTasksRepository
import com.rayxaus.reeltusky.testing.MainDispatcherRule
import com.rayxaus.reeltusky.testing.generateTaskListFixture
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val tasksFixture = generateTaskListFixture(5)
    private lateinit var repository: FakeTasksRepository
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setUp() {
        repository = FakeTasksRepository(tasksFixture)
        viewModel = TaskListViewModel(repository)
    }

    private fun TestScope.subscribeToUiState() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

    // --- ui state ---

    @Test
    fun `ui state reflects repository changes`() = runTest {
        subscribeToUiState()
        val testId = tasksFixture[1].id
        val listWithoutDeletedTask = tasksFixture.filterNot { it.id == testId }

        repository.deleteTask(testId)
        assertEquals(listWithoutDeletedTask, viewModel.uiState.value.tasks)
    }

    @Test
    fun `ui state receives data directly after subscription`() = runTest {
        assertEquals(emptyList<Task>(), viewModel.uiState.value.tasks)
        subscribeToUiState()
        assertEquals(tasksFixture, viewModel.uiState.value.tasks)
    }

    @Test
    fun `ui state changes isLoading to false after subscription`() = runTest {
        assertTrue(viewModel.uiState.value.isLoading)
        subscribeToUiState()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // --- onAdd ---

    @Test
    fun `add saves one task with trimmed title and description`() = runTest {
        val title = "\tNew task title "
        val description = " New task description\n"

        val trimmedTitle = "New task title"
        val trimmedDescription = "New task description"

        viewModel.onAdd(title, description)

        val diff = repository.tasks - tasksFixture.toSet()
        val addedTask = diff.single()

        assertEquals(trimmedTitle, addedTask.title)
        assertEquals(trimmedDescription, addedTask.description)
    }

    @Test
    fun `add does nothing with blank title`() = runTest {
        val title = " \t \n"
        val description = "New task description"

        viewModel.onAdd(title, description)

        assertEquals(tasksFixture, repository.tasks)
    }

    // --- onToggle ---

    @Test
    fun `toggle changes flag from true to false`() = runTest {
        val doneTask = tasksFixture.first { it.isDone }
        val listWithToggledTask = tasksFixture.map { task ->
            if (task.id == doneTask.id) {
                task.copy(isDone = false)
            } else {
                task
            }
        }

        viewModel.onToggle(doneTask.id, false)
        assertEquals(listWithToggledTask, repository.tasks)
    }

    @Test
    fun `toggle changes flag from false to true`() = runTest {
        val undoneTask = tasksFixture.first { !it.isDone }
        val listWithToggledTask = tasksFixture.map { task ->
            if (task.id == undoneTask.id) {
                task.copy(isDone = true)
            } else {
                task
            }
        }

        viewModel.onToggle(undoneTask.id, true)
        assertEquals(listWithToggledTask, repository.tasks)
    }

    @Test
    fun `toggle does nothing if flag is already set`() = runTest {
        val doneTask = tasksFixture.first { it.isDone }

        viewModel.onToggle(doneTask.id, true)
        assertEquals(tasksFixture, repository.tasks)
    }

    // --- onDelete ---

    @Test
    fun `delete removes task`() = runTest {
        val testId = tasksFixture[1].id
        val listWithoutDeletedTask = tasksFixture.filterNot { it.id == testId }

        viewModel.onDelete(testId)
        assertEquals(listWithoutDeletedTask, repository.tasks)
    }

    @Test
    fun `delete saves last deleted task and can rewrite it`() = runTest {
        subscribeToUiState()

        val testTask = tasksFixture[1]
        val testTask2 = tasksFixture[2]

        viewModel.onDelete(testTask.id)
        assertEquals(testTask, viewModel.uiState.value.lastDeletedTask)

        viewModel.onDelete(testTask2.id)
        assertEquals(testTask2, viewModel.uiState.value.lastDeletedTask)
    }

    @Test
    fun `deleting missing task keeps last deleted task`() = runTest {
        subscribeToUiState()

        val testTask = tasksFixture[1]

        viewModel.onDelete(testTask.id)
        assertEquals(testTask, viewModel.uiState.value.lastDeletedTask)

        viewModel.onDelete("non-existent-id")
        assertEquals(testTask, viewModel.uiState.value.lastDeletedTask)
    }

    // --- onRestoreDeletedTask ---

    @Test
    fun `restore puts back the last deleted task`() = runTest {
        val testId = tasksFixture[3].id
        val listWithoutDeletedTask = tasksFixture.filterNot { it.id == testId }

        viewModel.onDelete(testId)
        assertEquals(listWithoutDeletedTask, repository.tasks)

        viewModel.onRestoreDeletedTask()
        assertEquals(tasksFixture, repository.tasks)
    }

    @Test
    fun `restore resets last deleted task`() = runTest {
        subscribeToUiState()
        val testId = tasksFixture[3].id

        viewModel.onDelete(testId)
        assertEquals(
            tasksFixture[3],
            viewModel.uiState.value.lastDeletedTask
        )

        viewModel.onRestoreDeletedTask()
        assertNull(viewModel.uiState.value.lastDeletedTask)
    }

    @Test
    fun `repeated restore doesn't overwrite edited task`() = runTest {
        val testId = tasksFixture[3].id
        val changedTask = tasksFixture[3].copy(title = "new title", description = "new description")

        viewModel.onDelete(testId)
        viewModel.onRestoreDeletedTask()
        assertEquals(tasksFixture, repository.tasks)

        repository.updateTask(testId, changedTask.title, changedTask.description)

        viewModel.onRestoreDeletedTask()
        assertEquals(changedTask, repository.tasks.find { it.id == testId })
    }

    // --- onRestoreDeletedTaskDismiss ---

    @Test
    fun `resets last deleted task after dismiss`() = runTest {
        subscribeToUiState()

        viewModel.onDelete(tasksFixture[2].id)
        assertNotNull(viewModel.uiState.value.lastDeletedTask)

        viewModel.onRestoreDeletedTaskDismiss()
        assertNull(viewModel.uiState.value.lastDeletedTask)
    }

    @Test
    fun `dismiss keeps task deleted`() = runTest {
        val testId = tasksFixture[2].id

        viewModel.onDelete(testId)
        viewModel.onRestoreDeletedTaskDismiss()
        assertNull(repository.tasks.find { it.id == testId })
    }
}