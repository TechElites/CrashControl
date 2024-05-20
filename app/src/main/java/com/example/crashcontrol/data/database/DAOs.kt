package com.example.crashcontrol.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CrashesDAO {
    @Query("SELECT * FROM crash ORDER BY date, time DESC")
    fun getAll(): Flow<List<Crash>>

    @Upsert
    suspend fun upsert(crash: Crash)

    @Delete
    suspend fun delete(crash: Crash)
}