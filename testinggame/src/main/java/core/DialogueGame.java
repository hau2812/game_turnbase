package core;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import event.DialogueEvent.DialogueOption;

import static com.almasb.fxgl.dsl.FXGL.*;

public class DialogueGame {
    // Remove GameApplication inheritance since we're using this as a component

    private static final double TYPE_SPEED_SEC = 0.02;

    private Text dialogueText;
    private Text hintText;
    private HBox optionsRow;

    private final List<String> script = new ArrayList<>();
    private int index = 0;
    private String fullText = "";
    private String visibleText = "";
    private boolean isTyping = false;
    private boolean showingOptions = false;
    private Timeline typerTimeline;
    private int optionIndex = -1;
    private Map<String, DialogueOption> dialogueOptions = new HashMap<>();
    private Runnable onDialogueComplete;
    private boolean isActive = false;
    private Group dialogueGroup;
    private boolean isInitialized = false;

    public DialogueGame() {
        // Don't initialize UI components in constructor
        // They will be initialized when first needed
    }

    // New method to initialize UI components
    private void initializeUI() {
        if (isInitialized) {
            return; // Already initialized
        }

        Rectangle charCard = new Rectangle(220, 200, Color.web("#2b2f31"));
        charCard.setArcWidth(16);
        charCard.setArcHeight(16);
        charCard.setTranslateX(40);
        charCard.setTranslateY(80);

        Text charLabel = new Text("Character");
        charLabel.setFill(Color.LIGHTGRAY);
        charLabel.setFont(Font.font(18));
        charLabel.setTranslateX(100);
        charLabel.setTranslateY(185);

        Rectangle dialogBox = new Rectangle(780, 180, Color.web("#1e2123", 0.95));
        dialogBox.setArcWidth(18);
        dialogBox.setArcHeight(18);
        dialogBox.setStroke(Color.web("#5aa0ff"));
        dialogBox.setStrokeWidth(2);
        dialogBox.setTranslateX(10);
        dialogBox.setTranslateY(420);

        dialogueText = new Text();
        dialogueText.setWrappingWidth(780 - 32);
        dialogueText.setFill(Color.WHITE);
        dialogueText.setFont(Font.font(18));
        dialogueText.setTranslateX(26);
        dialogueText.setTranslateY(446);

        hintText = new Text("");
        hintText.setFill(Color.web("#b8ccff"));
        hintText.setFont(Font.font(14));
        hintText.setTranslateX(650);
        hintText.setTranslateY(585);

        optionsRow = new HBox(16);
        optionsRow.setAlignment(Pos.CENTER);
        optionsRow.setTranslateX(200);
        optionsRow.setTranslateY(540);

        dialogueGroup = new Group(charCard, charLabel, dialogBox, dialogueText, hintText, optionsRow);
        dialogueGroup.setVisible(false);

        // Add to game scene
        getGameScene().addUINode(dialogueGroup);

        // Add input handlers
        onBtnDown(MouseButton.PRIMARY, this::handleTap);
        onKeyDown(KeyCode.ENTER, this::handleTap);
        onKeyDown(KeyCode.SPACE, this::handleTap);

        isInitialized = true;

        // Don't load default script here - let the DialogueEvent provide the script
        // The default script was overriding the actual event dialogues
    }

    // New method to load dialogue from MapNode events
    public void loadDialogue(List<String> newScript, int newOptionIndex, Map<String, DialogueOption> options) {
        script.clear();
        script.addAll(newScript);
        dialogueOptions.clear();
        dialogueOptions.putAll(options);
        optionIndex = newOptionIndex;
        index = 0;
        resetDialogue();
    }

    public void startDialogue() {
        // Make sure the UI is initialized before attempting to show it
        if (!isInitialized) {
            initializeUI();
        }

        dialogueGroup.setVisible(true);
        isActive = true;
        if (script.size() > 0) {
            startTyping(script.get(index));
        }
    }

