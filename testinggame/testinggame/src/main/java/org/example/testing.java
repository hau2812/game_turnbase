package org.example;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {
    private Line blueLine;
    private Line redLine;

    private Rectangle blueBox;
    private Rectangle bluePowerBox;  // 👈 New power box
    private Rectangle redBox;

    private Rectangle blueHealthBar;
    private Rectangle redHealthBar;

    private Rectangle blueHealthBorder;
    private Rectangle redHealthBorder;

    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;

    private boolean moving = true;

    private double lineSpeed = 0.5; // base speed
    private double blueLineSpeed = lineSpeed; // can be boosted

    // Independent HP values
    private double blueMaxHP = 300;
    private double redMaxHP = 200;
    private double blueHP = blueMaxHP;
    private double redHP = redMaxHP;

    // Constant visual width of health bars
    private double healthBarWidth = 200;
    private double healthBarHeight = 20;

    private Text blueHPText;
    private Text redHPText;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Line Stop & Power Box");
    }

    @Override
    protected void initGame() {
        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);

        // Blue line
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth / 3);
        blueLine.setEndX(barX + barWidth / 3);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);

        // Red line
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth);
        redLine.setEndX(barX + barWidth);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);

        getGameScene().addUINode(blueLine);
        getGameScene().addUINode(redLine);

        // Normal blue box
        blueBox = new Rectangle(30, 30, Color.BLUE);
        blueBox.setTranslateX(50);
        blueBox.setTranslateY(500);

        // Blue power box
        bluePowerBox = new Rectangle(30, 30, Color.DARKBLUE);
        bluePowerBox.setTranslateX(100);
        bluePowerBox.setTranslateY(500);

        // Red box
        redBox = new Rectangle(30, 30, Color.RED);
        redBox.setTranslateX(500);
        redBox.setTranslateY(500);

        getGameScene().addUINode(blueBox);
        getGameScene().addUINode(bluePowerBox);
        getGameScene().addUINode(redBox);

        // Health bar positions
        double blueHealthX = blueBox.getTranslateX();
        double blueHealthY = blueBox.getTranslateY() - 200;

        double redHealthX = redBox.getTranslateX();
        double redHealthY = redBox.getTranslateY() - 200;

        // Health bar borders
        blueHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        blueHealthBorder.setStroke(Color.BLACK);
        blueHealthBorder.setStrokeWidth(2);
        blueHealthBorder.setTranslateX(blueHealthX);
        blueHealthBorder.setTranslateY(blueHealthY);

        redHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        redHealthBorder.setStroke(Color.BLACK);
        redHealthBorder.setStrokeWidth(2);
        redHealthBorder.setTranslateX(redHealthX);
        redHealthBorder.setTranslateY(redHealthY);

        // Actual health bars
        blueHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.BLUE);
        blueHealthBar.setTranslateX(blueHealthX);
        blueHealthBar.setTranslateY(blueHealthY);

        redHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.RED);
        redHealthBar.setTranslateX(redHealthX);
        redHealthBar.setTranslateY(redHealthY);

        // Blue HP text
        blueHPText = new Text("HP: " + (int) blueHP + " / " + (int) blueMaxHP);
        blueHPText.setFont(new Font(16));
        blueHPText.setFill(Color.BLUE);
        blueHPText.setTranslateX(blueHealthBorder.getTranslateX());
        blueHPText.setTranslateY(blueHealthBorder.getTranslateY() - 5);

        // Red HP text
        redHPText = new Text("HP: " + (int) redHP + " / " + (int) redMaxHP);
        redHPText.setFont(new Font(16));
        redHPText.setFill(Color.RED);
        redHPText.setTranslateX(redHealthBorder.getTranslateX());
        redHPText.setTranslateY(redHealthBorder.getTranslateY() - 5);




        // Add UI
        getGameScene().addUINode(blueHealthBorder);
        getGameScene().addUINode(blueHealthBar);
        getGameScene().addUINode(redHealthBorder);
        getGameScene().addUINode(redHealthBar);
        getGameScene().addUINode(blueHPText);
        getGameScene().addUINode(redHPText);

        // Normal blue box click
        blueBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (blueBox.getFill() == Color.GREEN) {
                    damageRed(20); // normal hit
                }
                resetLine(blueLine, Color.BLUE, blueBox, false);
            }
        });

        // Power blue box click
        bluePowerBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (bluePowerBox.getFill() == Color.GREEN) {
                    damageRed(30); // 1.5× damage
                }
                resetLine(blueLine, Color.DARKBLUE, bluePowerBox, true);
            }
        });

        // Red box click
        redBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (redBox.getFill() == Color.GREEN) {
                    damageBlue(20);
                }
                resetLine(redLine, Color.RED, redBox, false);
            }
        });

        // Move lines every frame
        run(() -> updateLines(), Duration.millis(5));
    }

    private void updateLines() {
        if (!moving) return;

        moveLine(blueLine, blueLineSpeed, blueBox);
        //moveLine(blueLine, blueLineSpeed, bluePowerBox); // check both blue boxes
        moveLine(redLine, lineSpeed, redBox);
    }

    private void moveLine(Line line, double speed, Rectangle box) {
        if (box.getFill() == Color.GREEN) return; // skip if already green

        double newX = line.getStartX() - speed;

        if (newX <= barX) {
            moving = false;
            box.setFill(Color.GREEN);
            if(box.equals(blueBox)) {
                bluePowerBox.setFill(Color.GREEN);
            }
            return;
        }

        line.setStartX(newX);
        line.setEndX(newX);
    }

    private void resetLine(Line line, Color lineColor, Rectangle box, boolean isPower) {
        if (!moving && box.getFill() == Color.GREEN) {
            if(box.equals(bluePowerBox)) {
                line.setStartX(barX + barWidth / 1.0);
                line.setEndX(barX + barWidth / 1.0);
            }else {
                line.setStartX(barX + barWidth / 2.0);
                line.setEndX(barX + barWidth / 2.0);
            }
            box.setFill(lineColor);
            if(box.equals(blueBox)||box.equals(bluePowerBox)) {
                bluePowerBox.setFill(lineColor);
                blueBox.setFill(lineColor);
            }

            if (!(blueBox.getFill() == Color.GREEN ||
                    bluePowerBox.getFill() == Color.GREEN ||
                    redBox.getFill() == Color.GREEN)) {
                moving = true;

            }
        }
    }

    private void damageBlue(double amount) {
        blueHP = Math.max(0, blueHP - amount);
        double ratio = blueHP / blueMaxHP;
        blueHealthBar.setWidth(healthBarWidth * ratio);
        blueHPText.setText("HP: " + (int) blueHP + " / " + (int) blueMaxHP);
    }

    private void damageRed(double amount) {
        redHP = Math.max(0, redHP - amount);
        double ratio = redHP / redMaxHP;
        redHealthBar.setWidth(healthBarWidth * ratio);
        redHPText.setText("HP: " + (int) redHP + " / " + (int) redMaxHP);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
