package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.TrackingDtoRes
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingApi {
    @GET("tracking")
    fun getAllByUser(): Observable<List<TrackingDtoRes>>

    @GET("tracking")
    fun getAllByUserTest(): Call<List<TrackingDtoRes>>

    @POST("tracking")
    fun save(@Body trackingDtoReq: TrackingDtoReq): Observable<TrackingDtoRes>

    @POST("tracking/{id}")
    fun update(
        @Path("id") trackingId: Int,
        @Body trackingDtoReq: TrackingDtoReq
    ): Observable<TrackingDtoRes>

    @DELETE("tracking/{id}")
    fun delete(
        @Path("id") trackingId: Int
    ): Observable<TrackingDtoRes>
}