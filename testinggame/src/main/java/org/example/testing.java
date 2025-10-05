package org.example;

import battle.BattleSystem;
import battle.BattleUI;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import map.GameMap;
import map.MapUI;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    // Configuration for which characters to include
    private static final boolean INCLUDE_HERO2 = true;  // Set to false for 1 hero
    private static final boolean INCLUDE_ENEMY2 = true; // Set to false for 1 enemy

    // Battle system components
    private BattleSystem battleSystem;
    private BattleUI battleUI;
    
    // Map system
    private GameMap gameMap;
    private MapUI mapUI;
    private boolean inMapMode = false;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Hero vs Enemy");
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);
        
        // Initialize battle system (but don't start it yet)
        battleSystem = new BattleSystem(INCLUDE_HERO2, INCLUDE_ENEMY2);
        battleUI = new BattleUI(battleSystem);
        battleSystem.setBattleUI(battleUI);
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

        // Add instruction text
        Text instructionText = new Text("Chọn đường đi của bạn! Nhấn N để thoát khỏi Map");
        instructionText.setFont(new Font(14));
        instructionText.setFill(Color.DARKBLUE);
        instructionText.setTranslateX(250);
        instructionText.setTranslateY(580);
        getGameScene().addUINode(instructionText);
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
            System.out.println(battleSystem.getHeroSlot2().getCharacter().getUniqueValue("MANA_SHIELD"));
        });
        
        // M key to return to map mode from battle
        onKeyDown(KeyCode.M, () -> {
            if (!inMapMode) {
                enterMapMode();
            }
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
        inMapMode = false;
        
        // Hide map UI
        mapUI.hide();
        
        // Initialize battle if not already done
        if (!battleSystem.isMoving()) {
            battleSystem.initializeBattle();
            battleUI.initializeUI();
            battleSystem.startBattleLoop();
            battleUI.renderHeroSkillsFor(battleSystem.getCurrentActingHero());
            battleSystem.setMoving(true);

        } else {
            // Show combat UI elements again
            battleUI.showAllCombatUI();
            battleSystem.setMoving(true); // Resume combat
        }
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