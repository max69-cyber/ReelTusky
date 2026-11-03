package com.rayxaus.reeltusky.ui.taskedit

import dagger.assisted.AssistedFactory

@AssistedFactory
interface TaskEditViewModelFactory {
    fun create(taskId: String): TaskEditViewModel
}