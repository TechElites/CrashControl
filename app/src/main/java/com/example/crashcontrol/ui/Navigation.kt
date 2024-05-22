package com.example.crashcontrol.ui

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.ui.screens.graphs.GraphsScreen
import com.example.crashcontrol.ui.screens.addcrash.AddCrashScreen
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsScreen
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsViewModel
import com.example.crashcontrol.ui.screens.crashesmap.CrashesMapScreen
import com.example.crashcontrol.ui.screens.debug.DebugScreen
import com.example.crashcontrol.ui.screens.home.HomeScreen
import com.example.crashcontrol.ui.screens.profile.ProfileScreen
import com.example.crashcontrol.ui.screens.profile.ProfileState
import com.example.crashcontrol.ui.screens.profile.ProfileViewModel
import com.example.crashcontrol.ui.screens.profile.signin.SignInScreen
import com.example.crashcontrol.ui.screens.profile.signin.SignInViewModel
import com.example.crashcontrol.ui.screens.profile.signup.SignUpScreen
import com.example.crashcontrol.ui.screens.profile.signup.SignUpViewModel
import com.example.crashcontrol.ui.screens.settings.SettingsScreen
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.NotificationService
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed class CrashControlRoute(
    val route: String,
    val title: Int,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : CrashControlRoute("crashes", R.string.crashes)

    data object CrashDetails : CrashControlRoute(
        "crashes/{crashId}",
        R.string.crash_details,
        listOf(navArgument("crashId") { type = NavType.StringType })
    ) {
        fun buildRoute(crashId: String) = "crashes/$crashId"
    }

    data object AddCrash : CrashControlRoute("crashes/add", R.string.add_crash)

    data object Settings : CrashControlRoute("settings", R.string.settings)

    data object CrashesMap : CrashControlRoute("map", R.string.crashes_map)

    data object Profile : CrashControlRoute("profile", R.string.profile)

    data object SignIn : CrashControlRoute("signin", R.string.sign_in)

    data object SignUp : CrashControlRoute("signup", R.string.sign_up)

    data object Debug : CrashControlRoute("debug", R.string.debug)

    data object Favourites : CrashControlRoute("favourites", R.string.favourites)

    data object Graphs : CrashControlRoute("graphs", R.string.graphs)

    companion object {
        val routes =
            setOf(
                Home,
                CrashDetails,
                AddCrash,
                Settings,
                CrashesMap,
                Profile,
                SignIn,
                SignUp,
                Debug,
                Favourites,
                Graphs
            )
    }
}

@Composable
fun CrashControlNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val crashesVm = koinViewModel<CrashesViewModel>()
    val crashesState by crashesVm.state.collectAsStateWithLifecycle()

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
                val crashDetailsVm = koinViewModel<CrashDetailsViewModel>()
                val state by crashDetailsVm.state.collectAsStateWithLifecycle()
                CrashDetailsScreen(
                    crash = crash,
                    actions = crashDetailsVm.actions,
                    onSubmit = { crashesVm.addCrash(state.toCrash()) },
                    onDelete = { crashesVm.deleteCrash(crash) },
                    navController = navController
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
                    navController = navController,
                    locationService = null
                )
            }
        }
        with(CrashControlRoute.Settings) {
            composable(route) {
                val settingsVm = koinViewModel<SettingsViewModel>()
                val state by settingsVm.state.collectAsStateWithLifecycle()
                val notificationService = koinInject<NotificationService>()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    SettingsScreen(state, settingsVm::changeTheme, notificationService)
                }
            }
        }
        with(CrashControlRoute.CrashesMap) {
            composable(route) {
                val fbDataSource = koinInject<FBDataSource>()
                val accountService = koinInject<AccountService>()
                CrashesMapScreen(
                    crashesState.crashes,
                    fbDataSource, accountService
                )
            }
        }
        with(CrashControlRoute.Profile) {
            composable(route) {
                val profileVm = koinViewModel<ProfileViewModel>()
                val state by profileVm.state.collectAsState(initial = ProfileState(true))
                ProfileScreen(
                    navController = navController,
                    state = state,
                    actions = profileVm.actions,
                    totalCrashes = crashesState.crashes.size
                )
            }
        }
        with(CrashControlRoute.SignIn) {
            composable(route) {
                val signInVm = koinViewModel<SignInViewModel>()
                val state by signInVm.state.collectAsStateWithLifecycle()
                SignInScreen(
                    navController = navController,
                    state = state,
                    actions = signInVm.actions
                )
            }
        }
        with(CrashControlRoute.SignUp) {
            composable(route) {
                val signUpVm = koinViewModel<SignUpViewModel>()
                val state by signUpVm.state.collectAsStateWithLifecycle()
                SignUpScreen(
                    navController = navController,
                    state = state,
                    actions = signUpVm.actions
                )
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
        with(CrashControlRoute.Graphs) {
            composable(route) {
                GraphsScreen(crashesState.crashes)
            }
        }
    }
}
