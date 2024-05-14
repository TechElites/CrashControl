package com.example.crashcontrol.ui.screens.crashesmap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.crashcontrol.data.database.Crash
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

@Composable
fun CrashesMapScreen(latestCrash: Crash?) {
    val centerMapOn = GeoPoint(44.133331, 12.233333)
    val crashes: MutableSet<MarkerState> = mutableSetOf()
    if (latestCrash?.latitude != null && latestCrash.longitude != null) {
        centerMapOn.latitude = latestCrash.latitude
        centerMapOn.longitude = latestCrash.longitude
        crashes.add(
            rememberMarkerState(
                geoPoint = centerMapOn
            )
        )
    }
    val cameraState = rememberCameraState {
        geoPoint = centerMapOn
        zoom = 15.0
    }

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        crashes.forEach { marker ->
            Marker(
                state = marker
            )
        }
    }
}