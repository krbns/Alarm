# Alarm Clock App

A modern Android alarm clock application built with native Kotlin and Jetpack Compose.

## Features

- Create, edit and delete alarms
- Set repeat days (weekdays)
- Toggle alarms on/off
- Alarm ring screen with dismiss and snooze (+5 minutes) buttons
- Vibration support
- Alarms persist after device reboot

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture (MVVM)
- **DI**: Hilt
- **Database**: Room
- **Scheduling**: AlarmManager

## Project Structure

```
app/src/main/java/com/kurban/alarm/
├── domain/          # Business logic layer
│   ├── model/       # Domain models
│   ├── repository/  # Repository interfaces
│   └── usecase/    # Use cases
├── data/            # Data layer
│   ├── local/       # Room database
│   └── repository/ # Repository implementations
├── di/              # Hilt dependency injection modules
├── notification/    # Alarm scheduling and receivers
└── presentation/   # UI layer
    ├── alarm/       # Ring screen
    ├── alarmEdit/  # Create/edit alarm screen
    ├── alarmList/  # Alarm list screen
    ├── navigation/ # Navigation setup
    └── theme/      # Theme and spacing
```

## Requirements

- Android SDK 26+ (Android 8.0 Oreo)
- Java 17+
- Gradle 8.5+
- Android Studio

## Build

```bash
./gradlew assembleDebug
```

Debug APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`
