package com.tsukinimev1.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextSecondary
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun PlayerScreen(
    animeId: String,
    episodeId: String,
    title: String,
    store: LocalStore,
    navController: NavController? = null,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var streamUrl by remember { mutableStateOf<String?>(null) }
    var displayTitle by remember { mutableStateOf(title) }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(episodeId) {
        loading = true
        error = null
        try {
            if (episodeId.isBlank()) {
                // detail tanpa episode → coba ambil episode list dulu
                val detail = ApiClient.fetchAnimeDetail(animeId)
                val first = detail?.episodeList?.firstOrNull()
                if (first != null) {
                    val ep = ApiClient.fetchEpisode(first.episodeId)
                    streamUrl = ep?.bestStreamUrl
                    displayTitle = first.title
                }
            } else {
                val ep = ApiClient.fetchEpisode(episodeId)
                streamUrl = ep?.bestStreamUrl
                if (ep?.animeTitle != null) displayTitle = ep.animeTitle
            }
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat video"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(streamUrl) {
        val url = streamUrl ?: return@LaunchedEffect
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            val position = player.currentPosition
            val duration = player.duration.takeIf { it > 0 } ?: 0
            val pct = if (duration > 0) ((position * 100) / duration).toInt() else 0
            scope.launch {
                store.pushHistory(
                    AnimeItem(
                        animeId = animeId,
                        title = displayTitle,
                        poster = null,
                        episode = pct.coerceIn(0, 100),
                    )
                )
            }
            player.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = AccentRed)
                }
            }
            error != null || streamUrl.isNullOrBlank() -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = error ?: "Video tidak ditemukan",
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.material3.Button(
                        onClick = { scope.launch { store.clearHistory() } },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed),
                    ) { Text("Tutup") }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth().background(Color.Black)) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    this.player = player
                                    useController = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = displayTitle,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = title.ifBlank { "Episode" },
                                color = TextSecondary,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}
