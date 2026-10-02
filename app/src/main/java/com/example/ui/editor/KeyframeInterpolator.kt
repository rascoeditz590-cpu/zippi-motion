package com.example.ui.editor

import com.example.data.local.entity.KeyframeEntity
import com.example.data.model.EasingType
import com.example.data.model.KeyframeProperty

object KeyframeInterpolator {

    fun interpolateProperty(
        keyframes: List<KeyframeEntity>,
        property: KeyframeProperty,
        timeMs: Long,
        defaultValue: Float
    ): Float {
        val propertyKeyframes = keyframes
            .filter { it.property == property.name }
            .sortedBy { it.timeMs }

        if (propertyKeyframes.isEmpty()) {
            return defaultValue
        }
        if (propertyKeyframes.size == 1 || timeMs <= propertyKeyframes.first().timeMs) {
            return propertyKeyframes.first().value
        }
        if (timeMs >= propertyKeyframes.last().timeMs) {
            return propertyKeyframes.last().value
        }

        // Find the segment [k1, k2] such that k1.timeMs <= timeMs <= k2.timeMs
        for (i in 0 until propertyKeyframes.size - 1) {
            val k1 = propertyKeyframes[i]
            val k2 = propertyKeyframes[i + 1]

            if (timeMs in k1.timeMs..k2.timeMs) {
                val span = (k2.timeMs - k1.timeMs).toFloat().coerceAtLeast(1f)
                val rawT = (timeMs - k1.timeMs).toFloat() / span
                val easing = EasingType.fromString(k1.easingType)
                val progress = easing.interpolate(rawT)
                return k1.value + (k2.value - k1.value) * progress
            }
        }

        return defaultValue
    }
}
