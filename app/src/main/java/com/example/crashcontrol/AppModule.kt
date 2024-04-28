package com.example.crashcontrol

import com.example.crashcontrol.utils.AccelerometerService
import org.koin.dsl.module

val appModule = module {
    single { AccelerometerService(get()) }
}