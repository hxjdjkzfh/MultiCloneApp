# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK build/tools/proguard/proguard-android.txt file.

# Keep the application class and its methods
-keep class com.multiclone.app.MultiCloneApplication { *; }

# Keep ViewModel classes and their methods
-keep class com.multiclone.app.ui.viewmodels.** { *; }

# Keep data models
-keep class com.multiclone.app.data.model.** { *; }

# Keep repositories
-keep class com.multiclone.app.data.repository.** { *; }

# Keep virtualization core components
-keep class com.multiclone.app.core.virtualization.** { *; }

# Keep Hilt-related classes
-keep class com.multiclone.app.di.** { *; }
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class com.google.auto.factory.** { *; }

# Compose-related rules
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Navigation Component
-keepnames class androidx.navigation.** { *; }

# Security related
-keep class androidx.security.crypto.** { *; }

# Common Android View Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View

# JSON Parsing
-keepclassmembers class org.json.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Parcelable classes (models that can be parceled between components)
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Debugging options
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile