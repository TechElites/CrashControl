package com.example.crashcontrol.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.DateFormat.getDateInstance
import java.text.DecimalFormat
import java.util.Date
import kotlin.math.pow
import kotlin.math.sqrt

data class AccelerationAxis(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {}

class AccelerometerService(private val ctx: Context) : SensorEventListener {
    private var sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastMovementCrash: Long = 0
    private var movementStart: Long = 0
    var currentValues: AccelerationAxis by mutableStateOf(AccelerationAxis())
        private set
    var lastImpactDate: String by mutableStateOf("")
        private set
    var lastCrashDuration: String by mutableStateOf("0")
        private set
    var lastImpactAccelleration: Float by mutableFloatStateOf(0f)
        private set

    fun startService() {
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * @author Tomislav Curis
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            movementStart = System.currentTimeMillis()
            currentValues = AccelerationAxis(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )
            val loAccelerationReader = sqrt(
                currentValues.x.toDouble().pow(2.0)
                        + currentValues.y.toDouble().pow(2.0)
                        + currentValues.z.toDouble().pow(2.0)
            )
            val precision = DecimalFormat("0.00")
            val ldAccRound = java.lang.Double.parseDouble(precision.format(loAccelerationReader))
            // precision/fall detection and more than 1000ms after last fall
            if (ldAccRound > 0.3 && ldAccRound < 1.2 && (movementStart - lastMovementCrash) > 1000) {
                val timeStamp = getDateInstance().format(Date(System.currentTimeMillis()))
                val duration = (System.currentTimeMillis() - movementStart).toString()
                lastMovementCrash = System.currentTimeMillis()
                lastImpactDate = timeStamp
                lastCrashDuration = duration
                lastImpactAccelleration = currentValues.y
            }
        }
    }
}