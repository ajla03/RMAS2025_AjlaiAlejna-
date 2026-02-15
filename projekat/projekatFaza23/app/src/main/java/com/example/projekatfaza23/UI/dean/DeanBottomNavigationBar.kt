package com.example.projekatfaza23.UI.dean

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun DeanBottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDirectory: () -> Unit
){
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {if(currentRoute!="home") onNavigateToHome() },
            icon = {Icon(Icons.Default.Home, contentDescription = "Pocetna")},
            label = {Text("Početna")},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF004D61),
                selectedTextColor = Color(0xFF004D61),
                indicatorColor = Color(0xFFE0F7FA)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = { if (currentRoute != "history") onNavigateToHistory() },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Historija") },
            label = { Text("Historija") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF004D61),
                selectedTextColor = Color(0xFF004D61),
                indicatorColor = Color(0xFFE0F7FA)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "directory",
            onClick = { if (currentRoute != "directory") onNavigateToDirectory() },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Direktorij") },
            label = { Text("Direktorij") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF004D61),
                selectedTextColor = Color(0xFF004D61),
                indicatorColor = Color(0xFFE0F7FA)
            )
        )
    }
}