package com.mustafaderinoz.core.util

import com.mustafaderinoz.core.domain.event.TicketType

object TicketUtils {

    fun minPriceLabel(ticketTypes: List<TicketType>): String? {
        val min = ticketTypes.minOfOrNull { it.priceCents } ?: return null
        val tl = min / 100.0
        return "₺${tl.toLong()}'den başlayan"
    }


    fun totalRemaining(ticketTypes: List<TicketType>): Long =
        ticketTypes.sumOf { it.remaining }

    fun formatPrice(priceCents: Long): String {
        val amount = priceCents / 100.0
        return if (amount == 0.0) "Ücretsiz" else "₺%.2f".format(amount)
    }
}