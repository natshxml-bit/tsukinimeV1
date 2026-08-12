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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .border(2.dp, AccentRed, CircleShape)
                        .background(Surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = greeting,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = profile.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        PillTag("Lv.${profile.level}", Color.White, AccentRed)
                        PillTag(profile.rank, Color(0xFFD4D4D8), Color.White.copy(alpha = 0.1f))
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
        Spacer(Modifier.height(12.dp))
        HomeSearchBar(onSearchClick = onSearchClick)
    }
}

@Composable
fun PillTag(
    text: String,
    textColor: Color,
    background: Color,
) {
    Box(
        modifier = Modifier
            .padding(top = 3.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 7.dp, vertical = 1.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
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
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
            .clickable(onClick = onSearchClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(18.dp),
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
