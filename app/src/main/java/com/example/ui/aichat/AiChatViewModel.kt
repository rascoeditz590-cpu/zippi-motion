package com.example.ui.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val suggestedPrompts: List<String> = listOf(
        "Suggest easing curves for energetic logo bounce",
        "How do I create a camera parallax effect with Null layer?",
        "Best GLSL shader combination for cyber glow",
        "Recommended frame rate for 4K motion graphics"
    )
)

class AiChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        AiChatUiState(
            messages = listOf(
                ChatMessage(
                    text = "Hello! I'm Zippi, your Motion Graphics & Timeline AI Assistant. How can I help you compose keyframes, shaders, or timing curves today?",
                    isUser = false
                )
            )
        )
    )
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun onInputTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage(customPrompt: String? = null) {
        val query = customPrompt ?: _uiState.value.inputText.trim()
        if (query.isBlank()) return

        val userMsg = ChatMessage(text = query, isUser = true)
        val updatedMessages = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            inputText = "",
            isSending = true
        )

        viewModelScope.launch {
            delay(800) // Simulating response from secure motion backend
            val aiResponse = generateAssistantAdvice(query)
            val assistantMsg = ChatMessage(text = aiResponse, isUser = false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + assistantMsg,
                isSending = false
            )
        }
    }

    private fun generateAssistantAdvice(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("bounce") || q.contains("easing") -> {
                "For an energetic bounce: Set property to Scale or Position Y. Use EASE_OUT_BOUNCE or EASE_OUT_BACK with an initial 1.25x overshoot over 400ms, followed by a settle curve to 1.0x over 200ms."
            }
            q.contains("null") || q.contains("parallax") -> {
                "To create a 3D parallax effect: Add a Null Controller layer. Link your background and foreground layers to the Null layer using the Inspector's 'Parent-to-Null' dropdown, then keyframe the Null's Position X and Tilt."
            }
            q.contains("shader") || q.contains("glow") -> {
                "For a vivid cyber glow: Chain the 'Deep Glow' shader (Radius: 24, Intensity: 1.8) with the 'Hue Shift' shader. Modulate the glow threshold keyframe at timeline beat drops."
            }
            q.contains("frame rate") || q.contains("fps") -> {
                "For high-end motion graphics and keyframe fluidity, 60 FPS is ideal. If exporting for social media reels, 1080x1920 @ 60 FPS with H.264 @ 16-20 Mbps delivers crisp, artifact-free playback."
            }
            else -> {
                "Great question! In Zippi Motion, you can apply this by combining multi-track layers with keyframe bezier curves. Check the Inspector tab to tweak parameters in real-time."
            }
        }
    }
}
