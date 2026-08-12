package com.tsukinimev1.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.CheckInStatus
import com.tsukinimev1.app.data.Genre
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Green
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary

@Composable
fun CheckInCard(
    status: CheckInStatus,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val checked = status.checkedToday
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AccentRed.copy(alpha = 0.16f),
                        Green.copy(alpha = 0.07f),
                    ),
                )
            )
            .border(1.dp, AccentRed.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(14.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(AccentRed)
                .padding(10.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Daily Check-in Hunter",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = if (checked)
                    (if (status.streak > 1) "Sudah check-in · streak ${status.streak} hari" else "Sudah check-in hari ini")
                else
                    (if (status.streak > 0) "Streak ${status.streak} hari · ambil +25 XP" else "Ambil +25 XP setiap hari"),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (checked) Green.copy(alpha = 0.12f) else AccentRed)
                .border(
                    width = if (checked) 1.dp else 0.dp,
                    color = if (checked) Green.copy(alpha = 0.4f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(enabled = !checked, onClick = onClaim)
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Text(
                text = if (checked) "DONE" else "CLAIM",
                color = if (checked) Green else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
fun GenreChipsRow(
    genres: List<Genre>,
    modifier: Modifier = Modifier,
    onGenreClick: (Genre) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Semua Genre",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16),
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(genres) { genre ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
                        .clickable { onGenreClick(genre) }
                        .padding(horizontal = 15.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = genre.title,
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingRow(
    items: List<Pair<AnimeItem, Int>>,
    modifier: Modifier = Modifier,
    onSeeAll: (() -> Unit)? = null,
    onItemClick: (AnimeItem) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Lanjut Nonton",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            if (onSeeAll != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onSeeAll)
                        .padding(vertical = 4),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Semua",
                        color = AccentRed,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = " ›", color = AccentRed, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items) { (anime, progress) ->
                ContinueCard(
                    anime = anime,
                    progress = progress,
                    onClick = { onItemClick(anime) },
                )
            }
        }
    }
}

@Composable
fun ContinueCard(
    anime: AnimeItem,
    progress: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceAlt)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = anime.poster ?: anime.banner,
            contentDescription = anime.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                        ),
                    )
                ),
        )
        // episode badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                .padding(horizontal = 9.dp, vertical = 3.dp),
        ) {
            Text(
                text = if (anime.episode != null) "Episode ${anime.episode}" else "Episode 1",
                color = Color.White,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        // play overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(46.dp)
                .clip(RoundedCornerShape(50))
                .background(AccentRed)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                text = anime.cleanTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.ExtraBold,
                softWrap = false,
            )
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress / 100f)
                            .height(3.dp)
                            .background(Color.White),
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$progress%",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
