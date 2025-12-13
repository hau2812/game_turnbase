# Dialog System Documentation

## Overview
A flexible dialog system for managing character conversations, player choices, and dynamic dialog sequences.

## Architecture

### Core Components

1. **DialogEntry** - Represents a single dialog line with:
   - Speaker name and text
   - Character image
   - Conditions (when to show)
   - Variants (different text based on context)
   - Options (player choices)
   - Actions (code to execute)
   - Next dialog IDs (branching)

2. **DialogLibrary** - Manages all registered dialogs:
   - Stores dialogs by ID
   - Indexes by purpose tag
   - Provides lookup and matching

3. **DialogContext** - Holds runtime data:
   - Heroes array
   - Custom objects (mood, flags, etc.)
   - Helper methods for common checks

4. **DialogSystem** - Manages dialog execution:
   - Queue-based processing
   - Handles options and branching
   - Supports dynamic dialog addition

5. **DialogUI** - Visual component:
   - Character image box (120x120)
   - Dialog text box (600px wide)
   - Option buttons
   - Positioned at bottom of screen

## Usage Examples

### 1. Register a Simple Dialog

```java
DialogLibrary library = DialogLibrary.getInstance();

DialogEntry greeting = new DialogEntry("greeting_1", "Hero1", "Hello!")
    .withPurpose("greeting")
    .withImage("assets/textures/sprites/hero1.png");

library.register(greeting);
```

### 2. Dialog with Variants (Mood-based)

```java
DialogEntry moodDialog = new DialogEntry("mood_dialog", "Hero2", "Good morning")
    .withPurpose("greeting")
    .withCondition(ctx -> ctx.hasHero("Hero2"))
    .withVariant(new DialogVariant("Good morning!!!", 
        ctx -> ctx.getHeroMood("Hero2") > 1))
    .withVariant(new DialogVariant("yawn!!", 
        ctx -> ctx.getHeroMood("Hero2") <= 1));

library.register(moodDialog);
```

### 3. Dialog with Player Options

```java
DialogEntry choiceDialog = new DialogEntry("choice_1", "Hero1", 
    "Should we rest?")
    .withOption(new DialogOption("Yes, rest")
        .withAction(ctx -> {
            // Heal all heroes
            for (Observer.characterSlot hero : ctx.getHeroes()) {
                if (hero != null) {
                    hero.setCurrentHp(hero.getCharacter().getHp());
                }
            }
            return null;
        })
        .withNextDialog("rest_result"))
    .withOption(new DialogOption("No, continue")
        .withNextDialog("continue_result"));

library.register(choiceDialog);
```

### 4. Campfire Scene Example

```java
// Register multiple greeting dialogs
DialogEntry greeting1 = new DialogEntry("greet_hero1_hero2", "Hero1", 
    "Hey Hero2, how are you?")
    .withPurpose("greeting")
    .withCondition(ctx -> ctx.hasHero("Hero1") && ctx.hasHero("Hero2"));

DialogEntry greeting2 = new DialogEntry("greet_hero2_hero3", "Hero2", 
    "Nice to see you, Hero3!")
    .withPurpose("greeting")
    .withCondition(ctx -> ctx.hasHero("Hero2") && ctx.hasHero("Hero3"));

library.register(greeting1);
library.register(greeting2);

// Start dialog
DialogContext context = new DialogContext(heroes);
DialogSystem system = DialogSystem.getInstance();

// Add greeting (randomly selects one if multiple match)
system.addDialogsByPurpose("greeting", context, true);

// Add campfire talk
system.addDialogsByPurpose("campfire_talk", context, true);
```

### 5. Dynamic Dialog Addition

```java
DialogEntry dynamicDialog = new DialogEntry("dynamic_1", "Hero1", 
    "Let's talk more!")
    .withOnShowAction(ctx -> {
        // Add more dialogs during runtime
        DialogSystem.getInstance().addDialogsByPurpose(
            "campfire_talk", ctx, true);
        return null;
    });
```

## Integration Steps

1. **Initialize in your main game class:**
```java
DialogUI dialogUI = new DialogUI();
DialogSystem.getInstance().setDialogUI(dialogUI);
```

2. **Register dialogs** (in initialization or separate file):
```java
DialogExamples.registerExampleDialogs();
// Or register your own dialogs
```

3. **Start a dialog sequence:**
```java
Observer.characterSlot[] heroes = battleSystem.getAllHeroes();
DialogContext context = new DialogContext(heroes);
context.put("mood", 5); // Add custom data

DialogSystem system = DialogSystem.getInstance();
system.addDialogsByPurpose("campfire_talk", context, true);
```

## Key Features

- **Purpose-based matching**: Dialogs tagged with purposes (e.g., "greeting", "campfire_talk")
- **Conditional variants**: Different text based on context (mood, hero presence, etc.)
- **Player choices**: Options that can change game state and branch dialog
- **Dynamic addition**: Add dialogs to queue during runtime
- **Random selection**: If multiple dialogs match, randomly pick one
- **Context passing**: Pass any objects through DialogContext
- **Queue-based**: Dialogs run sequentially until queue is empty

## Dialog Flow

1. Dialog is registered with conditions and purpose
2. System matches dialogs by purpose and context
3. Selected dialogs added to queue
4. System processes queue, showing each dialog
5. Player can click to continue or select option
6. Options can add more dialogs to queue
7. Sequence ends when queue is empty

## UI Layout

- **Character Image Box**: 120x120px square, left side
- **Dialog Box**: 800x150px rectangle, bottom of screen
- **Dialog Text**: 600px wide, starts at x=140
- **Options**: Buttons below text, clickable

