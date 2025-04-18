# MultiClone App

An Android application that allows users to create isolated clones of installed apps without root access.

## Features

- **Create App Clones**: Make multiple instances of any installed app
- **Custom Names and Icons**: Personalize your cloned apps
- **Separate Data**: Each clone runs in its own isolated environment
- **No Root Required**: Works on any Android device without root access
- **Home Screen Shortcuts**: Quick access to your cloned apps

## Technology Stack

- **Kotlin**: Modern Android development with Kotlin
- **Jetpack Compose**: Declarative UI building with animations
- **Material 3 Design**: Modern and consistent user interface
- **MVVM Architecture**: Clean separation of concerns
- **Dagger Hilt**: Dependency injection
- **App Sandboxing**: Isolation of app data and settings

## Requirements

- Android 8.0 (API level 26) or higher
- Internet permission for app updates

## Building from Source

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/multiclone-app.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```

4. Install on your device:
   ```bash
   ./gradlew installDebug
   ```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.