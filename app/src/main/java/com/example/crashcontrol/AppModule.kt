package com.example.crashcontrol

import com.example.crashcontrol.ui.screens.debug.DebugViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { DebugViewModel() }
}