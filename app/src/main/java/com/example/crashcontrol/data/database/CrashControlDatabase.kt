package com.example.crashcontrol.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [Crash::class], version = 5)
abstract class CrashControlDatabase : RoomDatabase() {
    abstract fun crashesDAO(): CrashesDAO
}