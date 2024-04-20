package com.example.crashcontrol.ui.screens.debug

import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel

class DebugViewModel : ViewModel() {
    object AccelerometerValues {
        val x = mutableFloatStateOf(0f)
        val y = mutableFloatStateOf(0f)
        val z = mutableFloatStateOf(0f)
    }
}