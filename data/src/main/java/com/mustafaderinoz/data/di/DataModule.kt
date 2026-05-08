package com.mustafaderinoz.data.di

import com.mustafaderinoz.core.domain.AuthRepository
import com.mustafaderinoz.data.remote.AuthApi
import com.mustafaderinoz.data.repository.AuthRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {
    // 1. Retrofit Sağlayıcısı
    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            // /docs/ kısmı kaldırıldı. Ana API dizini baseUrl olarak verildi.
            .baseUrl("https://tickets-api.halitkalayci.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    // 2. AuthApi Sağlayıcısı
    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    // 3. AuthRepository Sağlayıcısı
    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }
}