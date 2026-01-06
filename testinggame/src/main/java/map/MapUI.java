package map;

import audio.AudioManager;
import dialog.DialogRegistrations;
import dialog.DialogSystem;
import dialog.DialogUI;
import dialog.DialogLibrary;
import dialog.DialogEntry;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import com.almasb.fxgl.dsl.FXGL;
import characters.Observer;
import event.MapEvent;
import battle.BattleSystem;
import org.example.testing;
import shop.Shop;
import ui.ShopUI;
import items.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MapUI {
    private GameMap gameMap;
    private BattleSystem battleSystem;
    private List<Rectangle> pathButtons;
    private List<Text> pathTexts;
    private List<Circle> nodeCircles;
    private List<Line> pathLines;
    private boolean pathSelected;
    private Rectangle mapBackground;
    private List<Text> nodeLabels;
    private List<Observer.characterSlot> currentBattleEnemies;
    private Runnable onBattleModeRequested;
    private AudioManager audioManager;

    // Shop system
    private Shop shop;
    private ShopUI shopUI;
    private Inventory inventory;

    //Dialog system
    private DialogSystem dialogSystem;
    private DialogRegistrations dialogRegistrations;
    
    // Reference to testing instance for resetBackToMenu
    private org.example.testing testingInstance;
    
    // Current event node for dialog system
    private MapNode currentEventNode;

    public MapUI(GameMap gameMap) {
        this.gameMap = gameMap;
        this.pathButtons = new ArrayList<>();
        this.pathTexts = new ArrayList<>();
        this.nodeCircles = new ArrayList<>();
        this.pathLines = new ArrayList<>();
        this.nodeLabels = new ArrayList<>();
        this.pathSelected = false;
        this.currentBattleEnemies = new ArrayList<>();
        this.audioManager = AudioManager.getInstance();
    }
    
    public void setBattleSystem(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;
    }
    public void setDialogSystem(DialogSystem dialogSystem,DialogRegistrations dialogRegistrations) {
        this.dialogSystem = dialogSystem;
        this.dialogRegistrations = dialogRegistrations;
    }
    
    public void setOnBattleModeRequested(Runnable callback) {
        this.onBattleModeRequested = callback;
    }

    public void setShopSystem(Shop shop, ShopUI shopUI, Inventory inventory) {
        this.shop = shop;
        this.shopUI = shopUI;
        this.inventory = inventory;
    }
    
    /**
     * Set the testing instance reference for resetBackToMenu functionality
     */
    public void setTestingInstance(org.example.testing testingInstance) {
        this.testingInstance = testingInstance;
    }
    
    /**
     * Get the game map reference
     */
    public GameMap getGameMap() {
        return gameMap;
    }
    
    /**
     * Set the game map reference (used when creating floor 2)
     */
    public void setGameMap(GameMap newGameMap) {
        this.gameMap = newGameMap;
    }

    public void showPathSelection() {
        clearUI();
        createPathSelectionUI();
        testingInstance.inMapMode = true;
        // Start map exploration music
        if(battleSystem!=null&&!battleSystem.hasHeroName("Litaru ")) {
            audioManager.playMapMusic();
        }
    }

    public void showSelectedPath() {
        testing.inMapMode = true;
        if (gameMap.getSelectedPath() != null) {
            clearUI();
            createPathUI();
            pathSelected = true;
        }else{
            gameMap.setSelectedPath(gameMap.getPaths().getFirst());
            clearUI();
            createPathUI();
            pathSelected = true;
        }
    }
    private Text title2;

    private void createPathSelectionUI() {
        // Background
        mapBackground = new Rectangle(800, 600, Color.LIGHTBLUE);
        mapBackground.setTranslateX(0);
        mapBackground.setTranslateY(0);
        FXGL.getGameScene().addUINode(mapBackground);

        // Title
        title2 = new Text("Chọn đường đi của bạn");
        title2.setFont(new Font(24));
        title2.setFill(Color.DARKBLUE);
        title2.setTranslateX(300);
        title2.setTranslateY(50);
        FXGL.getGameScene().addUINode(title2);


        // Path selection buttons
        List<MapPath> paths = gameMap.getPaths();
        for (int i = 0; i < paths.size(); i++) {
            MapPath path = paths.get(i);
            createPathButton(path, i);
        }

    }

    private void createPathButton(MapPath path, int index) {
        double buttonWidth = 200;
        double buttonHeight = 100;
        double startX = 100 + index * 220;
        double startY = 200;

        // Button background
        Rectangle button = new Rectangle(buttonWidth, buttonHeight);
        button.setTranslateX(startX);
        button.setTranslateY(startY);
        
        // Set color based on path type
        switch (path.getPathType()) {
            case FOREST:
                button.setFill(Color.DARKGREEN);
                break;
            case MOUNTAIN:
                button.setFill(Color.GRAY);
                break;
            case VILLAGE:
                button.setFill(Color.SANDYBROWN);
                break;
        }
        
        button.setStroke(Color.BLACK);
        button.setStrokeWidth(2);

        // Button text
        Text buttonText = new Text(path.getPathType().getDisplayName());
        buttonText.setFont(new Font(16));
        buttonText.setFill(Color.WHITE);
        buttonText.setTextAlignment(TextAlignment.CENTER);
        buttonText.setTranslateX(startX + buttonWidth/2 - buttonText.getBoundsInLocal().getWidth()/2);
        buttonText.setTranslateY(startY + buttonHeight/2);

        // Description text
        Text descText = new Text(path.getPathType().getDescription());
        descText.setFont(new Font(12));
        descText.setFill(Color.BLACK);
        descText.setWrappingWidth(buttonWidth - 10);
        descText.setTextAlignment(TextAlignment.CENTER);
        descText.setTranslateX(startX + 5);
        descText.setTranslateY(startY + buttonHeight + 20);

        // Click handler
        button.setOnMouseClicked(e -> {
            audioManager.playButtonClick();
            gameMap.setSelectedPath(path);
            showSelectedPath();
        });

        // Hover effects
        button.setOnMouseEntered(e -> {
            button.setStrokeWidth(4);
            button.setStroke(Color.YELLOW);
        });

        button.setOnMouseExited(e -> {
            button.setStrokeWidth(2);
            button.setStroke(Color.BLACK);
        });

        pathButtons.add(button);
        pathTexts.add(buttonText);
        pathTexts.add(descText);

        FXGL.getGameScene().addUINode(button);
        FXGL.getGameScene().addUINode(buttonText);
        FXGL.getGameScene().addUINode(descText);
    }
    private Text title ;
    private Text progress ;
    private void createPathUI() {
        // Background
        mapBackground = new Rectangle(800, 600, Color.LIGHTCYAN);
        mapBackground.setTranslateX(0);
        mapBackground.setTranslateY(0);
        FXGL.getGameScene().addUINode(mapBackground);

        MapPath selectedPath = gameMap.getSelectedPath();
        if (selectedPath == null) return;

        // Title
        title = new Text(selectedPath.getPathType().getDisplayName());
        title.setFont(new Font(20));
        title.setFill(Color.DARKBLUE);
        title.setTranslateX(50);
        title.setTranslateY(30);
        FXGL.getGameScene().addUINode(title);

        // Progress indicator
        progress = new Text("Tiến độ: " + selectedPath.getProgress() + "/" + selectedPath.getTotalNodes());
        progress.setFont(new Font(14));
        progress.setFill(Color.BLACK);
        progress.setTranslateX(50);
        progress.setTranslateY(50);
        FXGL.getGameScene().addUINode(progress);

        // Draw path nodes
        List<MapNode> nodes = selectedPath.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            MapNode node = nodes.get(i);
            createNodeUI(node, i, selectedPath.getCurrentNodeIndex());
            
            // Draw line to next node
            if (i < nodes.size() - 1) {
                MapNode nextNode = nodes.get(i + 1);
                createPathLine(node, nextNode);
            }
        }

        // Draw line to boss if path is completed
        if (selectedPath.isCompleted()) {
            MapNode lastNode = nodes.get(nodes.size() - 1);
            createPathLine(lastNode, gameMap.getBossNode());
            createNodeUI(gameMap.getBossNode(), -1, selectedPath.getCurrentNodeIndex());
        }

        // Back button
        //createBackButton();
    }

    private void createNodeUI(MapNode node, int nodeIndex, int currentIndex) {
        double x = node.getPositionX();
        double y = node.getPositionY();

        // Node circle
        Circle nodeCircle = new Circle(15);
        nodeCircle.setTranslateX(x);
        nodeCircle.setTranslateY(y);

        // Color based on status and type
        if (nodeIndex < currentIndex || node.isCompleted()) {
            nodeCircle.setFill(Color.GREEN); // Completed
        } else if (nodeIndex == currentIndex) {
            nodeCircle.setFill(Color.YELLOW); // Current
        } else {
            nodeCircle.setFill(Color.LIGHTGRAY); // Not reached
        }

        // Border color based on node type
        switch (node.getType()) {
            case START:
                nodeCircle.setStroke(Color.BLUE);
                break;
            case BATTLE:
                nodeCircle.setStroke(Color.RED);
                break;
            case EVENT:
                nodeCircle.setStroke(Color.PURPLE);
                break;
            case SHOP:
                nodeCircle.setStroke(Color.GOLD);
                break;
            case REST:
                nodeCircle.setStroke(Color.CYAN);
                break;
            case RECRUIT:
                nodeCircle.setStroke(Color.ORANGE);
                break;
            case BOSS:
                nodeCircle.setStroke(Color.DARKRED);
                nodeCircle.setRadius(20);
                break;
        }
        nodeCircle.setStrokeWidth(3);

        // Node label
        Text nodeLabel = new Text(node.getName());
        nodeLabel.setFont(new Font(10));
        nodeLabel.setFill(Color.BLACK);
        nodeLabel.setTranslateX(x - nodeLabel.getBoundsInLocal().getWidth()/2);
        nodeLabel.setTranslateY(y + 25);

        // Click handler for current node
        if (nodeIndex == currentIndex && !node.isCompleted()) {
            nodeCircle.setOnMouseClicked(e -> {
                audioManager.playButtonClick();
                activateNode(node);
            });
            
            // Hover effect
            nodeCircle.setOnMouseEntered(e -> {
                nodeCircle.setStrokeWidth(5);
            });
            
            nodeCircle.setOnMouseExited(e -> {
                nodeCircle.setStrokeWidth(3);
            });
        }

        nodeCircles.add(nodeCircle);
        nodeLabels.add(nodeLabel);
        FXGL.getGameScene().addUINode(nodeCircle);
        FXGL.getGameScene().addUINode(nodeLabel);
    }

    private void createPathLine(MapNode from, MapNode to) {
        Line line = new Line();
        line.setStartX(from.getPositionX());
        line.setStartY(from.getPositionY());
        line.setEndX(to.getPositionX());
        line.setEndY(to.getPositionY());
        line.setStroke(Color.BROWN);
        line.setStrokeWidth(3);

        pathLines.add(line);
        FXGL.getGameScene().addUINode(line);
    }
    private Text backText;
    private  Rectangle backButton;
    private void createBackButton() {
        backButton = new Rectangle(100, 40, Color.LIGHTGRAY);
        backButton.setTranslateX(50);
        backButton.setTranslateY(550);
        backButton.setStroke(Color.BLACK);
        backButton.setStrokeWidth(2);

        backText = new Text("Quay lại");
        backText.setFont(new Font(14));
        backText.setFill(Color.BLACK);
        backText.setTranslateX(70);
        backText.setTranslateY(575);
        backText.setMouseTransparent(true);


        backButton.setOnMouseClicked(e -> {
            audioManager.playButtonClick();
            gameMap.setSelectedPath(null);
            showPathSelection();
        });
        FXGL.getGameScene().addUINode(backButton);
        FXGL.getGameScene().addUINode(backText);


    }

    private void activateNode(MapNode node) {

        node.activate();
        // Move to next node if current is completed
        if (node.isCompleted()) {
            gameMap.getSelectedPath().moveToNextNode();
        }

        // Handle different node types
        switch (node.getType()) {
            case START:
                DialogRegistrations.showStartDungeonDialog();

                break;
            case EVENT:
                // Show random event dialog
                DialogRegistrations.registerBasicEventDialog();
                DialogRegistrations.showRandomDialogWithPurpose("event", "current");
                break;
            case BATTLE:
                // Start battle with enemies in this node
                System.out.println("Starting battle at: " + node.getName());
                if (battleSystem != null && !node.getEnemies().isEmpty()) {
                    if(node.getId().equals("flamita_boss")){
                        DialogRegistrations.registerFlamitaBossFight();
                        // After the Flamita intro dialog ends, start the battle,
                        // then restore the normal dialog-end behavior (showSelectedPath).
                        DialogSystem dialogSystem = DialogSystem.getInstance();
                        dialogSystem.setOnDialogEnd(() -> {
                            // Start battle after intro dialog
                            setupBattleWithMapEnemies(node);
                            switchToBattleMode();
                            // Restore default: when future dialogs end (like victory dialog),
                            // the map path is shown/updated.
                            dialogSystem.setOnDialogEnd(this::showSelectedPath);
                        });
                        DialogRegistrations.showDialogByTitle("FlamitaBossFightBegin",null);
                    }else {
                        // Set up battle with map enemies
                        setupBattleWithMapEnemies(node);
                        // Automatically switch to battle mode
                        switchToBattleMode();
                    }
                }
                break;
            case RECRUIT:
                DialogRegistrations.registerRecruitDialogs();
                DialogRegistrations.showRandomDialogWithPurpose("recruitDialog","");
                break;
            case SHOP:
                System.out.println("Opened shop at: " + node.getName());
                // Open shop UI
                if (shopUI != null) {
                    shop.refreshShop(); // Generate new items
                    shopUI.show();
                } else {
                    // Fallback: Apply shop benefits to current active heroes
                    if (battleSystem != null) {
                        Observer.characterSlot[] heroes = battleSystem.getAllHeroes();
                        for (Observer.characterSlot hero : heroes) {
                            if (hero != null) {
                                hero.setCurrentHp(Math.min(hero.getCharacter().getHp(), hero.getCurrentHp() + 250));
                                hero.setCurrentMp(Math.min(hero.getCharacter().getMp(), hero.getCurrentMp() + 150));
                            }
                        }
                        System.out.println("Bought healing items! +250 HP and +150 MP!");
                    }
                }
                break;
            case REST:
                DialogRegistrations.registerBasicCampDialog();
                if(testingInstance.getReturn_time()>=5&&!testing.status.contains("camp0")&&!battleSystem.hasHeroName("Azar")){
                    DialogRegistrations.showDialogByTitle("camp0","current");
                    audioManager.playMusic("camp0.mp3",true);
                    return;
                }
                //Heal to full
                for(Observer.characterSlot hero : battleSystem.getAllHeroes()){
                    if (hero != null) {
                        hero.setCurrentHp(hero.getCharacter().getHp());
                        hero.setCurrentMp(hero.getCharacter().getMp());
                    }
                }

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
                    // No heroes, show default dialog
                    DialogRegistrations.showDialogByTitle("campNoDialogFound", "current");
                    break;
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

                                // Check if dialog contains both hero names (order doesn't matter)
                                for (String dialogId : allDialogs.keySet()) {
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

                // Show found dialog or default
                if (foundDialogTitle != null) {
                    String[] titles = {"campBegin",foundDialogTitle};
                    DialogRegistrations.showManyDialogByTitle(titles, "current");
                    System.out.println("Found dialog with title: " + foundDialogTitle);
                } else {
                    String[] titles = {"campBegin","campNoDialogFound"};
                    DialogRegistrations.showManyDialogByTitle(titles, "current");
                    System.out.println("Found nothing");
                }
                break;
            case BOSS:
//                System.out.println("Boss battle begins!");
//                // Start boss battle
//                // Start battle with enemies in this node
//                System.out.println("Starting battle at: " + node.getName());
//                if (battleSystem != null && !node.getEnemies().isEmpty()) {
//                    // Set up battle with map enemies
//                    setupBattleWithMapEnemies(node);
//
//                    // Automatically switch to battle mode
//                    switchToBattleMode();
//                }

                // Check if we're on floor 1, if so, create floor 2 instead of resetting to menu
                if (testingInstance != null&&!battleSystem.hasHeroName("Azar")) {
                    if (testing.getCurrentFloor() == 1&&testingInstance.getReturn_time()>=5) {
                        // Create floor 2 with 1.5x enemy stats
                        testingInstance.initializeFloor2();
                        // Refresh the map UI to show the new floor
                        showSelectedPath();
                    } else {
                        // Floor 2 boss defeated, reset to menu
                        testingInstance.setReturn_time(testingInstance.getReturn_time()+1);
                        testingInstance.resetBackToMenu();
                    }
                } else {
                    System.out.println("Error: testingInstance is null, cannot reset to menu");
                }
                break;
        }
        

        

    }

    private void clearUI() {
        // Remove all UI elements
        if (mapBackground != null) {
            FXGL.getGameScene().removeUINode(mapBackground);
        }
        
        for (Rectangle button : pathButtons) {
            FXGL.getGameScene().removeUINode(button);
        }
        
        for (Text text : pathTexts) {
            FXGL.getGameScene().removeUINode(text);
        }
        
        for (Circle circle : nodeCircles) {
            FXGL.getGameScene().removeUINode(circle);
        }
        
        for (Line line : pathLines) {
            FXGL.getGameScene().removeUINode(line);
        }
        
        for (Text text : nodeLabels) {
            FXGL.getGameScene().removeUINode(text);
        }
        
        if(title!=null) {
            FXGL.getGameScene().removeUINode(title);
        }
        if(progress!=null) {
            FXGL.getGameScene().removeUINode(progress);
        }
        if(title2!=null) {
            FXGL.getGameScene().removeUINode(title2);
        }
        if(backText!=null) {
            FXGL.getGameScene().removeUINode(backText);
        }
        if(backButton!=null) {
            FXGL.getGameScene().removeUINode(backButton);
        }

        pathButtons.clear();
        pathTexts.clear();
        nodeCircles.clear();
        pathLines.clear();
        nodeLabels.clear();
    }

    public boolean isPathSelected() {
        return pathSelected;
    }

    public void hide() {
        System.out.println("Hiding map UI...");
        clearUI();
        pathSelected = false;
        System.out.println("Map UI hidden!");
    }
    
    private void setupBattleWithMapEnemies(MapNode node) {
        // Get the enemies from the map node
        List<Observer.characterSlot> mapEnemies = node.getEnemies();
        
        if (mapEnemies.isEmpty()) {
            System.out.println("No enemies in this node!");
            return;
        }
        
        // Store the current battle enemies
        currentBattleEnemies.clear();
        currentBattleEnemies.addAll(mapEnemies);
        
        // Set up battle system with map enemies
        Observer.characterSlot enemy1 = mapEnemies.get(0);
        Observer.characterSlot enemy2 = mapEnemies.size() > 1 ? mapEnemies.get(1) : null;
        Observer.characterSlot enemy3 = mapEnemies.size() > 2 ? mapEnemies.get(2) : null;
        
        System.out.println("Setting up battle with: " + enemy1.getCharacter().getName());
        if (enemy2 != null) {
            System.out.println("And: " + enemy2.getCharacter().getName());
        }
        if (enemy3 != null) {
            System.out.println("And: " + enemy3.getCharacter().getName());
        }
        
        // Update battle system with map enemies
        if (battleSystem != null) {
            battleSystem.clearEnemyData();
            battleSystem.setMapEnemies(enemy1, enemy2, enemy3);
        }
    }
    
    public List<Observer.characterSlot> getCurrentBattleEnemies() {
        return currentBattleEnemies;
    }
    
    private void switchToBattleMode() {
        System.out.println("Switching to battle mode from MapUI...");
        // Don't hide here - let enterBattleMode handle it
        // hide();
        
        // Notify the main game to switch to battle mode
        if (onBattleModeRequested != null) {
            System.out.println("Calling battle mode callback...");
            onBattleModeRequested.run();
        } else {
            System.out.println("No battle mode callback set!");
        }
    }
    
    /**
     * Public method to switch to battle mode (for external use)
     */
    public void requestBattleMode() {
        switchToBattleMode();
    }
    
    /**
     * Get the current event node (for dialog system)
     */
    public MapNode getCurrentEventNode() {
        return currentEventNode;
    }
}
