package com.tsukinimev1.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.EpisodeInfo
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Cyan
import com.tsukinimev1.app.theme.Green
import com.tsukinimev1.app.theme.Surface
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    slug: String,
    store: LocalStore,
    navController: NavController,
) {
    var anime by remember { mutableStateOf<AnimeItem?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(slug) {
        loading = true
        try {
            anime = ApiClient.fetchAnimeDetail(slug)
            saved = store.isInWatchlist(slug)
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = AccentRed)
                }
            }
            error != null || anime == null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(error ?: "Anime tidak ditemukan", color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.material3.Button(
                        onClick = { navController.popBackStack() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed),
                    ) { Text("Kembali") }
                }
            }
            else -> {
                val a = anime!!
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        DetailBanner(
                            anime = a,
                            saved = saved,
                            onBack = { navController.popBackStack() },
                            onToggleSave = {
                                scope.launch {
                                    saved = store.toggleWatchlist(a.animeId)
                                }
                            },
                        )
                    }
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                InfoChip("★ ${a.score ?: "-"}", AccentRed, true)
                                InfoChip(if (a.isOngoing) "ONGOING" else "COMPLETED", if (a.isOngoing) Cyan else Green, false)
                                a.type?.let { InfoChip(it, Color.White, false) }
                                if (a.quality != null) InfoChip(a.quality!!, Color.White, false)
                            }
                            Spacer(Modifier.height(12.dp))
                            if (a.genres.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    a.genres.take(4).forEach { g ->
                                        InfoChip(g, Color.White, false)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                            if (!a.released.isNullOrBlank()) {
                                Text(
                                    "Rilis: ${a.released}",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                            Text(
                                text = "Sinopsis",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = a.synopsis ?: "Tidak ada sinopsis.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                            )
                            Spacer(Modifier.height(18.dp))
                            Text(
                                text = "Daftar Episode (${a.episodeList.size})",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    if (a.episodeList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceAlt)
                                    .clickable {
                                        navController.navigate("player/${a.animeId}/?title=${a.cleanTitle}")
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = AccentRed)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Putar", color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        items(a.episodeList) { ep ->
                            EpisodeRow(ep = ep, anime = a, onClick = {
                                navController.navigate(
                                    "player/${a.animeId}/${ep.episodeId}?title=${a.cleanTitle}"
                                )
                            })
                        }
                    }
                    item { Spacer(Modifier.height(30.dp)) }
                }
            }
        }
    }
}

@Composable
fun DetailBanner(
    anime: AnimeItem,
    saved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Surface),
    ) {
        AsyncImage(
            model = anime.banner ?: anime.poster,
            contentDescription = anime.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                            com.tsukinimev1.app.theme.Bg,
                        ),
                    )
                ),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            IconButton(onClick = onToggleSave) {
                Icon(
                    imageVector = if (saved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Simpan",
                    tint = if (saved) AccentRed else Color.White,
                )
            }
        }
        Text(
            text = anime.cleanTitle,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        )
    }
}

@Composable
fun InfoChip(
    text: String,
    color: Color,
    solid: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (solid) color else color.copy(alpha = 0.14f))
            .border(
                width = if (solid) 0.dp else 1.dp,
                color = if (solid) Color.Transparent else color.copy(alpha = 0.4f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            color = if (solid) Color.White else color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
fun EpisodeRow(
    ep: EpisodeInfo,
    anime: AnimeItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16, vertical = 5)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceAlt)
            .clickable(onClick = onClick)
            .padding(horizontal = 12, vertical = 12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ep.title.ifBlank { "Episode" },
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!ep.date.isNullOrBlank()) {
                Text(
                    text = ep.date,
                    color = TextSecondary,
                    fontSize = 11.sp,
                )
            }
        }
        if (ep.views != null) {
            Text(
                text = "👁 ${ep.views}",
                color = TextSecondary,
                fontSize = 11.sp,
            )
        }
    }
}
