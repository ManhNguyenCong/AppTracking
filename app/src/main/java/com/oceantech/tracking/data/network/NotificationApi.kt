package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.NotificationDto
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface NotificationApi {
    @GET("notifications")
    fun getAllByUser(): Observable<List<NotificationDto>>

    @GET("notifications")
    fun getAllByUserTest(): Call<List<NotificationDto>>
}