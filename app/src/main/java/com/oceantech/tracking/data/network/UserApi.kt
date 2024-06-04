package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.UserDtoReq
import com.oceantech.tracking.data.model.UserDtoRes
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<UserDto>
    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<UserDto>

    //
    @POST("public/sign")
    fun createOrUpdate(@Body user: UserDtoReq): Observable<UserDtoRes>

    @GET("users/{id}")
    fun getUserById(@Path("id") userId: Int): Observable<UserDtoRes>

    @GET("users/get-user-current")
    fun getUserCurrent(): Observable<UserDtoRes>

    @GET("users/lock/{id}")
    fun blockUser(@Path("id") userId: Int): Observable<UserDtoRes>

    @POST("users/searchByPage")
    fun searchByPage(@Body searchDto: SearchDto): Observable<Page<UserDto>>

    @POST("users/searchByPage")
    fun searchByPageTest(@Body searchDto: SearchDto): Call<Page<UserDto>>

    @GET("users/token-device")
    fun edit(@Query("tokenDevice") tokenDevice: String): Observable<UserDtoRes>

    @POST("users/update-myself")
    fun updateMyself(@Body user: UserDtoReq): Observable<UserDtoRes>

    @POST("users/update/{id}")
    fun edit(@Path("id") userId: Int, @Body userDtoReq: UserDtoReq): Observable<UserDtoRes>
}