package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.AndroidResponseDto
import com.oceantech.tracking.data.model.CommentsDto
import com.oceantech.tracking.data.model.CommentsDtoReq
import com.oceantech.tracking.data.model.CommentsDtoRes
import com.oceantech.tracking.data.model.LikesDtoReq
import com.oceantech.tracking.data.model.LikesDtoRes
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.PostsDtoReq
import com.oceantech.tracking.data.model.Resource
import com.oceantech.tracking.data.model.SearchDto
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostsApi {
    @POST("posts/get-news")
    fun getNewPosts(@Body search: SearchDto): Observable<Page<PostsDto>>

    @POST("posts/comments/{id}")
    fun commentPosts(@Path("id") postId: Int, @Body comment: CommentsDtoReq): Observable<CommentsDtoRes>

    @POST("posts/likes/{id}")
    fun likePosts(@Path("id") postId: Int, @Body like: LikesDtoReq): Observable<LikesDtoRes>

    @POST("posts/create")
    fun create(@Body postReq: PostsDtoReq): Observable<AndroidResponseDto>

//    @GET("public/images/{name}")
//    fun getImageFile(@Path("name") filename: String): Call<Resource>
}