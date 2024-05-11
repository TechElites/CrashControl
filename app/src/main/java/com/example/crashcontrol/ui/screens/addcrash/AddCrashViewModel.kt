package com.example.crashcontrol.ui.screens.addcrash

import androidx.lifecycle.ViewModel
import com.example.crashcontrol.data.database.Crash
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddCrashState(
    val position: String? = "",
    val exclamation: String = "",
    val favourite: Boolean = false,
    val date: String = "",
    val time: String = "",
    val face: String = ""

    /*val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false*/
) {
    val canSubmit get() = date.isNotBlank() && date.isNotBlank() && date.isNotBlank()

    fun toCrash() = Crash(
        latitude = 0.0,
        longitude = 0.0,
        exclamation = exclamation,
        favourite = favourite,
        date = date,
        time = time,
        face = face
    )
}

interface AddCrashActions {
    fun setPosition(position: String)
    fun setExclamation(exclamation: String)
    fun setFavourite(favorite: Boolean)
    fun setDate(date: String)
    fun setTime(time: String)
    fun setFace(face: String)
    /*fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)*/
}

class AddCrashViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddCrashState())
    val state = _state.asStateFlow()

    val actions = object : AddCrashActions {
        override fun setPosition(position: String) =
            _state.update { it.copy(position = position) }

        override fun setExclamation(exclamation: String) =
            _state.update { it.copy(exclamation = exclamation) }

        override fun setFavourite(favorite: Boolean) =
            _state.update { it.copy(favourite = favorite) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setTime(time: String) =
            _state.update { it.copy(time = time) }

        override fun setFace(face: String) =
            _state.update { it.copy(face = face) }

        /*override fun setShowLocationDisabledAlert(show: Boolean) =
            _state.update { it.copy(showLocationDisabledAlert = show) }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) =
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) =
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }

        override fun setShowNoInternetConnectivitySnackbar(show: Boolean) =
            _state.update { it.copy(showNoInternetConnectivitySnackbar = show) }*/
    }
}
