package com.zlagi.presentation.viewmodel.auth.onboarding

import android.content.Intent

class OnBoardingContract {

    sealed class OnBoardingEvent {
        data class GoogleSignInButtonClicked(val data: Intent) : OnBoardingEvent()
        object EmailSignInButtonClicked : OnBoardingEvent()
    }

    sealed class OnBoardingViewEffect {
        data class ShowSnackBarError(val message: Int): OnBoardingViewEffect()
        object NavigateToFeed: OnBoardingViewEffect()
        object NavigateToSignIn: OnBoardingViewEffect()
    }

    data class OnBoardingViewState(
        val loading: Boolean = false
    )
}
