package ui;

import characters.SpecialTalents;
import items.*;
import characters.Observer;
import characters.Characters;
import battle.BattleSystem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import com.almasb.fxgl.dsl.FXGL;

import java.util.*;

/**
 * UI for managing inventory and equipment
 */
public class InventoryUI {
    private Inventory inventory;
    private BattleSystem battleSystem;
    private boolean isVisible = false;
    // Map mode checker - returns true if in map mode, false if in battle mode
    private java.util.function.Supplier<Boolean> mapModeChecker = () -> false;
    
    // UI Components
    private Rectangle background;
    private VBox mainContainer;
    private HBox topContainer;
    private HBox bottomContainer;
    
    // Inventory section
    private VBox inventorySection;
    private Text inventoryTitle;
    private VBox inventoryItems;
    private ScrollPane inventoryScrollPane;
    
    // Equipment section
    private VBox equipmentSection;
    private HBox equipmentTitleContainer;
    private Text equipmentTitle;
    private Rectangle statsButton;
    private VBox equipmentSlots;
    private HBox characterNavigation;
    private Text currentCharacterName;
    private Rectangle prevButton;
    private Rectangle nextButton;
    private int currentCharacterIndex = 0;
    private Observer.characterSlot[] heroes;
    
    // Stats panel
    private Rectangle statsPanelBackground;
    private VBox statsPanelContainer;
    private boolean statsPanelVisible = false;
    
    // Battle consumables section
    private VBox consumablesSection;
    private Text consumablesTitle;
    private VBox consumableSlots;
    
    // Gold display
    private Text goldText;
    
    public InventoryUI(Inventory inventory, BattleSystem battleSystem) {
        this.inventory = inventory;
        this.battleSystem = battleSystem;
        initializeUI();
    }
    
    /**
     * Set the map mode checker function
     * This function should return true when in map mode, false when in battle mode
     */
    public void setMapModeChecker(java.util.function.Supplier<Boolean> checker) {
        this.mapModeChecker = checker != null ? checker : () -> false;
    }
    
    private void initializeUI() {
        // Main background - increased size to accommodate all content
        background = new Rectangle(1200, 800, Color.rgb(240, 240, 240)); // Lighter gray, increased height
        background.setStroke(Color.rgb(100, 100, 100)); // Darker border
        background.setStrokeWidth(3);
        background.setTranslateX(0);
        background.setTranslateY(0);
        
        // Main container
        mainContainer = new VBox(15);
        mainContainer.setTranslateX(30);
        mainContainer.setTranslateY(30);
        
        // Top container (title and gold)
        topContainer = new HBox(20);
        
        Text title = new Text("Inventory & Equipment");
        title.setFont(new Font(28));
        title.setFill(Color.rgb(25, 25, 112)); // Midnight blue
        title.setStroke(Color.rgb(50, 50, 150));
        title.setStrokeWidth(1);
        
        goldText = new Text("Gold: " + inventory.getGold());
        goldText.setFont(new Font(18));
        goldText.setFill(Color.rgb(255, 215, 0)); // Gold color
        goldText.setStroke(Color.rgb(184, 134, 11));
        goldText.setStrokeWidth(0.5);
        
        topContainer.getChildren().addAll(title, goldText);
        
        // Bottom container (inventory on left, right side with equipment and consumables)
        bottomContainer = new HBox(30);
        
        // Initialize sections
        initializeInventorySection();
        initializeEquipmentSection();
        initializeConsumablesSection();
        
        // Create right side container for equipment and consumables
        VBox rightSideContainer = new VBox(20);
        rightSideContainer.getChildren().addAll(equipmentSection, consumablesSection);
        
        bottomContainer.getChildren().addAll(inventorySection, rightSideContainer);
        
        // Add to main container
        mainContainer.getChildren().addAll(topContainer, bottomContainer);
        
        // Initially hidden
        hide();
    }
    
    private void initializeInventorySection() {
        inventorySection = new VBox(8);
        inventorySection.setPrefWidth(400); // Increased width for left side
        
        inventoryTitle = new Text("Inventory");
        inventoryTitle.setFont(new Font(22));
        inventoryTitle.setFill(Color.rgb(0, 100, 0)); // Dark green
        inventoryTitle.setStroke(Color.rgb(0, 150, 0));
        inventoryTitle.setStrokeWidth(0.5);
        
        inventoryItems = new VBox(3);
        
        // Create scroll pane for inventory items
        inventoryScrollPane = new ScrollPane();
        inventoryScrollPane.setContent(inventoryItems);
        inventoryScrollPane.setPrefViewportHeight(400); // Set maximum height
        inventoryScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Only vertical scroll
        inventoryScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        inventoryScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        inventorySection.getChildren().addAll(inventoryTitle, inventoryScrollPane);
    }
    
