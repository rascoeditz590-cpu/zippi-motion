package com.example.ui.shapes

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import com.example.data.model.ShapeType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object VectorShapePathBuilder {

    fun buildPath(shapeType: ShapeType, width: Float, height: Float): Path {
        val w = width.coerceAtLeast(1f)
        val h = height.coerceAtLeast(1f)
        val path = Path()

        when (shapeType) {
            ShapeType.RECTANGLE -> {
                path.addRect(Rect(0f, 0f, w, h))
            }
            ShapeType.ROUNDED_RECT -> {
                val radius = min(w, h) * 0.15f
                path.addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(radius, radius)))
            }
            ShapeType.CIRCLE, ShapeType.ELLIPSE -> {
                path.addOval(Rect(0f, 0f, w, h))
            }
            ShapeType.TRIANGLE -> {
                path.moveTo(w * 0.5f, 0f)
                path.lineTo(w, h)
                path.lineTo(0f, h)
                path.close()
            }
            ShapeType.RIGHT_TRIANGLE -> {
                path.moveTo(0f, 0f)
                path.lineTo(w, h)
                path.lineTo(0f, h)
                path.close()
            }
            ShapeType.DIAMOND -> {
                path.moveTo(w * 0.5f, 0f)
                path.lineTo(w, h * 0.5f)
                path.lineTo(w * 0.5f, h)
                path.lineTo(0f, h * 0.5f)
                path.close()
            }
            ShapeType.PENTAGON -> buildRegularPolygonPath(path, 5, w, h)
            ShapeType.HEXAGON -> buildRegularPolygonPath(path, 6, w, h)
            ShapeType.OCTAGON -> buildRegularPolygonPath(path, 8, w, h)
            ShapeType.STAR_4 -> buildStarPath(path, 4, w, h, innerRadiusRatio = 0.4f)
            ShapeType.STAR_5 -> buildStarPath(path, 5, w, h, innerRadiusRatio = 0.45f)
            ShapeType.STAR_6 -> buildStarPath(path, 6, w, h, innerRadiusRatio = 0.5f)
            ShapeType.STAR_8 -> buildStarPath(path, 8, w, h, innerRadiusRatio = 0.55f)
            ShapeType.BURST_12 -> buildStarPath(path, 12, w, h, innerRadiusRatio = 0.75f)
            ShapeType.HEART -> {
                path.moveTo(w * 0.5f, h * 0.8f)
                path.cubicTo(w * 0.1f, h * 0.55f, 0f, h * 0.35f, 0f, h * 0.22f)
                path.cubicTo(0f, h * 0.05f, w * 0.2f, 0f, w * 0.38f, 0f)
                path.cubicTo(w * 0.48f, 0f, w * 0.5f, h * 0.08f, w * 0.5f, h * 0.12f)
                path.cubicTo(w * 0.5f, h * 0.08f, w * 0.52f, 0f, w * 0.62f, 0f)
                path.cubicTo(w * 0.8f, 0f, w, h * 0.05f, w, h * 0.22f)
                path.cubicTo(w, h * 0.35f, w * 0.9f, h * 0.55f, w * 0.5f, h * 0.8f)
                path.close()
            }
            ShapeType.CROSS -> {
                val armW = w * 0.3f
                val armH = h * 0.3f
                val x1 = (w - armW) / 2f
                val x2 = x1 + armW
                val y1 = (h - armH) / 2f
                val y2 = y1 + armH

                path.moveTo(x1, 0f)
                path.lineTo(x2, 0f)
                path.lineTo(x2, y1)
                path.lineTo(w, y1)
                path.lineTo(w, y2)
                path.lineTo(x2, y2)
                path.lineTo(x2, h)
                path.lineTo(x1, h)
                path.lineTo(x1, y2)
                path.lineTo(0f, y2)
                path.lineTo(0f, y1)
                path.lineTo(x1, y1)
                path.close()
            }
            ShapeType.MOON -> {
                val outer = Path().apply { addOval(Rect(0f, 0f, w, h)) }
                val inner = Path().apply { addOval(Rect(w * 0.25f, 0f, w * 1.15f, h * 0.95f)) }
                return Path().apply { op(outer, inner, PathOperation.Difference) }
            }
            ShapeType.SUN -> {
                val centerRadius = min(w, h) * 0.25f
                val cx = w * 0.5f
                val cy = h * 0.5f
                path.addOval(Rect(cx - centerRadius, cy - centerRadius, cx + centerRadius, cy + centerRadius))
                // Rays
                buildStarPath(path, 8, w, h, innerRadiusRatio = 0.65f)
            }
            ShapeType.LIGHTNING -> {
                path.moveTo(w * 0.55f, 0f)
                path.lineTo(w * 0.2f, h * 0.52f)
                path.lineTo(w * 0.48f, h * 0.52f)
                path.lineTo(w * 0.35f, h)
                path.lineTo(w * 0.8f, h * 0.42f)
                path.lineTo(w * 0.52f, h * 0.42f)
                path.close()
            }
            ShapeType.CLOUD -> {
                path.moveTo(w * 0.2f, h * 0.75f)
                path.cubicTo(0f, h * 0.75f, 0f, h * 0.45f, w * 0.2f, h * 0.45f)
                path.cubicTo(w * 0.15f, h * 0.2f, w * 0.45f, h * 0.15f, w * 0.5f, h * 0.3f)
                path.cubicTo(w * 0.6f, h * 0.15f, w * 0.85f, h * 0.2f, w * 0.82f, h * 0.45f)
                path.cubicTo(w, h * 0.45f, w, h * 0.75f, w * 0.8f, h * 0.75f)
                path.close()
            }
            ShapeType.FLOWER -> {
                val cx = w * 0.5f
                val cy = h * 0.5f
                val r = min(w, h) * 0.5f
                val petals = 6
                for (i in 0 until petals) {
                    val angle = (i * 2 * PI / petals).toFloat()
                    val px = cx + cos(angle) * (r * 0.55f)
                    val py = cy + sin(angle) * (r * 0.55f)
                    path.addOval(Rect(px - r * 0.35f, py - r * 0.35f, px + r * 0.35f, py + r * 0.35f))
                }
                path.addOval(Rect(cx - r * 0.25f, cy - r * 0.25f, cx + r * 0.25f, cy + r * 0.25f))
            }
            ShapeType.ARROW_RIGHT -> {
                path.moveTo(0f, h * 0.35f)
                path.lineTo(w * 0.6f, h * 0.35f)
                path.lineTo(w * 0.6f, h * 0.1f)
                path.lineTo(w, h * 0.5f)
                path.lineTo(w * 0.6f, h * 0.9f)
                path.lineTo(w * 0.6f, h * 0.65f)
                path.lineTo(0f, h * 0.65f)
                path.close()
            }
            ShapeType.ARROW_LEFT -> {
                path.moveTo(w, h * 0.35f)
                path.lineTo(w * 0.4f, h * 0.35f)
                path.lineTo(w * 0.4f, h * 0.1f)
                path.lineTo(0f, h * 0.5f)
                path.lineTo(w * 0.4f, h * 0.9f)
                path.lineTo(w * 0.4f, h * 0.65f)
                path.lineTo(w, h * 0.65f)
                path.close()
            }
            ShapeType.ARROW_UP -> {
                path.moveTo(w * 0.35f, h)
                path.lineTo(w * 0.35f, h * 0.4f)
                path.lineTo(w * 0.1f, h * 0.4f)
                path.lineTo(w * 0.5f, 0f)
                path.lineTo(w * 0.9f, h * 0.4f)
                path.lineTo(w * 0.65f, h * 0.4f)
                path.lineTo(w * 0.65f, h)
                path.close()
            }
            ShapeType.ARROW_DOWN -> {
                path.moveTo(w * 0.35f, 0f)
                path.lineTo(w * 0.35f, h * 0.6f)
                path.lineTo(w * 0.1f, h * 0.6f)
                path.lineTo(w * 0.5f, h)
                path.lineTo(w * 0.9f, h * 0.6f)
                path.lineTo(w * 0.65f, h * 0.6f)
                path.lineTo(w * 0.65f, 0f)
                path.close()
            }
            ShapeType.ARROW_DOUBLE -> {
                path.moveTo(w * 0.3f, 0f)
                path.lineTo(w * 0.3f, h * 0.3f)
                path.lineTo(w * 0.7f, h * 0.3f)
                path.lineTo(w * 0.7f, 0f)
                path.lineTo(w, h * 0.5f)
                path.lineTo(w * 0.7f, h)
                path.lineTo(w * 0.7f, h * 0.7f)
                path.lineTo(w * 0.3f, h * 0.7f)
                path.lineTo(w * 0.3f, h)
                path.lineTo(0f, h * 0.5f)
                path.close()
            }
            ShapeType.CHEVRON_RIGHT -> {
                path.moveTo(w * 0.2f, 0f)
                path.lineTo(w * 0.8f, h * 0.5f)
                path.lineTo(w * 0.2f, h)
                path.lineTo(w * 0.4f, h * 0.5f)
                path.close()
            }
            ShapeType.SPEECH_BUBBLE -> {
                val bubbleH = h * 0.75f
                val radius = min(w, bubbleH) * 0.2f
                path.addRoundRect(RoundRect(Rect(0f, 0f, w, bubbleH), CornerRadius(radius, radius)))
                path.moveTo(w * 0.2f, bubbleH)
                path.lineTo(w * 0.1f, h)
                path.lineTo(w * 0.38f, bubbleH)
                path.close()
            }
            ShapeType.THOUGHT_BUBBLE -> {
                val bubbleH = h * 0.78f
                path.addOval(Rect(0f, 0f, w, bubbleH))
                path.addOval(Rect(w * 0.18f, h * 0.82f, w * 0.28f, h * 0.92f))
                path.addOval(Rect(w * 0.12f, h * 0.94f, w * 0.18f, h * 1.0f))
            }
            ShapeType.CALLOUT_RECT -> {
                val bubbleH = h * 0.75f
                path.addRect(Rect(0f, 0f, w, bubbleH))
                path.moveTo(w * 0.25f, bubbleH)
                path.lineTo(w * 0.15f, h)
                path.lineTo(w * 0.4f, bubbleH)
                path.close()
            }
            ShapeType.BANNER_RIBBON -> {
                path.moveTo(0f, h * 0.2f)
                path.lineTo(w, h * 0.2f)
                path.lineTo(w * 0.85f, h * 0.5f)
                path.lineTo(w, h * 0.8f)
                path.lineTo(0f, h * 0.8f)
                path.lineTo(w * 0.15f, h * 0.5f)
                path.close()
            }
            ShapeType.SHIELD -> {
                path.moveTo(0f, 0f)
                path.lineTo(w, 0f)
                path.lineTo(w, h * 0.5f)
                path.cubicTo(w, h * 0.8f, w * 0.5f, h, w * 0.5f, h)
                path.cubicTo(w * 0.5f, h, 0f, h * 0.8f, 0f, h * 0.5f)
                path.close()
            }
            ShapeType.HEX_BADGE -> {
                buildRegularPolygonPath(path, 6, w, h)
            }
            ShapeType.CAPSULE -> {
                val radius = min(w, h) * 0.5f
                path.addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(radius, radius)))
            }
            ShapeType.RING -> {
                val outer = Path().apply { addOval(Rect(0f, 0f, w, h)) }
                val inner = Path().apply {
                    val insetX = w * 0.22f
                    val insetY = h * 0.22f
                    addOval(Rect(insetX, insetY, w - insetX, h - insetY))
                }
                return Path().apply { op(outer, inner, PathOperation.Difference) }
            }
            ShapeType.GEAR -> {
                val cx = w * 0.5f
                val cy = h * 0.5f
                val outerR = min(w, h) * 0.5f
                buildStarPath(path, 10, w, h, innerRadiusRatio = 0.72f)
                val hole = Path().apply {
                    val holeR = outerR * 0.35f
                    addOval(Rect(cx - holeR, cy - holeR, cx + holeR, cy + holeR))
                }
                return Path().apply { op(path, hole, PathOperation.Difference) }
            }
            ShapeType.TAG -> {
                val holeR = min(w, h) * 0.08f
                path.moveTo(w * 0.25f, 0f)
                path.lineTo(w, 0f)
                path.lineTo(w, h)
                path.lineTo(w * 0.25f, h)
                path.lineTo(0f, h * 0.5f)
                path.close()
                val hole = Path().apply {
                    addOval(Rect(w * 0.15f - holeR, h * 0.5f - holeR, w * 0.15f + holeR, h * 0.5f + holeR))
                }
                return Path().apply { op(path, hole, PathOperation.Difference) }
            }
            ShapeType.CHECKMARK -> {
                path.moveTo(0f, h * 0.55f)
                path.lineTo(w * 0.38f, h * 0.95f)
                path.lineTo(w, h * 0.1f)
                path.lineTo(w * 0.85f, 0f)
                path.lineTo(w * 0.38f, h * 0.72f)
                path.lineTo(w * 0.12f, h * 0.42f)
                path.close()
            }
            ShapeType.PARALLELOGRAM -> {
                val offset = w * 0.22f
                path.moveTo(offset, 0f)
                path.lineTo(w, 0f)
                path.lineTo(w - offset, h)
                path.lineTo(0f, h)
                path.close()
            }
            ShapeType.TRAPEZOID -> {
                val offset = w * 0.2f
                path.moveTo(offset, 0f)
                path.lineTo(w - offset, 0f)
                path.lineTo(w, h)
                path.lineTo(0f, h)
                path.close()
            }
        }
        return path
    }

    private fun buildRegularPolygonPath(path: Path, sides: Int, w: Float, h: Float) {
        val cx = w * 0.5f
        val cy = h * 0.5f
        val rx = w * 0.5f
        val ry = h * 0.5f
        val angleStep = (2 * PI / sides).toFloat()
        val startAngle = (-PI / 2).toFloat()

        for (i in 0 until sides) {
            val angle = startAngle + i * angleStep
            val x = cx + rx * cos(angle)
            val y = cy + ry * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }

    private fun buildStarPath(path: Path, points: Int, w: Float, h: Float, innerRadiusRatio: Float) {
        val cx = w * 0.5f
        val cy = h * 0.5f
        val outerRx = w * 0.5f
        val outerRy = h * 0.5f
        val innerRx = outerRx * innerRadiusRatio
        val innerRy = outerRy * innerRadiusRatio

        val totalPoints = points * 2
        val angleStep = (2 * PI / totalPoints).toFloat()
        val startAngle = (-PI / 2).toFloat()

        for (i in 0 until totalPoints) {
            val angle = startAngle + i * angleStep
            val rx = if (i % 2 == 0) outerRx else innerRx
            val ry = if (i % 2 == 0) outerRy else innerRy
            val x = cx + rx * cos(angle)
            val y = cy + ry * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }
}
