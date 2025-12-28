# Architecture Documentation

## Overview

Dead Pixel Detective follows the **MVVM (Model-View-ViewModel)** architecture pattern with **unidirectional data flow**. The app is built entirely with Jetpack Compose for the UI layer.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                           UI Layer                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ MenuScreen  │  │ GameScreen  │  │ SettingsScreen + others │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                      │                │
│         └────────────────┼──────────────────────┘                │
│                          │                                       │
│                          ▼                                       │
│                ┌─────────────────┐                               │
│                │   Navigation    │                               │
│                └─────────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ViewModel Layer                             │
│  ┌─────────────────────┐      ┌─────────────────────────────┐   │
│  │    GameViewModel    │      │     SettingsViewModel       │   │
│  │  - gameState        │      │  - userPreferences          │   │
│  │  - sensorState      │      └─────────────────────────────┘   │
│  │  - animationProgress│                                        │
│  └─────────┬───────────┘                                        │
└────────────┼────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Domain Layer                               │
│  ┌─────────────────┐  ┌───────────────┐  ┌───────────────────┐  │
│  │ PuzzleGenerator │  │  TapDetector  │  │VisibilityCalculator│ │
│  └─────────────────┘  └───────────────┘  └───────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Data Layer                                │
│  ┌───────────────────────┐      ┌───────────────────────────┐   │
│  │  PreferencesRepository│      │      SensorManager        │   │
│  │  (DataStore)          │      │  (Accelerometer, Rotation)│   │
│  └───────────────────────┘      └───────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### UI Layer (`ui/`)

The UI layer consists of Jetpack Compose screens and components.

**Screens**:
- `MenuScreen`: Main menu with play, settings, and navigation options
- `GameScreen`: The main gameplay canvas with anomaly rendering
- `SettingsScreen`: Accessibility and preference toggles
- `HowToPlayScreen`: Game instructions
- `AboutScreen`: App information
- `LevelCompleteScreen`: Victory celebration
- `GameOverScreen`: Failure state with retry option

**Navigation**:
- Uses Jetpack Navigation Compose
- Sealed class `Screen` defines all routes
- `NavHost` manages screen transitions

**Theme**:
- Dark theme optimized for visibility game
- High contrast mode for accessibility
- Custom color palette for game elements

### ViewModel Layer (`viewmodel/`)

ViewModels manage UI state and handle business logic.

**GameViewModel**:
```kotlin
class GameViewModel : AndroidViewModel {
    val gameState: StateFlow<GameState>       // Current game state
    val sensorState: StateFlow<SensorState>   // Device sensor data
    val animationProgress: StateFlow<Float>    // Animation timing
    
    fun startNewGame()                         // Initialize game
    fun loadLevel(levelNumber: Int)            // Load specific level
    fun onTap(normalizedX: Float, normalizedY: Float)  // Handle tap
    fun getAnomalyVisibility(anomaly: Anomaly): Float  // Calculate visibility
}
```

**SettingsViewModel**:
```kotlin
class SettingsViewModel : AndroidViewModel {
    val userPreferences: StateFlow<UserPreferences>
    
    fun setHighContrastMode(enabled: Boolean)
    fun setReducedMotion(enabled: Boolean)
    fun setHapticFeedback(enabled: Boolean)
    // ... other preference setters
}
```

### Domain Layer (`game/`)

Pure business logic with no Android dependencies (except where unavoidable).

**PuzzleGenerator**:
- Generates levels with appropriate difficulty
- Creates anomalies with varied types and visibility conditions
- Deterministic with seeded random for testing

**TapDetector**:
- Checks if taps hit any anomalies
- Calculates distance from tap to anomaly center
- Computes score based on accuracy and difficulty

**VisibilityCalculator**:
- Determines anomaly visibility based on sensor state
- Handles all visibility condition types
- Returns 0.0-1.0 visibility multiplier

### Data Layer

**PreferencesRepository**:
- Wraps Android DataStore
- Provides reactive flows for preferences
- Handles persistence of user settings and game progress

**SensorManager**:
- Abstracts device sensor access
- Provides reactive flows for sensor data
- Handles accelerometer, rotation vector, brightness, orientation

