package com.tsukinimev1.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.data.hasRating
import com.tsukinimev1.app.data.isOngoing
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Cyan
import com.tsukinimev1.app.theme.Green
import kotlinx.coroutines.delay

private const val AUTO_SCROLL_MS = 6000

@Composable
fun HeroCarousel(
    items: List<AnimeItem>,
    onPlayClick: (AnimeItem) -> Unit,
    onCardClick: (AnimeItem) -> Unit,
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { items.size })

    // Progress ala IG Story / Netflix hero — nyala penuh pas auto-advance
    val progress = remember { Animatable(0f) }
    LaunchedEffect(pagerState.currentPage, items.size) {
        progress.snapTo(0f)
        if (items.size > 1) {
            progress.animateTo(1f, tween(AUTO_SCROLL_MS, easing = LinearEasing))
            val next = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp),
                pageSpacing = 12.dp,
            ) { page ->
                val anime = items[page]
                val active = pagerState.currentPage == page
                val scale by animateFloatAsState(
                    targetValue = if (active) 1f else 0.94f,
                    animationSpec = tween(450, easing = FastOutSlowInEasing),
                    label = "slideScale",
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .alpha(if (active) 1f else 0.55f),
                    contentAlignment = Alignment.Center,
                ) {
                    HeroCard(
                        anime = anime,
                        active = active,
                        onPlayClick = { onPlayClick(anime) },
                        onClick = { onCardClick(anime) },
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Story-style progress bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEachIndexed { i, _ ->
                val fill = when {
                    i < pagerState.currentPage -> 1f
                    i == pagerState.currentPage -> progress.value
                    else -> 0f
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fill.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(999.dp))
                            .background(AccentRed),
                    )
                }
            }
        }
    }
}

@Composable
fun HeroCard(
    anime: AnimeItem,
    active: Boolean,
    onPlayClick: () -> Unit,
    onClick: () -> Unit,
) {
    val statusColor = if (anime.isOngoing) Cyan else Green
    val statusText = if (anime.isOngoing) "Ongoing" else "Completed"
    val epCount = anime.episode?.takeIf { it > 0 }?.let { " · $it Eps" } ?: ""
    val synopsisText = anime.synopsis
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.length > 110) it.substring(0, 110) + "..." else it }
        ?: "Nonton ${anime.cleanTitle} sub Indo terlengkap di TsukiNime."

    // Ken Burns: zoom out pelan-pelan tiap card jadi aktif, mirip keyframe heroZoom di web
    val zoom = remember(anime) { Animatable(1.15f) }
    LaunchedEffect(active) {
        if (active) {
            zoom.animateTo(1f, tween(8000, easing = LinearOutSlowInEasing))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
    ) {
        SubcomposeAsyncImage(
            model = anime.banner ?: anime.poster,
            contentDescription = anime.title,
            modifier = Modifier
                .fillMaxSize()
                .scale(zoom.value),
            contentScale = ContentScale.Crop,
            loading = {
                ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1A1A1D)),
                )
            },
        )

        // Gradient overlay — lebih dalam & sinematik di bawah, tetap bersih di atas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to Color.Black.copy(alpha = 0.05f),
                        0.7f to Color.Black.copy(alpha = 0.55f),
                        1f to Color.Black.copy(alpha = 0.96f),
                    )
                ),
        )
        // Vignette tipis di sisi kiri biar teks makin nempel & kontras
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Black.copy(alpha = 0.35f),
                        0.5f to Color.Transparent,
                    )
                ),
        )

        AnimatedVisibility(
            visible = active,
            enter = fadeIn(tween(500, delayMillis = 100)) +
                slideInVertically(tween(500, delayMillis = 100), initialOffsetY = { it / 6 }),
            modifier = Modifier.align(Alignment.BottomStart),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlassBadge {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(statusColor, RoundedCornerShape(999.dp)),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = statusText + epCount,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.4.sp,
                            )
                        }
                    }
                    if (anime.hasRating) {
                        GlassBadge {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = anime.score?.trim().orEmpty(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = anime.cleanTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = synopsisText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                )

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(AccentRed)
                            .clickable(onClick = onPlayClick)
                            .padding(horizontal = 22.dp, vertical = 11.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = "Tonton",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    GlassBadge(
                        padding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
                        onClick = onClick,
                    ) {
                        Text(
                            text = "Detail",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

/** Badge "glass" — simulasi frosted-glass ala CSS backdrop-blur pake layer alpha + border tipis. */
@Composable
private fun GlassBadge(
    padding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .then(
                Modifier.background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.06f), Color.Black.copy(alpha = 0.25f))
                    )
                )
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** Shimmer sederhana buat placeholder poster/banner selagi loading. */
@Composable
private fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val shimmerAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            shimmerAnim.snapTo(0f)
            shimmerAnim.animateTo(1f, tween(1200, easing = LinearEasing))
        }
    }
    val translate = shimmerAnim.value * 1200f
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1C1C1F),
                    Color(0xFF2A2A2E),
                    Color(0xFF1C1C1F),
                ),
                start = Offset(translate - 400f, 0f),
                end = Offset(translate, 400f),
            )
        )
    )
}
