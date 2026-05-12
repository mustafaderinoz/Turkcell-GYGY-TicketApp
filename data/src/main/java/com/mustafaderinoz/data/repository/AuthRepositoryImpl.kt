package com.mustafaderinoz.data.repository

import com.mustafaderinoz.core.domain.AuthRepository
import com.mustafaderinoz.core.domain.AuthSession
import com.mustafaderinoz.core.domain.User
import com.mustafaderinoz.core.domain.UserRole
import com.mustafaderinoz.data.dto.CredentialsDto
import com.mustafaderinoz.data.remote.AuthApi
import com.mustafaderinoz.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean>
        get() = TODO("Not yet implemented")

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email=email, password=password))
    }.onSuccess {
        // jwt'i bi yere yaz..
    }
        .map {
                i -> AuthSession(
            user = User(
                i.user.id, i.user.email, UserRole.fromApi(i.user.role),
            ),
            accessToken = i.accessToken,
            refreshToken = i.refreshToken)
        }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.register(CredentialsDto(email = email, password = password))
    }.onSuccess {

    }.map { i ->
        AuthSession(
            user = User(i.user.id, i.user.email, UserRole.fromApi(i.user.role)),
            accessToken = i.accessToken,
            refreshToken = i.refreshToken
        )
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }
}