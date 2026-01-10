package ui;

import abilities.Ability;
import battle.BattleSystem;
import battle.BattleUI;
import characters.Characters;
import characters.Observer;
import dialog.DialogRegistrations;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import com.almasb.fxgl.dsl.FXGL;
import map.MapUI;
import org.example.testing;
import save.SaveData;
import save.SaveManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu UI that appears after intro dialog
 * Contains: Go to Dungeon button, Save/Load buttons
 */
public class MenuUI {
    private Group mainContainer;
    private Rectangle dungeonButton;
    private Text dungeonButtonText;
    private Rectangle saveButton;
    private Text saveButtonText;
    private Rectangle loadButton;
    private Text loadButtonText;
    private Rectangle libraryButton;
    private Text libraryButtonText;
    private boolean isVisible = false;
    
    private MapUI mapUI;
    private BattleSystem battleSystem;
    private BattleUI battleUI;
    private org.example.testing testingInstance; // Reference to testing instance
    private LibraryUI libraryUI;
    
    // City UI
    private Group cityContainer;
    private Rectangle cityButton;
    private Text cityButtonText;
    private Rectangle cityBackground;
    private Rectangle goToOfficeButton;
    private Text goToOfficeButtonText;
    private Rectangle goOutsideCityButton;
    private Text goOutsideCityButtonText;
    private Rectangle callLitaruButton;
    private Text callLitaruButtonText;
    private boolean isCityVisible = false;
    
    // Warehouse UI
    private WarehouseUI warehouseUI;
    private Rectangle warehouseButton;
    private Text warehouseButtonText;
    private items.StoryItemInventory storyItemInventory;
    
    // Call Party UI
    private Group callPartyContainer;
    private Rectangle callPartyButton;
    private Text callPartyButtonText;
    private Text goldCoinText;
    private Text returnTimeText;
    private Rectangle callPartyBackground;
    private Group characterListContainer;
    private Rectangle[] characterButtons;
    private Text[] characterButtonTexts;
    private Group optionsContainer;
    private Rectangle optionWalkButton;
    private Text optionWalkText;
    private Rectangle optionMakePartyButton;
    private Text optionMakePartyText;
    private Rectangle optionCancelButton;
    private Text optionCancelText;
    private String selectedCharacter = null;
    private boolean isCallPartyVisible = false;
    
    // Dimensions
    private static final double DUNGEON_BUTTON_SIZE = 200;
    private static final double DUNGEON_BUTTON_X = 600; // Lower right
    private static final double DUNGEON_BUTTON_Y = 200; // Moved up so it doesn't collapse with dialog
    private static final double LIBRARY_BUTTON_WIDTH = 200;
    private static final double LIBRARY_BUTTON_HEIGHT = 60;
    private static final double LIBRARY_BUTTON_X = DUNGEON_BUTTON_X;
    private static final double LIBRARY_BUTTON_Y = DUNGEON_BUTTON_Y + DUNGEON_BUTTON_SIZE + 20;
    private static final double CITY_BUTTON_WIDTH = 200;
    private static final double CITY_BUTTON_HEIGHT = 60;
    private static final double CITY_BUTTON_X = LIBRARY_BUTTON_X;
    private static final double CITY_BUTTON_Y = LIBRARY_BUTTON_Y + LIBRARY_BUTTON_HEIGHT + 20;
    
    // City UI dimensions
    private static final double CITY_BACKGROUND_WIDTH = 250;
    private static final double CITY_BACKGROUND_X = CITY_BUTTON_X - CITY_BACKGROUND_WIDTH - 20; // To the left of button
    private static final double CITY_BACKGROUND_Y = 10; // Start from top (spans full height)
    private static final double OFFICE_BUTTON_WIDTH = 200;
    private static final double OFFICE_BUTTON_HEIGHT = 50;
    private static final double OFFICE_BUTTON_X = CITY_BACKGROUND_X + 25;
    private static final double OFFICE_BUTTON_Y = 50; // Position from top
    
    private static final double SMALL_BUTTON_WIDTH = 100;
    private static final double SMALL_BUTTON_HEIGHT = 40;
    private static final double SAVE_BUTTON_X = 700; // Top right
    private static final double SAVE_BUTTON_Y = 10;
    private static final double LOAD_BUTTON_X = 700;
    private static final double LOAD_BUTTON_Y = 60;
    
    // Save/Load selection UI
    private Group saveLoadContainer;
    private Rectangle saveLoadBackground;
    private Rectangle cancelButton;
    private Text cancelButtonText;
    private Text modeTitleText; // "Save a file" or "Load a file"
    private Rectangle[] saveSlots;
    private Text[] saveSlotTexts;
    private boolean isSaveMode = false;
    private boolean isLoadMode = false;
    
    // Save/Load UI dimensions
    private static final double SAVE_LOAD_BACKGROUND_WIDTH = 500;
    private static final double SAVE_LOAD_BACKGROUND_HEIGHT = 400; // Increased to accommodate 2 rows of slots
    private static final double SAVE_LOAD_BACKGROUND_X = 150;
    private static final double SAVE_LOAD_BACKGROUND_Y = 100;
    private static final double SAVE_SLOT_WIDTH = 120;
    private static final double SAVE_SLOT_HEIGHT = 80;
    private static final double SAVE_SLOT_SPACING = 20;
    private static final double SAVE_SLOT_START_X = 200;
    private static final double SAVE_SLOT_START_Y = 200;
    private static final double CANCEL_BUTTON_SIZE = 40;
    private static final double CANCEL_BUTTON_X = 630;
    private static final double CANCEL_BUTTON_Y = 110;
    
    // Call Party UI dimensions
    private static final double CALL_PARTY_BUTTON_WIDTH = 120;
    private static final double CALL_PARTY_BUTTON_HEIGHT = 40;
    private static final double CALL_PARTY_BUTTON_X = 10;
    private static final double CALL_PARTY_BUTTON_Y = 10;
    
    // Warehouse UI dimensions
    private static final double WAREHOUSE_BUTTON_WIDTH = 120;
    private static final double WAREHOUSE_BUTTON_HEIGHT = 40;
    private static final double WAREHOUSE_BUTTON_X = CALL_PARTY_BUTTON_X + CALL_PARTY_BUTTON_WIDTH + 10; // Next to Call Party button
    private static final double WAREHOUSE_BUTTON_Y = 10;
    private static final double GOLD_DISPLAY_X = WAREHOUSE_BUTTON_X + WAREHOUSE_BUTTON_WIDTH + 20; // To the right of Warehouse button
    private static final double GOLD_DISPLAY_Y = WAREHOUSE_BUTTON_Y + 5;
    private static final double RETURN_TIME_DISPLAY_X = GOLD_DISPLAY_X;
    private static final double RETURN_TIME_DISPLAY_Y = WAREHOUSE_BUTTON_Y + 25;
    private static final double CALL_PARTY_BACKGROUND_WIDTH = 300;
    private static final double CALL_PARTY_BACKGROUND_HEIGHT = 500;
    private static final double CALL_PARTY_BACKGROUND_X = 10;
    private static final double CALL_PARTY_BACKGROUND_Y = 60;
    private static final double CHARACTER_BUTTON_WIDTH = 280;
    private static final double CHARACTER_BUTTON_HEIGHT = 50;
    private static final double CHARACTER_BUTTON_SPACING = 5;
    private static final double CHARACTER_LIST_START_Y = 10;
    private static final double OPTIONS_BACKGROUND_X = 330;
    private static final double OPTIONS_BACKGROUND_Y = 60;
    private static final double OPTIONS_BACKGROUND_WIDTH = 200;
    private static final double OPTIONS_BACKGROUND_HEIGHT = 200;
    private static final double OPTION_BUTTON_WIDTH = 180;
    private static final double OPTION_BUTTON_HEIGHT = 40;
    private static final double OPTION_BUTTON_SPACING = 10;
    private static final double OPTION_START_Y = 20;
    
