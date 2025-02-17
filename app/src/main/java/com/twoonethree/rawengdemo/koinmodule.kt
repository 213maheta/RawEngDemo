package com.twoonethree.rawengdemo

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SheduleRepository(get()) }
    viewModel { ScheduleViewModel(get()) }
}