package com.example.projekatfaza23.UI.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projekatfaza23.UI.dean.ApproveRequestScreen
import com.example.projekatfaza23.UI.dean.DeanDirectoryScreen
import com.example.projekatfaza23.UI.dean.DeanHistoryScreen
import com.example.projekatfaza23.UI.dean.DeanHomeScreen
import com.example.projekatfaza23.UI.dean.DeanViewModel
import com.example.projekatfaza23.UI.home.ClientHomeScreen
import com.example.projekatfaza23.UI.request.InboxRequestViewModel
import com.example.projekatfaza23.UI.login.LoginScreen
import com.example.projekatfaza23.UI.request.NewRequestScreen
import com.example.projekatfaza23.UI.secretary.SecretaryHistoryScreen
import com.example.projekatfaza23.UI.secretary.SecretaryHomeScreen
import com.example.projekatfaza23.UI.secretary.SecretaryValidateScreen
import com.example.projekatfaza23.UI.secretary.SecretaryViewModel

@Composable
fun AppNavigation (navController : NavHostController) {
    val sharedViewModel : InboxRequestViewModel = viewModel()
    val sharedDeanViewModel : DeanViewModel = viewModel()
    val sharedSecretaryViewModel : SecretaryViewModel = viewModel()

    NavHost (
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Login> {
            LoginScreen(viewModel(), {
                destinationScreen ->
                navController.navigate(destinationScreen){
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                    }
                }
            )
        }

        composable<Screen.Home>{
            ClientHomeScreen(
                viewModel = sharedViewModel,
                {navController.navigate(Screen.CreateRequest)},
                { navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                    }
                })
        }

        composable<Screen.CreateRequest> {
            NewRequestScreen(viewModel = sharedViewModel, navigateHome = {navController.popBackStack()})
        }

        composable<Screen.DeanHome>{
            DeanHomeScreen(viewModel = sharedDeanViewModel,
                onNavigateToDirectory = {navController.navigate((Screen.DeanDirectory))},
                navigateRequest = {navController.navigate(Screen.ApproveRequestScreen)},
                onLogoutClick = {
                    navController.navigate(Screen.Login){
                        // brisemo sve sa stacka
                        popUpTo(navController.graph.id){
                            inclusive = true
                        }
                        // da se sprijeci kreiranje visestrukih login ekrana
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {navController.navigate(Screen.DeanHistoryScreen)},
                onSwitchRole = {
                    navController.navigate(Screen.Home) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Screen.DeanDirectory> {
            DeanDirectoryScreen(
                viewModel = sharedDeanViewModel,
                navigateHome = {
                    navController.navigate(Screen.DeanHome) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }

        composable<Screen.DeanHistoryScreen> {
            DeanHistoryScreen(
                sharedDeanViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.DeanHome) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },                navigateRequest = {navController.navigate(Screen.ApproveRequestScreen)},
                onNavigateToDirectory = {navController.navigate(Screen.DeanDirectory)}
            )
        }

        composable<Screen.ApproveRequestScreen>{
            ApproveRequestScreen(sharedDeanViewModel, navigateHome = {navController.popBackStack()})
        }

        composable<Screen.SecretaryHomeScreen>{
            SecretaryHomeScreen(sharedSecretaryViewModel,
                onLogoutClicked = {
                    navController.navigate(Screen.Login) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToValidate = {navController.navigate(Screen.SecretaryValidateScreen)},
                onNavigateToHistory = {navController.navigate(Screen.SecretaryHistoryScreen)})
        }

        composable<Screen.SecretaryValidateScreen>{
            SecretaryValidateScreen(sharedSecretaryViewModel, navigateHome = {navController.popBackStack()})
        }

        composable<Screen.SecretaryHistoryScreen> {
            SecretaryHistoryScreen(
                sharedSecretaryViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.SecretaryHomeScreen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToRequest = {navController.navigate(Screen.SecretaryValidateScreen)})
        }

    }
}