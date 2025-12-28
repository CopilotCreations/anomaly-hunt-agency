package com.deadpixeldetective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deadpixeldetective.ui.theme.*
import com.deadpixeldetective.viewmodel.SettingsViewModel

/**
 * Settings screen with accessibility options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val preferences by viewModel.userPreferences.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Accessibility section
            Text(
                text = "Accessibility",
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsSwitch(
                title = "High Contrast Mode",
                description = "Increases visibility of anomalies",
                checked = preferences.highContrastMode,
                onCheckedChange = { viewModel.setHighContrastMode(it) }
            )
            
            SettingsSwitch(
                title = "Reduced Motion",
                description = "Slows down animations",
                checked = preferences.reducedMotion,
                onCheckedChange = { viewModel.setReducedMotion(it) }
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = DarkSurface
            )
            
            // Feedback section
            Text(
                text = "Feedback",
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsSwitch(
                title = "Haptic Feedback",
                description = "Vibrate on taps and discoveries",
                checked = preferences.hapticFeedback,
                onCheckedChange = { viewModel.setHapticFeedback(it) }
            )
            
            SettingsSwitch(
                title = "Sound Effects",
                description = "Play sounds during gameplay",
                checked = preferences.soundEffects,
                onCheckedChange = { viewModel.setSoundEffects(it) }
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = DarkSurface
            )
            
            // Gameplay section
            Text(
                text = "Gameplay",
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsSwitch(
                title = "Show Hints",
                description = "Display hints about visibility conditions",
                checked = preferences.showHints,
                onCheckedChange = { viewModel.setShowHints(it) }
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SecondaryVariant,
                    checkedTrackColor = SecondaryVariant.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = DarkBackground
                )
            )
        }
    }
}
