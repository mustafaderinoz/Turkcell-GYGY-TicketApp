package com.mustafaderinoz.data.remote

import com.mustafaderinoz.data.dto.ticket.PurchasedTicketDto
import retrofit2.http.GET

interface TicketApi {
    @GET("/me/tickets")       // Endpoint'i backend'e göre güncelle
    suspend fun getPurchasedTickets(): List<PurchasedTicketDto>
}