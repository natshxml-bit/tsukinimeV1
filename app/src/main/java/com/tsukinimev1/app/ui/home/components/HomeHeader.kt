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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tsukinimev1.app.data.UserProfile
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Surface
import com.tsukinimev1.app.theme.TextPrimary
import com.tsukinimev1.app.theme.TextSecondary

@Composable
fun HomeHeader(
    greeting: String,
    profile: UserProfile,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onProfileClick),
            ) {
                Avatar(profile)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = greeting,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = profile.name,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PillTag("Lv.${profile.level}", Color.White, AccentRed)
                        PillTag(
                            text = profile.rank,
                            textColor = TextSecondary,
                            background = Color(0xFF1F1F1F),
                            borderColor = Color.White.copy(alpha = 0.14f),
                        )
                    }
                }
            }
            Row {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifikasi",
                        tint = TextPrimary,
                    )
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Cari",
                        tint = TextPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun Avatar(profile: UserProfile) {
    val avatarUrl = profile.avatarUrl
    if (!avatarUrl.isNullOrBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Foto profil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .border(2.dp, AccentRed, CircleShape)
                .clip(CircleShape)
                .background(Surface),
        )
    } else {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF2A2A2A), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun PillTag(
    text: String,
    textColor: Color,
    background: Color,
    borderColor: Color = Color.Transparent,
) {
    Box(
        modifier = Modifier
            .padding(top = 3.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun HomeSearchBar(
    onSearchClick: () -> Unit,
    onQuerySubmit: ((String) -> Unit)? = null,
) {
    val queryState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A1A1A))
            .clickable(onClick = onSearchClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Cari anime...",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
