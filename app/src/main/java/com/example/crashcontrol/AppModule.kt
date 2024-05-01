package com.example.crashcontrol

import androidx.room.Room
import com.example.crashcontrol.data.database.CrashControlDatabase
import com.example.crashcontrol.data.repositories.CrashesRepository
import com.example.crashcontrol.ui.CrashesViewModel
import com.example.crashcontrol.utils.AccelerometerService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
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

    single {
        CrashesRepository(
            get<CrashControlDatabase>().crashesDAO()
        )
    }

    viewModel { CrashesViewModel(get()) }
}