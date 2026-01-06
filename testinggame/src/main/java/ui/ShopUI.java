package ui;

import org.example.testing;
import shop.Shop;
import items.*;
import map.MapUI;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseButton;
import com.almasb.fxgl.dsl.FXGL;

import java.util.List;

/**
 * UI for the shop system
 */
public class ShopUI {
    private Shop shop;
    private Inventory inventory;
    private MapUI mapUI;
    private boolean isVisible = false;
    
    // UI Components
    private Rectangle background;
    private HBox mainContainer; // Changed from VBox to HBox to accommodate left and right panels
    private VBox leftPanel; // Left panel for shop items list
    private VBox shopItems;
    private VBox rightPanel; // Right panel for item details
    private Text goldText;
    private Text title;
    private Rectangle exitButton;
    private Text exitButtonText;
    
    // Item detail panel components
    private Rectangle detailBackground;
    private VBox detailContainer;
    private Text detailTitle;
    private Text detailDescription;
    private Text detailStats;
    private Text detailPrice;
    private Rectangle confirmBuyButton;
    private Text confirmBuyText;
    private StackPane confirmBuyPane;
    private Shop.ShopItem selectedShopItem; // Currently selected item for detail view
    
    public ShopUI(Shop shop, Inventory inventory) {
        this.shop = shop;
        this.inventory = inventory;
        initializeUI();
    }
    
    /**
     * Set the MapUI reference for the exit button
     */
    public void setMapUI(MapUI mapUI) {
        this.mapUI = mapUI;
    }
    
