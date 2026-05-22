package com.mustafaderinoz.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    // "2026-05-22T14:12:26.236Z" → "22 May 2026"
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(iso: String): String = runCatching {
        val zdt = ZonedDateTime.parse(iso)
        zdt.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("tr")))
    }.getOrDefault(iso)

    // "2026-05-22T14:12:26.236Z" → "14:12"
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(iso: String): String = runCatching {
        val zdt = ZonedDateTime.parse(iso)
        zdt.format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrDefault("")
}