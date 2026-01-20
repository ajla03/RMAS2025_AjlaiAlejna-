package com.example.projekatfaza23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.projekatfaza23.UI.dean.DeanHomeScreen
import com.example.projekatfaza23.UI.navigation.AppNavigation
import com.example.projekatfaza23.UI.navigation.Screen
import com.example.projekatfaza23.UI.request.NewRequestScreen
import com.example.projekatfaza23.ui.theme.ProjekatFaza23Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjekatFaza23Theme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AppNavigation(navController = navController)
                }
            }
        }
    }
}

