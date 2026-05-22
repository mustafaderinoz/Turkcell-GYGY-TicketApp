package com.mustafaderinoz.data.remote

import com.mustafaderinoz.data.dto.event.EventDto
import retrofit2.http.GET

interface EventApi {
    @GET("/events")
    suspend fun getEvents(): List<EventDto>  // body de api bir şey istemiyor dönüş olarak EventDto dönecek
}