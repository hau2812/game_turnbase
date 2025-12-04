package org.example;

import audio.AudioManager;
import battle.BattleSystem;
import battle.BattleUI;
import characters.Observer;
import characters.SpecialTalents;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import map.GameMap;
import map.MapUI;
import ui.AudioSettingsUI;
import ui.InventoryUI;
import ui.ShopUI;
import ui.SimpleTestUI;
import ui.HeroSelectionUI;
import ui.BossSelectionUI;
import items.Inventory;
import items.ItemRegistry;
import shop.Shop;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    // ===== BATTLE CONFIGURATION ======================================================================================
    // Change these values to configure your battle settings
    private static final boolean HIDE_TALENTS = false;  // Set to true to hide talent text, false to show
    private static final boolean SKIP_PICKING = false;
    public static boolean EASY_MODE = false;
    public static boolean SKIP_TO_BOSS = false;
    // Available heroes to choose from (you can select multiple)
    private static final String[] AVAILABLE_HEROES = {
        "Flamita",
        //"Hero",
        "Hero2",
        "Pieberry",
        "Ina",
        "Leuna",
        "Flatina"
    };

    // Selected heroes for battle (will be set by hero selection UI)
    private String[] selectedHeroes = {
            "Flamita",
            "Flatina",
            //"Hero",
            //"Hero2",
            //"Pieberry",
            //"Ina",
            //"Leuna",
    };
    // ===== BATTLE CONFIGURATION ======================================================================================
    // Battle system components
    private BattleSystem battleSystem;
    private BattleUI battleUI;

    // Map system
    private GameMap gameMap;
    private MapUI mapUI;
    private boolean inMapMode = false;

    // Audio system
    private AudioManager audioManager;
    private AudioSettingsUI audioSettingsUI;

    // Inventory system
    private Inventory inventory;
    private InventoryUI inventoryUI;

    // Shop system
    private Shop shop;
    private ShopUI shopUI;

    // Test UI
    private SimpleTestUI testUI;
    
    // Selection UIs
    private HeroSelectionUI heroSelectionUI;
    private BossSelectionUI bossSelectionUI;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Lost Dungeon");
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

        // Initialize inventory system
        ItemRegistry.init();
        inventory = new Inventory();
        inventoryUI = new InventoryUI(inventory, battleSystem);
        System.out.println("Inventory system initialized");

        // Initialize shop system
        shop = new Shop();
        shopUI = new ShopUI(shop, inventory);
        System.out.println("Shop system initialized");

        // Give player some starting items
        giveStartingItems();

        // Initialize test UI
        testUI = new SimpleTestUI();
        System.out.println("Test UI initialized");

        // Test UI visibility (temporary)
        System.out.println("Testing UI visibility...");
        //testUI.show(); // Show test UI to verify UI system works

        // Don't force show inventory UI - let user press I to show it
        System.out.println("Game initialized successfully!");
        System.out.println("Controls: I=Inventory, S=Shop (map only), T=Test UI, F1=Audio");

        // Configure battle settings (will be updated after hero selection if not skipping)
        battleSystem.configureBattle(HIDE_TALENTS, selectedHeroes);

        battleUI = new BattleUI(battleSystem);
        battleUI.setInventory(inventory); // Connect inventory to battle UI
        SpecialTalents.setInventory(inventory);
        battleSystem.setBattleUI(battleUI);
        SpecialTalents.setBattleSystem(battleSystem);
        battleSystem.setOnBattleWon(() -> {
            // This callback is called when all enemies are defeated
            handleBattleVictory();
        });



        // Check if we should skip picking screens
        if (SKIP_PICKING) {
            // Skip hero and boss selection, use default selectedHeroes and go directly to map
            startGameWithoutBossSelection();
        } else {
            // Initialize selection UIs
            heroSelectionUI = new HeroSelectionUI(AVAILABLE_HEROES);
            heroSelectionUI.setOnStart(() -> {
                selectedHeroes = heroSelectionUI.getSelectedHeroes();
                heroSelectionUI.hide();
                showBossSelection();
            });
            
            bossSelectionUI = new BossSelectionUI();
            bossSelectionUI.setOnContinue(() -> {
                bossSelectionUI.hide();
                startGame();
            });

            // Start with hero selection
            heroSelectionUI.show();
        }

        // Start menu music
        audioManager.playMenuMusic();

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
            System.out.println(battleSystem.getHeroSlot().getCharacter().toString());


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

        // I key to toggle inventory (works in both map and battle mode)
        onKeyDown(KeyCode.I, () -> {
            System.out.println("=== I KEY PRESSED ===");
            System.out.println("Current mode: " + (inMapMode ? "MAP" : "BATTLE"));
            System.out.println("Inventory UI object: " + inventoryUI);

            if (inventoryUI != null) {
                boolean wasVisible = inventoryUI.isVisible();
                System.out.println("Inventory was visible: " + wasVisible);

                inventoryUI.toggle();

                boolean isNowVisible = inventoryUI.isVisible();
                System.out.println("Inventory is now visible: " + isNowVisible);
                System.out.println("Toggle successful: " + (wasVisible != isNowVisible));
            } else {
                System.out.println("ERROR: Inventory UI is null!");
            }
            System.out.println("=== END I KEY ===");
        });

        // S key to toggle shop (only works in map mode)
        onKeyDown(KeyCode.S, () -> {
            System.out.println("S key pressed - toggling shop");
            if (inMapMode && shopUI != null) {
                shopUI.toggle();
                System.out.println("Shop UI toggled. Visible: " + shopUI.isVisible());
            } else if (!inMapMode) {
                System.out.println("Shop can only be opened in map mode!");
            } else {
                System.out.println("Shop UI is null!");
            }
        });

        // T key to toggle test UI
        onKeyDown(KeyCode.T, () -> {
            System.out.println("T key pressed - toggling test UI");
            if (testUI != null) {
                testUI.toggle();
                System.out.println("Test UI toggled. Visible: " + testUI.isVisible());
            } else {
                System.out.println("Test UI is null!");
            }
        });
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
        shopUI.hide();
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
        //Gain gold
        for (Observer.characterSlot slot : battleSystem.getAllEnemies()){
            if (slot !=null){
                inventory.addGold(100);
            }
        }

        battleSystem.removeAllBuffsDebuffsFromHeroes();
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

    private void giveStartingItems() {
        // Give player some starting consumables
        inventory.addItem(ItemRegistry.getItem("health_potion_medium"), 3);
        inventory.addItem(ItemRegistry.getItem("mana_potion_medium"), 2);
        inventory.addItem(ItemRegistry.getItem("elixir_small"), 1);

        // Give player some enemy-targeting items
        inventory.addItem(ItemRegistry.getItem("fire_bomb"), 2);
        inventory.addItem(ItemRegistry.getItem("poison_dart"), 1);
        inventory.addItem(ItemRegistry.getItem("ice_shard"), 1);
        inventory.addItem(ItemRegistry.getItem("weakness_potion"), 1);

        // Give player some starting equipment
        inventory.addItem(ItemRegistry.getItem("iron_sword"), 1);
        inventory.addItem(ItemRegistry.getItem("leather_armor"), 1);
        inventory.addItem(ItemRegistry.getItem("mana_crystal"), 1);

        System.out.println("Starting items given to player!");
    }

    private void showBossSelection() {
        bossSelectionUI.show();
    }
    
    private void startGame() {
        // Initialize Map System
        if(bossSelectionUI.isSkipToBossSelected()) {
            SKIP_TO_BOSS=true;
        }
        gameMap = new GameMap();
        mapUI = new MapUI(gameMap);
        mapUI.setBattleSystem(battleSystem);
        mapUI.setShopSystem(shop, shopUI, inventory);
        mapUI.setOnBattleModeRequested(() -> {
            // This callback is called when a battle node is clicked
            enterBattleMode();
        });
        // Apply boss selections to game map
        if (bossSelectionUI.isOufuuSelected()) {
            gameMap.addOufuuBossFight("forest");
        }
        if (bossSelectionUI.isFlamitaSelected()) {
            gameMap.addFlamitaBossFight("forest");
        }
        if (bossSelectionUI.isMabelSelected()) {
            gameMap.addMabelBossFight("forest");
        }
        if(bossSelectionUI.isEasyModeSelected()){
            EASY_MODE=true;
        }

        // Update battle system with selected heroes
        battleSystem.configureBattle(HIDE_TALENTS, selectedHeroes);
        
        // Start with map mode
        inMapMode = true;
        mapUI.showPathSelection();
    }
    
    private void startGameWithoutBossSelection() {
        // Skip boss selection, use default selectedHeroes and go directly to map
        // Update battle system with selected heroes
        battleSystem.configureBattle(HIDE_TALENTS, selectedHeroes);
        // Initialize Map System
        gameMap = new GameMap();
        mapUI = new MapUI(gameMap);
        mapUI.setBattleSystem(battleSystem);
        mapUI.setShopSystem(shop, shopUI, inventory);
        mapUI.setOnBattleModeRequested(() -> {
            // This callback is called when a battle node is clicked
            enterBattleMode();
        });
        // Start with map mode
        inMapMode = true;
        mapUI.showPathSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}