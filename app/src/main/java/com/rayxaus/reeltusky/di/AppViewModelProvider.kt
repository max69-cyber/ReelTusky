package com.rayxaus.reeltusky.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.rayxaus.reeltusky.ReelTuskyApplication
import com.rayxaus.reeltusky.navigation.TaskEditRoute
import com.rayxaus.reeltusky.ui.taskedit.TaskEditViewModel
import com.rayxaus.reeltusky.ui.tasklist.TaskListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskListViewModel(
                repository = reelTuskyApplication().appContainer.tasksRepository
            )
        }
        initializer {
            TaskEditViewModel(
                repository = reelTuskyApplication().appContainer.tasksRepository,
                taskId = createSavedStateHandle().toRoute<TaskEditRoute>().taskId
            )
        }
    }

    private fun CreationExtras.reelTuskyApplication(): ReelTuskyApplication =
        this[APPLICATION_KEY] as ReelTuskyApplication
}