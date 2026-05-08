package com.mustafaderinoz.ticketapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafaderinoz.core.ui.theme.TicketAppTheme
import com.mustafaderinoz.ticketapp.ui.AuthViewModel
import com.mustafaderinoz.ticketapp.ui.LoginUiState
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    val viewModel: AuthViewModel = koinViewModel()
                    // ViewModel'daki state'i Compose tarafında dinliyoruz
                    val uiState by viewModel.uiState.collectAsState()

                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Mevcut State'e göre ekranda ne gösterileceğine karar veriyoruz
                        when (val state = uiState) {

                            is LoginUiState.Idle -> {
                                Button(onClick = { viewModel.login("mustafa@example.com", "mustafa123") }) {
                                    Text("Giriş Yap (Test İsteği)")
                                }
                            }

                            is LoginUiState.Loading -> {
                                CircularProgressIndicator()
                                Text(
                                    text = "İstek atıldı, backend bekleniyor...",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            is LoginUiState.Success -> {
                                Text("🎉 Giriş Başarılı!", style = MaterialTheme.typography.titleLarge)

                                Text(
                                    "Rol: ${state.session.user.role}",
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Text(
                                    "Access Token:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(state.session.accessToken)

                                Text(
                                    "Refresh Token:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(state.session.refreshToken)
                            }

                            is LoginUiState.Error -> {
                                Text("❌ Hata Oluştu", color = MaterialTheme.colorScheme.error)
                                Text("Detay: ${state.message}", modifier = Modifier.padding(vertical = 8.dp))
                                Button(onClick = { viewModel.login("mustafa@example.com", "mustafa123") }) {
                                    Text("Tekrar Dene")
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}