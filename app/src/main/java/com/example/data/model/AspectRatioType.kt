package com.example.data.model

enum class AspectRatioType(
    val label: String,
    val aspectWidth: Int,
    val aspectHeight: Int,
    val description: String
) {
    RATIO_16_9("16:9", 16, 9, "Landscape (YouTube, TV)"),
    RATIO_9_16("9:16", 9, 16, "Portrait (Reels, TikTok, Shorts)"),
    RATIO_1_1("1:1", 1, 1, "Square (Feed post)"),
    RATIO_4_5("4:5", 4, 5, "Portrait (Instagram Feed)"),
    RATIO_4_3("4:3", 4, 3, "Classic Standard"),
    RATIO_3_4("3:4", 3, 4, "Portrait Presentation");

    fun calculateDimensions(baseResolutionLongEdge: Int = 1920): Pair<Int, Int> {
        return if (aspectWidth >= aspectHeight) {
            val w = baseResolutionLongEdge
            val h = (baseResolutionLongEdge * aspectHeight / aspectWidth) / 2 * 2
            Pair(w, h)
        } else {
            val h = baseResolutionLongEdge
            val w = (baseResolutionLongEdge * aspectWidth / aspectHeight) / 2 * 2
            Pair(w, h)
        }
    }

    companion object {
        fun fromString(value: String): AspectRatioType {
            return entries.firstOrNull { it.label == value || it.name == value } ?: RATIO_9_16
        }
    }
}
