package com.tsukinimev1.app.ui.nobar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsukinimev1.app.theme.AccentRed
import com.tsukinimev1.app.theme.Surface
import com.tsukinimev1.app.theme.TextSecondary

@Composable
fun NobarScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AccentRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Groups,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 20.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Nobar",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Fitur nonton bareng (watch-together) akan segera hadir.\nKamu bisa bikin room, ajak teman, dan nonton anime bareng real-time.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
        )
    }
}