    public MenuUI() {
        initializeUI();
    }
    
    public void setNeededUI(MapUI mapUI, BattleSystem battleSystem, BattleUI battleUI) {
        this.mapUI = mapUI;
        this.battleSystem = battleSystem;
        this.battleUI = battleUI;
    }
    
    /**
     * Set story item inventory for warehouse UI
     */
    public void setStoryItemInventory(items.StoryItemInventory storyItemInventory) {
        this.storyItemInventory = storyItemInventory;
        if (warehouseUI == null && storyItemInventory != null) {
            warehouseUI = new WarehouseUI(storyItemInventory);
        }
    }
    
    /**
     * Set testing instance reference (for accessing selectedHeroes)
     */
    public void setTestingInstance(org.example.testing instance) {
        this.testingInstance = instance;
    }

    
    private void initializeUI() {
        mainContainer = new Group();
        libraryUI = new LibraryUI();
        // When LibraryUI closes via its own Back button, also restore Call Party UI
        libraryUI.setOnClose(() -> {
            libraryUI.hide();
            restoreCallPartyButton();
        });
        initializeSaveLoadUI();
        initializeCallPartyUI();
        initializeCityUI();
        initializeWarehouseUI();
        
        // Create "Go to Dungeon" button (bigger square at lower right, moved up)
        dungeonButton = new Rectangle(DUNGEON_BUTTON_SIZE, DUNGEON_BUTTON_SIZE, Color.rgb(100, 150, 200));
        dungeonButton.setStroke(Color.BLACK);
        dungeonButton.setStrokeWidth(2);
        dungeonButton.setTranslateX(DUNGEON_BUTTON_X);
        dungeonButton.setTranslateY(DUNGEON_BUTTON_Y);
        
        dungeonButtonText = new Text("Go to\nDungeon");

        dungeonButtonText.setFont(new Font(20));
        dungeonButtonText.setFill(Color.WHITE);
        dungeonButtonText.setTextAlignment(TextAlignment.CENTER);
        dungeonButtonText.setTranslateX(DUNGEON_BUTTON_X + DUNGEON_BUTTON_SIZE / 2 - 50);
        dungeonButtonText.setTranslateY(DUNGEON_BUTTON_Y + DUNGEON_BUTTON_SIZE / 2);
        dungeonButtonText.setWrappingWidth(100);
        dungeonButtonText.setMouseTransparent(true); // Make text non-interactive
        
        dungeonButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                goToDungeon();
            }
        });
        
        // Hover effect for dungeon button
        dungeonButton.setOnMouseEntered(e -> dungeonButton.setFill(Color.rgb(120, 170, 220)));
        dungeonButton.setOnMouseExited(e -> dungeonButton.setFill(Color.rgb(100, 150, 200)));
        
        // Create Save button (small rectangle at top right)
        saveButton = new Rectangle(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, Color.rgb(100, 200, 100));
        saveButton.setStroke(Color.BLACK);
        saveButton.setStrokeWidth(1);
        saveButton.setTranslateX(SAVE_BUTTON_X);
        saveButton.setTranslateY(SAVE_BUTTON_Y);
        
        saveButtonText = new Text("Save");
        saveButtonText.setFont(new Font(14));
        saveButtonText.setFill(Color.WHITE);
        saveButtonText.setTranslateX(SAVE_BUTTON_X + SMALL_BUTTON_WIDTH / 2 - 20);
        saveButtonText.setTranslateY(SAVE_BUTTON_Y + SMALL_BUTTON_HEIGHT / 2 + 5);
        saveButtonText.setMouseTransparent(true); // Make text non-interactive
        
        saveButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                saveGame();
            }
        });
        
        saveButton.setOnMouseEntered(e -> saveButton.setFill(Color.rgb(120, 220, 120)));
        saveButton.setOnMouseExited(e -> saveButton.setFill(Color.rgb(100, 200, 100)));
        
        // Create Load button (small rectangle at top right, below save)
        loadButton = new Rectangle(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, Color.rgb(200, 150, 100));
        loadButton.setStroke(Color.BLACK);
        loadButton.setStrokeWidth(1);
        loadButton.setTranslateX(LOAD_BUTTON_X);
        loadButton.setTranslateY(LOAD_BUTTON_Y);
        
        loadButtonText = new Text("Load");
        loadButtonText.setFont(new Font(14));
        loadButtonText.setFill(Color.WHITE);
        loadButtonText.setTranslateX(LOAD_BUTTON_X + SMALL_BUTTON_WIDTH / 2 - 20);
        loadButtonText.setTranslateY(LOAD_BUTTON_Y + SMALL_BUTTON_HEIGHT / 2 + 5);
        loadButtonText.setMouseTransparent(true); // Make text non-interactive
        
        loadButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                loadGame();
            }
        });
        
        loadButton.setOnMouseEntered(e -> loadButton.setFill(Color.rgb(220, 170, 120)));
        loadButton.setOnMouseExited(e -> loadButton.setFill(Color.rgb(200, 150, 100)));
        
        // Create Library button below "Go to Dungeon"
        libraryButton = new Rectangle(LIBRARY_BUTTON_WIDTH, LIBRARY_BUTTON_HEIGHT, Color.rgb(160, 160, 220));
        libraryButton.setStroke(Color.BLACK);
        libraryButton.setStrokeWidth(2);
        libraryButton.setTranslateX(LIBRARY_BUTTON_X);
        libraryButton.setTranslateY(LIBRARY_BUTTON_Y);

        libraryButtonText = new Text("Library");
        libraryButtonText.setFont(new Font(18));
        libraryButtonText.setFill(Color.WHITE);
        libraryButtonText.setTextAlignment(TextAlignment.CENTER);
        libraryButtonText.setTranslateX(LIBRARY_BUTTON_X + LIBRARY_BUTTON_WIDTH / 2 - 35);
        libraryButtonText.setTranslateY(LIBRARY_BUTTON_Y + LIBRARY_BUTTON_HEIGHT / 2 + 5);
        libraryButtonText.setMouseTransparent(true);

        libraryButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleLibrary();
            }
        });
        libraryButton.setOnMouseEntered(e -> libraryButton.setFill(Color.rgb(180, 180, 240)));
        libraryButton.setOnMouseExited(e -> libraryButton.setFill(Color.rgb(160, 160, 220)));

        mainContainer.getChildren().addAll(
                cityContainer,
                cityButton,
                cityButtonText,
                dungeonButton,
                dungeonButtonText,
                saveButton,
                saveButtonText,
                loadButton,
                loadButtonText,
                libraryButton,
                libraryButtonText,
                libraryUI.getRoot(),
                warehouseButton,
                warehouseButtonText
        );
    }
    
    /**
     * Initialize City UI
     */
    private void initializeCityUI() {
        cityContainer = new Group();
        
        // City button (right bottom of library button)
        cityButton = new Rectangle(CITY_BUTTON_WIDTH, CITY_BUTTON_HEIGHT, Color.rgb(180, 140, 100));
        cityButton.setStroke(Color.BLACK);
        cityButton.setStrokeWidth(2);
        cityButton.setTranslateX(CITY_BUTTON_X);
        cityButton.setTranslateY(CITY_BUTTON_Y);

        cityButtonText = new Text("Go to City");
        cityButtonText.setFont(new Font(18));
        cityButtonText.setFill(Color.WHITE);
        cityButtonText.setTextAlignment(TextAlignment.CENTER);
        cityButtonText.setTranslateX(CITY_BUTTON_X + CITY_BUTTON_WIDTH / 2 - 50);
        cityButtonText.setTranslateY(CITY_BUTTON_Y + CITY_BUTTON_HEIGHT / 2 + 5);
        cityButtonText.setMouseTransparent(true);

        cityButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleCity();
            }
        });
        cityButton.setOnMouseEntered(e -> cityButton.setFill(Color.rgb(200, 160, 120)));
        cityButton.setOnMouseExited(e -> cityButton.setFill(Color.rgb(180, 140, 100)));
        
        // City background (appears to the left of the button, spans full height)
        double appHeight = FXGL.getAppHeight();
        cityBackground = new Rectangle(CITY_BACKGROUND_WIDTH, appHeight, Color.rgb(60, 60, 60, 0.9));
        cityBackground.setStroke(Color.BLACK);
        cityBackground.setStrokeWidth(2);
        cityBackground.setTranslateX(CITY_BACKGROUND_X);
        cityBackground.setTranslateY(CITY_BACKGROUND_Y);
        
        // Go to Office button
        goToOfficeButton = new Rectangle(OFFICE_BUTTON_WIDTH, OFFICE_BUTTON_HEIGHT, Color.rgb(100, 150, 200));
        goToOfficeButton.setStroke(Color.BLACK);
        goToOfficeButton.setStrokeWidth(1);
        goToOfficeButton.setTranslateX(OFFICE_BUTTON_X);
        goToOfficeButton.setTranslateY(OFFICE_BUTTON_Y);
        
        goToOfficeButtonText = new Text("Go to Office");
        goToOfficeButtonText.setFont(new Font(16));
        goToOfficeButtonText.setFill(Color.WHITE);
        goToOfficeButtonText.setTranslateX(OFFICE_BUTTON_X + OFFICE_BUTTON_WIDTH / 2 - 60);
        goToOfficeButtonText.setTranslateY(OFFICE_BUTTON_Y + OFFICE_BUTTON_HEIGHT / 2 + 5);
        goToOfficeButtonText.setMouseTransparent(true);
        
        goToOfficeButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                DialogRegistrations.showOfficeDialog();
                hideCity();
            }
        });
        
        goToOfficeButton.setOnMouseEntered(e -> goToOfficeButton.setFill(Color.rgb(120, 170, 220)));
        goToOfficeButton.setOnMouseExited(e -> goToOfficeButton.setFill(Color.rgb(100, 150, 200)));
        
        // Go outside the city button (only shows if status contains "officeDialogLitaru")
        goOutsideCityButton = new Rectangle(OFFICE_BUTTON_WIDTH, OFFICE_BUTTON_HEIGHT, Color.rgb(100, 150, 200));
        goOutsideCityButton.setStroke(Color.BLACK);
        goOutsideCityButton.setStrokeWidth(1);
        goOutsideCityButton.setTranslateX(OFFICE_BUTTON_X);
        goOutsideCityButton.setTranslateY(OFFICE_BUTTON_Y + OFFICE_BUTTON_HEIGHT + 20); // Below office button
        
        goOutsideCityButtonText = new Text("Go outside the city");
        goOutsideCityButtonText.setFont(new Font(16));
        goOutsideCityButtonText.setFill(Color.WHITE);
        goOutsideCityButtonText.setTranslateX(OFFICE_BUTTON_X + OFFICE_BUTTON_WIDTH / 2 - 85);
        goOutsideCityButtonText.setTranslateY(OFFICE_BUTTON_Y + OFFICE_BUTTON_HEIGHT + 20 + OFFICE_BUTTON_HEIGHT / 2 + 5);
        goOutsideCityButtonText.setMouseTransparent(true);
        
        goOutsideCityButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                DialogRegistrations.showLitaruStartDialog();
                hideCity();
            }
        });
        
        goOutsideCityButton.setOnMouseEntered(e -> goOutsideCityButton.setFill(Color.rgb(120, 170, 220)));
        goOutsideCityButton.setOnMouseExited(e -> goOutsideCityButton.setFill(Color.rgb(100, 150, 200)));
        
        // Call Litaru button (only shows if storyItemInventory contains necro_sword, radiant_core, Phoenix_feather)
        callLitaruButton = new Rectangle(OFFICE_BUTTON_WIDTH, OFFICE_BUTTON_HEIGHT, Color.rgb(100, 150, 200));
        callLitaruButton.setStroke(Color.BLACK);
        callLitaruButton.setStrokeWidth(1);
        callLitaruButton.setTranslateX(OFFICE_BUTTON_X);
        callLitaruButton.setTranslateY(OFFICE_BUTTON_Y + OFFICE_BUTTON_HEIGHT * 2 + 20); // Below outside city button
        
        callLitaruButtonText = new Text("Call Litaru");
        callLitaruButtonText.setFont(new Font(16));
        callLitaruButtonText.setFill(Color.WHITE);
        callLitaruButtonText.setTranslateX(OFFICE_BUTTON_X + OFFICE_BUTTON_WIDTH / 2 - 60);
        callLitaruButtonText.setTranslateY(OFFICE_BUTTON_Y + OFFICE_BUTTON_HEIGHT * 2 + 20 + OFFICE_BUTTON_HEIGHT / 2 + 5);
        callLitaruButtonText.setMouseTransparent(true);
        
        callLitaruButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                DialogRegistrations.showLitaruEndDialog();
                hideCity();
            }
        });
        
        callLitaruButton.setOnMouseEntered(e -> callLitaruButton.setFill(Color.rgb(120, 170, 220)));
        callLitaruButton.setOnMouseExited(e -> callLitaruButton.setFill(Color.rgb(100, 150, 200)));
        
        cityContainer.getChildren().addAll(
            cityBackground,
            goToOfficeButton,
            goToOfficeButtonText,
            goOutsideCityButton,
            goOutsideCityButtonText,
            callLitaruButton,
            callLitaruButtonText
        );
        
        // Initially hidden
        cityContainer.setVisible(false);
        goOutsideCityButton.setVisible(false);
        goOutsideCityButtonText.setVisible(false);
        callLitaruButton.setVisible(false);
        callLitaruButtonText.setVisible(false);
    }
    
    /**
     * Toggle City UI visibility
     */
    private void toggleCity() {
        if (isCityVisible) {
            hideCity();
        } else {
            showCity();
        }
    }
    
    /**
     * Show City UI
     */
    private void showCity() {
        isCityVisible = true;
        cityContainer.setVisible(true);
        
        // Check if status contains "officeDialogLitaru" to show/hide the outside city button
        boolean showOutsideCity = testing.status != null && testing.status.contains("officeDialogLitaru") && !testing.status.contains("LitaruStart");
        goOutsideCityButton.setVisible(showOutsideCity);
        goOutsideCityButtonText.setVisible(showOutsideCity);
        
        // Check if storyItemInventory contains all required items for Call Litaru
        boolean showCallLitaru = storyItemInventory != null && 
                                  storyItemInventory.hasStoryItem("necro_sword") &&
                                  storyItemInventory.hasStoryItem("radiant_core") &&
                                  storyItemInventory.hasStoryItem("Phoenix_feather");
        callLitaruButton.setVisible(showCallLitaru);
        callLitaruButtonText.setVisible(showCallLitaru);
        
        // Update button positions to remove gaps
        updateCityButtonPositions(showOutsideCity, showCallLitaru);
    }
    
    /**
     * Update city button positions so they have no gaps between them
     */
    private void updateCityButtonPositions(boolean showOutsideCity, boolean showCallLitaru) {
        double currentY = OFFICE_BUTTON_Y;
        
        // Go to Office button is always visible at the top
        goToOfficeButton.setTranslateY(currentY);
        goToOfficeButtonText.setTranslateY(currentY + OFFICE_BUTTON_HEIGHT / 2 + 5);
        currentY += OFFICE_BUTTON_HEIGHT+20; // Move down by button height (no gap)
        
        // Go outside the city button
        if (showOutsideCity) {
            goOutsideCityButton.setTranslateY(currentY);
            goOutsideCityButtonText.setTranslateY(currentY + OFFICE_BUTTON_HEIGHT / 2 + 5);
            currentY += OFFICE_BUTTON_HEIGHT+20; // Move down by button height (no gap)
        }
        
        // Call Litaru button
        if (showCallLitaru) {
            callLitaruButton.setTranslateY(currentY);
            callLitaruButtonText.setTranslateY(currentY + OFFICE_BUTTON_HEIGHT / 2 + 5);
        }
    }
    
    /**
     * Hide City UI
     */
    private void hideCity() {
        isCityVisible = false;
        cityContainer.setVisible(false);
    }

    private void toggleLibrary() {
        if (libraryUI == null) {
            return;
        }
        if (libraryUI.isVisible()) {
            libraryUI.hide();
            // Re-enable Call Party UI if it was disabled
            restoreCallPartyButton();
        } else {
            // When opening library, hide Call Party panel and city panel, and disable Call Party toggle
            if (isCallPartyVisible) {
                hideCallParty();
            }
            if (isCityVisible) {
                hideCity();
            }
            disableCallPartyButtonForLibrary();
            libraryUI.show();
        }
    }
    
    /**
     * Hide the LibraryUI (called when dialog starts)
     */
    public void hideLibrary() {
        if (libraryUI != null && libraryUI.isVisible()) {
            libraryUI.hide();
            restoreCallPartyButton();
        }
    }
    
    /**
     * Initialize Call Party UI
     */
    private void initializeCallPartyUI() {
        callPartyContainer = new Group();
        
        // Call Party button (top left)
        callPartyButton = new Rectangle(CALL_PARTY_BUTTON_WIDTH, CALL_PARTY_BUTTON_HEIGHT, Color.rgb(150, 100, 200));
        callPartyButton.setStroke(Color.BLACK);
        callPartyButton.setStrokeWidth(1);
        callPartyButton.setTranslateX(CALL_PARTY_BUTTON_X);
        callPartyButton.setTranslateY(CALL_PARTY_BUTTON_Y);
        callPartyButton.setVisible(true); // Ensure button is visible
        
        callPartyButtonText = new Text("Call Party");
        callPartyButtonText.setFont(new Font(14));
        callPartyButtonText.setFill(Color.WHITE);
        callPartyButtonText.setTranslateX(CALL_PARTY_BUTTON_X + CALL_PARTY_BUTTON_WIDTH / 2 - 45);
        callPartyButtonText.setTranslateY(CALL_PARTY_BUTTON_Y + CALL_PARTY_BUTTON_HEIGHT / 2 + 5);
        callPartyButtonText.setMouseTransparent(true);
        callPartyButtonText.setVisible(true); // Ensure text is visible
        
        callPartyButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleCallParty();
            }
        });
        
        callPartyButton.setOnMouseEntered(e -> callPartyButton.setFill(Color.rgb(170, 120, 220)));
        callPartyButton.setOnMouseExited(e -> callPartyButton.setFill(Color.rgb(150, 100, 200)));
        
        // Gold coin display (to the right of Call Party button)
        goldCoinText = new Text("Gold: 0");
        goldCoinText.setFont(new Font(14));
        goldCoinText.setFill(Color.BLACK);
        goldCoinText.setTranslateX(GOLD_DISPLAY_X);
        goldCoinText.setTranslateY(GOLD_DISPLAY_Y);
        goldCoinText.setMouseTransparent(true);
        
        // Return time display (below gold coin)
        returnTimeText = new Text("Return Time: 0");
        returnTimeText.setFont(new Font(14));
        returnTimeText.setFill(Color.BLACK);
        returnTimeText.setTranslateX(RETURN_TIME_DISPLAY_X);
        returnTimeText.setTranslateY(RETURN_TIME_DISPLAY_Y);
        returnTimeText.setMouseTransparent(true);
        
        // Background for character list (scrollable area)
        callPartyBackground = new Rectangle(CALL_PARTY_BACKGROUND_WIDTH, CALL_PARTY_BACKGROUND_HEIGHT, Color.rgb(60, 60, 60, 0.9));
        callPartyBackground.setStroke(Color.BLACK);
        callPartyBackground.setStrokeWidth(2);
        callPartyBackground.setTranslateX(CALL_PARTY_BACKGROUND_X);
        callPartyBackground.setTranslateY(CALL_PARTY_BACKGROUND_Y);
        
        // Character list container (for scrolling)
        characterListContainer = new Group();
        characterListContainer.setTranslateX(CALL_PARTY_BACKGROUND_X + 10);
        characterListContainer.setTranslateY(CALL_PARTY_BACKGROUND_Y + 10);
        
        // Options container (appears when character is selected)
        optionsContainer = new Group();
        optionsContainer.setVisible(false);
        
        Rectangle optionsBackground = new Rectangle(OPTIONS_BACKGROUND_WIDTH, OPTIONS_BACKGROUND_HEIGHT, Color.rgb(60, 60, 60, 0.9));
        optionsBackground.setStroke(Color.BLACK);
        optionsBackground.setStrokeWidth(2);
        optionsBackground.setTranslateX(OPTIONS_BACKGROUND_X);
        optionsBackground.setTranslateY(OPTIONS_BACKGROUND_Y);
        
        // "Have a walk" option
        optionWalkButton = new Rectangle(OPTION_BUTTON_WIDTH, OPTION_BUTTON_HEIGHT, Color.rgb(100, 150, 200));
        optionWalkButton.setStroke(Color.BLACK);
        optionWalkButton.setStrokeWidth(1);
        optionWalkButton.setTranslateX(OPTIONS_BACKGROUND_X + 10);
        optionWalkButton.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y);
        
        optionWalkText = new Text("Have a walk");
        optionWalkText.setFont(new Font(14));
        optionWalkText.setFill(Color.WHITE);
        optionWalkText.setTranslateX(OPTIONS_BACKGROUND_X + 20);
        optionWalkText.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y + OPTION_BUTTON_HEIGHT / 2 + 5);
        optionWalkText.setMouseTransparent(true);
        
        optionWalkButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                haveAWalk();
            }
        });
        
        optionWalkButton.setOnMouseEntered(e -> optionWalkButton.setFill(Color.rgb(120, 170, 220)));
        optionWalkButton.setOnMouseExited(e -> optionWalkButton.setFill(Color.rgb(100, 150, 200)));
        
        // "Make party member" option
        optionMakePartyButton = new Rectangle(OPTION_BUTTON_WIDTH, OPTION_BUTTON_HEIGHT, Color.rgb(100, 200, 100));
        optionMakePartyButton.setStroke(Color.BLACK);
        optionMakePartyButton.setStrokeWidth(1);
        optionMakePartyButton.setTranslateX(OPTIONS_BACKGROUND_X + 10);
        optionMakePartyButton.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y + OPTION_BUTTON_HEIGHT + OPTION_BUTTON_SPACING);
        
        optionMakePartyText = new Text("Make party member");
        optionMakePartyText.setFont(new Font(14));
        optionMakePartyText.setFill(Color.WHITE);
        optionMakePartyText.setTranslateX(OPTIONS_BACKGROUND_X + 20);
        optionMakePartyText.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y + OPTION_BUTTON_HEIGHT + OPTION_BUTTON_SPACING + OPTION_BUTTON_HEIGHT / 2 + 5);
        optionMakePartyText.setMouseTransparent(true);
        
        optionMakePartyButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                makePartyMember();
            }
        });
        
        optionMakePartyButton.setOnMouseEntered(e -> optionMakePartyButton.setFill(Color.rgb(120, 220, 120)));
        optionMakePartyButton.setOnMouseExited(e -> optionMakePartyButton.setFill(Color.rgb(100, 200, 100)));
        
        // Cancel option
        optionCancelButton = new Rectangle(OPTION_BUTTON_WIDTH, OPTION_BUTTON_HEIGHT, Color.rgb(200, 100, 100));
        optionCancelButton.setStroke(Color.BLACK);
        optionCancelButton.setStrokeWidth(1);
        optionCancelButton.setTranslateX(OPTIONS_BACKGROUND_X + 10);
        optionCancelButton.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y + (OPTION_BUTTON_HEIGHT + OPTION_BUTTON_SPACING) * 2);
        
        optionCancelText = new Text("Cancel");
        optionCancelText.setFont(new Font(14));
        optionCancelText.setFill(Color.WHITE);
        optionCancelText.setTranslateX(OPTIONS_BACKGROUND_X + 20);
        optionCancelText.setTranslateY(OPTIONS_BACKGROUND_Y + OPTION_START_Y + (OPTION_BUTTON_HEIGHT + OPTION_BUTTON_SPACING) * 2 + OPTION_BUTTON_HEIGHT / 2 + 5);
        optionCancelText.setMouseTransparent(true);
        
        optionCancelButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                cancelCharacterSelection();
            }
        });
        
        optionCancelButton.setOnMouseEntered(e -> optionCancelButton.setFill(Color.rgb(220, 120, 120)));
        optionCancelButton.setOnMouseExited(e -> optionCancelButton.setFill(Color.rgb(200, 100, 100)));
        
        optionsContainer.getChildren().addAll(
            optionsBackground,
            optionWalkButton,
            optionWalkText,
            optionMakePartyButton,
            optionMakePartyText,
            optionCancelButton,
            optionCancelText
        );
        
        callPartyContainer.getChildren().addAll(
            callPartyButton,
            callPartyButtonText,
            goldCoinText,
            returnTimeText,
            callPartyBackground,
            characterListContainer,
            optionsContainer
        );
        
        // Container is visible, list/options hidden initially
        callPartyContainer.setVisible(true);
        callPartyBackground.setVisible(false);
        characterListContainer.setVisible(false);
        optionsContainer.setVisible(false);
        // Button itself is always visible
        updateCharacterList();
    }
    
    /**
     * Initialize Warehouse UI button
     */
    private void initializeWarehouseUI() {
        // Warehouse button (next to Call Party button)
        warehouseButton = new Rectangle(WAREHOUSE_BUTTON_WIDTH, WAREHOUSE_BUTTON_HEIGHT, Color.rgb(150, 150, 100));
        warehouseButton.setStroke(Color.BLACK);
        warehouseButton.setStrokeWidth(1);
        warehouseButton.setTranslateX(WAREHOUSE_BUTTON_X);
        warehouseButton.setTranslateY(WAREHOUSE_BUTTON_Y);
        
        warehouseButtonText = new Text("Azar\nWarehouse");
        warehouseButtonText.setFont(new Font(12));
        warehouseButtonText.setFill(Color.WHITE);
        warehouseButtonText.setTextAlignment(TextAlignment.CENTER);
        warehouseButtonText.setTranslateX(WAREHOUSE_BUTTON_X + WAREHOUSE_BUTTON_WIDTH / 2 - 50);
        warehouseButtonText.setTranslateY(WAREHOUSE_BUTTON_Y + WAREHOUSE_BUTTON_HEIGHT / 2 - 5);
        warehouseButtonText.setWrappingWidth(100);
        warehouseButtonText.setMouseTransparent(true);
        
        warehouseButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                toggleWarehouse();
            }
        });
        
        warehouseButton.setOnMouseEntered(e -> warehouseButton.setFill(Color.rgb(170, 170, 120)));
        warehouseButton.setOnMouseExited(e -> warehouseButton.setFill(Color.rgb(150, 150, 100)));
    }
    
    /**
     * Toggle Warehouse UI visibility
     */
    private void toggleWarehouse() {
        if (warehouseUI == null) {
            return;
        }
        if (warehouseUI.isVisible()) {
            warehouseUI.hide();
        } else {
            // Hide other panels when opening warehouse
            if (isCallPartyVisible) {
                hideCallParty();
            }
            if (isCityVisible) {
                hideCity();
            }
            warehouseUI.show();
        }
    }

    private void disableCallPartyButtonForLibrary() {
        if (callPartyButton != null) {
            callPartyButton.setDisable(true);
            callPartyButton.setVisible(false);
        }
        if (callPartyButtonText != null) {
            callPartyButtonText.setVisible(false);
        }
        if (goldCoinText != null) {
            goldCoinText.setVisible(false);
        }
        if (returnTimeText != null) {
            returnTimeText.setVisible(false);
        }
        // Hide warehouse button when library is opened
        if (warehouseButton != null) {
            warehouseButton.setDisable(true);
            warehouseButton.setVisible(false);
        }
        if (warehouseButtonText != null) {
            warehouseButtonText.setVisible(false);
        }
    }

    private void restoreCallPartyButton() {
        if (callPartyButton != null) {
            callPartyButton.setDisable(false);
            callPartyButton.setVisible(true);
        }
        if (callPartyButtonText != null) {
            callPartyButtonText.setVisible(true);
        }
        if (goldCoinText != null) {
            goldCoinText.setVisible(true);
        }
        if (returnTimeText != null) {
            returnTimeText.setVisible(true);
        }
        // Restore warehouse button when library is closed
        if (warehouseButton != null) {
            warehouseButton.setDisable(false);
            warehouseButton.setVisible(true);
        }
        if (warehouseButtonText != null) {
            warehouseButtonText.setVisible(true);
        }
    }
    
    /**
     * Update the character list with available heroes
     */
    private void updateCharacterList() {
        // Clear existing buttons
        if (characterButtons != null) {
            for (Rectangle button : characterButtons) {
                characterListContainer.getChildren().remove(button);
            }
        }
        if (characterButtonTexts != null) {
            for (Text text : characterButtonTexts) {
                characterListContainer.getChildren().remove(text);
            }
        }
        
        // Get available heroes
        String[] availableHeroes = org.example.testing.getAvailableHeroes();
        if (availableHeroes == null || availableHeroes.length == 0) {
            characterButtons = new Rectangle[0];
            characterButtonTexts = new Text[0];
            return;
        }
        
        characterButtons = new Rectangle[availableHeroes.length];
        characterButtonTexts = new Text[availableHeroes.length];
        
        for (int i = 0; i < availableHeroes.length; i++) {
            final String characterName = availableHeroes[i];
            double buttonY = CHARACTER_LIST_START_Y + i * (CHARACTER_BUTTON_HEIGHT + CHARACTER_BUTTON_SPACING);
            
            // Character button
            Rectangle button = new Rectangle(CHARACTER_BUTTON_WIDTH, CHARACTER_BUTTON_HEIGHT, Color.rgb(80, 120, 160));
            button.setStroke(Color.BLACK);
            button.setStrokeWidth(1);
            button.setTranslateX(0);
            button.setTranslateY(buttonY);
            
            // Character text
            Text text = new Text(characterName);
            text.setFont(new Font(14));
            text.setFill(Color.WHITE);
            text.setTranslateX(10);
            text.setTranslateY(buttonY + CHARACTER_BUTTON_HEIGHT / 2 + 5);
            text.setMouseTransparent(true);
            
            // Click handler
            button.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    selectCharacter(characterName);
                }
            });
            
            // Hover effect
            button.setOnMouseEntered(e -> button.setFill(Color.rgb(100, 140, 180)));
            button.setOnMouseExited(e -> button.setFill(Color.rgb(80, 120, 160)));
            
            characterButtons[i] = button;
            characterButtonTexts[i] = text;
            characterListContainer.getChildren().addAll(button, text);
        }
    }
    
    /**
     * Toggle Call Party UI visibility
     */
    private void toggleCallParty() {
        if (isCallPartyVisible) {
            hideCallParty();
        } else {
            showCallParty();
        }
    }
    
    /**
     * Show Call Party UI
     */
    private void showCallParty() {
        isCallPartyVisible = true;
        callPartyBackground.setVisible(true);
        characterListContainer.setVisible(true);
        updateCharacterList(); // Refresh list in case heroes changed
        // Hide options when showing
        optionsContainer.setVisible(false);
        selectedCharacter = null;
    }
    
    /**
     * Hide Call Party UI
     */
    private void hideCallParty() {
        isCallPartyVisible = false;
        callPartyBackground.setVisible(false);
        characterListContainer.setVisible(false);
        optionsContainer.setVisible(false);
        selectedCharacter = null;
    }
    
    /**
     * Select a character and show options
     */
    private void selectCharacter(String characterName) {
        selectedCharacter = characterName;
        optionsContainer.setVisible(true);
    }
    
    /**
     * Cancel character selection
     */
    private void cancelCharacterSelection() {
        selectedCharacter = null;
        optionsContainer.setVisible(false);
    }
    
    /**
     * Handle "Have a walk" option
     */
    private void haveAWalk() {
        if (selectedCharacter != null) {
            DialogRegistrations.registerAllDialogs();
            // Start dialog with format "HeronameHaveAWalk_Start"
            String dialogTitle = selectedCharacter + "HaveAWalk_Start";
            System.out.println("Starting walk dialog: " + dialogTitle);
            
            // Show the dialog
            DialogRegistrations.showDialogByTitle(dialogTitle,"menu");
            
            hideCallParty();
        }
    }
    
    /**
     * Handle "Make party member" option
     */
    private void makePartyMember() {
        if (selectedCharacter != null) {
            // Set selectedHeroes to only this character
            String[] newSelectedHeroes = {selectedCharacter};
            
            // Update through testing instance if available
            if (testingInstance != null) {
                testingInstance.setSelectedHeroes(newSelectedHeroes);
            //} else {
                // Fallback: update battleSystem directly
                if (battleSystem != null) {
                    battleSystem.configureBattle(false, newSelectedHeroes); // Using false as default for hideTalents
                }
            }
            
            System.out.println("Made " + selectedCharacter + " the only party member!");
            hideCallParty();
        }
    }
    
    /**
     * Initialize save/load selection UI
     */
    private void initializeSaveLoadUI() {
        saveLoadContainer = new Group();
        
        // Background (semi-transparent overlay)
        saveLoadBackground = new Rectangle(SAVE_LOAD_BACKGROUND_WIDTH, SAVE_LOAD_BACKGROUND_HEIGHT, Color.rgb(50, 50, 50, 0.8));
        saveLoadBackground.setStroke(Color.BLACK);
        saveLoadBackground.setStrokeWidth(2);
        saveLoadBackground.setTranslateX(SAVE_LOAD_BACKGROUND_X);
        saveLoadBackground.setTranslateY(SAVE_LOAD_BACKGROUND_Y);
        
        // Cancel button (top right of save/load box)
        cancelButton = new Rectangle(CANCEL_BUTTON_SIZE, CANCEL_BUTTON_SIZE, Color.rgb(200, 50, 50));
        cancelButton.setStroke(Color.BLACK);
        cancelButton.setStrokeWidth(1);
        cancelButton.setTranslateX(CANCEL_BUTTON_X);
        cancelButton.setTranslateY(CANCEL_BUTTON_Y);
        
        cancelButtonText = new Text("X");
        cancelButtonText.setFont(new Font(20));
        cancelButtonText.setFill(Color.WHITE);
        cancelButtonText.setTranslateX(CANCEL_BUTTON_X + CANCEL_BUTTON_SIZE / 2 - 8);
        cancelButtonText.setTranslateY(CANCEL_BUTTON_Y + CANCEL_BUTTON_SIZE / 2 + 7);
        cancelButtonText.setMouseTransparent(true); // Make text non-interactive
        
        cancelButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                cancelSaveLoad();
            }
        });
        
        cancelButton.setOnMouseEntered(e -> cancelButton.setFill(Color.rgb(220, 70, 70)));
        cancelButton.setOnMouseExited(e -> cancelButton.setFill(Color.rgb(200, 50, 50)));
        
        // Mode title text (will show "Save a file" or "Load a file")
        modeTitleText = new Text("Save a file");
        modeTitleText.setFont(new Font(24));
        modeTitleText.setFill(Color.WHITE);
        modeTitleText.setTranslateX(SAVE_LOAD_BACKGROUND_X + SAVE_LOAD_BACKGROUND_WIDTH / 2 - 80);
        modeTitleText.setTranslateY(SAVE_LOAD_BACKGROUND_Y + 50);
        modeTitleText.setMouseTransparent(true); // Make text non-interactive
        
        // Initialize save slots (6 slots: 1-5 manual, 6 auto-save)
        saveSlots = new Rectangle[6];
        saveSlotTexts = new Text[6];
        
        for (int i = 0; i < 6; i++) {
            final int slotIndex = i + 1; // save1, save2, ..., save6
            // Layout: 2 rows of 3 slots each
            int row = i / 3; // 0 for first row (slots 0-2), 1 for second row (slots 3-5)
            int col = i % 3; // Column within row (0, 1, or 2)
            double slotX = SAVE_SLOT_START_X + col * (SAVE_SLOT_WIDTH + SAVE_SLOT_SPACING);
            double slotY = SAVE_SLOT_START_Y + row * (SAVE_SLOT_HEIGHT + SAVE_SLOT_SPACING);
            
            // Slot button
            Rectangle slot = new Rectangle(SAVE_SLOT_WIDTH, SAVE_SLOT_HEIGHT, Color.rgb(100, 150, 200));
            slot.setStroke(Color.BLACK);
            slot.setStrokeWidth(2);
            slot.setTranslateX(slotX);
            slot.setTranslateY(slotY);
            
            // Check if save exists
            boolean saveExists = SaveManager.saveExists("save" + slotIndex);
            if (saveExists) {
                slot.setFill(Color.rgb(100, 200, 100)); // Green if save exists
            }
            
            // Slot text - special label for slot 6 (Auto Save)
            String slotLabel = (slotIndex == 6) ? "Auto Save" : "Save " + slotIndex;
            Text slotText = new Text(slotLabel);
            slotText.setFont(new Font(16));
            slotText.setFill(Color.WHITE);
            slotText.setTranslateX(slotX + SAVE_SLOT_WIDTH / 2 - 50);
            slotText.setTranslateY(slotY + SAVE_SLOT_HEIGHT / 2 + 5);
            slotText.setMouseTransparent(true); // Make text non-interactive
            
            // Click handler - slot 6 is not clickable in save mode (auto-save only)
            final boolean isAutoSaveSlot = (slotIndex == 6);
            slot.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (isSaveMode && !isAutoSaveSlot) {
                        // Manual save mode: slots 1-5 only
                        saveToSlot(slotIndex);
                    } else if (isLoadMode) {
                        // Load mode: all slots (1-6) can be loaded
                        loadFromSlot(slotIndex);
                    }
                }
            });
            
            // Hover effect - disable hover for auto-save in save mode
            final Rectangle finalSlot = slot;
            slot.setOnMouseEntered(e -> {
                if (!(isSaveMode && isAutoSaveSlot)) {
                    boolean currentSaveExists = SaveManager.saveExists("save" + slotIndex);
                    finalSlot.setFill(Color.rgb(120, currentSaveExists ? 220 : 170, currentSaveExists ? 120 : 220));
                }
            });
            slot.setOnMouseExited(e -> {
                if (!(isSaveMode && isAutoSaveSlot)) {
                    boolean currentSaveExists = SaveManager.saveExists("save" + slotIndex);
                    finalSlot.setFill(currentSaveExists ? Color.rgb(100, 200, 100) : Color.rgb(100, 150, 200));
                } else {
                    // Auto-save slot in save mode: grayed out
                    finalSlot.setFill(Color.rgb(150, 150, 150));
                }
            });
            
            saveSlots[i] = slot;
            saveSlotTexts[i] = slotText;
        }
        
        saveLoadContainer.getChildren().addAll(
            saveLoadBackground,
            cancelButton,
            cancelButtonText,
            modeTitleText
        );
        
        for (int i = 0; i < 6; i++) {
            saveLoadContainer.getChildren().addAll(saveSlots[i], saveSlotTexts[i]);
        }
        
        // Initially hidden
        saveLoadContainer.setVisible(false);
    }
    
    /**
     * Show the menu UI
     */
    public void show() {
        if (!isVisible) {
            isVisible = true;
            updateGoldAndReturnTime(); // Update values when showing
            FXGL.getGameScene().addUINode(mainContainer);
            FXGL.getGameScene().addUINode(saveLoadContainer);
            FXGL.getGameScene().addUINode(callPartyContainer);
        }
    }
    
    /**
     * Update gold coin and return time display
     */
    public void updateGoldAndReturnTime() {
        if (goldCoinText != null) {
            goldCoinText.setText("Gold: " + org.example.testing.gold_coin);
        }
        if (returnTimeText != null) {
            returnTimeText.setText("Return Time: " + org.example.testing.return_time);
        }
        if(testing.status.contains("LitaruStart")&&!testing.storyItemInventory.hasStoryItem("radiant_core")&&!testing.storyItemInventory.hasStoryItem("litaru_sword")){
            dungeonButtonText.setText("Go to Not \nDungeon");
        }else{
            dungeonButtonText.setText("Go to \nDungeon");
        }

    }
    
    /**
     * Hide the menu UI
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(mainContainer);
            FXGL.getGameScene().removeUINode(saveLoadContainer);
            FXGL.getGameScene().removeUINode(callPartyContainer);
            cancelSaveLoad(); // Make sure to exit save/load mode
            hideCallParty(); // Make sure to hide call party
            hideCity(); // Make sure to hide city UI
        }
    }
    
    /**
     * Check if menu is visible
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Handle "Go to Dungeon" button click
     */
    private void goToDungeon() {
        hide();
        if(testing.status.contains("LitaruStart")&&!testing.storyItemInventory.hasStoryItem("radiant_core")&&!testing.storyItemInventory.hasStoryItem("litaru_sword")){
            mapUI.getGameMap().initializeMap();
            mapUI.showSelectedPath();
            testing.selectedHeroes=new String[]{"Azar","Litaru"};
            battleSystem.removeAllHeroes();
            battleSystem.configureBattle(false,testing.selectedHeroes);
        }
        else if (mapUI != null) {
            mapUI.getGameMap().initializeMap();
            mapUI.showSelectedPath();
        }
    }
    
    /**
     * Handle Save button click
     * Shows save slot selection UI
     */
    private void saveGame() {
        // Hide call party and city panels
        if (isCallPartyVisible) {
            hideCallParty();
        }
        if (isCityVisible) {
            hideCity();
        }
        enterSaveMode();
    }
    
    /**
     * Enter save mode - show save slot selection
     */
    private void enterSaveMode() {
        isSaveMode = true;
        isLoadMode = false;
        modeTitleText.setText("Save a file");
        showSaveLoadUI();
        updateSaveSlotColors();
    }
    
    /**
     * Save to a specific slot
     */
    private void saveToSlot(int slotNumber) {
        String saveName = "save" + slotNumber;
        
        // Get current game state
        String[] availableHeroesArray = org.example.testing.getAvailableHeroes();
        List<String> availableHeroes = Arrays.asList(availableHeroesArray);
        int goldCoin = org.example.testing.gold_coin;
        int returnTime = org.example.testing.return_time;
        String status = testing.status;
        
        // Get story item IDs from testing.storyItemInventory
        List<String> storyItemIds = new ArrayList<>();
        if (testing.storyItemInventory != null) {
            storyItemIds = new ArrayList<>(testing.storyItemInventory.getAllStoryItemIds());
        } else if (testing.storyItem != null) {
            // Fallback to array if inventory not available
            storyItemIds = Arrays.asList(testing.storyItem);
        }
        
        // Create save data
        SaveData saveData = new SaveData(
            availableHeroes,
            goldCoin,
            returnTime,
            status,
            storyItemIds
        );
        
        // Save to file
        if (SaveManager.saveGame(saveName, saveData)) {
            String saveType = (slotNumber == 6) ? "Auto-saved" : "Game saved";
            System.out.println(saveType + " successfully to " + saveName + "!");
            // Update colors to show save exists (only if UI is visible)
            if (saveLoadContainer != null && saveLoadContainer.isVisible()) {
                updateSaveSlotColors();
            }
            // Only cancel save/load UI if in save mode and not auto-save
            if (isSaveMode && slotNumber != 6) {
                cancelSaveLoad(); // Exit save mode after manual saving
            }
        } else {
            System.out.println("Failed to save game to " + saveName + "!");
        }
    }
    
    /**
     * Handle Load button click
     * Shows load slot selection UI
     */
    private void loadGame() {
        // Hide call party and city panels
        if (isCallPartyVisible) {
            hideCallParty();
        }
        if (isCityVisible) {
            hideCity();
        }
        enterLoadMode();
    }
    
    /**
     * Enter load mode - show load slot selection
     */
    private void enterLoadMode() {
        isSaveMode = false;
        isLoadMode = true;
        modeTitleText.setText("Load a file");
        showSaveLoadUI();
        updateSaveSlotColors();
    }
    
    /**
     * Load from a specific slot
     */
    private void loadFromSlot(int slotNumber) {
        String saveName = "save" + slotNumber;
        
        // Load from file
        SaveData saveData = SaveManager.loadGame(saveName);
        
        if (saveData != null) {
            System.out.println("Game loaded successfully from " + saveName + "!");
            System.out.println("Gold: " + saveData.getGoldCoin());
            System.out.println("Return Time: " + saveData.getReturnTime());
            System.out.println("Characters: " + saveData.getAvailableCharacters());
            System.out.println("Status: " + saveData.getStatus());
            
            // Apply loaded data to game state
            // Update AVAILABLE_HEROES
            List<String> loadedHeroes = saveData.getAvailableCharacters();
            if (loadedHeroes != null && !loadedHeroes.isEmpty()) {
                String[] heroesArray = loadedHeroes.toArray(new String[0]);
                org.example.testing.setAvailableHeroes(heroesArray);
                System.out.println("Available heroes updated to: " + Arrays.toString(org.example.testing.getAvailableHeroes()));
            }
            
            // Update gold coin
            org.example.testing.gold_coin = saveData.getGoldCoin();
            
            // Update return time
            org.example.testing.return_time = saveData.getReturnTime();

            //Apply status string to game state
            testing.status = saveData.getStatus();
            
            // Update story items
            List<String> loadedStoryItemIds = saveData.getStoryItemIds();
            if (loadedStoryItemIds != null && !loadedStoryItemIds.isEmpty()) {
                // Update testing.storyItem array
                testing.storyItem = loadedStoryItemIds.toArray(new String[0]);
                
                // Add story items to testing.storyItemInventory
                if (testing.storyItemInventory != null) {
                    testing.storyItemInventory.clear(); // Clear existing items
                    for (String itemId : loadedStoryItemIds) {
                        testing.storyItemInventory.addStoryItem(itemId);
                    }
                }
                
                // Also update local storyItemInventory if it exists (for warehouse UI)
                if (storyItemInventory != null) {
                    storyItemInventory.clear(); // Clear existing items
                    for (String itemId : loadedStoryItemIds) {
                        storyItemInventory.addStoryItem(itemId);
                    }
                }
            } else {
                testing.storyItem = new String[]{};
                if (testing.storyItemInventory != null) {
                    testing.storyItemInventory.clear();
                }
                if (storyItemInventory != null) {
                    storyItemInventory.clear();
                }
            }
            
            // Update display
            updateGoldAndReturnTime();
            

            
            cancelSaveLoad(); // Exit load mode after loading
        } else {
            System.out.println("Failed to load game or save file " + saveName + " not found!");
        }
    }
    
    /**
     * Show save/load selection UI
     */
    private void showSaveLoadUI() {
        // Make menu semi-transparent
        mainContainer.setOpacity(0.3);
        // Disable all buttons so they can't be clicked
        disableAllButtons();
        saveLoadContainer.setVisible(true);
    }
    
    /**
     * Disable all menu buttons when save/load UI is active
     */
    private void disableAllButtons() {
        if (dungeonButton != null) {
            dungeonButton.setDisable(true);
            dungeonButton.setOpacity(0.5);
        }
        if (saveButton != null) {
            saveButton.setDisable(true);
            saveButton.setOpacity(0.5);
        }
        if (loadButton != null) {
            loadButton.setDisable(true);
            loadButton.setOpacity(0.5);
        }
        if (libraryButton != null) {
            libraryButton.setDisable(true);
            libraryButton.setOpacity(0.5);
        }
        if (cityButton != null) {
            cityButton.setDisable(true);
            cityButton.setOpacity(0.5);
        }
        if (callPartyButton != null) {
            callPartyButton.setDisable(true);
            callPartyButton.setOpacity(0.5);
        }
    }
    
    /**
     * Re-enable all menu buttons after save/load UI is closed
     */
    private void enableAllButtons() {
        if (dungeonButton != null) {
            dungeonButton.setDisable(false);
            dungeonButton.setOpacity(1.0);
        }
        if (saveButton != null) {
            saveButton.setDisable(false);
            saveButton.setOpacity(1.0);
        }
        if (loadButton != null) {
            loadButton.setDisable(false);
            loadButton.setOpacity(1.0);
        }
        if (libraryButton != null) {
            libraryButton.setDisable(false);
            libraryButton.setOpacity(1.0);
        }
        if (cityButton != null) {
            cityButton.setDisable(false);
            cityButton.setOpacity(1.0);
        }
        if (callPartyButton != null) {
            callPartyButton.setDisable(false);
            callPartyButton.setOpacity(1.0);
        }
    }
    
    /**
     * Cancel save/load mode
     */
    private void cancelSaveLoad() {
        isSaveMode = false;
        isLoadMode = false;
        mainContainer.setOpacity(1.0); // Restore full opacity
        // Re-enable all buttons
        enableAllButtons();
        saveLoadContainer.setVisible(false);
    }
    
    /**
     * Update save slot colors based on whether saves exist
     */
    private void updateSaveSlotColors() {
        for (int i = 0; i < 6; i++) {
            int slotNumber = i + 1;
            boolean saveExists = SaveManager.saveExists("save" + slotNumber);
            boolean isAutoSaveSlot = (slotNumber == 6);
            
            if (isAutoSaveSlot && isSaveMode) {
                // Auto-save slot in save mode: grayed out (not clickable)
                saveSlots[i].setFill(Color.rgb(150, 150, 150));
            } else if (saveExists) {
                saveSlots[i].setFill(Color.rgb(100, 200, 100)); // Green if save exists
            } else {
                saveSlots[i].setFill(Color.rgb(100, 150, 200)); // Blue if no save
            }
        }
    }
    
    /**
     * Auto-save to slot 6 (called from resetBackToMenu)
     */
    public void autoSave() {
        saveToSlot(6);
    }
}

