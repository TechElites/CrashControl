package com.example.crashcontrol.ui.screens.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crashcontrol.utils.AccelerometerService
import kotlinx.coroutines.delay

@Composable
fun DebugScreen(accelerometer: AccelerometerService) {
    var values = accelerometer.currentValues
    var crash = accelerometer.lastCrash

    LaunchedEffect(accelerometer) {
        val updateInterval = 1000L // Aggiorna ogni secondo
        while (true) {
            values = accelerometer.currentValues
            crash = accelerometer.lastCrash
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
            text = "Lastest Crash",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Date: ${crash.date}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Time: ${crash.time}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Impact face: ${crash.face}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { accelerometer.crash() }) {
            Text(text = "Simulate crash")
        }
    }
}