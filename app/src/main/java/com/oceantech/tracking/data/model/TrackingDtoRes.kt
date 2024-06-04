package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TrackingDtoRes(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val user: UserDtoRes?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("content")
    val content: String?
)
