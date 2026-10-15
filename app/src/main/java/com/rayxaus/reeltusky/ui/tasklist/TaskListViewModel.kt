package com.rayxaus.reeltusky.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayxaus.reeltusky.data.TasksRepository
import com.rayxaus.reeltusky.data.normalizeTaskTitleOrNull
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TasksRepository) : ViewModel() {
    val uiState: StateFlow<TaskListUiState> = repository.observeTasks()
        // lambda in `map` applies to new list from flow every time
        .map { tasks -> TaskListUiState(tasks = tasks) }
        // converts cold flow from map to hot StateFlow with initial value and coroutine.
        //
        // hot state always has something in value, and not depending on subscribers count.
        // cold flow works for every subscriber, and calls callback on each of them.
        //
        // also, stateIn allows saving subscription for time, in this case it's 5 seconds.
        //
        // viewModelScope is a coroutine scope tied to the ViewModel's lifecycle. it allows
        // executing suspend repository functions, not blocking main thread and binds lifecycle
        // of values collection to viewModel lifecycle. after 5 seconds changes
        // observation ends until new subscriber.
        //
        // initialValue takes place of value when first value from flow isn't received.
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUiState(isLoading = true)
        )

    fun onAdd(title: String, description: String) {
        val validTitle = normalizeTaskTitleOrNull(title) ?: return
        val formattedDescription = description.trim()

        viewModelScope.launch {
            repository.addTask(validTitle, formattedDescription)
        }
    }

    fun onToggle(id: String, isDone: Boolean) {
        // coroutine launches directly inside viewModel coroutine scope
        viewModelScope.launch {
            repository.setTaskDone(id, isDone)
        }
    }

    fun onDelete(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }
}