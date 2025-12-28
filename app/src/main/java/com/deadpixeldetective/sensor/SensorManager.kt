package com.deadpixeldetective.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import com.deadpixeldetective.model.SensorState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

/**
 * Manages device sensor data for game mechanics.
 * Provides reactive streams of sensor updates.
 */
class SensorManager(
    private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val rotationVector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    
    companion object {
        private const val ROTATION_THRESHOLD = 2.0f
        private const val SENSOR_DELAY = android.hardware.SensorManager.SENSOR_DELAY_UI
    }
    
    /**
     * Provides a flow of accelerometer data.
     */
    fun accelerometerFlow(): Flow<Triple<Float, Float, Float>> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(Triple(event.values[0], event.values[1], event.values[2]))
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SENSOR_DELAY)
        } ?: trySend(Triple(0f, 0f, 9.8f))
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Provides a flow of rotation detection.
     */
    fun rotationFlow(): Flow<Pair<Boolean, Float>> = callbackFlow {
        var lastRotation = FloatArray(3)
        var isRotating = false
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rotationMatrix = FloatArray(9)
                val orientationValues = FloatArray(3)
                
                android.hardware.SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                android.hardware.SensorManager.getOrientation(rotationMatrix, orientationValues)
                
                val rotationDelta = abs(orientationValues[0] - lastRotation[0]) +
                        abs(orientationValues[1] - lastRotation[1]) +
                        abs(orientationValues[2] - lastRotation[2])
                
                isRotating = rotationDelta > ROTATION_THRESHOLD * 0.01f
                lastRotation = orientationValues.copyOf()
                
                trySend(Pair(isRotating, orientationValues[2]))
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        rotationVector?.let {
            sensorManager.registerListener(listener, it, SENSOR_DELAY)
        } ?: trySend(Pair(false, 0f))
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Provides a flow of brightness level (0.0 to 1.0).
     */
    fun brightnessFlow(): Flow<Float> = flow {
        while (true) {
            val brightness = try {
                val currentBrightness = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    128
                )
                currentBrightness / 255f
            } catch (_: Exception) {
                0.5f
            }
            emit(brightness)
            kotlinx.coroutines.delay(500)
        }
    }
    
    /**
     * Provides a flow of orientation (landscape/portrait).
     */
    fun orientationFlow(): Flow<Boolean> = flow {
        while (true) {
            val rotation = windowManager.defaultDisplay.rotation
            val isLandscape = rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
            emit(isLandscape)
            kotlinx.coroutines.delay(200)
        }
    }
    
    /**
     * Combines all sensor data into a single SensorState flow.
     */
    fun sensorStateFlow(): Flow<SensorState> = combine(
        accelerometerFlow(),
        rotationFlow(),
        brightnessFlow(),
        orientationFlow()
    ) { accel, rotation, brightness, isLandscape ->
        SensorState(
            brightness = brightness,
            isRotating = rotation.first,
            rotationAngle = rotation.second,
            isLandscape = isLandscape,
            isSystemUIVisible = true,
            accelerometerX = accel.first,
            accelerometerY = accel.second,
            accelerometerZ = accel.third
        )
    }
}
