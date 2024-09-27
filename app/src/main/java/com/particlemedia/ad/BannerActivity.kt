package com.particlemedia.ad

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.particles.msp.AdCache
import com.particles.msp.api.AdFormat
import com.particles.msp.api.AdListener
import com.particles.msp.api.AdLoader
import com.particles.msp.api.AdRequest
import com.particles.msp.api.AdSize
import com.particles.msp.api.BannerAdView
import com.particles.msp.api.MSPAd
import com.particles.msp.api.MSPInitListener
import com.particles.msp.api.MSPInitStatus
import com.particles.msp.api.MSPInitializationParameters
import com.particles.msp.api.NativeAd
import com.particles.msp.api.NativeAdView
import com.particles.msp.api.NativeAdViewBinder
import com.particles.msp.util.Logger
import com.particles.prebidadapter.MSP
import kotlin.system.measureTimeMillis

class BannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        // 1. init MSP SDK
        val initParams = object : MSPInitializationParameters  {
            override fun getConsentString(): String {
                // currently returned value is not used
                return ""
            }

            override fun getParameters(): Map<String, Any> {
                // currently returned value is not used
                return mapOf()
            }

            override fun hasUserConsent(): Boolean {
                // currently returned value is not used
                return true
            }

            override fun isAgeRestrictedUser(): Boolean {
                // currently returned value is not used
                return false
            }

            override fun isDoNotSell(): Boolean {
                // currently returned value is not used
                return false
            }

            override fun isInTestMode(): Boolean {
                // currently returned value is not used
                return true
            }

            override fun getPrebidAPIKey(): String {
                return "af7ce3f9-462d-4df1-815f-09314bb87ca3" // Please replace with your own Prebid API Key
            }

            override fun getPrebidHostUrl(): String {
                return "https://prebid-server.themsp.ai/openrtb2/auction"
            }
        }
        val initListener = object: MSPInitListener {
            override fun onComplete(status: MSPInitStatus, message: String) {
                Logger.info("MSP SDK is initialized: $status")
            }
        }

        val timeTakenInit = measureTimeMillis { MSP.init(applicationContext, initParams, initListener, false) }
        Logger.setLogLevel(Logger.DEBUG) // for debugging only. Please do NOT set for production builds.
        Logger.info("MSP.init() DURATION: $timeTakenInit ms")

        // 2. listen and handle loaded Ad
        val adLoadListener = object : AdListener {
            override fun onAdClicked(ad: MSPAd) {
                Logger.info("Ad clicked. info: ${ad.adInfo}")
            }

            override fun onAdLoaded(ad: MSPAd) {
                // This API is DEPRECATED
            }

            override fun onAdLoaded(placementId: String) {
                Logger.info("Ad load event received. placementId: $placementId. Fetching ads from cache...")
                val ad:MSPAd? = AdCache.getAd(placementId)
                if (ad == null) {
                    Logger.info("Got null ad from cache. placementId: $placementId")
                    return
                }

                Logger.info("Got ad from cache. placementId: $placementId. adInfo: ${ad.adInfo}")

                val adViewContainer = findViewById<FrameLayout>(R.id.ad_container)
                val destroyButton = findViewById<Button>(R.id.destroy_button)
                var adView: View? = null
                if (ad is BannerAdView) {
                    adView = (ad.adView) as View
                    Logger.info("Banner Ad loaded.")
                } else if (ad is NativeAd) {
                    Logger.info("Native Ad loaded. Title: ${ad.title}")
                    val nativeAdViewBinder: NativeAdViewBinder = NativeAdViewBinder.Builder()
                        .layoutResourceId(R.layout.native_ad)
                        .titleTextViewId(R.id.ad_title_text_view)
                        .advertiserTextViewId(R.id.advertiser_text_view)
                        .bodyTextViewId(R.id.ad_body_text_view)
                        .callToActionViewId(R.id.cta_button)
                        .mediaViewId(R.id.ad_media_view_container)
                        .optionsViewId(R.id.options_view)
                        .build()
                    adView = NativeAdView(ad, nativeAdViewBinder, applicationContext)
                }
                adView?.let {
                    adViewContainer.addView(it)
                    destroyButton.setOnClickListener { ad.destroy() }
                }
            }

            override fun onError(msg: String) {
                Logger.info("MainActivity. Ads loading error: $msg")
            }
        }

        // 3. Load an Ad
        val placementId = "demo-android-article-top"  // Please replace with your own placement ID
        val adRequest = AdRequest.Builder(AdFormat.BANNER)
            .setContext(applicationContext)
            .setPlacement(placementId)
            .setAdSize(AdSize(320, 50, false, false))
            .setCustomParams(mapOf("user_id" to "177905312"))
            .setAdaptiveBannerSize(AdSize(384, 0, false, true))
            .setIsCacheSupported(true)
            .setTestParams(getTestParams()) // for testing ONLY. Please do NOT set for production builds. Otherwise no impression will be counted.
            .build()

        val start = System.currentTimeMillis()
        Logger.info("AdLoader.loadAd() start")
        AdLoader().loadAd(placementId, adLoadListener, this, adRequest)
        Logger.info("AdLoader.loadAd() end. DURATION: ${System.currentTimeMillis() - start} ms")
    }


    fun getTestParams(): Map<String, Any> {
        val testParams: MutableMap<String, Any> = HashMap()
        testParams["test_ad"] = true
        //testParams["ad_network"] = "msp_google"
        //testParams["ad_network"] = "msp_fb"
        //testParams["ad_network"] = "msp_nova"
        testParams["ad_network"] = "Prebid"
        return testParams
    }
}