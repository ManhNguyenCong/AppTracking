package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserDtoReq(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("username")
    val username: String?,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("dob")
    val dob: Date?,
    @SerializedName("birthPlace")
    val birthPlace: String? = "Việt Nam",
    @SerializedName("hasPhoto")
    val hasPhoto: Boolean? = false,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("email")
    val email: String?,
    @SerializedName("university")
    val university: String? = null,
    @SerializedName("changePass")
    val changePass: Boolean? = false,
    @SerializedName("setPassword")
    val setPassword: Boolean? = true,
    @SerializedName("password")
    val password: String?,
    @SerializedName("confirmPassword")
    val confirmPassword: String?,
    @SerializedName("countDayCheckin")
    val countDayCheckin: Int? = null,
    @SerializedName("countDayTracking")
    val countDayTracking: Int? = null,
    @SerializedName("roles")
    val roles: List<RoleDtoReq>? = listOf(),
    @SerializedName("active")
    val active: Boolean? = true,
    @SerializedName("tokenDevice")
    val tokenDevice: String? = null,
    @SerializedName("year")
    val year: Int? = null
)
