# Audio Resources Directory

This directory contains all audio assets for the game.

## Directory Structure

```
audio/
├── music/           # Background music tracks
│   ├── battle_theme.mp3
│   ├── map_exploration.mp3
│   ├── victory_theme.mp3
│   └── menu_theme.mp3
└── sounds/          # Sound effects
    ├── sword_slash.wav
    ├── magic_cast.wav
    ├── button_click.wav
    ├── damage_taken.wav
    ├── level_up.wav
    └── item_pickup.wav
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

## Integration Notes

The AudioManager class automatically loads files from this directory structure. Simply place your audio files in the appropriate subdirectories and they will be available in-game.

For testing without audio files, the system will continue to work but will log warnings for missing files.
