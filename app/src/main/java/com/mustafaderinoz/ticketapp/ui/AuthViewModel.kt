package com.mustafaderinoz.ticketapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafaderinoz.core.domain.AuthRepository
import com.mustafaderinoz.core.domain.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 1. Arayüzün (UI) alabileceği durumları temsil eden Sealed Class
sealed class LoginUiState {
    object Idle : LoginUiState() // Başlangıç durumu (Bekleme)
    object Loading : LoginUiState() // İstek atıldı, yükleniyor
    data class Success(val session: AuthSession) : LoginUiState() // Giriş başarılı
    data class Error(val message: String) : LoginUiState() // Hata durumu
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)


    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Arayüze yükleniyor durumunu bildir
            _uiState.value = LoginUiState.Loading

            // İsteği at
            val result = authRepository.login(email, password)

            // Sonuca göre state'i güncelle
            result.onSuccess { session ->
                _uiState.value = LoginUiState.Success(session)
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error.message ?: "Bilinmeyen bir hata oluştu")
            }
        }
    }
}