/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.crashcontrol.ui.screens.profile.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.crashcontrol.R
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.BasicButton
import com.example.crashcontrol.ui.composables.BasicField
import com.example.crashcontrol.ui.composables.EmailField
import com.example.crashcontrol.ui.composables.IconButtonField
import com.example.crashcontrol.ui.composables.PasswordField
import com.example.crashcontrol.ui.composables.RepeatPasswordField
import com.example.crashcontrol.utils.rememberCameraLauncher
import com.example.crashcontrol.utils.rememberPermission
import com.example.crashcontrol.utils.saveImageToStorage
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController, state: SignUpState, actions: SignUpActions
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var snackBarMessage by remember { mutableIntStateOf(R.string.email_error) }
    var showWrongInputAlert by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    var pictureTaken by remember { mutableStateOf(false) }

    var cameraLauncher = rememberCameraLauncher { imageUri ->
        saveImageToStorage(imageUri, ctx.applicationContext.contentResolver)
    }

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() = if (cameraPermission.status.isGranted) {
        cameraLauncher.captureImage()
    } else {
        cameraPermission.launchPermissionRequest()
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    // Do something with the selected image URI
                    selectedImageUri = it
                    actions.setPicture(it.toString())
                }
            }
        }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
            Row {


                Button(
                    onClick = {
                        cameraLauncher.capturedImageUri = Uri.EMPTY
                        takePicture()
                    }, modifier = Modifier.width(170.dp), enabled = !pictureTaken
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
                        contentDescription = "Camera"
                    )
                    Text(text = "Take picture")
                }
                Spacer(Modifier.size(16.dp))
                Button(
                    onClick = {
                        cameraLauncher.capturedImageUri = Uri.EMPTY
                        val intent = Intent(Intent.ACTION_PICK).apply {
                            type = "image/*"
                        }
                        pickMedia.launch(intent)
                    }, modifier = Modifier.width(170.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_photo_library_24),
                        contentDescription = "Gallery"
                    )
                    Text(text = "Gallery")
                }
            }
            if (selectedImageUri != null) {
                Card(
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(80.dp),
                ) {
                    AsyncImage(
                        ImageRequest.Builder(ctx).data(selectedImageUri).crossfade(true).build(),
                        "Captured image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(80.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Default image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            if (cameraLauncher.capturedImageUri.path?.isNotEmpty() == true) {
                pictureTaken = true
                selectedImageUri = cameraLauncher.capturedImageUri
                actions.setPicture(cameraLauncher.capturedImageUri.toString())
            }
            EmailField(state.email, actions::setEmail, fieldModifier)
            BasicField(R.string.username, state.username, actions::setUsername, fieldModifier)
            BasicField(R.string.name, state.name, actions::setName, fieldModifier)
            BasicField(R.string.surname, state.surname, actions::setSurname, fieldModifier)
            IconButtonField(
                text = R.string.birthday,
                icon = Icons.Default.DateRange,
                value = state.birthday,
                onNewValue = actions::setBirthday,
                onButtonClicked = { showDatePicker = true },
                modifier = fieldModifier
            )
            PasswordField(state.password, actions::setPassword, fieldModifier)
            RepeatPasswordField(state.repeatPassword, actions::setRepeatedPassword, fieldModifier)/*IconButton(onClick = { takePicture() }) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Camera")
            }*/

            BasicButton(
                R.string.create_account,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
            ) {
                val objection = state.canSubmit()
                if (objection != null) {
                    snackBarMessage = objection
                    showWrongInputAlert = true
                } else {
                    actions.signUp()
                    navController.navigate(CrashControlRoute.Profile.route)
                }
            }
        }
    }

    if (showWrongInputAlert) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                getString(ctx, snackBarMessage), duration = SnackbarDuration.Long
            )
            showWrongInputAlert = false
        }
    }

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = {
            TextButton(onClick = {
                val selectedDate = Calendar.getInstance().apply {
                    timeInMillis =
                        if (datePickerState.selectedDateMillis == null || datePickerState.selectedDateMillis == 0L) {
                            System.currentTimeMillis()
                        } else {
                            datePickerState.selectedDateMillis!!
                        }
                }
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate.timeInMillis
                actions.setBirthday(formatter.format(calendar.time))
                showDatePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = {
                showDatePicker = false
            }) { Text("Cancel") }
        }) {
            DatePicker(state = datePickerState)
        }
    }
}