package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.model.KeyframeProperty
import com.example.data.model.LayerType
import com.example.data.model.ShapeType
import com.example.ui.shapes.VectorShapeView
import com.example.ui.theme.KeyframeDiamond
import com.example.ui.theme.SnappingIndicator
import com.example.ui.theme.ZippiAmber
import com.example.ui.theme.ZippiPink
import kotlin.math.roundToInt

@Composable
fun CanvasLayerRenderer(
    layers: List<LayerWithKeyframes>,
    playheadMs: Long,
    selectedLayerId: String?,
    onSelectLayer: (String) -> Unit,
    onMoveLayer: (layerId: String, deltaX: Float, deltaY: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        // Sort layers by orderIndex to preserve layer stack hierarchy
        val sortedLayers = layers.sortedBy { it.layer.orderIndex }

        for (lwk in sortedLayers) {
            val layer = lwk.layer
            val keyframes = lwk.keyframes

            // Visibility & Time window check
            if (!layer.isVisible) continue
            if (playheadMs !in layer.startTimeMs..layer.endTimeMs) continue

            // Evaluate transform values via keyframe interpolation
            val posX = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.POSITION_X, playheadMs, defaultValue = 0f
            )
            val posY = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.POSITION_Y, playheadMs, defaultValue = 0f
            )
            val scaleX = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.SCALE_X, playheadMs, defaultValue = 1.0f
            )
            val scaleY = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.SCALE_Y, playheadMs, defaultValue = 1.0f
            )
            val rotationZ = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.ROTATION_Z, playheadMs, defaultValue = 0f
            )
            val opacity = KeyframeInterpolator.interpolateProperty(
                keyframes, KeyframeProperty.OPACITY, playheadMs, defaultValue = 1.0f
            ).coerceIn(0f, 1f)

            val isSelected = layer.id == selectedLayerId
            val layerType = LayerType.fromString(layer.layerType)

            Box(
                modifier = Modifier
                    .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                    .graphicsLayer {
                        this.scaleX = scaleX
                        this.scaleY = scaleY
                        this.rotationZ = rotationZ
                        this.alpha = opacity
                    }
                    .testTag("canvas_layer_${layer.id}")
                    .clickable { onSelectLayer(layer.id) }
                    .then(
                        if (isSelected && !layer.isLocked) {
                            Modifier.pointerInput(layer.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    onMoveLayer(layer.id, dragAmount.x, dragAmount.y)
                                }
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Layer Content Render
                when (layerType) {
                    LayerType.SHAPE -> {
                        val shape = ShapeType.fromString(layer.shapeType ?: ShapeType.RECTANGLE.name)
                        VectorShapeView(
                            shapeType = shape,
                            fillColor = Color(layer.fillColor),
                            strokeColor = Color(layer.strokeColor),
                            strokeWidth = layer.strokeWidth.dp,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    LayerType.TEXT -> {
                        Text(
                            text = layer.textContent ?: "",
                            color = Color(layer.textColor),
                            fontSize = (layer.fontSize * 0.5f).sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    LayerType.IMAGE -> {
                        if (layer.sourceUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(layer.sourceUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = layer.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("PNG Image", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    LayerType.VIDEO -> {
                        Box(
                            modifier = Modifier
                                .size(160.dp, 100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1B4B))
                                .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color(0xFF818CF8))
                        }
                    }
                    LayerType.NULL -> {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.dp, ZippiAmber, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CenterFocusStrong,
                                    contentDescription = "Null Controller",
                                    tint = ZippiAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    LayerType.ADJUSTMENT -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x22FFAA00))
                        )
                    }
                    LayerType.AUDIO, LayerType.GROUP -> {
                        // Invisible on visual canvas
                    }
                }

                // Selection Box & Transform Handles
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(1.5.dp, ZippiPink, RoundedCornerShape(2.dp))
                    ) {
                        // Corner resize anchor dots
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ZippiPink)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ZippiPink)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ZippiPink)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ZippiPink)
                        )
                    }
                }
            }
        }
    }
}
