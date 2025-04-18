# MultiClone App

An Android application that allows users to create isolated clones of installed apps without root access.

## Features

- Clone any installed app on your device
- Customize clone name and icon
- Run multiple instances of the same app
- Create shortcuts for cloned apps
- Clean, modern UI built with Jetpack Compose

## Architecture

The app is built using Clean Architecture principles with MVVM pattern:

- **UI Layer**: Jetpack Compose UI components, ViewModels
- **Domain Layer**: Use cases, business logic
- **Data Layer**: Repositories, data sources

## Technologies Used

- Kotlin
- Jetpack Compose
- Hilt for dependency injection
- Kotlin Coroutines & Flow
- Material 3 Design
- Android Virtualization for app cloning

## Requirements

- Android 14+
- Gradle 8.0+
- Android Studio Arctic Fox or newer

## Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle and build the project
4. Run on a device or emulator running Android 14+

## License

This project is available under the MIT license.