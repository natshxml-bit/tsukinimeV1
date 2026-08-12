package com.tsukinimev1.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tsukinimev1.app.data.EpisodeDetail
import com.tsukinimev1.app.data.EpisodeInfo
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary
import kotlinx.coroutines.launch

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
    var curEpId by remember { mutableStateOf(episodeId) }

    var epData by remember { mutableStateOf<EpisodeDetail?>(null) }
    var selectedServer by remember { mutableStateOf<String?>(null) }
    var selectedQuality by remember { mutableStateOf<String?>(null) }
    val episodeList = remember { mutableStateListOf<EpisodeInfo>() }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(curEpId) {
        loading = true
        error = null
        streamUrl = null
        epData = null
        selectedServer = null
        selectedQuality = null
        try {
            var ep: EpisodeDetail? = null
            if (curEpId.isBlank()) {
                val detail = ApiClient.fetchAnimeDetail(animeId)
                val first = detail?.episodeList?.firstOrNull()
                if (first != null) {
                    ep = ApiClient.fetchEpisode(first.episodeId)
                    displayTitle = first.title
                }
            } else {
                ep = ApiClient.fetchEpisode(curEpId)
                if (ep?.animeTitle != null) displayTitle = ep.animeTitle
            }

            val detail = runCatching { ApiClient.fetchAnimeDetail(animeId) }.getOrNull()
            episodeList.clear()
            episodeList.addAll(detail?.episodeList ?: emptyList())

            epData = ep
            if (ep != null) {
                val best = ep.bestStreamUrl
                val match = ep.servers.firstNotNullOfOrNull { s ->
                    s.qualities.firstOrNull { it.url == best }?.let { s.server to it.quality }
                }
                if (match != null) {
                    selectedServer = match.first
                    selectedQuality = match.second
                    streamUrl = best
                } else if (ep.servers.isNotEmpty() && ep.servers.first().qualities.isNotEmpty()) {
                    val s = ep.servers.first()
                    val q = s.qualities.last()
                    selectedServer = s.server
                    selectedQuality = q.quality
                    streamUrl = q.url
                } else {
                    streamUrl = best
                }
            }
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat video"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(streamUrl) {
        val url = streamUrl ?: return@LaunchedEffect
        player.stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            val position = player.currentPosition
            val duration = player.duration.takeIf { it > 0 } ?: 0
            val pct = if (duration > 0) ((position * 100) / duration).toInt() else 0
            scope.launch {
                if (curEpId.isNotBlank()) store.markWatched(curEpId)
                val detail = runCatching { ApiClient.fetchAnimeDetail(animeId) }.getOrNull()
                store.pushHistory(
                    AnimeItem(
                        animeId = animeId,
                        title = displayTitle,
                        poster = detail?.poster,
                        banner = detail?.banner,
                        episode = pct.coerceIn(0, 100),
                    )
                )
            }
            player.release()
        }
    }

    val servers = epData?.servers ?: emptyList()
    val currentQualities = servers.firstOrNull { it.server == selectedServer }?.qualities ?: emptyList()
    val curIndex = episodeList.indexOfFirst { it.episodeId == curEpId }
    val hasPrev = curIndex > 0
    val hasNext = curIndex in 0 until episodeList.size - 1

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
                    verticalArrangement = Arrangement.Center,
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
                        onClick = { navController?.popBackStack() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed),
                    ) { Text("Kembali") }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { navController?.popBackStack() }, modifier = Modifier.size(42.dp)) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White,
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayTitle,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                            )
                            Text(
                                text = if (episodeList.isNotEmpty()) {
                                    val n = curIndex.coerceAtLeast(0)
                                    "Episode ${episodeList[n].title.ifBlank { "${n + 1}" }} dari ${episodeList.size}"
                                } else title.ifBlank { "Episode" },
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                            )
                        }
                        IconButton(
                            onClick = { if (hasPrev) curEpId = episodeList[curIndex - 1].episodeId },
                            enabled = hasPrev,
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Episode sebelumnya", tint = if (hasPrev) Color.White else Color.White.copy(alpha = 0.25f))
                        }
                        IconButton(
                            onClick = { if (hasNext) curEpId = episodeList[curIndex + 1].episodeId },
                            enabled = hasNext,
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Episode berikutnya", tint = if (hasNext) AccentRed else Color.White.copy(alpha = 0.25f))
                        }
                    }

                    if (servers.size > 1) {
                        SectionLabel("Server")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(servers) { s ->
                                SelectorChip(
                                    text = s.server.ifBlank { "Server" },
                                    selected = s.server == selectedServer,
                                    onClick = {
                                        selectedServer = s.server
                                        val q = s.qualities.lastOrNull() ?: s.qualities.firstOrNull()
                                        if (q != null) {
                                            selectedQuality = q.quality
                                            streamUrl = q.url
                                        }
                                    },
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    if (currentQualities.isNotEmpty()) {
                        SectionLabel("Kualitas")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(currentQualities) { q ->
                                SelectorChip(
                                    text = q.quality.ifBlank { "Auto" },
                                    selected = q.quality == selectedQuality,
                                    onClick = {
                                        selectedQuality = q.quality
                                        streamUrl = q.url
                                    },
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun SelectorChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) AccentRed else SurfaceAlt)
            .border(1.dp, if (selected) AccentRed else Color.White.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
