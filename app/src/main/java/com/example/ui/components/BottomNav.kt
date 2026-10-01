package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.TabType
import com.example.ui.theme.*

@Composable
fun BottomNav(
    currentTab: TabType,
    onTabSelected: (TabType) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_navigation_bar"),
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == TabType.HOME,
            onClick = { onTabSelected(TabType.HOME) },
            icon = { Icon(Icons.Default.Mic, contentDescription = "Home") },
            label = { Text("হোম") },
            modifier = Modifier.testTag("nav_home_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryViolet,
                indicatorColor = PrimaryViolet.copy(alpha = 0.3f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        NavigationBarItem(
            selected = currentTab == TabType.CHAT,
            onClick = { onTabSelected(TabType.CHAT) },
            icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chat") },
            label = { Text("চ্যাট") },
            modifier = Modifier.testTag("nav_chat_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryViolet,
                indicatorColor = PrimaryViolet.copy(alpha = 0.3f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        NavigationBarItem(
            selected = currentTab == TabType.TASKS,
            onClick = { onTabSelected(TabType.TASKS) },
            icon = { Icon(Icons.Default.AutoMode, contentDescription = "Tasks") },
            label = { Text("টাস্ক") },
            modifier = Modifier.testTag("nav_tasks_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryViolet,
                indicatorColor = PrimaryViolet.copy(alpha = 0.3f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        NavigationBarItem(
            selected = currentTab == TabType.NEWS,
            onClick = { onTabSelected(TabType.NEWS) },
            icon = { Icon(Icons.Default.Newspaper, contentDescription = "News") },
            label = { Text("খবর") },
            modifier = Modifier.testTag("nav_news_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryViolet,
                indicatorColor = PrimaryViolet.copy(alpha = 0.3f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )

        NavigationBarItem(
            selected = currentTab == TabType.SETTINGS,
            onClick = { onTabSelected(TabType.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("সেটিংস") },
            modifier = Modifier.testTag("nav_settings_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PrimaryViolet,
                indicatorColor = PrimaryViolet.copy(alpha = 0.3f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}
