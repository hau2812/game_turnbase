package ui;

import items.StoryItem;
import items.StoryItemInventory;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.almasb.fxgl.dsl.FXGL;

import java.util.List;

/**
 * UI for Azar's Warehouse - displays story items
 */
public class WarehouseUI {
    private StoryItemInventory storyItemInventory;
    private boolean isVisible = false;
    
    // UI Components
    private Group root;
    private Rectangle background;
    private Rectangle leftPanel;
    private Rectangle rightPanel;
    private ScrollPane itemListScrollPane;
    private VBox itemListContainer;
    private VBox detailContainer;
    private Text detailTitle;
    private Text detailDescription;
    private Rectangle closeButton;
    private Text closeButtonText;
    
    // Selected item
    private StoryItem selectedItem = null;
    
    // Dimensions
    private static final double PANEL_SPACING = 20;
    private static final double CLOSE_BUTTON_HEIGHT = 40;
    private static final double PANEL_START_Y = PANEL_SPACING + CLOSE_BUTTON_HEIGHT + 10; // Start below close button
    private static final double LEFT_PANEL_WIDTH_RATIO = 0.7; // 70% of screen
    private static final double RIGHT_PANEL_WIDTH_RATIO = 0.25; // 25% of screen
    private static final double ITEM_BUTTON_HEIGHT = 50; // Half height boxes
    private static final double ITEM_BUTTON_SPACING = 10;
    private static final int ITEMS_PER_ROW = 3;
    
    public WarehouseUI(StoryItemInventory storyItemInventory) {
        this.storyItemInventory = storyItemInventory;
        initializeUI();
    }
    
