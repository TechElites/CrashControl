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
    val favourite: Boolean = false,

    @ColumnInfo
    val date: String,

    @ColumnInfo(name = "impact_time")
    val impactTime: String?,

    @ColumnInfo
    val duration: Long?,

    @ColumnInfo
    val height: Double,

    @ColumnInfo(name = "impact_accelleration")
    val impactAccelleration: Double
)