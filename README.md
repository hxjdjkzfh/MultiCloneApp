# MultiCloneApp

An Android application that allows users to create isolated clones of installed apps without root access.

## Features

* Create multiple instances of the same app with isolated data
* Customize clone names and icons
* Generate shortcuts for quick access to cloned apps
* Easily manage all your cloned applications
* No root access required

## Architecture

This app is built using modern Android development practices:

* **MVVM Architecture**: Clear separation of UI, business logic, and data
* **Jetpack Compose**: Modern declarative UI toolkit
* **Hilt**: Dependency injection
* **Kotlin Coroutines & Flow**: Asynchronous programming
* **Clean Architecture**: Domain, data, and presentation layers

## Technical Details

The app uses Android's built-in virtualization capabilities to create isolated environments for each cloned application. This is achieved by:

1. Creating a virtual environment for each clone
2. Installing APKs within isolated user profiles
3. Managing data isolation between clones
4. Proxying intents to the appropriate cloned application

## Build and Install

### Prerequisites
- Android Studio Arctic Fox or newer
- Kotlin 1.9.0+
- Android SDK 34

### Building
1. Clone this repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.