package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "keyframes",
    foreignKeys = [
        ForeignKey(
            entity = LayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["layerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["layerId"]),
        Index(value = ["layerId", "property", "timeMs"])
    ]
)
data class KeyframeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val layerId: String,
    val property: String, // KeyframeProperty: POSITION_X, POSITION_Y, SCALE_X, ROTATION_Z, OPACITY, etc.
    val timeMs: Long,
    val value: Float,
    val easingType: String = "EASE_IN_OUT_CUBIC",
    val bezierControlPoints: String? = null // e.g., "0.42,0.0,0.58,1.0"
)
