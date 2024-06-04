package com.oceantech.tracking.data.network

import io.reactivex.Observable
import retrofit2.http.DELETE

interface TokenApi {

    @DELETE("oauth/logout")
    fun logout(): Observable<Void>
}