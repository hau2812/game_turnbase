package ui;

import com.almasb.fxgl.dsl.FXGL;
import core.DialogueGame;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import event.DialogueEvent;

import java.util.Map;

/**
 * UI component for displaying dialogues in the game
 * Separated from MapUI for better modularity
 */
public class DialogueUI {
    private DialogueGame dialogueGame;
    private Group dialogueContainer;
    private Rectangle charCard;
    private Text charLabel;
    private Rectangle dialogBox;
    private Text dialogueText;
    private Text hintText;
    private HBox optionsRow;
    private boolean isVisible;
    private Runnable onDialogueComplete;
    private Runnable onShowCallback; // Callback to hide map when dialogue shows
    private Runnable onHideCallback; // Callback to restore map when dialogue hides

    public DialogueUI() {
        this.dialogueGame = new DialogueGame();
        this.isVisible = false;
        initializeUI();
    }

    private void initializeUI() {
        // Character card (left side) - will be hidden for now
        charCard = new Rectangle(220, 200, Color.web("#2b2f31"));
        charCard.setArcWidth(16);
        charCard.setArcHeight(16);
        charCard.setTranslateX(40);
        charCard.setTranslateY(80);
        charCard.setVisible(false); // Hide the character card

        charLabel = new Text("Character");
        charLabel.setFill(Color.LIGHTGRAY);
        charLabel.setFont(Font.font(18));
        charLabel.setTranslateX(100);
        charLabel.setTranslateY(185);
        charLabel.setVisible(false); // Hide the character label

        // Dialogue box (bottom)
        dialogBox = new Rectangle(780, 180, Color.web("#1e2123", 0.95));
        dialogBox.setArcWidth(18);
        dialogBox.setArcHeight(18);
        dialogBox.setStroke(Color.web("#5aa0ff"));
        dialogBox.setStrokeWidth(2);
        dialogBox.setTranslateX(10);
        dialogBox.setTranslateY(420);

        // Dialogue text
        dialogueText = new Text();
        dialogueText.setWrappingWidth(780 - 32);
        dialogueText.setFill(Color.WHITE);
        dialogueText.setFont(Font.font(18));
        dialogueText.setTranslateX(26);
        dialogueText.setTranslateY(446);

        // Hint text
        hintText = new Text("");
        hintText.setFill(Color.web("#b8ccff"));
        hintText.setFont(Font.font(14));
        hintText.setTranslateX(650);
        hintText.setTranslateY(585);

        // Options row
        optionsRow = new HBox(16);
        optionsRow.setAlignment(Pos.CENTER);
        optionsRow.setTranslateX(200);
        optionsRow.setTranslateY(540);

        // Container for all dialogue elements
        dialogueContainer = new Group(charCard, charLabel, dialogBox, 
                                      dialogueText, hintText, optionsRow);
        dialogueContainer.setVisible(false);

        // Add to game scene
        FXGL.getGameScene().addUINode(dialogueContainer);
    }

    /**
     * Set callbacks for when dialogue is shown/hidden
     * @param onShow Called when dialogue is shown (use to hide map)
     * @param onHide Called when dialogue is hidden (use to restore map)
     */
    public void setVisibilityCallbacks(Runnable onShow, Runnable onHide) {
        this.onShowCallback = onShow;
        this.onHideCallback = onHide;
    }

    /**
     * Show a dialogue event
     * @param dialogueEvent The dialogue event to display
     * @param onComplete Callback when dialogue is completed
     */
    public void showDialogue(DialogueEvent dialogueEvent, Runnable onComplete) {
        this.onDialogueComplete = onComplete;
        
        // Hide the map before showing dialogue
        if (onShowCallback != null) {
            onShowCallback.run();
        }

        // Connect the dialogue event to the dialogue game
        dialogueEvent.setDialogueGame(dialogueGame);
        
        // Set completion callback
        dialogueGame.setOnDialogueComplete(() -> {
            hide();
            if (onDialogueComplete != null) {
                onDialogueComplete.run();
            }
        });
        
        // Show the dialogue
        dialogueContainer.setVisible(true);
        isVisible = true;
        dialogueEvent.trigger();
    }

    /**
     * Hide the dialogue UI
     */
    public void hide() {
        dialogueContainer.setVisible(false);
        isVisible = false;

        // Restore the map when dialogue is hidden
        if (onHideCallback != null) {
            onHideCallback.run();
        }
    }

    /**
     * Check if dialogue is currently visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Update character name displayed in the dialogue
     */
    public void setCharacterName(String name) {
        charLabel.setText(name);
    }

    /**
     * Clean up resources
     */
    public void dispose() {
        if (dialogueContainer != null) {
            FXGL.getGameScene().removeUINode(dialogueContainer);
        }
    }

    /**
     * Get the DialogueGame instance (for advanced control if needed)
     */
    public DialogueGame getDialogueGame() {
        return dialogueGame;
    }
}
