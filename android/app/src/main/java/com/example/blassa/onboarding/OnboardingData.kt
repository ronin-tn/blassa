package com.example.blassa.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.blassa.R

data class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

val onboardingPages = listOf(
    OnboardingPage( // Screen 1: Map
        title = R.string.onboarding_title_1,
        description = R.string.onboarding_desc_1,
        image = R.drawable.onboarding_map_exact
    ),
    OnboardingPage( // Screen 2: Car
        title = R.string.onboarding_title_2,
        description = R.string.onboarding_desc_2,
        image = R.drawable.onboarding_car_exact
    ),
    OnboardingPage( // Screen 3: Woman
        title = R.string.onboarding_title_3,
        description = R.string.onboarding_desc_3,
        image = R.drawable.onboarding_woman_exact
    )
)
