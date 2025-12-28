package com.deadpixeldetective.ads

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Ad configuration.
 */
class AdConfigTest {
    
    @Test
    fun `banner test ad unit ID is correct`() {
        assertEquals(
            "ca-app-pub-3940256099942544/6300978111",
            AdUnitIds.BANNER_TEST
        )
    }
    
    @Test
    fun `interstitial test ad unit ID is correct`() {
        assertEquals(
            "ca-app-pub-3940256099942544/1033173712",
            AdUnitIds.INTERSTITIAL_TEST
        )
    }
    
    @Test
    fun `test ad unit IDs are not production IDs`() {
        // Test IDs should contain the test app ID prefix
        assertTrue(AdUnitIds.BANNER_TEST.contains("ca-app-pub-3940256099942544"))
        assertTrue(AdUnitIds.INTERSTITIAL_TEST.contains("ca-app-pub-3940256099942544"))
    }
}
