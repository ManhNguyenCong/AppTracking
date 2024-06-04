package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class AndroidResponseDto<PostSDto> (
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: PostsDtoRes?,
    @SerializedName("message")
    val message: String?
)
