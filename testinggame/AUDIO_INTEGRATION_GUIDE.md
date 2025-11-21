# Audio Integration Guide

This guide explains how to add music and sound effects to your turn-based game using the new audio system.

## Overview

The audio system consists of:
- **AudioManager**: Centralized audio control and management
- **AudioSettingsUI**: User interface for audio settings
- **Resource Structure**: Organized audio file storage
- **Integration Points**: Battle system, map system, and UI interactions

## Quick Start

### 1. Add Audio Files

Place your audio files in the following directory structure:

```
src/main/resources/audio/
├── music/
│   ├── battle_theme.mp3
│   ├── map_exploration.mp3
│   ├── victory_theme.mp3
│   └── menu_theme.mp3
└── sounds/
    ├── sword_slash.wav
    ├── magic_cast.wav
    ├── button_click.wav
    ├── damage_taken.wav
    ├── level_up.wav
    └── item_pickup.wav
```

### 2. Basic Usage

The AudioManager is already integrated into your game systems. Here's how to use it:

```java
// Get the audio manager instance
AudioManager audioManager = AudioManager.getInstance();

// Play background music
audioManager.playBattleMusic();
audioManager.playMapMusic();
audioManager.playVictoryMusic();

// Play sound effects
audioManager.playSwordSlash();
audioManager.playMagicCast();
audioManager.playButtonClick();
audioManager.playDamageTaken();

// Control audio settings
audioManager.setMusicVolume(0.7);  // 0.0 to 1.0
audioManager.setSoundVolume(0.8);  // 0.0 to 1.0
audioManager.setMusicEnabled(false);
audioManager.setSoundEnabled(false);
```

## Current Integration Points

### Battle System
- **Battle Music**: Automatically plays when battle starts
- **Victory Music**: Plays when all enemies are defeated
- **Skill Sounds**: Plays appropriate sounds based on skill type
- **Damage Sounds**: Plays when characters take damage

### Map System
- **Exploration Music**: Plays when entering map mode
- **Button Clicks**: Plays sound for all UI interactions

### Battle UI
- **Skill Selection**: Plays sound when clicking skill boxes
- **Target Selection**: Plays sound when clicking health bars

## Adding Audio Settings UI

To add audio settings to your main game:

```java
// In your main game class (e.g., testing.java)
private AudioSettingsUI audioSettingsUI;

@Override
protected void initGame() {
    // ... existing code ...
    
    // Initialize audio settings UI
    audioSettingsUI = new AudioSettingsUI();
    audioSettingsUI.getContainer().setTranslateX(200);
    audioSettingsUI.getContainer().setTranslateY(100);
    getGameScene().addUINode(audioSettingsUI.getContainer());
    audioSettingsUI.hide(); // Start hidden
}

@Override
protected void initInput() {
    // ... existing code ...
    
    // Add key binding for audio settings (e.g., F1 key)
    onKeyDown(KeyCode.F1, () -> {
        audioSettingsUI.toggle();
    });
}
```

## Customizing Audio

### Adding New Music Tracks

1. Add your audio file to `src/main/resources/audio/music/`
2. Update the AudioManager constants:
```java
private static final String NEW_TRACK = "new_track.mp3";
```
3. Load the track in the `loadAudioAssets()` method:
```java
loadMusic(NEW_TRACK);
```
4. Add a convenience method:
```java
public void playNewTrack() {
    playMusic(NEW_TRACK, true);
}
```

### Adding New Sound Effects

1. Add your audio file to `src/main/resources/audio/sounds/`
2. Update the AudioManager constants:
```java
private static final String NEW_SOUND = "new_sound.wav";
```
3. Load the sound in the `loadAudioAssets()` method:
```java
loadSound(NEW_SOUND);
```
4. Add a convenience method:
```java
public void playNewSound() {
    playSound(NEW_SOUND);
}
```

### Custom Sound Triggers

You can add custom sound triggers anywhere in your code:

```java
// In any game component
AudioManager audioManager = AudioManager.getInstance();

// Play sound when something specific happens
if (specialCondition) {
    audioManager.playLevelUp();
}

// Play custom sound
audioManager.playSound("custom_sound.wav");
```

## Audio File Requirements

### Music Files
- **Format**: MP3 or WAV
- **Quality**: 44.1kHz, 16-bit minimum
- **Length**: 2-4 minutes for looping tracks
- **Volume**: Normalized to prevent clipping

### Sound Effects
- **Format**: WAV (preferred) or MP3
- **Quality**: 44.1kHz, 16-bit minimum
- **Length**: Short clips (0.1-3 seconds)
- **Volume**: Normalized and consistent

## Recommended Audio Sources

### Free Music Resources
- [Freesound.org](https://freesound.org/) - Creative Commons sounds
- [OpenGameArt.org](https://opengameart.org/) - Game-specific audio
- [Zapsplat](https://www.zapsplat.com/) - Professional sound library (free tier)
- [YouTube Audio Library](https://www.youtube.com/audiolibrary/music) - Royalty-free music

### Battle Music Suggestions
- Epic orchestral themes
- Fast-paced electronic music
- Medieval/fantasy battle themes

### Map Music Suggestions
- Ambient exploration music
- Calm, atmospheric tracks
- Adventure-themed melodies

## Troubleshooting

### Audio Not Playing
1. Check that audio files are in the correct directory structure
2. Verify file formats are supported (MP3, WAV)
3. Check console for error messages about missing files
4. Ensure audio is enabled in settings

### Performance Issues
1. Use compressed audio formats (MP3) for music
2. Keep sound effects short and optimized
3. Consider reducing audio quality for lower-end devices

### Volume Issues
1. Normalize all audio files to consistent levels
2. Use the AudioManager volume controls to adjust levels
3. Test on different devices and speakers

## Advanced Features

### Dynamic Music
You can implement dynamic music that changes based on game state:

```java
// Example: Change music intensity based on battle progress
public void updateBattleMusicIntensity(float intensity) {
    if (intensity > 0.8f) {
        audioManager.playMusic("battle_intense.mp3", true);
    } else if (intensity > 0.4f) {
        audioManager.playMusic("battle_normal.mp3", true);
    } else {
        audioManager.playMusic("battle_calm.mp3", true);
    }
}
```

### Audio Events
You can create custom audio events for specific game moments:

```java
// Example: Play special sound for critical hits
public void onCriticalHit() {
    audioManager.playSound("critical_hit.wav");
    // Add screen shake or visual effects here
}
```

## Testing Without Audio Files

The system is designed to work even without audio files present. It will:
- Log warnings for missing files
- Continue game execution normally
- Allow all audio settings to function
- Gracefully handle audio-related errors

This makes development easier when you don't have audio assets ready yet.

## Conclusion

The audio system is now fully integrated into your game. You can:
1. Add audio files to the resources directory
2. Use the AudioManager for programmatic control
3. Add the AudioSettingsUI for user control
4. Customize and extend the system as needed

The system is designed to be robust, performant, and easy to use while providing a rich audio experience for your players.
