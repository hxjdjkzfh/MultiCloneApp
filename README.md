# MultiClone App

An advanced Android application enabling seamless multi-profile app management with enhanced virtualization and isolation capabilities. Create multiple instances of your favorite apps without requiring root access!

## Features

- **App Cloning**: Create multiple instances of any installed app
- **Profile Isolation**: Complete data separation between cloned instances
- **Customization**: Custom names and icons for each cloned app
- **No Root Required**: Works without requiring root access
- **Modern UI**: Material 3 design with smooth animations
- **Background Services**: Manages app virtualization efficiently

## Technical Overview

MultiClone App is built using:

- **Kotlin** for Android development
- **MVVM Clean Architecture** for maintainable, testable code
- **Jetpack Compose** for modern, declarative UI
- **Material 3** design system for beautiful visuals
- **Coroutines & Flow** for asynchronous operations
- **GitHub Actions** for CI/CD

## Architecture

The app follows Clean Architecture principles with distinct layers:

### UI Layer
- **Screens**: HomeScreen, AppSelectionScreen, SettingsScreen, AboutScreen
- **Components**: AppItem, CloneItem, LoadingOverlay, ActionButton
- **ViewModels**: HomeViewModel, AppSelectionViewModel, SettingsViewModel

### Domain Layer
- **Use Cases**: GetInstalledAppsUseCase, CreateCloneUseCase, GetClonesUseCase, DeleteCloneUseCase, LaunchCloneUseCase
- **Services**: VirtualAppService

### Data Layer
- **Repositories**: AppRepository, CloneRepository
- **Models**: AppInfo, CloneInfo with serialization support
- **Storage**: Secure local storage for clone configurations

### Core/Virtualization
- **VirtualAppEngine**: Main virtualization orchestrator
- **CloneEnvironment**: Manages isolated app environments
- **ClonedAppInstaller**: Handles installation across environments
- **CloneProxyActivity**: Intercepts app launches to route correctly
- **VirtualizationService**: Background service for environment management
- **CloneManagerService**: Handles clone lifecycle events

## Implementation Progress (April 2025)

- ✅ Completed all virtualization components in core package
- ✅ Implemented Material Design 3 UI with animations
- ✅ Created data models with serialization support
- ✅ Built secure storage for clone configurations
- ✅ Added GitHub workflow for CI/CD
- ✅ Fixed Java compatibility (downgraded to Java 8)
- ✅ Adjusted Gradle configuration for build environment compatibility

## Setting Up Development Environment

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 8 compatibility
- Android SDK 30

### Build Configuration
The project currently targets:
- `compileSdk`: 30
- `minSdk`: 21
- `targetSdk`: 30

We've needed to adjust from higher SDK versions due to build environment compatibility.

## GitHub Setup

### Pushing to GitHub
1. Make sure you have a GitHub account and a personal access token with repo permissions
2. Use our included script to push your code:

```bash
# Make script executable
chmod +x commit-and-push.sh

# Run with your information
./commit-and-push.sh YOUR_GITHUB_TOKEN YOUR_USERNAME YOUR_REPO_NAME
```

Alternatively, you can run the script without arguments and it will prompt you for the required information.

## Building and Running

```bash
# Clean build
./gradlew clean build

# Build debug version
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug
```

## CI/CD Pipeline

This project uses GitHub Actions for:

- **Continuous Integration**: Building and testing on each commit
- **APK Distribution**: Building both debug and release APKs
- **Telegram Deployment**: Automatically sending builds to a Telegram chat (see setup-telegram-bot.md)

## Contributions

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.