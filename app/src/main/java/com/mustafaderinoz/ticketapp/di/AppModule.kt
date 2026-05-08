package com.mustafaderinoz.ticketapp.di

import com.mustafaderinoz.ticketapp.ui.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AuthViewModel(get()) }
}