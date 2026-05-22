package com.mustafaderinoz.data.mapper


import com.mustafaderinoz.core.domain.ticket.PurchasedTicket
import com.mustafaderinoz.data.dto.ticket.PurchasedTicketDto

// EventDto.toDomain() ile aynı mantık:
// API alanı değişirse sadece burayı düzeltiyoruz, core'a dokunmuyoruz.
internal fun PurchasedTicketDto.toDomain(): PurchasedTicket = PurchasedTicket(
    id = id,
    qrCode = qrCode,
    status = status,
    ticketTypeId = ticketTypeId,
)