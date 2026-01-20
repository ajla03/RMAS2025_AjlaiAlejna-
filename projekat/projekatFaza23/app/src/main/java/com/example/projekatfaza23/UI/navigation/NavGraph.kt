package com.example.projekatfaza23.UI.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projekatfaza23.UI.dean.DeanHomeScreen
import com.example.projekatfaza23.UI.home.ClientHomeScreen
import com.example.projekatfaza23.UI.request.InboxRequestViewModel
import com.example.projekatfaza23.UI.login.LoginScreen
import com.example.projekatfaza23.UI.request.NewRequestScreen

@Composable
fun AppNavigation (navController : NavHostController) {
    val sharedViewModel : InboxRequestViewModel = viewModel()
    NavHost (
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Login> {
            LoginScreen(viewModel(), {
                destinationScreen ->
                navController.navigate(destinationScreen){
                    popUpTo<Screen.Login> {
                        inclusive = true
                    }
                }
            })
        }

        composable<Screen.Home>{
            ClientHomeScreen(
                viewModel = sharedViewModel,
                {navController.navigate(Screen.CreateRequest)
            })
        }

        composable<Screen.CreateRequest> {
            NewRequestScreen(viewModel = sharedViewModel, navigateHome = {navController.popBackStack()})
        }

        composable<Screen.DeanHome>{
            DeanHomeScreen()
        }


    }
}