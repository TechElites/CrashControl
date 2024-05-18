package com.example.crashcontrol.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getString
import com.example.crashcontrol.CrashActivity
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.util.Date
import java.util.Random
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class AccelerationAxis(
    val x: Float = 0f, val y: Float = 0f, val z: Float = 0f
) {}

class AccelerometerService(private val ctx: Context) : SensorEventListener {
    private var notificationSender: NotificationService = NotificationService(ctx)
    private var sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastMovementCrash: Long = 0
    private var movementStart: Long = 0
    var currentValues: AccelerationAxis by mutableStateOf(AccelerationAxis())
        private set
    var lastCrash: Crash = Crash(0, 0.0, 0.0, "", false, "", "", "Up")
        private set

    fun startService(notificationService: NotificationService) {
        notificationSender = notificationService
        notificationService.createNotificationChannel("CrashControl")
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun StopService() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * @author Tomislav Curis
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            movementStart = System.currentTimeMillis()
            currentValues = AccelerationAxis(
                x = event.values[0], y = event.values[1], z = event.values[2]
            )
            val loAccelerationReader = sqrt(
                currentValues.x.toDouble().pow(2.0) + currentValues.y.toDouble()
                    .pow(2.0) + currentValues.z.toDouble().pow(2.0)
            )
            //val precision = DecimalFormat("0.00")
            //val ldAccRound: Double? = (precision.format(loAccelerationReader)).toDoubleOrNull()
            val ldAccRound: Double = loAccelerationReader
            // precision/fall detection and more than 1000ms after last fall
            if (ldAccRound != null) {
                if (ldAccRound > 0.3 && ldAccRound < 1.2 && (movementStart - lastMovementCrash) > 1000) {
                    crash()
                }
            }
        }
    }

    fun crash() {
        val date = getDateInstance().format(Date(System.currentTimeMillis()))
        val time = getTimeInstance().format(Date(System.currentTimeMillis()))
        val face = getImpactFace(currentValues)
        lastMovementCrash = System.currentTimeMillis()
        lastCrash = Crash(0, 0.0, 0.0, "", false, date, time, face)
        val intent = Intent(ctx, CrashActivity::class.java).apply {
            putExtra("date", date)
            putExtra("time", time)
            putExtra("face", face)
        }
        val r = Random()
        val no: Int = r.nextInt(999999)
        val pendingIntent = PendingIntent.getActivity(ctx, no, intent, PendingIntent.FLAG_MUTABLE)
        notificationSender.showNotification(
            getString(ctx, R.string.crash_notification_title),
            getString(ctx, R.string.crash_notification_message),
            pendingIntent
        )
    }

    private fun getImpactFace(values: AccelerationAxis): String {
        return when {
            abs(values.x) > abs(values.y) && abs(values.x) > abs(values.z) -> {
                if (values.x < 0) "Left" else "Right"
            }

            abs(values.y) > abs(values.x) && abs(values.y) > abs(values.z) -> {
                if (values.y < 0) "Up" else "Down"
            }

            abs(values.z) > abs(values.x) && abs(values.z) > abs(values.y) -> {
                if (values.z < 0) "Front" else "Back"
            }

            else -> "Up"
        }
    }
}