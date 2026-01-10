package org.example;

import audio.AudioManager;
import battle.BattleSystem;
import battle.BattleUI;
import characters.Observer;
import characters.SpecialTalents;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import items.StoryItemInventory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import map.GameMap;
import map.MapUI;
import ui.*;
import items.Inventory;
import items.ItemRegistry;
import shop.Shop;
import dialog.DialogSystem;
import dialog.DialogUI;
import dialog.DialogRegistrations;
import dialog.DialogLibrary;
import dialog.DialogEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    public static String status="";
    public static String[] storyItem={};
    public static boolean hasFlamitaBoss;
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
        //"Flamita",
        "Hero",
//        "Hero2",
//        "Pieberry",
//        "Ina",
        //"Leuna",
        //"Flatina",
        //"Chigon"
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
    public static String[] selectedHeroes = {
            //"Flamita",
            //"Flatina",
            "Hero",
            //"Hero2",
            //"Pieberry",
            //"Ina",
            //"Leuna",
            //"Chigon",
            //"Lucia"
    };
    
    /**
     * Set selected heroes array (used by MenuUI)
     */
    public static String getSelectedHeroes() {
        return selectedHeroes[0];
    }
    public void setSelectedHeroes(String[] heroes) {
        selectedHeroes = heroes;
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
    public static String getStatus() {return status;}
    public static void setStatus(String status) {testing.status = status;}
    public static void addStatus(String status) {testing.status = testing.status+" "+status;}
    // ===== BATTLE CONFIGURATION ======================================================================================
    // Battle system components
    private BattleSystem battleSystem;
    private BattleUI battleUI;

    // Map system
    private GameMap gameMap;
    private MapUI mapUI;
    public static boolean inMapMode = false;
    private static int currentFloor = 1; // Track current floor (1, 2, or 3)
    
    public static int getCurrentFloor() {return currentFloor;}
    
    public void setCurrentFloor(int floor) {
        currentFloor = floor;
    }

    // Audio system
    private AudioManager audioManager;
    private AudioSettingsUI audioSettingsUI;

    // Inventory system
    private Inventory inventory;
    private InventoryUI inventoryUI;
    public static StoryItemInventory storyItemInventory;
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
    
    // Frame rate limiter to ensure consistent game speed across different monitor refresh rates
    private long lastUpdateTime = 0;
    private static final long TARGET_UPDATE_INTERVAL_NS = 16_666_667L; // ~60 FPS (16.67ms per frame)
    
    @Override
    protected void onUpdate(double tpf) {
        long currentTime = System.nanoTime();
        
        // Limit update rate to ~60 FPS to ensure consistent game speed
        if (lastUpdateTime > 0) {
            long elapsed = currentTime - lastUpdateTime;
            if (elapsed < TARGET_UPDATE_INTERVAL_NS) {
                // Skip this update to maintain target frame rate
                return;
            }
        }
        
        lastUpdateTime = currentTime;
        super.onUpdate(tpf);
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
        // Set map mode checker for inventory UI
        inventoryUI.setMapModeChecker(() -> inMapMode);

        System.out.println("Inventory system initialized");
        
        // Initialize story item inventory
        storyItemInventory = new items.StoryItemInventory();
        // Load story items from testing.storyItem if any exist
        if (storyItem != null && storyItem.length > 0) {
            for (String itemId : storyItem) {
                storyItemInventory.addStoryItem(itemId);
            }
        }

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
        // Note: DialogUI now has a full-screen background that covers battle UI,
        // so we don't need to clear battle UI anymore - just hide other UIs
        dialogSystem.setOnDialogStart(() -> {
            // Hide all UIs when dialog starts (dialog background will cover battle UI)
            if (mapUI != null) {
                mapUI.hide();
                inMapMode=false;
            }
            if (menuUI != null) {
                menuUI.hide();
                menuUI.hideLibrary(); // Close LibraryUI if it's open
            }
            if (shopUI != null) {
                shopUI.hide();
            }
            if (inventoryUI != null) {
                inventoryUI.hide();
            }
            // Battle UI is now covered by dialog's full-screen background, no need to clear it
        });
        dialogSystem.setOnDialogEnd(() -> {
            if (mapUI != null) {
                mapUI.showSelectedPath();
            }
        });
        

        
        // Register all dialogs
        DialogRegistrations.registerAllDialogs();
        System.out.println("Dialog system initialized");
        
        // Initialize dialog box registry
        ui.DialogBoxRegistry.init();
        System.out.println("Dialog box registry initialized");

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
        
        // Connect BattleUI to AudioSettingsUI for AV value updates
        if (audioSettingsUI != null) {
            audioSettingsUI.setBattleUI(battleUI);
        }
        battleSystem.setOnBattleWon(() -> {
            // This callback is called when all enemies are defeated
            handleBattleVictory();
        });
        
        battleSystem.setOnBattleLost(() -> {
            // This callback is called when all heroes are defeated
            handleBattleDefeat();
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
            if(battleUI != null&&!battleSystem.hasHeroName("Litaru ")) {
                if(!dialogUI.isVisible()&&!inventoryUI.isVisible()) {
                    resetBackToMenu();
                }
            }
        });

        onKeyDown(KeyCode.B, () -> {
                // Debug key - can be used for testing
            battleSystem.getHeroSlot().setCurrentHp(10);
            //battleSystem.applyDamage(battleSystem.getHeroSlot(),300);
        });

        // M key to return to map mode from battle
        onKeyDown(KeyCode.M, () -> {
            try {
                System.out.println(battleSystem.getEnemySlot().getCurrentMp());
//                System.out.println(battleSystem.getHeroSlot().getCharacter().toString());
//                System.out.println(battleSystem.getEnemySlot().getCharacter().toString());
            }catch(Exception e) {

            }
        });

        // F1 key to toggle audio settings
        onKeyDown(KeyCode.W, () -> {
            audioSettingsUI.toggle();
        });

        // I key to toggle inventory (works in both map and battle mode)
        onKeyDown(KeyCode.I, () -> {
            System.out.println("=== I KEY PRESSED ===");
            System.out.println("Current mode: " + (inMapMode ? "MAP" : "BATTLE"));
            System.out.println("Inventory UI object: " + inventoryUI);

            if (inventoryUI != null&&!battleSystem.hasHeroName("Lucia")&&!battleSystem.hasHeroName("Azar")&&!battleSystem.hasHeroName("Litaru ")&&!menuUI.isVisible()&&!dialogSystem.isRunning()) {
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
//            System.out.println("S key pressed - toggling shop");
//            if (inMapMode && shopUI != null) {
//                shopUI.toggle();
//                System.out.println("Shop UI toggled. Visible: " + shopUI.isVisible());
//            } else if (!inMapMode) {
//                System.out.println("Shop can only be opened in map mode!");
//            } else {
//                System.out.println("Shop UI is null!");
//            }
        });

        // T key to test camp dialog search 100 times
        onKeyDown(KeyCode.T, () -> {
            // Register camp dialogs once
            DialogRegistrations.registerBasicCampDialog();
            
            // Run test 100 times to check percentage
            int count = 10000;
            int foundCount = 0;
            int notFoundCount = 0;
            Map<String, Integer> dialogCounts = new HashMap<>(); // Track how many times each dialog was found
            
            System.out.println("\n========== STARTING CAMP DIALOG TEST (100 runs) ==========");
            
            for (int z = 0; z < count; z++) {
                ArrayList<Observer.characterSlot> heroes = new ArrayList<>(Arrays.asList(battleSystem.getAllHeroes()));
                // Remove null heroes
                heroes.removeIf(h -> h == null);
                Collections.shuffle(heroes);

                // Limit to 3 heroes
                if (heroes.size() > 3) {
                    heroes = new ArrayList<>(heroes.subList(0, 3));
                }

                // Always use all heroes to search
                int numHeroesToSearch = heroes.size();

                if (numHeroesToSearch == 0 || heroes.isEmpty()) {
                    // No heroes, count as not found
                    notFoundCount++;
                    continue;
                }

                // Get hero names
                List<String> heroNames = new ArrayList<>();
                for (int i = 0; i < numHeroesToSearch && i < heroes.size(); i++) {
                    String heroName = heroes.get(i).getCharacter().getName();
                    heroNames.add(heroName);
                }

                // Search types: 1, 2, 3
                List<Integer> searchTypes = new ArrayList<>(Arrays.asList(1, 2, 3));
                Collections.shuffle(searchTypes);

                String foundDialogTitle = null;
                System.out.println(heroNames.toString());
                // Try each search type
                for (int searchType : searchTypes) {
                    List<String> matchingDialogs = new ArrayList<>();
                    boolean found = false;
                    
                    if (searchType == 1 && numHeroesToSearch >= 1) {
                        // Search for "camp1" + hero name (try each hero one by one)
                        DialogLibrary library = DialogLibrary.getInstance();
                        Map<String, DialogEntry> allDialogs = library.getAllDialogs();

                        for (String heroName : heroNames) {
                            String searchPattern = "camp1" + heroName;

                            for (String dialogId : allDialogs.keySet()) {
                                // Check if dialog title contains the pattern
                                if (dialogId.contains(searchPattern)) {
                                    String baseTitle = dialogId.substring(0, dialogId.lastIndexOf('_'));
                                    if (!matchingDialogs.contains(baseTitle)) {
                                        matchingDialogs.add(baseTitle);
                                        found = true;
                                    }
                                }
                            }

                            // If found with this hero, stop searching
                            if (found) {
                                break;
                            }
                        }
                    } else if (searchType == 2 && numHeroesToSearch >= 2) {
                        // Search for "camp2" + combinations of 2 hero names
                        // Check both orderings since dialog might be "camp2Hero1Hero2" or "camp2Hero2Hero1"
                        DialogLibrary library = DialogLibrary.getInstance();
                        Map<String, DialogEntry> allDialogs = library.getAllDialogs();
                        
                        for (int i = 0; i < heroNames.size(); i++) {
                            for (int j = i + 1; j < heroNames.size(); j++) {
                                String hero1 = heroNames.get(i);
                                String hero2 = heroNames.get(j);

                                for (String dialogId : allDialogs.keySet()) {
                                    // Check if dialog contains both hero names (order doesn't matter)
                                    if (dialogId.contains("camp2") && 
                                        dialogId.contains(hero1) && 
                                        dialogId.contains(hero2)) {
                                        String baseTitle = dialogId.substring(0, dialogId.lastIndexOf('_'));
                                        if (!matchingDialogs.contains(baseTitle)) {
                                            matchingDialogs.add(baseTitle);
                                            found = true;
                                        }
                                    }
                                }
                                // If found with this combination, stop searching
                                if (found) {
                                    break;
                                }
                            }
                            // If found with this hero, stop searching
                            if (found) {
                                break;
                            }
                        }
                    } else if (searchType == 3 && numHeroesToSearch >= 3) {
                        // Search for "camp3" + all 3 hero names
                        // Check if dialog contains all 3 hero names (order doesn't matter)
                        if (heroNames.size() >= 3) {
                            String hero1 = heroNames.get(0);
                            String hero2 = heroNames.get(1);
                            String hero3 = heroNames.get(2);
                            
                            DialogLibrary library = DialogLibrary.getInstance();
                            Map<String, DialogEntry> allDialogs = library.getAllDialogs();

                            for (String dialogId : allDialogs.keySet()) {
                                // Check if dialog contains "camp3" and all three hero names
                                if (dialogId.contains("camp3") && 
                                    dialogId.contains(hero1) && 
                                    dialogId.contains(hero2) && 
                                    dialogId.contains(hero3)) {
                                    matchingDialogs.add(dialogId.substring(0, dialogId.lastIndexOf('_')));
                                    found = true;
                                }
                            }
                        }
                    }

                    // If found dialogs, randomly pick one
                    if (!matchingDialogs.isEmpty()) {
                        Collections.shuffle(matchingDialogs);
                        foundDialogTitle = matchingDialogs.get(0);
                        break; // Found a dialog, stop searching
                    }
                }

                // Count results
                if (foundDialogTitle != null) {
                    foundCount++;
                    // Track individual dialog counts
                    dialogCounts.put(foundDialogTitle, dialogCounts.getOrDefault(foundDialogTitle, 0) + 1);
                    System.out.println("Run " + (z + 1) + ": Found dialog with title: " + foundDialogTitle);
                } else {
                    notFoundCount++;
                    System.out.println("Run " + (z + 1) + ": Found nothing");
                }
            }
            
            // Print statistics
            System.out.println("\n========== CAMP DIALOG TEST RESULTS ==========");
            System.out.println("Total runs: " + count);
            System.out.println("Found: " + foundCount + " (" + String.format("%.2f", foundCount * 100.0 / count) + "%)");
            System.out.println("Not found: " + notFoundCount + " (" + String.format("%.2f", notFoundCount * 100.0 / count) + "%)");
            System.out.println("\n--- Individual Dialog Counts ---");
            if (dialogCounts.isEmpty()) {
                System.out.println("No dialogs were found.");
            } else {
                // Sort by count (descending) for better readability
                List<Map.Entry<String, Integer>> sortedDialogs = new ArrayList<>(dialogCounts.entrySet());
                sortedDialogs.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                
                for (Map.Entry<String, Integer> entry : sortedDialogs) {
                    String dialogTitle = entry.getKey();
                    int timesFound = entry.getValue();
                    double percentage = (timesFound * 100.0 / count);
                    System.out.println("  " + dialogTitle + ": " + timesFound + " times (" + String.format("%.2f", percentage) + "%)");
                }
            }
            System.out.println("=============================================\n");
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
     * Initialize floor 2 with 1.5x enemy stats
     */
    public void initializeFloor2() {
        battleSystem.healToFullAllHeroes();
        currentFloor = 2;
        // Create new GameMap with 1.5x stat multiplier
        gameMap = new GameMap(1.25f);
        mapUI.setGameMap(gameMap);
        // Reset the selected path's currentNodeIndex to start from the beginning
        if (gameMap.getSelectedPath() != null) {
            gameMap.getSelectedPath().currentNodeIndex = 0;
        }
        System.out.println("Floor 2 initialized with 1.5x enemy stats");
    }
    
    /**
     * Initialize floor 3 with 1.5x enemy stats
     */
    public void initializeFloor3() {
        battleSystem.healToFullAllHeroes();
        currentFloor = 3;
        // Create new GameMap with 1.5x stat multiplier
        gameMap = new GameMap(1.5f);
        mapUI.setGameMap(gameMap);
        // Reset the selected path's currentNodeIndex to start from the beginning
        if (gameMap.getSelectedPath() != null) {
            gameMap.getSelectedPath().currentNodeIndex = 0;
        }
        System.out.println("Floor 3 initialized with 1.5x enemy stats");
    }
    
    /**
     * Reset game back to menu - resets heroes, clears battle data, and restarts game
     * Can be called from MapUI when clicking on boss node or from N key
     */
    public void resetBackToMenu() {
        try {
            //reseting event
            setStatus(status.replaceAll("camp0",""));
            //restart inventory
            inventory.addGold(-inventory.getGold()+300);
            inventory.removeAllItems();
            giveStartingItems();

            currentFloor = 1; // Reset floor to 1 when returning to menu
            //reset heroes
            battleUI.removeAllHeroesLine();
            battleSystem.resetHeroes();

            if("Azar".equals(battleSystem.getHeroSlot().getCharacter().getName())||"Litaru ".equals(battleSystem.getHeroSlot().getCharacter().getName())) {
                battleSystem.removeAllCharacters();
                setSelectedHeroes(new String[]{"Hero"});
                battleSystem.configureBattle(battleSystem.getHideTalents(), new String[]{"Hero"});
            }else{
                audioManager.playMenuMusic();
                battleSystem.removeAllCharacters();
                setSelectedHeroes(new String[]{selectedHeroes[0]});
                battleSystem.configureBattle(battleSystem.getHideTalents(),new String[]{selectedHeroes[0]});
            }
            // Clear old enemy data to prevent showing dead enemies in next battle

            battleSystem.clearEnemyData();
            battleSystem.setPartyMp(0);
            // Stop the battle loop properly
            battleSystem.stopBattleLoop();
            battleUI.clearAllBattleUI();
            mapUI.hide();
            menuUI.show();
            
            // Auto-save to slot 6 when returning to menu
            if (menuUI != null) {
                menuUI.autoSave();
            }

            startGame();
            //create new link

        } catch (Exception e) {
            System.out.println("Error in resetBackToMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBattleVictory() {

        //Gain gold
        if(mapUI.getGameMap().getSelectedPath() != null) {
            int index = mapUI.getGameMap().getSelectedPath().currentNodeIndex;
            double floorScale = 0.75+currentFloor*0.25;
            for (String enemy : battleSystem.defeatEnemies) {
                int gold = (int)(100 + 25 * index*floorScale);
                inventory.addGold(gold);
            }
        }

        battleSystem.removeAllBuffsDebuffsFromHeroes();
        // Clear all battle UI elements
        battleUI.clearAllBattleUI();

        // Clear old enemy data to prevent showing dead enemies in next battle
        //battleSystem.clearEnemyData();

        // Stop the battle loop properly
        battleSystem.stopBattleLoop();

        // Show victory dialog


        // Return to map mode - show the selected path instead of path selection
        inMapMode = true;
        if (gameMap.getSelectedPath() != null&&!battleSystem.hasHeroName("Litaru ")) {
            // Show the selected path with nodes
            mapUI.showSelectedPath();
        }
        // Show victory dialog

        DialogRegistrations.showBattleVictoryDialog(battleSystem);
    }
    
    private void handleBattleDefeat() {
        // Stop the battle loop properly
        battleSystem.stopBattleLoop();
        
        // Clear all battle UI elements
        battleUI.clearAllBattleUI();
        
        // Clear old enemy data
        battleSystem.clearEnemyData();
        
        // Reset back to menu
        resetBackToMenu();
        
        // Show defeat dialog
        DialogRegistrations.showBattleLoseDialog();
    }

    private void giveStartingItems() {
        // Give player some starting consumables
        inventory.addItem(ItemRegistry.getItem("health_potion_small"), 3);
        inventory.addItem(ItemRegistry.getItem("mana_potion_small"), 2);
        inventory.addItem(ItemRegistry.getItem("elixir_small"), 1);

        // Give player some starting equipment
        inventory.addItem(ItemRegistry.getItem("iron_sword"), 1);
        inventory.addItem(ItemRegistry.getItem("leather_armor"), 1);

        //Give all items for testing (each item 3 times)
        //inventory.giveAllItems();

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
//        if (bossSelectionUI.isOufuuSelected()) {
//            gameMap.addOufuuBossFight("forest");
//        }
//        if (bossSelectionUI.isFlamitaSelected()) {
//            gameMap.addFlamitaBossFight("forest");
//        }
//        if (bossSelectionUI.isMabelSelected()) {
//            gameMap.addMabelBossFight("forest");
//        }
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
            menuUI.setStoryItemInventory(storyItemInventory); // Pass story item inventory
        }

        mapUI.setBattleSystem(battleSystem);
        mapUI.setDialogSystem(dialogSystem,dialogRegistrations);
        mapUI.setTestingInstance(this);

        menuUI.setNeededUI(mapUI, battleSystem, battleUI);
        DialogRegistrations.initializeSystems(battleSystem, battleUI, mapUI, menuUI, shopUI, inventoryUI,this);

        if(!skip_opening) {
            DialogRegistrations.showDialogByTitle("intro","menu");
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