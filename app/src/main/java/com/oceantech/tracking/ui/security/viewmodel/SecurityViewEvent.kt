package com.oceantech.tracking.ui.security.viewmodel

import com.oceantech.tracking.core.NimpeViewEvents

sealed class SecurityViewEvent:NimpeViewEvents {
    object ReturnSigninEvent: SecurityViewEvent()
    object ReturnResetpassEvent: SecurityViewEvent()
}