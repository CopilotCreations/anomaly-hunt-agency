package com.deadpixeldetective.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * AdMob test ad unit IDs.
 * These are official Google test IDs that work on any device.
 */
object AdUnitIds {
    const val BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"
}

/**
 * Composable that displays a banner ad.
 * Uses non-personalized ads for privacy compliance.
 */
@Composable
fun BannerAd(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdUnitIds.BANNER_TEST
                
                // Request non-personalized ads
                val adRequest = AdRequest.Builder()
                    .build()
                
                loadAd(adRequest)
            }
        }
    )
}

/**
 * Manager for interstitial ads between levels.
 */
class InterstitialAdManager(private val context: Context) {
    
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    
    /**
     * Preloads an interstitial ad.
     */
    fun preloadAd() {
        if (isLoading || interstitialAd != null) return
        
        isLoading = true
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            AdUnitIds.INTERSTITIAL_TEST,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }
    
    /**
     * Shows the interstitial ad if available.
     * @param activity The activity context
     * @param onAdDismissed Callback when ad is dismissed
     */
    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadAd() // Preload next ad
                    onAdDismissed()
                }
                
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    preloadAd()
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // No ad available, proceed anyway
            preloadAd()
            onAdDismissed()
        }
    }
    
    /**
     * Checks if an ad is ready to show.
     */
    fun isAdReady(): Boolean = interstitialAd != null
}

/**
 * Creates a remembered InterstitialAdManager.
 */
@Composable
fun rememberInterstitialAdManager(): InterstitialAdManager {
    val context = LocalContext.current
    return remember {
        InterstitialAdManager(context).also { it.preloadAd() }
    }
}
