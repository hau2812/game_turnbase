package dialog;

import java.util.function.Function;

/**
 * Represents a player choice/option in dialog
 */
public class DialogOption {
    private String text;                           // Option text displayed to player
    private Function<DialogContext, Void> action;   // Action to execute when chosen (with context)
    private Runnable runnable;                     // Simple function to execute when chosen (no parameters)
    private String nextDialogId;                    // Next dialog to show (null to continue)
    
    public DialogOption(String text) {
        this.text = text;
    }
    
    public DialogOption withAction(Function<DialogContext, Void> action) {
        this.action = action;
        return this;
    }
    
    /**
     * Set a simple runnable function to execute when this option is chosen
     * This is a simpler alternative to withAction() that doesn't require DialogContext
     */
    public DialogOption withRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }
    
    public DialogOption withNextDialog(String nextDialogId) {
        this.nextDialogId = nextDialogId;
        return this;
    }
    
    // Getters
    public String getText() { return text; }
    public Function<DialogContext, Void> getAction() { return action; }
    public Runnable getRunnable() { return runnable; }
    public String getNextDialogId() { return nextDialogId; }
}

