package com.example.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ui.aichat.AiChatViewModel
import com.example.ui.auth.AuthViewModel
import com.example.ui.editor.EditorViewModel
import com.example.ui.export.ExportViewModel
import com.example.ui.home.HomeViewModel
import com.example.ui.newproject.NewProjectViewModel
import com.example.ui.profile.ProfileViewModel

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val appContainer: AppContainer,
    private val projectId: String? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    projectRepository = appContainer.projectRepository,
                    userPreferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(NewProjectViewModel::class.java) -> {
                NewProjectViewModel(
                    projectRepository = appContainer.projectRepository,
                    deviceCapabilityRepository = appContainer.deviceCapabilityRepository
                ) as T
            }
            modelClass.isAssignableFrom(EditorViewModel::class.java) -> {
                requireNotNull(projectId) { "projectId is required for EditorViewModel" }
                EditorViewModel(
                    projectId = projectId,
                    projectRepository = appContainer.projectRepository
                ) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    userPreferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    userPreferencesRepository = appContainer.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(ExportViewModel::class.java) -> {
                requireNotNull(projectId) { "projectId is required for ExportViewModel" }
                ExportViewModel(
                    projectId = projectId,
                    projectRepository = appContainer.projectRepository,
                    deviceCapabilityRepository = appContainer.deviceCapabilityRepository
                ) as T
            }
            modelClass.isAssignableFrom(AiChatViewModel::class.java) -> {
                AiChatViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
