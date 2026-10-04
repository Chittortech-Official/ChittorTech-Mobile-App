# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*
-keepclassmembers class kotlinx.serialization.json.** { *** descriptor; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# Keep ChittorTech models for Firebase/Serialization
-keep class com.chittortech.app.model.** { *; }
