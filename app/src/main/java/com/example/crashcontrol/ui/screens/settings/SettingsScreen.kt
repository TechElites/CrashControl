package com.example.crashcontrol.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.crashcontrol.R
import com.example.crashcontrol.data.models.Theme
import com.example.crashcontrol.utils.NotificationService
import com.example.crashcontrol.utils.PermissionStatus
import com.example.crashcontrol.utils.rememberPermission

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsScreen(
    state: SettingsState,
    changeTheme: (Theme) -> Unit,
    notificationService: NotificationService
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    val notificationPermission = rememberPermission(
        Manifest.permission.POST_NOTIFICATIONS
    ) { status ->
        when (status) {
            PermissionStatus.Granted -> {
                notificationService.requestSendingNotification()
            }

            PermissionStatus.PermanentlyDenied -> showPermissionPermanentlyDeniedSnackbar = true
            else -> {}
        }
    }

    fun requestNotification() {
        if (notificationPermission.status.isGranted) {
            notificationService.requestSendingNotification()
        } else {
            notificationPermission.launchPermissionRequest()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_theme),
                modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Theme.entries.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (theme == state.theme),
                            onClick = { changeTheme(theme) },
                            role = Role.RadioButton
                        )
                        .padding(start = 155.dp),
                ) {
                    RadioButton(
                        selected = (theme == state.theme),
                        onClick = null
                    )
                    Text(
                        text = stringResource(
                            when (theme) {
                                Theme.Light -> R.string.theme_light
                                Theme.Dark -> R.string.theme_dark
                                Theme.System -> R.string.theme_system
                            }
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.app_notifications),
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = ::requestNotification,
                    enabled = !notificationPermission.status.isGranted
                ) {
                    if (notificationPermission.status.isGranted) {
                        Text(stringResource(R.string.enabled))
                    } else {
                        Text(stringResource(R.string.check_permission))
                    }
                }
            }
//            Spacer(modifier = Modifier.height(40.dp))
//            Text(
//                text = "App Language",
//                modifier = Modifier.padding(bottom = 10.dp),
//                style = MaterialTheme.typography.titleLarge
//            )
//            var expanded by remember { mutableStateOf(false) }
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                OutlinedTextField(
//                    value = "",
//                    onValueChange = {},
//                    enabled = false,
//                    trailingIcon = {
//                        IconButton(onClick = { expanded = !expanded }) {
//                            Icon(
//                                imageVector = Icons.Default.MoreVert,
//                                contentDescription = "More"
//                            )
//                        }
//                    }
//                )
//                DropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false },
//                ) {
//                    Language.entries.forEach { language ->
//                        DropdownMenuItem(
//                            onClick = { /* TODO */ },
//                            text = {
//                                Text(
//                                    text = stringResource(
//                                        when (language) {
//                                            Language.Italian -> R.string.language_italian
//                                            Language.English -> R.string.language_english
//                                        }
//                                    ),
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    modifier = Modifier.padding(start = 16.dp)
//                                )
//                            }
//                        )
//                    }
//                }
//            }
        }
    }
    if (showPermissionPermanentlyDeniedSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                ctx.getString(R.string.notifications_required),
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