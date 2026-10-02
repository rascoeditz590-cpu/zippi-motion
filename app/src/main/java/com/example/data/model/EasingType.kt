package com.example.data.model

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class EasingType(val displayName: String) {
    LINEAR("Linear"),
    EASE_IN_QUAD("Ease In Quad"),
    EASE_OUT_QUAD("Ease Out Quad"),
    EASE_IN_OUT_QUAD("Ease In-Out Quad"),
    EASE_IN_CUBIC("Ease In Cubic"),
    EASE_OUT_CUBIC("Ease Out Cubic"),
    EASE_IN_OUT_CUBIC("Ease In-Out Cubic"),
    EASE_IN_QUART("Ease In Quart"),
    EASE_OUT_QUART("Ease Out Quart"),
    EASE_IN_EXPO("Ease In Expo"),
    EASE_OUT_EXPO("Ease Out Expo"),
    EASE_IN_OUT_EXPO("Ease In-Out Expo"),
    EASE_IN_BACK("Ease In Back (Anticipate)"),
    EASE_OUT_BACK("Ease Out Back (Overshoot)"),
    EASE_IN_OUT_BACK("Ease In-Out Back"),
    EASE_OUT_BOUNCE("Ease Out Bounce"),
    EASE_IN_OUT_BOUNCE("Ease In-Out Bounce"),
    ELASTIC_OUT("Elastic Out"),
    CUBIC_BEZIER("Custom Cubic Bezier");

    /**
     * Calculates the interpolated progression t in [0.0, 1.0].
     */
    fun interpolate(t: Float, p1x: Float = 0.25f, p1y: Float = 0.1f, p2x: Float = 0.25f, p2y: Float = 1.0f): Float {
        val clampedT = t.coerceIn(0f, 1f)
        return when (this) {
            LINEAR -> clampedT
            EASE_IN_QUAD -> clampedT * clampedT
            EASE_OUT_QUAD -> clampedT * (2f - clampedT)
            EASE_IN_OUT_QUAD -> if (clampedT < 0.5f) 2f * clampedT * clampedT else -1f + (4f - 2f * clampedT) * clampedT
            EASE_IN_CUBIC -> clampedT.pow(3)
            EASE_OUT_CUBIC -> (clampedT - 1f).pow(3) + 1f
            EASE_IN_OUT_CUBIC -> if (clampedT < 0.5f) 4f * clampedT.pow(3) else (clampedT - 1f) * (2f * clampedT - 2f) * (2f * clampedT - 2f) + 1f
            EASE_IN_QUART -> clampedT.pow(4)
            EASE_OUT_QUART -> 1f - (clampedT - 1f).pow(4)
            EASE_IN_EXPO -> if (clampedT == 0f) 0f else 2.0.pow(10.0 * (clampedT - 1.0)).toFloat()
            EASE_OUT_EXPO -> if (clampedT == 1f) 1f else 1f - 2.0.pow(-10.0 * clampedT.toDouble()).toFloat()
            EASE_IN_OUT_EXPO -> when {
                clampedT == 0f -> 0f
                clampedT == 1f -> 1f
                clampedT < 0.5f -> (2.0.pow(20.0 * clampedT.toDouble() - 10.0) / 2.0).toFloat()
                else -> ((2.0 - 2.0.pow(-20.0 * clampedT.toDouble() + 10.0)) / 2.0).toFloat()
            }
            EASE_IN_BACK -> {
                val c1 = 1.70158f
                val c3 = c1 + 1f
                c3 * clampedT * clampedT * clampedT - c1 * clampedT * clampedT
            }
            EASE_OUT_BACK -> {
                val c1 = 1.70158f
                val c3 = c1 + 1f
                1f + c3 * (clampedT - 1f).pow(3) + c1 * (clampedT - 1f).pow(2)
            }
            EASE_IN_OUT_BACK -> {
                val c1 = 1.70158f
                val c2 = c1 * 1.525f
                if (clampedT < 0.5f) {
                    ((2f * clampedT).pow(2) * ((c2 + 1f) * 2f * clampedT - c2)) / 2f
                } else {
                    ((2f * clampedT - 2f).pow(2) * ((c2 + 1f) * (clampedT * 2f - 2f) + c2) + 2f) / 2f
                }
            }
            EASE_OUT_BOUNCE -> easeOutBounce(clampedT)
            EASE_IN_OUT_BOUNCE -> {
                if (clampedT < 0.5f) {
                    (1f - easeOutBounce(1f - 2f * clampedT)) / 2f
                } else {
                    (1f + easeOutBounce(2f * clampedT - 1f)) / 2f
                }
            }
            ELASTIC_OUT -> {
                val c4 = (2.0 * PI) / 3.0
                when (clampedT) {
                    0f -> 0f
                    1f -> 1f
                    else -> (2.0.pow(-10.0 * clampedT.toDouble()) * sin((clampedT * 10.0 - 0.75) * c4) + 1.0).toFloat()
                }
            }
            CUBIC_BEZIER -> sampleCubicBezier(clampedT, p1x, p1y, p2x, p2y)
        }
    }

    private fun easeOutBounce(x: Float): Float {
        val n1 = 7.5625f
        val d1 = 2.75f
        return when {
            x < 1f / d1 -> n1 * x * x
            x < 2f / d1 -> {
                val x2 = x - 1.5f / d1
                n1 * x2 * x2 + 0.75f
            }
            x < 2.5f / d1 -> {
                val x2 = x - 2.25f / d1
                n1 * x2 * x2 + 0.9375f
            }
            else -> {
                val x2 = x - 2.625f / d1
                n1 * x2 * x2 + 0.984375f
            }
        }
    }

    private fun sampleCubicBezier(t: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        // Approximate Newton-Raphson or binary search for t
        var low = 0f
        var high = 1f
        var currentT = t
        for (i in 0 until 8) {
            val currentX = evaluateBezierComponent(currentT, x1, x2)
            if (kotlin.math.abs(currentX - t) < 0.001f) break
            if (currentX < t) low = currentT else high = currentT
            currentT = (low + high) * 0.5f
        }
        return evaluateBezierComponent(currentT, y1, y2)
    }

    private fun evaluateBezierComponent(t: Float, p1: Float, p2: Float): Float {
        val u = 1f - t
        return 3f * u * u * t * p1 + 3f * u * t * t * p2 + t * t * t
    }

    companion object {
        fun fromString(value: String): EasingType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: LINEAR
        }
    }
}
