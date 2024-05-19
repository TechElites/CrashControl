package com.example.crashcontrol.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    androidx.compose.material3.AlertDialog(
        icon = { Icon(icon, contentDescription = "Icon") },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) { Text("Dismiss") }
        })
}