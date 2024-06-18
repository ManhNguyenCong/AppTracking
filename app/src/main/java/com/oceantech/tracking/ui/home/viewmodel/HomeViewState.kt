package com.oceantech.tracking.ui.home.viewmodel

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.AndroidResponseDto
import com.oceantech.tracking.data.model.CommentsDtoRes
import com.oceantech.tracking.data.model.LikesDtoRes
import com.oceantech.tracking.data.model.NotificationDto
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.PostsDto
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

    val asyncPosts: Async<Page<PostsDto>> = Uninitialized,
    val asyncCommentPosts: Async<CommentsDtoRes> = Uninitialized,
    val asyncLikePosts: Async<LikesDtoRes> = Uninitialized,

    val asyncCreatePost: Async<AndroidResponseDto> = Uninitialized,

    val asyncUser: Async<UserDtoRes> = Uninitialized,
    val asyncEdit: Async<UserDtoRes> = Uninitialized,
    val asyncBlock: Async<UserDtoRes> = Uninitialized
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
            asyncPosts is Loading ||
            asyncCommentPosts is Loading ||
            asyncLikePosts is Loading ||
            asyncCreatePost is Loading ||
            asyncUser is Loading ||
            asyncEdit is Loading ||
            asyncBlock is Loading
}