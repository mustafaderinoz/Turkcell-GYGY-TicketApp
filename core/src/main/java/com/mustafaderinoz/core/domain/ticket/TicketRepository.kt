package com.mustafaderinoz.core.domain.ticket

interface TicketRepository {
    suspend fun getPurchasedTickets(): Result<List<PurchasedTicket>>
    suspend fun getTicketById(id: String): Result<PurchasedTicket>
}