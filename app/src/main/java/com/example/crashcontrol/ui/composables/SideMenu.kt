package com.example.crashcontrol.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.crashcontrol.ui.CrashControlRoute

@Composable
fun SideMenu(
    navController: NavHostController,
    currentRoute: CrashControlRoute,
    onItemClick: () -> Unit
) {
    ModalDrawerSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "CrashControl",
                fontSize = 33.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
        Divider()
        NavigationDrawerItem(
            label = { Text(text = "Go back", fontSize = 20.sp) },
            selected = false,
            onClick = { navController.navigateUp(); onItemClick(); },
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            icon = {
                Icon(
                    Icons.Filled.ArrowBack, contentDescription = "Back",
                    modifier = Modifier.size(30.dp)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = "Home Page", fontSize = 20.sp) },
            selected = currentRoute == CrashControlRoute.Home,
            onClick = { navController.navigate(CrashControlRoute.Home.route); onItemClick(); },
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            icon = {
                Icon(
                    Icons.Filled.Home, contentDescription = "Home",
                    modifier = Modifier.size(30.dp)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = "Profile Page", fontSize = 20.sp) },
            selected = currentRoute == CrashControlRoute.Profile,
            onClick = { navController.navigate(CrashControlRoute.Profile.route); onItemClick(); },
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            icon = {
                Icon(
                    Icons.Filled.Person, contentDescription = "Profile",
                    modifier = Modifier.size(30.dp)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = "World Map", fontSize = 20.sp) },
            selected = currentRoute == CrashControlRoute.CrashesMap,
            onClick = { navController.navigate(CrashControlRoute.CrashesMap.route); onItemClick(); },
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            icon = {
                Icon(
                    Icons.Filled.Place, contentDescription = "Map",
                    modifier = Modifier.size(30.dp)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = "Achievements", fontSize = 20.sp) },
            selected = currentRoute == CrashControlRoute.Achievements,
            onClick = { navController.navigate(CrashControlRoute.Achievements.route); onItemClick(); },
            modifier = Modifier.padding(top = 10.dp, start = 10.dp),
            icon = {
                Icon(
                    Icons.Filled.Star, contentDescription = "Achievements",
                    modifier = Modifier.size(30.dp)
                )
            }
        )
    }
}