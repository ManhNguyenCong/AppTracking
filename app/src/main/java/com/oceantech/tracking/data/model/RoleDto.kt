package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class RoleDto(
    @SerializedName("authority")
    val authority: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)

fun RoleDto.toReq(): RoleDtoReq {
    return RoleDtoReq(description, id, name)
}
