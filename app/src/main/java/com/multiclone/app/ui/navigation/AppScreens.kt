package com.multiclone.app.ui.navigation

/**
 * Defines all the screens in the app
 */
sealed class AppScreens(val route: String) {
    object Home : AppScreens("home")
    object AppSelection : AppScreens("app_selection")
    object CloneConfig : AppScreens("clone_config")
    object Settings : AppScreens("settings")
    object About : AppScreens("about")
}