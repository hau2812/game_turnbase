package dialog;

import java.util.*;
import java.util.Map;

/**
 * Manages registered dialogs and provides lookup functionality
 */
public class DialogLibrary {
    private static DialogLibrary instance;
    
    // Main storage: dialog ID -> DialogEntry
    private Map<String, DialogEntry> dialogs;
    
    // Purpose index: purpose tag -> List of dialog IDs
    private Map<String, List<String>> purposeIndex;
    
    private DialogLibrary() {
        this.dialogs = new HashMap<>();
        this.purposeIndex = new HashMap<>();
    }
    
    public static DialogLibrary getInstance() {
        if (instance == null) {
            instance = new DialogLibrary();
        }
        return instance;
    }
    
    /**
     * Register a dialog entry
     */
    public void register(DialogEntry entry) {
        dialogs.put(entry.getId(), entry);
        
        // Index by purpose
        if (entry.getPurpose() != null) {
            purposeIndex.computeIfAbsent(entry.getPurpose(), k -> new ArrayList<>())
                       .add(entry.getId());
        }
    }
    
    /**
     * Get a dialog by ID
     */
    public DialogEntry getDialog(String id) {
        return dialogs.get(id);
    }
    
    /**
     * Get all dialogs matching a purpose and context
     * Returns a random one if multiple match
     */
    public DialogEntry getDialogByPurpose(String purpose, DialogContext context) {
        List<String> dialogIds = purposeIndex.get(purpose);
        if (dialogIds == null || dialogIds.isEmpty()) {
            return null;
        }
        
        // Filter by context matching
        List<DialogEntry> matchingDialogs = new ArrayList<>();
        for (String id : dialogIds) {
            DialogEntry entry = dialogs.get(id);
            // Only consider first-line entries (id ends with "_1") to avoid starting mid-chain
            if (entry != null && id.endsWith("_1") && entry.matches(context)) {
                matchingDialogs.add(entry);
            }
        }
        
        if (matchingDialogs.isEmpty()) {
            return null;
        }
        
        // Return random if multiple match
        if (matchingDialogs.size() == 1) {
            return matchingDialogs.get(0);
        } else {
            Random random = new Random();
            return matchingDialogs.get(random.nextInt(matchingDialogs.size()));
        }
    }
    
    /**
     * Get all dialogs matching a purpose (for adding multiple)
     */
    public List<DialogEntry> getAllDialogsByPurpose(String purpose, DialogContext context) {
        List<String> dialogIds = purposeIndex.get(purpose);
        if (dialogIds == null || dialogIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DialogEntry> matchingDialogs = new ArrayList<>();
        for (String id : dialogIds) {
            DialogEntry entry = dialogs.get(id);
            if (entry != null && entry.matches(context)) {
                matchingDialogs.add(entry);
            }
        }
        
        return matchingDialogs;
    }
    
    /**
     * Check if a dialog exists
     */
    public boolean hasDialog(String id) {
        return dialogs.containsKey(id);
    }
    
    /**
     * Get all registered dialog IDs
     */
    public Set<String> getAllDialogIds() {
        return new HashSet<>(dialogs.keySet());
    }
    
    /**
     * Get all registered dialogs
     */
    public Map<String, DialogEntry> getAllDialogs() {
        return new HashMap<>(dialogs);
    }
    
    /**
     * Remove a dialog by ID
     * @param dialogId The ID of the dialog to remove
     * @return true if the dialog was removed, false if it didn't exist
     */
    public boolean removeDialog(String dialogId) {
        DialogEntry removed = dialogs.remove(dialogId);
        if (removed != null) {
            // Also remove from purpose index if it has a purpose
            if (removed.getPurpose() != null) {
                List<String> purposeDialogs = purposeIndex.get(removed.getPurpose());
                if (purposeDialogs != null) {
                    purposeDialogs.remove(dialogId);
                    // Clean up empty purpose lists
                    if (purposeDialogs.isEmpty()) {
                        purposeIndex.remove(removed.getPurpose());
                    }
                }
            }
            return true;
        }
        return false;
    }
}

