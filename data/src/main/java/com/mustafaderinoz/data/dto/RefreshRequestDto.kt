package com.mustafaderinoz.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(val refreshToken: String)

// {"refreshToken":"abc"}