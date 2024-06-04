package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommentsDtoReq(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("post")
    val post: PostsDtoReq?,
    @SerializedName("user")
    val user: UserDtoReq?
)
