package com.rayxaus.reeltusky

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rayxaus.reeltusky.navigation.TaskEditRoute
import com.rayxaus.reeltusky.navigation.TaskListRoute
import com.rayxaus.reeltusky.ui.taskedit.TaskEditScreen
import com.rayxaus.reeltusky.ui.tasklist.TaskListScreen

@Composable
fun ReelTuskyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TaskListRoute) {
        composable<TaskListRoute> { backStackEntry ->
            // backStackEntry == current entry for a route
            TaskListScreen(
                onTaskClick = { id ->
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(TaskEditRoute(taskId = id))
                    }
                }
            )
        }
        composable<TaskEditRoute> {
            TaskEditScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}