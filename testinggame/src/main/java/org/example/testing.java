package org.example;

import audio.AudioManager;
import battle.BattleSystem;
import battle.BattleUI;
import characters.SpecialTalents;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import map.GameMap;
import map.MapUI;
import ui.AudioSettingsUI;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    // ===== BATTLE CONFIGURATION ======================================================================================
    // Change these values to configure your battle settings
    private static final boolean HIDE_TALENTS = true;  // Set to true to hide talent text, false to show
    
    // Available heroes to choose from (you can select multiple)
    private static final String[] AVAILABLE_HEROES = {
        "Flamita",
        "Hero",
        "Hero2",
        "Pieberry",
        "Ina",
        "Leuna"
    };
    
    // Selected heroes for battle (choose which ones to use)
    private static final String[] SELECTED_HEROES = {
        //"Ina",
        "Flamita",
        "Hero2",
//        "Pieberry",
        "Leuna"

    };
    // ===== BATTLE CONFIGURATION ======================================================================================
    // Battle system components
    private BattleSystem battleSystem;
    private BattleUI battleUI;
    private SpecialTalents specialTalents;
    
    // Map system
    private GameMap gameMap;
    private MapUI mapUI;
    private boolean inMapMode = false;
    
    // Audio system
    private AudioManager audioManager;
    private AudioSettingsUI audioSettingsUI;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Hero vs Enemy");
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);
        
        // Initialize audio system
        audioManager = AudioManager.getInstance();
        audioSettingsUI = new AudioSettingsUI();
        audioSettingsUI.getContainer().setTranslateX(200);
        audioSettingsUI.getContainer().setTranslateY(100);
        getGameScene().addUINode(audioSettingsUI.getContainer());
        audioSettingsUI.hide(); // Start hidden
        
        // Initialize battle system (but don't start it yet)
        battleSystem = new BattleSystem();
        
        // Configure battle settings
        battleSystem.configureBattle(HIDE_TALENTS, SELECTED_HEROES);
        
        battleUI = new BattleUI(battleSystem);
        battleSystem.setBattleUI(battleUI);
        SpecialTalents.setBattleSystem(battleSystem);
        battleSystem.setOnBattleWon(() -> {
            // This callback is called when all enemies are defeated
            handleBattleVictory();
        });
        
        // Initialize Map System first
        gameMap = new GameMap();
        mapUI = new MapUI(gameMap);
        mapUI.setBattleSystem(battleSystem);
        mapUI.setOnBattleModeRequested(() -> {
            // This callback is called when a battle node is clicked
            enterBattleMode();
        });

        // Start with map mode
        inMapMode = true;
        mapUI.showPathSelection();
        
        // Start menu music
        audioManager.playMenuMusic();

        // Add instruction text

    }

    @Override
    protected void initInput() {
        // N key to exit map mode and start battle
        onKeyDown(KeyCode.N, () -> {
            if (inMapMode) {
                exitMapMode();
            }
        });

        onKeyDown(KeyCode.B, () -> {
                // Debug key - can be used for testing
            //System.out.println(battleSystem.getEnemySlot().getCharacter().toString());
            System.out.println(battleSystem.getEnemySlot().getCurrentMp());


        });
        
        // M key to return to map mode from battle
        onKeyDown(KeyCode.M, () -> {
            if (!inMapMode) {
                enterMapMode();
            }
        });
        
        // F1 key to toggle audio settings
        onKeyDown(KeyCode.F1, () -> {
            audioSettingsUI.toggle();
        });
    }

    private void toggleMapMode() {
        if (inMapMode) {
            exitMapMode();
        } else {
            enterMapMode();
        }
    }

    private void enterMapMode() {
        inMapMode = true;
        battleSystem.setMoving(false); // Pause combat
        
        // Hide combat UI elements temporarily
        battleUI.hideAllCombatUI();
        
        // Show map UI
        mapUI.showPathSelection();
    }
    
    private void enterBattleMode() {
        inMapMode = false;
        
        // Hide map UI
        mapUI.hide();
        
        // Stop any existing battle loop first
        battleSystem.stopBattleLoop();
        


        
        // Always reinitialize battle system for map battles
        battleSystem.initializeBattle();
        battleUI.initializeUI();
        
        // Start fresh battle loop
        battleSystem.startBattleLoop();
        
        battleUI.renderHeroSkillsFor(battleSystem.getCurrentActingHero());
        battleUI.showAllCombatUI();
        battleSystem.setMoving(true);
    }

    private void exitMapMode() {
//        inMapMode = false;
//
//        // Hide map UI
//        mapUI.hide();
//
//        // Initialize battle if not already done
//        if (!battleSystem.isMoving()) {
//            battleSystem.initializeBattle();
//            battleUI.initializeUI();
//            battleSystem.startBattleLoop();
//            battleUI.renderHeroSkillsFor(battleSystem.getCurrentActingHero());
//            battleSystem.setMoving(true);
//
//        } else {
//            // Show combat UI elements again
//            battleUI.showAllCombatUI();
//            battleSystem.setMoving(true); // Resume combat
//        }
    }
    
    private void handleBattleVictory() {
        // Clear all battle UI elements
        battleUI.clearAllBattleUI();
        
        // Clear old enemy data to prevent showing dead enemies in next battle
        battleSystem.clearEnemyData();
        
        // Stop the battle loop properly
        battleSystem.stopBattleLoop();
        
        // Return to map mode - show the selected path instead of path selection
        inMapMode = true;
        if (gameMap.getSelectedPath() != null) {
            // Show the selected path with nodes
            mapUI.showSelectedPath();
        } else {
            // Fallback to path selection if no path is selected
            mapUI.showPathSelection();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}