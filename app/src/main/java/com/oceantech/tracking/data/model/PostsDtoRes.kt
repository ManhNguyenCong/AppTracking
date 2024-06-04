package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PostsDtoRes(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val user: UserDtoRes?,
    @SerializedName("comments")
    val comments: List<CommentsDtoRes>?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("likes")
    val likes: List<LikesDtoRes>?,
    @SerializedName("media")
    val media: List<DocumentDto>?
)

fun PostsDtoRes.toPostDto(): PostsDto {
    return PostsDto(id, user?.toUserDto(), comments?.map { it.toCommentDto() }, content, date, likes?.map { it.toLikeDto() }, media)
}