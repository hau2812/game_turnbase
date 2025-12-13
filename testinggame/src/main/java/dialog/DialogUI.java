package dialog;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.almasb.fxgl.dsl.FXGL;

import java.util.ArrayList;
import java.util.List;

/**
 * UI component for displaying dialogs at the bottom of the screen
 */
public class DialogUI {
    private Group mainContainer;
    private Rectangle dialogBox;
    private Rectangle characterImageBox;
    private ImageView characterImage;
    private Text dialogText;
    private Text speakerNameText;
    private StackPane skipButton;
    private HBox optionsContainer;
    private double optionButtonWidth;
    private List<StackPane> optionButtons;
    private List<Text> optionTexts;
    private boolean isVisible = false;
    
    // Typewriter effect
    private Timeline typewriterTimeline;
    private String fullText;
    private int currentCharIndex;
    private boolean isTextComplete;
    private boolean clickToContinueEnabled;
    
    // Dimensions
    private static final double DIALOG_BOX_HEIGHT = 150;
    private static final double CHARACTER_BOX_SIZE = 120;
    private static final double DIALOG_TEXT_X = CHARACTER_BOX_SIZE + 20;
    private static final double DIALOG_TEXT_Y = 500;
    private static final double DIALOG_TEXT_WIDTH = 600;
    private static final double OPTION_BUTTON_HEIGHT = 30;
    private static final double OPTION_SPACING = 5;
    private static final double SKIP_BUTTON_WIDTH = 60;
    private static final double SKIP_BUTTON_HEIGHT = 24;
    
    // Typewriter settings
    private static final Duration TYPEWRITER_DELAY = Duration.millis(30); // 30ms per character
    
    public DialogUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        mainContainer = new Group();
        optionButtons = new ArrayList<>();
        optionTexts = new ArrayList<>();
        
        // Main dialog box (long rectangle)
        dialogBox = new Rectangle(800, DIALOG_BOX_HEIGHT, Color.rgb(240, 240, 240));
        dialogBox.setStroke(Color.BLACK);
        dialogBox.setStrokeWidth(2);
        dialogBox.setTranslateX(0);
        dialogBox.setTranslateY(450); // Bottom of 600px screen
        
        // Character image box (small square)
        characterImageBox = new Rectangle(CHARACTER_BOX_SIZE, CHARACTER_BOX_SIZE, Color.rgb(200, 200, 200));
        characterImageBox.setStroke(Color.BLACK);
        characterImageBox.setStrokeWidth(2);
        characterImageBox.setTranslateX(10);
        characterImageBox.setTranslateY(460); // Slightly above dialog box
        
        // Character image
        characterImage = new ImageView();
        characterImage.setFitWidth(CHARACTER_BOX_SIZE - 4);
        characterImage.setFitHeight(CHARACTER_BOX_SIZE - 4);
        characterImage.setTranslateX(12);
        characterImage.setTranslateY(462);
        
        // Speaker name
        speakerNameText = new Text();
        speakerNameText.setFont(new Font(24));
        speakerNameText.setFill(Color.BLUE);
        speakerNameText.setTranslateX(DIALOG_TEXT_X);
        speakerNameText.setTranslateY(470);
        
        // Dialog text
        dialogText = new Text();
        dialogText.setFont(new Font(20));
        dialogText.setFill(Color.BLACK);
        dialogText.setWrappingWidth(DIALOG_TEXT_WIDTH);
        dialogText.setTranslateX(DIALOG_TEXT_X);
        dialogText.setTranslateY(DIALOG_TEXT_Y);
        dialogText.setMouseTransparent(true); // Make text non-interactive so it doesn't block button clicks

        
        // Options container
        optionsContainer = new HBox();
        optionsContainer.setTranslateX(DIALOG_TEXT_X);
        optionsContainer.setTranslateY(DIALOG_TEXT_Y + 60);
        optionsContainer.setSpacing(OPTION_SPACING);
        optionsContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Skip button (top-right of dialog box)
        skipButton = buildSkipButton();

