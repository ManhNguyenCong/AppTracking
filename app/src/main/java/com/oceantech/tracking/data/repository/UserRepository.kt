package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.UserDtoReq
import com.oceantech.tracking.data.model.UserDtoRes
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.ui.security.UserPreferences
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi, private val preferences: UserPreferences
) {
    fun getCurrentUser(): Observable<UserDto> = api.getCurrentUser().subscribeOn(Schedulers.io())
    fun getString(): String = "test part"

    suspend fun clearUserPreferences() {
        preferences.clear()
    }

    fun updateMyself(user: UserDtoReq): Observable<UserDtoRes> =
        api.updateMyself(user).subscribeOn(Schedulers.io())

    fun searchByPage(searchDto: SearchDto): Observable<Page<UserDto>> =
        api.searchByPage(searchDto).subscribeOn(Schedulers.io())
}