package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TimeSheetDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user")
    val user: UserDto?,
    @SerializedName("dateAttendance")
    val dateAttendance: Date?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("offline")
    val offline: Boolean?,
    @SerializedName("ip")
    val ip: String?,
)
