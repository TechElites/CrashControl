package com.example.crashcontrol.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.crashcontrol.ui.screens.debug.DebugViewModel

class Accelerometer(private var sensorManager: SensorManager) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val accelVals = DebugViewModel.AccelerometerValues

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val xValue = event.values[0]
            val yValue = event.values[1]
            val zValue = event.values[2]
            accelVals.x.floatValue = xValue
            accelVals.y.floatValue = yValue
            accelVals.z.floatValue = zValue
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Non utilizzato in questo esempio
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