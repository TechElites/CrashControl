package com.example.crashcontrol.ui.screens.crashdetails

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.ui.CrashControlRoute
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun CrashDetailsScreen(
    crash: Crash,
    state: CrashDetailsState,
    navController: NavController,
    actions: CrashDetailsActions,
    onSubmit: () -> Unit,
    onDelete: () -> Unit
) {
    val ctx = LocalContext.current
    actions.setId(crash.id)
    actions.setPosition(OSMPlace(0, crash.latitude!!, crash.longitude!!, ""))
    actions.setExclamation(crash.exclamation)
    //actions.setFavourite(crash.favourite)
    actions.setDate(crash.date)
    actions.setTime(crash.time)
    actions.setFace(crash.face)

    val centerMapOn = GeoPoint(44.133331, 12.233333)
    val crashesMarker: MarkerState = rememberMarkerState(geoPoint = centerMapOn)
    val crashesIcons: Drawable = AppCompatResources.getDrawable(ctx, R.drawable.my_crash_pointer)!!
    centerMapOn.latitude = crash.latitude
    centerMapOn.longitude = crash.longitude

    val cameraState = rememberCameraState {
        geoPoint = centerMapOn
        zoom = 17.0
    }

    // define properties with remember with default value
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    // setup mapProperties in side effect
    SideEffect {
        mapProperties = mapProperties.copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.MAPNIK).copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        //onSubmit()
                        actions.setFavourite(!state.favourite)
                        onSubmit()
                    }) {
                    Icon(
                        Icons.Outlined.Favorite,
                        "Remove from favourites",
                        tint = if (state.favourite) {
                            Color.Black
                        } else {
                            Color.White
                        }
                    )

                }
                Spacer(Modifier.size(8.dp))
                FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
                    navController.navigateUp()
                    Handler(Looper.getMainLooper()).postDelayed({
                        onDelete()
                    }, 1500)

                }) {
                    Icon(
                        Icons.Outlined.Delete,
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
                //.padding(12.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .height(300.dp)
                    .width(300.dp)
            ) {
                OpenStreetMap(
                    //modifier = Modifier.fillMaxSize(),
                    cameraState = cameraState, properties = mapProperties // add properties,
                ) {
                    Marker(
                        state = crashesMarker,
                        icon = crashesIcons,
                        title = crash.exclamation,
                        snippet = crash.date
                    ) {
                        Column(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(7.dp)
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = it.title)
                            Text(text = it.snippet, fontSize = 10.sp)
                        }
                    }

                }
            }
            Spacer(Modifier.size(8.dp))
            Button(onClick = { /*intent to open map in lat long*/
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
                fontSize = 30.sp
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
