package com.example.crashcontrol.ui.composables

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.crashcontrol.R
import com.example.crashcontrol.data.models.ImpactFace
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.ui.screens.addcrash.AddCrashActions
import com.example.crashcontrol.ui.screens.addcrash.Mode
import com.example.crashcontrol.utils.Coordinates
import com.example.crashcontrol.utils.LocationService
import com.example.crashcontrol.utils.PermissionStatus
import com.example.crashcontrol.utils.StartMonitoringResult
import com.example.crashcontrol.utils.rememberPermission
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1

@Composable
fun PositionPicker(
    snackbarHost: SnackbarHostState,
    osmDataSource: OSMDataSource,
    locationService: LocationService?,
    snackbarHostState: SnackbarHostState,
    actions: AddCrashActions,
    ctx: Context,
    mode: Mode
) {
    var placesListexpanded by remember { mutableStateOf(false) }
    var placeText by remember { mutableStateOf("") }
    var placeFound: OSMPlace? by remember { mutableStateOf<OSMPlace?>(null) }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted -> {
                val res = locationService?.requestCurrentLocation()
                showLocationDisabledAlert = res == StartMonitoringResult.GPSDisabled
            }

            PermissionStatus.Denied -> showPermissionDeniedAlert = true

            PermissionStatus.PermanentlyDenied -> showPermissionPermanentlyDeniedSnackbar = true

            PermissionStatus.Unknown -> {}
        }
    }

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

    val coroutineScopeRequest = rememberCoroutineScope()
    fun requestLocation() = coroutineScopeRequest.launch {
        if (locationPermission.status.isGranted) {
            val res1 = locationService?.requestCurrentLocation()
            showLocationDisabledAlert = res1 == StartMonitoringResult.GPSDisabled
            val latitude = locationService?.coordinates?.latitude
            val longitude = locationService?.coordinates?.longitude
            if (latitude == null || longitude == null) {
                placeText = ctx.getString(R.string.no_place_result)
                return@launch
            }
            val place = OSMPlace(0, latitude, longitude, ctx.getString(R.string.current_position))
            placeText = place.displayName
            actions.setPosition(place)
            if (isOnline()) {
                val placeSearch = osmDataSource.getPlace(Coordinates(latitude, longitude))
                if (placeSearch.latitude != 0.0) {
                    placeText = placeSearch.displayName
                }
            } else {
                val res2 = snackbarHost.showSnackbar(
                    message = ctx.getString(R.string.no_internet_connectivity),
                    actionLabel = ctx.getString(R.string.open_settings),
                    duration = SnackbarDuration.Long
                )
                if (res2 == SnackbarResult.ActionPerformed) {
                    openWirelessSettings()
                }
            }
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    if (mode == Mode.Automatic) {
        LaunchedEffect(Unit) {
            requestLocation()
        }
    }

    val coroutineScopeSearch = rememberCoroutineScope()
    fun searchPlaces() = coroutineScopeSearch.launch {
        if (isOnline()) {
            val place = osmDataSource.searchPlaces(placeText)
            if (place.isNotEmpty()) {
                placeFound = place[0]
            }
            placesListexpanded = true
        } else {
            val res = snackbarHost.showSnackbar(
                message = ctx.getString(R.string.no_internet_connectivity),
                actionLabel = ctx.getString(R.string.open_settings),
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
        }
    }

    val icon: @Composable () -> Unit
    if (mode == Mode.Manual) {
        icon = {
            IconButton(onClick = {
                searchPlaces()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            }
        }
    } else {
        icon = {
            IconButton(onClick = {
                requestLocation()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_my_location_24),
                    contentDescription = "Location"
                )
            }
        }
    }

    Box {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = placeText,
                    modifier = Modifier.width(280.dp),
                    onValueChange = { placeText = it },
                    readOnly = mode == Mode.Automatic,
                    label = { Text(stringResource(R.string.position)) },
                    trailingIcon = icon,
                    singleLine = true
                )
            }
            DropdownMenu(
                expanded = placesListexpanded,
                onDismissRequest = { placesListexpanded = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            placeFound?.displayName ?: ctx.getString(R.string.no_place_result)
                        )
                    },
                    onClick = {
                        if (placeFound != null) {
                            actions.setPosition(placeFound!!)
                            placeText = placeFound!!.displayName
                            placesListexpanded = false
                        }
                    }
                )
            }
        }
    }
    if (showLocationDisabledAlert) {
        AlertDialog(title = { Text(ctx.getString(R.string.location_disabled)) },
            text = { Text(ctx.getString(R.string.enable_location_for)) },
            confirmButton = {
                TextButton(onClick = {
                    locationService?.openLocationSettings()
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
        AlertDialog(title = { Text(ctx.getString(R.string.location_denied)) },
            text = { Text(ctx.getString(R.string.location_required_for)) },
            confirmButton = {
                TextButton(onClick = {
                    locationPermission.launchPermissionRequest()
                    showPermissionDeniedAlert = false
                }) {
                    Text(ctx.getString(R.string.grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedAlert = false }) {
                    Text(ctx.getString(R.string.dismiss))
                }
            },
            onDismissRequest = { showPermissionDeniedAlert = false })
    }
    if (showPermissionPermanentlyDeniedSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                ctx.getString(R.string.location_required),
                ctx.getString(R.string.open_settings),
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

@Composable
fun FacePicker(
    value: String, onValueChange: KFunction1<String, Unit>, mode: Mode
) {
    val ctx = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val options = ImpactFace().toList()
    var selectedOption by remember { mutableStateOf(ctx.getString(options[0])) }

    Box {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = { expanded = !expanded },
                label = { Text(ctx.getString(R.string.impact_face)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { if (mode == Mode.Manual) expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Icon"
                        )
                    }
                },
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(ctx.getString(option)) }, onClick = {
                        selectedOption = ctx.getString(option)
                        onValueChange(ctx.getString(option))
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: Int = R.string.select_time,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    val ctx = LocalContext.current
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
                    shape = MaterialTheme.shapes.extraLarge, color = containerColor
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
                    text = ctx.getString(title),
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