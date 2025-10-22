package event;

import core.DialogueGame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

public class DialogueEvent extends GameEvent {
    private List<String> script;
    private Map<String, DialogueOption> options;
    private int optionIndex;
    private DialogueGame dialogueGame;
    
    public static class DialogueOption {
        private String text;
        private Consumer<Void> action;
        
        public DialogueOption(String text, Consumer<Void> action) {
            this.text = text;
            this.action = action;
        }
        
        public String getText() {
            return text;
        }
        
        public void execute() {
            action.accept(null);
        }
    }
    
    public DialogueEvent(String description) {
        super(description);
        this.script = new ArrayList<>();
        this.options = new HashMap<>();
        this.optionIndex = -1;
    }
    
    public void setDialogueGame(DialogueGame dialogueGame) {
        this.dialogueGame = dialogueGame;
    }
    
    public void addLine(String line) {
        script.add(line);
    }
    
    public void setOptionIndex(int index) {
        this.optionIndex = index;
    }
    
    public void addOption(String optionText, Consumer<Void> action) {
        options.put(optionText, new DialogueOption(optionText, action));
    }
    
    @Override
    public void trigger() {
        if (dialogueGame == null) {
            System.out.println("DialogueGame not connected to event!");
            return;
        }
        
        dialogueGame.loadDialogue(script, optionIndex, options);
        dialogueGame.startDialogue();
    }
}
