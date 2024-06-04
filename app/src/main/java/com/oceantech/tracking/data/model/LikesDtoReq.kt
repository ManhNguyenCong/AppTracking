package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LikesDtoReq(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("user")
    val user: UserDtoReq?,
    @SerializedName("post")
    val postsDto: PostsDtoReq?
)