    private void initializeEquipmentSection() {
        equipmentSection = new VBox(8);
        equipmentSection.setPrefWidth(400); // Adjusted width for right side
        
        // Create title container with text and button
        equipmentTitleContainer = new HBox(10);
        
        equipmentTitle = new Text("Character Equipment");
        equipmentTitle.setFont(new Font(22));
        equipmentTitle.setFill(Color.rgb(139, 0, 139)); // Dark magenta
        equipmentTitle.setStroke(Color.rgb(186, 85, 211));
        equipmentTitle.setStrokeWidth(0.5);
        
        // Stats button
        statsButton = new Rectangle(80, 30, Color.rgb(100, 150, 200));
        statsButton.setStroke(Color.BLACK);
        statsButton.setStrokeWidth(1);
        
        Text statsButtonText = new Text("Stats");
        statsButtonText.setFont(new Font(12));
        statsButtonText.setFill(Color.WHITE);
        statsButtonText.setMouseTransparent(true);
        
        StackPane statsButtonPane = new StackPane();
        statsButtonPane.getChildren().addAll(statsButton, statsButtonText);
        statsButtonPane.setPrefSize(80, 30);
        statsButtonPane.setOnMouseClicked(e -> toggleStatsPanel());
        
        statsButtonPane.setOnMouseEntered(e -> statsButton.setFill(Color.rgb(120, 170, 220)));
        statsButtonPane.setOnMouseExited(e -> statsButton.setFill(Color.rgb(100, 150, 200)));
        
        equipmentTitleContainer.getChildren().addAll(equipmentTitle, statsButtonPane);
        
        // Initialize heroes array
        if (battleSystem != null) {
            heroes = battleSystem.getAllHeroes();
        } else {
            heroes = new Observer.characterSlot[0];
        }
        
        // Create character navigation
        createCharacterNavigation();
        
        equipmentSlots = new VBox(8);
        
        // Show current character equipment
        showCurrentCharacterEquipment();
        
        // Initialize stats panel
        initializeStatsPanel();
        
        equipmentSection.getChildren().addAll(equipmentTitleContainer, characterNavigation, equipmentSlots);
    }
    
    private void createCharacterNavigation() {
        characterNavigation = new HBox(15);
        characterNavigation.setTranslateX(0);
        characterNavigation.setTranslateY(0);
        
        // Previous button
        prevButton = new Rectangle(30, 30, Color.LIGHTGRAY);
        prevButton.setStroke(Color.BLACK);
        prevButton.setStrokeWidth(1);
        
        Text prevText = new Text("◀");
        prevText.setFont(new Font(16));
        prevText.setFill(Color.BLACK);
        
        StackPane prevPane = new StackPane();
        prevPane.getChildren().addAll(prevButton, prevText);
        prevPane.setOnMouseClicked(e -> previousCharacter());
        
        // Current character name
        currentCharacterName = new Text("No Character");
        currentCharacterName.setFont(new Font(16));
        currentCharacterName.setFill(Color.rgb(75, 0, 130)); // Indigo
        currentCharacterName.setStroke(Color.rgb(138, 43, 226));
        currentCharacterName.setStrokeWidth(0.3);
        
        // Next button
        nextButton = new Rectangle(30, 30, Color.LIGHTGRAY);
        nextButton.setStroke(Color.BLACK);
        nextButton.setStrokeWidth(1);
        
        Text nextText = new Text("▶");
        nextText.setFont(new Font(16));
        nextText.setFill(Color.BLACK);
        
        StackPane nextPane = new StackPane();
        nextPane.getChildren().addAll(nextButton, nextText);
        nextPane.setOnMouseClicked(e -> nextCharacter());
        
        characterNavigation.getChildren().addAll(prevPane, currentCharacterName, nextPane);
        
        // Update character name
        updateCurrentCharacterName();
    }
    
    private void showCurrentCharacterEquipment() {
        equipmentSlots.getChildren().clear();
        
        if (heroes.length > 0 && currentCharacterIndex < heroes.length) {
            Observer.characterSlot currentHero = heroes[currentCharacterIndex];
            if (currentHero != null) {
                createCharacterEquipmentSection(currentHero);
            }
        }
    }
    
    private void updateCurrentCharacterName() {
        if (heroes.length > 0 && currentCharacterIndex < heroes.length) {
            Observer.characterSlot currentHero = heroes[currentCharacterIndex];
            if (currentHero != null) {
                currentCharacterName.setText(currentHero.getCharacter().getName());
            } else {
                currentCharacterName.setText("No Character");
            }
        } else {
            currentCharacterName.setText("No Character");
        }
    }
    
