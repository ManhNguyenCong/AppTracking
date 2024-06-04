package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.CommentsDtoReq
import com.oceantech.tracking.data.model.CommentsDtoRes
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.network.PostsApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class PostsRepository(
    private val api: PostsApi
) {
    fun getNewPosts(search: SearchDto): Observable<Page<PostsDto>> =
        api.getNewPosts(search).subscribeOn(Schedulers.io())

    fun commentPosts(postId: Int, comment: CommentsDtoReq): Observable<CommentsDtoRes> =
        api.commentPosts(postId, comment).subscribeOn(Schedulers.io())
}