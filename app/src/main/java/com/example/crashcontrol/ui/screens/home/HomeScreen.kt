package com.example.crashcontrol.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.CrashesState

@Composable
fun HomeScreen(state: CrashesState, navController: NavHostController) {
    val crashList = listOf<Crash>(
        Crash(1, "PO Box 4166", "Crimson", "10/28/2023", 93.0, 38.0, 33.0, 2L, 13L),
        Crash(2, "Suite 93", "Yellow", "2/7/2024", 4.0, 13.0, 68.0, 4L, 21L),
        Crash(3, "PO Box 13455", "Orange", "9/19/2023", 79.0, 74.0, 99.0, 17L, 19L),
        Crash(4, "Room 503", "Yellow", "12/24/2023", 57.0, 75.0, 98.0, 17L, 7L),
        Crash(5, "Room 1417", "Khaki", "10/4/2023", 34.0, 6.0, 91.0, 10L, 4L),
        Crash(6, "PO Box 25447", "Green", "1/15/2024", 64.0, 63.0, 3.0, 1L, 18L),
        Crash(7, "Room 70", "Green", "11/19/2023", 44.0, 30.0, 95.0, 14L, 10L),
        Crash(8, "Apt 890", "Purple", "2/6/2024", 69.0, 91.0, 77.0, 22L, 0L),
        Crash(9, "Room 520", "Aquamarine", "12/18/2023", 97.0, 65.0, 68.0, 18L, 15L),
        Crash(10, "PO Box 63079", "Red", "10/3/2023", 80.0, 23.0, 12.0, 23L, 1L)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    navController.navigate(CrashControlRoute.AddCrash.route)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Crash")
            }
        }
    ) { contentPadding ->
        if (/*state.crashes.isNotEmpty()*/crashList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(/*state.crashes*/crashList) { item ->
                    CrashItem(
                        item,
                        onClick = {
                            navController.navigate(CrashControlRoute.CrashDetails.buildRoute(item.id.toString()))
                        }
                    )
                }
            }
        } else {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun NoItemsPlaceholder(padding: Modifier) {
    Text(text = "Non c'è nu cazz")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashItem(item: Crash, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(150.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.height.toString() + "m",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = item.date.toString(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = item.position.toString(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
