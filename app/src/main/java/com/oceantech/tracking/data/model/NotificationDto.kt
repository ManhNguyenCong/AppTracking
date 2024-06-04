package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class NotificationDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("body")
    val body: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("date")
    val date: Date?,
    @SerializedName("user")
    val user: UserDto?
)
