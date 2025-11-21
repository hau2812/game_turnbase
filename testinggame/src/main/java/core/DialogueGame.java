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

import static com.almasb.fxgl.dsl.FXGL.*;

public class DialogueGame extends GameApplication {

    private static final double TYPE_SPEED_SEC = 0.02;
    private static final int OPTION_AT_INDEX = 2;

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

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Dialogue Demo");
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.web("#16181a"));

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

        Rectangle dialogBox = new Rectangle(1180, 180, Color.web("#1e2123", 0.95));
        dialogBox.setArcWidth(18);
        dialogBox.setArcHeight(18);
        dialogBox.setStroke(Color.web("#5aa0ff"));
        dialogBox.setStrokeWidth(2);
        dialogBox.setTranslateX(50);
        dialogBox.setTranslateY(520);

        dialogueText = new Text();
        dialogueText.setWrappingWidth(1180 - 32);
        dialogueText.setFill(Color.WHITE);
        dialogueText.setFont(Font.font(20));
        dialogueText.setTranslateX(66);
        dialogueText.setTranslateY(546);

        hintText = new Text("");
        hintText.setFill(Color.web("#b8ccff"));
        hintText.setFont(Font.font(14));
        hintText.setTranslateX(980);
        hintText.setTranslateY(685);

        optionsRow = new HBox(16);
        optionsRow.setAlignment(Pos.CENTER);
        optionsRow.setTranslateX(300);
        optionsRow.setTranslateY(640);

        getGameScene().addUINode(new Group(charCard, charLabel, dialogBox, dialogueText, hintText, optionsRow));

        onBtnDown(MouseButton.PRIMARY, this::handleTap);
        onKeyDown(KeyCode.ENTER, this::handleTap);
        onKeyDown(KeyCode.SPACE, this::handleTap);

        script.add("??? : ...You finally made it. I thought the storm would keep you away.");
        script.add("You : I nearly turned back. The wind—like knives. What did you want to show me?");
        script.add("??? : Before we go further, decide what we do next. Choose wisely.");
        script.add("You : Alright. Let's move.");

        startTyping(script.get(index));
    }

    private void handleTap() {
        if (showingOptions) return;
        if (isTyping) finishTyping();
        else next();
    }

    private void next() {
        if (index == OPTION_AT_INDEX && !showingOptions) {
            showOptions(true);
            return;
        }
        index++;
        if (index >= script.size()) {
            index = script.size() - 1;
            hintText.setText("End of demo");
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
        createOptionButton("Ask about the ruins");
        createOptionButton("Look for supplies");
        createOptionButton("Set up camp");

    }

    private void createOptionButton(String text) {
        Button b = getUIFactoryService().newButton(text);
        b.setPrefWidth(240);
        b.setOnAction(e -> onChoose(text));
        optionsRow.getChildren().add(b);
    }

    private void onChoose(String option) {
        String branch = switch (option) {
            case "Ask about the ruins" -> "You : Tell me about those ruins—who built them?";
            case "Look for supplies" -> "You : Let's scavenge first. No point marching in hungry.";
            default -> "You : We set up camp here. We move at first light.";
        };
        if (index == OPTION_AT_INDEX) {
            script.add(index + 1, branch);
        }
        showOptions(false);
        next();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
