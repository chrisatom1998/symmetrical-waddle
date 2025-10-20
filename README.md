# Gem Match - Match-3 Game for Android

A classic match-3 puzzle game with a gem theme for Android devices.

## Features

- **8x8 Game Board**: Classic grid-based gameplay
- **6 Gem Types**: Colorful gems including Red, Blue, Green, Yellow, Purple, and Orange
- **Match Detection**: Automatic detection of horizontal and vertical matches (3+ gems)
- **Cascade System**: Gems fall and refill automatically after matches
- **Score Tracking**: Points awarded for each matched gem
- **Move Counter**: Track the number of moves made
- **Touch Controls**: Simple tap-to-select and swap mechanics

## Project Structure

```
app/src/main/java/com/example/gemmatch/
├── model/
│   └── Gem.kt              # Gem data models and types
├── game/
│   ├── GameBoard.kt        # Game logic and board management
│   └── GameView.kt         # Custom view for rendering and input
└── ui/
    └── MainActivity.kt     # Main activity
```

## Game Mechanics

### How to Play

1. Tap a gem to select it (it will be highlighted)
2. Tap an adjacent gem to swap positions
3. Match 3 or more gems of the same type horizontally or vertically
4. Gems automatically cascade down to fill empty spaces
5. New gems appear from the top to fill the board
6. Score points with each match!

### Core Components

**Gem Types**:
- Each gem has a unique color and type
- 6 different gem types available
- Random generation ensures variety

**GameBoard**:
- Manages the 8x8 grid of gems
- Handles swap validation (only adjacent gems)
- Detects matches in rows and columns
- Implements gravity system for falling gems
- Prevents initial board states with matches

**GameView**:
- Custom Android View for rendering
- Touch input handling
- Visual feedback for selected gems
- Draws gems as colored circles

## Technical Details

- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL

## Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on an emulator or device

```bash
./gradlew build
```

## Future Enhancements

Potential features for future versions:

- Animations for gem swaps and matches
- Sound effects and background music
- Special power-up gems (bombs, lightning, etc.)
- Level system with objectives
- High score persistence
- Particle effects for matches
- Timed challenges
- Combo multipliers

## License

This is a sample project for educational purposes.
