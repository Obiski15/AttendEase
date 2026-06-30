# AttendEase Android Client

The official mobile client for the **AttendEase** attendance management system. Built using modern Android development practices, **Kotlin**, and **Jetpack Compose**.

---

## Tech Stack & Libraries

- **UI Framework:** Jetpack Compose (Declarative UI)
- **Dependency Injection:** [Koin](https://insert-koin.io/) (Koin Compose)
- **Networking:** [Ktor Client](https://ktor.io/) (with Content Negotiation & JSON Serialization)
- **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Local Persistence & Caching:** Room Database (Offline-first architecture)
- **Background Processing:** WorkManager (Offline data sync)
- **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Async & Streams:** Kotlin Coroutines & Flow (StateFlow / SharedFlow)

---

## Key Features

- **Offline Resilience & Cache-First Architecture:** Ensures instantaneous UI rendering via local Room Database cache. Background network fetches seamlessly synchronize the UI while offline actions (e.g. check-ins) are queued via WorkManager and executed when connectivity is restored.
- **Role-Based Access Control:** Distinct dashboards and capabilities tailored to Admins, Lecturers, and Students.
- **Geofenced Check-ins:** Prevents proxy attendance by enforcing a strict geographical radius.
- **Secure Release Builds:** Full v2/v3 signing, ProGuard minification/obfuscation, and local keystore property abstraction for CI/CD safety.

---

## Project Directory Structure

```
app/src/main/java/com/example/attendease/
├── MainActivity.kt               # Root Activity
├── AttendanceApplication.kt      # Application Class (Koin Initializer)
├── di/
│   └── AppModule.kt              # Koin Dependency Injection module definitions
├── enums/                        # Shared Enums (e.g., UserRole)
├── dto/                          # Data Transfer Objects
│   ├── request/                  # API request schemas (Auth, Courses, etc.)
│   └── response/                 # API response schemas (Auth, Dashboard, etc.)
├── data/
│   ├── api/                      # Ktor API endpoints (BaseApi, AuthApi, CourseApi, etc.)
│   ├── repository/               # Repository layers managing data operations and local caching
│   ├── session/                  # Local session management (SessionManager, SharedPreferences)
│   └── network/                  # NetworkClient configuration
├── viewModel/                    # ViewModels exposing screen states and actions
├── state/                        # State model classes (e.g., LoginUiState)
├── utils/                        # Network / Connectivity Observers and general utility helpers
└── ui/
    ├── theme/                    # Theme configurations, shapes, colors, dimensions, and typography
    ├── components/               # Reusable UI widgets (TopAppBar, BottomBar, StatCard, GridActionCard)
    ├── navigation/               # Navigation graphs (RootNavGraph, AdminNavGraph, LecturerNavGraph, etc.)
    └── screens/                  # Feature Screens
        ├── auth/                 # Login Screen
        ├── admin/                # Admin dashboards and management modules
        ├── lecturer/             # Lecturer session controls and history
        └── student/              # Attendance scanning and student dashboard
```

---

## Getting Started

### Prerequisites
- **Android Studio** (Ladybug / Iguana or newer)
- **JDK 17** or higher
- Android SDK 34+

### 1. Clone the repository
```bash
git clone <repository-url>
cd AttendEase
```

### 2. Configure Local Properties
Create a `local.properties` file in the root project folder (if not present) and configure your backend development URL:
```properties
# Local development base URL (Ktor client will compile BuildConfig.BASE_URL from this)
BASE_URL=http://10.0.2.2:8000/api/v1
```
*Note: `10.0.2.2` is the special alias mapping to the host system's `localhost` from inside the Android Emulator.*

### 3. Connect to a Physical Device (via ADB Reverse Proxy)
If you are debugging on a physical Android device over USB or Wi-Fi, run the following command in your terminal to map the device's local port to the backend port running on your machine:
```bash
adb reverse tcp:8000 tcp:8000
```
Then, update your `local.properties` to:
```properties
BASE_URL=http://localhost:8000/api/v1
```

### 4. Configure Release Keystore (Optional)
If you need to build the `release` variant locally, create a `keystore.properties` file in the root project folder:
```properties
storeFile=release.keystore
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```
*Note: Both `*.keystore` files and `keystore.properties` are safely ignored by `.gitignore`.*

### 5. Build and Run
- Sync the Gradle project in Android Studio.
- Press **Run** (`Shift + F10`) to deploy the application.

---

## Contribution Guidelines

### 1. Code Architecture Standards
- **Separation of Concerns:** Keep Composables focused strictly on rendering UI. Never execute database transactions, repository queries, or network requests directly inside Compose code. Always delegate logic through the appropriate **ViewModel**.
- **State Collection:** Always collect ViewModel flows inside screens using `collectAsState()` or `collectAsStateWithLifecycle()` to prevent memory leaks.
- **Dependency Injection:** Use Koin for constructor injection. Declare new dependencies, ViewModels, and API instances in [AppModule.kt](file:///c:/Users/user/Documents/App-Development/app/src/main/java/com/example/attendease/di/AppModule.kt). Use `koinInject()` for Compose UI injection.

### 2. UI/UX Rules
- **Design Tokens:** Always utilize dimen/spacing tokens defined in [Spacing](file:///c:/Users/user/Documents/App-Development/app/src/main/java/com/example/attendease/ui/theme/Dimens.kt) (e.g. `Spacing.md`, `Spacing.lg`) rather than using ad-hoc hardcoded dp values.
- **Styling consistency:** Use Material Theme tokens (`MaterialTheme.colorScheme.primary`, etc.) for unified dark/light support and consistent styling across screens.
- **No Simple Mockups:** Avoid static, mock-only implementations. Integrate every new UI component with actual API routes and live ViewModel states.

### 3. Documentation
- Write readable, maintainable code.
- Write comments **only** when explaining the **why** of a complex business decision, not the **what** of standard code actions.

### 4. Useful Gradle Commands
Run these commands in the terminal under the client root folder:
- **Compile Kotlin code:**
  ```powershell
  ./gradlew compileDebugKotlin
  ```
- **Run Unit Tests:**
  ```powershell
  ./gradlew test
  ```
- **Build Debug APK:**
  ```powershell
  ./gradlew assembleDebug
  ```

### 5. Code Formatting
- Follow the official Kotlin coding conventions.
- Maintain formatting configurations set in `.editorconfig` (if present).
- Ensure unused imports are removed before submitting PRs.

