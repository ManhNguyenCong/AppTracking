package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.TrackingDtoRes
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call

class TrackingRepository(
    var api: TrackingApi
) {
    fun getAllByUser(): Observable<List<TrackingDtoRes>> =
        api.getAllByUser().subscribeOn(Schedulers.io())

    fun getAllByUserTest(): Call<List<TrackingDtoRes>> =
        api.getAllByUserTest()

    fun save(tracking: TrackingDtoReq): Observable<TrackingDtoRes> =
        api.save(tracking).subscribeOn(Schedulers.io())

    fun update(id: Int, tracking: TrackingDtoReq): Observable<TrackingDtoRes> =
        api.update(id, tracking).subscribeOn(Schedulers.io())

    fun delete(id: Int): Observable<TrackingDtoRes> = api.delete(id).subscribeOn(Schedulers.io())
}