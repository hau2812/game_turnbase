package org.example;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.awt.*;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;




public class testing extends GameApplication {
    private Rectangle redBar;
    private double maxWidth = 200;   // full length of bar
    private double currentWidth = maxWidth;
    private Line blueLine;
    private Line redLine;

    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
    }

    @Override
    protected void initGame() {
        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);

        // Blue line (starts at right side of bar)
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth);
        blueLine.setEndX(barX + barWidth);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);

        // Red line (slightly below blue line)
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth);
        redLine.setEndX(barX + barWidth);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);

        getGameScene().addUINode(blueLine);
        getGameScene().addUINode(redLine);

        // Move lines every frame
        run(() -> updateLines(), Duration.millis(5));
    }

    private void updateLines() {
        moveLine(blueLine, 0.5);
        moveLine(redLine, 0.3);
    }

    private void moveLine(Line line, double speed) {
        double newX = line.getStartX() - speed;

        if (newX <= barX) {
            // Reset to middle when reaching the left end
            newX = barX + barWidth / 2.0;
        }

        line.setStartX(newX);
        line.setEndX(newX);
    }



    public static void main(String[] args) {
        launch(args);
    }

}
