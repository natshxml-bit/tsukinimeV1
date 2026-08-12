package com.tsukinimev1.app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.ScheduleDay
import com.tsukinimev1.app.data.cleanTitle
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Surface
import com.tsukinimev1.app.theme.SurfaceAlt
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary

@Composable
fun ScheduleScreen(navController: NavController) {
    var days by remember { mutableStateOf<List<ScheduleDay>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        days = ApiClient.fetchSchedule()
        loading = false
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Jadwal Rilis",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp),
            )
        }
        when {
            loading -> item {
                Box(Modifier.fillMaxWidth().padding(vertical = 80.dp), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = AccentRed)
                }
            }
            else -> items(days) { day ->
                Column(modifier = Modifier.padding(horizontal = 16, vertical = 6)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceAlt)
                            .padding(horizontal = 12, vertical = 10),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = day.day,
                            color = AccentRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = day.date,
                            color = TextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    if (day.animeList.isEmpty()) {
                        Text(
                            "Belum ada jadwal",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12, vertical = 6),
                        )
                    } else {
                        day.animeList.forEach { anime ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Surface)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = anime.cleanTitle,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = anime.episode?.let { "Eps $it" } ?: "",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
        item { Spacer(Modifier.height(30.dp)) }
    }
}