        mainContainer.getChildren().addAll(
            dialogBox,
            characterImageBox,
            characterImage,
            speakerNameText,
            dialogText,
            skipButton,
            optionsContainer
        );
    }
    
    /**
     * Show a dialog with typewriter effect
     */
    public void showDialog(String speakerName, String text, String imagePath, List<DialogOption> options) {
        // Stop any existing typewriter animation
        stopTypewriter();
        
        // Set speaker name
        if (speakerName != null && !speakerName.isEmpty()) {
            speakerNameText.setText(speakerName + ":");
            speakerNameText.setVisible(true);
        } else {
            speakerNameText.setVisible(false);
        }
        
        // Store full text and reset typewriter state
        fullText = text != null ? text : "";
        currentCharIndex = 0;
        isTextComplete = false;
        dialogText.setText(""); // Start with empty text
        
        // Set character image
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Image img = FXGL.image(imagePath);
                characterImage.setImage(img);
                characterImage.setVisible(true);
            } catch (Exception e) {
                characterImage.setVisible(false);
            }
        } else {
            characterImage.setVisible(false);
        }
        
        // Clear previous options
        clearOptions();
        
        // Show UI
        if (!isVisible) {
            isVisible = true;
            FXGL.getGameScene().addUINode(mainContainer);
        }
        
        // Add options immediately (they can be clicked even during typewriter)
        if (options != null && !options.isEmpty()) {
            // Compute width per option (split the available width)
            optionButtonWidth = (DIALOG_TEXT_WIDTH - (options.size() - 1) * OPTION_SPACING) / options.size();
            if (optionButtonWidth < 60) { // safety minimum
                optionButtonWidth = 60;
            }
            for (int i = 0; i < options.size(); i++) {
                DialogOption option = options.get(i);
                createOptionButton(i, option.getText());
            }
        } else {
            optionButtonWidth = DIALOG_TEXT_WIDTH;
        }
        
        // Start typewriter effect
        startTypewriter();
    }
    
    /**
     * Start the typewriter animation
     */
    private void startTypewriter() {
        if (fullText == null || fullText.isEmpty()) {
            isTextComplete = true;
            return;
        }
        
        // Stop any existing animation
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
        }
        
        currentCharIndex = 0;
        isTextComplete = false;
        
        // Create timeline with one keyframe that repeats
        typewriterTimeline = new Timeline(
            new KeyFrame(TYPEWRITER_DELAY, e -> {
                if (currentCharIndex < fullText.length()) {
                    dialogText.setText(fullText.substring(0, currentCharIndex + 1));
                    currentCharIndex++;
                } else {
                    completeTypewriter();
                }
            })
        );
        
        typewriterTimeline.setCycleCount(fullText.length());
        typewriterTimeline.setOnFinished(e -> completeTypewriter());
        typewriterTimeline.play();
    }
    
    /**
     * Stop the typewriter animation and show all text
     */
    private void stopTypewriter() {
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
            typewriterTimeline = null;
        }
        
        // Show all text immediately
        if (fullText != null) {
            dialogText.setText(fullText);
            isTextComplete = true;
        }
    }
    
    /**
     * Complete the typewriter animation
     */
    private void completeTypewriter() {
        isTextComplete = true;
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
            typewriterTimeline = null;
        }
    }
    
    
    /**
     * Create an option button
     */
    private void createOptionButton(int index, String optionText) {
        Rectangle bg = new Rectangle(optionButtonWidth, OPTION_BUTTON_HEIGHT, Color.rgb(220, 220, 220));
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1);

        Text text = new Text(optionText);
        text.setFont(new Font(12));
        text.setFill(Color.BLACK);

        StackPane optionPane = new StackPane();
        optionPane.setPrefWidth(optionButtonWidth);
        optionPane.setPrefHeight(OPTION_BUTTON_HEIGHT);
        optionPane.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        StackPane.setMargin(text, new Insets(0, 0, 0, 8));

        optionPane.getChildren().addAll(bg, text);

        final int optionIndex = index;
        optionPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                DialogSystem.getInstance().selectOption(optionIndex);
            }
        });

        // Hover effect
        optionPane.setOnMouseEntered(e -> bg.setFill(Color.rgb(200, 200, 200)));
        optionPane.setOnMouseExited(e -> bg.setFill(Color.rgb(220, 220, 220)));

        optionButtons.add(optionPane);
        optionTexts.add(text);
        optionsContainer.getChildren().add(optionPane);
    }

    private StackPane buildSkipButton() {
        Rectangle bg = new Rectangle(SKIP_BUTTON_WIDTH, SKIP_BUTTON_HEIGHT, Color.rgb(210, 210, 210));
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1);

        Text text = new Text("Skip");
        text.setFont(new Font(12));
        text.setFill(Color.BLACK);

        StackPane button = new StackPane(bg, text);
        button.setPrefSize(SKIP_BUTTON_WIDTH, SKIP_BUTTON_HEIGHT);
        button.setAlignment(Pos.CENTER);
        button.setTranslateX(dialogBox.getTranslateX() + 800 - SKIP_BUTTON_WIDTH - 10);
        button.setTranslateY(dialogBox.getTranslateY() + 5);

        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Reveal current text then skip until a choice is found
                revealFullText();
                dialog.DialogSystem.getInstance().skipUntilChoice();
            }
        });

        button.setOnMouseEntered(e -> bg.setFill(Color.rgb(190, 190, 190)));
        button.setOnMouseExited(e -> bg.setFill(Color.rgb(210, 210, 210)));

        return button;
    }
    
    /**
     * Clear all options
     */
    private void clearOptions() {
        for (StackPane button : optionButtons) {
            optionsContainer.getChildren().remove(button);
        }
        optionButtons.clear();
        optionTexts.clear();
    }
    
    /**
     * Hide the dialog UI
     */
    public void hide() {
        // Stop typewriter animation when hiding
        stopTypewriter();
        
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(mainContainer);
        }
    }
    
    /**
     * Check if dialog UI is visible
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Set up click to continue (when no options)
     */
    public void setClickToContinue(boolean enabled) {
        clickToContinueEnabled = enabled;
        if (enabled) {
            dialogBox.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    handleDialogClick();
                }
            });
        } else {
            dialogBox.setOnMouseClicked(null);
        }
    }
    
    /**
     * Reveal full text immediately (exposed for skip button / system)
     */
    public void revealFullText() {
        stopTypewriter();
    }

    /**
     * Handle click on dialog box
     * If text is still animating, skip to show all text
     * If text is complete, continue to next dialog
     */
    private void handleDialogClick() {
        if (!isTextComplete) {
            // Skip typewriter animation and show all text
            stopTypewriter();
        } else {
            // Text is complete, continue to next dialog if enabled
            if (clickToContinueEnabled) {
                DialogSystem.getInstance().continueDialog();
            }
        }
    }
}