    public void endDialogue() {
        isActive = false;
        if (dialogueGroup != null) {
            dialogueGroup.setVisible(false);
        }
        if (onDialogueComplete != null) {
            onDialogueComplete.run();
        }
    }

    public void setOnDialogueComplete(Runnable onComplete) {
        this.onDialogueComplete = onComplete;
    }

    public boolean isDialogueActive() {
        return isActive;
    }

    private void resetDialogue() {
        stopTyping();
        fullText = "";
        visibleText = "";
        isTyping = false;
        showingOptions = false;
    }

    private void handleTap() {
        if (!isActive) return;
        if (showingOptions) return;
        if (isTyping) finishTyping();
        else next();
    }

    private void next() {
        if (index == optionIndex && !showingOptions) {
            showOptions(true);
            return;
        }
        index++;
        if (index >= script.size()) {
            endDialogue();
            return;
        }
        showOptions(false);
        startTyping(script.get(index));
    }

    private void startTyping(String text) {
        stopTyping();
        fullText = text;
        visibleText = "";
        isTyping = true;
        dialogueText.setText("");
        hintText.setText("");

        typerTimeline = new Timeline();
        for (int i = 0; i < fullText.length(); i++) {
            final int charIndex = i;
            typerTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i * TYPE_SPEED_SEC),
                            e -> {
                                visibleText = fullText.substring(0, charIndex + 1);
                                dialogueText.setText(visibleText);
                                if (charIndex == fullText.length() - 1) finishTyping();
                            })
            );
        }
        typerTimeline.play();
    }

    private void finishTyping() {
        stopTyping();
        visibleText = fullText;
        dialogueText.setText(visibleText);
        isTyping = false;
        if (!showingOptions) hintText.setText("Click / tap to continue…");
    }

    private void stopTyping() {
        if (typerTimeline != null) {
            typerTimeline.stop();
            typerTimeline = null;
        }
    }

    private void showOptions(boolean show) {
        showingOptions = show;
        optionsRow.getChildren().clear();
        if (!show) return;

        hintText.setText("Choose an action");

        if (dialogueOptions.isEmpty()) {
            // Default options if none provided
            createOptionButton("Ask about the ruins");
            createOptionButton("Look for supplies");
            createOptionButton("Set up camp");
        } else {
            // Use the options from the dialogue event
            for (String optionText : dialogueOptions.keySet()) {
                createOptionButton(optionText);
            }
        }
    }

    private void createOptionButton(String text) {
        Button b = getUIFactoryService().newButton(text);
        b.setPrefWidth(200);
        b.setPrefHeight(40);

        // Ensure consistent styling for all buttons
        b.setStyle("-fx-background-color: #5aa0ff; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-opacity: 1.0;");

        // Hover effect
        b.setOnMouseEntered(e -> {
            b.setStyle("-fx-background-color: #7ab8ff; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-radius: 5; " +
                    "-fx-opacity: 1.0;");
        });

        b.setOnMouseExited(e -> {
            b.setStyle("-fx-background-color: #5aa0ff; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-radius: 5; " +
                    "-fx-opacity: 1.0;");
        });

        b.setOnAction(e -> onChoose(text));
        optionsRow.getChildren().add(b);
    }

    private void onChoose(String option) {
        // First check if this is a custom dialogue option
        if (dialogueOptions.containsKey(option)) {
            DialogueOption dialogueOption = dialogueOptions.get(option);
            dialogueOption.execute();
        }

        // For backward compatibility with the demo version
        String branch = switch (option) {
            case "Ask about the ruins" -> "You : Tell me about those ruins—who built them?";
            case "Look for supplies" -> "You : Let's scavenge first. No point marching in hungry.";
            default -> "You : We set up camp here. We move at first light.";
        };

        if (index == optionIndex) {
            script.add(index + 1, branch);
        }

        showOptions(false);
        next();
    }
}
