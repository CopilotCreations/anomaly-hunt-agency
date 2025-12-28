package com.deadpixeldetective.game

import com.deadpixeldetective.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for PuzzleGenerator.
 */
class PuzzleGeneratorTest {
    
    private lateinit var generator: PuzzleGenerator
    private val fixedRandom = Random(42) // Fixed seed for reproducible tests
    
    @Before
    fun setUp() {
        generator = PuzzleGenerator(fixedRandom)
    }
    
    @Test
    fun `generateLevel creates valid level with correct number`() {
        val level = generator.generateLevel(5)
        
        assertEquals(5, level.number)
        assertTrue(level.anomalies.isNotEmpty())
        assertTrue(level.maxAttempts > 0)
    }
    
    @Test
    fun `calculateDifficulty returns EASY for levels 1-3`() {
        assertEquals(Difficulty.EASY, generator.calculateDifficulty(1))
        assertEquals(Difficulty.EASY, generator.calculateDifficulty(2))
        assertEquals(Difficulty.EASY, generator.calculateDifficulty(3))
    }
    
    @Test
    fun `calculateDifficulty returns NORMAL for levels 4-7`() {
        assertEquals(Difficulty.NORMAL, generator.calculateDifficulty(4))
        assertEquals(Difficulty.NORMAL, generator.calculateDifficulty(5))
        assertEquals(Difficulty.NORMAL, generator.calculateDifficulty(7))
    }
    
    @Test
    fun `calculateDifficulty returns HARD for levels 8-12`() {
        assertEquals(Difficulty.HARD, generator.calculateDifficulty(8))
        assertEquals(Difficulty.HARD, generator.calculateDifficulty(10))
        assertEquals(Difficulty.HARD, generator.calculateDifficulty(12))
    }
    
    @Test
    fun `calculateDifficulty returns EXPERT for levels above 12`() {
        assertEquals(Difficulty.EXPERT, generator.calculateDifficulty(13))
        assertEquals(Difficulty.EXPERT, generator.calculateDifficulty(20))
        assertEquals(Difficulty.EXPERT, generator.calculateDifficulty(100))
    }
    
    @Test
    fun `calculateAnomalyCount increases with level number`() {
        val count1 = generator.calculateAnomalyCount(1, Difficulty.EASY)
        val count10 = generator.calculateAnomalyCount(10, Difficulty.HARD)
        val count15 = generator.calculateAnomalyCount(15, Difficulty.EXPERT)
        
        assertTrue(count1 >= 1)
        assertTrue(count10 >= count1)
        assertTrue(count15 >= count10)
    }
    
    @Test
    fun `calculateMaxAttempts varies by difficulty`() {
        val easyAttempts = generator.calculateMaxAttempts(1, Difficulty.EASY)
        val normalAttempts = generator.calculateMaxAttempts(5, Difficulty.NORMAL)
        val hardAttempts = generator.calculateMaxAttempts(10, Difficulty.HARD)
        val expertAttempts = generator.calculateMaxAttempts(15, Difficulty.EXPERT)
        
        assertEquals(5, easyAttempts)
        assertEquals(4, normalAttempts)
        assertEquals(3, hardAttempts)
        assertEquals(2, expertAttempts)
    }
    
    @Test
    fun `generateAnomaly creates anomaly within valid bounds`() {
        val anomaly = generator.generateAnomaly("test_id", 5, Difficulty.NORMAL)
        
        assertEquals("test_id", anomaly.id)
        assertTrue(anomaly.x in 0.1f..0.9f)
        assertTrue(anomaly.y in 0.1f..0.9f)
        assertTrue(anomaly.radius > 0)
        assertNotNull(anomaly.type)
        assertNotNull(anomaly.visibilityCondition)
    }
    
    @Test
    fun `calculateRadius decreases with difficulty`() {
        val easyRadius = generator.calculateRadius(Difficulty.EASY)
        val normalRadius = generator.calculateRadius(Difficulty.NORMAL)
        val hardRadius = generator.calculateRadius(Difficulty.HARD)
        val expertRadius = generator.calculateRadius(Difficulty.EXPERT)
        
        assertTrue(easyRadius > normalRadius)
        assertTrue(normalRadius > hardRadius)
        assertTrue(hardRadius > expertRadius)
    }
    
    @Test
    fun `selectAnomalyType returns valid type for difficulty`() {
        val easyType = generator.selectAnomalyType(1, Difficulty.EASY)
        val expertType = generator.selectAnomalyType(15, Difficulty.EXPERT)
        
        assertNotNull(easyType)
        assertNotNull(expertType)
        assertTrue(easyType in AnomalyType.entries)
        assertTrue(expertType in AnomalyType.entries)
    }
    
    @Test
    fun `generated levels have unique anomaly IDs`() {
        val level = generator.generateLevel(5)
        val ids = level.anomalies.map { it.id }
        
        assertEquals(ids.size, ids.toSet().size)
    }
    
    @Test
    fun `multiple levels have different configurations`() {
        val level1 = generator.generateLevel(1)
        val level10 = generator.generateLevel(10)
        
        assertNotEquals(level1.difficulty, level10.difficulty)
    }
}

/**
 * Unit tests for TapDetector.
 */
class TapDetectorTest {
    
    private lateinit var detector: TapDetector
    
    @Before
    fun setUp() {
        detector = TapDetector()
    }
    
    @Test
    fun `checkTap returns hit when tapping on anomaly`() {
        val anomaly = Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.05f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val result = detector.checkTap(0.5f, 0.5f, listOf(anomaly), emptySet())
        
        assertTrue(result.isHit)
        assertEquals(anomaly, result.anomalyFound)
    }
    
