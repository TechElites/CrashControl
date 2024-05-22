package com.example.crashcontrol.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.crashcontrol.R
import com.example.crashcontrol.ui.CrashControlRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: CrashControlRoute,
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(currentRoute.title),
                fontWeight = FontWeight.Medium,
            )
        },
        navigationIcon = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Side Menu"
                )
            }
        },
        actions = {
            if (currentRoute.route == CrashControlRoute.Settings.route) {
                IconButton(onClick = { navController.navigate(CrashControlRoute.Debug.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bug_report_24),
                        contentDescription = "Debug"
                    )
                }
            }
            if (currentRoute.route == CrashControlRoute.Debug.route) {
                IconButton(onClick = { navController.navigate(CrashControlRoute.Settings.route) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
            if (currentRoute.route == CrashControlRoute.Home.route) {
                IconButton(onClick = { navController.navigate(CrashControlRoute.Favourites.route) }) {
                    Icon(Icons.Default.Favorite, "Favourites")
                }
                IconButton(onClick = { navController.navigate(CrashControlRoute.Graphs.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                        "Graphs"
                    )
                }
            }
            if (currentRoute.route == CrashControlRoute.Favourites.route || currentRoute.route == CrashControlRoute.Graphs.route) {
                IconButton(onClick = { navController.navigate(CrashControlRoute.Home.route) }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}