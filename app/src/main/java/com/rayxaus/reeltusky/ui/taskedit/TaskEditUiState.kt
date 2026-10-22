package com.rayxaus.reeltusky.ui.taskedit

import com.rayxaus.reeltusky.data.Task

sealed interface TaskEditUiState {
    data object Loading : TaskEditUiState
    data object NotFound : TaskEditUiState
    data class Loaded(
        val task: Task,
        val savingState: SavingState = SavingState.Idle
    ) : TaskEditUiState {
        enum class SavingState {
            Idle,
            Saving,
            Saved,
        }
    }
}
