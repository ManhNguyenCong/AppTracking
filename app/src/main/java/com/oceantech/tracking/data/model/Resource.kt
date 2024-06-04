package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.InputStream

data class Resource(
    @SerializedName("description")
    val description: String?,
    @SerializedName("file")
    val file: File?,
    @SerializedName("fileName")
    val fileName: String?,
    @SerializedName("inputStream")
    val inputStream: InputStream?,
    @SerializedName("open")
    val open: Boolean?,
    @SerializedName("readable")
    val readable: Boolean?,
    @SerializedName("uri")
    val uri: String?,
    @SerializedName("url")
    val url: String?
)