package com.tsukinimev1.app.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.tsukinimev1.app.theme.Cyan
import com.tsukinimev1.app.theme.Green
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
            .height(268.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val anime = items[page]
            val active = pagerState.currentPage == page
            val scale by animateFloatAsState(
                targetValue = if (active) 1f else 0.93f,
                animationSpec = tween(450, easing = FastOutSlowInEasing),
                label = "slideScale",
            )
            val alpha by animateFloatAsState(
                targetValue = if (active) 1f else 0.55f,
                animationSpec = tween(350),
                label = "slideAlpha",
            )
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

    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(items.size) { i ->
            val active = pagerState.currentPage == i
            val width by animateDpAsState(
                targetValue = if (active) 16.dp else 6.dp,
                animationSpec = tween(300),
                label = "dotWidth",
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .shadow(
                        elevation = if (active) 6.dp else 0.dp,
                        shape = RoundedCornerShape(999.dp),
                        ambientColor = AccentRed,
                        spotColor = AccentRed,
                    )
                    .background(if (active) AccentRed else Color.White.copy(alpha = 0.28f))
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
    val epCount = anime.episode?.takeIf { it > 0 }?.let { " $it" } ?: ""
    val synopsisText = anime.synopsis
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.length > 120) it.substring(0, 120) + "..." else it }
        ?: "Nonton ${anime.cleanTitle} sub Indo terlengkap di TsukiNime."

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color.Black.copy(alpha = 0.5f),
            )
            .clip(RoundedCornerShape(18.dp))
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
                            Color.Black.copy(alpha = 0.05f),
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.92f),
                        ),
                    )
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Text(
                text = anime.cleanTitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 22.sp,
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(4.dp), spotColor = Color.Black),
            )

            Spacer(Modifier.height(9.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141414).copy(alpha = 0.82f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (anime.hasRating) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0xFFFFC107))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF121212),
                                    modifier = Modifier.size(9.dp),
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = anime.score?.trim().orEmpty(),
                                    color = Color(0xFF121212),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(statusColor.copy(alpha = 0.14f))
                                .border(1.dp, statusColor, RoundedCornerShape(5.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = statusText + epCount,
                                color = statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(2.dp),
                                    ambientColor = statusColor,
                                    spotColor = statusColor,
                                ),
                            )
                        }

                        anime.genres.take(2).forEach { g ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = g,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    if (!anime.synopsis.isNullOrBlank()) {
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = synopsisText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                        )
                    } else {
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = synopsisText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1.7f)
                        .height(44.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(13.dp),
                            ambientColor = AccentRed.copy(alpha = 0.5f),
                            spotColor = AccentRed.copy(alpha = 0.45f),
                        )
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    AccentRed,
                                    Color(0xFFB91C1C),
                                ),
                            )
                        )
                        .clickable(onClick = onPlayClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Tonton",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }

                Spacer(Modifier.width(9.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFF08080B).copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(13.dp))
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Nobar",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
