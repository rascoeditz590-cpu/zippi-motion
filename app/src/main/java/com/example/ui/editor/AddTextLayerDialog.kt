package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ZippiGradientButton
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZippiAmber
import com.example.ui.theme.ZippiPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextLayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (text: String, fontSize: Float, textColor: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textContent by remember { mutableStateOf("ZIPPI MOTION") }
    var fontSize by remember { mutableStateOf(48f) }
    var textColor by remember { mutableStateOf(0xFFFFFFFF) }

    val colors = listOf(
        0xFFFFFFFF to "White",
        0xFFFF2A85 to "Pink",
        0xFFFFAA00 to "Amber",
        0xFF00E5FF to "Cyan",
        0xFFA855F7 to "Purple",
        0xFF10B981 to "Lime",
        0xFFFF5252 to "Red"
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = ZippiPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Text Layer",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            // Text Input
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                label = { Text("Text Content") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_text_content_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StudioSurfaceVariant,
                    unfocusedContainerColor = StudioSurfaceVariant,
                    focusedBorderColor = ZippiPink,
                    unfocusedBorderColor = StudioCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Font Size Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Font Size", color = TextSecondary, fontSize = 13.sp)
                    Text(text = "${fontSize.toInt()} sp", color = ZippiAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = fontSize,
                    onValueChange = { fontSize = it },
                    valueRange = 18f..110f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZippiPink,
                        activeTrackColor = ZippiPink,
                        inactiveTrackColor = StudioCardBorder
                    ),
                    modifier = Modifier.testTag("font_size_slider")
                )
            }

            // Color Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Color:", color = TextMuted, fontSize = 13.sp)
                colors.forEach { (colorVal, _) ->
                    val isSelected = textColor == colorVal
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(colorVal))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else StudioCardBorder,
                                shape = CircleShape
                            )
                            .clickable { textColor = colorVal }
                    )
                }
            }

            // Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF09080E))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textContent.ifBlank { "Sample Text" },
                    color = Color(textColor),
                    fontSize = (fontSize * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            // Add Button
            ZippiGradientButton(
                text = "Add Text Layer to Timeline",
                onClick = {
                    onConfirm(textContent.ifBlank { "New Text" }, fontSize, textColor)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "confirm_add_text_button"
            )
        }
    }
}
