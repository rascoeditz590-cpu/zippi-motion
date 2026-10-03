package com.example.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.KeyframeEntity
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.model.KeyframeProperty
import com.example.data.model.LayerType
import com.example.data.model.ShapeType
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

data class EditorUiState(
    val project: ProjectEntity? = null,
    val layers: List<LayerWithKeyframes> = emptyList(),
    val playheadMs: Long = 0L,
    val isPlaying: Boolean = false,
    val selectedLayerId: String? = null,
    val timelineZoom: Float = 1.0f,
    val activeTab: Int = 0
)

class EditorViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _playheadMs = MutableStateFlow(0L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedLayerId = MutableStateFlow<String?>(null)
    val selectedLayerId: StateFlow<String?> = _selectedLayerId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f)
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    val project: StateFlow<ProjectEntity?> = projectRepository.getProjectById(projectId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val layers: StateFlow<List<LayerWithKeyframes>> = projectRepository.getLayersWithKeyframes(projectId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var playbackJob: Job? = null

    fun togglePlayPause() {
        val nextPlayState = !_isPlaying.value
        _isPlaying.value = nextPlayState
        if (nextPlayState) {
            startPlayback()
        } else {
            playbackJob?.cancel()
        }
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val maxDuration = project.value?.durationMs ?: 10000L
            val stepMs = 33L
            while (_isPlaying.value) {
                val next = _playheadMs.value + stepMs
                if (next >= maxDuration) {
                    _playheadMs.value = 0L
                } else {
                    _playheadMs.value = next
                }
                delay(stepMs)
            }
        }
    }

    fun seekTo(timeMs: Long) {
        val maxDuration = project.value?.durationMs ?: 10000L
        _playheadMs.value = timeMs.coerceIn(0L, maxDuration)
    }

    fun selectLayer(layerId: String?) {
        _selectedLayerId.value = layerId
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 4.0f)
    }

    private fun insertLayer(
        make: (id: String, track: Int, order: Int, maxDuration: Long) -> LayerEntity
    ) {
        viewModelScope.launch {
            val current = layers.value
            val track = (current.maxOfOrNull { it.layer.trackIndex } ?: -1) + 1
            val order = (current.maxOfOrNull { it.layer.orderIndex } ?: -1) + 1
            val maxDuration = project.value?.durationMs ?: 10000L
            val newLayer = make(UUID.randomUUID().toString(), track, order, maxDuration)
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addShapeLayer(shapeType: ShapeType, color: Long) {
        val start = _playheadMs.value
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = shapeType.displayName,
                layerType = LayerType.SHAPE.name,
                shapeType = shapeType.name,
                fillColor = color,
                strokeColor = 0x00000000,
                strokeWidth = 0f,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = start,
                endTimeMs = (start + 4000L).coerceAtMost(maxDuration)
            )
        }
    }

    fun addTextLayer(text: String, fontSize: Float, textColor: Long) {
        val start = _playheadMs.value
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = if (text.length > 14) text.take(14) + "..." else text,
                layerType = LayerType.TEXT.name,
                textContent = text,
                fontSize = fontSize,
                textColor = textColor,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = start,
                endTimeMs = (start + 4000L).coerceAtMost(maxDuration)
            )
        }
    }

    fun addMediaLayer(uri: String, type: LayerType, name: String) {
        val start = _playheadMs.value
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = name,
                layerType = type.name,
                sourceUri = uri,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = start,
                endTimeMs = (start + 5000L).coerceAtMost(maxDuration)
            )
        }
    }

    fun addNullLayer() {
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = "Null Controller",
                layerType = LayerType.NULL.name,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
        }
    }

    fun addAdjustmentLayer() {
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = "Adjustment Layer",
                layerType = LayerType.ADJUSTMENT.name,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
        }
    }

    fun addGroupLayer() {
        insertLayer { id, track, order, maxDuration ->
            LayerEntity(
                id = id,
                projectId = projectId,
                name = "Group Container",
                layerType = LayerType.GROUP.name,
                trackIndex = track,
                orderIndex = order,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
        }
    }

    private suspend fun saveKf(layerId: String, prop: KeyframeProperty, t: Long, v: Float) {
        projectRepository.addKeyframe(
            KeyframeEntity(
                id = "$layerId:${prop.name}:$t",
                layerId = layerId,
                property = prop.name,
                timeMs = t,
                value = v
            )
        )
    }

    fun transformLayer(layerId: String, panX: Float, panY: Float, zoom: Float, rotation: Float) {
        viewModelScope.launch {
            val lwk = layers.value.firstOrNull { it.layer.id == layerId } ?: return@launch
            val t = _playheadMs.value
            val kf = lwk.keyframes

            val x = KeyframeInterpolator.interpolateProperty(kf, KeyframeProperty.POSITION_X, t, 0f)
            val y = KeyframeInterpolator.interpolateProperty(kf, KeyframeProperty.POSITION_Y, t, 0f)
            val sx = KeyframeInterpolator.interpolateProperty(kf, KeyframeProperty.SCALE_X, t, 1f)
            val sy = KeyframeInterpolator.interpolateProperty(kf, KeyframeProperty.SCALE_Y, t, 1f)
            val rot = KeyframeInterpolator.interpolateProperty(kf, KeyframeProperty.ROTATION_Z, t, 0f)

            val rad = Math.toRadians(rot.toDouble())
            val c = cos(rad).toFloat()
            val s = sin(rad).toFloat()
            val dx = panX * sx * c - panY * sy * s
            val dy = panX * sx * s + panY * sy * c

            saveKf(layerId, KeyframeProperty.POSITION_X, t, x + dx)
            saveKf(layerId, KeyframeProperty.POSITION_Y, t, y + dy)
            if (zoom != 1f) {
                saveKf(layerId, KeyframeProperty.SCALE_X, t, (sx * zoom).coerceIn(0.1f, 10f))
                saveKf(layerId, KeyframeProperty.SCALE_Y, t, (sy * zoom).coerceIn(0.1f, 10f))
            }
            if (rotation != 0f) {
                saveKf(layerId, KeyframeProperty.ROTATION_Z, t, rot + rotation)
            }
        }
    }

    fun moveLayer(layerId: String, deltaX: Float, deltaY: Float) {
        transformLayer(layerId, deltaX, deltaY, 1f, 0f)
    }

    fun deleteSelectedLayer() {
        val selectedId = _selectedLayerId.value ?: return
        viewModelScope.launch {
            projectRepository.deleteLayer(selectedId)
            _selectedLayerId.value = null
        }
    }

    fun toggleLayerVisibility(layerId: String) {
        viewModelScope.launch {
            val lwk = layers.value.firstOrNull { it.layer.id == layerId } ?: return@launch
            projectRepository.setLayerVisibility(layerId, !lwk.layer.isVisible)
        }
    }

    fun toggleLayerLock(layerId: String) {
        viewModelScope.launch {
            val lwk = layers.value.firstOrNull { it.layer.id == layerId } ?: return@launch
            projectRepository.setLayerLocked(layerId, !lwk.layer.isLocked)
        }
    }

    fun toggleLayerMute(layerId: String) {
        viewModelScope.launch {
            val lwk = layers.value.firstOrNull { it.layer.id == layerId } ?: return@launch
            projectRepository.setLayerMuted(layerId, !lwk.layer.isMuted)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}
