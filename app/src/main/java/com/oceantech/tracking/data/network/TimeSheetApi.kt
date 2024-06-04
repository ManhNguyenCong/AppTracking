package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheetDto
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeSheetApi {
    @GET("time-sheets")
    fun getAllByUser(): Observable<List<TimeSheetDto>>

    @GET("time-sheets/check-in")
    fun checkIn(@Query("ip") ip: String): Observable<TimeSheetDto>
}