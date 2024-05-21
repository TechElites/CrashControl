package com.example.crashcontrol.ui.screens.crashesmap

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.FBCrash
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.utils.AccountService
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

/**
 * documentation:
 * https://utsmannn.github.io/osm-android-compose/usage/
 */
@Composable
fun CrashesMapScreen(
    myCrashes: List<Crash>,
    fbDataSource: FBDataSource,
    accountService: AccountService
) {
    val ctx = LocalContext.current
    val localCrashes = myCrashes.filter { it.latitude != null }
        .map {
            FBCrash(
                ctx.getString(R.string.you),
                it.latitude!!,
                it.longitude!!,
                it.exclamation,
                it.date,
                it.time,
                it.face
            )
        }
    val centerMapOn = remember { mutableStateOf(GeoPoint(44.133331, 12.233333)) }
    val crashes = remember { mutableStateOf(listOf<FBCrash>()) }
    val crashesMarker = remember { mutableStateOf(listOf<MarkerState>()) }
    val crashesIcons = remember { mutableStateOf(listOf<Drawable>()) }
    val isLoading = remember { mutableStateOf(true) }

    val coroutineScopeRequest = rememberCoroutineScope()
    fun requestCrashes() = coroutineScopeRequest.launch {
        if (accountService.hasUser) {
            val myRemoteCrash = fbDataSource.loadCrash(accountService.currentUserId)
            val remoteCrashes = fbDataSource.loadCrashes().filter { it != myRemoteCrash }
            crashes.value = localCrashes + remoteCrashes.toList()
            isLoading.value = false
        } else {
            crashes.value = localCrashes
            isLoading.value = false
        }
    }

    LaunchedEffect(Unit) {
        requestCrashes()
    }

    if (isLoading.value) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (crashes.value.isNotEmpty()) {
        val latestCrash = crashes.value[0]
        centerMapOn.value = GeoPoint(latestCrash.latitude, latestCrash.longitude)

        crashesMarker.value = crashes.value.map { crash ->
            rememberMarkerState(
                geoPoint = GeoPoint(crash.latitude, crash.longitude)
            )
        }

        crashesIcons.value = crashes.value.map {
            if (localCrashes.contains(it))
                AppCompatResources.getDrawable(ctx, R.drawable.my_crash_pointer)!!
            else
                AppCompatResources.getDrawable(ctx, R.drawable.crash_pointer)!!
        }

        val cameraState = rememberCameraState {
            geoPoint = centerMapOn.value
            zoom = 17.0
        }

        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            onMapClick = {},
            onMapLongClick = {},
        ) {
            for (i in crashes.value.indices) {
                Marker(
                    state = crashesMarker.value[i],
                    icon = crashesIcons.value[i],
                    title = crashes.value[i].exclamation,
                    snippet = crashes.value[i].date,
                    onClick = { false }
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
                        Text(text = crashes.value[i].username, fontSize = 10.sp)
                    }
                }
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_map_crashes),
                    modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}