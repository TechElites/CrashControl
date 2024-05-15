package com.example.crashcontrol.ui.screens.crashdetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.ui.screens.addcrash.AddCrashActions
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CrashDetailsScreen(
    crash: Crash,
    state: StateFlow<CrashDetailsState>,
    actions: AddCrashActions,
    onSubmit: () -> Unit
) {
    val ctx = LocalContext.current

    /*fun shareDetails() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, crash.exclamation)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share travel")
        if (shareIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(shareIntent)
        }
    }*/

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    actions.setFavourite(!state.value.favourite)
                    onSubmit()
                }
            ) {
                Icon(Icons.Outlined.Favorite, "Add to favourites")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Button(
                onClick = { /*intent to open map in lat long*/
                    val gmmIntentUri = Uri.parse("geo:${crash.latitude},${crash.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    ctx.startActivity(mapIntent)
                }) {
                Text("Open in Maps")
            }
            Spacer(Modifier.size(8.dp))
            Text(
                crash.exclamation,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge,
                //modifier = Modifier.size(100.dp)
            )
            Text(
                crash.date + ", " + crash.time,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall,
                //modifier = Modifier.fillMaxSize(),

            )
            Spacer(Modifier.size(8.dp))
            Text(
                crash.face,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
