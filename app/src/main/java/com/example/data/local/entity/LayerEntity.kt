package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "layers",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["orderIndex"])
    ]
)
data class LayerEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val layerType: String, // LayerType name: VIDEO, IMAGE, AUDIO, TEXT, SHAPE, NULL, GROUP, ADJUSTMENT
    val trackIndex: Int = 0,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 5000L,
    val inPointMs: Long = 0L,
    val outPointMs: Long = 5000L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isMuted: Boolean = false,
    val parentId: String? = null, // Parent-to-null or parent layer for hierarchical transforms
    val blendMode: String = "NORMAL",
    val maskType: String = "NONE", // NONE, RECTANGLE, CIRCLE, VECTOR
    val maskFeather: Float = 0f,
    val maskInvert: Boolean = false,
    val sourceUri: String? = null,
    val textContent: String? = null,
    val fontFamily: String? = "Default",
    val fontSize: Float = 48f,
    val textColor: Long = 0xFFFFFFFF,
    val shapeType: String? = null,
    val fillColor: Long = 0xFFFF2A85,
    val strokeColor: Long = 0x00000000,
    val strokeWidth: Float = 0f,
    val effectsJson: String = "[]",
    val orderIndex: Int = 0
)
