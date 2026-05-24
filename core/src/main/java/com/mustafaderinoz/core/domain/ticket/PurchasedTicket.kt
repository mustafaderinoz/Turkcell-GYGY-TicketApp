package com.mustafaderinoz.core.domain.ticket

data class PurchasedTicket(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String,
)