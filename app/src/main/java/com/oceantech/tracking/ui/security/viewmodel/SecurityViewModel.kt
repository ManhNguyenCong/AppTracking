package com.oceantech.tracking.ui.security.viewmodel

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.UserDtoReq
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo: UserRepository
) :
    TrackingViewModel<SecurityViewState, SecurityViewAction, SecurityViewEvent>(state) {

    override fun handle(action: SecurityViewAction) {
        when (action) {
            is SecurityViewAction.LogginAction -> handleLogin(action.userName, action.password)
            is SecurityViewAction.SaveTokenAction -> handleSaveToken(action.token)
            is SecurityViewAction.GetUserCurrent -> handleCurrentUser()
            is SecurityViewAction.SignInAction -> handleSignIn(action.user)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    private fun handleLogin(userName: String, password: String) {
        setState {
            copy(asyncLogin = Loading())
        }
        repository.login(userName, password).execute {
            copy(asyncLogin = it)
        }
    }

    private fun handleSaveToken(tokenResponse: TokenResponse) {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }
    }

    private fun handleSignIn(userReq: UserDtoReq) {
        setState { copy(asyncSignIn = Loading()) }
        repository.signIn(userReq).execute { copy(asyncSignIn = it) }
    }

    fun handleReturnSignin() {
        _viewEvents.post(SecurityViewEvent.ReturnSigninEvent)
    }

    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetpassEvent)
    }

    fun getString() = "test"

    fun saveUserInfo(user: UserDto) {
        viewModelScope.launch {
            userRepo.saveUserInfo(user)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            userRepo.clearUserPreferences()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: SecurityViewState): SecurityViewModel
    }

    companion object : MvRxViewModelFactory<SecurityViewModel, SecurityViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: SecurityViewState
        ): SecurityViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}