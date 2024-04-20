package com.example.crashcontrol.ui.screens.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun DebugScreen() {
    val accelVals = DebugViewModel.AccelerometerValues
    val xValue by accelVals.x
    val yValue by accelVals.y
    val zValue by accelVals.z
    Column {
        Text(text = "Accelerometer values:")
        Text(text = "X: $xValue")
        Text(text = "Y: $yValue")
        Text(text = "Z: $zValue")
    }
}