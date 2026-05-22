package com.mustafaderinoz.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.domain.event.EventRepository
import com.mustafaderinoz.core.domain.ticket.PurchasedTicket
import com.mustafaderinoz.core.domain.ticket.TicketRepository
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
            _state.update {
                it.copy(
                    isEventsLoading = true,
                    isTicketsLoading = true,
                    eventsError = null,
                    ticketsError = null
                )
            }

            // 1. Önce etkinlikleri çekiyoruz
            eventRepository.getEvents().fold(
                onSuccess = { eventList ->
                    _state.update { it.copy(events = eventList, isEventsLoading = false) }

                    // 2. Etkinlikler başarıyla geldikten SONRA biletleri çekiyoruz
                    loadTicketsEnrichedWith(eventList)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isEventsLoading = false,
                            eventsError = e.message ?: "Etkinlikler yüklenemedi.",
                            isTicketsLoading = false // Etkinlik yoksa biletleri bekletmeyi de iptal et
                        )
                    }
                }
            )
        }
    }

    private suspend fun loadTicketsEnrichedWith(events: List<Event>) {
        ticketRepository.getPurchasedTickets().fold(
            onSuccess = { list ->
                // Artık "events" listesinin dolu olduğundan %100 eminiz
                val enriched = list.map { ticket ->
                    val event = events.find { e ->
                        e.ticketTypes.any { tt -> tt.id == ticket.ticketTypeId }
                    }
                    val ticketType = event?.ticketTypes?.find { it.id == ticket.ticketTypeId }

                    ticket.copy(
                        eventName     = event?.name     ?: "",
                        eventVenue    = event?.venue    ?: "",
                        eventStartsAt = event?.startsAt ?: "",
                        ticketTypeName        = ticketType?.name           ?: "",
                        ticketTypePriceCents  = ticketType?.priceCents     ?: 0L,
                    )
                }
                _state.update { it.copy(tickets = enriched, isTicketsLoading = false) }
            },
            onFailure = { e ->
                _state.update {
                    it.copy(
                        isTicketsLoading = false,
                        ticketsError = e.message ?: "Biletler yüklenemedi."
                    )
                }
            }
        )
    }
}