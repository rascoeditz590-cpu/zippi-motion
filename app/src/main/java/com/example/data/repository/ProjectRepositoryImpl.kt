package com.example.data.repository

import com.example.data.local.dao.KeyframeDao
import com.example.data.local.dao.LayerDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.KeyframeEntity
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.local.entity.relations.ProjectWithLayers
import com.example.data.model.AspectRatioType
import com.example.data.model.EasingType
import com.example.data.model.KeyframeProperty
import com.example.data.model.LayerType
import com.example.data.model.ShapeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val layerDao: LayerDao,
    private val keyframeDao: KeyframeDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    override fun getFavoriteProjects(): Flow<List<ProjectEntity>> = projectDao.getFavoriteProjects()

    override fun getProjectById(id: String): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    override fun getProjectWithLayers(id: String): Flow<ProjectWithLayers?> =
        projectDao.getProjectWithLayers(id)

    override fun getLayersWithKeyframes(projectId: String): Flow<List<LayerWithKeyframes>> =
        layerDao.getLayersWithKeyframesForProject(projectId)

    override suspend fun createProject(
        name: String,
        aspectRatio: AspectRatioType,
        width: Int,
        height: Int,
        fps: Int,
        durationMs: Long
    ): ProjectEntity = withContext(ioDispatcher) {
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Untitled Project" },
            aspectRatio = aspectRatio.label,
            width = width,
            height = height,
            fps = fps,
            durationMs = durationMs,
            backgroundColor = 0xFF0D0C13,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertProject(project)

        // Add a default background shape layer and a welcome text layer
        val textLayer = LayerEntity(
            id = UUID.randomUUID().toString(),
            projectId = project.id,
            name = "Title Text",
            layerType = LayerType.TEXT.name,
            trackIndex = 0,
            startTimeMs = 0L,
            endTimeMs = durationMs,
            inPointMs = 0L,
            outPointMs = durationMs,
            textContent = "Zippi Motion",
            fontSize = 54f,
            textColor = 0xFFFFFFFF,
            orderIndex = 0
        )
        layerDao.insertLayer(textLayer)

        // Seed initial position and scale keyframes for the text
        val kfScale1 = KeyframeEntity(
            layerId = textLayer.id,
            property = KeyframeProperty.SCALE_X.name,
            timeMs = 0L,
            value = 0.5f,
            easingType = EasingType.EASE_OUT_BACK.name
        )
        val kfScale2 = KeyframeEntity(
            layerId = textLayer.id,
            property = KeyframeProperty.SCALE_X.name,
            timeMs = 800L,
            value = 1.0f,
            easingType = EasingType.LINEAR.name
        )
        val kfOpacity1 = KeyframeEntity(
            layerId = textLayer.id,
            property = KeyframeProperty.OPACITY.name,
            timeMs = 0L,
            value = 0.0f,
            easingType = EasingType.EASE_OUT_CUBIC.name
        )
        val kfOpacity2 = KeyframeEntity(
            layerId = textLayer.id,
            property = KeyframeProperty.OPACITY.name,
            timeMs = 500L,
            value = 1.0f,
            easingType = EasingType.LINEAR.name
        )
        keyframeDao.insertKeyframes(listOf(kfScale1, kfScale2, kfOpacity1, kfOpacity2))

        project
    }

    override suspend fun duplicateProject(projectId: String): String? = withContext(ioDispatcher) {
        val original = projectDao.getProjectByIdSync(projectId) ?: return@withContext null
        val newProjectId = UUID.randomUUID().toString()
        val clonedProject = original.copy(
            id = newProjectId,
            name = "${original.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertProject(clonedProject)

        val layers = layerDao.getLayersWithKeyframesForProject(projectId).first()
        for (lwk in layers) {
            val newLayerId = UUID.randomUUID().toString()
            val clonedLayer = lwk.layer.copy(
                id = newLayerId,
                projectId = newProjectId
            )
            layerDao.insertLayer(clonedLayer)

            val clonedKeyframes = lwk.keyframes.map { kf ->
                kf.copy(
                    id = UUID.randomUUID().toString(),
                    layerId = newLayerId
                )
            }
            if (clonedKeyframes.isNotEmpty()) {
                keyframeDao.insertKeyframes(clonedKeyframes)
            }
        }
        newProjectId
    }

    override suspend fun deleteProject(projectId: String) = withContext(ioDispatcher) {
        projectDao.deleteProjectById(projectId)
    }

    override suspend fun updateProject(project: ProjectEntity) = withContext(ioDispatcher) {
        projectDao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun setFavorite(projectId: String, isFavorite: Boolean) = withContext(ioDispatcher) {
        projectDao.setFavorite(projectId, isFavorite)
    }

    override suspend fun addLayer(layer: LayerEntity) = withContext(ioDispatcher) {
        layerDao.insertLayer(layer)
    }

    override suspend fun updateLayer(layer: LayerEntity) = withContext(ioDispatcher) {
        layerDao.updateLayer(layer)
    }

    override suspend fun deleteLayer(layerId: String) = withContext(ioDispatcher) {
        layerDao.deleteLayerById(layerId)
    }

    override suspend fun setLayerVisibility(layerId: String, isVisible: Boolean) = withContext(ioDispatcher) {
        layerDao.setLayerVisibility(layerId, isVisible)
    }

    override suspend fun setLayerLocked(layerId: String, isLocked: Boolean) = withContext(ioDispatcher) {
        layerDao.setLayerLocked(layerId, isLocked)
    }

    override suspend fun setLayerMuted(layerId: String, isMuted: Boolean) = withContext(ioDispatcher) {
        layerDao.setLayerMuted(layerId, isMuted)
    }

    override suspend fun addKeyframe(keyframe: KeyframeEntity) = withContext(ioDispatcher) {
        keyframeDao.insertKeyframe(keyframe)
    }

    override suspend fun deleteKeyframe(keyframeId: String) = withContext(ioDispatcher) {
        keyframeDao.deleteKeyframeById(keyframeId)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(ioDispatcher) {
        val existing = projectDao.getAllProjects().first()
        if (existing.isEmpty()) {
            val starterProject = ProjectEntity(
                id = UUID.randomUUID().toString(),
                name = "Cyberpunk Intro Glow",
                aspectRatio = "9:16",
                width = 1080,
                height = 1920,
                fps = 60,
                durationMs = 8000L,
                backgroundColor = 0xFF0D0C13,
                isFavorite = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            projectDao.insertProject(starterProject)

            // Shape Layer (Glowing Star)
            val starLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = starterProject.id,
                name = "Star Emblem",
                layerType = LayerType.SHAPE.name,
                trackIndex = 0,
                startTimeMs = 0L,
                endTimeMs = 8000L,
                shapeType = ShapeType.STAR_5.name,
                fillColor = 0xFFFF2A85,
                strokeColor = 0xFFFFAA00,
                strokeWidth = 4f,
                orderIndex = 0
            )
            layerDao.insertLayer(starLayer)

            // Text Layer
            val textLayer = LayerEntity(
                id = UUID.randomUUID().toString(),
                projectId = starterProject.id,
                name = "Cyberpunk Title",
                layerType = LayerType.TEXT.name,
                trackIndex = 1,
                startTimeMs = 500L,
                endTimeMs = 8000L,
                textContent = "FUTURE MOTION",
                fontSize = 52f,
                textColor = 0xFFF4F2FA,
                orderIndex = 1
            )
            layerDao.insertLayer(textLayer)

            // Sample keyframes: rotation & scale
            keyframeDao.insertKeyframes(
                listOf(
                    KeyframeEntity(
                        layerId = starLayer.id,
                        property = KeyframeProperty.ROTATION_Z.name,
                        timeMs = 0L,
                        value = 0f,
                        easingType = EasingType.EASE_IN_OUT_CUBIC.name
                    ),
                    KeyframeEntity(
                        layerId = starLayer.id,
                        property = KeyframeProperty.ROTATION_Z.name,
                        timeMs = 3000L,
                        value = 360f,
                        easingType = EasingType.EASE_IN_OUT_CUBIC.name
                    ),
                    KeyframeEntity(
                        layerId = textLayer.id,
                        property = KeyframeProperty.POSITION_Y.name,
                        timeMs = 500L,
                        value = 100f,
                        easingType = EasingType.EASE_OUT_BACK.name
                    ),
                    KeyframeEntity(
                        layerId = textLayer.id,
                        property = KeyframeProperty.POSITION_Y.name,
                        timeMs = 1500L,
                        value = 0f,
                        easingType = EasingType.LINEAR.name
                    )
                )
            )
        }
    }
}
