package com.oceantech.tracking.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.CommentsDtoReq
import com.oceantech.tracking.data.model.LikesDtoReq
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.UserDtoReq
import com.oceantech.tracking.data.repository.NotificationRepository
import com.oceantech.tracking.data.repository.PostsRepository
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.TokenRepository
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import java.net.NetworkInterface
import java.net.SocketException

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    private val userRepo: UserRepository,
    private val tokenRepo: TokenRepository,
    private val timeSheetRepo: TimeSheetRepository,
    private val trackingRepo: TrackingRepository,
    private val notifRepo: NotificationRepository,
    private val postsRepo: PostsRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
            is HomeViewAction.Logout -> handLogout()
            is HomeViewAction.CheckIn -> handCheckIn(action.ip)
            is HomeViewAction.SaveTracking -> handSaveTracking(action.tracking)
            is HomeViewAction.UpdateTracking -> handUpdateTracking(action.id, action.tracking)
            is HomeViewAction.GetAllTrackingByUser -> handGetTrackingsByUser()
            is HomeViewAction.GetTimeSheetsByUser -> handGetTimeSheetsByUser()
            is HomeViewAction.SearchByPage -> handSearchByPage(action.search)

            is HomeViewAction.GetNotificationsByUser -> handleGetNotifications()
            is HomeViewAction.UpdateMyself -> handleUpdateMySelf(action.user)

            is HomeViewAction.GetNewPosts -> handleGetNewPosts(action.search)
            is HomeViewAction.CommentPosts -> handleCommentPosts(action.postId, action.comment)
            is HomeViewAction.LikePosts -> handleLikePosts(action.postId, action.like)
        }
    }

    private fun handleLikePosts(postId: Int, like: LikesDtoReq) {
        setState { copy(asyncNewPosts = Uninitialized, asyncLikePosts = Loading()) }
        postsRepo.likePosts(postId, like).execute {
            copy(asyncLikePosts = it)
        }
    }

    private fun handleCommentPosts(postId: Int, commentReq: CommentsDtoReq) {
        setState { copy(asyncNewPosts = Uninitialized, asyncCommentPosts = Loading()) }
        postsRepo.commentPosts(postId, commentReq).execute {
            copy(asyncCommentPosts = it)
        }
    }

    private fun handleGetNewPosts(search: SearchDto) {
        setState { copy(asyncNewPosts = Loading(), asyncCommentPosts = Uninitialized, asyncLikePosts = Uninitialized) }
        postsRepo.getNewPosts(search).execute {
            copy(asyncNewPosts = it)
        }
    }

    private fun handleUpdateMySelf(user: UserDtoReq) {
        setState { copy(userCurrent = Uninitialized, asyncUpdateMyself = Loading()) }
        userRepo.updateMyself(user).execute { copy(asyncUpdateMyself = it) }
    }

    private fun handleGetNotifications() {
        setState { copy(asyncNotifications = Loading()) }
        notifRepo.getAllByUser().execute {
            copy(asyncNotifications = it)
        }
    }

    private fun handSearchByPage(search: SearchDto) {
        setState { copy(asyncPage = Loading()) }
        userRepo.searchByPage(search).execute {
            copy(asyncPage = it)
        }
    }

    private fun handGetTimeSheetsByUser() {
        setState { copy(timeSheets = Loading()) }
        timeSheetRepo.getAllByUser().execute {
            copy(timeSheets = it)
        }
    }

    private fun handGetTrackingsByUser() {
        setState { copy(trackings = Loading()) }
        trackingRepo.getAllByUser().execute {
            copy(trackings = it)
        }
    }

    private fun handSaveTracking(tracking: TrackingDtoReq) {
        setState { copy(asyncSaveTracking = Loading()) }
        trackingRepo.save(tracking).execute {
            copy(asyncSaveTracking = it)
        }
    }

    private fun handUpdateTracking(id: Int, tracking: TrackingDtoReq) {
        setState { copy(asyncUpdateTracking = Loading()) }
        trackingRepo.update(id, tracking).execute {
            copy(asyncUpdateTracking = it)
        }
    }

    private fun handCheckIn(ip: String) {
        setState { copy(asyncCheckIn = Loading()) }
        timeSheetRepo.checkIn(ip).execute {
            copy(asyncCheckIn = it)
        }
    }

    private fun handLogout() {
        setState { copy(asyncLogout = Loading()) }
        tokenRepo.logout().execute {
            copy(asyncLogout = it)
        }
    }

    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguage)
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading(), asyncUpdateMyself = Uninitialized) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    fun clearUserPreferences() {
        this.viewModelScope.async {
            userRepo.clearUserPreferences()
        }
    }

    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val enumIpAddr = en.nextElement().inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddr = enumIpAddr.nextElement()
                    if (!inetAddr.isLoopbackAddress) {
                        return inetAddr.hostAddress?.toString()
                    }
                }
            }
        } catch (e: SocketException) {
            Log.e("Test Tracking", "getLocalIpAddress: " + e.message)
        }

        return null
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext, state: HomeViewState
        ): HomeViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}