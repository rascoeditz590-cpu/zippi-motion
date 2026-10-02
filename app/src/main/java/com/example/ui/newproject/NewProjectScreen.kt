package com.example.ui.newproject

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AspectRatioType
import com.example.ui.components.ZippiGradientButton
import com.example.ui.components.ZippiTopBar
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZippiAmber
import com.example.ui.theme.ZippiPink
import com.example.ui.theme.ZippiPinkContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewProjectScreen(
    viewModel: NewProjectViewModel,
    onNavigateBack: () -> Unit,
    onProjectCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val calculatedDimensions = uiState.selectedRatio.calculateDimensions(uiState.selectedResolution.longEdge)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        ZippiTopBar(
            title = "New Project",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Project Name Field
            Column {
                Text(
                    text = "Project Name",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.projectName,
                    onValueChange = viewModel::onProjectNameChanged,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurface,
                        unfocusedContainerColor = StudioSurface,
                        focusedBorderColor = ZippiPink,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Aspect Ratio Selector
            Column {
                Text(
                    text = "Canvas Aspect Ratio",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AspectRatioType.entries.forEach { ratio ->
                        val isSelected = uiState.selectedRatio == ratio
                        Box(
                            modifier = Modifier
                                .testTag("ratio_chip_${ratio.label}")
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ZippiPinkContainer else StudioSurface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ZippiPink else StudioCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.onAspectRatioSelected(ratio) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Miniature shape representation
                                Box(
                                    modifier = Modifier
                                        .size(width = (ratio.aspectWidth * 1.5).dp.coerceIn(8.dp, 22.dp), height = (ratio.aspectHeight * 1.5).dp.coerceIn(8.dp, 22.dp))
                                        .border(1.dp, if (isSelected) ZippiPink else TextMuted, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ratio.label,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Resolution Presets
            Column {
                Text(
                    text = "Resolution",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.availableResolutions.forEach { res ->
                        val isSelected = uiState.selectedResolution == res
                        Box(
                            modifier = Modifier
                                .testTag("resolution_chip_${res.label}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZippiPink.copy(alpha = 0.2f) else StudioSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ZippiPink else StudioCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onResolutionSelected(res) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = res.label,
                                color = if (isSelected) ZippiPink else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Frame Rate (Only hardware-supported encoder rates offered)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Frame Rate (Encoder Supported)",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${uiState.selectedFps} FPS",
                        color = ZippiAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.availableFps.forEach { fps ->
                        val isSelected = uiState.selectedFps == fps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fps_chip_${fps}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF382300) else StudioSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ZippiAmber else StudioCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onFpsSelected(fps) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$fps",
                                color = if (isSelected) ZippiAmber else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Initial Duration Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duration",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${uiState.durationSeconds} seconds",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Slider(
                    value = uiState.durationSeconds.toFloat(),
                    onValueChange = { viewModel.onDurationSecondsChanged(it.toInt()) },
                    valueRange = 2f..60f,
                    steps = 58,
                    colors = SliderDefaults.colors(
                        thumbColor = ZippiPink,
                        activeTrackColor = ZippiPink,
                        inactiveTrackColor = StudioCardBorder
                    ),
                    modifier = Modifier.testTag("duration_slider")
                )
            }

            // Canvas Background Color
            Column {
                Text(
                    text = "Canvas Background",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val colors = listOf(
                        0xFF0D0C13 to "Obsidian",
                        0xFF000000 to "Black",
                        0xFF1E1B2E to "Deep Indigo",
                        0xFFFFFFFF to "White"
                    )
                    colors.forEach { (colorValue, label) ->
                        val isSelected = uiState.backgroundColor == colorValue
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ZippiPink else StudioCardBorder,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.onBackgroundColorSelected(colorValue) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = label,
                                    tint = if (colorValue == 0xFFFFFFFF) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Summary specs banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioSurfaceVariant)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Output Specifications",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${calculatedDimensions.first} x ${calculatedDimensions.second} px",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioBackground)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${uiState.selectedFps} FPS",
                            color = ZippiAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Action Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioSurface)
                .border(width = 1.dp, color = StudioCardBorder)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            ZippiGradientButton(
                text = if (uiState.isCreating) "Creating Studio..." else "Create Project",
                onClick = { viewModel.createProject(onProjectCreated) },
                enabled = !uiState.isCreating,
                modifier = Modifier.fillMaxWidth(),
                testTag = "create_project_confirm_button"
            )
        }
    }
}
