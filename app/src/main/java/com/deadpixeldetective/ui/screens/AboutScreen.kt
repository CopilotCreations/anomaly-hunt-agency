package com.deadpixeldetective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.deadpixeldetective.ui.theme.*

/**
 * About screen with app information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Dead Pixel Detective",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyLarge,
                color = SecondaryVariant
            )
            
            Text(
                text = "A perception-based puzzle game where you hunt for subtle visual anomalies.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            HorizontalDivider(color = DarkSurface)
            
            InfoSection(
                title = "Privacy",
                content = "This app works 100% offline. We do not collect any personal data. " +
                        "Ads are displayed using Google AdMob with non-personalized settings."
            )
            
            InfoSection(
                title = "Sensor Usage",
                content = "The app uses device sensors (accelerometer, rotation) to create " +
                        "dynamic gameplay mechanics. Sensor data is processed locally and never transmitted."
            )
            
            InfoSection(
                title = "Accessibility",
                content = "We strive to make this game accessible to all players. " +
                        "Enable high contrast mode, reduced motion, or hints in Settings."
            )
            
            HorizontalDivider(color = DarkSurface)
            
            Text(
                text = "© 2024 Dead Pixel Detective",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            
            Text(
                text = "All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, content: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
