package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserDtoRes(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("dob")
    val dob: Date?,
    @SerializedName("birthPlace")
    val birthPlace: String?,
    @SerializedName("hasPhoto")
    val hasPhoto: Boolean?,
    @SerializedName("image")
    val image: String? = "string",
    @SerializedName("email")
    val email: String?,
    @SerializedName("university")
    val university: String?,
    @SerializedName("changePass")
    val changePass: Boolean?,
    @SerializedName("setPassword")
    val setPassword: Boolean?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("confirmPassword")
    val confirmPassword: String?,
    @SerializedName("countDayCheckin")
    val countDayCheckin: Int?,
    @SerializedName("countDayTracking")
    val countDayTracking: Int?,
    @SerializedName("roles")
    val roles: List<RoleDtoRes>?,
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("tokenDevice")
    val tokenDevice: String?,
    @SerializedName("year")
    val year: Int?
)

fun UserDtoRes.toUserDto(): UserDto {
    return UserDto(
        id,
        firstName,
        lastName,
        username,
        displayName,
        gender,
        dob,
        birthPlace,
        hasPhoto,
        image,
        email,
        university,
        changePass,
        setPassword,
        password,
        confirmPassword,
        countDayCheckin,
        countDayTracking,
        roles?.map { it.toRoleDto() },
        active,
        tokenDevice,
        year
    )
}
