# MultiCloneApp - Руководство по настройке и сборке

## Обзор проекта

MultiCloneApp - Android-приложение, позволяющее клонировать другие приложения в изолированных виртуальных средах без необходимости root-доступа. Приложение использует:

- Kotlin для разработки Android
- Архитектуру MVVM с чистой архитектурой
- Jetpack Compose для UI
- Material 3 для дизайна
- GitHub Actions для CI/CD
- Продвинутую технологию виртуализации приложений

## 1. Настройка локальной сборки

### Требования для сборки

- JDK 11 (приложение настроено на работу с Java 11)
- Android SDK с API level 29
- Gradle 7.0.2

### Переменные среды для сборки

Добавьте следующие переменные среды:

```bash
export ANDROID_HOME=/path/to/your/android/sdk
export ANDROID_SDK_ROOT=/path/to/your/android/sdk
```

### Сборка проекта локально

```bash
# Очистка проекта
./gradlew clean

# Сборка отладочной версии
./gradlew assembleDebug

# Установка на подключенное устройство
./gradlew installDebug
```

## 2. Настройка GitHub репозитория

### Инициализация Git и первый коммит

```bash
git init
git config --global user.email "your.email@example.com"
git config --global user.name "Your Name"
git add .
git commit -m "Initial commit for MultiCloneApp"
```

### Настройка удаленного репозитория

```bash
git remote add origin https://github.com/hxjdjkzfh/MultiCloneApp.git
git push -u origin main
```

Или с использованием токена:

```bash
git remote add origin https://{YOUR_GITHUB_TOKEN}@github.com/hxjdjkzfh/MultiCloneApp.git
git push -u origin main
```

## 3. Настройка GitHub Actions

GitHub Actions уже настроены в файле `.github/workflows/android.yml`.

### Настройка секретов для GitHub Actions

Перейдите в настройки репозитория (Settings > Secrets and variables > Actions) и добавьте следующие секреты:

- `SIGNING_KEY` (base64-кодированный файл keystore)
- `SIGNING_STORE_PASSWORD` (пароль хранилища ключей)
- `SIGNING_KEY_ALIAS` (псевдоним ключа)
- `SIGNING_KEY_PASSWORD` (пароль ключа)

### Запуск workflow вручную

Вы можете запустить сборку вручную:
1. Перейдите во вкладку Actions
2. Выберите "Android Build"
3. Нажмите "Run workflow"

## 4. Последние изменения в проекте

1. **Совместимость с Java 11**:
   - Обновлены зависимости для работы с Java 11
   - Понижены версии библиотек Jetpack для совместимости

2. **Понижение целевого SDK**:
   - Изменен compileSdk и targetSdk с 30 на 29 для лучшей совместимости

3. **Исправление GitHub Actions workflow**:
   - Заменены все ссылки на `upload-artifact@v3` на совместимую версию `upload-artifact@v2`

4. **Улучшение сборочной конфигурации**:
   - Добавлены специфичные настройки в local.properties
   - Оптимизированы Gradle настройки

## 5. Известные проблемы и решения

### Ошибка "Failed to find target with hash string 'android-29'"

Причина: Android SDK не может найти правильную версию платформы.

Решение:
1. Убедитесь, что у вас установлен Android SDK Platform 29
2. Проверьте путь к SDK в local.properties
3. Попробуйте следующие команды:

```bash
# Установка Platform 29
sdkmanager "platforms;android-29"

# Установка Build Tools
sdkmanager "build-tools;30.0.2"
```

### Ошибка "Execution failed for task ':app:processDebugResources'"

Решение:
- Выполните `./gradlew clean` перед сборкой
- Удостоверьтесь, что все ресурсы XML валидны

## 6. Полезные ссылки

- [Официальная документация Android](https://developer.android.com/)
- [Документация по GitHub Actions](https://docs.github.com/en/actions)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Gradle для Android](https://developer.android.com/studio/build)