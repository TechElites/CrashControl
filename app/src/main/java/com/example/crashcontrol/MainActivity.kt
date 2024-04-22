package com.example.crashcontrol

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.crashcontrol.ui.screens.debug.DebugScreen
import com.example.crashcontrol.ui.theme.CrashControlTheme
import com.example.crashcontrol.utils.Accelerometer

class MainActivity : ComponentActivity() {
    private var accelerometer: Accelerometer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accelerometer = Accelerometer(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
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
        accelerometer?.subscribe()
    }

    override fun onPause() {
        super.onPause()
        accelerometer?.unsubscribe()
    }
}