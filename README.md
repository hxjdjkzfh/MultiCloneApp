# MultiClone App

![MultiClone Logo](art/logo.png)

## Overview

MultiClone is an advanced Android application that enables users to create and manage multiple instances of installed apps without requiring root access. This allows users to maintain separate profiles, data, and settings for the same application.

## Features

- **Multi-App Cloning**: Create multiple instances of your favorite apps
- **Custom Naming & Icons**: Personalize each clone with custom names and icons
- **Isolation Levels**: Choose between different isolation levels (Basic, Standard, Maximum)
- **No Root Required**: Works on unrooted devices with Android 8.0 (API 26) and above
- **Material Design 3**: Modern, clean UI with Material Design 3 components and animations
- **Dark Mode Support**: Full support for light/dark mode
- **Privacy-Focused**: All operations happen locally on your device

## Technical Details

- **Clean Architecture**: Built with MVVM pattern and clean architecture principles
- **Jetpack Compose**: 100% Kotlin with Jetpack Compose UI
- **App Virtualization**: Advanced app virtualization technology for app isolation
- **Hilt Dependency Injection**: For modular and testable code
- **Coroutines & Flow**: For reactive programming and async operations

## Requirements

- Android 8.0 (API 26) or higher (optimized for Android 14)
- 2GB RAM minimum
- 50MB free storage space plus space for cloned apps

## Installation

Download the latest APK from the [Releases](https://github.com/USERNAME/multiclone/releases) section.

## Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/USERNAME/multiclone.git
   ```

2. Open the project in Android Studio.

3. Build the project:
   ```
   ./gradlew assembleDebug
   ```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Privacy Policy

MultiClone does not collect or transmit any personal data. All app data is stored locally on your device.

## Disclaimer

This app is not intended to circumvent any security measures or violate terms of service of any applications. Use responsibly and at your own risk.