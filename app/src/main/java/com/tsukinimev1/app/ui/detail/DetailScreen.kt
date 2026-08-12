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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onTextLayout
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
import com.tsukinimev1.app.theme.Green
import com.tsukinimev1.app.theme.Surface
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
                    tint = if (saved) AccentRed else TextPrimary,
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
            .aspectRatio(16f / 9f)
            .background(Surface),
    ) {
        AsyncImage(
            model = detail.banner ?: detail.poster,
            contentDescription = detail.title,
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
                            Bg.copy(alpha = 0.6f),
                            Bg,
                        ),
                        startY = 0f,
                        endY = 1200f,
                    )
                ),
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            AsyncImage(
                model = detail.poster,
                contentDescription = detail.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceAlt)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .offset(y = 60.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(y = 20.dp),
            ) {
                Text(
                    text = detail.cleanTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!detail.altTitle.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = detail.altTitle,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(8.dp))
                DetailInfoPills(detail)
                if (detail.isOngoing && !detail.scheduleDay.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "📅 Setiap ${detail.scheduleDay}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Amber)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoPills(detail: AnimeDetail) {
    val pills = mutableListOf<Pair<String, Color>>()
    if (detail.hasRating) pills.add("★ ${detail.score?.trim()}" to AccentRed)
    pills.add(
        (if (detail.isOngoing) "ONGOING" else "COMPLETED") to (if (detail.isOngoing) Cyan else Green)
    )
    detail.type?.takeIf { it.isNotBlank() }?.let { pills.add(it to Color.White) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        pills.forEach { (text, color) ->
            InfoChip(text, color, solid = color == AccentRed)
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
        Spacer(Modifier.height(40.dp))

        // Metadata chips (max 8 + N lainnya)
        val meta = buildList<Pair<String, String>> {
            addNotNull("TV", detail.type)
            addNotNull("Rilis ${detail.released}", detail.released)
            addNotNull(detail.author, detail.author)
            addNotNull("${detail.totalEpisodes} Eps", detail.totalEpisodes?.let { it.toString() })
        }
        if (meta.isNotEmpty()) {
            MetaChipsRow(meta)
            Spacer(Modifier.height(12.dp))
        }

        // Genre tags (accent)
        if (detail.genres.isNotEmpty()) {
            GenreTags(detail.genres)
            Spacer(Modifier.height(16.dp))
        }

        // CTA
        Row {
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentRed)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tonton Sekarang", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentRed.copy(alpha = 0.12f))
                    .border(1.dp, AccentRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleSubscribe),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (subscribed) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Subscribe", color = if (subscribed) AccentRed else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

private fun <T> MutableList<Pair<String, String>>.addNotNull(text: String, raw: String?) {
    if (!raw.isNullOrBlank()) add(text to raw)
}

@Composable
fun MetaChipsRow(chips: List<Pair<String, String>>) {
    val visible = chips.take(8)
    val hidden = chips.size - visible.size
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        visible.forEach { (text, _) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(text, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        if (hidden > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text("+$hidden lainnya", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GenreTags(genres: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        genres.take(8).forEach { g ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(AccentRed.copy(alpha = 0.12f))
                    .border(1.dp, AccentRed.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(g, color = AccentRed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
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
            Box(modifier = Modifier.width(3.dp).height(16.dp).background(AccentRed, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(8.dp))
            Text("Sinopsis", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = synopsis,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { lineCount = it.lineCount },
        )
        if (lineCount >= 4) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (expanded) "Ciutkan" else "Selengkapnya",
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
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
            Text(
                text = "${detail.episodes.size} Episode",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
            )
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
        Spacer(Modifier.height(8.dp))
        visible.forEach { ep ->
            EpisodeRow(ep = ep, onPlay = { onPlay(ep) })
        }
        if (visible.size < filtered.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceAlt)
            .clickable(onClick = onPlay)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (ep.watched) AccentRed else Color.Transparent)
                .border(if (ep.watched) 0.dp else 1.5.dp, AccentRed, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = episodeNumber(ep),
                color = if (ep.watched) Color.White else AccentRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ep.title.ifBlank { "Episode" },
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val rel = ep.date?.let { relativeTime(it) }
            val viewsText = ep.views?.let { "👁 ${formatCount(it.toLong())}" }
            val info = listOfNotNull(viewsText, rel).joinToString(" · ")
            if (info.isNotEmpty()) {
                Spacer(Modifier.height(3.dp))
                Text(info, color = TextSecondary, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
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