    private void previousCharacter() {
        if (heroes.length > 0) {
            currentCharacterIndex = (currentCharacterIndex - 1 + heroes.length) % heroes.length;
            updateCurrentCharacterName();
            showCurrentCharacterEquipment();
            // Refresh stats panel if visible
            if (statsPanelVisible) {
                showStatsPanel();
            }
        }
    }
    
    private void nextCharacter() {
        if (heroes.length > 0) {
            currentCharacterIndex = (currentCharacterIndex + 1) % heroes.length;
            updateCurrentCharacterName();
            showCurrentCharacterEquipment();
            // Refresh stats panel if visible
            if (statsPanelVisible) {
                showStatsPanel();
            }
        }
    }
    
    private void initializeConsumablesSection() {
        consumablesSection = new VBox(8);
        consumablesSection.setPrefWidth(400); // Same width as equipment section
        
        consumablesTitle = new Text("Battle Items (Max 3)");
        consumablesTitle.setFont(new Font(22));
        consumablesTitle.setFill(Color.rgb(220, 20, 60)); // Crimson
        consumablesTitle.setStroke(Color.rgb(255, 69, 0));
        consumablesTitle.setStrokeWidth(0.5);
        
        consumableSlots = new VBox(8);
        
        consumablesSection.getChildren().addAll(consumablesTitle, consumableSlots);
    }
    
    private void createCharacterEquipmentSection(Observer.characterSlot hero) {
        // Create equipment slots for this character
        for (EquipmentItem.EquipmentSlot slot : EquipmentItem.EquipmentSlot.values()) {
            createCharacterEquipmentSlot(hero, slot);
        }
    }
    
