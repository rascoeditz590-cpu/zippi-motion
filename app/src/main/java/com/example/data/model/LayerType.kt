package com.example.data.model

enum class LayerType(val displayName: String) {
    VIDEO("Video"),
    IMAGE("Image"),
    AUDIO("Audio"),
    TEXT("Text"),
    SHAPE("Shape"),
    NULL("Null Controller"),
    GROUP("Group"),
    ADJUSTMENT("Adjustment Layer");

    val isMediaLayer: Boolean
        get() = this == VIDEO || this == IMAGE || this == AUDIO

    val hasVisualOutput: Boolean
        get() = this != AUDIO && this != NULL

    companion object {
        fun fromString(value: String): LayerType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: VIDEO
        }
    }
}
