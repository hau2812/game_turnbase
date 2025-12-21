package dialog;

import battle.BattleSystem;
import battle.BattleUI;
import map.MapUI;
import org.example.testing;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * Manages running dialogs and their execution
 */
public class DialogSystem {
    private static DialogSystem instance;
    private DialogUI dialogUI;
    private Queue<DialogEntry> dialogQueue;
    private DialogEntry currentDialog;
    private DialogContext currentContext;
    private boolean isRunning;
    private Runnable onDialogStart;
    private Runnable onDialogEnd;
    private Predicate<String> nextDialogFilter; // Filter for next dialog IDs

    private MapUI mapUI;
    private BattleUI battleUI;
    private BattleSystem battleSystem;
    private DialogRegistrations dialogRegistrations;

    private DialogSystem() {
        this.dialogQueue = new LinkedList<>();
        this.isRunning = false;
    }
    
    public static DialogSystem getInstance() {
        if (instance == null) {
            instance = new DialogSystem();
        }
        return instance;
    }
    
    public void setDialogUI(DialogUI dialogUI) {
        this.dialogUI = dialogUI;
    }
    public void setMapUI(MapUI mapUI) {this.mapUI = mapUI;}
    public void setBattleUI(BattleUI battleUI) {this.battleUI = battleUI;}
    public void setBattleSystem(BattleSystem battleSystem) {this.battleSystem = battleSystem;}
    public void setNeededSystem(MapUI mapUI,BattleUI battleUI, BattleSystem battleSystem,DialogRegistrations dialogRegistrations) {this.mapUI = mapUI;this.battleUI=battleUI; this.battleSystem = battleSystem;this.dialogRegistrations = dialogRegistrations;}
    /**
     * Set callback to execute when dialog starts (e.g., hide BattleUI/MapUI)
     */
    public void setOnDialogStart(Runnable callback) {
        this.onDialogStart = callback;
    }
    
    /**
     * Set callback to execute when dialog ends (e.g., restore BattleUI/MapUI)
     */
    public void setOnDialogEnd(Runnable callback) {
        this.onDialogEnd = callback;
    }
    
    /**
     * Start a dialog sequence with initial context
     * @param initialDialogIds List of dialog IDs to start with
     * @param context Context object with heroes and other data
     */
    public boolean startDialog(List<String> initialDialogIds, DialogContext context) {
        this.currentContext = context;
        this.dialogQueue.clear();
        
        // Add initial dialogs to queue
        DialogLibrary library = DialogLibrary.getInstance();
        for (String id : initialDialogIds) {
            DialogEntry entry = library.getDialog(id);
            if (entry != null) {
                dialogQueue.offer(entry);
            }else{
                return false;
            }
        }
        
        isRunning = true;
        
        // Clear BattleUI and MapUI when dialog starts
        if (onDialogStart != null) {
            onDialogStart.run();
        }
        
        processNextDialog();
        return true;
    }
    
    /**
     * Add dialogs by purpose to the queue
     * @param purpose Purpose tag (e.g., "greeting", "campfire_talk")
     * @param context Context for matching
     * @param randomSelect If true, randomly select one if multiple match
     */
    public void addDialogsByPurpose(String purpose, DialogContext context, boolean randomSelect) {
        DialogLibrary library = DialogLibrary.getInstance();
        
        if (randomSelect) {
            DialogEntry entry = library.getDialogByPurpose(purpose, context);
            if (entry != null) {
                dialogQueue.offer(entry);
            }
        } else {
            List<DialogEntry> entries = library.getAllDialogsByPurpose(purpose, context);
            for (DialogEntry entry : entries) {
                dialogQueue.offer(entry);
            }
        }
        
        // If not currently running, start processing
        if (!isRunning && !dialogQueue.isEmpty()) {
            isRunning = true;
            
            // Clear BattleUI and MapUI when dialog starts
            if (onDialogStart != null) {
                onDialogStart.run();
            }
            
            processNextDialog();
        }
    }
    
    /**
     * Process the next dialog in the queue
     */
    private void processNextDialog() {
        if (dialogQueue.isEmpty()) {
            // No more dialogs, end sequence
            endDialog();
            return;
        }
        
        currentDialog = dialogQueue.poll();
        
        // Update testing.status with dialog title (without _1 suffix)
        updateStatusWithDialogTitle(currentDialog.getId());
        
        // Execute on-show actions
        for (var action : currentDialog.getOnShowActions()) {
            action.apply(currentContext);
        }
        
        // Display the dialog
        if (dialogUI != null) {
            String displayText = currentDialog.getDisplayText(currentContext);
            
            // Set background image based on dialog title
            dialogUI.setBackgroundFromTitle(currentDialog.getId());
            
            dialogUI.showDialog(
                currentDialog.getSpeakerName(),
                displayText,
                currentDialog.getImagePath(),
                currentDialog.getOptions()
            );
            // Enable click-to-continue if no options
            dialogUI.setClickToContinue(currentDialog.getOptions().isEmpty());
        }
    }
    
