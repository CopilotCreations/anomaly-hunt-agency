package com.deadpixeldetective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deadpixeldetective.ui.theme.*

/**
 * How to Play screen with game instructions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to Play") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Hunt for subtle visual anomalies hidden on your screen!",
                style = MaterialTheme.typography.headlineSmall,
                color = SecondaryVariant
            )
            
            InstructionSection(
                title = "The Goal",
                content = "Each level contains one or more subtle visual anomalies. " +
                        "Your job is to find them by tapping on their location."
            )
            
            InstructionSection(
                title = "Types of Anomalies",
                items = listOf(
                    "Pixel offsets - A single pixel slightly out of place",
                    "Subtle gradients - Barely visible color shifts",
                    "Temporal flickers - Pixels that appear and disappear",
                    "Pixel clusters - Small groups of misaligned pixels",
                    "Color shifts - Slight hue variations"
                )
            )
            
            InstructionSection(
                title = "Visibility Conditions",
                content = "Some anomalies only become visible under certain conditions:",
                items = listOf(
                    "Low brightness - Dim your screen",
                    "High brightness - Increase screen brightness",
                    "Device rotation - Rotate your device",
                    "Orientation - Switch between portrait/landscape",
                    "Animation timing - Wait for the right moment"
                )
            )
            
            InstructionSection(
                title = "Scoring",
                items = listOf(
                    "Higher difficulty levels give more points",
                    "Finding anomalies quickly earns bonus points",
                    "Using fewer attempts increases your score"
                )
            )
            
            InstructionSection(
                title = "Tips",
                items = listOf(
                    "Look carefully at the screen edges",
                    "Pay attention to subtle movements",
                    "Try different viewing angles",
                    "Enable hints in settings if you're stuck"
                )
            )
        }
    }
}

@Composable
private fun InstructionSection(
    title: String,
    content: String? = null,
    items: List<String> = emptyList()
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        content?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        
        for (item in items) {
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
