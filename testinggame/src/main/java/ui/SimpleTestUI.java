package ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.almasb.fxgl.dsl.FXGL;

/**
 * Simple test UI to verify UI system is working
 */
public class SimpleTestUI {
    private Rectangle background;
    private Text title;
    private boolean isVisible = false;
    
    public SimpleTestUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        // Simple background
        background = new Rectangle(400, 200, Color.LIGHTBLUE);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        background.setTranslateX(200);
        background.setTranslateY(200);
        
        // Simple title
        title = new Text("Test UI - Press I for Inventory, S for Shop");
        title.setFont(new Font(16));
        title.setFill(Color.BLACK);
        title.setTranslateX(220);
        title.setTranslateY(300);
    }
    
    public void show() {
        if (!isVisible) {
            isVisible = true;
            FXGL.getGameScene().addUINode(background);
            FXGL.getGameScene().addUINode(title);
            System.out.println("Simple Test UI shown!");
        }
    }
    
    public void hide() {
        if (isVisible) {
            isVisible = false;
            FXGL.getGameScene().removeUINode(background);
            FXGL.getGameScene().removeUINode(title);
            System.out.println("Simple Test UI hidden!");
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
}
