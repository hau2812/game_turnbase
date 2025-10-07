package audio;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized audio management system for the game
 * Handles background music, sound effects, and audio settings
 */
public class AudioManager {
    
    // Music tracks
    private static final String BATTLE_MUSIC = "battle_theme.mp3";
    private static final String Flamita_BOSS_MUSIC = "flamita_boss_theme.mp3";
    private static final String Mabel_BOSS_MUSIC = "mabel_boss_theme.mp3";
    private static final String MAP_MUSIC = "map_exploration.mp3";
    private static final String VICTORY_MUSIC = "victory_theme.mp3";
    private static final String MENU_MUSIC = "menu_theme.mp3";
    
    // Sound effects
    private static final String SWORD_SLASH = "sword_slash.wav";
    private static final String MAGIC_CAST = "magic_cast.wav";
    private static final String HEAL_CAST = "heal_cast.wav";
    private static final String BUTTON_CLICK = "button_click.wav";
    private static final String DAMAGE_TAKEN = "damage_taken.wav";
    private static final String LEVEL_UP = "level_up.wav";
    private static final String ITEM_PICKUP = "item_pickup.wav";
    
    private static AudioManager instance;
    private Map<String, Music> musicTracks;
    private Map<String, Sound> soundEffects;
    private Music currentMusic;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private double musicVolume = 0.3;
    private double soundVolume = 0.5;
    
    private AudioManager() {
        musicTracks = new HashMap<>();
        soundEffects = new HashMap<>();
        loadAudioAssets();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Load all audio assets into memory
     */
    private void loadAudioAssets() {
        FXGL.getSettings().setGlobalMusicVolume(musicVolume);
        FXGL.getSettings().setGlobalSoundVolume(soundVolume);
        try {
            // Load music tracks
            loadMusic(BATTLE_MUSIC);
            loadMusic(MAP_MUSIC);
            loadMusic(VICTORY_MUSIC);
            loadMusic(MENU_MUSIC);
            loadMusic(Flamita_BOSS_MUSIC);
            loadMusic(Mabel_BOSS_MUSIC);
            // Load sound effects
            loadSound(SWORD_SLASH);
            loadSound(MAGIC_CAST);
            loadSound(HEAL_CAST);
            loadSound(BUTTON_CLICK);
            loadSound(DAMAGE_TAKEN);
            loadSound(LEVEL_UP);
            loadSound(ITEM_PICKUP);
            
        } catch (Exception e) {
            System.err.println("Error loading audio assets: " + e.getMessage());
            // Continue without audio if files are missing
        }
    }
    
    private void loadMusic(String filename) {
        try {
            Music music = FXGL.getAssetLoader().loadMusic(filename);
            musicTracks.put(filename, music);
        } catch (Exception e) {
            System.err.println("Could not load music: " + filename);
        }
    }
    
    private void loadSound(String filename) {
        try {
            Sound sound = FXGL.getAssetLoader().loadSound(filename);
            soundEffects.put(filename, sound);
        } catch (Exception e) {
            System.err.println("Could not load sound: " + filename);
        }
    }
    
    /**
     * Play background music with crossfade transition
     */
    public void playMusic(String musicKey, boolean loop) {
        if (!musicEnabled) return;
        
        Music music = musicTracks.get(musicKey);

        if (music == null) {
            System.err.println("Music not found: " + musicKey);
            return;
        }

        // Stop current music if playing
        if (currentMusic != null) {
            // Use FXGL's built-in method to stop music
            FXGL.getAudioPlayer().stopMusic(currentMusic);
        }
        
        // Use FXGL's built-in method to play music
        FXGL.getAudioPlayer().playMusic(music);
        //Change Volume
        music.getAudio().setVolume(FXGL.getSettings().getGlobalMusicVolume());
        currentMusic = music;
    }
    
    /**
     * Stop current background music
     */
    public void stopMusic() {
        if (currentMusic != null) {
            FXGL.getAudioPlayer().stopMusic(currentMusic);
            currentMusic = null;
        }
    }
    
    /**
     * Play sound effect
     */
    public void playSound(String soundKey) {
        if (!soundEnabled) return;
        
        Sound sound = soundEffects.get(soundKey);
        if (sound == null) {
            System.err.println("Sound not found: " + soundKey);
            return;
        }
        
        // Use FXGL's built-in method to play sound
        FXGL.getAudioPlayer().playSound(sound);
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        // Use FXGL's built-in method to set music volume
        FXGL.getSettings().setGlobalMusicVolume(volume);

    }
    
    /**
     * Set sound effects volume (0.0 to 1.0)
     */
    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0.0, Math.min(1.0, volume));
        // Use FXGL's built-in method to set sound volume
        FXGL.getSettings().setGlobalSoundVolume(volume);
    }
    
    /**
     * Enable/disable music
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null) {
            FXGL.getAudioPlayer().stopMusic(currentMusic);
        }
    }
    
    /**
     * Enable/disable sound effects
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    // Convenience methods for specific game states
    public void playBattleMusic() {
        playMusic(BATTLE_MUSIC, true);
    }

    public void playFlamitaMusic() {
        playMusic(Flamita_BOSS_MUSIC, true);
    }
    public void playMabelMusic() {
        playMusic(Mabel_BOSS_MUSIC, true);
    }
    
    public void playMapMusic() {
        playMusic(MAP_MUSIC, true);
    }
    
    public void playVictoryMusic() {
        playMusic(VICTORY_MUSIC, false);
    }
    
    public void playMenuMusic() {
        playMusic(MENU_MUSIC, true);
    }
    
    // Sound effect convenience methods
    public void playSwordSlash() {
        playSound(SWORD_SLASH);
    }
    
    public void playMagicCast() {
        playSound(MAGIC_CAST);
    }

    public void playHealCast() {
        playSound(HEAL_CAST);
    }
    
    public void playButtonClick() {
        playSound(BUTTON_CLICK);
    }
    
    public void playDamageTaken() {
        playSound(DAMAGE_TAKEN);
    }
    
    public void playLevelUp() {
        playSound(LEVEL_UP);
    }
    
    public void playItemPickup() {
        playSound(ITEM_PICKUP);
    }
    
    // Getters for settings
    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public double getMusicVolume() { return musicVolume; }
    public double getSoundVolume() { return soundVolume; }
}
