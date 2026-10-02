package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayerType
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrackAdjustmentColor
import com.example.ui.theme.TrackAudioColor
import com.example.ui.theme.TrackShapeColor
import com.example.ui.theme.TrackTextColor
import com.example.ui.theme.TrackVideoColor
import com.example.ui.theme.ZippiAmber
import com.example.ui.theme.ZippiPink

data class LayerOptionItem(
    val type: LayerType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddLayerBottomSheet(
    onDismiss: () -> Unit,
    onSelectOption: (LayerType) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val options = listOf(
        LayerOptionItem(
            type = LayerType.VIDEO,
            title = "Video Clip",
            description = "Import video from gallery",
            icon = Icons.Default.Movie,
            accentColor = TrackVideoColor
        ),
        LayerOptionItem(
            type = LayerType.IMAGE,
            title = "Image / PNG",
            description = "Photo or transparent asset",
            icon = Icons.Default.Image,
            accentColor = Color(0xFF06B6D4)
        ),
        LayerOptionItem(
            type = LayerType.TEXT,
            title = "Text Layer",
            description = "Kinetic typography & titles",
            icon = Icons.Default.TextFields,
            accentColor = TrackTextColor
        ),
        LayerOptionItem(
            type = LayerType.SHAPE,
            title = "Vector Shape",
            description = "40+ vector shapes & stars",
            icon = Icons.Default.Category,
            accentColor = TrackShapeColor
        ),
        LayerOptionItem(
            type = LayerType.AUDIO,
            title = "Audio Track",
            description = "Music, SFX or voiceover",
            icon = Icons.Default.Audiotrack,
            accentColor = TrackAudioColor
        ),
        LayerOptionItem(
            type = LayerType.NULL,
            title = "Null Controller",
            description = "Invisible parent for transforms",
            icon = Icons.Default.CenterFocusStrong,
            accentColor = ZippiAmber
        ),
        LayerOptionItem(
            type = LayerType.ADJUSTMENT,
            title = "Adjustment",
            description = "FX applied to all lower layers",
            icon = Icons.Default.Tune,
            accentColor = TrackAdjustmentColor
        ),
        LayerOptionItem(
            type = LayerType.GROUP,
            title = "Group Layer",
            description = "Composite multiple tracks",
            icon = Icons.Default.Folder,
            accentColor = ZippiPink
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioSurface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Add Layer to Timeline",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose a layer type to insert at playhead",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .testTag("add_layer_option_${item.type.name.lowercase()}")
                            .clip(RoundedCornerShape(14.dp))
                            .background(StudioSurfaceVariant)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectOption(item.type)
                            }
                            .padding(12.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(item.accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
