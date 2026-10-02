package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.di.AppContainer
import com.example.di.AppViewModelFactory
import com.example.ui.aichat.AiChatScreen
import com.example.ui.aichat.AiChatViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.AuthViewModel
import com.example.ui.editor.EditorScreen
import com.example.ui.editor.EditorViewModel
import com.example.ui.export.ExportScreen
import com.example.ui.export.ExportViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.newproject.NewProjectScreen
import com.example.ui.newproject.NewProjectViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.profile.ProfileViewModel

@Composable
fun ZippiNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        }
    ) {
        composable(route = Screen.Home.route) {
            val factory = AppViewModelFactory(appContainer)
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToNewProject = { navController.navigate(Screen.NewProject.route) },
                onNavigateToEditor = { projectId ->
                    navController.navigate(Screen.Editor.createRoute(projectId))
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAiChat = { navController.navigate(Screen.AiChat.route) }
            )
        }

        composable(route = Screen.NewProject.route) {
            val factory = AppViewModelFactory(appContainer)
            val newProjectViewModel: NewProjectViewModel = viewModel(factory = factory)
            NewProjectScreen(
                viewModel = newProjectViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProjectCreated = { projectId ->
                    navController.navigate(Screen.Editor.createRoute(projectId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val factory = AppViewModelFactory(appContainer, projectId = projectId)
            val editorViewModel: EditorViewModel = viewModel(factory = factory)
            EditorScreen(
                viewModel = editorViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExport = { id ->
                    navController.navigate(Screen.Export.createRoute(id))
                }
            )
        }

        composable(route = Screen.Auth.route) {
            val factory = AppViewModelFactory(appContainer)
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            AuthScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAuthSuccess = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Profile.route) {
            val factory = AppViewModelFactory(appContainer)
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
            )
        }

        composable(
            route = Screen.Export.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val factory = AppViewModelFactory(appContainer, projectId = projectId)
            val exportViewModel: ExportViewModel = viewModel(factory = factory)
            ExportScreen(
                viewModel = exportViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.AiChat.route) {
            val factory = AppViewModelFactory(appContainer)
            val aiChatViewModel: AiChatViewModel = viewModel(factory = factory)
            AiChatScreen(
                viewModel = aiChatViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
