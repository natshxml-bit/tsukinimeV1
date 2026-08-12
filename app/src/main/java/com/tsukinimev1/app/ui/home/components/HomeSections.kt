package com.tsukinimev1.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.AnimeCard
import com.tsukinimev1.app.ui.components.CardBadge

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    seeAllLabel: String = "Semua",
    onSeeAll: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        if (onSeeAll != null) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onSeeAll)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = seeAllLabel,
                    color = AccentRed,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.width(14.dp),
                )
            }
        }
    }
}

@Composable
fun AnimeGridSection(
    title: String,
    items: List<AnimeItem>,
    badge: CardBadge? = null,
    modifier: Modifier = Modifier,
    onSeeAll: (() -> Unit)? = null,
    onItemClick: (AnimeItem) -> Unit,
) {
    if (items.isEmpty()) return
    // A0: grid preview wajib kelipatan kolom (3,6,9...). Sisa item tetap ada
    // di halaman "Semua". Kalau hasil potong < 1 baris penuh -> skip total.
    val columns = 3
    val displayItems = items.take((items.size / columns) * columns)
    if (displayItems.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = title, onSeeAll = onSeeAll)
        Spacer(Modifier.height(10.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            val cellW = (maxWidth - 20.dp) / columns
            val cardH = cellW * 1.5f + 46.dp
            val rows = displayItems.size / columns
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardH * rows + 14.dp * (rows - 1)),
            ) {
                items(displayItems) { anime ->
                    AnimeCard(
                        anime = anime,
                        badge = badge,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onItemClick(anime) },
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    color: Color = com.tsukinimev1.app.theme.SurfaceAlt,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.width(140.dp).height(18.dp).background(color))
            Box(modifier = Modifier.width(60.dp).height(14.dp).background(color))
        }
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(3) {
                Column(modifier = Modifier.width(100.dp)) {
                    Box(modifier = Modifier.width(100.dp).height(150.dp).background(color))
                    Spacer(Modifier.height(6.dp))
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(color))
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(3) {
                Column(modifier = Modifier.width(100.dp)) {
                    Box(modifier = Modifier.width(100.dp).height(150.dp).background(color))
                    Spacer(Modifier.height(6.dp))
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(color))
                }
            }
        }
    }
}
