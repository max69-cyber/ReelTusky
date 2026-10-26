package com.rayxaus.reeltusky.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskValidationTest {
    @Test
    fun `returns initial string if there's nothing to trim and keeps inner spaces`() {
        // Arrange
        val raw = "Normal string"

        // Act
        val result = normalizeTaskTitleOrNull(raw)

        // Assert
        assertEquals(raw, result)
    }

    @Test
    fun `trims multiple spaces from sides`() {
        val raw = "  String with spaces on sides  "
        val result = normalizeTaskTitleOrNull(raw)
        assertEquals("String with spaces on sides", result)
    }

    @Test
    fun `returns null on empty string`() {
        val raw = ""
        val result = normalizeTaskTitleOrNull(raw)
        assertNull(result)
    }

    @Test
    fun `returns null on string with only spaces`() {
        val raw = "     "
        val result = normalizeTaskTitleOrNull(raw)
        assertNull(result)
    }

    @Test
    fun `trims tabs on start and end of string`() {
        val raw = "\tText\twith\ttabs\t"
        val result = normalizeTaskTitleOrNull(raw)
        assertEquals("Text\twith\ttabs", result)
    }

    @Test
    fun `trims new line on start and end of string`() {
        val raw = "\nText\nwith\nnew\nlines\n"
        val result = normalizeTaskTitleOrNull(raw)
        assertEquals("Text\nwith\nnew\nlines", result)
    }

    @Test
    fun `returns null on string with only tabs and new lines`() {
        val raw = "\t\n\t"
        val result = normalizeTaskTitleOrNull(raw)
        assertNull(result)
    }
}