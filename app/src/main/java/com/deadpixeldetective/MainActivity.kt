package com.deadpixeldetective

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.deadpixeldetective.ui.DeadPixelDetectiveNavHost
import com.deadpixeldetective.ui.theme.DeadPixelDetectiveTheme

/**
 * Main entry point for the Dead Pixel Detective game.
 * Sets up edge-to-edge display and Compose content.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            DeadPixelDetectiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    DeadPixelDetectiveNavHost()
                }
            }
        }
    }
}
