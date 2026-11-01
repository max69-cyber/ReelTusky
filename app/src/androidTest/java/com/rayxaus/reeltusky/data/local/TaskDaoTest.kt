package com.rayxaus.reeltusky.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()

        // db in app memory; not in the separate file like in the real app
        db = Room.inMemoryDatabaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
        ).build()
        dao = db.taskDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun createTaskEntityFixture(
        id: String = "test-id",
        title: String = "Test title",
        createdAt: Long = 100L,
        description: String = "Test description",
        isDone: Boolean = false,
    ) = TaskEntity(
        id = id,
        title = title,
        createdAt = createdAt,
        description = description,
        isDone = isDone,
    )

    private suspend fun seed(vararg entities: TaskEntity) {
        entities.forEach {
            dao.insert(it)
        }
    }

    // --- observeAll ---

    @Test
    fun observeAll_sortsByCreatedAtAscAndId() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 200)
        val entity4 = createTaskEntityFixture(id = "4", createdAt = 300)

        val sortedEntities = listOf(entity, entity2, entity3, entity4)
        seed(entity3, entity2, entity4, entity)

        val entities = dao.observeAll().first()

        assertEquals(sortedEntities, entities)
    }

    @Test
    fun observeAll_reemitsAfterInsert() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val initialEntities = listOf(entity, entity2)
        val updatedEntities = listOf(entity, entity2, entity3)
        seed(entity, entity2)

        dao.observeAll().test {
            assertEquals(initialEntities, awaitItem())
            dao.insert(entity3)
            assertEquals(updatedEntities, awaitItem())
        }
    }

    // --- updateAndGet ---

    @Test
    fun updateAndGet_updatesAndReturnsCorrectTask() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val updatedEntity =
            entity2.copy(title = "Updated title", description = "Updated description")
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val updatedEntities = listOf(entity, updatedEntity, entity3)
        seed(entity, entity2, entity3)

        val resultEntity =
            dao.updateAndGet(entity2.id, updatedEntity.title, updatedEntity.description)
        val resultList = dao.observeAll().first()

        assertEquals(updatedEntities, resultList)
        assertEquals(updatedEntity, resultEntity)
    }

    @Test
    fun updateAndGet_doesNothingAndReturnsNullOnMissingTask() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val entities = listOf(entity, entity2, entity3)
        seed(entity, entity2, entity3)

        val resultEntity =
            dao.updateAndGet("not_existing_entity_id", "Title", "Description")
        val resultList = dao.observeAll().first()

        assertEquals(entities, resultList)
        assertNull(resultEntity)
    }

    // --- updateIsDone ---

    @Test
    fun updateIsDone_updatesIsDoneField() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300, isDone = true)

        val updatedEntity2 = entity2.copy(isDone = true)
        val updatedEntity3 = entity3.copy(isDone = false)

        val updatedEntities = listOf(entity, updatedEntity2, updatedEntity3)
        seed(entity, entity2, entity3)

        dao.updateIsDone(entity2.id, true)
        dao.updateIsDone(entity3.id, false)

        val resultList = dao.observeAll().first()

        assertEquals(updatedEntities, resultList)
    }

    // --- getAndDelete ---

    @Test
    fun getAndDelete_deletesTaskAndReturnsIt() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val listWithDeletedEntity = listOf(entity, entity3)
        seed(entity, entity2, entity3)

        val deletedEntity = dao.getAndDelete(entity2.id)
        val resultList = dao.observeAll().first()

        assertEquals(listWithDeletedEntity, resultList)
        assertEquals(entity2, deletedEntity)
    }

    @Test
    fun getAndDelete_doesNothingAndReturnsNullOnMissingTask() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val entities = listOf(entity, entity2, entity3)
        seed(entity, entity2, entity3)

        val deletedEntity = dao.getAndDelete("not_existing_entity_id")
        val resultList = dao.observeAll().first()

        assertEquals(entities, resultList)
        assertNull(deletedEntity)
    }

    // --- upsert ---

    @Test
    fun upsert_replacesExistingEntity() = runTest {
        val entity = createTaskEntityFixture(id = "1", createdAt = 100)
        val entity2 = createTaskEntityFixture(id = "2", createdAt = 200)
        val entity3 = createTaskEntityFixture(id = "3", createdAt = 300)

        val entityToUpsert = createTaskEntityFixture(
            id = "2",
            title = "Updated",
            createdAt = 200,
            description = "Updated",
            isDone = true,
        )

        val upsertedList = listOf(entity, entityToUpsert, entity3)
        seed(entity, entity2, entity3)

        dao.upsert(entityToUpsert)
        val resultList = dao.observeAll().first()

        assertEquals(upsertedList, resultList)
    }
}
