package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class SearchDto(
    @SerializedName("keyWord")
    val keyWord: String?,
    @SerializedName("pageIndex")
    val pageIndex: Int?,
    @SerializedName("size")
    val size: Int?,
    @SerializedName("status")
    val status: Int?
)
