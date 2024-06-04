package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class VersionDto(
    @SerializedName("versionName")
    val versionName: String?
)
