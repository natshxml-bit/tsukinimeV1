package com.tsukinimev1.app.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsukinimev1.app.Routes
import com.tsukinimev1.app.theme.Surface
import com.tsukinimev1.app.theme.TextSecondary
import com.tsukinimev1.app.theme.AccentRed

data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

val Tabs = listOf(
    TabItem(Routes.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
    TabItem(Routes.SCHEDULE, "Schedule", Icons.Outlined.Widgets, Icons.Filled.Widgets),
    TabItem(Routes.ALL, "All", Icons.Outlined.GridView, Icons.Filled.GridView),
    TabItem(Routes.NOBAR, "Nobar", Icons.Outlined.Groups, Icons.Filled.Groups),
    TabItem(Routes.LIBRARY, "Library", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary),
    TabItem(Routes.PROFILE, "Profil", Icons.Outlined.Person, Icons.Filled.Person),
)

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(top = 6, bottom = 10),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color.White.copy(alpha = 0.06f)),
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                TabItemView(
                    tab = tab,
                    selected = selected,
                    onClick = { onNavigate(tab.route) },
                )
            }
        }
    }
}

@Composable
private fun TabItemView(
    tab: TabItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = AccentRed
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 4)
            .background(
                color = if (selected) accent.copy(alpha = 0.16f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 8, vertical = 4),
    ) {
        Icon(
            imageVector = if (selected) tab.selectedIcon else tab.icon,
            contentDescription = tab.label,
            tint = if (selected) accent else TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = tab.label,
            color = if (selected) accent else TextSecondary,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
