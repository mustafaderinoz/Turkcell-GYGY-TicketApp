package com.mustafaderinoz.data.dto

import kotlinx.serialization.Serializable

// /auth/login ve /auth/register'a atılan istek
@Serializable
data class CredentialsDto(val email: String, val password: String) {}