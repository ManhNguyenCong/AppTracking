package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

//data class Page<T>(
//
//    @SerializedName("content")
//    val content: List<T>? = ArrayList(),
//
//    @SerializedName("totalPages")
//    val totalPages: Int? = null,
//
//    @SerializedName("totalElements")
//    val totalElements: Int? = null,
//
//    @SerializedName("number")
//    val number: Int? = null,
//
//    @SerializedName("size")
//    val size: Int? = null,
//
//    @SerializedName("first")
//    val first: Boolean? = null,
//
//    @SerializedName("empty")
//    val empty: Boolean? = null
//
//)

//data class Pageable(
//
//    @SerializedName("pageIndex")
//    val pageNumber: Int? = null,
//
//    @SerializedName("pageSize")
//    val pageSize: Int? = null
//
//)

data class Page<T>(
    @SerializedName("content")
    val content: List<T>,
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable?,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: Sort?,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int
)

data class Pageable(
    @SerializedName("offset")
    val offset: Int?,
    @SerializedName("pageNumber")
    val pageNumber: Int?,
    @SerializedName("pageSize")
    val pageSize: Int?,
    @SerializedName("sort")
    val sort: Sort?,
    @SerializedName("paged")
    val paged: Boolean?,
    @SerializedName("unpaged")
    val unpaged: Boolean?
)

data class Sort(
    @SerializedName("empty")
    val empty: Boolean?,
    @SerializedName("sorted")
    val sorted: Boolean?,
    @SerializedName("unsorted")
    val unsorted: Boolean?
)