    private void initializeUI() {
        root = new Group();
        
        double appWidth = FXGL.getAppWidth();
        double appHeight = FXGL.getAppHeight();
        
        // Full screen background
        background = new Rectangle(appWidth, appHeight, Color.rgb(40, 40, 40, 0.95));
        background.setStroke(Color.rgb(100, 100, 100));
        background.setStrokeWidth(3);
        
        // Title for left panel (outside the panel, at the top)
        Text leftPanelTitle = new Text("Story Items");
        leftPanelTitle.setFont(new Font(24));
        leftPanelTitle.setFill(Color.WHITE);
        leftPanelTitle.setTranslateX(PANEL_SPACING + 20);
        leftPanelTitle.setTranslateY(PANEL_SPACING + 30);
        
        // Left panel (big) - item list
        double leftPanelWidth = appWidth * LEFT_PANEL_WIDTH_RATIO;
        double leftPanelX = PANEL_SPACING;
        double leftPanelY = PANEL_START_Y;
        double leftPanelHeight = appHeight - PANEL_START_Y - PANEL_SPACING;
        
        leftPanel = new Rectangle(leftPanelWidth, leftPanelHeight, Color.rgb(60, 60, 60, 0.9));
        leftPanel.setStroke(Color.BLACK);
        leftPanel.setStrokeWidth(2);
        leftPanel.setTranslateX(leftPanelX);
        leftPanel.setTranslateY(leftPanelY);
        
        // Item list container (scrollable) - using VBox for rows, HBox for items in each row
        itemListContainer = new VBox(ITEM_BUTTON_SPACING);
        itemListContainer.setTranslateX(leftPanelX + 20);
        itemListContainer.setTranslateY(leftPanelY);
        
        itemListScrollPane = new ScrollPane(itemListContainer);
        itemListScrollPane.setPrefWidth(leftPanelWidth - 40);
        itemListScrollPane.setPrefHeight(leftPanelHeight);
        itemListScrollPane.setTranslateX(leftPanelX );
        itemListScrollPane.setTranslateY(leftPanelY-50);
        itemListScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        itemListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        itemListScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Right panel (small) - item detail
        double rightPanelWidth = appWidth * RIGHT_PANEL_WIDTH_RATIO;
        double rightPanelX = leftPanelX + leftPanelWidth + PANEL_SPACING;
        double rightPanelY = PANEL_START_Y;
        double rightPanelHeight = appHeight - PANEL_START_Y - PANEL_SPACING;
        
        rightPanel = new Rectangle(rightPanelWidth, rightPanelHeight, Color.rgb(60, 60, 60, 0.9));
        rightPanel.setStroke(Color.BLACK);
        rightPanel.setStrokeWidth(2);
        rightPanel.setTranslateX(rightPanelX);
        rightPanel.setTranslateY(rightPanelY);
        
        // Detail container
        detailContainer = new VBox(15);
        detailContainer.setTranslateX(rightPanelX + 20);
        detailContainer.setTranslateY(rightPanelY + 20);
        
        detailTitle = new Text("Select an item");
        detailTitle.setFont(new Font(20));
        detailTitle.setFill(Color.WHITE);
        detailTitle.setWrappingWidth(rightPanelWidth - 40);
        
        detailDescription = new Text("");
        detailDescription.setFont(new Font(14));
        detailDescription.setFill(Color.rgb(200, 200, 200));
        detailDescription.setWrappingWidth(rightPanelWidth - 40);
        
        detailContainer.getChildren().addAll(detailTitle, detailDescription);
        
        // Close button (top right)
        closeButton = new Rectangle(60, 40, Color.rgb(200, 50, 50));
        closeButton.setStroke(Color.BLACK);
        closeButton.setStrokeWidth(1);
        closeButton.setTranslateX(appWidth - 80);
        closeButton.setTranslateY(PANEL_SPACING);
        
        closeButtonText = new Text("Close");
        closeButtonText.setFont(new Font(14));
        closeButtonText.setFill(Color.WHITE);
        closeButtonText.setTranslateX(appWidth - 80 + 10);
        closeButtonText.setTranslateY(PANEL_SPACING + 25);
        closeButtonText.setMouseTransparent(true);
        
        closeButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                hide();
            }
        });
        
        closeButton.setOnMouseEntered(e -> closeButton.setFill(Color.rgb(220, 70, 70)));
        closeButton.setOnMouseExited(e -> closeButton.setFill(Color.rgb(200, 50, 50)));
        
        root.getChildren().addAll(
            background,
            leftPanel,
            rightPanel,
            leftPanelTitle,
            itemListScrollPane,
            detailContainer,
            closeButton,
            closeButtonText
        );
        
        updateItemList();
    }
    
    /**
     * Update the item list display
     */
    private void updateItemList() {
        itemListContainer.getChildren().clear();
        
        List<StoryItem> items = storyItemInventory.getAllStoryItems();
        
        if (items.isEmpty()) {
            Text noItemsText = new Text("No story items collected yet.");
            noItemsText.setFont(new Font(16));
            noItemsText.setFill(Color.rgb(150, 150, 150));
            itemListContainer.getChildren().add(noItemsText);
            return;
        }
        
        // Calculate item box width (3 per row with spacing)
        double availableWidth = itemListScrollPane.getPrefWidth() - 40; // Account for padding
        double itemBoxWidth = (availableWidth - (ITEM_BUTTON_SPACING * (ITEMS_PER_ROW - 1))) / ITEMS_PER_ROW;
        
        // Create rows of items
        HBox currentRow = null;
        for (int i = 0; i < items.size(); i++) {
            StoryItem item = items.get(i);
            
            // Start a new row every 3 items
            if (i % ITEMS_PER_ROW == 0) {
                currentRow = new HBox(ITEM_BUTTON_SPACING);
                itemListContainer.getChildren().add(currentRow);
            }
            
            // Create item box (square)
            Rectangle itemButton = new Rectangle(itemBoxWidth, ITEM_BUTTON_HEIGHT, Color.rgb(80, 120, 160));
            itemButton.setStroke(Color.BLACK);
            itemButton.setStrokeWidth(1);
            
            // Item name text (centered)
            Text itemText = new Text(item.getName());
            itemText.setFont(new Font(14));
            itemText.setFill(Color.WHITE);
            itemText.setWrappingWidth(itemBoxWidth - 20);
            // Center text horizontally and vertically
            double textWidth = itemText.getLayoutBounds().getWidth();
            double textHeight = itemText.getLayoutBounds().getHeight();
            itemText.setTranslateX((itemBoxWidth - textWidth) / 2);
            itemText.setTranslateY((ITEM_BUTTON_HEIGHT/2+5));
            itemText.setMouseTransparent(true);
            
            // Highlight if selected
            if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                itemButton.setFill(Color.rgb(100, 140, 180));
            }
            
            itemButton.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    selectItem(item);
                }
            });
            
            itemButton.setOnMouseEntered(e -> {
                if (selectedItem == null || !selectedItem.getId().equals(item.getId())) {
                    itemButton.setFill(Color.rgb(100, 140, 180));
                }
            });
            
            itemButton.setOnMouseExited(e -> {
                if (selectedItem == null || !selectedItem.getId().equals(item.getId())) {
                    itemButton.setFill(Color.rgb(80, 120, 160));
                }
            });
            
            Group itemGroup = new Group(itemButton, itemText);
            if (currentRow != null) {
                currentRow.getChildren().add(itemGroup);
            }
        }
    }
    
    /**
     * Select an item and show its details
     */
    private void selectItem(StoryItem item) {
        selectedItem = item;
        detailTitle.setText(item.getName());
        detailDescription.setText(item.getDescription());
        updateItemList(); // Refresh to show selection highlight
    }
    
    /**
     * Show the warehouse UI
     */
    public void show() {
        if (!isVisible) {
            isVisible = true;
            updateItemList(); // Refresh list when showing
            FXGL.getGameScene().addUINode(root);
        }
    }
    
    /**
     * Hide the warehouse UI
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(root);
        }
    }
    
    /**
     * Check if warehouse is visible
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Get the root node
     */
    public Group getRoot() {
        return root;
    }
}

