package com.example.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

object UnityAdsHelper {
    private const val UNITY_GAME_ID = "800076939"
    private const val TEST_MODE = false

    fun initialize(context: Context) {
        if (!UnityAds.isInitialized) {
            UnityAds.initialize(
                context,
                UNITY_GAME_ID,
                TEST_MODE,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        // Successfully initialized
                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        // Failed to initialize
                    }
                }
            )
        }
    }
}

@Composable
fun AdBannerComponent() {
    // A visual placeholder for the AdBanner
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Unity Ad Banner Placeholder", color = Color.White)
    }
}
