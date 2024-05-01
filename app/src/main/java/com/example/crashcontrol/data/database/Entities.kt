package com.example.crashcontrol.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Crash(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val position: String?,

    @ColumnInfo
    val exclamation: String,

    @ColumnInfo
    val date: String,

    @ColumnInfo
    val height: Double,

    @ColumnInfo
    val velocity: Double?,

    @ColumnInfo
    val acceleration: Double?,

    @ColumnInfo(name = "start_time")
    val startTime: Long?,

    @ColumnInfo(name = "end_time")
    val endTime: Long?
)