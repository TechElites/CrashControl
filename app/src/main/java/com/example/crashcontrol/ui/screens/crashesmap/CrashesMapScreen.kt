package com.example.crashcontrol.ui.screens.crashesmap

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crashcontrol.R.drawable.my_crash_pointer
import com.example.crashcontrol.data.database.Crash
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

/**
 * documentation:
 * https://utsmannn.github.io/osm-android-compose/usage/
 */
@Composable
fun CrashesMapScreen(latestCrash: Crash?) {
    val ctx = LocalContext.current
    val centerMapOn = GeoPoint(44.133331, 12.233333)
    val crashes: MutableList<Crash> = mutableListOf()
    val crashesMarker: MutableList<MarkerState> = mutableListOf()
    val crashesIcons: MutableList<Drawable> = mutableListOf()
    if (latestCrash?.latitude != null && latestCrash.longitude != null) {
        centerMapOn.latitude = latestCrash.latitude
        centerMapOn.longitude = latestCrash.longitude
        crashes.add(latestCrash)
        crashesMarker.add(rememberMarkerState(geoPoint = centerMapOn))
        crashesIcons.add(AppCompatResources.getDrawable(ctx, my_crash_pointer)!!)
    }
    val cameraState = rememberCameraState {
        geoPoint = centerMapOn
        zoom = 17.0
    }

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        for (i in crashes.indices) {
            Marker(
                state = crashesMarker[i],
                icon = crashesIcons[i],
                title = crashes[i].exclamation,
                snippet = crashes[i].date
            ) {
                Column(
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(7.dp)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = it.title)
                    Text(text = it.snippet, fontSize = 10.sp)
                }
            }
        }
    }
}