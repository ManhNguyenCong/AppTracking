package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.NotificationDto
import com.oceantech.tracking.data.network.NotificationApi
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers

class NotificationRepository(
    private val notificationApi: NotificationApi
) {
    fun getAllByUser(): Observable<List<NotificationDto>> = notificationApi.getAllByUser().subscribeOn(Schedulers.io())

    fun getAllByUserTest() = notificationApi.getAllByUserTest()
}