package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.model.LayerType
import com.example.ui.theme.KeyframeDiamond
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TimelineRuler
import com.example.ui.theme.TrackAdjustmentColor
import com.example.ui.theme.TrackAudioColor
import com.example.ui.theme.TrackShapeColor
import com.example.ui.theme.TrackTextColor
import com.example.ui.theme.TrackVideoColor
import com.example.ui.theme.ZippiAmber
import com.example.ui.theme.ZippiPink

@Composable
fun MultiTrackTimelinePane(
    layers: List<LayerWithKeyframes>,
    playheadMs: Long,
    durationMs: Long,
    selectedLayerId: String?,
    onSelectLayer: (String?) -> Unit,
    onSeek: (Long) -> Unit,
    onToggleVisibility: (String) -> Unit,
    onToggleLock: (String) -> Unit,
    onDeleteSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(TimelineRuler.copy(alpha = 0.4f))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSeek((durationMs / 2).coerceAtLeast(0L)) }
            ) {
                Text(
                    text = "0s        2s        4s        6s        8s       10s",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (selectedLayerId != null) {
                IconButton(
                    onClick = onDeleteSelected,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("timeline_delete_layer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Selected Layer",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (layers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Timeline is empty. Tap '+' to add video, images, text, or shapes.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                layers.forEach { lwk ->
                    val layer = lwk.layer
                    val isSelected = layer.id == selectedLayerId
                    val type = LayerType.fromString(layer.layerType)

                    val typeColor = when (type) {
                        LayerType.VIDEO -> TrackVideoColor
                        LayerType.IMAGE -> Color(0xFF06B6D4)
                        LayerType.AUDIO -> TrackAudioColor
                        LayerType.TEXT -> TrackTextColor
                        LayerType.SHAPE -> TrackShapeColor
                        LayerType.ADJUSTMENT -> TrackAdjustmentColor
                        LayerType.NULL -> ZippiAmber
                        LayerType.GROUP -> ZippiPink
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StudioSurfaceHighlight else StudioSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ZippiPink else StudioCardBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectLayer(layer.id) }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 4.dp, height = 28.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(typeColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = layer.name,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${layer.layerType} · ${(layer.endTimeMs - layer.startTimeMs) / 1000f}s",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (lwk.keyframes.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = KeyframeDiamond,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${lwk.keyframes.size}",
                                            color = KeyframeDiamond,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onToggleVisibility(layer.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Visibility",
                                        tint = if (layer.isVisible) TextSecondary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleLock(layer.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "Toggle Lock",
                                        tint = if (layer.isLocked) ZippiAmber else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
