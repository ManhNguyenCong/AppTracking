package com.oceantech.tracking.ui.security.viewmodel

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.UserDtoRes

data class SecurityViewState (
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var asyncSignIn: Async<UserDtoRes> = Uninitialized,
    var userCurrent: Async<UserDto> = Uninitialized
    ):MvRxState{
        fun isLoading()= asyncLogin is Loading
    }