    private void createCharacterEquipmentSlot(Observer.characterSlot hero, EquipmentItem.EquipmentSlot slot) {
        HBox slotContainer = new HBox(15);
        
        Text slotLabel = new Text("  " + slot.getDisplayName() + ":");
        slotLabel.setFont(new Font(13));
        slotLabel.setFill(Color.BLACK);
        slotLabel.setWrappingWidth(100); // Prevent text wrapping
        
        Rectangle slotRect = new Rectangle(180, 30, Color.WHITE); // Increased size
        slotRect.setStroke(Color.BLACK);
        slotRect.setStrokeWidth(1);
        
        // Get equipped item for this character and slot
        EquipmentItem equippedItem = inventory.getEquippedItem(hero, slot);
        String itemName = equippedItem != null ? equippedItem.getName() : "Empty";
        Color itemColor = equippedItem != null ? Color.BLUE : Color.GRAY;
        
        Text slotText = new Text(itemName);
        slotText.setFont(new Font(11));
        slotText.setFill(itemColor);
        slotText.setWrappingWidth(170); // Allow text wrapping within slot
        slotText.setTranslateX(5);
        slotText.setTranslateY(20);
        
        StackPane slotPane = new StackPane();
        slotPane.getChildren().addAll(slotRect, slotText);
        
        // Add visual feedback for equipped items
        if (equippedItem != null) {
            slotRect.setFill(Color.rgb(240, 248, 255)); // Light blue background for equipped items
            slotRect.setStroke(Color.rgb(70, 130, 180)); // Steel blue border
        }
        
        // Add right-click event to unequip item
        slotPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY && equippedItem != null) {
                unequipItem(hero, slot, equippedItem);
            }
        });
        
        // Add hover effect for equipped items
        if (equippedItem != null) {
            slotPane.setOnMouseEntered(e -> {
                slotRect.setFill(Color.rgb(220, 230, 240)); // Darker blue on hover
            });
            
            slotPane.setOnMouseExited(e -> {
                slotRect.setFill(Color.rgb(240, 248, 255)); // Back to light blue
            });
        }
        
        slotContainer.getChildren().addAll(slotLabel, slotPane);
        equipmentSlots.getChildren().add(slotContainer);
    }
    
    private void unequipItem(Observer.characterSlot hero, EquipmentItem.EquipmentSlot slot, EquipmentItem item) {
        if (inventory.unequipItem(slot, hero)) {
            // Reset and apply equipment stats first
            SpecialTalents.resetStatModification(hero);
            // Then apply buff/debuff modifications
            SpecialTalents.applyStatModifications(hero, null);
            updateBattleUI(hero);
            System.out.println("Unequipped " + item.getName() + " from " + hero.getCharacter().getName());
            refreshUI();
            // Refresh stats panel if visible
            if (statsPanelVisible) {
                showStatsPanel();
            }
        } else {
            System.out.println("Cannot unequip " + item.getName() + " from " + hero.getCharacter().getName());
        }
    }
    
    public void show() {
        System.out.println("InventoryUI.show() called. Current visible: " + isVisible);
        if (!isVisible) {
            isVisible = true;
            refreshUI();
            // Add to UI layer to ensure it's on top
            FXGL.getGameScene().addUINode(background);
            FXGL.getGameScene().addUINode(mainContainer);
            System.out.println("Inventory UI shown! Background: " + background + ", Container: " + mainContainer);
            System.out.println("Background position: x=" + background.getTranslateX() + ", y=" + background.getTranslateY());
            System.out.println("Container position: x=" + mainContainer.getTranslateX() + ", y=" + mainContainer.getTranslateY());
        } else {
            System.out.println("Inventory UI already visible, skipping show()");
        }
    }
    
    public void hide() {
        System.out.println("InventoryUI.hide() called. Current visible: " + isVisible);
        if (isVisible) {
            isVisible = false;
            hideStatsPanel(); // Hide stats panel if visible
            FXGL.getGameScene().removeUINode(background);
            FXGL.getGameScene().removeUINode(mainContainer);
            System.out.println("Inventory UI hidden!");
        } else {
            System.out.println("Inventory UI already hidden, skipping hide()");
        }
    }
    
    public void toggle() {
        System.out.println("InventoryUI.toggle() called. Current visible: " + isVisible);
        if (isVisible) {
            System.out.println("Hiding inventory UI...");
            battleSystem.setMoving(true);
            hide();
        } else {
            System.out.println("Showing inventory UI...");
            battleSystem.setMoving(false);
            show();
        }
        System.out.println("InventoryUI.toggle() completed. New visible: " + isVisible);
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void refreshUI() {
        refreshGold();
        refreshInventory();
        refreshEquipment();
        refreshConsumables();
    }
    
    private void refreshGold() {
        goldText.setText("Gold: " + inventory.getGold());
    }
    
    private void refreshInventory() {
        inventoryItems.getChildren().clear();
        
        Map<String, Integer> items = inventory.getAllItems();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Item item = inventory.getItem(entry.getKey());
            if (item != null) {
                createInventoryItemDisplay(item, entry.getValue());
            }
        }
    }
    
    private void createInventoryItemDisplay(Item item, int quantity) {
        VBox itemContainer = new VBox(5);
        
        // Item name and quantity
        Text itemText = new Text(item.getName() + " x" + quantity);
        itemText.setFont(new Font(26));
        
        // Set color based on rarity for equipment items
        if (item.getItemType() == Item.ItemType.EQUIPMENT) {
            Item.ItemRarity rarity = item.getRarity();
            if (rarity == Item.ItemRarity.COMMON) {
                itemText.setFill(Color.rgb(100, 100, 100)); // Darker gray
            } else if (rarity == Item.ItemRarity.UNCOMMON) {
                itemText.setFill(Color.rgb(0, 150, 0)); // Darker green
            } else if (rarity == Item.ItemRarity.RARE) {
                itemText.setFill(Color.rgb(0, 0, 180)); // Darker blue
            } else if (rarity == Item.ItemRarity.LEGENDARY) {
                itemText.setFill(Color.rgb(200, 170, 0)); // Darker gold
            } else {
                itemText.setFill(Color.BLACK);
            }
        } else {
            itemText.setFill(Color.BLACK);
        }
        
        itemText.setWrappingWidth(250); // Allow text wrapping
        
        // Item description
        Text descriptionText = new Text(item.getDescription());
        descriptionText.setFont(new Font(20));
        descriptionText.setFill(Color.GRAY);
        descriptionText.setWrappingWidth(250);
        
        // Item details based on type
        Text detailsText = null;
        if (item.getItemType() == Item.ItemType.EQUIPMENT) {
            EquipmentItem equipment = (EquipmentItem) item;
            detailsText = new Text("Slot: " + equipment.getSlot().getDisplayName() + 
                                 " | " + equipment.getStatBonus().toString());
            detailsText.setFont(new Font(18));
            detailsText.setFill(Color.BLUE);
        } else if (item.getItemType() == Item.ItemType.CONSUMABLE) {
            ConsumableItem consumable = (ConsumableItem) item;
            detailsText = new Text("Effect: " + consumable.getEffectDescription() + 
                                 " | Value: " + consumable.getEffectValue());
            detailsText.setFont(new Font(18));
            detailsText.setFill(Color.GREEN);
        }
        
        // Action buttons
        HBox buttonContainer = new HBox(10);
        if (item.getItemType() == Item.ItemType.EQUIPMENT) {
            createEquipmentButton(buttonContainer, (EquipmentItem) item);
        } else if (item.getItemType() == Item.ItemType.CONSUMABLE) {
            createConsumableButton(buttonContainer, (ConsumableItem) item);
        }
        
        // Add all elements
        itemContainer.getChildren().add(itemText);
        if (detailsText != null) {
            itemContainer.getChildren().add(detailsText);
        }
        itemContainer.getChildren().add(descriptionText);
        itemContainer.getChildren().add(buttonContainer);
        
        inventoryItems.getChildren().add(itemContainer);
    }
    
    private void createEquipmentButton(HBox container, EquipmentItem equipment) {
        Rectangle equipButton = new Rectangle(70, 25, Color.LIGHTBLUE);
        equipButton.setStroke(Color.BLACK);
        equipButton.setStrokeWidth(1);
        
        Text equipText = new Text("Equip");
        equipText.setFont(new Font(11));
        equipText.setFill(Color.BLACK);
        // Center text in StackPane - StackPane will center automatically
        equipText.setMouseTransparent(true);

        StackPane equipPane = new StackPane();
        equipPane.getChildren().addAll(equipButton, equipText);
        equipPane.setPrefSize(70, 25);

        equipPane.setOnMouseClicked(e -> showCharacterSelectionDialog(equipment));
        
        container.getChildren().add(equipPane);
        
    }
    
    private void createConsumableButton(HBox container, ConsumableItem consumable) {
        Rectangle addButton = new Rectangle(70, 25, Color.GREEN);
        addButton.setStroke(Color.BLACK);
        addButton.setStrokeWidth(1);
        
        Text addText = new Text("Add");
        addText.setFont(new Font(11));
        addText.setFill(Color.WHITE);
        // Center text in StackPane - StackPane will center automatically
        addText.setMouseTransparent(true);
        
        StackPane addPane = new StackPane();
        addPane.getChildren().addAll(addButton, addText);
        addPane.setPrefSize(70, 25);
        
        // Update button state based on map mode
        updateAddButtonState(addButton, addText);
        
        addPane.setOnMouseClicked(e -> addToBattleConsumables(consumable));
        
        container.getChildren().add(addPane);
    }
    
    private void showCharacterSelectionDialog(EquipmentItem equipment) {
        if (battleSystem == null) return;
        
        Observer.characterSlot[] heroes = battleSystem.getAllHeroes();
        if (heroes.length == 0) return;
        
        // Create character selection dialog
        Rectangle dialogBackground = new Rectangle(400, 300, Color.rgb(250, 250, 250));
        dialogBackground.setStroke(Color.BLACK);
        dialogBackground.setStrokeWidth(2);
        dialogBackground.setTranslateX(400);
        dialogBackground.setTranslateY(200);
        
        VBox dialogContainer = new VBox(15);
        dialogContainer.setTranslateX(420);
        dialogContainer.setTranslateY(220);
        
        Text dialogTitle = new Text("Select Character to Equip:");
        dialogTitle.setFont(new Font(18));
        dialogTitle.setFill(Color.BLACK);
        
        Text itemInfo = new Text(equipment.getName() + " (" + equipment.getSlot().getDisplayName() + ")");
        itemInfo.setFont(new Font(14));
        itemInfo.setFill(Color.BLUE);
        
        VBox characterButtons = new VBox(10);
        
        for (Observer.characterSlot hero : heroes) {
            if (hero != null) {
                createCharacterSelectionButton(characterButtons, hero, equipment, dialogBackground, dialogContainer);
            }
        }
        
        // Cancel button
        Rectangle cancelButton = new Rectangle(100, 30, Color.RED);
        cancelButton.setStroke(Color.BLACK);
        cancelButton.setStrokeWidth(1);
        
        Text cancelText = new Text("Cancel");
        cancelText.setFont(new Font(12));
        cancelText.setFill(Color.WHITE);
        // Center text in StackPane - StackPane will center automatically
        cancelText.setMouseTransparent(true);
        
        StackPane cancelPane = new StackPane();
        cancelPane.getChildren().addAll(cancelButton, cancelText);
        cancelPane.setPrefSize(100, 30);
        
        cancelPane.setOnMouseClicked(e -> {
            FXGL.getGameScene().removeUINode(dialogBackground);
            FXGL.getGameScene().removeUINode(dialogContainer);
        });
        
        dialogContainer.getChildren().addAll(dialogTitle, itemInfo, characterButtons, cancelPane);
        
        // Add to UI
        FXGL.getGameScene().addUINode(dialogBackground);
        FXGL.getGameScene().addUINode(dialogContainer);
    }
    
    private void createCharacterSelectionButton(VBox container, Observer.characterSlot hero, EquipmentItem equipment, 
                                               Rectangle dialogBackground, VBox dialogContainer) {
        Rectangle charButton = new Rectangle(200, 40, Color.LIGHTBLUE);
        charButton.setStroke(Color.BLACK);
        charButton.setStrokeWidth(1);
        
        Text charText = new Text(hero.getCharacter().getName());
        charText.setFont(new Font(14));
        charText.setFill(Color.BLACK);
        // Center text in StackPane - StackPane will center automatically
        charText.setMouseTransparent(true);
        
        StackPane charPane = new StackPane();
        charPane.getChildren().addAll(charButton, charText);
        charPane.setPrefSize(200, 40);
        
        charPane.setOnMouseClicked(e -> {
            if (inventory.equipItem(equipment.getId(), hero)) {
                // Get the actual hero slot from battle system
                Observer.characterSlot heroSlot = battleSystem.getHeroByName(hero.getCharacter().getName());
                if (heroSlot != null) {
                    // Apply buff/debuff modifications
                    SpecialTalents.applyStatModifications(heroSlot, null);
                    updateBattleUI(heroSlot);
                }
                System.out.println("Equipped " + equipment.getName() + " to " + hero.getCharacter().getName());
                refreshUI();
                // Refresh stats panel if visible
                if (statsPanelVisible) {
                    showStatsPanel();
                }
            } else {
                System.out.println("Cannot equip " + equipment.getName() + " to " + hero.getCharacter().getName());
            }
            
            // Remove dialog
            FXGL.getGameScene().removeUINode(dialogBackground);
            FXGL.getGameScene().removeUINode(dialogContainer);
        });
        
        container.getChildren().add(charPane);
        hideStatsPanel();
    }
    
    
    private void addToBattleConsumables(ConsumableItem consumable) {
        // Only allow changing consumables when in map mode
        if (!mapModeChecker.get()) {
            System.out.println("Cannot change consumables during battle. Return to map mode first.");
            return;
        }
        
        if (inventory.addBattleConsumable(consumable.getId())) {
            System.out.println("Added " + consumable.getName() + " to battle consumables");
            refreshUI();
        } else {
            System.out.println("Cannot add " + consumable.getName() + " (max 3 items)");
        }
    }
    
    private void refreshEquipment() {
        // Update heroes array
        if (battleSystem != null) {
            heroes = battleSystem.getAllHeroes();
        } else {
            heroes = new Observer.characterSlot[0];
        }
        
        // Reset current character index if it's out of bounds
        if (currentCharacterIndex >= heroes.length) {
            currentCharacterIndex = 0;
        }
        
        // Update character name and show current character equipment
        updateCurrentCharacterName();
        showCurrentCharacterEquipment();
    }
    
    /**
     * Update the visual state of add button based on map mode
     */
    private void updateAddButtonState(Rectangle button, Text text) {
        boolean inMapMode = mapModeChecker.get();
        if (inMapMode) {
            button.setFill(Color.GREEN);
            text.setFill(Color.WHITE);
        } else {
            button.setFill(Color.GRAY);
            text.setFill(Color.DARKGRAY);
        }
    }
    
    /**
     * Update the visual state of remove button based on map mode
     */
    private void updateRemoveButtonState(Rectangle button, Text text) {
        boolean inMapMode = mapModeChecker.get();
        if (inMapMode) {
            button.setFill(Color.RED);
            text.setFill(Color.WHITE);
        } else {
            button.setFill(Color.GRAY);
            text.setFill(Color.DARKGRAY);
        }
    }
    
    private void refreshConsumables() {
        consumableSlots.getChildren().clear();
        
        List<ConsumableItem> battleConsumables = inventory.getBattleConsumables();
        for (int i = 0; i < 3; i++) {
            HBox slotContainer = new HBox(12);
            
            Text slotLabel = new Text("Slot " + (i + 1) + ":");
            slotLabel.setFont(new Font(13));
            slotLabel.setFill(Color.BLACK);
            slotLabel.setWrappingWidth(60); // Prevent text wrapping
            
            Rectangle slotRect = new Rectangle(140, 30, Color.WHITE); // Increased size
            slotRect.setStroke(Color.BLACK);
            slotRect.setStrokeWidth(1);
            
            Text slotText = new Text("Empty");
            slotText.setFont(new Font(11));
            slotText.setFill(Color.GRAY);
            slotText.setWrappingWidth(130); // Allow text wrapping
            slotText.setTranslateX(5);
            slotText.setTranslateY(20);
            
            StackPane slotPane = new StackPane();
            slotPane.getChildren().addAll(slotRect, slotText);
            
            if (i < battleConsumables.size()) {
                ConsumableItem consumable = battleConsumables.get(i);
                slotText.setText(consumable.getName());
                slotText.setFill(Color.BLACK);
                
                // Add effect info
                Text effectText = new Text(consumable.getEffectDescription() + " (" + consumable.getEffectValue() + ")");
                effectText.setFont(new Font(8));
                effectText.setFill(Color.GREEN);
                effectText.setWrappingWidth(120);
                effectText.setTranslateX(5);
                effectText.setTranslateY(35);
                
                StackPane slotPaneWithEffect = new StackPane();
                slotPaneWithEffect.getChildren().addAll(slotRect, slotText, effectText);
                
                // Add remove button
                Rectangle removeButton = new Rectangle(25, 25, Color.RED);
                removeButton.setStroke(Color.BLACK);
                removeButton.setStrokeWidth(1);
                
                Text removeText = new Text("X");
                removeText.setFont(new Font(11));
                removeText.setFill(Color.WHITE);
                // Center text in StackPane - StackPane will center automatically
                removeText.setMouseTransparent(true);
                
                StackPane removePane = new StackPane();
                removePane.getChildren().addAll(removeButton, removeText);
                removePane.setPrefSize(25, 25);
                
                // Disable remove button visually when not in map mode
                updateRemoveButtonState(removeButton, removeText);
                
                final int index = i;
                removePane.setOnMouseClicked(e -> {
                    // Only allow changing consumables when in map mode
                    if (!mapModeChecker.get()) {
                        System.out.println("Cannot change consumables during battle. Return to map mode first.");
                        return;
                    }
                    inventory.removeBattleConsumable(index);
                    refreshUI();
                });
                
                slotContainer.getChildren().addAll(slotLabel, slotPaneWithEffect, removePane);
            } else {
                slotContainer.getChildren().addAll(slotLabel, slotPane);
            }
            
            consumableSlots.getChildren().add(slotContainer);
        }
    }
    
    private void initializeStatsPanel() {
        // Stats panel background
        statsPanelBackground = new Rectangle(350, 500, Color.rgb(250, 250, 250));
        statsPanelBackground.setStroke(Color.BLACK);
        statsPanelBackground.setStrokeWidth(2);
        statsPanelBackground.setTranslateX(450);
        statsPanelBackground.setTranslateY(50);
        statsPanelBackground.setVisible(false);
        
        // Stats panel container
        statsPanelContainer = new VBox(10);
        statsPanelContainer.setTranslateX(470);
        statsPanelContainer.setTranslateY(70);
        statsPanelContainer.setVisible(false);
        
        // Close button
        Rectangle closeButton = new Rectangle(30, 30, Color.RED);
        closeButton.setStroke(Color.BLACK);
        closeButton.setStrokeWidth(1);
        
        Text closeText = new Text("X");
        closeText.setFont(new Font(16));
        closeText.setFill(Color.WHITE);
        closeText.setMouseTransparent(true);
        
        StackPane closePane = new StackPane();
        closePane.getChildren().addAll(closeButton, closeText);
        closePane.setPrefSize(30, 30);
        closePane.setOnMouseClicked(e -> hideStatsPanel());
        
        closePane.setOnMouseEntered(e -> closeButton.setFill(Color.rgb(220, 0, 0)));
        closePane.setOnMouseExited(e -> closeButton.setFill(Color.RED));
        
        statsPanelContainer.getChildren().add(closePane);
    }
    
    private void toggleStatsPanel() {
        if (statsPanelVisible) {
            hideStatsPanel();
        } else {
            showStatsPanel();
        }
    }
    
    private void showStatsPanel() {
        if (heroes.length == 0 || currentCharacterIndex >= heroes.length) {
            return;
        }
        
        Observer.characterSlot currentHero = heroes[currentCharacterIndex];
        if (currentHero == null) {
            return;
        }
        
        // Clear previous stats
        statsPanelContainer.getChildren().clear();
        
        // Close button
        Rectangle closeButton = new Rectangle(30, 30, Color.RED);
        closeButton.setStroke(Color.BLACK);
        closeButton.setStrokeWidth(1);
        
        Text closeText = new Text("X");
        closeText.setFont(new Font(16));
        closeText.setFill(Color.WHITE);
        closeText.setMouseTransparent(true);
        
        StackPane closePane = new StackPane();
        closePane.getChildren().addAll(closeButton, closeText);
        closePane.setPrefSize(30, 30);
        closePane.setOnMouseClicked(e -> hideStatsPanel());
        
        closePane.setOnMouseEntered(e -> closeButton.setFill(Color.rgb(220, 0, 0)));
        closePane.setOnMouseExited(e -> closeButton.setFill(Color.RED));
        
        // Character name
        Text nameText = new Text(currentHero.getCharacter().getName());
        nameText.setFont(new Font(20));
        nameText.setFill(Color.BLACK);
        nameText.setStroke(Color.rgb(139, 0, 139));
        nameText.setStrokeWidth(0.5);
        
        // Stats
        Characters.character charData = currentHero.getCharacter();
        
        Text hpText = new Text(String.format("HP: %.0f / %.0f", currentHero.getCurrentHp(), charData.getHp()));
        hpText.setFont(new Font(16));
        hpText.setFill(Color.RED);
        
        Text mpText = new Text(String.format("MP: %.0f / %.0f", currentHero.getCurrentMp(), charData.getMp()));
        mpText.setFont(new Font(16));
        mpText.setFill(Color.BLUE);
        
        Text atkText = new Text(String.format("ATK: %.0f", charData.getAtk()));
        atkText.setFont(new Font(16));
        atkText.setFill(Color.BLACK);
        
        Text defText = new Text(String.format("DEF: %.0f", charData.getDef()));
        defText.setFont(new Font(16));
        defText.setFill(Color.BLACK);
        
        Text resText = new Text(String.format("RES: %.0f", charData.getRes()));
        resText.setFont(new Font(16));
        resText.setFill(Color.BLACK);
        
        Text spdText = new Text(String.format("SPD: %.0f", charData.getSpd()));
        spdText.setFont(new Font(16));
        spdText.setFill(Color.BLACK);
        
        // Active effects
        Text effectsTitle = new Text("Active Effects:");
        effectsTitle.setFont(new Font(14));
        effectsTitle.setFill(Color.BLACK);
        effectsTitle.setUnderline(true);
        
        VBox effectsList = new VBox(5);
        if (currentHero.getActiveEffects() != null && !currentHero.getActiveEffects().isEmpty()) {
            for (characters.BuffDebuff effect : currentHero.getActiveEffects()) {
                Text effectText = new Text("- " + effect.getName() + " (" + effect.getDuration() + " turns)");
                effectText.setFont(new Font(12));
                effectText.setFill(Color.BLUE);
                effectsList.getChildren().add(effectText);
            }
        } else {
            Text noEffectsText = new Text("None");
            noEffectsText.setFont(new Font(12));
            noEffectsText.setFill(Color.GRAY);
            effectsList.getChildren().add(noEffectsText);
        }
        
        statsPanelContainer.getChildren().addAll(
            closePane,
            nameText,
            hpText,
            mpText,
            atkText,
            defText,
            resText,
            spdText,
            effectsTitle,
            effectsList
        );
        
        statsPanelBackground.setVisible(true);
        statsPanelContainer.setVisible(true);
        statsPanelVisible = true;
        
            FXGL.getGameScene().addUINode(statsPanelBackground);
        FXGL.getGameScene().addUINode(statsPanelContainer);
    }
    
    private void hideStatsPanel() {
        if (statsPanelVisible) {
            statsPanelBackground.setVisible(false);
            statsPanelContainer.setVisible(false);
            statsPanelVisible = false;
            
            FXGL.getGameScene().removeUINode(statsPanelBackground);
            FXGL.getGameScene().removeUINode(statsPanelContainer);
        }
    }
    
    /**
     * Update battle UI for a character after equipment changes
     */
    private void updateBattleUI(Observer.characterSlot hero) {
        // Don't update battle UI if dialog system is running
        dialog.DialogSystem dialogSystem = dialog.DialogSystem.getInstance();

        
        if (battleSystem != null && battleSystem.hasAliveEnemy()) {
            // Access BattleUI through reflection or check if there's a getter
            // For now, we'll use the battleSystem's internal reference if available
            try {
                java.lang.reflect.Field battleUIField = battleSystem.getClass().getDeclaredField("battleUI");
                battleUIField.setAccessible(true);
                battle.BattleUI battleUI = (battle.BattleUI) battleUIField.get(battleSystem);
                if (battleUI != null) {
                    battleUI.updateHealthUI(hero);
                    battleUI.updateMpUI(hero);
                    battleUI.updateBurningRageBar(hero);
                    if (dialogSystem != null && dialogSystem.isRunning()) {
                        return;
                    }
                    battleUI.renderHeroSkillsFor(hero);
                    hide();
                    show();
                }
            } catch (Exception e) {
                // If reflection fails, stats will update on next turn
                System.out.println("Could not update battle UI: " + e.getMessage());
            }
        }
    }
}
