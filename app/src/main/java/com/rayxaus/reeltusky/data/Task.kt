package com.rayxaus.reeltusky.data

data class Task (
    val id: String,
    val title: String,
    val createdAt: Long,
    val description: String = "",
    val isDone: Boolean = false,
)
