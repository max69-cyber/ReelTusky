package com.rayxaus.reeltusky.ui.taskedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rayxaus.reeltusky.data.TasksRepository
import com.rayxaus.reeltusky.data.normalizeTaskTitleOrNull
import com.rayxaus.reeltusky.navigation.TaskEditRoute
import com.rayxaus.reeltusky.ui.taskedit.TaskEditUiState.Loaded.SavingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskEditViewModel(
    private val repository: TasksRepository,
    // constructor arg without `val` doesn't count as class field, but as a variable
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    /** route parameter from viewModel's Bundle-based store */
    private val taskId = savedStateHandle.toRoute<TaskEditRoute>().taskId

    // private mutable state
    private val _uiState = MutableStateFlow<TaskEditUiState>(TaskEditUiState.Loading)
    // public readonly state
    val uiState = _uiState.asStateFlow()

    // init can use class fields declared before it, but not fields after
    init {
        viewModelScope.launch {
            val currentTask = repository.getTaskById(taskId)
            _uiState.value =
                currentTask?.let { TaskEditUiState.Loaded(it) } ?: TaskEditUiState.NotFound
        }
    }

    fun onSave(title: String, description: String) {
        // guaranteed state in `Loaded` condition, or `return` if it is not.
        // allows to call `copy` function on a state.
        // only loaded task can be copied.
        val loadedState = _uiState.value as? TaskEditUiState.Loaded ?: return

        if (loadedState.savingState != SavingState.Idle) return

        val normalizedTitle = normalizeTaskTitleOrNull(title) ?: return
        val normalizedDescription = description.trim()

        _uiState.value = loadedState.copy(savingState = SavingState.Saving)

        viewModelScope.launch {
            val updatedTask = repository.updateTask(taskId, normalizedTitle, normalizedDescription)

            _uiState.value = updatedTask?.let {
                loadedState.copy(task = it, savingState = SavingState.Saved)
            } ?: TaskEditUiState.NotFound
        }
    }
}