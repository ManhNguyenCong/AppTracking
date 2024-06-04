package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class RoleDtoRes(
    @SerializedName("authority")
    val authority: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)

fun RoleDtoRes.toRoleDto(): RoleDto {
    return RoleDto(authority, description, id, name)
}
