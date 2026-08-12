package com.tsukinimev1.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.data.hasRating
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary

data class CardBadge(
    val text: String,
    val background: Color,
)

@Composable
fun AnimeCard(
    anime: AnimeItem,
    modifier: Modifier = Modifier,
    badge: CardBadge? = null,
    showScore: Boolean = true,
    showEpisode: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceAlt),
        ) {
            AsyncImage(
                model = anime.poster,
                contentDescription = anime.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // gradient scrim bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        )
                    ),
            )
            if (badge != null) {
                // A6.1: flat solid rectangle, nempel pojok kiri-atas tanpa margin,
                // cuma 2 sudut rounded (kiri-atas & kanan-bawah), no shadow/border.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .background(badge.background)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = badge.text,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (showScore && anime.hasRating) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(10.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = anime.score.orEmpty(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            if (showEpisode && anime.episode != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${anime.episode} Eps",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = anime.cleanTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 15.sp,
            modifier = Modifier.height(40.dp),
        )
    }
}
