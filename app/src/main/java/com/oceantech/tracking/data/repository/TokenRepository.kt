package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.network.TokenApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TokenRepository @Inject constructor(
    val api: TokenApi
) {
    fun logout(): Observable<Void> = api.logout().subscribeOn(Schedulers.io())
}