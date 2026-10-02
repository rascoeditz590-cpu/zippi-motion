package com.example.data.repository

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log

data class DeviceVideoCapabilities(
    val supportedFps: List<Int>,
    val maxResolutionWidth: Int,
    val maxResolutionHeight: Int,
    val supports4K: Boolean,
    val supports60Fps: Boolean,
    val supports120Fps: Boolean,
    val supportedCodecs: List<String>
)

class DeviceCapabilityRepository {

    fun getCapabilities(): DeviceVideoCapabilities {
        val standardFps = mutableListOf(24, 30)
        var maxW = 1920
        var maxH = 1080
        var has60 = false
        var has120 = false
        var has4K = false
        val codecs = mutableListOf<String>()

        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (!info.isEncoder) continue

                for (type in info.supportedTypes) {
                    if (type.equals(MediaFormat.MIMETYPE_VIDEO_AVC, ignoreCase = true) ||
                        type.equals(MediaFormat.MIMETYPE_VIDEO_HEVC, ignoreCase = true)
                    ) {
                        codecs.add("${info.name} ($type)")
                        val caps = info.getCapabilitiesForType(type)
                        val videoCaps = caps.videoCapabilities ?: continue

                        val supportedWidths = videoCaps.supportedWidths
                        val supportedHeights = videoCaps.supportedHeights
                        if (supportedWidths.upper > maxW) maxW = supportedWidths.upper
                        if (supportedHeights.upper > maxH) maxH = supportedHeights.upper

                        if (maxW >= 3840 || maxH >= 3840) {
                            has4K = true
                        }

                        // Check frame rate capability for standard 1080p
                        val frameRateRange = videoCaps.supportedFrameRates
                        if (frameRateRange.upper >= 60) {
                            has60 = true
                        }
                        if (frameRateRange.upper >= 120) {
                            has120 = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DeviceCapability", "Error querying encoder capabilities", e)
        }

        // On emulators or modest devices, ensure safe defaults
        if (has60 || maxW >= 1920) standardFps.add(60)
        if (has120) standardFps.add(120)

        return DeviceVideoCapabilities(
            supportedFps = standardFps.distinct().sorted(),
            maxResolutionWidth = maxW,
            maxResolutionHeight = maxH,
            supports4K = has4K,
            supports60Fps = standardFps.contains(60),
            supports120Fps = standardFps.contains(120),
            supportedCodecs = codecs.distinct()
        )
    }
}
