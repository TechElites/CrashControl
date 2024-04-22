package com.example.crashcontrol.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

data class AccelerometerValues(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {}

class Accelerometer(private var sensorManager: SensorManager) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    var values = AccelerometerValues()

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            values = AccelerometerValues(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Non utilizzato
    }

    fun subscribe() {
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unsubscribe() {
        sensorManager.unregisterListener(this)
    }
}