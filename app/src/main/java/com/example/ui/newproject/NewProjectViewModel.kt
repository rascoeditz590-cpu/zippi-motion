package com.example.ui.newproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AspectRatioType
import com.example.data.repository.DeviceCapabilityRepository
import com.example.data.repository.DeviceVideoCapabilities
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResolutionPreset(
    val label: String,
    val longEdge: Int
)

data class NewProjectUiState(
    val projectName: String = "Untitled Motion",
    val selectedRatio: AspectRatioType = AspectRatioType.RATIO_9_16,
    val selectedResolution: ResolutionPreset = ResolutionPreset("1080p (Full HD)", 1920),
    val availableResolutions: List<ResolutionPreset> = emptyList(),
    val selectedFps: Int = 60,
    val availableFps: List<Int> = listOf(24, 30, 60),
    val durationSeconds: Int = 10,
    val backgroundColor: Long = 0xFF0D0C13,
    val isCreating: Boolean = false,
    val createdProjectId: String? = null
)

class NewProjectViewModel(
    private val projectRepository: ProjectRepository,
    private val deviceCapabilityRepository: DeviceCapabilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewProjectUiState())
    val uiState: StateFlow<NewProjectUiState> = _uiState.asStateFlow()

    init {
        loadDeviceCapabilities()
    }

    private fun loadDeviceCapabilities() {
        val caps = deviceCapabilityRepository.getCapabilities()
        val resolutions = mutableListOf(
            ResolutionPreset("720p (HD)", 1280),
            ResolutionPreset("1080p (Full HD)", 1920)
        )
        if (caps.maxResolutionWidth >= 2560 || caps.maxResolutionHeight >= 2560) {
            resolutions.add(ResolutionPreset("2K (1440p)", 2560))
        }
        if (caps.supports4K) {
            resolutions.add(ResolutionPreset("4K (Ultra HD)", 3840))
        }

        val fpsList = caps.supportedFps.ifEmpty { listOf(24, 30, 60) }
        val defaultFps = if (fpsList.contains(60)) 60 else (fpsList.lastOrNull() ?: 30)

        _uiState.value = _uiState.value.copy(
            availableResolutions = resolutions,
            availableFps = fpsList,
            selectedFps = defaultFps
        )
    }

    fun onProjectNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(projectName = name)
    }

    fun onAspectRatioSelected(ratio: AspectRatioType) {
        _uiState.value = _uiState.value.copy(selectedRatio = ratio)
    }

    fun onResolutionSelected(res: ResolutionPreset) {
        _uiState.value = _uiState.value.copy(selectedResolution = res)
    }

    fun onFpsSelected(fps: Int) {
        _uiState.value = _uiState.value.copy(selectedFps = fps)
    }

    fun onDurationSecondsChanged(sec: Int) {
        _uiState.value = _uiState.value.copy(durationSeconds = sec.coerceIn(1, 3600))
    }

    fun onBackgroundColorSelected(color: Long) {
        _uiState.value = _uiState.value.copy(backgroundColor = color)
    }

    fun createProject(onCreated: (String) -> Unit) {
        val state = _uiState.value
        _uiState.value = state.copy(isCreating = true)
        viewModelScope.launch {
            val dimensions = state.selectedRatio.calculateDimensions(state.selectedResolution.longEdge)
            val project = projectRepository.createProject(
                name = state.projectName.ifBlank { "Untitled Project" },
                aspectRatio = state.selectedRatio,
                width = dimensions.first,
                height = dimensions.second,
                fps = state.selectedFps,
                durationMs = state.durationSeconds * 1000L
            )
            _uiState.value = _uiState.value.copy(isCreating = false, createdProjectId = project.id)
            onCreated(project.id)
        }
    }
}
