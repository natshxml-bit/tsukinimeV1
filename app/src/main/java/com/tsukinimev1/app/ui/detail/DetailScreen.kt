package com.tsukinimev1.app.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Amber
import com.tsukinimev1.app.theme.Bg
import com.tsukinimev1.app.theme.Cyan
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.AnimeCard
import com.tsukinimev1.app.ui.detail.model.AnimeDetail
import com.tsukinimev1.app.ui.detail.model.Episode
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    slug: String,
    viewModel: DetailViewModel,
    store: LocalStore,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(slug) { viewModel.load(slug) }

    val listState = rememberLazyListState()
    val showSolidBar by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset > 240 }
    }

    val scope = rememberCoroutineScope()
    var saved by remember { mutableStateOf(false) }
    var subscribed by remember { mutableStateOf(false) }
    LaunchedEffect(slug) {
        saved = store.isInWatchlist(slug)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is DetailUiState.Loading -> DetailLoading()
            is DetailUiState.Error -> DetailError(state.message, onBack = { navController.popBackStack() })
            is DetailUiState.Success -> {
                val detail = state.detail
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        DetailHero(
                            detail = detail,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    item {
                        DetailBody(
                            detail = detail,
                            subscribed = subscribed,
                            onToggleSubscribe = { subscribed = !subscribed },
                            onPlay = { navController.navigate("player/${detail.animeId}/?title=${detail.cleanTitle}") },
                        )
                    }
                    item {
                        DetailSynopsis(detail.synopsis)
                        Spacer(Modifier.height(24.dp))
                    }
                    if (state.related.isNotEmpty()) {
                        item {
                            RelatedSection(
                                items = state.related,
                                onItemClick = { navController.navigate("detail/${it.animeId}") },
                            )
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                    item {
                        EpisodeSection(
                            detail = detail,
                            onPlay = { ep ->
                                navController.navigate("player/${detail.animeId}/${ep.episodeId}?title=${detail.cleanTitle}")
                            },
                        )
                    }
                    item { Spacer(Modifier.height(30.dp)) }
                }
            }
        }
        DetailTopBar(
            title = (uiState as? DetailUiState.Success)?.detail?.cleanTitle.orEmpty(),
            showSolid = showSolidBar,
            saved = saved,
            onBack = { navController.popBackStack() },
            onToggleSave = {
                saved = !saved
                scope.launch {
                    val res = store.toggleWatchlist(slug)
                    if (res != saved) saved = res
                }
            },
        )
    }
}

@Composable
fun DetailTopBar(
    title: String,
    showSolid: Boolean,
    saved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
) {
    AnimatedVisibility(
        visible = showSolid,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Bg)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onToggleSave, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (saved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Simpan",
                    tint = if (saved) Amber else TextPrimary,
                )
            }
        }
    }
}

@Composable
fun DetailLoading() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(16.dp)).background(SurfaceAlt))
        Spacer(Modifier.height(16.dp))
        repeat(5) {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 6.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceAlt))
        }
    }
}

@Composable
fun DetailError(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = TextSecondary, fontSize = 14.sp)
        Spacer(Modifier.height(12.dp))
        androidx.compose.material3.Button(
            onClick = onBack,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed),
        ) { Text("Kembali") }
    }
}

