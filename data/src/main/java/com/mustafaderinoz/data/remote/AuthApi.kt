package com.mustafaderinoz.data.remote

import com.mustafaderinoz.data.dto.auth.CredentialsDto
import com.mustafaderinoz.data.dto.auth.RefreshRequestDto
import com.mustafaderinoz.data.dto.auth.TokenPairDto
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit ile spesifik bir API'e istek atan kodu yazdık.
// retrofitin bir gereksinimidir.
//atacağım isteğin nereye gideceğini belirleriz
interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body body: CredentialsDto): TokenPairDto

    @POST("/auth/register")
    suspend fun register(@Body body: CredentialsDto): TokenPairDto

    @POST("/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): TokenPairDto
}