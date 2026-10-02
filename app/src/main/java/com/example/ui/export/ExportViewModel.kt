package com.example.ui.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ProjectEntity
import com.example.data.repository.DeviceCapabilityRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExportUiState(
    val selectedResolution: String = "1080p (Full HD)",
    val selectedCodec: String = "H.264 (AVC)",
    val selectedFps: Int = 60,
    val bitrateMbps: Int = 16,
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val isExportComplete: Boolean = false,
    val outputFilePath: String? = null
)

class ExportViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    private val deviceCapabilityRepository: DeviceCapabilityRepository
) : ViewModel() {

    val project: StateFlow<ProjectEntity?> = projectRepository.getProjectById(projectId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private var exportJob: Job? = null

    init {
        val caps = deviceCapabilityRepository.getCapabilities()
        val defaultFps = if (caps.supportedFps.contains(60)) 60 else (caps.supportedFps.lastOrNull() ?: 30)
        _uiState.value = _uiState.value.copy(selectedFps = defaultFps)
    }

    fun onResolutionSelected(res: String) {
        val defaultBitrate = when (res) {
            "720p (HD)" -> 8
            "1080p (Full HD)" -> 16
            "2K (1440p)" -> 28
            "4K (Ultra HD)" -> 45
            else -> 16
        }
        _uiState.value = _uiState.value.copy(selectedResolution = res, bitrateMbps = defaultBitrate)
    }

    fun onCodecSelected(codec: String) {
        _uiState.value = _uiState.value.copy(selectedCodec = codec)
    }

    fun onFpsSelected(fps: Int) {
        _uiState.value = _uiState.value.copy(selectedFps = fps)
    }

    fun onBitrateChanged(bitrate: Int) {
        _uiState.value = _uiState.value.copy(bitrateMbps = bitrate)
    }

    fun startExport() {
        if (_uiState.value.isExporting) return
        _uiState.value = _uiState.value.copy(
            isExporting = true,
            exportProgress = 0f,
            isExportComplete = false
        )

        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            // Simulated export progression until Media3 Transformer pipeline in Phase 7
            for (step in 1..100) {
                delay(30)
                _uiState.value = _uiState.value.copy(exportProgress = step / 100f)
            }
            _uiState.value = _uiState.value.copy(
                isExporting = false,
                isExportComplete = true,
                outputFilePath = "/storage/emulated/0/Movies/ZippiMotion/${project.value?.name ?: "video"}.mp4"
            )
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _uiState.value = _uiState.value.copy(isExporting = false, exportProgress = 0f)
    }
}
