package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.CommentsDtoRes
import com.oceantech.tracking.data.model.NotificationDto
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.PostsDtoRes
import com.oceantech.tracking.data.model.TimeSheetDto
import com.oceantech.tracking.data.model.TrackingDtoRes
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.UserDtoRes

data class HomeViewState(
    val asyncLogout: Async<Void> = Uninitialized,
    val asyncCheckIn: Async<TimeSheetDto> = Uninitialized,
    val asyncSaveTracking: Async<TrackingDtoRes> = Uninitialized,
    val asyncUpdateTracking: Async<TrackingDtoRes> = Uninitialized,
    val userCurrent: Async<UserDto> = Uninitialized,
    val trackings: Async<List<TrackingDtoRes>> = Uninitialized,
    val timeSheets: Async<List<TimeSheetDto>> = Uninitialized,
    val asyncPage: Async<Page<UserDto>> = Uninitialized,
    val asyncNotifications: Async<List<NotificationDto>> = Uninitialized,
    val asyncUpdateMyself: Async<UserDtoRes> = Uninitialized,

    val asyncNewPosts: Async<Page<PostsDto>> = Uninitialized,
    val asyncCommentPosts: Async<CommentsDtoRes> = Uninitialized
) : MvRxState {
    fun isLoading() = userCurrent is Loading ||
            asyncLogout is Loading ||
            asyncCheckIn is Loading ||
            asyncSaveTracking is Loading ||
            asyncUpdateTracking is Loading ||
            trackings is Loading ||
            timeSheets is Loading ||
            asyncNotifications is Loading ||
            asyncUpdateMyself is Loading ||
            asyncNewPosts is Loading ||
            asyncCommentPosts is Loading
}