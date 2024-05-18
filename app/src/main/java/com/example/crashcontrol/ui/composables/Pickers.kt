package com.example.crashcontrol.ui.composables

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.ui.screens.addcrash.Mode
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1

@Composable
fun PositionPicker(
    snackbarHost: SnackbarHostState,
    osmDataSource: OSMDataSource,
    mode: Mode,
    onSelect: (OSMPlace) -> Unit,
) {
    // !TODO: searching for a position now returns no results
    val ctx = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var placeText by remember { mutableStateOf("") }
    if (mode == Mode.Automatic) {
        placeText = ctx.getString(R.string.current_position)

    }
    var places: MutableList<OSMPlace> = mutableListOf()

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

    val coroutineScope = rememberCoroutineScope()
    fun searchPlaces() = coroutineScope.launch {
        if (isOnline()) {
            places = osmDataSource.searchPlaces(placeText).toMutableList()
            if (places.size > 3) {
                places = places.subList(0, 3)
            }
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
                expanded = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Icon"
                )
            }
        }
    } else {
        icon = {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Icon"
            )
        }
    }

    Box {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = placeText,
                    onValueChange = { if (mode == Mode.Manual) placeText = it },
                    label = { Text(stringResource(R.string.position)) },
                    trailingIcon = icon,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                places.forEach { option ->
                    DropdownMenuItem(text = { Text(option.displayName) }, onClick = {
                        onSelect(option)
                        placeText = option.displayName
                        expanded = false
                    })
                }
                if (places.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(ctx.getString(R.string.no_result)) },
                        onClick = { expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun FacePicker(
    value: String,
    onValueChange: KFunction1<String, Unit>,
    mode: Mode
) {
    val ctx = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        R.string.left,
        R.string.right,
        R.string.up,
        R.string.down,
        R.string.front,
        R.string.back
    )
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