@Composable
fun DetailHero(
    detail: AnimeDetail,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp),
    ) {
        AsyncImage(
            model = detail.banner ?: detail.poster,
            contentDescription = detail.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Gradient overlay: solid Bg di bawah, transparan di atas (gaya referensi)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Bg.copy(alpha = 0.92f),
                            Bg,
                        ),
                    )
                ),
        )
        // Back — lingkaran 45dp blur semi-transparan
        Box(
            modifier = Modifier
                .padding(top = 20.dp, start = 15.dp)
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        // Poster + title — menjorok keluar dari bottom banner (overlap)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 105.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            AsyncImage(
                model = detail.poster,
                contentDescription = detail.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, AccentRed.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = Color.Black),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f).padding(bottom = 6.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Status badge — cyan (ongoing) / gold (completed)
                    val ongoing = detail.isOngoing
                    val statusBg = if (ongoing) Cyan.copy(alpha = 0.12f) else Color(0xFFF5B301).copy(alpha = 0.12f)
                    val statusFg = if (ongoing) Cyan else Color(0xFFF5B301)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBg)
                            .border(1.dp, statusFg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = (detail.status ?: "Ongoing").uppercase(),
                            color = statusFg,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                        )
                    }
                    // Type badge
                    detail.type?.takeIf { it.isNotBlank() }?.let { t ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentRed.copy(alpha = 0.15f))
                                .border(1.dp, AccentRed.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = t,
                                color = AccentRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }
                    // Rating — amber star (skip kalau null/0)
                    if (detail.hasRating) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFC107).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(11.dp),
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = detail.score?.trim().orEmpty(),
                                color = Color(0xFFFFC107),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = detail.cleanTitle,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(4.dp), spotColor = Color.Black),
                )
                if (!detail.altTitle.isNullOrBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = detail.altTitle,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun DetailBody(
    detail: AnimeDetail,
    subscribed: Boolean,
    onToggleSubscribe: () -> Unit,
    onPlay: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(140.dp))

        // META ROW — kartu 3 kolom (Rilis / Author / Total)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.03f))
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                .padding(vertical = 14.dp),
        ) {
            listOf(
                Triple(Icons.Filled.CalendarMonth, "Rilis", detail.released ?: "—"),
                Triple(Icons.Filled.Sell, "Author", detail.author ?: "—"),
                Triple(Icons.Filled.Bookmark, "Total", "${detail.totalEpisodes ?: detail.episodes.size} Eps"),
            ).forEachIndexed { i, (icon, label, value) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (i > 0) 1.dp else 0.dp,
                            color = Color.White.copy(alpha = 0.06f),
                            shape = RectangleShape,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = AccentRed, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = value,
                        color = Color(0xFFE4E4E7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Genre tags — chip style (outline accent, tanpa icon)
        if (detail.genres.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                detail.genres.take(6).forEach { g ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AccentRed.copy(alpha = 0.1f))
                            .border(1.dp, AccentRed.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = g,
                            color = AccentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Badge jadwal (hanya ongoing)
        if (detail.isOngoing && !detail.scheduleDay.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "📅 Setiap ${detail.scheduleDay}",
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // CTA — Tonton (flex 1.8) + Subscribe (flex 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1.8f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentRed)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("TONTON SEKARANG", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 0.3.sp)
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentRed.copy(alpha = 0.12f))
                    .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleSubscribe),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (subscribed) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (subscribed) "Terlanggan" else "Subscribe",
                        color = if (subscribed) AccentRed else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            detail.views?.let { Text("${formatCount(it)} Views", color = TextSecondary, fontSize = 12.sp) }
                ?: Spacer(Modifier.width(0.dp))
            detail.subscribers?.let { Text("${formatCount(it)} Subscriber", color = TextSecondary, fontSize = 12.sp) }
        }
    }
}

fun formatCount(v: Long): String {
    return when {
        v >= 1_000_000 -> String.format("%.1fM", v / 1_000_000.0)
        v >= 1_000 -> String.format("%.1fK", v / 1_000.0)
        else -> v.toString()
    }
}

@Composable
fun DetailSynopsis(synopsis: String?) {
    if (synopsis.isNullOrBlank()) return
    var expanded by remember { mutableStateOf(false) }
    var lineCount by remember { mutableStateOf(0) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(3.dp).height(16.dp).background(AccentRed, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(8.dp))
            Text("Sinopsis", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = synopsis,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { lineCount = it.lineCount },
        )
        if (lineCount >= 4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (expanded) "Sembunyikan" else "Selengkapnya",
                    color = AccentRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(5.dp))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(16.dp).rotate(arrowRotation),
                )
            }
        }
    }
}

