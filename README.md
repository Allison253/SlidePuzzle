**Slide Puzzle App (Android – Kotlin)**


An Android slide puzzle application built in Kotlin using the Android View system. The app allows users to generate a classic sliding tile puzzle from any image stored on their device.
Overview


This project dynamically converts a user-selected image into either a:
  •	3×3 (8-puzzle)
  •	4×4 (15-puzzle)

The image is divided into equal tiles, shuffled into a valid configuration, and rendered programmatically. Users solve the puzzle by sliding tiles into the empty space until the image is restored.


Key Features
  •	Image selection from device gallery
  •	Dynamic bitmap slicing into square tiles
  •	Programmatic ImageView generation and layout
  •	Solvable board generation using inversion counting
  •	Win-state detection
  •	Orientation-aware layout recalculation
  Technical Highlights
  •	Written in Kotlin
  •	Custom board management via a BoardView class
  •	Dynamic UI generation (no static grid layout)
  •	Bitmap manipulation using Bitmap.createBitmap
  •	Shuffle algorithm with solvability validation
  •	Internal tracking of tile positions and empty space index


Core Logic Responsibilities
  •	Splitting a bitmap into tiles
  •	Maintaining tile position state
  •	Handling tile movement
  •	Generating solvable shuffled states
  •	Detecting puzzle completion
  •	Redrawing layout on configuration changes

Future Enhancements
    •	Move counter and timer
    •	Tile slide animations
    •	State persistence across app restarts
    •	Additional grid sizes
