package dialog;

import java.util.function.Function;

/**
 * Represents a player choice/option in dialog
 */
public class DialogOption {
    private String text;                           // Option text displayed to player
    private Function<DialogContext, Void> action;   // Action to execute when chosen
    private String nextDialogId;                    // Next dialog to show (null to continue)
    
    public DialogOption(String text) {
        this.text = text;
    }
    
    public DialogOption withAction(Function<DialogContext, Void> action) {
        this.action = action;
        return this;
    }
    
    public DialogOption withNextDialog(String nextDialogId) {
        this.nextDialogId = nextDialogId;
        return this;
    }
    
    // Getters
    public String getText() { return text; }
    public Function<DialogContext, Void> getAction() { return action; }
    public String getNextDialogId() { return nextDialogId; }
}

