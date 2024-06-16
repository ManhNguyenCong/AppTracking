package com.oceantech.tracking.ui.home.viewmodel

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.CommentsDtoReq
import com.oceantech.tracking.data.model.LikesDtoReq
import com.oceantech.tracking.data.model.PostsDtoReq
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.UserDtoReq

sealed class HomeViewAction : NimpeViewModelAction {

    object GetCurrentUser : HomeViewAction()
    object ResetLang : HomeViewAction()
    object SetNavUp : HomeViewAction()

    object Logout : HomeViewAction()
    object GetTimeSheetsByUser: HomeViewAction()
    object GetAllTrackingByUser: HomeViewAction()
    object GetNotificationsByUser: HomeViewAction()

    data class CheckIn(val ip: String) : HomeViewAction()
    data class SaveTracking(val tracking: TrackingDtoReq) : HomeViewAction()
    data class UpdateTracking(val id: Int, val tracking: TrackingDtoReq) : HomeViewAction()
    data class SearchByPage(val search: SearchDto): HomeViewAction()
    data class UpdateMyself(val user: UserDtoReq): HomeViewAction()

    data class GetNewPosts(val search: SearchDto): HomeViewAction()
    data class CommentPosts(val postId: Int, val comment: CommentsDtoReq): HomeViewAction()
    data class LikePosts(val postId: Int, val like: LikesDtoReq): HomeViewAction()

    data class createPost(val postReq: PostsDtoReq): HomeViewAction()
}