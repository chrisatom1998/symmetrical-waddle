# Build Instructions

## Prerequisites

To build this Android project, you need:

1. **Android SDK** (API 24 or higher)
   - Download from: https://developer.android.com/studio
   - Or install Android Studio which includes the SDK

2. **Android SDK Build Tools**
   - Usually included with Android Studio
   - Can be installed via SDK Manager

3. **Java Development Kit (JDK)**
   - JDK 8 or higher (JDK 11 or 17 recommended)
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/

## Environment Setup

### Set ANDROID_HOME Environment Variable

**On Linux/macOS:**
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

Add these lines to your `~/.bashrc` or `~/.zshrc` to make them permanent.

**On Windows:**
```cmd
setx ANDROID_HOME "C:\Users\YourUsername\AppData\Local\Android\Sdk"
setx PATH "%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools"
```

## Building the Project

### Using Gradle Wrapper (Recommended)

**On Linux/macOS:**
```bash
./gradlew build
```

**On Windows:**
```cmd
gradlew.bat build
```

### Build Variants

**Debug build:**
```bash
./gradlew assembleDebug
```

**Release build:**
```bash
./gradlew assembleRelease
```

## Installing on Device

### Via ADB
```bash
./gradlew installDebug
```

### Via Android Studio
1. Open the project in Android Studio
2. Connect your Android device or start an emulator
3. Click the "Run" button or press Shift+F10

## Verification

Before attempting to build, you can verify the project structure:

```bash
./verify-structure.sh
```

This will check that all required files are present and properly formatted.

You can also validate the Kotlin code:

```bash
./validate-kotlin.sh
```

This performs basic syntax checks on all Kotlin source files.

## Common Issues

### Issue: "Android SDK not found"
**Solution:** Ensure ANDROID_HOME is set correctly and points to your Android SDK installation.

### Issue: "Gradle sync failed"
**Solution:**
1. Check your internet connection (Gradle needs to download dependencies)
2. Try running `./gradlew clean`
3. Invalidate caches in Android Studio: File → Invalidate Caches / Restart

### Issue: "compileSdkVersion is not specified"
**Solution:** Install Android SDK API level 34 via SDK Manager in Android Studio.

## Project Structure

```
symmetrical-waddle/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/gemmatch/
│           │   ├── model/         # Data models
│           │   ├── game/          # Game logic
│           │   └── ui/            # User interface
│           └── res/               # Resources (layouts, strings, colors)
├── build.gradle.kts              # Project-level build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
└── gradlew                       # Gradle wrapper script
```

## Running Tests

```bash
./gradlew test              # Run unit tests
./gradlew connectedAndroidTest  # Run instrumented tests (requires device/emulator)
```

## Code Quality

The project is structured following Android best practices:
- **Model-View separation**: Clean architecture with separated concerns
- **Kotlin**: Modern Android development language
- **Material Design**: Using Material Components for UI

## Next Steps

After a successful build:
1. Run the app on an emulator or physical device
2. Test the match-3 gameplay mechanics
3. Consider adding features from the README's "Future Enhancements" section

## Validation Results

### Structure Validation ✓
- All required files present
- All XML files are well-formed
- Proper directory structure

### Code Validation ✓
- 4 Kotlin source files
- 402 lines of code
- All files have proper package declarations
- Balanced braces and parentheses
- No syntax errors detected

**Note:** The validation scripts perform basic checks. A full compilation with Android SDK is required to ensure complete compatibility.
