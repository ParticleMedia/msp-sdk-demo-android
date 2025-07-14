package com.particlemedia.ad

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.particles.msp.api.AdFormat
import com.particles.msp.api.AdListener
import com.particles.msp.api.AdLoader
import com.particles.msp.api.AdRequest
import com.particles.msp.api.AdSize
import com.particles.msp.api.BannerAdView
import com.particles.msp.api.InterstitialAd
import com.particles.msp.api.MSPAd
import com.particles.msp.api.MSPConstants
import com.particles.msp.api.MSPInitListener
import com.particles.msp.api.MSPInitStatus
import com.particles.msp.api.MSPInitializationParameters
import com.particles.msp.api.NativeAd
import com.particles.msp.api.NativeAdView
import com.particles.msp.api.NativeAdViewBinder
import com.particles.msp.util.Logger
import com.particles.prebidadapter.MSP
import kotlin.system.measureTimeMillis

class InterstitialFormatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        Logger.setLogLevel(Logger.DEBUG) // for debugging only. Please do NOT set for production builds.
        // 1. init MSP SDK
        val initParams = object : MSPInitializationParameters  {
            override fun getAppId(): Int {
                return 1
            }

            override fun getConsentString(): String {
                // currently returned value is not used
                return ""
            }

            override fun getOrgId(): Int {
                return 1061
            }

            override fun getParameters(): Map<String, Any> {
                // currently returned value is not used
                return mapOf(MSPConstants.INIT_PARAM_KEY_PPID to "shun-test-ppid", MSPConstants.INIT_PARAM_KEY_EMAIL to "shun.j@shun.com")
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
                return "https://msp.newsbreak.com/openrtb2/auction"
            }
        }
        val initListener = object: MSPInitListener {
            override fun onComplete(status: MSPInitStatus, message: String) {
                Logger.info("MSP SDK is initialized: $status")
            }
        }

        val timeTakenInit = measureTimeMillis { MSP.init(applicationContext, initParams, initListener, false) }
        Logger.info("MSP.init() DURATION: $timeTakenInit ms")

        // 2. listen and handle loaded Ad
        val adLoadListener = object : AdListener {
            override fun onAdClicked(ad: MSPAd) {
                Logger.info("Ad clicked. info: ${ad.adInfo}")
                ad.sendHideAdEvent("test ad hide reason")
                ad.sendReportAdEvent("test ad report reason", "test ad report description")
            }

            override fun onAdDismissed(ad: MSPAd) {
                Logger.info("Ad dismissed. info: ${ad.adInfo}")
            }

            override fun onAdImpression(ad: MSPAd) {
                Logger.info("Ad is displayed. info: ${ad.adInfo}")
            }

            override fun onAdLoaded(placementId: String) {
                Logger.info("Ad load event received. placementId: $placementId. Fetching ads from cache...")
                val ad:MSPAd? = AdLoader().getAd(placementId)
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
                } else if (ad is InterstitialAd) {
                    Logger.info("Interstitial Ad loaded.")
                    ad.show(this@InterstitialFormatActivity)
                }
                adView?.let {
                    adViewContainer.addView(it)
                }
                destroyButton.setOnClickListener { ad.destroy() }
            }

            override fun onError(msg: String) {
                Logger.info("MainActivity. Ads loading error: $msg")
            }
        }

        // 3. Load an Interstitial Ad
        val placementId = "demo-android-interstitial" // Please replace with your own placement ID
        val adRequest = AdRequest.Builder(AdFormat.INTERSTITIAL)
            .setContext(this)
            .setPlacement(placementId)
            .setCustomParams(mapOf("user_id" to "177905312"))
            .setTestParams(getTestParams())
            .build()
        val start = System.currentTimeMillis()
        Logger.info("AdLoader.loadAd() start")
        AdLoader().loadAd(placementId, adLoadListener, adRequest)
        Logger.info("AdLoader.loadAd() end. DURATION: ${System.currentTimeMillis() - start} ms")
    }

    fun getTestParams(): Map<String, Any> {
        val testParams: MutableMap<String, Any> = HashMap()
        testParams["test_ad"] = true
        //testParams["ad_network"] = "msp_google"
        //testParams["ad_network"] = "msp_fb"
        testParams["ad_network"] = "msp_nova"
        testParams["is_vertical"] = true
        testParams["creative_type"] = "video"
        return testParams
    }
}