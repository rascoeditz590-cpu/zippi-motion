package com.example.ui.editor

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.R

@Composable
fun VideoLayerPlayer(
    uri: String,
    localTimeMs: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exo = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            seekTo(localTimeMs.coerceAtLeast(0L))
        }
    }
    DisposableEffect(exo) { onDispose { exo.release() } }
    LaunchedEffect(exo, isPlaying) { exo.playWhenReady = isPlaying }
    LaunchedEffect(exo, localTimeMs, isPlaying) {
        if (!isPlaying) exo.seekTo(localTimeMs.coerceAtLeast(0L))
    }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            LayoutInflater.from(ctx).inflate(R.layout.zippi_player_view, null) as PlayerView
        },
        update = { it.player = exo }
    )
}
