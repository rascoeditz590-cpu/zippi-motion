package com.example.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.ProjectEntity

data class ProjectWithLayers(
    @Embedded
    val project: ProjectEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val layers: List<LayerEntity> = emptyList()
)
