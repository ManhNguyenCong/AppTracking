package com.oceantech.tracking.ui.home.viewmodel

import com.oceantech.tracking.core.NimpeViewEvents

sealed class HomeViewEvent:NimpeViewEvents{
    object ResetLanguage: HomeViewEvent()
    object SaveFeedback: HomeViewEvent()

    object SetNavUp: HomeViewEvent()
}