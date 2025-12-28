package com.deadpixeldetective.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deadpixeldetective.ui.screens.*
import com.deadpixeldetective.ui.theme.DeadPixelDetectiveTheme
import com.deadpixeldetective.viewmodel.GameViewModel
import com.deadpixeldetective.viewmodel.SettingsViewModel

/**
 * Navigation destinations for the app.
 */
sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Game : Screen("game")
    data object Settings : Screen("settings")
    data object HowToPlay : Screen("how_to_play")
    data object About : Screen("about")
    data object LevelComplete : Screen("level_complete")
    data object GameOver : Screen("game_over")
}

/**
 * Main navigation host for the app.
 */
@Composable
fun DeadPixelDetectiveNavHost(
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val userPreferences by settingsViewModel.userPreferences.collectAsState()
    
    DeadPixelDetectiveTheme(highContrast = userPreferences.highContrastMode) {
        NavHost(
            navController = navController,
            startDestination = Screen.Menu.route
        ) {
            composable(Screen.Menu.route) {
                MenuScreen(
                    onPlayClick = {
                        gameViewModel.startNewGame()
                        navController.navigate(Screen.Game.route)
                    },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onHowToPlayClick = { navController.navigate(Screen.HowToPlay.route) },
                    onAboutClick = { navController.navigate(Screen.About.route) },
                    highScore = gameViewModel.highScore.collectAsState().value,
                    highestLevel = gameViewModel.highestLevel.collectAsState().value
                )
            }
            
            composable(Screen.Game.route) {
                GameScreen(
                    viewModel = gameViewModel,
                    userPreferences = userPreferences,
                    onLevelComplete = { navController.navigate(Screen.LevelComplete.route) },
                    onGameOver = { navController.navigate(Screen.GameOver.route) },
                    onBackToMenu = { 
                        navController.popBackStack(Screen.Menu.route, inclusive = false)
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.HowToPlay.route) {
                HowToPlayScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.LevelComplete.route) {
                val gameState by gameViewModel.gameState.collectAsState()
                LevelCompleteScreen(
                    score = gameState.score,
                    levelNumber = gameViewModel.getCurrentLevelNumber(),
                    onNextLevel = {
                        gameViewModel.nextLevel()
                        navController.popBackStack(Screen.Game.route, inclusive = false)
                    },
                    onBackToMenu = {
                        navController.popBackStack(Screen.Menu.route, inclusive = false)
                    }
                )
            }
            
            composable(Screen.GameOver.route) {
                val gameState by gameViewModel.gameState.collectAsState()
                GameOverScreen(
                    score = gameState.score,
                    levelNumber = gameViewModel.getCurrentLevelNumber(),
                    onTryAgain = {
                        gameViewModel.retryLevel()
                        navController.popBackStack(Screen.Game.route, inclusive = false)
                    },
                    onBackToMenu = {
                        navController.popBackStack(Screen.Menu.route, inclusive = false)
                    }
                )
            }
        }
    }
}
