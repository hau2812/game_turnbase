package ui;

import dialog.DialogRegistrations;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.almasb.fxgl.dsl.FXGL;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for selecting heroes before starting the game
 */
public class HeroSelectionUI {
    private Rectangle background;
    private Group mainContainer;
    private Text title;
    private Text instruction;
    private Text selectedCount;
    private List<Button> heroButtons;
    private List<String> selectedHeroes;
    private String[] availableHeroes;
    private Button startButton;
    private boolean isVisible = false;
    private Runnable onStartCallback;
    
    public HeroSelectionUI(String[] availableHeroes) {
        this.availableHeroes = availableHeroes;
        this.selectedHeroes = new ArrayList<>();
        this.heroButtons = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        mainContainer = new Group();
        
        // Background
        background = new Rectangle(600, 500, Color.rgb(240, 240, 240));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(3);
        background.setTranslateX(100);
        background.setTranslateY(50);
        background.setArcWidth(10);
        background.setArcHeight(10);
        
        // Title
        title = new Text("Select Your Heroes");
        title.setFont(new Font(24));
        title.setFill(Color.BLACK);
        title.setTranslateX(350);
        title.setTranslateY(90);
        
        // Instruction
        instruction = new Text("Select up to 3 heroes (Click to toggle)");
        instruction.setFont(new Font(14));
        instruction.setFill(Color.BLACK);
        instruction.setTranslateX(250);
        instruction.setTranslateY(120);
        
        // Selected count
        selectedCount = new Text("Selected: 0/3");
        selectedCount.setFont(new Font(16));
        selectedCount.setFill(Color.BLACK);
        selectedCount.setTranslateX(490);
        selectedCount.setTranslateY(120);

        mainContainer.getChildren().addAll(background, title, instruction, selectedCount);
        // Create hero buttons
        double startX = 150;
        double startY = 160;
        double buttonWidth = 150;
        double buttonHeight = 40;
        double spacing = 20;
        int heroesPerColumn = 5;
        
        for (int i = 0; i < availableHeroes.length; i++) {
            String heroName = availableHeroes[i];
            
            // Calculate column (every 5 heroes move to next column)
            int column = i / heroesPerColumn;
            // Calculate row within column (0-4)
            int row = i % heroesPerColumn;
            
            // Calculate X position: startX + (column * (buttonWidth + spacing))
            double x = startX + column * (buttonWidth + spacing);
            // Calculate Y position: startY + (row * (buttonHeight + spacing))
            double y = startY + row * (buttonHeight + spacing);
            
            Button heroButton = createHeroButton(heroName, x, y, buttonWidth, buttonHeight);
            heroButtons.add(heroButton);
            mainContainer.getChildren().add(heroButton);
        }
        
        // Start button
        startButton = new Button("Start Game");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);
        startButton.setTranslateX(300);
        startButton.setTranslateY(450);
        startButton.setFont(new Font(18));
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setOnAction(e -> {
            if (selectedHeroes.size() > 0) {
                if (onStartCallback != null) {
                    onStartCallback.run();
                }
            }
        });
        mainContainer.getChildren().add(startButton);
        
        updateStartButton();
        

    }
    
    private Button createHeroButton(String heroName, double x, double y, double width, double height) {
        Button button = new Button(heroName);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setTranslateX(x);
        button.setTranslateY(y);
        button.setFont(new Font(14));
        button.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: black;");

        button.setOnAction(e -> {
            toggleHeroSelection(heroName, button);
        });
        
        return button;
    }
    
    private void toggleHeroSelection(String heroName, Button button) {
        if (selectedHeroes.contains(heroName)) {
            // Deselect
            selectedHeroes.remove(heroName);
            button.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: black;");
        } else {
            // Select (if not at max)
            if (selectedHeroes.size() < 3) {
                selectedHeroes.add(heroName);
                button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
        
        updateSelectedCount();
        updateStartButton();
    }
    
    private void updateSelectedCount() {
        selectedCount.setText("Selected: " + selectedHeroes.size() + "/3");
    }
    
    private void updateStartButton() {
        if (selectedHeroes.size() > 0) {
            startButton.setDisable(false);
            startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            startButton.setDisable(true);
            startButton.setStyle("-fx-background-color: #CCCCCC; -fx-text-fill: #666666;");
        }
    }
    
    public void setOnStart(Runnable callback) {
        this.onStartCallback = callback;
    }
    
    public String[] getSelectedHeroes() {
        return selectedHeroes.toArray(new String[0]);
    }
    
    public void show() {
        if (!isVisible) {
            isVisible = true;
            FXGL.getGameScene().addUINode(mainContainer);
            System.out.println("Hero Selection UI shown!");
        }
    }
    
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(mainContainer);

            System.out.println("Hero Selection UI hidden!");
        }
    }
    
    public boolean isVisible() {
        return isVisible;
    }
}

