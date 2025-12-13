package ui;

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
    private VBox mainContainer;
    private VBox shopItems;
    private Text goldText;
    private Text title;
    private Rectangle exitButton;
    private Text exitButtonText;
    
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
        // Main background
        background = new Rectangle(800, 600, Color.LIGHTYELLOW);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        background.setTranslateX(0);
        background.setTranslateY(0);
        
        // Main container
        mainContainer = new VBox(10);
        mainContainer.setTranslateX(50);
        mainContainer.setTranslateY(50);
        
        // Title
        title = new Text("Shop");
        title.setFont(new Font(24));
        title.setFill(Color.DARKBLUE);
        
        // Gold display
        goldText = new Text("Your Gold: " + inventory.getGold());
        goldText.setFont(new Font(16));
        goldText.setFill(Color.GOLD);
        
        // Shop items container
        shopItems = new VBox(5);
        
        // Add to main container
        mainContainer.getChildren().addAll(title, goldText, shopItems);
        
        // Exit button (top right of screen)
        exitButton = new Rectangle(40, 40, Color.rgb(200, 50, 50));
        exitButton.setStroke(Color.BLACK);
        exitButton.setStrokeWidth(1);
        exitButton.setTranslateX(750); // Top right: 800 - 40 - 10 (margin)
        exitButton.setTranslateY(10);
        
        exitButtonText = new Text("X");
        exitButtonText.setFont(new Font(20));
        exitButtonText.setFill(Color.WHITE);
        exitButtonText.setTranslateX(760); // Center the X in the button
        exitButtonText.setTranslateY(35);
        exitButtonText.setMouseTransparent(true); // Make text non-interactive
        
        // Exit button click handler
        exitButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                hide();
                if (mapUI != null) {
                    mapUI.showSelectedPath();
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
            FXGL.getGameScene().removeUINode(background);
            FXGL.getGameScene().removeUINode(mainContainer);
            FXGL.getGameScene().removeUINode(exitButton);
            FXGL.getGameScene().removeUINode(exitButtonText);
        }
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
        
        // Buy button click handler
        buyPane.setOnMouseClicked(e -> {
            if (shop.buyItem(shopItem.getItem().getId(), 1, inventory.getGold())) {
                // Add item to inventory
                inventory.addItem(shopItem.getItem(), 1);
                
                // Deduct gold
                inventory.spendGold(shopItem.getPrice());
                
                // Refresh UI
                refreshUI();
                
                System.out.println("Bought " + shopItem.getItem().getName() + " for " + shopItem.getPrice() + " gold!");
            } else {
                System.out.println("Cannot buy " + shopItem.getItem().getName() + " - not enough gold or out of stock!");
            }
        });
        
        itemContainer.getChildren().addAll(itemText, buyPane);
        shopItems.getChildren().add(itemContainer);
    }
}
