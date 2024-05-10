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
    val impactTime: String? = "",
    val duration: Long? = 0,
    val height: Double = 0.0,
    val impactAccelleration: Float = 0f,

    /*val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false*/
) {
    val canSubmit get() = date.isNotBlank() && date.isNotBlank() && date.isNotBlank()

    fun toCrash() = Crash(
        position = position,
        exclamation = exclamation,
        favourite = favourite,
        date = date,
        impactTime = impactTime,
        duration = duration,
        height = height,
        impactAccelleration = impactAccelleration,
    )
}

interface AddCrashActions {
    fun setPosition(position: String?)
    fun setExclamation(exclamation: String)
    fun setFavourite(favorite: Boolean)
    fun setDate(date: String)
    fun setImpactTime(impactTime: String?)
    fun setDuration(duration: Long?)
    fun setHeight(height: Double)
    fun setImpactAccelleration(impactAccelleration: Float)
    /*fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)*/
}

class AddCrashViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddCrashState())
    val state = _state.asStateFlow()

    val actions = object : AddCrashActions {
        override fun setPosition(position: String?) =
            _state.update { it.copy(position = position) }

        override fun setExclamation(exclamation: String) =
            _state.update { it.copy(exclamation = exclamation) }

        override fun setFavourite(favorite: Boolean) =
            _state.update { it.copy(favourite = favorite) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setImpactTime(impactTime: String?) =
            _state.update { it.copy(impactTime = impactTime) }

        override fun setDuration(duration: Long?) =
            _state.update { it.copy(duration = duration) }

        override fun setHeight(height: Double) =
            _state.update { it.copy(height = height) }

        override fun setImpactAccelleration(impactAccelleration: Float) =
            _state.update { it.copy(impactAccelleration = impactAccelleration) }

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
