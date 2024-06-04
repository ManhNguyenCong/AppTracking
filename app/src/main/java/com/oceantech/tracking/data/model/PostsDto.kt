package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PostsDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val user: UserDto?,
    @SerializedName("comments")
    val comments: List<CommentsDto>?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("likes")
    val likes: List<LikesDto>?,
    @SerializedName("media")
    val media: List<DocumentDto>?
)

fun PostsDto.toReq(): PostsDtoReq {
    return PostsDtoReq(
        id = id,
        user = user?.toReq(),
        comments = comments?.map { it.toReq() },
        content = content,
        date = date,
        likes = likes?.map { it.toReq() },
        media = media
    )
}