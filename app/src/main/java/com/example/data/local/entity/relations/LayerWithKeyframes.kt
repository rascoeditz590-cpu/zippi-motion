package com.example.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.KeyframeEntity
import com.example.data.local.entity.LayerEntity

data class LayerWithKeyframes(
    @Embedded
    val layer: LayerEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "layerId"
    )
    val keyframes: List<KeyframeEntity> = emptyList()
)
