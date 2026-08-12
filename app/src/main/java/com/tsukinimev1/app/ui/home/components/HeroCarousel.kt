package com.tsukinimev1.app.ui.home.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.data.hasRating
import com.tsukinimev1.app.data.isOngoing
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Bg
import com.tsukinimev1.app.theme.Cyan
import com.tsukinimev1.app.theme.Green
import com.tsukinimev1.app.theme.Surface
import kotlinx.coroutines.delay

@Composable
fun HeroCarousel(
    items: List<AnimeItem>,
    onPlayClick: (AnimeItem) -> Unit,
    onCardClick: (AnimeItem) -> Unit,
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(pagerState.currentPage, items.size) {
        if (items.size > 1) {
            delay(6000)
            val next = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        // Backdrop blur — banner aktif sebagai bg samar di belakang slide
        items.forEachIndexed { i, anime ->
            val active = pagerState.currentPage == i
            if (active) {
                AsyncImage(
                    model = anime.banner ?: anime.poster,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.06f)
                        .alpha(0.45f),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f),
                            Bg,
                        ),
                    )
                ),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 34.dp),
        ) { page ->
            val anime = items[page]
            val active = pagerState.currentPage == page
            val scale by animateFloatAsState(if (active) 1f else 0.94f, label = "scale")
            val alpha by animateFloatAsState(if (active) 1f else 0.5f, label = "alpha")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .alpha(alpha),
                contentAlignment = Alignment.Center,
            ) {
                HeroCard(
                    anime = anime,
                    onPlayClick = { onPlayClick(anime) },
                    onClick = { onCardClick(anime) },
                )
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(items.size) { i ->
            val active = pagerState.currentPage == i
            val width by animateDpAsState(if (active) 14.dp else 6.dp, label = "dot")
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) AccentRed else Color.White.copy(alpha = 0.3f))
                    .width(width)
                    .height(6.dp),
            )
        }
    }
}

@Composable
fun HeroCard(
    anime: AnimeItem,
    onPlayClick: () -> Unit,
    onClick: () -> Unit,
) {
    val statusColor = if (anime.isOngoing) Cyan else Green
    val statusText = if (anime.isOngoing) "EPISODE" else "COMPLETED"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .clickable(onClick = onClick),
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
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.9f),
                            ),
                            startY = 0f,
                            endY = 1000f,
                        )
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text = anime.cleanTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(4.dp), spotColor = Color.Black),
                )
                Spacer(Modifier.height(8.dp))
                // Glass box — satu container translucent berisi tags + sinopsis
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141414).copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (anime.hasRating) {
                                // Rating — amber box solid ala UTM
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFC107))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF121212),
                                        modifier = Modifier.size(8.dp),
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = anime.score?.trim().orEmpty(),
                                        color = Color(0xFF121212),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                    )
                                }
                            }
                            // Status pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            // Type pill
                            anime.type?.takeIf { it.isNotBlank() }?.let { t ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = t,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            // Genre pills (max 2)
                            anime.genres.take(2).forEach { g ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = g,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        if (!anime.synopsis.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = anime.synopsis,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 13.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tonton — filled accent, flex besar
                    Box(
                        modifier = Modifier
                            .weight(1.6f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentRed)
                            .clickable(onClick = onPlayClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Tonton",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    // Nobar — dark translucent outline
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF08080B).copy(alpha = 0.72f))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(13.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Nobar",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                }
            }
        }
    }
}
