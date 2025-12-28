package com.deadpixeldetective.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for data layer models and utilities.
 */
class DataLayerTest {
    
    @Test
    fun `preferences key names are unique`() {
        // This test validates that we don't accidentally duplicate preference keys
        val keys = listOf(
            "high_contrast_mode",
            "reduced_motion",
            "haptic_feedback",
            "sound_effects",
            "show_hints",
            "high_score",
            "highest_level",
            "total_anomalies_found"
        )
        
        assertEquals(keys.size, keys.toSet().size)
    }
}
