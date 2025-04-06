-keep class io.github.jakubherr.gitfit.domain.model.** { *; }

-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

# https://engineering.teknasyon.com/code-optimization-with-proguard-and-r8-in-android-4d92e15a398b

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Jetpack Compose
-keepclasseswithmembers class androidx.compose.** { *; }

# Keep composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep basic Compose functionality
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep all classes annotated with @Serializable and their members
-keep @kotlinx.serialization.Serializable class * {
    *;
}