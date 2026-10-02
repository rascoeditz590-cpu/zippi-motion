package com.example.ui.editor

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.entity.relations.LayerWithKeyframes
import com.example.data.model.LayerType
import com.example.data.model.ShapeType
import com.example.ui.components.AspectRatioBadge
import com.example.ui.shapes.ShapePickerDialog
import com.example.ui.theme.KeyframeDiamond
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDivider
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceHighlight
import com.example.ui.theme.StudioSurfaceVariant
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
import java.util.Locale

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToExport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val project by viewModel.project.collectAsStateWithLifecycle()
    val layers by viewModel.layers.collectAsStateWithLifecycle()
    val playheadMs by viewModel.playheadMs.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val selectedLayerId by viewModel.selectedLayerId.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    var showAddLayerSheet by remember { mutableStateOf(false) }
    var showShapePicker by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }

    // Zero-permission Android Photo Picker for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addMediaLayer(uri.toString(), LayerType.VIDEO, "Video Clip")
        }
    }

    // Zero-permission Android Photo Picker for Images / PNGs
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addMediaLayer(uri.toString(), LayerType.IMAGE, "Image Asset")
        }
    }

    // System Audio Picker
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addMediaLayer(uri.toString(), LayerType.AUDIO, "Audio Track")
        }
    }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StudioBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ZippiPink)
        }
        return
    }

    val proj = project!!
    val durationMs = proj.durationMs.coerceAtLeast(1000L)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Workspace Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("editor_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Projects",
                    tint = TextPrimary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = proj.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AspectRatioBadge(ratio = proj.aspectRatio)
                }
                Text(
                    text = "${proj.width}x${proj.height} · ${proj.fps}fps",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(onClick = { /* Undo in Phase 3 */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = TextSecondary
                )
            }

            IconButton(onClick = { /* Redo in Phase 3 */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { onNavigateToExport(proj.id) },
                modifier = Modifier
                    .testTag("editor_export_button")
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZippiPink)
            ) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = "Export",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Viewport / Video Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.95f)
                .background(Color(0xFF07060A))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val ratioFloat = when (proj.aspectRatio) {
                "16:9" -> 16f / 9f
                "9:16" -> 9f / 16f
                "1:1" -> 1f
                "4:5" -> 4f / 5f
                "4:3" -> 4f / 3f
                "3:4" -> 3f / 4f
                else -> 9f / 16f
            }

            Box(
                modifier = Modifier
                    .aspectRatio(ratioFloat)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(proj.backgroundColor))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Live Canvas Layer Renderer with transforms and keyframe interpolation!
                CanvasLayerRenderer(
                    layers = layers,
                    playheadMs = playheadMs,
                    selectedLayerId = selectedLayerId,
                    onSelectLayer = viewModel::selectLayer,
                    onMoveLayer = viewModel::moveLayer
                )
            }
        }

        // Playhead Timecode & Playback Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioSurface)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val curSec = playheadMs / 1000
            val curMs = (playheadMs % 1000) / 10
            val totalSec = durationMs / 1000
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d / %02d:00", curSec, curMs, totalSec),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )

            // Playback controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.seekTo(0L) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.FastRewind, contentDescription = "To Start", tint = TextSecondary)
                }

                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier
                        .testTag("play_pause_button")
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ZippiPink)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { viewModel.seekTo(durationMs) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "To End", tint = TextSecondary)
                }
            }

            // Add Layer Button
            IconButton(
                onClick = { showAddLayerSheet = true },
                modifier = Modifier
                    .testTag("add_layer_floating_button")
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ZippiAmber)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Layer",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Workspace Tab Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = StudioSurface,
            contentColor = ZippiPink,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = ZippiPink
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(StudioDivider)
                )
            }
        ) {
            val tabs = listOf("Timeline", "Inspector", "Keyframes", "Shaders")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { viewModel.setActiveTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == index) TextPrimary else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("editor_tab_$index")
                )
            }
        }

        // Workspace Pane
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.05f)
                .background(StudioBackground)
                .navigationBarsPadding()
        ) {
            when (activeTab) {
                0 -> MultiTrackTimelinePane(
                    layers = layers,
                    playheadMs = playheadMs,
                    durationMs = durationMs,
                    selectedLayerId = selectedLayerId,
                    onSelectLayer = viewModel::selectLayer,
                    onSeek = viewModel::seekTo,
                    onToggleVisibility = viewModel::toggleLayerVisibility,
                    onToggleLock = viewModel::toggleLayerLock,
                    onDeleteSelected = viewModel::deleteSelectedLayer
                )
                1 -> InspectorPane(selectedLayerId = selectedLayerId)
                2 -> KeyframePane(selectedLayerId = selectedLayerId)
                else -> ShadersPane()
            }
        }
    }

    // Add Layer Bottom Sheet
    if (showAddLayerSheet) {
        AddLayerBottomSheet(
            onDismiss = { showAddLayerSheet = false },
            onSelectOption = { layerType ->
                showAddLayerSheet = false
                when (layerType) {
                    LayerType.SHAPE -> showShapePicker = true
                    LayerType.TEXT -> showTextDialog = true
                    LayerType.VIDEO -> {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                    LayerType.IMAGE -> {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    LayerType.AUDIO -> {
                        audioPickerLauncher.launch("audio/*")
                    }
                    LayerType.NULL -> viewModel.addNullLayer()
                    LayerType.ADJUSTMENT -> viewModel.addAdjustmentLayer()
                    LayerType.GROUP -> viewModel.addGroupLayer()
                }
            }
        )
    }

    // Shape Picker Dialog
    if (showShapePicker) {
        ShapePickerDialog(
            onDismiss = { showShapePicker = false },
            onShapeSelected = { shapeType, color ->
                viewModel.addShapeLayer(shapeType, color)
            }
        )
    }

    // Text Layer Dialog
    if (showTextDialog) {
        AddTextLayerDialog(
            onDismiss = { showTextDialog = false },
            onConfirm = { text, fontSize, textColor ->
                viewModel.addTextLayer(text, fontSize, textColor)
            }
        )
    }
}

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
        // Timeline Header with Ruler & Delete action
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

        // Multi-track layer items
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
                            // Layer indicator pill & title
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

                            // Layer quick controls
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

@Composable
fun InspectorPane(selectedLayerId: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (selectedLayerId != null) "Layer Inspector Active" else "Select a layer to inspect transforms",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "X, Y, Z, Scale, Rotation, Opacity, Blend Modes & Masks configured for Phase 3 & 4.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun KeyframePane(selectedLayerId: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Keyframe & Curve Graph Editor",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "15+ Easing curves & cubic bezier interpolation engine configured in Room schema.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ShadersPane() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "OpenGL ES 3.0 Shader Effects",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Blur (20), Color (50+), Light, Distortion & 3D Shaders ready for Phase 5.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

