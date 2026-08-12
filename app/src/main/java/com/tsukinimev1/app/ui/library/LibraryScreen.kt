package com.tsukinimev1.app.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.AnimeCard
import com.tsukinimev1.app.ui.home.components.ContinueWatchingRow

@Composable
fun LibraryScreen(
    store: LocalStore,
    navController: NavController,
) {
    val watchlistIds by store.watchlist.collectAsStateWithLifecycle(initialValue = emptySet())
    val history by store.history.collectAsStateWithLifecycle(initialValue = emptyList())
    var savedItems by remember { mutableStateOf<List<AnimeItem>>(emptyList()) }

    androidx.compose.runtime.LaunchedEffect(watchlistIds) {
        if (watchlistIds.isEmpty()) {
            savedItems = emptyList()
            return@LaunchedEffect
        }
        val list = mutableListOf<AnimeItem>()
        for (id in watchlistIds) {
            ApiClient.fetchAnimeDetail(id)?.let { list.add(it) }
        }
        savedItems = list
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text = "Library",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp),
            )
        }
        item {
            Text(
                text = "Lanjut Nonton",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(10.dp))
        }
        item {
            if (history.isEmpty()) {
                Text(
                    text = "Belum ada riwayat nonton",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            } else {
                ContinueWatchingRow(
                    items = history.map { it to (it.episode ?: 0) },
                    onItemClick = { navController.navigate("detail/${it.animeId}") },
                )
            }
        }
        item {
            Spacer(Modifier.height(22.dp))
            Text(
                text = "Watchlist",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(10.dp))
        }
        item {
            if (savedItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.height(32.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Belum ada watchlist", color = TextSecondary, fontSize = 12.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.height(230.dp * ((savedItems.size + 2) / 3).coerceAtLeast(1)),
                ) {
                    items(savedItems) { anime ->
                        AnimeCard(
                            anime = anime,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate("detail/${anime.animeId}") },
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(30.dp)) }
    }
}
