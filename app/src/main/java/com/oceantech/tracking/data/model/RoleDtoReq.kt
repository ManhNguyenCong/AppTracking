package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class RoleDtoReq(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)
