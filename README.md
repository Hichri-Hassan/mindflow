# MindFlow - AI-Powered Mood Tracking App 🧠✨

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2026+-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern Android mood tracking application built with **Kotlin 1.9.25** and **Jetpack Compose**, featuring intelligent mood analysis powered by **Google Gemini AI** with offline fallback support.

> **Contest Ready**: ✅ Fully functional without additional setup • ✅ Works offline • ✅ Kotlin 1.9.25 • ✅ Runs on Android emulator & physical devices

## 🎯 Key Features

- 📝 **Daily Mood Journaling** - Write your thoughts and track emotions with 6 mood categories
- 🤖 **AI-Powered Analysis** - Intelligent mood detection using Google Gemini AI (with offline fallback)
- 📅 **Interactive Calendar** - Visual mood history with color-coded daily entries
- 📊 **Weekly Insights** - Trend analysis, word clouds, and AI-generated personalized summaries
- 🔥 **Streak Tracking** - Build consistent journaling habits with daily streak counter
- 💾 **Local-First Storage** - All data stored securely on device using Room Database
- 🎨 **Material 3 Design** - Beautiful gradient UI with smooth animations

## 📂 Project Structure

```
kotlin-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/mindflow/app/
│   │   │   ├── config/
│   │   │   │   └── AppConfig.kt              # API configuration
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── MoodEntryEntity.kt    # Room entity
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── MoodEntryDao.kt       # Database queries
│   │   │   │   │   ├── converter/
│   │   │   │   │   │   └── Converters.kt         # Type converters
│   │   │   │   │   └── database/
│   │   │   │   │       └── MindFlowDatabase.kt   # Room database
│   │   │   │   └── repository/
│   │   │   │       └── MoodRepository.kt         # Data layer
│   │   │   ├── domain/
│   │   │   │   └── service/
│   │   │   │       └── MoodAnalyzerService.kt    # AI business logic
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── HomeScreen.kt             # Dashboard
│   │   │       │   ├── DailyEntryScreen.kt       # Journal input
│   │   │       │   ├── MoodCalendarScreen.kt     # Calendar view
│   │   │       │   └── InsightsScreen.kt         # Analytics
│   │   │       ├── viewmodel/
│   │   │       │   ├── HomeViewModel.kt
│   │   │       │   ├── DailyEntryViewModel.kt
│   │   │       │   ├── CalendarViewModel.kt
│   │   │       │   ├── InsightsViewModel.kt
│   │   │       │   └── ViewModelFactory.kt
│   │   │       ├── components/
│   │   │       │   └── GradientButton.kt         # Reusable UI
│   │   │       └── theme/
│   │   │           ├── Color.kt
│   │   │           └── Theme.kt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts                      # App-level Gradle config
├── build.gradle.kts                          # Project-level Gradle config
├── gradle.properties                         # Gradle settings
├── settings.gradle.kts
└── README.md                                 # This file
```

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 1.9.25 | Primary language (Contest requirement ✅) |
| **Jetpack Compose** | Latest | Modern declarative UI framework |
| **Room Database** | 2.6.1 | Local data persistence with SQLite |
| **Coroutines & Flow** | 1.7.3 | Asynchronous programming & reactive streams |
| **Navigation Compose** | 2.7.6 | Type-safe navigation between screens |
| **Material 3** | Latest | Google's latest design system |
| **Google Gemini AI** | 0.1.2 | AI-powered mood analysis (optional) |
| **Gradle** | 8.5 | Build automation |
| **Android Gradle Plugin** | 8.2.0 | Android build configuration |
| **Min SDK** | 26 (Android 8.0) | Minimum supported Android version |
| **Target SDK** | 34 (Android 14) | Target Android version |

## 📱 How to Try Key Features

### 1️⃣ **Home Screen** - Dashboard Overview
- Launch the app to see today's date and motivational message
- View your current streak counter (starts at 0)
- Check total entries and weekly mood average
- Navigate to any screen using the bottom buttons

### 2️⃣ **Daily Entry** - AI Mood Analysis
```
Steps to test:
1. Tap "New Entry" from home screen
2. Select a mood (😊 Happy, 👍 Good, 😐 Neutral, 😢 Sad, 😰 Anxious, 😫 Stressed)
3. Write a journal entry (e.g., "Had a great day at work, feeling productive!")
4. Tap "Analyze & Save"
5. See AI-powered analysis appear below your entry
6. Save the entry and see it added to your history
```

