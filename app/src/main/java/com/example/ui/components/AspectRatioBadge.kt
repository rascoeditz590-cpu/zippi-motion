package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZippiPink
import com.example.ui.theme.ZippiPinkContainer

@Composable
fun AspectRatioBadge(
    ratio: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) ZippiPink else ZippiPinkContainer.copy(alpha = 0.6f),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) ZippiPink else Color(0xFF47283E),
                shape = shape
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = ratio,
            color = if (isSelected) Color.White else Color(0xFFFFD8E4),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
