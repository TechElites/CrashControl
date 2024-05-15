package com.example.crashcontrol

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.crashcontrol.data.database.CrashControlDatabase
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.repositories.CrashesRepository
import com.example.crashcontrol.data.repositories.SettingsRepository
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsViewModel
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.LocationService
import com.example.crashcontrol.utils.NotificationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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

    single { LocationService(get()) }

    single { SettingsRepository(get()) }

    single {
        CrashesRepository(
            get<CrashControlDatabase>().crashesDAO()
        )
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    single { OSMDataSource(get()) }

    viewModel { AddCrashViewModel() }

    viewModel { SettingsViewModel(get()) }

    viewModel { CrashesViewModel(get()) }

    viewModel { CrashDetailsViewModel() }
}