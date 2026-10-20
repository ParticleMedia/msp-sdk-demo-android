# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

## Google Ads
-keep class com.google.android.gms.ads.** { *; }

## ---------------Begin: MSP SDK (narrowed from -keep class com.particles.** { *; }) ----------
# Every rule below covers a place where MSP reaches its own classes by reflection or by name,
# which R8 cannot see. Verified identical across MSP 3.7.0 – 4.15.2.

# 1. Adapters are created by AdNetworkAdapterProviderImp via Class.forName(name).newInstance().
#    Without this, the adapter is stripped/renamed, createInstance() returns null and that
#    network silently never bids.
-keep class * extends com.particles.msp.adapter.AdNetworkAdapter {
    <init>();
}

# 2. PrebidBidder is created by BidderProviderImp via Class.forName + kotlin-reflect
#    KClass.constructors, which reads the class's Kotlin @Metadata (kotlin.Metadata itself is
#    kept by kotlin-reflect's bundled rules, which prebid-adapter brings in).
-keep class com.particles.prebidadapter.PrebidBidder { *; }

# 3. Ad config and user id are fetched with Retrofit + Gson. The request/response models are
#    plain data classes without @SerializedName, so Gson maps JSON keys to field names:
#    renaming a field breaks ad config parsing and no ads load.
-keep interface com.particles.msp.auction.AdConfigApi { *; }
-keep interface com.particles.msp.util.UserIdApi { *; }
-keep class com.particles.msp.auction.AdConfigRequest { *; }
-keep class com.particles.msp.auction.AdConfigResponse { *; }
-keep class com.particles.msp.auction.AdConfigSettings { *; }
-keep class com.particles.msp.auction.LogConfig { *; }
-keep class com.particles.msp.auction.EventSamplingRate { *; }
-keep class com.particles.msp.util.UserIdRequest { *; }
-keep class com.particles.msp.util.UserIdResponse { *; }

# 4. Ad config JSON (AdConfig / Placement / BidderInfo) is decoded with kotlinx.serialization.
#    The generated serializers use string keys; keep them and their entry points.
-keep @kotlinx.serialization.Serializable class com.particles.** { *; }
-keep class com.particles.**$$serializer { *; }
-keepclassmembers class com.particles.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# 5. MES events are protobuf-lite messages; protobuf-lite reads message fields by name.
-keep class com.particles.mes.protos.** { *; }

# 6. WebView JS bridges (nova-sdk HTML / MRAID / form ads).
-keepclassmembers class com.particles.** {
    @android.webkit.JavascriptInterface <methods>;
}

# 7. Serializable objects passed through Intent extras (e.g. nova FormParams) — keep the
#    serialized shape stable so a restored Activity can still deserialize them.
-keepclassmembers class com.particles.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
## ---------------End: MSP SDK ----------
