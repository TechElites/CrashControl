package com.example.crashcontrol.data.repositories

import android.content.ContentResolver
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.database.CrashesDAO
import kotlinx.coroutines.flow.Flow

class CrashesRepository(
    private val crashesDAO: CrashesDAO
) {
    val crashes: Flow<List<Crash>> = crashesDAO.getAll()

    suspend fun upsert(place: Crash) = crashesDAO.upsert(place)

    suspend fun delete(place: Crash) = crashesDAO.delete(place)
}