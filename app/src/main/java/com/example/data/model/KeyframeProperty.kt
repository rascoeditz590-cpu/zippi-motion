package com.example.data.model

enum class KeyframeProperty(
    val displayName: String,
    val defaultValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val unit: String
) {
    POSITION_X("Position X", 0f, -4000f, 4000f, "px"),
    POSITION_Y("Position Y", 0f, -4000f, 4000f, "px"),
    POSITION_Z("Position Z", 0f, -2000f, 2000f, "px"),
    SCALE_X("Scale X", 1.0f, 0.0f, 10.0f, "x"),
    SCALE_Y("Scale Y", 1.0f, 0.0f, 10.0f, "x"),
    ROTATION_Z("Rotation Z", 0f, -7200f, 7200f, "°"),
    TILT_X("Tilt X (3D)", 0f, -180f, 180f, "°"),
    TILT_Y("Tilt Y (3D)", 0f, -180f, 180f, "°"),
    OPACITY("Opacity", 1.0f, 0.0f, 1.0f, "%"),
    EFFECT_PARAM("Effect Parameter", 0.0f, -100f, 100f, "");

    companion object {
        fun fromString(value: String): KeyframeProperty {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: POSITION_X
        }
    }
}
