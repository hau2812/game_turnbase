package ui;

import items.*;
import characters.Observer;
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
    private Text equipmentTitle;
    private VBox equipmentSlots;
    private HBox characterNavigation;
    private Text currentCharacterName;
    private Rectangle prevButton;
    private Rectangle nextButton;
    private int currentCharacterIndex = 0;
    private Observer.characterSlot[] heroes;
    
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
        
        equipmentTitle = new Text("Character Equipment");
        equipmentTitle.setFont(new Font(22));
        equipmentTitle.setFill(Color.rgb(139, 0, 139)); // Dark magenta
        equipmentTitle.setStroke(Color.rgb(186, 85, 211));
        equipmentTitle.setStrokeWidth(0.5);
        
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
        
        equipmentSection.getChildren().addAll(equipmentTitle, characterNavigation, equipmentSlots);
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
        }
    }
    
    private void nextCharacter() {
        if (heroes.length > 0) {
            currentCharacterIndex = (currentCharacterIndex + 1) % heroes.length;
            updateCurrentCharacterName();
            showCurrentCharacterEquipment();
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
            System.out.println("Unequipped " + item.getName() + " from " + hero.getCharacter().getName());
            refreshUI();
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
            hide();
        } else {
            System.out.println("Showing inventory UI...");
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
        itemText.setFont(new Font(13));
        itemText.setFill(Color.BLACK);
        itemText.setWrappingWidth(250); // Allow text wrapping
        
        // Item description
        Text descriptionText = new Text(item.getDescription());
        descriptionText.setFont(new Font(10));
        descriptionText.setFill(Color.GRAY);
        descriptionText.setWrappingWidth(250);
        
        // Item details based on type
        Text detailsText = null;
        if (item.getItemType() == Item.ItemType.EQUIPMENT) {
            EquipmentItem equipment = (EquipmentItem) item;
            detailsText = new Text("Slot: " + equipment.getSlot().getDisplayName() + 
                                 " | " + equipment.getStatBonus().toString());
            detailsText.setFont(new Font(9));
            detailsText.setFill(Color.BLUE);
        } else if (item.getItemType() == Item.ItemType.CONSUMABLE) {
            ConsumableItem consumable = (ConsumableItem) item;
            detailsText = new Text("Effect: " + consumable.getEffectDescription() + 
                                 " | Value: " + consumable.getEffectValue());
            detailsText.setFont(new Font(9));
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
        Rectangle equipButton = new Rectangle(70, 25, Color.GREEN);
        equipButton.setStroke(Color.BLACK);
        equipButton.setStrokeWidth(1);
        
        Text equipText = new Text("Equip");
        equipText.setFont(new Font(11));
        equipText.setFill(Color.WHITE);
        equipText.setTranslateX(20);
        equipText.setTranslateY(17);
        
        StackPane equipPane = new StackPane();
        equipPane.getChildren().addAll(equipButton, equipText);
        
        equipPane.setOnMouseClicked(e -> showCharacterSelectionDialog(equipment));
        
        container.getChildren().add(equipPane);
    }
    
    private void createConsumableButton(HBox container, ConsumableItem consumable) {
        Rectangle addButton = new Rectangle(70, 25, Color.BLUE);
        addButton.setStroke(Color.BLACK);
        addButton.setStrokeWidth(1);
        
        Text addText = new Text("Add");
        addText.setFont(new Font(11));
        addText.setFill(Color.WHITE);
        addText.setTranslateX(22);
        addText.setTranslateY(17);
        
        StackPane addPane = new StackPane();
        addPane.getChildren().addAll(addButton, addText);
        
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
        cancelText.setTranslateX(30);
        cancelText.setTranslateY(20);
        
        StackPane cancelPane = new StackPane();
        cancelPane.getChildren().addAll(cancelButton, cancelText);
        
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
        charText.setTranslateX(80);
        charText.setTranslateY(25);
        
        StackPane charPane = new StackPane();
        charPane.getChildren().addAll(charButton, charText);
        
        charPane.setOnMouseClicked(e -> {
            if (inventory.equipItem(equipment.getId(), hero)) {
                System.out.println("Equipped " + equipment.getName() + " to " + hero.getCharacter().getName());
                refreshUI();
            } else {
                System.out.println("Cannot equip " + equipment.getName() + " to " + hero.getCharacter().getName());
            }
            
            // Remove dialog
            FXGL.getGameScene().removeUINode(dialogBackground);
            FXGL.getGameScene().removeUINode(dialogContainer);
        });
        
        container.getChildren().add(charPane);
    }
    
    
    private void addToBattleConsumables(ConsumableItem consumable) {
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
                removeText.setTranslateX(8);
                removeText.setTranslateY(17);
                
                StackPane removePane = new StackPane();
                removePane.getChildren().addAll(removeButton, removeText);
                
                final int index = i;
                removePane.setOnMouseClicked(e -> {
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
}
