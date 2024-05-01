package com.example.crashcontrol.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.repositories.CrashesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CrashesState(val crashes: List<Crash>)

class CrashesViewModel(
    private val repository: CrashesRepository
) : ViewModel() {
    val state = repository.crashes.map { CrashesState(crashes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CrashesState(emptyList())
    )

    fun addCrash(crash: Crash) = viewModelScope.launch {
        repository.upsert(crash)
    }

    fun deleteCrash(crash: Crash) = viewModelScope.launch {
        repository.delete(crash)
    }
}