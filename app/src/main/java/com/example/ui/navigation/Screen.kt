package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object NewProject : Screen("new_project")
    data object Editor : Screen("editor/{projectId}") {
        fun createRoute(projectId: String) = "editor/$projectId"
    }
    data object Auth : Screen("auth")
    data object Profile : Screen("profile")
    data object Export : Screen("export/{projectId}") {
        fun createRoute(projectId: String) = "export/$projectId"
    }
    data object AiChat : Screen("ai_chat")
}
