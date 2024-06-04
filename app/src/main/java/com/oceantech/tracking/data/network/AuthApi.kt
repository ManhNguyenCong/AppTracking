package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserCredentials
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

interface AuthApi {
    @POST("oauth/token")
    fun loginWithRefreshToken(@Body credentials: UserCredentials): Call<TokenResponse>
    @POST("oauth/token")
    fun oauth(@Body credentials: UserCredentials): Observable<TokenResponse>

    @POST("public/sign")
    fun createOrUpdate(@Body user: UserDtoReq): Observable<UserDtoRes>

    companion object {
        val CLIENT_ID = "core_client" //"core_client"

        val CLIENT_SECRET = "secret" //"secret"

        val GRANT_TYPE_PASSWORD = "password"

        val GRANT_TYPE_REFRESH = "refresh_token"

        val DEFAULT_SCOPES = "read write delete"
    }
}