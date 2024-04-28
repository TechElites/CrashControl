package com.example.crashcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.crashcontrol.ui.screens.debug.DebugScreen
import com.example.crashcontrol.ui.theme.CrashControlTheme
import com.example.crashcontrol.utils.AccelerometerService
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    private lateinit var accelerometer: AccelerometerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accelerometer = get<AccelerometerService>()

        setContent {
            CrashControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DebugScreen(accelerometer)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer.subscribe()
    }

    override fun onPause() {
        super.onPause()
        accelerometer.unsubscribe()
    }
}