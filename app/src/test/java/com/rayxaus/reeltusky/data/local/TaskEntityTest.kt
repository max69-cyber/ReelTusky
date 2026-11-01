package com.rayxaus.reeltusky.data.local

import com.rayxaus.reeltusky.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskEntityTest {
    @Test
    fun `toTaskEntity and toTask preserve all fields`() {
        val domain = Task(
            id = "test_id",
            title = "Test title",
            createdAt = 1_700_000_000_000,
            description = "Test description",
            isDone = true,
        )

        val result = domain.toTaskEntity().toTask()
        assertEquals(domain, result)
    }
}