    private void initializeUI() {
        // Main background - wider to accommodate detail panel
        background = new Rectangle(1000, 600, Color.LIGHTYELLOW);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        background.setTranslateX(0);
        background.setTranslateY(0);
        
        // Main container - horizontal layout
        mainContainer = new HBox(20);
        mainContainer.setTranslateX(50);
        mainContainer.setTranslateY(50);
        
        // Left panel for shop items
        leftPanel = new VBox(10);
        
        // Title
        title = new Text("Shop");
        title.setFont(new Font(24));
        title.setFill(Color.DARKBLUE);
        
        // Gold display
        goldText = new Text("Your Gold: " + inventory.getGold());
        goldText.setFont(new Font(16));
        goldText.setFill(Color.DARKGOLDENROD);
        
        // Shop items container
        shopItems = new VBox(5);
        
        // Add to left panel
        leftPanel.getChildren().addAll(title, goldText, shopItems);
        
        // Right panel for item details (initially hidden)
        rightPanel = new VBox(15);
        rightPanel.setPrefWidth(350);
        
        // Detail background
        detailBackground = new Rectangle(350, 500, Color.rgb(250, 250, 250));
        detailBackground.setStroke(Color.BLACK);
        detailBackground.setStrokeWidth(2);
        detailBackground.setVisible(false);
        
        // Detail container
        detailContainer = new VBox(10);
        detailContainer.setTranslateX(10);
        detailContainer.setTranslateY(10);
        
        // Detail title
        detailTitle = new Text("");
        detailTitle.setFont(new Font(20));
        detailTitle.setFill(Color.BLACK);
        detailTitle.setWrappingWidth(330);
        
        // Detail description
        detailDescription = new Text("");
        detailDescription.setFont(new Font(14));
        detailDescription.setFill(Color.GRAY);
        detailDescription.setWrappingWidth(330);
        
        // Detail stats
        detailStats = new Text("");
        detailStats.setFont(new Font(14));
        detailStats.setFill(Color.BLUE);
        detailStats.setWrappingWidth(330);
        
        // Detail price
        detailPrice = new Text("");
        detailPrice.setFont(new Font(16));
        detailPrice.setFill(Color.DARKGOLDENROD);
        detailPrice.setWrappingWidth(330);
        
        // Confirm buy button
        confirmBuyButton = new Rectangle(120, 35, Color.GREEN);
        confirmBuyButton.setStroke(Color.BLACK);
        confirmBuyButton.setStrokeWidth(1);
        
        confirmBuyText = new Text("Confirm Buy");
        confirmBuyText.setFont(new Font(12));
        confirmBuyText.setFill(Color.WHITE);
        confirmBuyText.setMouseTransparent(true);
        
        confirmBuyPane = new StackPane();
        confirmBuyPane.getChildren().addAll(confirmBuyButton, confirmBuyText);
        confirmBuyPane.setPrefSize(120, 35);
        
        // Confirm buy button click handler
        confirmBuyPane.setOnMouseClicked(e -> {
            if (selectedShopItem != null) {
                if (shop.buyItem(selectedShopItem.getItem().getId(), 1, inventory.getGold())) {
                    // Add item to inventory
                    inventory.addItem(selectedShopItem.getItem(), 1);
                    
                    // Deduct gold
                    inventory.spendGold(selectedShopItem.getPrice());
                    
                    // Hide detail panel
                    hideItemDetail();
                    
                    // Refresh UI
                    refreshUI();
                    
                    //System.out.println("Bought " + selectedShopItem.getItem().getName() + " for " + selectedShopItem.getPrice() + " gold!");
                } else {
                    //System.out.println("Cannot buy " + selectedShopItem.getItem().getName() + " - not enough gold or out of stock!");
                }
            }
        });
        
        // Hover effects will be set conditionally in showItemDetail() based on buyability
        
        // Add to detail container
        detailContainer.getChildren().addAll(detailTitle, detailDescription, detailStats, detailPrice, confirmBuyPane);
        
        // Add panels to main container
        mainContainer.getChildren().addAll(leftPanel, rightPanel);
        
        // Exit button (top right of screen) - positioned to be visible on 800px screen
        exitButton = new Rectangle(40, 40, Color.rgb(200, 50, 50));
        exitButton.setStroke(Color.BLACK);
        exitButton.setStrokeWidth(1);
        // Position at top right of visible screen: screen width (800) - button width (40) - margin (10)
        exitButton.setTranslateX(750);
        exitButton.setTranslateY(10);
        
        exitButtonText = new Text("X");
        exitButtonText.setFont(new Font(20));
        exitButtonText.setFill(Color.WHITE);
        exitButtonText.setMouseTransparent(true); // Make text non-interactive
        // Center the X in the button: button X + half button width - half text width (approx)
        exitButtonText.setTranslateX(760);
        exitButtonText.setTranslateY(35);
        exitButtonText.setMouseTransparent(true); // Make text non-interactive
        
        // Exit button click handler
        exitButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                hide();

                if (mapUI != null) {
                    mapUI.showSelectedPath();
                    testing.inMapMode = true;
                }
            }
        });
        
        // Hover effects
        exitButton.setOnMouseEntered(e -> exitButton.setFill(Color.rgb(220, 70, 70)));
        exitButton.setOnMouseExited(e -> exitButton.setFill(Color.rgb(200, 50, 50)));
        
        // Initially hidden
        hide();
    }
    
    public void show() {
        if (!isVisible) {
            isVisible = true;
            refreshUI();
            hideItemDetail(); // Ensure detail panel is hidden when showing shop
            // Add to UI layer to ensure it's on top
            FXGL.getGameScene().addUINode(background);
            FXGL.getGameScene().addUINode(mainContainer);
            FXGL.getGameScene().addUINode(exitButton);
            FXGL.getGameScene().addUINode(exitButtonText);
            System.out.println("Shop UI shown! Background: " + background + ", Container: " + mainContainer);
        }
    }
    
    public void hide() {
        if (isVisible) {
            isVisible = false;
            hideItemDetail(); // Hide detail panel when hiding shop
            FXGL.getGameScene().removeUINode(background);
            FXGL.getGameScene().removeUINode(mainContainer);
            FXGL.getGameScene().removeUINode(exitButton);
            FXGL.getGameScene().removeUINode(exitButtonText);
        }
    }
    
    /**
     * Show item detail panel on the right
     */
    private void showItemDetail(Shop.ShopItem shopItem) {
        selectedShopItem = shopItem;
        Item item = shopItem.getItem();
        
        // Set detail title
        detailTitle.setText(item.getName());
        
        // Set detail description
        detailDescription.setText(item.getDescription());
        
        // Set detail stats based on item type
        if (item.getItemType() == Item.ItemType.EQUIPMENT) {
            EquipmentItem equipment = (EquipmentItem) item;
            detailStats.setText("Slot: " + equipment.getSlot().getDisplayName() + 
                              "\n" + equipment.getStatBonus().toString());
            detailStats.setFill(Color.BLUE);
        } else if (item.getItemType() == Item.ItemType.CONSUMABLE) {
            ConsumableItem consumable = (ConsumableItem) item;
            detailStats.setText("Effect: " + consumable.getEffectDescription() + 
                              "\nValue: " + consumable.getEffectValue());
            detailStats.setFill(Color.GREEN);
        } else {
            detailStats.setText("");
        }
        
        // Set price
        detailPrice.setText("Price: " + shopItem.getPrice() + " gold");
        
        // Check if item is buyable (player has enough gold and item is in stock)
        boolean isBuyable = shopItem.getQuantity() > 0 && inventory.getGold() >= shopItem.getPrice();
        
        // Update button appearance and hover effects based on buyability
        if (isBuyable) {
            detailPrice.setFill(Color.DARKGOLDENROD);
            confirmBuyButton.setFill(Color.GREEN);
            confirmBuyText.setFill(Color.WHITE);
            
            // Enable hover effects only when buyable
            confirmBuyPane.setOnMouseEntered(e -> confirmBuyButton.setFill(Color.rgb(0, 200, 0)));
            confirmBuyPane.setOnMouseExited(e -> confirmBuyButton.setFill(Color.GREEN));
        } else {
            detailPrice.setFill(Color.RED);
            confirmBuyButton.setFill(Color.GRAY);
            confirmBuyText.setFill(Color.DARKGRAY);
            
            // Disable hover effects when not buyable
            confirmBuyPane.setOnMouseEntered(e -> {}); // Do nothing on hover
            confirmBuyPane.setOnMouseExited(e -> {}); // Do nothing on exit
        }
        
        // Show detail panel
        detailBackground.setVisible(true);
        detailContainer.setVisible(true);
        
        // Add to scene if not already added
        if (!FXGL.getGameScene().getUINodes().contains(detailBackground)) {
            FXGL.getGameScene().addUINode(detailBackground);
            FXGL.getGameScene().addUINode(detailContainer);
        }
        
        // Position detail panel on the right
        detailBackground.setTranslateX(550);
        detailBackground.setTranslateY(50);
        detailContainer.setTranslateX(560);
        detailContainer.setTranslateY(60);
    }
    
    /**
     * Hide item detail panel
     */
    private void hideItemDetail() {
        selectedShopItem = null;
        detailBackground.setVisible(false);
        detailContainer.setVisible(false);
        FXGL.getGameScene().removeUINode(detailBackground);
        FXGL.getGameScene().removeUINode(detailContainer);
    }
    
    public void toggle() {
        if (isVisible) {
            hide();

        } else {
            show();
        }
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void refreshUI() {
        refreshGold();
        refreshShopItems();
    }
    
    private void refreshGold() {
        goldText.setText("Your Gold: " + inventory.getGold());
    }
    
    private void refreshShopItems() {
        shopItems.getChildren().clear();
        
        List<Shop.ShopItem> availableItems = shop.getAvailableItems();
        for (Shop.ShopItem shopItem : availableItems) {
            if (shopItem.getQuantity() > 0) {
                createShopItemDisplay(shopItem);
            }
        }
    }
    
    private void createShopItemDisplay(Shop.ShopItem shopItem) {
        HBox itemContainer = new HBox(10);
        
        // Item info
        Text itemText = new Text(shopItem.getItem().getName() + " x" + shopItem.getQuantity() + 
                                " - " + shopItem.getPrice() + " gold");
        itemText.setFont(new Font(12));
        itemText.setFill(Color.BLACK);
        
        // Buy button
        Rectangle buyButton = new Rectangle(60, 25, Color.GREEN);
        buyButton.setStroke(Color.BLACK);
        buyButton.setStrokeWidth(1);
        
        Text buyText = new Text("Buy");
        buyText.setFont(new Font(10));
        buyText.setFill(Color.WHITE);
        buyText.setTranslateX(20);
        buyText.setTranslateY(18);
        
        StackPane buyPane = new StackPane();
        buyPane.getChildren().addAll(buyButton, buyText);
        
        // Buy button click handler - show item details instead of buying immediately
        buyPane.setOnMouseClicked(e -> {
            showItemDetail(shopItem);
        });
        
        // Hover effect for buy button
        buyPane.setOnMouseEntered(e -> buyButton.setFill(Color.rgb(0, 200, 0)));
        buyPane.setOnMouseExited(e -> buyButton.setFill(Color.GREEN));
        
        itemContainer.getChildren().addAll(itemText, buyPane);
        shopItems.getChildren().add(itemContainer);
    }
}
