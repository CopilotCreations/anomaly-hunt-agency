package com.deadpixeldetective

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deadpixeldetective.ui.screens.MenuScreen
import com.deadpixeldetective.ui.theme.DeadPixelDetectiveTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for UI components.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun menuScreen_displaysTitle() {
        composeTestRule.setContent {
            DeadPixelDetectiveTheme {
                MenuScreen(
                    onPlayClick = {},
                    onSettingsClick = {},
                    onHowToPlayClick = {},
                    onAboutClick = {},
                    highScore = 0,
                    highestLevel = 1
                )
            }
        }
        
        composeTestRule.onNodeWithText("Dead Pixel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Detective").assertIsDisplayed()
    }
    
    @Test
    fun menuScreen_displaysPlayButton() {
        composeTestRule.setContent {
            DeadPixelDetectiveTheme {
                MenuScreen(
                    onPlayClick = {},
                    onSettingsClick = {},
                    onHowToPlayClick = {},
                    onAboutClick = {},
                    highScore = 0,
                    highestLevel = 1
                )
            }
        }
        
        composeTestRule.onNodeWithText("PLAY").assertIsDisplayed()
    }
    
    @Test
    fun menuScreen_displaysSettingsButton() {
        composeTestRule.setContent {
            DeadPixelDetectiveTheme {
                MenuScreen(
                    onPlayClick = {},
                    onSettingsClick = {},
                    onHowToPlayClick = {},
                    onAboutClick = {},
                    highScore = 0,
                    highestLevel = 1
                )
            }
        }
        
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }
    
    @Test
    fun menuScreen_playButtonClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            DeadPixelDetectiveTheme {
                MenuScreen(
                    onPlayClick = { clicked = true },
                    onSettingsClick = {},
                    onHowToPlayClick = {},
                    onAboutClick = {},
                    highScore = 0,
                    highestLevel = 1
                )
            }
        }
        
        composeTestRule.onNodeWithText("PLAY").performClick()
        assert(clicked)
    }
    
    @Test
    fun menuScreen_displaysHighScore() {
        composeTestRule.setContent {
            DeadPixelDetectiveTheme {
                MenuScreen(
                    onPlayClick = {},
                    onSettingsClick = {},
                    onHowToPlayClick = {},
                    onAboutClick = {},
                    highScore = 500,
                    highestLevel = 5
                )
            }
        }
        
        composeTestRule.onNodeWithText("High Score: 500").assertIsDisplayed()
        composeTestRule.onNodeWithText("Highest Level: 5").assertIsDisplayed()
    }
}
