package com.example.crashcontrol.ui.screens.addcrash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.remote.OSMPlace
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCrashScreen(
    navController: NavHostController,
    state: AddCrashState,
    actions: AddCrashActions,
    onSubmit: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var placeText by remember { mutableStateOf("") }
    var place by remember { mutableStateOf<OSMPlace?>(null) }
    var placeNotFound by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    fun isOnline(): Boolean {
        val connectivityManager =
            ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true || capabilities?.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        ) == true
    }

    fun openWirelessSettings() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.applicationContext.packageManager) != null) {
            ctx.applicationContext.startActivity(intent)
        }
    }

    val osmDataSource = koinInject<OSMDataSource>()

    val coroutineScope = rememberCoroutineScope()
    fun searchPlaces() = coroutineScope.launch {
        if (isOnline()) {
            val res = osmDataSource.searchPlaces(placeText)
            place = res.getOrNull(0)
            placeNotFound = res.isEmpty()
        } else {
            val res = snackbarHostState.showSnackbar(
                message = "No Internet connectivity",
                actionLabel = "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
                if (!state.canSubmit) return@FloatingActionButton
                onSubmit()
                navController.navigateUp()
            }) {
                Icon(Icons.Filled.Check, contentDescription = "Add New Crash")
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Oh no!",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = placeText,
                    onValueChange = { placeText = it },
                    singleLine = true,
                    modifier = Modifier.width(287.dp),
                )
                IconButton(onClick = ::searchPlaces) {
                    Icon(Icons.Outlined.Search, "Search")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Result: ${
                        when {
                            place != null -> place?.displayName
                            placeNotFound -> "Place not found"
                            else -> "-"
                        }
                    }", modifier = Modifier.width(287.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        actions.setPosition(place!!)
                        placeText = place?.displayName.toString()
                    },
                    enabled = place != null
                ) {
                    Text("Accept suggestion")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.date,
                    onValueChange = actions::setDate,
                    label = { Text("Date") })
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.time,
                    onValueChange = actions::setTime,
                    label = { Text("Time") })
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Time",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(value = state.exclamation,
                    onValueChange = actions::setExclamation,
                    label = { Text("Exclamation") })
                IconButton(onClick = { /*nothing*/ }) {
                    Icon(
                        Icons.Filled.Face,
                        contentDescription = "Exclamation",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(value = state.face,
                    onValueChange = actions::setFace,
                    label = { Text("Impact face") })
                IconButton(onClick = { /*nothing*/ }) {
                    Icon(
                        Icons.Filled.Build,
                        contentDescription = "Face",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            // date picker component
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { /*TODO*/ },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDate = Calendar.getInstance().apply {
                                    timeInMillis = datePickerState.selectedDateMillis!!
                                }
                                val formatter = SimpleDateFormat("dd/MM/yyyy")
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDate.timeInMillis
                                actions.setDate(formatter.format(calendar.time))
                                showDatePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) { Text("Cancel") }
                    }
                )
                {
                    DatePicker(state = datePickerState)
                }
            }

            // time picker component
            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { /*TODO*/ },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                actions.setTime(timePickerState.hour.toString() + ":" + timePickerState.minute)
                                showTimePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showTimePicker = false
                            }
                        ) { Text("Cancel") }
                    }
                )
                {
                    TimePicker(state = timePickerState)
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}