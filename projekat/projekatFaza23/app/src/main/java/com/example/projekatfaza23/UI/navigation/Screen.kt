package com.example.projekatfaza23.UI.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    object Login : Screen

    @Serializable
    object CreateRequest : Screen
}