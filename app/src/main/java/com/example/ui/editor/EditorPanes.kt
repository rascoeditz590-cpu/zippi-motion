package com.example.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InspectorPane(selectedLayerId: String?) {
    PlaceholderPane(
        title = if (selectedLayerId != null) "Layer Inspector" else "Select a layer to inspect",
        subtitle = "Position, scale, rotation and opacity sliders coming next."
    )
}

@Composable
fun KeyframePane(selectedLayerId: String?) {
    PlaceholderPane(
        title = "Keyframe Editor",
        subtitle = "Easing curves and keyframe graph coming next."
    )
}

@Composable
fun ShadersPane() {
    PlaceholderPane(
        title = "Effects",
        subtitle = "Blur, color, glow and distortion effects coming later."
    )
}

@Composable
private fun PlaceholderPane(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
