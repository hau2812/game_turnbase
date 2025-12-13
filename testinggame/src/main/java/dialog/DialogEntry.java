package dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a single dialog entry with text, speaker, conditions, and options
 */
public class DialogEntry {
    private String id;                          // Unique identifier for this dialog
    private String speakerName;                 // Name of the character speaking (null for narrator)
    private String text;                        // The dialog text
    private String imagePath;                   // Path to character image (optional)
    
    // Conditions for this dialog to be shown
    private List<Predicate<DialogContext>> conditions;
    
    // Variants: different text based on context
    private List<DialogVariant> variants;
    
    // Options/choices for the player
    private List<DialogOption> options;
    
    // Actions to execute when this dialog is shown
    private List<Function<DialogContext, Void>> onShowActions;
    
    // Next dialog IDs (can be multiple for branching)
    private List<String> nextDialogIds;
    
    // Purpose tag for matching (e.g., "greeting", "campfire_talk")
    private String purpose;
    
    public DialogEntry(String id, String speakerName, String text) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.conditions = new ArrayList<>();
        this.variants = new ArrayList<>();
        this.options = new ArrayList<>();
        this.onShowActions = new ArrayList<>();
        this.nextDialogIds = new ArrayList<>();
    }
    
    // Builder pattern methods
    public DialogEntry withImage(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }
    
    public DialogEntry withCondition(Predicate<DialogContext> condition) {
        this.conditions.add(condition);
        return this;
    }
    
    public DialogEntry withVariant(DialogVariant variant) {
        this.variants.add(variant);
        return this;
    }
    
    public DialogEntry withOption(DialogOption option) {
        this.options.add(option);
        return this;
    }
    
    public DialogEntry withOnShowAction(Function<DialogContext, Void> action) {
        this.onShowActions.add(action);
        return this;
    }
    
    public DialogEntry withNextDialog(String nextDialogId) {
        this.nextDialogIds.add(nextDialogId);
        return this;
    }
    
    public DialogEntry withPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }
    
    /**
     * Check if this dialog entry matches the given context
     */
    public boolean matches(DialogContext context) {
        // Check all conditions
        for (Predicate<DialogContext> condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get the text to display based on context and variants
     */
    public String getDisplayText(DialogContext context) {
        // Check variants first
        for (DialogVariant variant : variants) {
            if (variant.matches(context)) {
                return variant.getText();
            }
        }
        return text;
    }
    
    // Getters
    public String getId() { return id; }
    public String getSpeakerName() { return speakerName; }
    public String getText() { return text; }
    public String getImagePath() { return imagePath; }
    public List<DialogOption> getOptions() { return options; }
    public List<String> getNextDialogIds() { return nextDialogIds; }
    public String getPurpose() { return purpose; }
    public List<Function<DialogContext, Void>> getOnShowActions() { return onShowActions; }

    @Override
    public String toString() {
        return "DialogEntry{" +
                "id='" + id + '\'' +
                ", speakerName='" + speakerName + '\'' +
                ", text='" + text + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", conditions=" + conditions +
                ", variants=" + variants +
                ", options=" + options +
                ", onShowActions=" + onShowActions +
                ", nextDialogIds=" + nextDialogIds +
                ", purpose='" + purpose + '\'' +
                '}';
    }
}


