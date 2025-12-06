# MindFlow - Mood Tracking App 🧠✨

A beautiful, native Android mood tracking application built with **Kotlin** and **Jetpack Compose**.

## 🎯 Features

- **Daily Mood Journaling** - Write your thoughts and feelings
- **AI-Powered Analysis** - Get instant mood analysis with sentiment detection
- **Mood Calendar** - Visualize your mood patterns over time
- **Weekly Insights** - Discover trends, word clouds, and personalized recommendations
- **Local Storage** - All data stored securely on device using Room Database

## 🏗️ Architecture

This app follows **Clean Architecture** principles with **MVVM** pattern:

```
app/
├── data/
│   ├── local/
│   │   ├── entity/      # Room entities
│   │   ├── dao/         # Data Access Objects
│   │   ├── converter/   # Type converters
│   │   └── database/    # Room Database
│   └── repository/      # Repository pattern
├── domain/
│   └── service/         # Business logic (Mood Analyzer)
└── ui/
    ├── screens/         # Composable screens
    ├── viewmodel/       # ViewModels with StateFlow
    └── theme/           # Colors, Typography, Theme
```

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **Room Database** | Local persistence |
| **Coroutines & Flow** | Async operations |
| **Navigation Compose** | Screen navigation |
| **Material 3** | Design system |

## 📱 Screens

1. **Home Screen** - Today's mood, quick stats, navigation
2. **Daily Entry Screen** - Journal input with mood analysis
3. **Calendar Screen** - Monthly mood visualization
4. **Insights Screen** - Weekly trends and recommendations

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Build & Run

1. Clone the repository
2. Open `kotlin-android` folder in Android Studio
3. Wait for Gradle sync to complete
4. Run on emulator or physical device (API 26+)

```bash
./gradlew assembleDebug
```

## 📊 Data Flow

```
User Input → Screen → ViewModel → Repository → Room DAO → SQLite
                ↑                                          ↓
                └──────── StateFlow (reactive) ←───────────┘
```

## 🎨 Design

- Purple gradient theme (#7C4DFF primary)
- Glassmorphism-inspired cards
- Emoji-based mood indicators
- Smooth animations

## 📄 License

MIT License - feel free to use this code for your projects!

## 👨‍💻 Author

Built with ❤️ for the Kotlin Contest
