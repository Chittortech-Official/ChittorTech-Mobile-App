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

# OWASP MASVS-CODE-2: Strip debug and verbose logging in release builds to eliminate Logcat data leakage
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve LineNumberTable and SourceFile attributes for sanitized crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

