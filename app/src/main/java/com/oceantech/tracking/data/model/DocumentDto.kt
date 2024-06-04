package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class DocumentDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("contentSize")
    val contentSize: Int?,
    @SerializedName("contentType")
    val contentType: String?,
    @SerializedName("extension")
    val extension: String?,
    @SerializedName("filePath")
    val filePath: String?,
    @SerializedName("isVideo")
    val isVideo: Boolean?,
    @SerializedName("posts")
    val posts: PostsDtoReq?
)