### Model Layer (`model/`)

Data classes representing game entities:

```kotlin
data class Anomaly(
    val id: String,
    val type: AnomalyType,
    val x: Float, val y: Float,
    val radius: Float,
    val visibilityCondition: VisibilityCondition
)

data class Level(
    val number: Int,
    val anomalies: List<Anomaly>,
    val maxAttempts: Int,
    val difficulty: Difficulty
)

data class GameState(
    val currentLevel: Level?,
    val score: Int,
    val attemptsRemaining: Int,
    val foundAnomalies: Set<String>,
    val isLevelComplete: Boolean,
    val isGameOver: Boolean
)

data class SensorState(
    val brightness: Float,
    val isRotating: Boolean,
    val isLandscape: Boolean,
    // ...
)
```

## Data Flow

### Unidirectional Data Flow

```
User Action → ViewModel → Update State → UI Recomposition
     ↑                                          │
     └──────────────────────────────────────────┘
```

1. User taps on screen
2. `GameScreen` calls `viewModel.onTap(x, y)`
3. `GameViewModel` processes tap through `TapDetector`
4. State updates in `_gameState` MutableStateFlow
5. UI observes `gameState` StateFlow and recomposes

### Sensor Data Flow

```
Device Sensors → SensorManager → StateFlow → ViewModel → UI
```

1. Sensors emit raw data
2. `SensorManager` transforms to `SensorState`
3. `GameViewModel` collects sensor flow
4. `VisibilityCalculator` uses sensor state for anomaly visibility
5. Game canvas renders anomalies with calculated visibility

## Key Design Decisions

### Why Compose Canvas for Rendering?

The game uses Compose `Canvas` for rendering anomalies because:
1. Full control over pixel-level rendering
2. Efficient for the subtle visual effects needed
3. Integrates seamlessly with Compose animations
4. Allows dynamic response to sensor data

### Why StateFlow over LiveData?

StateFlow is preferred because:
1. Better null safety (always has a value)
2. Native Kotlin coroutines integration
3. More predictable behavior in Compose
4. Built-in equality checks prevent unnecessary recomposition

### Why DataStore over SharedPreferences?

DataStore provides:
1. Asynchronous, non-blocking API
2. Type safety with Kotlin
3. Consistency guarantees
4. Better error handling

### Why No Dependency Injection Framework?

For this app's scope:
1. Manual DI is sufficient
2. Reduces APK size
3. Faster build times
4. Simpler debugging

For scaling, Hilt could be added later.

## Testing Strategy

### Unit Tests

Located in `app/src/test/`:
- `PuzzleLogicTest`: Tests puzzle generation, tap detection, visibility calculation
- `GameModelsTest`: Tests data class behavior

Key testing principles:
- Use fixed random seeds for determinism
- Test edge cases (boundaries, zero values)
- Verify all difficulty levels

### Instrumentation Tests

Located in `app/src/androidTest/`:
- `MainActivityTest`: UI component tests with Compose Testing

## Performance Considerations

### Animation Efficiency

- Use `rememberInfiniteTransition` for continuous animations
- Reduced motion mode increases animation duration, reducing CPU usage
- Sensor updates throttled with `SENSOR_DELAY_UI`

### Memory Management

- Anomalies are generated on-demand per level
- Tap history limited to prevent memory growth
- No image assets (all rendering is procedural)

### Battery Optimization

- Low-frequency sensor sampling
- Animations pause when app is backgrounded
- No background services or workers

## Extensibility Points

### Adding New Anomaly Types

1. Add to `AnomalyType` enum
2. Add rendering logic in `GameScreen.drawAnomaly()`
3. Add visibility logic if needed in `VisibilityCalculator`

### Adding New Visibility Conditions

1. Add to `VisibilityCondition` sealed class
2. Implement calculation in `VisibilityCalculator`
3. Add to `PuzzleGenerator.selectVisibilityCondition()`

### Adding Sound Effects

1. Place CC0 audio files in `res/raw/`
2. Create `SoundManager` class
3. Trigger sounds from `GameViewModel` on events
