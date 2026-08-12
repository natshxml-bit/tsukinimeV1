package com.tsukinimev1.app.ui.search

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.ui.components.AnimeCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<AnimeItem>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    fun doSearch(q: String) {
        if (q.trim().length < 2) {
            results = emptyList()
            return
        }
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(450)
            searching = true
            try {
                results = ApiClient.fetchSearch(q.trim())
            } catch (_: Exception) {
                results = emptyList()
            } finally {
                searching = false
            }
        }
    }

    LaunchedEffect(query) { doSearch(query) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Cari Anime",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Cari anime...", color = TextSecondary, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = AccentRed) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(999.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AccentRed,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
        )
        Spacer(Modifier.height(16.dp))

        when {
            searching -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = AccentRed)
                }
            }
            results.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (query.trim().length < 2) "Ketik minimal 2 huruf" else "Tidak ada hasil",
                        color = TextSecondary,
                        fontSize = 13.sp,
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(results) { anime ->
                        AnimeCard(
                            anime = anime,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate("detail/${anime.animeId}") },
                        )
                    }
                }
            }
        }
    }
}
