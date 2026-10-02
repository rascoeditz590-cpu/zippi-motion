package com.example.ui.shapes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.ShapeType

@Composable
fun VectorShapeView(
    shapeType: ShapeType,
    fillColor: Color,
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Transparent,
    strokeWidth: Dp = 0.dp
) {
    Canvas(modifier = modifier) {
        val path = VectorShapePathBuilder.buildPath(shapeType, size.width, size.height)

        if (fillColor != Color.Transparent) {
            drawPath(path = path, color = fillColor, style = Fill)
        }

        if (strokeColor != Color.Transparent && strokeWidth.toPx() > 0f) {
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth.toPx())
            )
        }
    }
}
