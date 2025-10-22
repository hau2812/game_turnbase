package ui;

import shop.Shop;
import items.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import com.almasb.fxgl.dsl.FXGL;

import java.util.List;

/**
 * UI for the shop system
 */
public class ShopUI {
    private Shop shop;
    private Inventory inventory;
    private boolean isVisible = false;
    
    // UI Components
    private Rectangle background;
    private VBox mainContainer;
    private VBox shopItems;
    private Text goldText;
    private Text title;
    
    public ShopUI(Shop shop, Inventory inventory) {
        this.shop = shop;
        this.inventory = inventory;
        initializeUI();
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
            System.out.println("Shop UI shown! Background: " + background + ", Container: " + mainContainer);
        }
    }
    
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(background);
            FXGL.getGameScene().removeUINode(mainContainer);
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
