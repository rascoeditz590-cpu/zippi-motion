package com.example.data.repository

import com.example.data.local.entity.KeyframeEntity
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.local.entity.relations.ProjectWithLayers
import com.example.data.model.AspectRatioType
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<ProjectEntity>>
    fun getFavoriteProjects(): Flow<List<ProjectEntity>>
    fun getProjectById(id: String): Flow<ProjectEntity?>
    fun getProjectWithLayers(id: String): Flow<ProjectWithLayers?>
    fun getLayersWithKeyframes(projectId: String): Flow<List<LayerWithKeyframes>>

    suspend fun createProject(
        name: String,
        aspectRatio: AspectRatioType,
        width: Int,
        height: Int,
        fps: Int,
        durationMs: Long = 10000L
    ): ProjectEntity

    suspend fun duplicateProject(projectId: String): String?
    suspend fun deleteProject(projectId: String)
    suspend fun updateProject(project: ProjectEntity)
    suspend fun setFavorite(projectId: String, isFavorite: Boolean)

    suspend fun addLayer(layer: LayerEntity)
    suspend fun updateLayer(layer: LayerEntity)
    suspend fun deleteLayer(layerId: String)
    suspend fun setLayerVisibility(layerId: String, isVisible: Boolean)
    suspend fun setLayerLocked(layerId: String, isLocked: Boolean)
    suspend fun setLayerMuted(layerId: String, isMuted: Boolean)

    suspend fun addKeyframe(keyframe: KeyframeEntity)
    suspend fun deleteKeyframe(keyframeId: String)
}
