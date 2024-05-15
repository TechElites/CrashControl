package com.example.crashcontrol.ui.screens.crashdetails

import androidx.lifecycle.ViewModel
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.OSMPlace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CrashDetailsState(
    val position: OSMPlace? = null,
    val exclamation: String = "",
    val favourite: Boolean = false,
    val date: String = "",
    val time: String = "",
    val face: String = ""

) {
    fun toCrash() = Crash(
        latitude = position?.latitude ?: 0.0,
        longitude = position?.longitude ?: 0.0,
        exclamation = exclamation,
        favourite = favourite,
        date = date,
        time = time,
        face = face
    )
}

interface CrashDetailsActions {
    fun setPosition(position: OSMPlace)
    fun setExclamation(exclamation: String)
    fun setFavourite(favorite: Boolean)
    fun setDate(date: String)
    fun setTime(time: String)
    fun setFace(face: String)

}

class CrashDetailsViewModel : ViewModel() {
    private val _state = MutableStateFlow(CrashDetailsState())
    val state = _state.asStateFlow()

    val actions = object : CrashDetailsActions {
        override fun setPosition(position: OSMPlace) =
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

    }
}
