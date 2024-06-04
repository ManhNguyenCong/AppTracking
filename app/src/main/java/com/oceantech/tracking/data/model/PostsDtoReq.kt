package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PostsDtoReq(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val user: UserDtoReq?,
    @SerializedName("comments")
    val comments: List<CommentsDtoReq>?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("likes")
    val likes: List<LikesDtoReq>?,
    @SerializedName("media")
    val media: List<DocumentDto>?
)