    @Test
    fun `checkTap returns miss when tapping far from anomaly`() {
        val anomaly = Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val result = detector.checkTap(0.1f, 0.1f, listOf(anomaly), emptySet())
        
        assertFalse(result.isHit)
        assertNull(result.anomalyFound)
    }
    
    @Test
    fun `checkTap ignores already found anomalies`() {
        val anomaly = Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.05f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val result = detector.checkTap(0.5f, 0.5f, listOf(anomaly), setOf("test"))
        
        assertFalse(result.isHit)
        assertNull(result.anomalyFound)
    }
    
    @Test
    fun `checkTap hits closest anomaly when multiple are near`() {
        val anomaly1 = Anomaly(
            id = "far",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.55f,
            radius = 0.05f,
            visibilityCondition = VisibilityCondition.Always
        )
        val anomaly2 = Anomaly(
            id = "close",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.51f,
            radius = 0.05f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val result = detector.checkTap(0.5f, 0.5f, listOf(anomaly1, anomaly2), emptySet())
        
        assertTrue(result.isHit)
        assertEquals("close", result.anomalyFound?.id)
    }
    
    @Test
    fun `calculateDistance returns correct distance`() {
        val distance = detector.calculateDistance(0f, 0f, 3f, 4f)
        assertEquals(5f, distance, 0.001f)
    }
    
    @Test
    fun `calculateDistance returns zero for same point`() {
        val distance = detector.calculateDistance(5f, 5f, 5f, 5f)
        assertEquals(0f, distance, 0.001f)
    }
    
    @Test
    fun `calculateScore returns higher score for harder difficulty`() {
        val easyScore = detector.calculateScore(0.01f, 5, 5, Difficulty.EASY)
        val expertScore = detector.calculateScore(0.01f, 2, 2, Difficulty.EXPERT)
        
        assertTrue(expertScore > easyScore)
    }
    
    @Test
    fun `calculateScore returns higher score for more remaining attempts`() {
        val highAttempts = detector.calculateScore(0.01f, 5, 5, Difficulty.NORMAL)
        val lowAttempts = detector.calculateScore(0.01f, 1, 5, Difficulty.NORMAL)
        
        assertTrue(highAttempts > lowAttempts)
    }
    
    @Test
    fun `calculateScore returns higher score for closer tap`() {
        val closeScore = detector.calculateScore(0.01f, 3, 5, Difficulty.NORMAL)
        val farScore = detector.calculateScore(0.08f, 3, 5, Difficulty.NORMAL)
        
        assertTrue(closeScore > farScore)
    }
}

/**
 * Unit tests for VisibilityCalculator.
 */
class VisibilityCalculatorTest {
    
    private lateinit var calculator: VisibilityCalculator
    
    @Before
    fun setUp() {
        calculator = VisibilityCalculator()
    }
    
    @Test
    fun `Always condition returns full visibility`() {
        val anomaly = createAnomaly(VisibilityCondition.Always)
        val sensorState = SensorState()
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `LowBrightness condition returns full visibility at low brightness`() {
        val anomaly = createAnomaly(VisibilityCondition.LowBrightness(0.3f))
        val sensorState = SensorState(brightness = 0.2f)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `LowBrightness condition returns reduced visibility at high brightness`() {
        val anomaly = createAnomaly(VisibilityCondition.LowBrightness(0.3f))
        val sensorState = SensorState(brightness = 0.8f)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertTrue(visibility < 1.0f)
        assertTrue(visibility >= 0.3f)
    }
    
    @Test
    fun `HighBrightness condition returns full visibility at high brightness`() {
        val anomaly = createAnomaly(VisibilityCondition.HighBrightness(0.7f))
        val sensorState = SensorState(brightness = 0.9f)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `DuringRotation condition returns full visibility when rotating`() {
        val anomaly = createAnomaly(VisibilityCondition.DuringRotation)
        val sensorState = SensorState(isRotating = true)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `DuringRotation condition returns reduced visibility when not rotating`() {
        val anomaly = createAnomaly(VisibilityCondition.DuringRotation)
        val sensorState = SensorState(isRotating = false)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(0.2f, visibility, 0.001f)
    }
    
    @Test
    fun `SpecificOrientation condition returns full visibility in correct orientation`() {
        val anomaly = createAnomaly(VisibilityCondition.SpecificOrientation(isLandscape = true))
        val sensorState = SensorState(isLandscape = true)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `SpecificOrientation condition returns reduced visibility in wrong orientation`() {
        val anomaly = createAnomaly(VisibilityCondition.SpecificOrientation(isLandscape = true))
        val sensorState = SensorState(isLandscape = false)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(0.15f, visibility, 0.001f)
    }
    
    @Test
    fun `AnimationPhase condition returns full visibility in phase range`() {
        val anomaly = createAnomaly(VisibilityCondition.AnimationPhase(0.2f, 0.4f))
        val sensorState = SensorState()
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.3f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    @Test
    fun `AnimationPhase condition returns reduced visibility outside phase range`() {
        val anomaly = createAnomaly(VisibilityCondition.AnimationPhase(0.2f, 0.4f))
        val sensorState = SensorState()
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.6f)
        
        assertEquals(0.1f, visibility, 0.001f)
    }
    
    @Test
    fun `SystemUIHidden condition returns full visibility when UI is hidden`() {
        val anomaly = createAnomaly(VisibilityCondition.SystemUIHidden)
        val sensorState = SensorState(isSystemUIVisible = false)
        
        val visibility = calculator.calculateVisibility(anomaly, sensorState, 0.5f)
        
        assertEquals(1.0f, visibility, 0.001f)
    }
    
    private fun createAnomaly(condition: VisibilityCondition): Anomaly {
        return Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = condition
        )
    }
}
