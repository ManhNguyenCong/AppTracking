package com.oceantech.tracking.ui.security.viewmodel

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserDtoReq

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()

    data class SignInAction(val user: UserDtoReq): SecurityViewAction()
}