package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

object UnityAdsHelper {
    private const val UNITY_GAME_ID = "800076939"
    private const val TEST_MODE = false
    const val PLACEMENT_ID_BANNER = "Banner_Android"

    fun initialize(context: Context) {
        if (!UnityAds.isInitialized) {
            UnityAds.initialize(
                context,
                UNITY_GAME_ID,
                TEST_MODE,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        Log.d("UnityAdsHelper", "Initialized successfully")
                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        Log.e("UnityAdsHelper", "Initialization failed: $error $message")
                    }
                }
            )
        }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun AdBannerComponent() {
    val context = LocalContext.current
    val activity = context.findActivity()

    if (activity != null) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            factory = { ctx ->
                val layout = FrameLayout(ctx)
                try {
                    val bannerView = BannerView(activity, UnityAdsHelper.PLACEMENT_ID_BANNER, UnityBannerSize(320, 50))
                    bannerView.listener = object : BannerView.IListener {
                        override fun onBannerLoaded(bannerAdView: BannerView?) {
                            Log.d("UnityAdsHelper", "Banner loaded successfully")
                        }
                        override fun onBannerClick(bannerAdView: BannerView?) {
                            Log.d("UnityAdsHelper", "Banner clicked")
                        }
                        override fun onBannerFailedToLoad(bannerAdView: BannerView?, errorInfo: BannerErrorInfo?) {
                            Log.e("UnityAdsHelper", "Banner failed to load: ${errorInfo?.errorMessage}")
                        }
                        override fun onBannerLeftApplication(bannerAdView: BannerView?) {}
                        
                        override fun onBannerShown(bannerAdView: BannerView?) {
                            Log.d("UnityAdsHelper", "Banner shown")
                        }
                    }
                    layout.addView(bannerView)
                    bannerView.load()
                } catch (e: Exception) {
                    Log.e("UnityAdsHelper", "Error creating BannerView", e)
                }
                layout
            }
        )
    } else {
        // Fallback placeholder if activity is null
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Ad Banner PlaceHolder", color = Color.White)
        }
    }
}

