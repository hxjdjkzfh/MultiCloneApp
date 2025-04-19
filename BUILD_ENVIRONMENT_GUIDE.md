# MultiClone App Build Environment Guide

This guide provides detailed instructions for setting up a proper build environment for the MultiClone App project. Due to the complexity of the virtualization requirements and build environment constraints, following these steps exactly will help avoid common issues.

## Environment Requirements

### Java Setup

The project currently requires **Java 8 compatibility**. Despite modern Android development often using newer Java versions, we've found that our virtualization components work more reliably with Java 8 compatibility mode.

To configure Java 8 compatibility in your environment:

```gradle
// In your app-level build.gradle.kts
android {
    // Other config...
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```

### Gradle Configuration

We've adjusted our Gradle configuration to work with Gradle 7.5, which provides better compatibility with our environment requirements. The project may not build correctly with newer Gradle versions without modifications.

If you encounter Gradle-related issues:

1. Make sure you're using the wrapper provided in the repository:
   ```bash
   chmod +x gradlew
   ./gradlew --version  # Should show Gradle 7.5
   ```

2. If you need to downgrade your Gradle version:
   ```bash
   # In gradle/wrapper/gradle-wrapper.properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-bin.zip
   ```

### Android SDK Configuration

The project currently targets:
- `compileSdk`: 30
- `minSdk`: 21
- `targetSdk`: 30

Although we initially targeted Android API 34, we've found better compatibility with API 30 for our virtualization components. Make sure your Android SDK includes:

- Android SDK Platform 30
- Android SDK Build-Tools 30.0.3
- Android SDK Command-line Tools

You can check your installed SDK platforms and build tools with:
```bash
sdkmanager --list
```

## Common Build Issues and Solutions

### Issue: Failed to find target with hash string 'android-30'

This means your SDK is missing the Android 30 platform or it's not properly recognized.

**Solution**:
```bash
# Install Android SDK Platform 30
sdkmanager "platforms;android-30"

# Verify installation
sdkmanager --list | grep "platforms;android-30"
```

### Issue: Execution failed for task ':app:compileDebugKotlin'

This typically happens when there are Kotlin compatibility issues with the JDK version.

**Solution**:
1. Make sure your Kotlin version is compatible with JDK 8:
   ```gradle
   // In project-level build.gradle.kts
   plugins {
       kotlin("android") version "1.7.20" // Use a version compatible with Java 8
   }
   ```

2. Check that your kotlinOptions match your compileOptions:
   ```gradle
   // In app-level build.gradle.kts
   kotlinOptions {
       jvmTarget = "1.8"
   }
   ```

### Issue: Duplicate class errors

This can happen when there are conflicts between library dependencies.

**Solution**:
1. Examine the error messages to identify conflicting libraries
2. Add exclusions for the problematic dependencies:
   ```gradle
   implementation("com.example:library:1.0.0") {
       exclude(group = "org.conflicting", module = "module")
   }
   ```
3. Try running with `./gradlew app:dependencies` to view the dependency tree

## GitHub CI Environment

Our GitHub Actions workflow is configured to handle these requirements automatically. The workflow:

1. Sets up JDK 17 (with Java 8 compatibility in our Gradle config)
2. Sets up the Android SDK with the correct platform and build tools
3. Creates the keystore directory for release signing
4. Builds both debug and release versions of the app

If you're using your own CI environment, make sure it mirrors these configurations.

## Building Locally

For the best experience building locally:

1. Use Android Studio Arctic Fox or newer
2. Configure Gradle JDK to use Java 8 compatibility
3. Ensure Android SDK Platform 30 and Build-Tools 30.0.3 are installed
4. Use the project's Gradle wrapper for building
5. If you encounter issues, try a clean build:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

## Troubleshooting

If you still encounter build issues:

1. Check your `local.properties` has the correct SDK path:
   ```
   sdk.dir=/path/to/your/android/sdk
   ```

2. Verify the Android SDK components with:
   ```bash
   sdkmanager --list | grep "platforms;android-30"
   sdkmanager --list | grep "build-tools;30.0.3"
   ```

3. Try invalidating Android Studio caches (File > Invalidate Caches / Restart)

4. If all else fails, try working with GitHub Actions by pushing your code to a repository. Our workflows are already configured for the correct build environment.