package com.tsukinimev1.app.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.AnimeCard
import com.tsukinimev1.app.ui.components.CardBadge
import androidx.compose.material3.Scaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    type: String,
    title: String,
    store: LocalStore,
    navController: NavController,
    genreSlug: String? = null,
) {
    var items by remember { mutableStateOf<List<AnimeItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun load() {
        loading = true
        error = null
        try {
            items = when {
                genreSlug != null -> ApiClient.fetchByGenre(genreSlug)
                type == "recent" || type == "film" -> {
                    val home = ApiClient.fetchHome()
                    if (type == "recent") home.recent else home.film
                }
                else -> ApiClient.fetchList(type)
            }
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat data"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(type, genreSlug) { load() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { padding ->
        when {
            loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = AccentRed)
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(error ?: "Error", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    androidx.compose.material3.Button(
                        onClick = { androidx.compose.runtime.rememberCoroutineScope().launch { load() } },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed),
                    ) { Text("Coba lagi", fontSize = 13.sp) }
                }
            }
            items.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("Tidak ada data", color = TextSecondary, fontSize = 13.sp)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(padding),
                ) {
                    items(items) { anime ->
                        AnimeCard(
                            anime = anime,
                            modifier = Modifier.fillMaxWidth(),
                            badge = when (type) {
                                "ongoing-anime" -> CardBadge("ONGOING", com.tsukinimev1.app.theme.Cyan, Color(0xFF0D0D12))
                                "complete-anime" -> CardBadge("END", com.tsukinimev1.app.theme.Indigo, Color(0xFF0D0D12))
                                "film" -> CardBadge("MOVIE", com.tsukinimev1.app.theme.Amber, Color(0xFF0D0D12))
                                else -> null
                            },
                            onClick = { navController.navigate("detail/${anime.animeId}") },
                        )
                    }
                }
            }
        }
    }
}
