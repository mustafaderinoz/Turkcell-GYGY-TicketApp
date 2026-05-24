package com.mustafaderinoz.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.domain.event.EventRepository
import com.mustafaderinoz.core.domain.ticket.PurchasedTicketUi
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
    val tickets: List<PurchasedTicketUi> = emptyList(),
    val ticketsError: String? = null,
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
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

            val ticketTypeToEventMap = events
                ?.flatMap { event -> event.ticketTypes.map { it.id to event } }
                ?.toMap() ?: emptyMap()

            val ticketTypeMap = events
                ?.flatMap { it.ticketTypes }
                ?.associateBy { it.id } ?: emptyMap()

            val enrichedTickets = tickets?.map { ticket ->
                PurchasedTicketUi(
                    ticket = ticket,
                    event = ticketTypeToEventMap[ticket.ticketTypeId],
                    ticketType = ticketTypeMap[ticket.ticketTypeId],
                )
            } ?: emptyList()

            _state.update {
                it.copy(
                    isEventsLoading = false,
                    events = events ?: emptyList(),
                    eventsError = eventsError,
                    isTicketsLoading = false,
                    tickets = enrichedTickets,
                    ticketsError = ticketsError
                )
            }
        }
    }
}