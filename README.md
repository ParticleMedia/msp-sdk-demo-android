# MSP SDK (Android) integration guide
## Privisioning
A *Prebid API Key* needs to be provided offline by Particles. Please search `"af7ce3f9-462d-4df1-815f-09314bb87ca3"` in the demo app and replace it with your own. 

Publisher App developers need to pass an *placement id* provisioned by Particles to `LoadAd` API to load an Ad. Please search `"demo-android-article-top"` in the demo app and replace it with your own.

## Dependencies
For now MSP SDK is distributed as aar files and we are working on publish it to public remote respositories like maven central. You can download all the aar files from dir [*app/libs*](https://github.com/ParticleMedia/msp-sdk-demo/tree/main/app/libs).

Since SDK libraries are distribured as aar files, you also need to explictly specify their transient dependencies as below in your app gradle file: 
```
    // facebook-adapter dependencies
    implementation ("com.facebook.android:audience-network-sdk:6.16.0")

    // google-adapter dependencies
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    // nova-adapter dependencies
    implementation ("androidx.appcompat:appcompat:1.4.2")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.browser:browser:1.4.0")
    implementation ("androidx.media3:media3-exoplayer:1.1.1")
    implementation ("androidx.media3:media3-ui:1.1.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.13.2")

    // prebid-adapter dependencies
    implementation ("com.google.code.gson:gson:2.8.9")

    // mes-android-sdk dependencies
    implementation ("com.google.protobuf:protobuf-javalite:3.19.0")
```

## API usage 
1. Init SDK using `MSP.init`
2. Load an Ad using `AdLoader`
3. Got notified via `AdListener.onAdLoaded(placementId: String)` when Ad finished loading.
4. Fetch the loaded Ad from cache using `AdCache.getAd` API
   
Please checkout the demo app for [sample code](https://github.com/ParticleMedia/msp-sdk-demo/blob/main/app/src/main/java/com/particlemedia/ad/MainActivity.kt)

## Proguard rules
Add below rules to your app's proguard-rules.pro file: 
```
## Google Ads
-keep class com.google.android.gms.ads.** { *; }

## MSP SDK
-keep class com.particles.** { *; }
```
## Privacy & CCPA
Please follow Prebid's documentation to set user's IAB US Privacy signal: https://docs.prebid.org/prebid-mobile/prebid-mobile-privacy-regulation.html#notice-and-opt-out-signal 

## Verify your integration
If everything goes well, you should be able to get below sample Ad from Prebid.

<img width="300" src="https://github.com/user-attachments/assets/9135888c-f4d9-468c-b026-e989d1acbbad">
