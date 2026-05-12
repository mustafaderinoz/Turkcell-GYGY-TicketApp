package com.mustafaderinoz.ticketapp.di

import com.mustafaderinoz.ticketapp.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // viewModel
    viewModelOf(::LoginViewModel)
}