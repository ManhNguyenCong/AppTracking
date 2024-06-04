package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LikesDtoRes(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("user")
    val user: UserDtoRes?,
    @SerializedName("post")
    val postsDto: PostsDtoRes?
)

fun LikesDtoRes.toLikeDto(): LikesDto {
    return LikesDto(id, date, type, user?.toUserDto(), postsDto?.toPostDto())
}
