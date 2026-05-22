package com.mustafaderinoz.core.domain.ticket

data class PurchasedTicket(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String,
    // Event'ten join edilen alanlar (HomeViewModel katmanında doldurulur)
    val eventName: String = "",
    val eventVenue: String = "",
    val eventStartsAt: String = "",
    val ticketTypeName: String = "",
    val ticketTypePriceCents: Long = 0L,
)