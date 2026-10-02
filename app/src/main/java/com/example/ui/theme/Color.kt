package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Neon Pink Palette
val ZippiPink = Color(0xFFFF2A85)
val ZippiPinkLight = Color(0xFFFF5EA3)
val ZippiPinkDark = Color(0xFFC70B5C)
val ZippiPinkContainer = Color(0xFF3E0B20)
val ZippiOnPinkContainer = Color(0xFFFFD8E4)

// Radiant Amber Accent Palette
val ZippiAmber = Color(0xFFFFAA00)
val ZippiAmberLight = Color(0xFFFFC043)
val ZippiAmberDark = Color(0xFFC77800)
val ZippiAmberContainer = Color(0xFF3C2400)
val ZippiOnAmberContainer = Color(0xFFFFE1A3)

// Supporting Accent (Coral / Electric Violet)
val ZippiCoral = Color(0xFFF43F5E)
val ZippiViolet = Color(0xFFA855F7)

// Immersive Dark Studio Canvas & Surfaces
val StudioBackground = Color(0xFF0D0C13)
val StudioSurface = Color(0xFF16151F)
val StudioSurfaceVariant = Color(0xFF211F2D)
val StudioSurfaceHighlight = Color(0xFF2C283B)
val StudioCardBorder = Color(0xFF332F45)
val StudioDivider = Color(0xFF222030)

// Text and Icon Content
val TextPrimary = Color(0xFFF4F2FA)
val TextSecondary = Color(0xFFA9A5BE)
val TextMuted = Color(0xFF716C85)

// Timeline / Keyframe Specific Colors
val TimelineRuler = Color(0xFF423D59)
val PlayheadPink = Color(0xFFFF2A85)
val KeyframeDiamond = Color(0xFFFFAA00)
val SnappingIndicator = Color(0xFF00E5FF)
val TrackVideoColor = Color(0xFF2563EB)
val TrackAudioColor = Color(0xFF10B981)
val TrackTextColor = Color(0xFF8B5CF6)
val TrackShapeColor = Color(0xFFF59E0B)
val TrackAdjustmentColor = Color(0xFFEC4899)

// Signature Gradients
val ZippiGradient = Brush.horizontalGradient(
    colors = listOf(ZippiPink, ZippiAmber)
)

val ZippiVerticalGradient = Brush.verticalGradient(
    colors = listOf(ZippiPink, ZippiAmber)
)

val ZippiCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF231F32), Color(0xFF181624))
)

val ZippiGlowBrush = Brush.radialGradient(
    colors = listOf(Color(0x33FF2A85), Color.Transparent)
)
