package com.mustafaderinoz.data.dto.event

import kotlinx.serialization.Serializable

// Event var neden EventDto oluşturuyoruz içeriğide aynı olmasına rağmen:
//Burası dış dünyayla iletişim halinde olan bir yerdir şu an aynı olabilir ama ilerde değişebilir.
//Dış dünyayla iletişimi core ile kurarsak dış dünya değiştiğinde core da değişir
@Serializable
data class EventDto(
    val id: String,
    val name: String,
    val description: String,
    val place: String,
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketTypeDto> = emptyList()
)
