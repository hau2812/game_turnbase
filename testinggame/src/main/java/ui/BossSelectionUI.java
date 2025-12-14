package ui;

import dialog.DialogRegistrations;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.almasb.fxgl.dsl.FXGL;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for selecting custom boss fights
 */
public class BossSelectionUI {
    private Rectangle background;
    private Group mainContainer;
    private Text title;
    private Text instruction;
    private List<CheckBox> bossCheckboxes;
    private CheckBox easyModeCheckbox;
    private CheckBox skipToBossCheckbox;
    private Button continueButton;
    private boolean isVisible = false;
    private Runnable onContinueCallback;
    
    private static final String[] BOSS_OPTIONS = {
        "Oufuu Family",
        "Flamita Boss",
        "Mabel Boss"
    };
    
    public BossSelectionUI() {
        this.bossCheckboxes = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        mainContainer = new Group();
        
        // Background (made taller to fit easy mode and skip to boss options)
        background = new Rectangle(500, 500, Color.rgb(240, 240, 240));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(3);
        background.setTranslateX(150);
        background.setTranslateY(50);
        background.setArcWidth(10);
        background.setArcHeight(10);
        
        // Title
        title = new Text("Select game mode(optional)");
        title.setFont(new Font(20));
        title.setFill(Color.BLACK);
        title.setTranslateX(250);
        title.setTranslateY(90);
        

//        instruction = new Text("Select game mode");
//        instruction.setFont(new Font(14));
//        instruction.setFill(Color.BLACK);
//        instruction.setTranslateX(200);
//        instruction.setTranslateY(170);
        mainContainer.getChildren().addAll(background, title);
        // Create boss checkboxes
        double startY = 220;
        double spacing = 50;
        
//        for (int i = 0; i < BOSS_OPTIONS.length; i++) {
//            CheckBox checkbox = new CheckBox(BOSS_OPTIONS[i]);
//            checkbox.setFont(new Font(16));
//            checkbox.setTranslateX(200);
//            checkbox.setTranslateY(startY + i * spacing);
//            bossCheckboxes.add(checkbox);
//            mainContainer.getChildren().add(checkbox);
//        }
        
        // Easy Mode checkbox
        easyModeCheckbox = new CheckBox("Easy Mode");
        easyModeCheckbox.setFont(new Font(16));
        easyModeCheckbox.setTranslateX(200);
        easyModeCheckbox.setTranslateY(120);
        mainContainer.getChildren().add(easyModeCheckbox);
        
        // Skip to Boss checkbox
        skipToBossCheckbox = new CheckBox("Skip to Boss");
        skipToBossCheckbox.setFont(new Font(16));
        skipToBossCheckbox.setTranslateX(200);
        skipToBossCheckbox.setTranslateY(150);
        mainContainer.getChildren().add(skipToBossCheckbox);
        
        // Continue button
        continueButton = new Button("Continue to Map");
        continueButton.setPrefWidth(200);
        continueButton.setPrefHeight(50);
        continueButton.setTranslateX(300);
        continueButton.setTranslateY(470);
        continueButton.setFont(new Font(18));
        continueButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        continueButton.setOnAction(e -> {
            if (onContinueCallback != null) {
                onContinueCallback.run();
            }
        });
        mainContainer.getChildren().add(continueButton);
        

    }
    
    public void setOnContinue(Runnable callback) {
        this.onContinueCallback = callback;
    }
    
    public boolean isOufuuSelected() {
        return bossCheckboxes.get(0).isSelected();
    }
    
    public boolean isFlamitaSelected() {
        return bossCheckboxes.get(1).isSelected();
    }
    
    public boolean isMabelSelected() {
        return bossCheckboxes.get(2).isSelected();
    }
    
    public boolean isEasyModeSelected() {
        return easyModeCheckbox != null && easyModeCheckbox.isSelected();
    }
    
    public boolean isSkipToBossSelected() {
        return skipToBossCheckbox != null && skipToBossCheckbox.isSelected();
    }
    
    public void show() {
        if (!isVisible) {
            isVisible = true;
            FXGL.getGameScene().addUINode(mainContainer);
            System.out.println("Boss Selection UI shown!");
        }
    }
    
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(mainContainer);
            System.out.println("Boss Selection UI hidden!");

        }
    }
    
    public boolean isVisible() {
        return isVisible;
    }
}

