package ui;

import dialog.DialogRegistrations;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.testing;

/**
 * A clickable dialog box that displays a dialog when clicked.
 * The box is only clickable when testing.status contains the dialogString.
 */
public class DialogBox {
    private String hostName;
    private String shortName;
    private String dialogString;
    
    private Rectangle box;
    private Text boxText;
    private Group container;
    
    public DialogBox(String hostName, String shortName, String dialogString) {
        this.hostName = hostName;
        this.shortName = shortName;
        this.dialogString = dialogString;
        
        container = new Group();
        createUI();
    }
    
    private void createUI() {
        // Create box rectangle
        box = new Rectangle(200, 40, Color.rgb(70, 70, 70));
        box.setStroke(Color.WHITE);
        box.setStrokeWidth(1);
        box.setTranslateX(0);
        box.setTranslateY(0);
        
        // Create text
        boxText = new Text(shortName);
        boxText.setFont(new Font(14));
        boxText.setFill(Color.WHITE);
        boxText.setMouseTransparent(true);
        boxText.setTranslateX(10);
        boxText.setTranslateY(25);
        
        // Set up click handler
        box.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (isClickable()) {
                    DialogRegistrations.showOnlyDialogByTitle(dialogString,"menu");
                }
            }
        });
        
        container.getChildren().addAll(box, boxText);
        updateClickability();
    }
    
    /**
     * Check if this dialog box should be clickable based on testing.status
     */
    public boolean isClickable() {
        return testing.getStatus() != null && testing.getStatus().contains(dialogString);
    }
    
    /**
     * Update the visual appearance based on clickability
     */
    public void updateClickability() {
        boolean clickable = isClickable();
        if (clickable) {
            box.setFill(Color.rgb(90, 90, 90));
            box.setOnMouseEntered(e -> box.setFill(Color.rgb(110, 110, 110)));
            box.setOnMouseExited(e -> box.setFill(Color.rgb(90, 90, 90)));
        } else {
            box.setFill(Color.rgb(50, 50, 50));
            box.setOnMouseEntered(null);
            box.setOnMouseExited(null);
        }
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public String getDialogString() {
        return dialogString;
    }
    
    public Group getContainer() {
        return container;
    }
    
    public void setPosition(double x, double y) {
        container.setTranslateX(x);
        container.setTranslateY(y);
    }
}

