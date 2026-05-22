package com.mustafaderinoz.data.mapper

import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.domain.event.TicketType
import com.mustafaderinoz.data.dto.event.EventDto
import com.mustafaderinoz.data.dto.event.TicketTypeDto

// diyelim ki artık apide venue alanı değişti eventdto da değişikliği yaparız burayada öreneğin venue=place yapmamız yeterli core daki Event alanı değiştirmemeliyiz.
internal fun EventDto.toDomain(): Event = Event(
    id=id,
    name=name,
    description=description.orEmpty(),
    venue=place.orEmpty(),
    startsAt = startsAt.orEmpty(),
    endsAt = endsAt.orEmpty(),
    ticketTypes = ticketTypes.map { it.toDomain() }
)

internal fun TicketTypeDto.toDomain() : TicketType = TicketType(
    id=id,
    name=name,
    priceCents=priceCents,
    capacity=capacity,
    soldCount=soldCount,
    remaining=remaining
)