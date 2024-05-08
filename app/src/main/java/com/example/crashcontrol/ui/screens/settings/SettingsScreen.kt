package com.example.crashcontrol.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.crashcontrol.R
import com.example.crashcontrol.data.models.Language
import com.example.crashcontrol.data.models.Theme
import com.example.crashcontrol.utils.LocationService

@Composable
fun SettingsScreen(
    state: SettingsState,
    changeTheme: (Theme) -> Unit,
    location: LocationService
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App Theme",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Theme.entries.forEach { theme ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (theme == state.theme),
                        onClick = { changeTheme(theme) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = (theme == state.theme),
                    onClick = null
                )
                Text(
                    text = stringResource(
                        when (theme) {
                            Theme.Light -> R.string.theme_light
                            Theme.Dark -> R.string.theme_dark
                            Theme.System -> R.string.theme_system
                        }
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Place your crashes on the map",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Switch(
                checked = true,
                onCheckedChange = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "App Language",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        var expanded by remember { mutableStateOf(false) }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Language.entries.forEach { language ->
                    DropdownMenuItem(
                        onClick = { /* TODO */ },
                        text = {
                            Text(
                                text = stringResource(
                                    when (language) {
                                        Language.Italian -> R.string.language_italian
                                        Language.English -> R.string.language_english
                                    }
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}