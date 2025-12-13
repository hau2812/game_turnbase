package dialog;

import java.util.function.Predicate;

/**
 * Represents a variant of dialog text based on conditions
 */
public class DialogVariant {
    private String text;
    private Predicate<DialogContext> condition;
    
    public DialogVariant(String text, Predicate<DialogContext> condition) {
        this.text = text;
        this.condition = condition;
    }
    
    public boolean matches(DialogContext context) {
        return condition.test(context);
    }
    
    public String getText() {
        return text;
    }
}

