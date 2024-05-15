package com.example.crashcontrol.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.crashcontrol.ui.screens.addcrash.AddCrashScreen
import com.example.crashcontrol.ui.screens.debug.DebugScreen
import com.example.crashcontrol.ui.screens.home.HomeScreen
import com.example.crashcontrol.ui.screens.settings.SettingsScreen
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.LocationService
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsViewModel
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed class CrashControlRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : CrashControlRoute("crashes", "Crashes")

    data object CrashDetails : CrashControlRoute(
        "crashes/{crashId}",
        "Crash Details",
        listOf(navArgument("crashId") { type = NavType.StringType })
    ) {
        fun buildRoute(crashId: String) = "crashes/$crashId"
    }

    data object AddCrash : CrashControlRoute("crashes/add", "Add Crash")

    data object Settings : CrashControlRoute("settings", "Settings")

    data object CrashesMap : CrashControlRoute("map", "Crashes Map")

    data object Profile : CrashControlRoute("profile", "Profile")

    data object Achievements : CrashControlRoute("achievements", "Achievements")

    data object Debug : CrashControlRoute("debug", "Debug")

    data object Favourites : CrashControlRoute("favourites", "Favourites")

    companion object {
        val routes =
            setOf(Home, CrashDetails, AddCrash, Settings, CrashesMap, Profile, Achievements, Debug, Favourites)
    }
}

@Composable
fun CrashControlNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val crashesVm = koinViewModel<CrashesViewModel>()
    val crashesState by crashesVm.state.collectAsStateWithLifecycle()
    val crasheDetailsVm = koinViewModel<CrashDetailsViewModel>()

    NavHost(
        navController = navController,
        startDestination = CrashControlRoute.Home.route,
        modifier = modifier
    ) {
        with(CrashControlRoute.Home) {
            composable(route) {
                HomeScreen(crashesState, navController, false)
            }
        }
        with(CrashControlRoute.CrashDetails) {
            composable(route, arguments) { backStackEntry ->
                val crash = requireNotNull(crashesState.crashes.find {
                    it.id == backStackEntry.arguments?.getString("crashId")?.toInt()
                })
                CrashDetailsScreen(
                    crash = crash,
                    state = koinViewModel<CrashDetailsViewModel>().state,
                    actions = koinViewModel<AddCrashViewModel>().actions,
                    onSubmit = { /*crasheDetailsVm.addCrash(crasheDetailsVm.state.value.toCrash())*/}
                )
            }
        }
        with(CrashControlRoute.AddCrash) {
            composable(route) {
                val addCrashVm = koinViewModel<AddCrashViewModel>()
                val state by addCrashVm.state.collectAsStateWithLifecycle()
                AddCrashScreen(
                    state = state,
                    actions = addCrashVm.actions,
                    onSubmit = { crashesVm.addCrash(state.toCrash()) },
                    navController = navController
                )
            }
        }
        with(CrashControlRoute.Settings) {
            composable(route) {
                val settingsVm = koinViewModel<SettingsViewModel>()
                val state by settingsVm.state.collectAsStateWithLifecycle()
                val location = koinInject<LocationService>()
                SettingsScreen(state, settingsVm::changeTheme, location)
            }
        }
        with(CrashControlRoute.CrashesMap) {
            composable(route) {
//                CrashesMapScreen(placesState.crashes, navController)
            }
        }
        with(CrashControlRoute.Profile) {
            composable(route) {
//                ProfileScreen()
            }
        }
        with(CrashControlRoute.Achievements) {
            composable(route) {
//                AchievementsScreen()
            }
        }
        with(CrashControlRoute.Debug) {
            composable(route) {
                val accelerometer = koinInject<AccelerometerService>()
                DebugScreen(accelerometer)
            }
        }
        with(CrashControlRoute.Favourites) {
            composable(route) {
                HomeScreen(crashesState, navController, true)
            }
        }
    }
}
