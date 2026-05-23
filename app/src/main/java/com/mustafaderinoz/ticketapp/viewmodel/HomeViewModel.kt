package com.mustafaderinoz.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.domain.event.EventRepository
import com.mustafaderinoz.core.domain.ticket.PurchasedTicket
import com.mustafaderinoz.core.domain.ticket.TicketRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isEventsLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val eventsError: String? = null,

    val isTicketsLoading: Boolean = false,
    val tickets: List<PurchasedTicket> = emptyList(),
    val ticketsError: String? = null,
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        // ÇÖZÜM: İkisini aynı anda çağırmak yerine tek bir yükleme akışı başlatıyoruz
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // 1. Loading durumunu başlat
            _state.update {
                it.copy(
                    isEventsLoading = true,
                    isTicketsLoading = true,
                    eventsError = null,
                    ticketsError = null
                )
            }

            // 2. Verileri Paralel Çek
            val eventsDeferred = async { eventRepository.getEvents() }
            val ticketsDeferred = async { ticketRepository.getPurchasedTickets() }

            val eventsResult = eventsDeferred.await()
            val ticketsResult = ticketsDeferred.await()


            val events = eventsResult.getOrNull()
            val eventsError = eventsResult.exceptionOrNull()?.message
                ?: if (eventsResult.isFailure) "Etkinlikler yüklenemedi." else null

            val tickets = ticketsResult.getOrNull()
            val ticketsError = ticketsResult.exceptionOrNull()?.message
                ?: if (ticketsResult.isFailure) "Biletler yüklenemedi." else null


            val finalTickets = if (events != null && tickets != null) {


                val ticketTypeToEventMap = events.flatMap { event ->
                    event.ticketTypes.map { it.id to event }
                }.toMap()

                val ticketTypeMap = events.flatMap { it.ticketTypes }.associateBy { it.id }

                tickets.map { ticket ->
                    val event = ticketTypeToEventMap[ticket.ticketTypeId]
                    val ticketType = ticketTypeMap[ticket.ticketTypeId]

                    ticket.copy(
                        eventName = event?.name ?: "",
                        eventVenue = event?.venue ?: "",
                        eventStartsAt = event?.startsAt ?: "",
                        ticketTypeName = ticketType?.name ?: "",
                        ticketTypePriceCents = ticketType?.priceCents ?: 0L,
                    )
                }
            } else {
                tickets ?: emptyList() // Eventler başarısızsa
            }

            // 5. State'i TEK SEFERDE Güncelle
            _state.update {
                it.copy(
                    isEventsLoading = false,
                    events = events ?: emptyList(),
                    eventsError = eventsError,

                    isTicketsLoading = false,
                    tickets = finalTickets,
                    ticketsError = ticketsError
                )
            }
        }
    }
}