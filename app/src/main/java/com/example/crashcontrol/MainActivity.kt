package com.example.crashcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.crashcontrol.data.models.Theme
import com.example.crashcontrol.ui.CrashControlNavGraph
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.AppBar
import com.example.crashcontrol.ui.composables.SideMenu
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.ui.theme.CrashControlTheme
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.NotificationService
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var accelerometer: AccelerometerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accelerometer = get<AccelerometerService>()
        accelerometer.startService(get<NotificationService>())

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
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            CrashControlRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: CrashControlRoute.Home
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            SideMenu(navController, currentRoute) {
                                scope.launch {
                                    drawerState.apply {
                                        close()
                                    }
                                }
                            }
                        },
                    ) {
                        Scaffold(
                            topBar = {
                                AppBar(
                                    navController, currentRoute
                                ) {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }
                            }
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

    override fun onDestroy() {
        super.onDestroy()
        accelerometer.StopService()
    }
}