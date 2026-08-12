package com.tsukinimev1.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.CheckInStatus
import com.tsukinimev1.app.data.Genre
import com.tsukinimev1.app.data.HomeData
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.data.UserProfile
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.CardBadge
import com.tsukinimev1.app.ui.home.components.CheckInCard
import com.tsukinimev1.app.ui.home.components.ContinueWatchingRow
import com.tsukinimev1.app.ui.home.components.GenreChipsRow
import com.tsukinimev1.app.ui.home.components.HeroCarousel
import com.tsukinimev1.app.ui.home.components.HomeHeader
import com.tsukinimev1.app.ui.home.components.ShimmerBlock
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    store: LocalStore,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkIn by store.checkIn.collectAsStateWithLifecycle(initialValue = CheckInStatus())
    val profile by store.profile.collectAsStateWithLifecycle(initialValue = UserProfile())
    val history by store.history.collectAsStateWithLifecycle(initialValue = emptyList())

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    fun greeting(): String {
        val h = java.time.LocalTime.now().hour
        return when {
            h < 11 -> "Selamat Pagi"
            h < 15 -> "Selamat Siang"
            h < 18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    fun openDetail(anime: AnimeItem) {
        navController.navigate("detail/${anime.animeId}")
    }

    fun openPlayer(anime: AnimeItem) {
        val epId = anime.episodeList.firstOrNull()?.episodeId
            ?: anime.episodeList.firstOrNull()?.endpoint
            ?: ""
        navController.navigate("player/${anime.animeId}/$epId?title=${anime.cleanTitle}")
    }

    fun openList(type: String, title: String, genre: String? = null) {
        val genreArg = genre?.let { "?genre=$it" } ?: ""
        navController.navigate("list/$type/$title$genreArg")
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            HomeHeader(
                greeting = greeting(),
                profile = profile,
                onProfileClick = { navController.navigate("profile") },
                onNotificationsClick = {},
                onSearchClick = { navController.navigate("search") },
            )
            Spacer(Modifier.height(8.dp))
        }

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                item { ShimmerBlock() }
                item { Spacer(Modifier.height(24.dp)) }
                item { ShimmerBlock() }
                item { Spacer(Modifier.height(24.dp)) }
                item { ShimmerBlock() }
            }
            is HomeUiState.Error -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Gagal memuat data", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = state.message,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp),
                        )
                        Spacer(Modifier.height(14.dp))
                        androidx.compose.material3.Button(
                            onClick = { viewModel.load() },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = AccentRed,
                            ),
                        ) {
                            Text("Coba lagi", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                val data = state.data
                val heroItems = if (data.ongoing.isNotEmpty()) data.ongoing else data.recent

                if (heroItems.isNotEmpty()) {
                    item {
                        HeroCarousel(
                            items = heroItems,
                            onPlayClick = ::openPlayer,
                            onCardClick = ::openDetail,
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    CheckInCard(
                        status = checkIn,
                        onClaim = {
                            scope.launch {
                                store.claimCheckIn()
                            }
                        },
                    )
                    Spacer(Modifier.height(24.dp))
                }

                if (state.genres.isNotEmpty()) {
                    item {
                        GenreChipsRow(
                            genres = state.genres.take(15),
                            onGenreClick = { g ->
                                openList("genre", g.title, genre = g.endpoint)
                            },
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }

                if (history.isNotEmpty()) {
                    item {
                        ContinueWatchingRow(
                            items = history.map { it to (it.episode ?: 0) },
                            onSeeAll = { navController.navigate("library") },
                            onItemClick = ::openDetail,
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }

                // Grid sections (reusable component, data beda-beda)
                if (data.recent.isNotEmpty()) {
                    item {
                        com.tsukinimev1.app.ui.home.components.AnimeGridSection(
                            title = "Episode Terbaru",
                            items = data.recent,
                            badge = CardBadge("NEW", AccentRed),
                            onSeeAll = { openList("recent", "Episode Terbaru") },
                            onItemClick = ::openDetail,
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }

                if (data.ongoing.isNotEmpty()) {
                    item {
                        com.tsukinimev1.app.ui.home.components.AnimeGridSection(
                            title = "Sedang Tayang",
                            items = data.ongoing,
                            badge = CardBadge("ONGOING", com.tsukinimev1.app.theme.Cyan, Color(0xFF0D0D12)),
                            onSeeAll = { openList("ongoing-anime", "Sedang Tayang") },
                            onItemClick = ::openDetail,
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }

                if (state.recommendations.isNotEmpty()) {
                    item {
                        com.tsukinimev1.app.ui.home.components.AnimeGridSection(
                            title = "Rekomendasi buat kamu",
                            items = state.recommendations,
                            badge = CardBadge("PICKS", com.tsukinimev1.app.theme.Indigo, Color(0xFF0D0D12)),
                            onSeeAll = { openList("ongoing-anime", "Rekomendasi") },
                            onItemClick = ::openDetail,
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}
