package com.deadpixeldetective

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Application class for Dead Pixel Detective.
 * Initializes AdMob with test device configuration.
 */
class DeadPixelDetectiveApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeAds()
    }

    private fun initializeAds() {
        // Configure test devices for AdMob
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(
                "EMULATOR", // All emulators
                RequestConfiguration.DEVICE_ID_EMULATOR
            ))
            .build()
        MobileAds.setRequestConfiguration(configuration)
        
        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this) { }
    }
}
