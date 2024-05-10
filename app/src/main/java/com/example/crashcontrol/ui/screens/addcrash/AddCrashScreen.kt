package com.example.crashcontrol.ui.screens.addcrash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.CrashesState
import com.example.crashcontrol.ui.screens.addcrash.AddCrashActions

@Composable
fun AddCrashScreen(
    navController: NavHostController,
    state: AddCrashState,
    actions: AddCrashActions,
    onSubmit: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (!state.canSubmit) return@FloatingActionButton
                    onSubmit()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Add New Crash")
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Oh no!",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //Text(text = "Position: ", fontSize = 20.sp)
                OutlinedTextField(
                    value = state.position ?: "",
                    onValueChange = actions::setPosition,
                    label = { Text("Position") })
                IconButton(onClick = { /*TODO GoogleMaps intent*/ }) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        // Change the icon size
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //Text(text = "Date: ", fontSize = 20.sp)
                OutlinedTextField(
                    value = state.date,
                    onValueChange = actions::setDate,
                    label = { Text("Date") })
                IconButton(onClick = { /*TODO open date input*/ }) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Date",
                        // Change the icon size
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //Text(text = "Exclamation: ", fontSize = 20.sp)
                OutlinedTextField(
                    value = state.exclamation,
                    onValueChange = actions::setExclamation,
                    label = { Text("Exclamation") })
                Icon(
                    Icons.Filled.Face,
                    contentDescription = "Face",
                    // Change the icon size
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //Text(text = "Height: ", fontSize = 20.sp)
                OutlinedTextField(
                    value = state.height.toString(),
                    onValueChange = { actions.setHeight(it.toDouble()) },
                    label = { Text("Height") })
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Height",
                    // Change the icon size
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }

}