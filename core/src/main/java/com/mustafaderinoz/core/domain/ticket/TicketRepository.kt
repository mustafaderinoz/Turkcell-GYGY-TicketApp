package com.mustafaderinoz.core.domain.ticket

interface TicketRepository {
    suspend fun getPurchasedTickets(): Result<List<PurchasedTicket>>
}