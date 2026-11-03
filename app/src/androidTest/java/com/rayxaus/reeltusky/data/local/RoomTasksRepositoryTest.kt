package com.rayxaus.reeltusky.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rayxaus.reeltusky.data.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomTasksRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: RoomTasksRepository

    @Before
    fun setUp() {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
        ).build()
        repository = RoomTasksRepository(taskDao = db.taskDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addTask_persistsTaskAndReturnsItsId() = runTest {
        val title = "New task title"
        val description = "New task description"

        val id = repository.addTask(title, description)

        val stored = requireNotNull(repository.getTaskById(id)) { "task $id not found" }

        assertEquals(title, stored.title)
        assertEquals(description, stored.description)
        assertTrue(stored.createdAt > 0)
        assertFalse(stored.isDone)
    }

    @Test
    fun addTask_twoCallsGenerateUniqueIds() = runTest {
        val idOne = repository.addTask("Task 1", "Description 1")
        val idTwo = repository.addTask("Task 2", "Description 2")

        assertNotEquals(idOne, idTwo)

        assertNotNull(repository.getTaskById(idOne))
        assertNotNull(repository.getTaskById(idTwo))
    }

    @Test
    fun restoreTask_returnsTasksToItsPlace() = runTest {
        val testTask =
            Task(
                id = "2",
                title = "Task 2",
                createdAt = 200,
                description = "Description 2",
                isDone = true
            )
        
        val tasks = listOf(
            Task(id = "1", title = "Task 1", createdAt = 100),
            testTask,
            Task(id = "3", title = "Task 3", createdAt = 300, isDone = true)
        )

        tasks.forEach { db.taskDao().insert(it.toTaskEntity()) }

        val deletedTask = requireNotNull(repository.deleteTask(testTask.id))
        val deleteResult = repository.observeTasks().first()

        assertEquals(tasks.filter { it.id != testTask.id }, deleteResult)
        assertEquals(testTask, deletedTask)

        repository.restoreTask(deletedTask)

        val restoreResult = repository.observeTasks().first()
        assertEquals(tasks, restoreResult)
    }
}