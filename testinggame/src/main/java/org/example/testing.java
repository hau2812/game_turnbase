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
import ui.MenuUI;
import items.Inventory;
import items.ItemRegistry;
import shop.Shop;
import dialog.DialogSystem;
import dialog.DialogUI;
import dialog.DialogRegistrations;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    // ===== BATTLE CONFIGURATION ======================================================================================
    // Change these values to configure your battle settings
    private static final boolean HIDE_TALENTS = false;  // Set to true to hide talent text, false to show
    private static final boolean SKIP_PICKING = false;
    public static boolean EASY_MODE = false;
    public static boolean SKIP_TO_BOSS = false;
    public static int gold_coin = 0;
    public static int return_time = 0;
    public static boolean skip_opening = false;

    // Available heroes to choose from (you can select multiple)
    public static String[] ALL_HEROES = {
            "Flamita",
            "Hero",
            "Hero2",
            "Pieberry",
            "Ina",
            "Leuna",
            "Flatina",
            "Chigon"
    };
    // Available heroes to choose from (you can select multiple)
    public static String[] AVAILABLE_HEROES = {
        "Flamita",
        "Hero",
//        "Hero2",
//        "Pieberry",
//        "Ina",
//        "Leuna",
//        "Flatina",
//        "Chigon"
    };

    
    /**
     * Get available heroes array
     */
    public static String[] getAvailableHeroes() {
        return AVAILABLE_HEROES;
    }
    
    /**
     * Set available heroes array (used when loading save)
     */
    public static void setAvailableHeroes(String[] heroes) {
        AVAILABLE_HEROES = heroes;
    }

    public static String[] getAllHeros() {return ALL_HEROES;}

    public static void setAllHeros(String[] allHeros) {ALL_HEROES = allHeros;}

    // Selected heroes for battle (will be set by hero selection UI)
    public String[] selectedHeroes = {
            //"Flamita",
            //"Flatina",
            "Hero",
            //"Hero2",
            //"Pieberry",
            //"Ina",
            //"Leuna",
            //"Chigon"
    };
    
    /**
     * Set selected heroes array (used by MenuUI)
     */
    public void setSelectedHeroes(String[] heroes) {
        this.selectedHeroes = heroes;
        // Also update battle system if it's already configured
        if (battleSystem != null) {
            //System.out.println("ok");
            battleSystem.configureBattle(HIDE_TALENTS, selectedHeroes);
            battleSystem.removeAllCharacters();
        }
    }

    public int getGold_coin() {return gold_coin;}
    public void setGold_coin(int gold_coin) {testing.gold_coin = gold_coin;}
    public int getReturn_time() {return return_time;}
    public void setReturn_time(int return_time) {testing.return_time = return_time;}

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
    
    // Dialog system
    private DialogUI dialogUI;
    private DialogSystem dialogSystem;
    private DialogRegistrations dialogRegistrations;
    // Menu UI
    private MenuUI menuUI;

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

        // Initialize dialog system
        dialogUI = new DialogUI();
        dialogSystem = DialogSystem.getInstance();
        dialogSystem.setDialogUI(dialogUI);
        
        // Set callbacks to clear/hide UIs when dialog runs
        dialogSystem.setOnDialogStart(() -> {
            // Clear BattleUI and MapUI when dialog starts
            if (battleUI != null) {
                battleUI.clearAllBattleUI();
            }
            if (mapUI != null) {
                mapUI.hide();
                inMapMode=false;
            }
            menuUI.hide();
        });
        dialogSystem.setOnDialogEnd(() -> {
            if (mapUI != null) {
                mapUI.showSelectedPath();
            }
        });
        

        
        // Register all dialogs
        DialogRegistrations.registerAllDialogs();
        System.out.println("Dialog system initialized");

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
            showBossSelection();
            // Initialize dialog system with references to game systems
        }
        // Start menu music
        audioManager.playMenuMusic();
    }

    @Override
    protected void initInput() {
        // N key to exit map mode and start battle
        onKeyDown(KeyCode.N, () -> {
            resetBackToMenu();
        });

        onKeyDown(KeyCode.B, () -> {
                // Debug key - can be used for testing
            //System.out.println(battleSystem.getEnemySlot().getCharacter().toString());
            DialogRegistrations.registerRecruitDialogs();
            DialogRegistrations.showRandomDialogWithPurpose("recruitDialog");


        });

        // M key to return to map mode from battle
        onKeyDown(KeyCode.M, () -> {
            if(mapUI!=null){
                mapUI.hide();
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

        // D key to show random smallTalk dialog and print all registered dialogs
        onKeyDown(KeyCode.D, () -> {
            //System.out.println("D key pressed - showing random smallTalk dialog");
            DialogRegistrations.printAllRegisteredDialogs();
            //DialogRegistrations.showRandomDialogWithPurpose("smallTalk");
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

    }
    
    /**
     * Reset game back to menu - resets heroes, clears battle data, and restarts game
     * Can be called from MapUI when clicking on boss node or from N key
     */
    public void resetBackToMenu() {
        try {
            battleSystem.resetHeroes();
            setSelectedHeroes(new String[]{"Hero"});
            // Clear old enemy data to prevent showing dead enemies in next battle
            battleSystem.clearEnemyData();
            // Stop the battle loop properly
            battleSystem.stopBattleLoop();
            battleUI.clearAllBattleUI();
            mapUI.hide();
            menuUI.show();
            AudioManager.getInstance().playMenuMusic();
            startGame();
            //create new link

        } catch (Exception e) {
            System.out.println("Error in resetBackToMenu: " + e.getMessage());
            e.printStackTrace();
        }
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

        // Show victory dialog


        // Return to map mode - show the selected path instead of path selection
        inMapMode = true;
        if (gameMap.getSelectedPath() != null) {
            // Show the selected path with nodes
            mapUI.showSelectedPath();
        } else {
            // Fallback to path selection if no path is selected
            mapUI.showPathSelection();
        }
        // Show victory dialog
        DialogRegistrations.showBattleVictoryDialog(battleSystem);
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
        shopUI.setMapUI(mapUI);
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
        //mapUI.showPathSelection();
        
        // Initialize menu UI if not already done
        if (menuUI == null) {
            menuUI = new MenuUI();
            menuUI.setNeededUI(mapUI, battleSystem, battleUI);
            menuUI.setTestingInstance(this); // Pass testing instance for selectedHeroes access
        }

        mapUI.setBattleSystem(battleSystem);
        mapUI.setDialogSystem(dialogSystem,dialogRegistrations);
        mapUI.setTestingInstance(this);

        menuUI.setNeededUI(mapUI, battleSystem, battleUI);
        DialogRegistrations.initializeSystems(battleSystem, battleUI, mapUI, menuUI);

        if(!skip_opening) {
            DialogRegistrations.showDialogByTitle("intro");
            skip_opening = true;
        }
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
        shopUI.setMapUI(mapUI);
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