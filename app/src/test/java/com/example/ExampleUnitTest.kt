package com.example

import com.example.data.local.entity.KeyframeEntity
import com.example.data.model.AspectRatioType
import com.example.data.model.EasingType
import com.example.data.model.KeyframeProperty
import com.example.ui.editor.KeyframeInterpolator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAspectRatioDimensions() {
        val (w16_9, h16_9) = AspectRatioType.RATIO_16_9.calculateDimensions(1920)
        assertEquals(1920, w16_9)
        assertEquals(1080, h16_9)

        val (w9_16, h9_16) = AspectRatioType.RATIO_9_16.calculateDimensions(1920)
        assertEquals(1080, w9_16)
        assertEquals(1920, h9_16)

        val (w1_1, h1_1) = AspectRatioType.RATIO_1_1.calculateDimensions(1080)
        assertEquals(1080, w1_1)
        assertEquals(1080, h1_1)
    }

    @Test
    fun testEasingCurvesInterpolation() {
        assertEquals(0f, EasingType.LINEAR.interpolate(0f), 0.001f)
        assertEquals(1f, EasingType.LINEAR.interpolate(1f), 0.001f)
        assertEquals(0.5f, EasingType.LINEAR.interpolate(0.5f), 0.001f)

        // Ease Out Quad decelerates (at t=0.5 value should be > 0.5)
        assertTrue(EasingType.EASE_OUT_QUAD.interpolate(0.5f) > 0.5f)

        // Cubic Bezier interpolation
        val bezierVal = EasingType.CUBIC_BEZIER.interpolate(0.5f, 0.42f, 0.0f, 0.58f, 1.0f)
        assertTrue(bezierVal in 0f..1f)
    }

    @Test
    fun testKeyframeInterpolationEngine() {
        val keyframes = listOf(
            KeyframeEntity(
                layerId = "layer1",
                property = KeyframeProperty.POSITION_X.name,
                timeMs = 0L,
                value = 100f,
                easingType = EasingType.LINEAR.name
            ),
            KeyframeEntity(
                layerId = "layer1",
                property = KeyframeProperty.POSITION_X.name,
                timeMs = 1000L,
                value = 300f,
                easingType = EasingType.LINEAR.name
            )
        )

        // At t=0
        val v0 = KeyframeInterpolator.interpolateProperty(keyframes, KeyframeProperty.POSITION_X, 0L, 0f)
        assertEquals(100f, v0, 0.01f)

        // At t=500ms
        val v500 = KeyframeInterpolator.interpolateProperty(keyframes, KeyframeProperty.POSITION_X, 500L, 0f)
        assertEquals(200f, v500, 0.01f)

        // At t=1000ms
        val v1000 = KeyframeInterpolator.interpolateProperty(keyframes, KeyframeProperty.POSITION_X, 1000L, 0f)
        assertEquals(300f, v1000, 0.01f)

        // Fallback for property without keyframes
        val vScale = KeyframeInterpolator.interpolateProperty(keyframes, KeyframeProperty.SCALE_X, 500L, 1.0f)
        assertEquals(1.0f, vScale, 0.01f)
    }
}
