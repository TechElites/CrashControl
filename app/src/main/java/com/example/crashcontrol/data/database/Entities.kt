package com.example.crashcontrol.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Crash(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val latitude: Double?,

    @ColumnInfo
    val longitude: Double?,

    @ColumnInfo
    val exclamation: String,

    @ColumnInfo
    val favourite: Boolean = false,

    @ColumnInfo
    val date: Long,

    @ColumnInfo
    val time: Long,

    @ColumnInfo
    val face: String
)