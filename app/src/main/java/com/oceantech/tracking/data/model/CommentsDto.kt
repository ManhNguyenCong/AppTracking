package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommentsDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("post")
    val post: PostsDto?,
    @SerializedName("user")
    val user: UserDto?
)

fun CommentsDto.toReq(): CommentsDtoReq {
    return CommentsDtoReq(id, date, content, post?.toReq(), user?.toReq())
}