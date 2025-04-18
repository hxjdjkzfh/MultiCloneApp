# MultiClone App

An advanced Android application enabling seamless multi-profile app management with enhanced virtualization and isolation capabilities.

## Features

- **App Cloning**: Create multiple instances of any installed app
- **Profile Isolation**: Complete data separation between cloned instances
- **Customization**: Custom names and icons for each cloned app
- **No Root Required**: Works without requiring root access
- **Modern UI**: Material 3 design with smooth animations

## Technical Overview

MultiClone App is built using:

- **Kotlin** for Android development
- **MVVM Clean Architecture** for maintainable, testable code
- **Jetpack Compose** for modern, declarative UI
- **Material 3** design system for beautiful visuals
- **Hilt** for dependency injection
- **GitHub Actions** for CI/CD

## Architecture

The app follows Clean Architecture principles with distinct layers:

### UI Layer
- **Screens**: HomeScreen, AppSelectionScreen, CloneConfigScreen, ClonesListScreen
- **Components**: AppItem, CloneItem, LoadingOverlay
- **ViewModels**: AppSelectionViewModel, CloneConfigViewModel, ClonesListViewModel

### Domain Layer
- **Use Cases**: GetInstalledAppsUseCase, CreateCloneUseCase, GetClonesUseCase, DeleteCloneUseCase, LaunchCloneUseCase
- **Services**: VirtualAppService

### Data Layer
- **Repositories**: AppRepository, CloneRepository
- **Models**: AppInfo, CloneInfo

### Core
- **Virtualization**: VirtualAppEngine, CloneProxyActivity, CloneManagerService

## Virtualization Technology

MultiClone uses advanced app virtualization to:

1. Create isolated environments for each cloned app
2. Redirect file system access to the isolated environment
3. Manage environment lifecycle through a background service
4. Proxy app launches to load the proper environment

## Building from Source

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11+
- Android SDK 34+

### Steps
1. Clone the repository
```bash
git clone https://github.com/hxjdjkzfh/MiltiAppCloner.git
```

2. Open the project in Android Studio

3. Build the app
```bash
./gradlew assembleDebug
```

The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`

## CI/CD

This project uses GitHub Actions for:

- **Continuous Integration**: Building and testing on each commit
- **APK Distribution**: Building both debug and release APKs
- **Telegram Deployment**: Automatically sending builds to a Telegram chat

## License

This project is licensed under the MIT License - see the LICENSE file for details.