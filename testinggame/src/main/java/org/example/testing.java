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
        
        // Initialize battle system
        battleSystem = new BattleSystem(INCLUDE_HERO2, INCLUDE_ENEMY2);
        battleUI = new BattleUI(battleSystem);
        battleSystem.setBattleUI(battleUI);
        
        // Initialize battle
        battleSystem.initializeBattle();
        battleUI.initializeUI();
        
        // Start battle loop
        battleSystem.startBattleLoop();
        
        // Initialize hero skills for the first hero
        battleUI.renderHeroSkillsFor(battleSystem.getCurrentActingHero());

        // Initialize Map System
        gameMap = new GameMap();
        mapUI = new MapUI(gameMap);

        // Add instruction text
        Text instructionText = new Text("Nhấn M để mở Map - Chọn đường đi của bạn!");
        instructionText.setFont(new Font(14));
        instructionText.setFill(Color.DARKBLUE);
        instructionText.setTranslateX(250);
        instructionText.setTranslateY(580);
        getGameScene().addUINode(instructionText);
    }

    @Override
    protected void initInput() {
        // Toggle between combat and map mode with M key
        onKeyDown(KeyCode.M, () -> {
            toggleMapMode();
        });
        
        // ESC to exit map mode
        onKeyDown(KeyCode.N, () -> {
            if (inMapMode || true) {
                exitMapMode();
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

    private void exitMapMode() {
        inMapMode = false;
        
        // Hide map UI
        mapUI.hide();
        
        // Show combat UI elements again
        battleUI.showAllCombatUI();
        
        battleSystem.setMoving(true); // Resume combat
    }

    public static void main(String[] args) {
        launch(args);
    }
}