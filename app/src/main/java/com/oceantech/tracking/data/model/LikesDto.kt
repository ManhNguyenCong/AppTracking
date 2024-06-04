package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LikesDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("user")
    val user: UserDto?,
    @SerializedName("post")
    val postsDto: PostsDto?
)

fun LikesDto.toReq(): LikesDtoReq {
    return LikesDtoReq(id, date, type, user?.toReq(), postsDto?.toReq() )
}