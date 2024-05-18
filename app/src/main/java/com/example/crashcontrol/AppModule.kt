package com.example.crashcontrol

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.crashcontrol.data.database.CrashControlDatabase
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.repositories.CrashesRepository
import com.example.crashcontrol.data.repositories.SettingsRepository
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.ui.screens.addcrash.AddCrashViewModel
import com.example.crashcontrol.ui.screens.crashdetails.CrashDetailsViewModel
import com.example.crashcontrol.ui.screens.profile.ProfileViewModel
import com.example.crashcontrol.ui.screens.profile.signin.SignInViewModel
import com.example.crashcontrol.ui.screens.profile.signup.SignUpViewModel
import com.example.crashcontrol.ui.screens.settings.SettingsViewModel
import com.example.crashcontrol.utils.AccelerometerService
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.LocationService
import com.example.crashcontrol.utils.NotificationService
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
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

    single { FirebaseApp.initializeApp(androidContext()) }

    single { AccountService(FirebaseAuth.getInstance()) }

    single { FBDataSource(FirebaseFirestore.getInstance()) }

    single { AccelerometerService(get()) }

    single { NotificationService(get()) }

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

    viewModel { ProfileViewModel(get<AccountService>(), get<FBDataSource>()) }

    viewModel { SignUpViewModel(get<AccountService>(), get<FBDataSource>()) }

    viewModel { SignInViewModel(get<AccountService>()) }

    viewModel { AddCrashViewModel() }

    viewModel { SettingsViewModel(get()) }

    viewModel { CrashesViewModel(get()) }

    viewModel { CrashDetailsViewModel() }
}

