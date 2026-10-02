package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ProjectEntity
import com.example.data.model.UserProfile
import com.example.data.repository.ProjectRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val favoriteProjects: List<ProjectEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedTab: Int = 0 // 0: All, 1: Favorites
)

class HomeViewModel(
    private val projectRepository: ProjectRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val userProfile: StateFlow<UserProfile> = userPreferencesRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile(id = "guest", displayName = "Guest Creator", email = "guest@zippimotion.app")
        )

    val uiState: StateFlow<HomeUiState> = combine(
        projectRepository.getAllProjects(),
        projectRepository.getFavoriteProjects(),
        _searchQuery,
        _selectedTab
    ) { allProjects, favoriteProjects, query, tab ->
        val listToFilter = if (tab == 1) favoriteProjects else allProjects
        val filtered = if (query.isBlank()) {
            listToFilter
        } else {
            listToFilter.filter { it.name.contains(query, ignoreCase = true) }
        }
        HomeUiState(
            projects = filtered,
            favoriteProjects = favoriteProjects,
            searchQuery = query,
            selectedTab = tab,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTabSelected(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun toggleFavorite(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepository.setFavorite(project.id, !project.isFavorite)
        }
    }

    fun duplicateProject(projectId: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val newId = projectRepository.duplicateProject(projectId)
            if (newId != null) {
                onComplete(newId)
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }
}
