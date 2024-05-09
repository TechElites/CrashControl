package com.example.crashcontrol.ui.screens.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crashcontrol.utils.AccelerometerService
import kotlinx.coroutines.delay

@Composable
fun DebugScreen(accelerometer: AccelerometerService) {
    var values = accelerometer.currentValues
    var date: String = accelerometer.lastImpactDate
    var duration: String = accelerometer.lastCrashDuration
    var accelleration: Float = accelerometer.lastImpactAccelleration

    LaunchedEffect(accelerometer) {
        val updateInterval = 1000L // Aggiorna ogni secondo
        while (true) {
            values = accelerometer.currentValues
            date = accelerometer.lastImpactDate
            duration = accelerometer.lastCrashDuration
            accelleration = accelerometer.lastImpactAccelleration
            delay(updateInterval)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Accellerometer values",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Accelleration on X: ${values.x} m/s²",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Accelleration on Y: ${values.y} m/s²",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Accelleration on Z: ${values.z} m/s²",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Lastest Fall",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Date: $date",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Duration: $duration ms",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Impact accelleration: $accelleration m/s²",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}