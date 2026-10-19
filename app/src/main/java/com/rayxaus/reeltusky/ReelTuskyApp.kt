package com.rayxaus.reeltusky

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rayxaus.reeltusky.navigation.TaskEditRoute
import com.rayxaus.reeltusky.navigation.TaskListRoute
import com.rayxaus.reeltusky.ui.taskedit.TaskEditScreen
import com.rayxaus.reeltusky.ui.tasklist.TaskListScreen

@Composable
fun ReelTuskyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = TaskListRoute) {
        composable<TaskListRoute> {
            TaskListScreen(
                // TODO: tap on two or more tasks at same time opens many edit screens (dropUnlessResumed())
                onTaskClick = { id -> navController.navigate(TaskEditRoute(taskId = id)) }
            )
        }
        composable<TaskEditRoute> { backStackEntry ->
            // backStackEntry == current entry for a route, toRoute deserializes it to data class
            val route = backStackEntry.toRoute<TaskEditRoute>()
            TaskEditScreen(
                taskId = route.taskId,
                onBack = { navController.navigateUp() }
            )
        }
    }
}