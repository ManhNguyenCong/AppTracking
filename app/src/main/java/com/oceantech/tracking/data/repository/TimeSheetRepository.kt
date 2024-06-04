package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TimeSheetDto
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import javax.inject.Inject

class TimeSheetRepository @Inject constructor(
    var api: TimeSheetApi
) {
    fun checkIn(ip: String): Observable<TimeSheetDto> = api.checkIn(ip).subscribeOn(Schedulers.io())

    fun getAllByUser(): Observable<List<TimeSheetDto>> = api.getAllByUser().subscribeOn(Schedulers.io())
}