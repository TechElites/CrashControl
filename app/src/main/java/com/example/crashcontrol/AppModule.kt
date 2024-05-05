package com.example.crashcontrol

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.crashcontrol.data.database.CrashControlDatabase
import com.example.crashcontrol.data.repositories.CrashesRepository
import com.example.crashcontrol.data.repositories.SettingsRepository
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.utils.AccelerometerService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            get(),
            CrashControlDatabase::class.java,
            "crash-control"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { AccelerometerService(get()) }

    single { SettingsRepository(get()) }

    single {
        CrashesRepository(
            get<CrashControlDatabase>().crashesDAO()
        )
    }

    viewModel { SettingsViewModel(get()) }

    viewModel { CrashesViewModel(get()) }
}