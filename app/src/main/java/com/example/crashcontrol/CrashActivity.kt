package com.example.crashcontrol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.ui.screens.addcrash.AddCrashActions
import com.example.crashcontrol.ui.screens.addcrash.AddCrashState
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.theme.CrashControlTheme
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import kotlin.math.pow

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrashControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    val data: Bundle? = intent.extras
                    val date = data?.getString("date")
                    val time = data?.getString("time")
                    val duration = data?.getLong("duration")
                    val accelleration = data?.getFloat("acceleration")
                    val crashesVm = koinViewModel<CrashesViewModel>()
                    val addCrashVm = koinViewModel<AddCrashViewModel>()
                    val state by addCrashVm.state.collectAsStateWithLifecycle()
                    if (date != null && time != null && duration != null && accelleration != null) {
                        addCrashVm.actions.setDate(date)
                        addCrashVm.actions.setImpactTime(time)
                        addCrashVm.actions.setDuration(duration)
                        addCrashVm.actions.setImpactAccelleration(accelleration)
                        val flightTime = duration.toDouble() / 1000
                        var height: Double = 0.5 * 9.81 * flightTime.pow(2.0)
                        val precision = DecimalFormat("0.0000")
                        height = precision.format(height).toDouble()
                        addCrashVm.actions.setHeight(height)
                    }
                    AddExclamantion(
                        state = state,
                        actions = addCrashVm.actions,
                        onSubmit = { crashesVm.addCrash(state.toCrash()) },
                    )
                }
            }
        }
    }
}

@Composable
fun AddExclamantion(
    state: AddCrashState,
    actions: AddCrashActions,
    onSubmit: () -> Unit
) {
    val ctx = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (!state.canSubmit) return@FloatingActionButton
                    onSubmit()
                    val intent = Intent(ctx, MainActivity::class.java)
                    ctx.startActivity(intent)
                }
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Add New Crash")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How you feeling champ?",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.exclamation,
                    onValueChange = actions::setExclamation,
                    label = { Text("Exclamation") })
                Icon(
                    Icons.Filled.Face,
                    contentDescription = "Face",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") })
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Date",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.impactTime.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") })
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Time",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.duration.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Duration") })
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Duration",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.impactAccelleration.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Impact accelleration") })
                Icon(
                    Icons.Filled.ExitToApp,
                    contentDescription = "Accelleration",
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.height.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Height") })
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Height",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}