**What you'll see:**
- Real-time mood analysis (using keyword matching or Gemini AI)
- Detected emotions and sentiment
- Personalized insights
- Entry saved to local database

### 3️⃣ **Calendar View** - Historical Overview
```
Steps to test:
1. Add a few entries with different moods
2. Navigate to "Calendar" tab
3. See color-coded mood indicators for each day
4. Swipe between months
5. Tap any day to see entries (if multiple exist)
```

**Visual indicators:**
- 😊 Happy = Green
- 👍 Good = Light Green  
- 😐 Neutral = Gray
- 😢 Sad = Blue
- 😰 Anxious = Yellow
- 😫 Stressed = Red

### 4️⃣ **Insights Dashboard** - Weekly Analysis
```
Steps to test:
1. Add entries across multiple days
2. Navigate to "Insights" tab
3. View weekly mood distribution chart
4. See word cloud of frequently used words
5. Read AI-generated weekly summary
6. Check mood trends and patterns
```

**What you'll see:**
- Pie chart of mood distribution
- Word cloud visualization
- Streak and consistency stats
- Personalized recommendations
- Mood trend analysis (improving/declining)

### 5️⃣ **Streak Tracking** - Build Habits
```
Steps to test:
1. Add an entry today (streak = 1)
2. Close and reopen the app
3. Add entries on consecutive days
4. Watch your streak counter increase
5. Skip a day and see the streak reset
```

### 🎯 Test Scenarios

**Scenario A: Complete User Journey**
```
Day 1: Add happy entry → See streak = 1
Day 2: Add stressed entry → Streak = 2, view calendar
Day 3: Add good entry → Streak = 3, check insights
Day 4: Skip entry → Streak resets to 0
Day 5: Add anxious entry → New streak begins
```

**Scenario B: AI Analysis Testing**
```
Entry 1: "Feeling stressed about deadlines" 
→ Should detect: Stressed, negative sentiment

Entry 2: "Amazing day! So happy and energized!"
→ Should detect: Happy, positive sentiment

Entry 3: "Just an ordinary day, nothing special"
→ Should detect: Neutral, calm sentiment
```

**Scenario C: Data Persistence**
```
1. Add 5 entries with different moods
2. Close the app completely
3. Reopen the app
4. Verify all entries are still there
5. Check calendar shows all mood indicators
6. Confirm insights reflect all data
```

## 🚀 Quick Start Guide

### ✅ No Additional Setup Required!

The app is **fully functional out of the box** and works offline. AI features have an intelligent fallback that uses keyword-based analysis when no API key is configured.

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK** 17 or higher
- **Android SDK** 34
- **Android Emulator** or physical device (API 26+)

### Installation & Running

#### Option 1: Android Studio (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/Hichri-Hassan/mindflow.git
cd mindflow/kotlin-android

# 2. Open in Android Studio
# File → Open → Select 'kotlin-android' folder

# 3. Wait for Gradle sync to complete

# 4. Run the app
# Click the green ▶️ Run button or press Shift+F10
```

#### Option 2: Command Line

```bash
# 1. Clone and navigate
git clone https://github.com/Hichri-Hassan/mindflow.git
cd mindflow/kotlin-android

# 2. Build the project
./gradlew clean build

# 3. Install on connected device/emulator
./gradlew installDebug

# 4. Launch the app
adb shell am start -n com.mindflow.app/.MainActivity
```

### 🎮 Testing Platform Support

✅ **Android Emulator** (API 26-34)
- Tested on: Medium Phone API 36.1 (Android 14)
- Works on any emulator with API 26+

✅ **Physical Android Devices** (Android 8.0+)
- Tested on real devices running Android 8.0 through 14
- Full compatibility with phones and tablets

### 🔑 Optional: Enable AI Features (Gemini)

The app works perfectly without this step! But if you want to try the Gemini AI analysis:

```bash
# 1. Get a free API key from Google AI Studio
# Visit: https://makersuite.google.com/app/apikey

# 2. Add your key to AppConfig.kt
# File: app/src/main/java/com/mindflow/app/config/AppConfig.kt
# Change: const val GEMINI_API_KEY: String = "YOUR_API_KEY_HERE"

# 3. Rebuild and run
./gradlew installDebug
```

**Note**: Without an API key, the app uses an intelligent keyword-based analysis that still provides meaningful insights!

## 🏗️ Architecture & Design Patterns

### MVVM Architecture
```
┌─────────────────┐
│   UI Layer      │  Jetpack Compose Screens
│   (Screens)     │  - Observes StateFlow
└────────┬────────┘  - User interactions
         │
         ↓
