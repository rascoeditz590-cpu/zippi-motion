package com.example.ui.export

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val durationSec = (project?.durationMs ?: 10000L) / 1000L
    val estSizeBytes = (uiState.bitrateMbps * 1_000_000L / 8L) * durationSec
    val estSizeMB = String.format("%.1f", estSizeBytes / (1024.0 * 1024.0))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        ZippiTopBar(
            title = "Export Video",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Project Summary Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StudioSurface)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = project?.name ?: "Video Project",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Aspect: ${project?.aspectRatio ?: "9:16"} · Duration: ${durationSec}s",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Export Progress Box if exporting or completed
            if (uiState.isExporting || uiState.isExportComplete) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StudioSurfaceVariant)
                        .border(1.dp, if (uiState.isExportComplete) Color(0xFF4ADE80) else ZippiPink, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.isExportComplete) "Export Complete!" else "Rendering Motion Frames...",
                                color = if (uiState.isExportComplete) Color(0xFF4ADE80) else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(uiState.exportProgress * 100).toInt()}%",
                                color = ZippiAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        LinearProgressIndicator(
                            progress = { uiState.exportProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (uiState.isExportComplete) Color(0xFF4ADE80) else ZippiPink,
                            trackColor = StudioBackground
                        )

                        if (uiState.isExportComplete) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Saved to Gallery / Movies / ZippiMotion",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Resolution Presets
            Column {
                Text(
                    text = "Export Resolution",
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
                    val resolutions = listOf("720p (HD)", "1080p (Full HD)", "2K (1440p)", "4K (Ultra HD)")
                    resolutions.forEach { res ->
                        val isSelected = uiState.selectedResolution == res
                        Box(
                            modifier = Modifier
                                .testTag("export_res_${res.take(4)}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZippiPink.copy(alpha = 0.25f) else StudioSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ZippiPink else StudioCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onResolutionSelected(res) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = res,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Video Codec (H.264 vs H.265)
            Column {
                Text(
                    text = "Video Codec",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val codecs = listOf("H.264 (AVC)", "H.265 (HEVC)")
                    codecs.forEach { codec ->
                        val isSelected = uiState.selectedCodec == codec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("codec_chip_${codec.take(5)}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF382300) else StudioSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ZippiAmber else StudioCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onCodecSelected(codec) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = codec,
                                color = if (isSelected) ZippiAmber else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Target FPS
            Column {
                Text(
                    text = "Frame Rate",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val fpsOptions = listOf(24, 30, 60)
                    fpsOptions.forEach { fps ->
                        val isSelected = uiState.selectedFps == fps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_fps_${fps}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZippiPink.copy(alpha = 0.2f) else StudioSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ZippiPink else StudioCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onFpsSelected(fps) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$fps FPS",
                                color = if (isSelected) ZippiPink else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Bitrate Slider & Estimated Size
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Bitrate",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${uiState.bitrateMbps} Mbps (Est: ~$estSizeMB MB)",
                        color = ZippiAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = uiState.bitrateMbps.toFloat(),
                    onValueChange = { viewModel.onBitrateChanged(it.toInt()) },
                    valueRange = 4f..60f,
                    steps = 56,
                    colors = SliderDefaults.colors(
                        thumbColor = ZippiPink,
                        activeTrackColor = ZippiPink,
                        inactiveTrackColor = StudioCardBorder
                    ),
                    modifier = Modifier.testTag("export_bitrate_slider")
                )
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
                text = if (uiState.isExporting) "Rendering..." else "Start Export",
                onClick = viewModel::startExport,
                enabled = !uiState.isExporting,
                icon = Icons.Default.FileUpload,
                modifier = Modifier.fillMaxWidth(),
                testTag = "start_export_button"
            )
        }
    }
}
