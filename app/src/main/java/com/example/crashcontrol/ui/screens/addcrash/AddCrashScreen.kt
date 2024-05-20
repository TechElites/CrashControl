package com.example.crashcontrol.ui.screens.addcrash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.crashcontrol.MainActivity
import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.ui.composables.BasicAlertDialog
import com.example.crashcontrol.ui.composables.BasicField
import com.example.crashcontrol.ui.composables.FacePicker
import com.example.crashcontrol.ui.composables.IconButtonField
import com.example.crashcontrol.ui.composables.PositionPicker
import com.example.crashcontrol.ui.composables.TimePickerDialog
import com.example.crashcontrol.utils.LocationService
import org.koin.compose.koinInject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class Mode { Automatic, Manual }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCrashScreen(
    navController: NavHostController?,
    state: AddCrashState,
    actions: AddCrashActions,
    onSubmit: () -> Unit,
    locationService: LocationService?,
) {
    val mode: Mode = if (navController != null) Mode.Manual else Mode.Automatic

    val snackbarHostState = remember { SnackbarHostState() }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    var showWrongInputAlert by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    fun addEvent(title: String, exclamation: String, location: String, begin: Long, end: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, exclamation)
            if (location.isNotEmpty()) {
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            }
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
        }
        startActivity(ctx, intent, null)
    }

    val openAlertDialog = remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
        FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
            if (!state.canSubmit()) {
                showWrongInputAlert = true
                return@FloatingActionButton
            }
            onSubmit()
            actions.saveFBCrash()
            openAlertDialog.value = true
        }) {
            Icon(Icons.Filled.Check, contentDescription = "Add New Crash")
        }
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ctx.getString(R.string.crash_title),
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PositionPicker(
                    snackbarHost = snackbarHostState,
                    osmDataSource = koinInject<OSMDataSource>(),
                    locationService = locationService,
                    snackbarHostState = snackbarHostState,
                    actions = actions,
                    ctx = ctx,
                    mode = mode
                )
            }
            IconButtonField(text = R.string.date,
                icon = Icons.Filled.DateRange,
                value = state.date,
                onNewValue = actions::setDate,
                onButtonClicked = { if (mode == Mode.Manual) showDatePicker = true })
            IconButtonField(text = R.string.time,
                icon = Icons.Filled.DateRange,
                value = state.time,
                onNewValue = actions::setTime,
                onButtonClicked = { if (mode == Mode.Manual) showTimePicker = true })
            BasicField(
                text = R.string.exclamation,
                value = state.exclamation,
                onNewValue = actions::setExclamation,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FacePicker(
                    value = state.face, onValueChange = actions::setFace, mode = mode
                )
            }

            if (showDatePicker) {
                DatePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = {
                    TextButton(onClick = {
                        val selectedDate = Calendar.getInstance().apply {
                            timeInMillis =
                                if (datePickerState.selectedDateMillis == null || datePickerState.selectedDateMillis == 0L) {
                                    System.currentTimeMillis()
                                } else {
                                    datePickerState.selectedDateMillis!!
                                }
                        }
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDate.timeInMillis
                        actions.setDate(formatter.format(calendar.time))
                        showDatePicker = false
                    }) { Text("OK") }
                }, dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) { Text("Cancel") }
                }) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                TimePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = {
                    TextButton(onClick = {
                        val f: NumberFormat = DecimalFormat("00")
                        actions.setTime(
                            f.format(timePickerState.hour).toString() + ":" + f.format(
                                timePickerState.minute
                            ).toString()
                        )
                        showTimePicker = false
                    }) { Text("OK") }
                }, dismissButton = {
                    TextButton(onClick = {
                        showTimePicker = false
                    }) { Text("Cancel") }
                }) {
                    TimePicker(state = timePickerState)
                }
            }
        }
        when {
            openAlertDialog.value -> {
                BasicAlertDialog(
                    onDismissRequest = {
                        openAlertDialog.value = false
                        if (mode == Mode.Manual && navController != null) {
                            navController.navigateUp()
                        } else {
                            val intent = Intent(ctx, MainActivity::class.java)
                            ctx.startActivity(intent)
                        }
                    },
                    onConfirmation = {
                        openAlertDialog.value = false
                        addEvent(
                            "Crash",
                            state.exclamation,
                            location = if (state.position != null) {
                                state.position.displayName
                            } else {
                                ""
                            },
                            SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${state.date} ${state.time}")!!.time,
                            SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${state.date} ${state.time}")!!.time
                        )
                        if (mode == Mode.Manual && navController != null) {
                            navController.navigateUp()
                        } else {
                            val intent = Intent(ctx, MainActivity::class.java)
                            Handler(Looper.getMainLooper()).postDelayed({
                                ctx.startActivity(intent)
                            }, 1500)
                        }
                    },
                    dialogTitle = "Add to Calendar",
                    dialogText = "Do you want to add this crash to your calendar?",
                    icon = Icons.Default.DateRange
                )
            }
        }
    }
    if (showWrongInputAlert) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                ContextCompat.getString(ctx, R.string.empty_fields_error),
                duration = SnackbarDuration.Long
            )
            showWrongInputAlert = false
        }
    }
}