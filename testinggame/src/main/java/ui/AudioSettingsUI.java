package ui;

import audio.AudioManager;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Audio settings UI component for the game
 * Provides controls for music/sound volume and enable/disable toggles
 */
public class AudioSettingsUI {
    
    private AudioManager audioManager;
    private battle.BattleUI battleUI; // Reference to BattleUI for AV value updates
    private VBox settingsContainer;
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private CheckBox musicEnabledCheckbox;
    private CheckBox soundEnabledCheckbox;
    private CheckBox showAVValueCheckbox;
    private Button closeButton;
    
    public AudioSettingsUI() {
        this.audioManager = AudioManager.getInstance();
        createUI();
        setupEventHandlers();
    }
    
    /**
     * Set the BattleUI reference for AV value updates
     */
    public void setBattleUI(battle.BattleUI battleUI) {
        this.battleUI = battleUI;
    }
    
    private void createUI() {
        // Main container
        settingsContainer = new VBox(15);
        settingsContainer.setAlignment(Pos.CENTER);
        settingsContainer.setPadding(new Insets(20));
        
        // Background
        Rectangle background = new Rectangle(400, 300, Color.web("#2b2f31", 0.95));
        background.setArcWidth(16);
        background.setArcHeight(16);
        background.setStroke(Color.web("#5aa0ff"));
        background.setStrokeWidth(2);
        
        // Title
        Text title = new Text("Audio Settings");
        title.setFont(Font.font(24));
        title.setFill(Color.BLACK);
        
        // Music settings
        VBox musicSettings = createVolumeControl("Music Volume", FXGL.getSettings().getGlobalMusicVolume());
        HBox musicSliderContainer = (HBox) musicSettings.getChildren().get(1);
        musicVolumeSlider = (Slider) musicSliderContainer.getChildren().get(1);
        
        musicEnabledCheckbox = new CheckBox("Enable Music");
        musicEnabledCheckbox.setSelected(audioManager.isMusicEnabled());
        musicEnabledCheckbox.setTextFill(Color.BLACK);
        
        // Sound settings
        VBox soundSettings = createVolumeControl("Sound Effects Volume", FXGL.getSettings().getGlobalMusicVolume());
        HBox soundSliderContainer = (HBox) soundSettings.getChildren().get(1);
        soundVolumeSlider = (Slider) soundSliderContainer.getChildren().get(1);
        
        soundEnabledCheckbox = new CheckBox("Enable Sound Effects");
        soundEnabledCheckbox.setSelected(audioManager.isSoundEnabled());
        soundEnabledCheckbox.setTextFill(Color.BLACK);
        
        // Show AV Value checkbox
        showAVValueCheckbox = new CheckBox("Show AV Value");
        showAVValueCheckbox.setSelected(battle.BattleUI.showAVValue);
        showAVValueCheckbox.setTextFill(Color.BLACK);
        
        // Close button
        closeButton = new Button("Close");
        closeButton.setPrefWidth(100);
        closeButton.setPrefHeight(35);
        
        // Add all components
        settingsContainer.getChildren().addAll(
            title,
            musicSettings,
            musicEnabledCheckbox,
            soundSettings,
            soundEnabledCheckbox,
            showAVValueCheckbox,
            closeButton
        );
        
        // Add background behind everything
        //settingsContainer.getChildren().add(0, background);
    }
    
    private VBox createVolumeControl(String labelText, double initialValue) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 0, 50)); // top, right, bottom, left
        Label label = new Label(labelText);
        label.setTextFill(Color.BLACK);
        label.setFont(Font.font(14));
        
        Slider slider = new Slider(0.0, 1.0, initialValue);
        slider.setPrefWidth(200);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.25);
        slider.setMinorTickCount(1);
        
        HBox sliderContainer = new HBox(10);
        sliderContainer.setAlignment(Pos.CENTER_LEFT);
        sliderContainer.getChildren().addAll(
            new Label("0%"),
            slider,
            new Label("100%")
        );
        
        container.getChildren().addAll(label, sliderContainer);
        return container;
    }
    
    private void setupEventHandlers() {
        // Music volume slider
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioManager.setMusicVolume(newVal.doubleValue());
        });
        
        // Sound volume slider
        soundVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioManager.setSoundVolume(newVal.doubleValue());
        });
        
        // Music enable/disable
        musicEnabledCheckbox.setOnAction(e -> {
            audioManager.setMusicEnabled(musicEnabledCheckbox.isSelected());
        });
        
        // Sound enable/disable
        soundEnabledCheckbox.setOnAction(e -> {
            audioManager.setSoundEnabled(soundEnabledCheckbox.isSelected());
        });
        
        // Show AV Value checkbox
        showAVValueCheckbox.setOnAction(e -> {
            battle.BattleUI.showAVValue = showAVValueCheckbox.isSelected();
            // Update AV values display if battle UI exists
            if (battleUI != null) {
                battleUI.toggleAVValueDisplay();
            }
        });
        
        // Close button
        closeButton.setOnAction(e -> {
            hide();
        });
    }
    
    public VBox getContainer() {
        return settingsContainer;
    }
    
    public void show() {
        settingsContainer.setVisible(true);
        settingsContainer.setManaged(true);
    }
    
    public void hide() {
        settingsContainer.setVisible(false);
        settingsContainer.setManaged(false);
    }
    
    public boolean isVisible() {
        return settingsContainer.isVisible();
    }
    
    public void toggle() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }
}
