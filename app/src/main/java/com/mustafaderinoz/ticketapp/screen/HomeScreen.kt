package com.mustafaderinoz.ticketapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.util.DateTimeUtils
import com.mustafaderinoz.core.util.TicketUtils
import com.mustafaderinoz.ticketapp.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "Yaklaşan Etkinlikler",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Spacer(Modifier.height(16.dp))
            EventsRow(isLoading = state.isEventsLoading, error = state.eventsError, events = state.events)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Satın Alınmış Biletler",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventsRow(
    isLoading: Boolean,
    error: String?,
    events: List<Event>
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
        events.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(text = "Şimdilik hiçbir etkinlik yok.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        else -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Kartlar arası boşluk
            ) {
                items(items = events, key = { it.id }) { event ->
                    EventCard(event)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventCard(event: Event) {
    val initial = event.name.firstOrNull { it.isLetter() } ?: '?'
    val remaining = TicketUtils.totalRemaining(event.ticketTypes)

    Card(
        modifier = Modifier
            .width(260.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ── Üst: Baş harf banner (Sabit Renkli)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer), // Sabit arka plan rengi
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial.uppercaseChar().toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer // Sabit yazı rengi
                )
            }

            // ── Alt: Bilgiler ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Etkinlik adı
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Mekan
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = event.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Açıklama
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(4.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(Modifier.height(4.dp))

                // Tarih & saat badge'leri
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DateBadge(
                        icon = Icons.Outlined.DateRange,
                        label = DateTimeUtils.formatDate(event.startsAt)
                    )
                    DateBadge(
                        icon = Icons.Outlined.CheckCircle,
                        label = "${DateTimeUtils.formatTime(event.startsAt)} – ${DateTimeUtils.formatTime(event.endsAt)}"
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Alt satır: Kalan bilet + fiyat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (pillBg, pillText) = if (remaining > 0)
                        Color(0xFFEAF3DE) to Color(0xFF27500A)
                    else
                        Color(0xFFFCEBEB) to Color(0xFF791F1F)

                    Surface(
                        shape = CircleShape,
                        color = pillBg
                    ) {
                        Text(
                            text = if (remaining > 0) "$remaining kaldı" else "Tükendi",
                            style = MaterialTheme.typography.labelSmall,
                            color = pillText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    TicketUtils.minPriceLabel(event.ticketTypes)?.let { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateBadge(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}