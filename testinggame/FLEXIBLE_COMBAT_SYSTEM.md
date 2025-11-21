# Flexible Combat System - 1v1 and 2v2 Support

## Overview
The combat system has been modified to support flexible team compositions:
- 1 Hero vs 1 Enemy
- 1 Hero vs 2 Enemies  
- 2 Heroes vs 1 Enemy
- 2 Heroes vs 2 Enemies (original)

## Configuration

### In `testing.java`
```java
// Configuration for which characters to include
private static final boolean INCLUDE_HERO2 = false;  // Set to false for 1 hero
private static final boolean INCLUDE_ENEMY2 = false; // Set to false for 1 enemy
```

### In `Observer.java`
The system automatically configures character creation based on the settings above.

## Changes Made

### 1. Observer.CharacterSlotRegistry
- Added configuration flags `createHero2` and `createEnemy2`
- Made Hero2 and Enemy2 creation conditional
- Only register non-null character slots

### 2. UI System
- All UI elements (health bars, MP bars, lines, text) are now conditionally created
- Only elements for existing characters are added to the scene
- Null checks prevent crashes when accessing non-existent UI elements

### 3. Combat Logic
- Turn system handles missing heroes/enemies gracefully
- Line movement system skips null lines
- Target selection only allows valid targets
- Enemy AI adapts to available hero targets

### 4. Health/MP Management
- Health bar updates check for null elements
- MP bar updates handle missing heroes
- Damage application safely handles all character combinations

## How to Use

### For 1 Hero vs 1 Enemy:
```java
private static final boolean INCLUDE_HERO2 = false;
private static final boolean INCLUDE_ENEMY2 = false;
```

### For 1 Hero vs 2 Enemies:
```java
private static final boolean INCLUDE_HERO2 = false;
private static final boolean INCLUDE_ENEMY2 = true;
```

### For 2 Heroes vs 1 Enemy:
```java
private static final boolean INCLUDE_HERO2 = true;
private static final boolean INCLUDE_ENEMY2 = false;
```

### For 2 Heroes vs 2 Enemies (original):
```java
private static final boolean INCLUDE_HERO2 = true;
private static final boolean INCLUDE_ENEMY2 = true;
```

## Technical Details

### Null Safety
All methods now include null checks for:
- Character slots (`heroSlot2`, `enemySlot2`)
- UI elements (health bars, MP bars, text elements)
- Combat lines (`greenLine`, `yellowLine`)

### Enemy AI Adaptation
The enemy AI now:
- Checks if Hero2 exists before targeting
- Handles cases where only one hero is available
- Gracefully handles edge cases where no valid targets exist

### Turn System
The turn system:
- Only processes lines for existing characters
- Skips turn logic for null character slots
- Maintains proper turn order regardless of team composition

## Benefits
- **Flexible Gameplay**: Support for various team compositions
- **Robust Code**: Extensive null checking prevents crashes
- **Easy Configuration**: Simple boolean flags control team setup
- **Backward Compatible**: Original 2v2 functionality preserved
