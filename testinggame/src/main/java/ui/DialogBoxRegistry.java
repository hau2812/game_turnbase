package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for DialogBox entries.
 * Use register() to add new dialog boxes.
 */
public class DialogBoxRegistry {
    private static Map<String, List<DialogBox>> registry = new HashMap<>();
    
    /**
     * Register a new dialog box.
     * @param hostName The hero name this dialog belongs to, or "overall" for general dialogs
     * @param shortName The display name shown on the box
     * @param dialogString The dialog title string (must match testing.status to be clickable)
     */
    public static void register(String hostName, String shortName, String dialogString) {
        DialogBox box = new DialogBox(hostName, shortName, dialogString);
        
        if (!registry.containsKey(hostName)) {
            registry.put(hostName, new ArrayList<>());
        }
        registry.get(hostName).add(box);
    }
    
    /**
     * Get all dialog boxes for a specific host (hero name or "overall")
     */
    public static List<DialogBox> getByHost(String hostName) {
        return registry.getOrDefault(hostName, new ArrayList<>());
    }
    
    /**
     * Get all registered host names (including "overall")
     */
    public static List<String> getAllHosts() {
        return new ArrayList<>(registry.keySet());
    }
    
    /**
     * Get all dialog boxes
     */
    public static List<DialogBox> getAll() {
        List<DialogBox> all = new ArrayList<>();
        for (List<DialogBox> boxes : registry.values()) {
            all.addAll(boxes);
        }
        return all;
    }
    
    /**
     * Clear all registered dialog boxes
     */
    public static void clear() {
        registry.clear();
    }
    
    /**
     * Initialize with template entries.
     * Add your dialog boxes here using the register() method.
     * 
     * Template:
     * register("HeroName", "Short Display Name", "dialogTitle");
     * register("overall", "Short Display Name", "dialogTitle");
     */
    public static void init() {
        // Clear existing entries
        clear();
        
        // ===== TEMPLATE: Add your dialog boxes here =====
        // Example:
        // register("Flamita", "Flamita Intro", "FlamitaIntro");
        // register("overall", "General Dialog", "generalDialog");
        // ================================================
        
        // Add your dialog registrations below:
        //overall
        register("overall", "Start of the game", "intro");
        //Flamita
        register("Flamita", "Start of Recruit", "recruitDialogFlamita");
        register("Flamita", "Recruit when have NOT met", "recruitDialogFlamitaBasic");
        register("Flamita", "Recruit when have  met", "recruitDialogFlamitaHaveMet");
        register("Flamita", "Recruit with Leuna", "recruitDialogFlamitaWithLeuna");
        register("Flamita", "Recruit with Chigon", "recruitDialogFlamitaWithChigon");
        register("Flamita", "Boss fight when have met ", "FlamitaBossFightBeginHaveMet");
        register("Flamita", "Boss fight when have not met ", "FlamitaBossFightBeginHaveNotMet");
        register("Flamita", "Boss fight with Leuna", "FlamitaBossFightBeginHaveLeuna");
        register("Flamita", "Boss fight with Chigon", "FlamitaBossFightBeginHaveChigon");
        register("Flamita", "Boss fight with Pieberry", "FlamitaBossFightBeginHavePieberry");
        register("Flamita", "After boss fight", "FlamitaBossBattleVictory");
        register("Flamita", "After boss fight with Pieberry", "FlamitaBossBattleVictoryWithPieberry");

    }
}

