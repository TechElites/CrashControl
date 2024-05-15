package com.example.crashcontrol.ui.screens.addcrash

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import com.example.crashcontrol.MainActivity
import com.example.crashcontrol.utils.LocationService
import com.example.crashcontrol.utils.PermissionStatus
import com.example.crashcontrol.utils.StartMonitoringResult
import com.example.crashcontrol.utils.rememberPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomaticCrashScreen(
    state: AddCrashState,
    actions: AddCrashActions,
    locationService: LocationService,
    onSubmit: () -> Unit
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted -> {
                val res = locationService.requestCurrentLocation()
                showLocationDisabledAlert = res == StartMonitoringResult.GPSDisabled
            }

            PermissionStatus.Denied -> showPermissionDeniedAlert = true

            PermissionStatus.PermanentlyDenied -> showPermissionPermanentlyDeniedSnackbar = true

            PermissionStatus.Unknown -> {}
        }
    }

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            val res = locationService.requestCurrentLocation()
            showLocationDisabledAlert = res == StartMonitoringResult.GPSDisabled
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    SideEffect {
        requestLocation()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ouch!",
                        fontWeight = FontWeight.Medium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(ctx, MainActivity::class.java)
                        ctx.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }, floatingActionButton = {
            FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
                if (!state.canSubmit) return@FloatingActionButton
                onSubmit()
                val intent = Intent(ctx, MainActivity::class.java)
                ctx.startActivity(intent)
            }) {
                Icon(Icons.Filled.Check, contentDescription = "Add New Crash")
            }
        }, snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How you're feeling mate?",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(value = state.exclamation,
                    onValueChange = actions::setExclamation,
                    label = { Text("Exclamation") })
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.Face, contentDescription = "Face",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(value = state.date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") })
                IconButton(onClick = {}) {
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
                OutlinedTextField(value = state.time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") })
                IconButton(onClick = {}) {
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
                OutlinedTextField(value = state.face,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Impact face") })
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.Build,
                        contentDescription = "Face",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(value = state.position ?: "",
                    onValueChange = actions::setPosition,
                    label = { Text("Current position") })
                IconButton(onClick = ::requestLocation) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Position",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Text("Latitude: ${locationService.coordinates?.latitude ?: "-"}")
            Text("Longitude: ${locationService.coordinates?.longitude ?: "-"}")
        }
        if (showLocationDisabledAlert) {
            AlertDialog(title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your current location in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationService.openLocationSettings()
                        showLocationDisabledAlert = false
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDisabledAlert = false }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { showLocationDisabledAlert = false })
        }
        if (showPermissionDeniedAlert) {
            AlertDialog(title = { Text("Location permission denied") },
                text = { Text("Location permission is required to get your current location in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationPermission.launchPermissionRequest()
                        showPermissionDeniedAlert = false
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDeniedAlert = false }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { showPermissionDeniedAlert = false })
        }
        if (showPermissionPermanentlyDeniedSnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    "Location permission is required.",
                    "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(ctx.packageManager) != null) {
                        ctx.startActivity(intent)
                    }
                }
                showPermissionPermanentlyDeniedSnackbar = false
            }
        }
    }
}