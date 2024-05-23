package com.example.crashcontrol.ui.screens.crashdetails

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.ui.composables.BasicAlertDialog
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun CrashDetailsScreen(
    crash: Crash,
    navController: NavController,
    actions: CrashDetailsActions,
    onSubmit: () -> Unit,
    onDelete: () -> Unit
) {
    val ctx = LocalContext.current
    val centerMapOn = GeoPoint(44.133331, 12.233333)
    val crashesMarker: MarkerState = rememberMarkerState(geoPoint = centerMapOn)
    val crashesIcons: Drawable = AppCompatResources.getDrawable(ctx, R.drawable.my_crash_pointer)!!
    if (crash.latitude != null && crash.longitude != null) {
        actions.setPosition(OSMPlace(0, crash.latitude, crash.longitude, ""))
        centerMapOn.latitude = crash.latitude
        centerMapOn.longitude = crash.longitude
    }

    actions.setId(crash.id)
    actions.setExclamation(crash.exclamation)
    actions.setDate(crash.date)
    actions.setTime(crash.time)
    actions.setFace(crash.face)

    val cameraState = rememberCameraState {
        geoPoint = centerMapOn
        zoom = 17.0
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    SideEffect {
        mapProperties = mapProperties.copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.MAPNIK).copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    val openFavouriteDialog = remember { mutableStateOf(false) }
    var openDeleteDialog = remember { mutableStateOf(false) }
    val fav = remember { mutableStateOf(crash.favourite) }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        fav.value = !fav.value
                        actions.setFavourite(fav.value)
                        openFavouriteDialog.value = true
                    }) {
                    Icon(
                        if (fav.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Toggle favourites",
                    )

                }
                Spacer(Modifier.size(8.dp))
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        openDeleteDialog.value = true
                    }) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete crash",
                    )
                }
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            if (crash.latitude == null) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = ctx.getString(R.string.no_crash_location),
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        lineHeight = 50.sp
                    )
                }
                Icon(
                    ImageVector.vectorResource(R.drawable.my_crash_pointer),
                    "Broken heart",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Card(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp)
                ) {
                    OpenStreetMap(
                        cameraState = cameraState, properties = mapProperties,
                        onMapClick = {},
                        onMapLongClick = {},
                    ) {
                        Marker(
                            state = crashesMarker,
                            icon = crashesIcons,
                            onClick = { false }
                        ) {}
                    }
                }
                Spacer(Modifier.size(8.dp))
                Button(onClick = {
                    val gmmIntentUri = Uri.parse("geo:${crash.latitude},${crash.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    ctx.startActivity(mapIntent)
                }) {
                    Text("Open in Maps")
                }
            }
            Spacer(Modifier.size(8.dp))
            Text(
                crash.exclamation,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Text(
                crash.date,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                crash.time,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.size(8.dp))
            Text(
                crash.face,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    when {
        openFavouriteDialog.value -> {
            BasicAlertDialog(
                onDismissRequest = {
                    fav.value = !fav.value
                    actions.setFavourite(fav.value)
                    openFavouriteDialog.value = false
                },
                onConfirmation = {
                    openFavouriteDialog.value = false
                    onSubmit()
                },
                dialogTitle = ctx.getString(R.string.add_favourite),
                dialogText = ctx.getString(R.string.add_favourite_text),
                confimationText = R.string.confirm,
                icon = Icons.Default.Favorite
            )
        }
        openDeleteDialog.value -> {
            BasicAlertDialog(
                onDismissRequest = {
                    openDeleteDialog.value = false
                },
                onConfirmation = {
                    actions.deleteFBCrash()
                    navController.navigateUp()
                    Handler(Looper.getMainLooper()).postDelayed({
                        onDelete()
                    }, 1500)
                },
                dialogTitle = ctx.getString(R.string.delete_crash),
                dialogText = ctx.getString(R.string.delete_crash_text),
                confimationText = R.string.delete,
                icon = Icons.Default.Delete
            )
        }
    }
}