    /**
     * Extract base title from dialog ID (remove _1, _2, etc.) and update testing.status
     * @param dialogId The dialog ID (e.g., "dialogTitle_1")
     */
    private void updateStatusWithDialogTitle(String dialogId) {
        if (dialogId == null || dialogId.isEmpty()) {
            return;
        }
        
        // Extract base title by removing the _number suffix
        String baseTitle = dialogId;
        int lastUnderscore = dialogId.lastIndexOf('_');
        if (lastUnderscore > 0) {
            // Check if after underscore is a number
            String afterUnderscore = dialogId.substring(lastUnderscore + 1);
            try {
                Integer.parseInt(afterUnderscore);
                // If it's a number, extract base title
                baseTitle = dialogId.substring(0, lastUnderscore);
            } catch (NumberFormatException e) {
                // Not a number, use full ID as base title
                baseTitle = dialogId;
            }
        }
        
        // Check if status already contains this base title
        String currentStatus = testing.getStatus();
        if (currentStatus == null) {
            currentStatus = "";
        }
        
        if (!currentStatus.contains(baseTitle)) {
            // Add base title to status
            if (currentStatus.isEmpty()) {
                testing.setStatus(baseTitle);
            } else {
                testing.setStatus(currentStatus + " " + baseTitle);
            }
        }
    }
    
    /**
     * Continue to next dialog (called when player clicks continue)
     */
    public void continueDialog() {
        if (currentDialog == null) {
            processNextDialog();
            return;
        }
        
        // If dialog has next dialogs, add them to queue (filtered if filter is set)
        List<String> nextIds = currentDialog.getNextDialogIds();
        if (!nextIds.isEmpty()) {
            DialogLibrary library = DialogLibrary.getInstance();
            for (String id : nextIds) {
                // Apply filter if set
                if (nextDialogFilter != null && !nextDialogFilter.test(id)) {
                    continue; // Skip this dialog if it doesn't pass the filter
                }
                DialogEntry entry = library.getDialog(id);
                if (entry != null) {
                    dialogQueue.offer(entry);
                }
            }
        }
        
        processNextDialog();
    }
    
    /**
     * Set a filter for next dialog IDs. Only dialogs whose IDs pass the filter will be added to the queue.
     * Set to null to disable filtering.
     * @param filter Predicate that returns true for dialog IDs that should be allowed
     */
    public void setNextDialogFilter(Predicate<String> filter) {
        this.nextDialogFilter = filter;
    }
    
    /**
     * Handle option selection
     */
    public void selectOption(int optionIndex) {
        if (currentDialog == null || optionIndex < 0 || 
            optionIndex >= currentDialog.getOptions().size()) {
            return;
        }
        
        DialogOption option = currentDialog.getOptions().get(optionIndex);
        
        // Execute option action
        if (option.getAction() != null) {
            option.getAction().apply(currentContext);
        }
        
        // Add next dialog if specified (filtered if filter is set)
        if (option.getNextDialogId() != null) {
            String nextDialogId = option.getNextDialogId();
            // Apply filter if set
            if (nextDialogFilter == null || nextDialogFilter.test(nextDialogId)) {
                DialogLibrary library = DialogLibrary.getInstance();
                DialogEntry nextEntry = library.getDialog(nextDialogId);
                if (nextEntry != null) {
                    dialogQueue.offer(nextEntry);
                }
            }
        }
        
        processNextDialog();
    }

    /**
     * Skip dialogs until a choice (options) is encountered. If no options remain, end dialog.
     */
    public void skipUntilChoice() {
        if (!isRunning) {
            return;
        }
        int guard = 0;
        while (guard < 1000) {
            if (currentDialog != null && currentDialog.getOptions() != null && !currentDialog.getOptions().isEmpty()) {
                if (dialogUI != null) {
                    dialogUI.revealFullText();
                }
                return; // stop at the first choice
            }

            // Reveal current text before skipping
            if (dialogUI != null) {
                dialogUI.revealFullText();
            }

            // If nothing left to advance, end
            if ((currentDialog == null || currentDialog.getNextDialogIds().isEmpty()) && dialogQueue.isEmpty()) {
                endDialog();
                return;
            }

            continueDialog();
            guard++;

            if (!isRunning) {
                return;
            }
        }
        System.out.println("skipUntilChoice: reached guard limit (possible loop).");
    }
    
    /**
     * End the current dialog sequence
     */
    public void endDialog() {
        isRunning = false;
        currentDialog = null;
        if (dialogUI != null) {
            dialogUI.hide();
        }
        
        // Restore BattleUI and MapUI when dialog ends (if needed)
        if (onDialogEnd != null) {
            onDialogEnd.run();
        }
    }
    
    /**
     * Check if dialog is currently running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Force stop dialog
     */
    public void stop() {
        dialogQueue.clear();
        endDialog();
    }
}

