package com.example.ui.editor
import androidx.lifecycle.ViewModel
import kotlin.math.cos
import kotlin.math.sin
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
            val stepMs = 33L // ~30 fps tick
            while (_isPlaying.value) {
                val current = _playheadMs.value
                val next = current + stepMs
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

    fun addShapeLayer(shapeType: ShapeType, color: Long) {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = shapeType.displayName,
                layerType = LayerType.SHAPE.name,
                shapeType = shapeType.name,
                fillColor = color,
                strokeColor = 0x00000000,
                strokeWidth = 0f,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = _playheadMs.value,
                endTimeMs = (_playheadMs.value + 4000L).coerceAtMost(maxDuration)
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addTextLayer(text: String, fontSize: Float, textColor: Long) {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = if (text.length > 14) text.take(14) + "..." else text,
                layerType = LayerType.TEXT.name,
                textContent = text,
                fontSize = fontSize,
                textColor = textColor,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = _playheadMs.value,
                endTimeMs = (_playheadMs.value + 4000L).coerceAtMost(maxDuration)
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addMediaLayer(uri: String, type: LayerType, name: String) {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = name,
                layerType = type.name,
                sourceUri = uri,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = _playheadMs.value,
                endTimeMs = (_playheadMs.value + 5000L).coerceAtMost(maxDuration)
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addNullLayer() {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = "Null Controller",
                layerType = LayerType.NULL.name,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addAdjustmentLayer() {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = "Adjustment Layer",
                layerType = LayerType.ADJUSTMENT.name,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
        }
    }

    fun addGroupLayer() {
        viewModelScope.launch {
            val currentLayers = layers.value
            val maxTrack = currentLayers.maxOfOrNull { it.layer.trackIndex } ?: -1
            val maxOrder = currentLayers.maxOfOrNull { it.layer.orderIndex } ?: -1
            val maxDuration = project.value?.durationMs ?: 10000L

            val newLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = "Group Container",
                layerType = LayerType.GROUP.name,
                trackIndex = maxTrack + 1,
                orderIndex = maxOrder + 1,
                startTimeMs = 0L,
                endTimeMs = maxDuration
            )
            projectRepository.addLayer(newLayer)
            _selectedLayerId.value = newLayer.id
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
            fun cur(p: KeyframeProperty, d: Float) =
                KeyframeInterpolator.interpolateProperty(kf, p, t, d)

            val x = cur(KeyframeProperty.POSITION_X, 0f)
            val y = cur(KeyframeProperty.POSITION_Y, 0f)
            val sx = cur(KeyframeProperty.SCALE_X, 1f)
            val sy = cur(KeyframeProperty.SCALE_Y, 1f)
            val rot = cur(KeyframeProperty.ROTATION_Z, 0f)

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
            if (rotation != 0f) saveKf(layerId, KeyframeProperty.ROTATION_Z, t, rot + rotation)
        }
    }
    fun moveLayer(layerId: String, deltaX: Float, deltaY: Float) {
        viewModelScope.launch {
            val lwk = layers.value.firstOrNull { it.layer.id == layerId } ?: return@launch
            val curX = KeyframeInterpolator.interpolateProperty(
                lwk.keyframes, KeyframeProperty.POSITION_X, _playheadMs.value, defaultValue = 0f
            )
            val curY = KeyframeInterpolator.interpolateProperty(
                lwk.keyframes, KeyframeProperty.POSITION_Y, _playheadMs.value, defaultValue = 0f
            )
            val newX = curX + deltaX
            val newY = curY + deltaY

            // Insert or update keyframe at playhead position
            val kfX = KeyframeEntity(
                id = UUID.randomUUID().toString(),
                layerId = layerId,
                property = KeyframeProperty.POSITION_X.name,
                timeMs = _playheadMs.value,
                value = newX
            )
            val kfY = KeyframeEntity(
                id = UUID.randomUUID().toString(),
                layerId = layerId,
                property = KeyframeProperty.POSITION_Y.name,
                timeMs = _playheadMs.value,
                value = newY
            )
            projectRepository.addKeyframe(kfX)
            projectRepository.addKeyframe(kfY)
        }
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
