# Dead Pixel Detective

A perception-based puzzle game for Android where players hunt for subtle visual anomalies rendered on screen.

## Overview

Dead Pixel Detective is a unique puzzle game that challenges players to find simulated visual artifacts that respond to device conditions like screen brightness, device rotation, and orientation changes. These are not actual hardware defects but carefully crafted anomalies that test your observational skills.

## Features

- **Perception-based gameplay**: Find subtle visual anomalies that mimic screen defects
- **Dynamic visibility**: Anomalies react to screen brightness, device rotation, and orientation
- **Progressive difficulty**: 7 anomaly types with increasing challenge across levels
- **Accessibility options**: High contrast mode, reduced motion, and haptic feedback toggles
- **100% offline**: Works without internet connection
- **Privacy-focused**: No personal data collection
- **Ad-supported**: Optional interstitial ads between levels (non-personalized)

## Requirements

- Android 10 (API 29) or higher
- Android Studio Hedgehog or newer
- JDK 17

## Building the App

### Prerequisites

1. Install Android Studio Hedgehog (2023.1.1) or newer
2. Install JDK 17
3. Clone this repository

### Build Steps

```bash
# Clone the repository
git clone <repository-url>
cd anomaly-hunt-agency

# Build debug APK
./gradlew assembleDebug

# The APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

### Running on Device/Emulator

```bash
# Install on connected device
./gradlew installDebug

# Or run from Android Studio
# 1. Open the project in Android Studio
# 2. Click Run > Run 'app'
```

## Project Structure

```
anomaly-hunt-agency/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/deadpixeldetective/
│   │   │   │   ├── ads/           # AdMob integration
│   │   │   │   ├── data/          # Data layer (preferences)
│   │   │   │   ├── game/          # Core game logic
│   │   │   │   ├── model/         # Data models
│   │   │   │   ├── sensor/        # Device sensor management
│   │   │   │   ├── ui/            # Compose UI
│   │   │   │   │   ├── screens/   # Screen composables
│   │   │   │   │   └── theme/     # Material theme
│   │   │   │   └── viewmodel/     # ViewModels
│   │   │   └── res/               # Resources
│   │   ├── test/                  # Unit tests
│   │   └── androidTest/           # Instrumentation tests
│   └── build.gradle.kts
├── docs/
│   ├── ARCHITECTURE.md
│   ├── USAGE.md
│   └── SUGGESTIONS.md
├── .github/workflows/
│   └── ci.yml
└── privacy_policy.md
```

## Architecture

The app follows MVVM architecture with:

- **ViewModels**: Manage game state and user preferences
- **Unidirectional data flow**: State flows down, events flow up
- **Compose-first UI**: 100% Jetpack Compose, no XML layouts
- **Clean separation**: Rendering, puzzle logic, and sensor input are isolated

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumentation tests (requires emulator/device)
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## Gameplay

1. **Objective**: Find subtle visual anomalies hidden on each level
2. **Tap**: Touch where you believe an anomaly is located
3. **Conditions**: Some anomalies only appear under specific conditions:
   - Low or high screen brightness
   - Device rotation
   - Portrait/landscape orientation
   - Specific animation timing

See [docs/USAGE.md](docs/USAGE.md) for detailed gameplay instructions.

## Privacy & Compliance

- **Offline-first**: All game logic runs locally
- **No data collection**: We don't collect any personal information
- **Non-personalized ads**: AdMob configured for privacy
- **Sensor data**: Used only for gameplay, never transmitted

See [privacy_policy.md](privacy_policy.md) and [play_store_compliance.md](play_store_compliance.md) for full details.

## License

Copyright 2024. All rights reserved.

## Attribution

This game uses the following:
- Google AdMob SDK (test ads only)
- Jetpack Compose
- Material 3 Design
