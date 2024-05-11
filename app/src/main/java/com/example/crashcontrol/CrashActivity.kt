package com.example.crashcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.ui.screens.addcrash.AddAutomaticCrashScreen
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.theme.CrashControlTheme
import com.example.crashcontrol.utils.LocationService
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class CrashActivity : ComponentActivity() {
    private lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = get<LocationService>()

        setContent {
            CrashControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    val data: Bundle? = intent.extras
                    val date = data?.getString("date")
                    val time = data?.getString("time")
                    val face = data?.getString("face")
                    val crashesVm = koinViewModel<CrashesViewModel>()
                    val addCrashVm = koinViewModel<AddCrashViewModel>()
                    val state by addCrashVm.state.collectAsStateWithLifecycle()
                    if (date != null && time != null && face != null) {
                        addCrashVm.actions.setDate(date)
                        addCrashVm.actions.setTime(time)
                        addCrashVm.actions.setFace(face)
                    }
                    AddAutomaticCrashScreen(
                        state = state,
                        actions = addCrashVm.actions,
                        locationService = locationService,
                        onSubmit = { crashesVm.addCrash(state.toCrash()) },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationService.resumeLocationRequest()
    }

    override fun onPause() {
        super.onPause()
        locationService.pauseLocationRequest()
    }
}