@Composable
fun RelatedSection(
    items: List<AnimeItem>,
    onItemClick: (AnimeItem) -> Unit,
) {
    if (items.isEmpty()) return
    Column {
        Text(
            text = "Rekomendasi",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items) { anime ->
                AnimeCard(
                    anime = anime,
                    badge = null,
                    modifier = Modifier.width(110.dp),
                    onClick = { onItemClick(anime) },
                )
            }
        }
    }
}

@Composable
fun EpisodeSection(
    detail: AnimeDetail,
    onPlay: (Episode) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var page by remember { mutableStateOf(0) }
    val pageSize = 20

    LaunchedEffect(detail.animeId) { page = 0 }

    val filtered = remember(detail.episodes, query) {
        if (query.isBlank()) detail.episodes
        else detail.episodes.filter { it.title.contains(query, ignoreCase = true) || it.episodeId.contains(query, ignoreCase = true) }
    }
    val visible = filtered.take(pageSize * (page + 1))

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(4.dp).height(18.dp).background(AccentRed, RoundedCornerShape(4.dp)))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${detail.episodes.size} Episode",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Row {
                IconButton(onClick = { showSearch = !showSearch }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Search, contentDescription = "Cari", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Sort, contentDescription = "Urut", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }
        AnimatedVisibility(visible = showSearch) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceAlt)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 13.sp),
                        decorationBox = { inner ->
                            Box {
                                if (query.isEmpty()) Text("Cari episode...", color = TextSecondary, fontSize = 13.sp)
                                inner()
                            }
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        visible.forEach { ep ->
            EpisodeRow(ep = ep, onPlay = { onPlay(ep) })
            Spacer(Modifier.height(12.dp))
        }
        if (visible.size < filtered.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentRed)
                    .clickable { page += 1 }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("Muat ${pageSize} lagi", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EpisodeRow(
    ep: Episode,
    onPlay: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (ep.watched) AccentRed.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f))
            .border(if (ep.watched) 1.dp else 1.dp, if (ep.watched) AccentRed.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // EP numeral badge — border accent / solid accent kalau ditonton
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (ep.watched) AccentRed.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.12f))
                .border(
                    if (ep.watched) 1.5.dp else 1.dp,
                    if (ep.watched) AccentRed.copy(alpha = 0.6f) else AccentRed.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = episodeNumber(ep),
                color = AccentRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ep.title.ifBlank { "Episode" },
                color = Color(0xFFE4E4E7),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val viewsText = ep.views?.let { "👁 ${formatCount(it.toLong())}" }
            val rel = ep.date?.let { relativeTime(it) }
            val info = listOfNotNull(viewsText, rel).joinToString("  ·  ")
            if (info.isNotEmpty()) {
                Spacer(Modifier.height(3.dp))
                Text(info, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AccentRed.copy(alpha = 0.15f))
                .border(1.dp, AccentRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = AccentRed, modifier = Modifier.size(11.dp))
                Spacer(Modifier.width(4.dp))
                Text("Play", color = AccentRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

fun relativeTime(dateStr: String): String {
    return try {
        val s = dateStr.replace(",", "").trim()
        val fmt = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale("id", "ID"))
        val d = fmt.parse(s) ?: return dateStr
        val diff = System.currentTimeMillis() - d.time
        val hours = diff / 3600_000
        if (hours in 0 until 24) {
            when {
                hours < 1 -> "Baru saja"
                hours < 24 -> "$hours jam lalu"
                else -> dateStr
            }
        } else {
            java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale("id", "ID")).format(d)
        }
    } catch (e: Exception) {
        dateStr
    }
}

fun episodeNumber(ep: Episode): String {
    val m = Regex("\\d+").find(ep.title) ?: Regex("\\d+").find(ep.episodeId)
    return m?.value ?: "?"
}