┌─────────────────┐
│  ViewModel      │  Business Logic
│  (StateFlow)    │  - Manages UI state
└────────┬────────┘  - Coordinates data
         │
         ↓
┌─────────────────┐
│  Repository     │  Data Abstraction
│                 │  - Single source of truth
└────────┬────────┘  - Coordinates data sources
         │
         ↓
┌─────────────────┐
│  Data Sources   │  Room Database + AI Service
│  (Room DAO)     │  - Local persistence
└─────────────────┘  - Gemini AI API
```

### Key Design Patterns

- **MVVM (Model-View-ViewModel)**: Separation of concerns between UI and business logic
- **Repository Pattern**: Abstracts data sources from ViewModels
- **Dependency Injection**: ViewModelFactory provides dependencies
- **Observer Pattern**: StateFlow for reactive UI updates
- **Singleton**: Database instance shared across app
- **Strategy Pattern**: AI analyzer with fallback strategies (Gemini AI → Keyword matching)

### Data Flow

```
User Input → Screen → ViewModel → Repository → Room DAO → SQLite
                ↑                                          ↓
                └──────── StateFlow (reactive) ←───────────┘
```

## 🎨 UI/UX Design

- **Material 3 Design System** with custom purple gradient theme
- **Glassmorphism-inspired** cards with blur effects
- **Emoji-based mood indicators** for intuitive visualization
- **Smooth animations** using Compose animation APIs
- **Responsive layouts** that adapt to different screen sizes
- **Dark-mode ready** color scheme

## 🧪 Testing & Quality Assurance

### ✅ Verified Functionality

- **Data Persistence**: All entries correctly saved and retrieved from Room Database
- **Streak Calculation**: Accurate consecutive day tracking with proper reset logic
- **AI Analysis**: Both Gemini AI and offline keyword fallback tested
- **Navigation**: Smooth transitions between all screens
- **State Management**: Reactive UI updates via StateFlow
- **Error Handling**: Graceful fallbacks for network/API issues

### � Build Configuration

```kotlin
// Gradle versions
Gradle: 8.5
Android Gradle Plugin: 8.2.0
Kotlin: 1.9.25

// Compile options
Java Version: 17
Min SDK: 26 (Android 8.0 Oreo)
Target SDK: 34 (Android 14)
Compile SDK: 34
```

## 🌟 Kotlin Features Showcase

This project demonstrates modern Kotlin best practices:

- **Coroutines**: Async database operations and API calls
- **Flow & StateFlow**: Reactive data streams
- **Sealed Classes**: Type-safe mood categories
- **Extension Functions**: Reusable utility functions
- **Data Classes**: Immutable entity models
- **Object Declarations**: Singleton database and config
- **Null Safety**: Leveraging Kotlin's type system
- **Scope Functions**: Clean, idiomatic code
- **Lambdas & Higher-Order Functions**: Compose UI patterns

## 📋 Contest Requirements Checklist

✅ **Kotlin Version**: 1.9.25 (>= 1.7.0 required)
✅ **Platform Support**: Android emulator + physical devices (2 platforms)  
✅ **No Additional Setup**: Works out of the box with offline fallback
✅ **Comprehensive README**: Installation, testing, and feature guide included
✅ **GitHub Repository**: Public repo with clear structure
✅ **Fully Functioning**: All features tested and working
✅ **API Key Handling**: Optional with intelligent fallback (no setup needed)

## 🚀 Future Enhancements

- [ ] Export mood data to CSV/PDF
- [ ] Customizable mood categories
- [ ] Dark mode toggle
- [ ] Data backup to cloud
- [ ] Reminder notifications
- [ ] Multi-language support
- [ ] Mood prediction using ML

## �📄 License

```
MIT License

Copyright (c) 2025 MindFlow

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- [Kotlin Programming Language](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Gemini AI](https://ai.google.dev/)
- [Material Design 3](https://m3.material.io/)
- [Android Developers](https://developer.android.com/)

## 👨‍💻 Author

**Hassan Hichri**
- GitHub: [@Hichri-Hassan](https://github.com/Hichri-Hassan)
- Project: [MindFlow](https://github.com/Hichri-Hassan/mindflow)

---

**Built with ❤️ for the Kotlin Contest 2025**

*Empowering mental wellness through technology* 🧠✨
