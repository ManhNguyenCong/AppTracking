package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommentsDtoRes(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("post")
    val post: PostsDtoRes?,
    @SerializedName("user")
    val user: UserDtoRes?
)

fun CommentsDtoRes.toCommentDto(): CommentsDto {
    return CommentsDto(id, date, content, post?.toPostDto(), user?.toUserDto())
}