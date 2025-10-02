# Battle System Refactor Documentation

## Overview
The combat system has been successfully refactored from `testing.java` into a separate `battle` package, creating a clean separation of concerns and making the codebase more maintainable.

## New Architecture

### 1. `testing.java` - Game Core
Now serves as the main game application controller:
- **Responsibilities**: Game initialization, input handling, map system integration
- **Size**: Reduced from ~827 lines to ~104 lines
- **Focus**: High-level game flow and coordination

### 2. `battle/BattleSystem.java` - Combat Logic
Handles all combat-related logic:
- **Responsibilities**: 
  - Character slot management
  - Combat state management
  - Skill usage and damage calculation
  - Enemy AI
  - Turn system
  - Line movement mechanics
- **Key Features**:
  - Flexible team composition support (1v1, 1v2, 2v1, 2v2)
  - Clean separation from UI concerns
  - Configurable through constructor parameters

### 3. `battle/BattleUI.java` - Combat User Interface
Manages all combat-related UI elements:
- **Responsibilities**:
  - Health bars and MP bars
  - Combat timing lines
  - Skill boxes and tooltips
  - Target selection
  - UI visibility management
- **Key Features**:
  - Conditional UI creation based on team composition
  - Null-safe operations
  - Clean integration with BattleSystem

## Key Benefits

### 1. **Separation of Concerns**
- Combat logic is isolated from game flow
- UI is separated from business logic
- Each class has a single, clear responsibility

### 2. **Maintainability**
- Combat features can be modified without touching core game code
- UI changes don't affect combat logic
- Easier to debug and test individual components

### 3. **Reusability**
- BattleSystem can be reused in different contexts
- BattleUI can be extended or replaced independently
- Combat system is now modular

### 4. **Scalability**
- Easy to add new combat features
- Simple to extend UI elements
- Battle system can be enhanced without affecting other systems

## Configuration

### Team Composition
Configure in `testing.java`:
```java
private static final boolean INCLUDE_HERO2 = false;  // Set to false for 1 hero
private static final boolean INCLUDE_ENEMY2 = false; // Set to false for 1 enemy
```

### Usage Example
```java
// Initialize battle system
battleSystem = new BattleSystem(INCLUDE_HERO2, INCLUDE_ENEMY2);
battleUI = new BattleUI(battleSystem);
battleSystem.setBattleUI(battleUI);

// Initialize battle
battleSystem.initializeBattle();
battleUI.initializeUI();

// Start battle loop
battleSystem.startBattleLoop();
```

## Class Interactions

```
testing.java (Game Core)
    ↓ creates and configures
BattleSystem (Combat Logic)
    ↓ uses
BattleUI (Combat Interface)
    ↓ updates
Observer.characterSlot (Character Data)
```

## Migration Summary

### Moved from `testing.java` to `BattleSystem.java`:
- Character slot management
- Combat state variables
- Skill usage logic
- Enemy AI
- Turn system
- Line movement mechanics
- Damage calculation

### Moved from `testing.java` to `BattleUI.java`:
- All UI elements (health bars, MP bars, lines, text)
- Skill box creation and management
- Target selection UI
- UI visibility controls
- Health/MP display updates

### Remaining in `testing.java`:
- Game settings and initialization
- Input handling (keyboard controls)
- Map system integration
- High-level game flow

## Technical Details

### BattleSystem API
- `initializeBattle()`: Sets up character slots and initial state
- `startBattleLoop()`: Begins the combat timing system
- `useSkill()`: Handles skill execution and effects
- `setMoving()`: Controls combat flow state
- Getters for character slots and combat state

### BattleUI API
- `initializeUI()`: Creates and displays all combat UI elements
- `renderHeroSkillsFor()`: Updates skill boxes for active hero
- `updateMpUI()`: Updates MP display for characters
- `updateHealthUI()`: Updates health display for characters
- `hideAllCombatUI()` / `showAllCombatUI()`: Visibility management

### Integration Points
- BattleSystem and BattleUI are tightly integrated through dependency injection
- testing.java coordinates between battle system and map system
- Observer.characterSlot remains the data model for characters

## Future Enhancements

The new architecture makes it easy to add:
- Different battle modes
- New combat mechanics
- Enhanced UI effects
- Battle animations
- Combat statistics
- Save/load battle state

## Backward Compatibility

All existing functionality is preserved:
- Same combat mechanics
- Same UI appearance
- Same input controls
- Same team composition flexibility
- Same map system integration
