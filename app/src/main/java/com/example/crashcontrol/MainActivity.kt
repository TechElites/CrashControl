package com.example.crashcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.crashcontrol.data.models.Theme
import com.example.crashcontrol.ui.CrashControlNavGraph
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.AppBar
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.ui.theme.CrashControlTheme
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.LocationService
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var accelerometer: AccelerometerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accelerometer = get<AccelerometerService>()

        setContent {
            val settingsVm = koinViewModel<SettingsViewModel>()
            val settingsState by settingsVm.state.collectAsStateWithLifecycle()
            CrashControlTheme(
                darkTheme = when (settingsState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            CrashControlRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: CrashControlRoute.Home
                        }
                    }

                    Scaffold(
                        topBar = { AppBar(navController, currentRoute) }
                    ) { contentPadding ->
                        CrashControlNavGraph(
                            navController,
                            modifier = Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }
}