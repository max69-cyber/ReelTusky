package com.rayxaus.reeltusky.navigation

import kotlinx.serialization.Serializable

@Serializable data object TaskListRoute

@Serializable data class TaskEditRoute(val taskId: String)