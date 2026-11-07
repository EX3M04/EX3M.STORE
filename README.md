# EX3M.STORE - Android App Store

A Material 3 Expressive designed Android app store that fetches applications from GitHub releases.

## Features

- **Material 3 Expressive Design** - Modern, beautiful UI with dynamic theming
- **GitHub Integration** - Fetches apps from GitHub releases automatically
- **Dynamic Color Theming** - Material You support with wallpaper-based colors
- **Background Downloads** - Download APKs in background with notifications
- **Wave Animation Progress** - Beautiful wave-style download progress indicator
- **Extensive Settings** - Deep customization of UI, downloads, and behavior
- **Download Management** - Pause, resume, cancel, and install downloads
- **Install from Notification** - Tap notification to install completed downloads

## Architecture

- **Clean Architecture** with Repository pattern
- **Hilt** for Dependency Injection
- **Room** for local database (downloads)
- **DataStore** for preferences
- **Retrofit** + **Kotlinx Serialization** for GitHub API
- **Coil** for image loading
- **Jetpack Compose** for UI
- **Coroutines** + **Flow** for async operations

## Project Structure

```
app/
├── src/main/
│   ├── java/com/ex3m/store/
│   │   ├── data/
│   │   │   ├── model/          # Data models (AppCard, DownloadEntity, etc.)
│   │   │   ├── db/             # Room database (DownloadDao, DownloadDatabase)
│   │   │   ├── prefs/          # DataStore preferences (SettingsPreferences)
│   │   │   └── repository/     # Repositories (AppRepository, DownloadRepository)
│   │   ├── di/                 # Hilt modules (NetworkModule, DatabaseModule, ServiceModule)
│   │   ├── service/            # Services (GitHubApiService, DownloadService)
│   │   ├── ui/
│   │   │   ├── main/           # Main screen (MainActivity, MainScreen, MainViewModel)
│   │   │   ├── detail/         # App detail screen (DetailScreen, DetailViewModel)
│   │   │   ├── downloads/      # Downloads screen (DownloadsScreen, DownloadsViewModel)
│   │   │   ├── settings/       # Settings screen (SettingsScreen, SettingsViewModel)
│   │   │   ├── navigation/     # Navigation (AppNavHost)
│   │   │   └── theme/          # Theme (Theme.kt)
│   │   └── Ex3mStoreApplication.kt
│   ├── res/
│   │   ├── values/strings.xml
│   │   ├── xml/                # File provider paths, backup rules
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## GitHub Repository Structure

The app expects the following structure in the GitHub repository (EX3Mka/EX3M.STORE):

```
EX3M.STORE/
├── .github/
│   └── workflows/
├── releases/                    # GitHub Releases (auto-generated)
├── MyApp.jpg                   # App icons (root level, lowercase with .jpg)
├── AnotherApp.jpg
├── descriptions/
│   ├── MyApp.txt               # Descriptions (no spaces in filename)
│   └── AnotherApp.txt
└── README.md
```

### Release Format

Each GitHub release should:
- Have a **tag_name** as version (e.g., `v1.0.0`)
- Have a **name** as app display name (e.g., `My App`)
- Have a **body** as description
- Include assets:
  - `myapp.jpg` - App icon (root level)
  - `myapp.apk` - APK file to download

### Description Files

Place description files in `descriptions/` folder:
- Filename: App name without spaces + `.txt` (e.g., `MyApp.txt` for "My App")
- Content: Full app description (markdown supported)

## Building

```bash
# Clone the repository
git clone https://github.com/EX3Mka/EX3M.STORE.git
cd EX3M.STORE

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## Configuration

### GitHub API

The app uses GitHub's public API. For higher rate limits, add a Personal Access Token in Settings → Advanced → GitHub Token.

### Customization

All settings are persisted via DataStore and include:

**Appearance:**
- Theme mode (System/Light/Dark)
- Dynamic colors (Material You)
- Card elevation, corner radius, press animation
- Animation speed multiplier
- Compact card mode
- Show version/size on cards

**Downloads:**
- Wi-Fi only downloads
- Auto-install after download
- Download notifications
- Max concurrent downloads (1-5)
- Chunk size, resume on startup

**Store:**
- Show pre-release apps
- Auto-refresh interval
- Analytics opt-in

**Advanced:**
- Debug logging
- GitHub token
- Custom API URL
- Cache settings

## Permissions

- `INTERNET` - GitHub API and downloads
- `ACCESS_NETWORK_STATE` - Check connectivity
- `FOREGROUND_SERVICE` - Background downloads
- `POST_NOTIFICATIONS` - Download progress notifications
- `REQUEST_INSTALL_PACKAGES` - Install APKs
- `READ/WRITE_EXTERNAL_STORAGE` - APK storage (legacy)
- `MANAGE_EXTERNAL_STORAGE` - Scoped storage access

## License

MIT License - see LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a Pull Request

## Screenshots

*Coming soon...*