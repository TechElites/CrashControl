package com.example.crashcontrol.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.crashcontrol.R
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.CrashesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: CrashesState, navController: NavHostController, favourites: Boolean) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val selectedOption = remember { mutableStateOf("All") }
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_filter_alt_24),
                        contentDescription = "Filter",
                        //modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.size(8.dp))
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        navController.navigate(CrashControlRoute.AddCrash.route)
                    }
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add New Crash")
                }
            }
        }
    ) { contentPadding ->
        if (state.crashes.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                if (favourites) {
                    //Toast.makeText(ctx, state.crashes.filter { crash: Crash -> crash.favourite }.toString(), Toast.LENGTH_SHORT).show()
                    items(state.crashes.filter { crash: Crash -> crash.favourite }) { item ->
                        CrashItem(
                            item,
                            onClick = {
                                navController.navigate(
                                    CrashControlRoute.CrashDetails.buildRoute(
                                        item.id.toString()
                                    )
                                )
                            }
                        )
                    }
                } else {
                    items(if (selectedOption.value == "All") state.crashes else state.crashes.filter { crash: Crash -> crash.face == selectedOption.value }) { item ->
                        CrashItem(
                            item,
                            onClick = {
                                navController.navigate(
                                    CrashControlRoute.CrashDetails.buildRoute(
                                        item.id.toString()
                                    )
                                )
                            }
                        )
                    }
                }
            }
        } else {
            NoItemsPlaceholder()
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
            ) {
                Text(
                    text = "Filter crashes by landing face",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                for (option in listOf("Left", "Right", "Up", "Down", "Front", "Back", "All")) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption.value == option,
                            onClick = { selectedOption.value = option }
                        )
                        Text(option)
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun NoItemsPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.Clear, "Clear icon",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(48.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            "No items",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Tap the + button to add a new crash.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.exclamation,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = item.date,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = item.face,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
