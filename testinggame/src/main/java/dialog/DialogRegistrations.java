package dialog;

import battle.BattleSystem;
import battle.BattleUI;
import map.MapUI;
import org.example.testing;
import ui.MenuUI;
import characters.Observer;

import java.util.*;

/**
 * Central file for all dialog registrations and dialog-related logic
 * This file contains all dialog text registrations and dialog triggering logic
 */
public class DialogRegistrations {
    // System references for dialog actions
    private static BattleSystem battleSystem;
    private static BattleUI battleUI;
    private static MapUI mapUI;
    private static MenuUI menuUI;
    
    /**
     * Initialize system references for dialog control
     * @param battleSystem Reference to battle system
     * @param battleUI Reference to battle UI
     * @param mapUI Reference to map UI
     * @param menuUI Reference to menu UI
     */
    public static void initializeSystems(BattleSystem battleSystem, BattleUI battleUI, MapUI mapUI, MenuUI menuUI) {
        DialogRegistrations.battleSystem = battleSystem;
        DialogRegistrations.battleUI = battleUI;
        DialogRegistrations.mapUI = mapUI;
        DialogRegistrations.menuUI = menuUI;
    }
    
    /**
     * Get the battle system reference
     */
    public static BattleSystem getBattleSystem() {
        return battleSystem;
    }
    
    /**
     * Get the battle UI reference
     */
    public static BattleUI getBattleUI() {
        return battleUI;
    }
    
    /**
     * Get the map UI reference
     */
    public static MapUI getMapUI() {
        return mapUI;
    }
    
    /**
     * Get the menu UI reference
     */
    public static MenuUI getMenuUI() {
        return menuUI;
    }
    
    /**
     * Register all dialogs in the game
     * Call this during game initialization
     */
    public static void registerAllDialogs() {
        // Register battle victory dialogs
        registerBattleVictoryDialogs();
        registerIntroDialog();
        registerFlamitaHaveAWalk();
        // Register small talk dialogs
        //registerAzarLeunaDialog();
        //registerAzarFlamitaDialog();
        
        // Add more dialog registrations here as needed
        // registerCampfireDialogs();
        // registerShopDialogs();
        // etc.
    }
    /**
     * Get 3 random heroes from the provided list, excluding heroes already in the party
     * @param heroList List of available hero names
     * @return List of 3 random hero names (or fewer if not enough heroes available)
     */
    public static List<String> get3RandomHero(List<String> heroList) {
        if (heroList == null || heroList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a mutable copy of the list
        List<String> available = new ArrayList<>(heroList);

        // Remove heroes that are already in the party
        // Use getSelectedHeroes() instead of getAllHeroes() because hero slots
        // might not be initialized yet (they're only created in initializeBattle())
        if (battleSystem != null) {
            String[] partyHeroes = battleSystem.getSelectedHeroes();
            for (String heroName : partyHeroes) {
                if (heroName != null && !heroName.isEmpty()) {
                    available.remove(heroName);
                }
            }
        }

        // If no heroes available after filtering, return empty list
        if (available.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Shuffle the list
        Collections.shuffle(available);
        
        // Return up to 3 heroes
        int count = Math.min(3, available.size());
        return available.subList(0, count);
    }

    public static void registerRecruitDialogs(){
        // Get available heroes and create a mutable list
        List<String> availableHeroes = new ArrayList<>(Arrays.asList(testing.getAllHeros()));

        
        // Get 3 random heroes for recruitment options
        List<String> randomHeroes = get3RandomHero(availableHeroes);
        
        dialogMakerHelperWithPurpose("recruitDialog1;Azar:Hey...I found someone over here","recruitDialog");
        dialogMakerHelperWithPurpose("recruitDialog2;Azar:Let see they are friend or foe","recruitDialog");

        dialogMakerHelper("recruitDialog0;:Choose your ally!");
        linkChainTo("recruitDialog1","recruitDialog0_1");
        linkChainTo("recruitDialog2","recruitDialog0_1");

        DialogLibrary library = DialogLibrary.getInstance();
        DialogEntry branchEntry = library.getDialog("recruitDialog0_1");
        if (branchEntry != null) {
            branchEntry.getOptions().clear();
            
            // Create options for each random hero
            for (int i = 0; i < randomHeroes.size(); i++) {
                String heroName = randomHeroes.get(i);
                final String selectedHero = heroName; // Final for lambda
                
                DialogOption option = new DialogOption(heroName)
                    .withAction(ctx -> {
                        // Add hero to party
                        addHeroToParty(selectedHero);
                        return null;
                    })
                    .withNextDialog("recruitDialog-1_1");
                
                branchEntry.withOption(option);
            }

        }
    }
    
    /**
     * Add a hero to the current party
     * @param heroName Name of the hero to add
     */
    private static void addHeroToParty(String heroName) {
        if (battleSystem == null) {
            System.out.println("Cannot add hero: battleSystem is null");
            return;
        }
        
        // Get current selected heroes
        String[] currentHeroes = battleSystem.getSelectedHeroes();
        List<String> newHeroes = new ArrayList<>(Arrays.asList(currentHeroes));
        
        // Check if hero is already in party
        if (newHeroes.contains(heroName)) {
            System.out.println("Hero " + heroName + " is already in the party!");
            return;
        }
        
        // Check if party is full (max 3 heroes)
        if (newHeroes.size() >= 3) {
            System.out.println("Party is full! Cannot add " + heroName);
            return;
        }
        
        // Add the new hero
        newHeroes.add(heroName);
        
        // Update battle system
        String[] updatedHeroes = newHeroes.toArray(new String[0]);
        battleSystem.configureBattle(battleSystem.getHideTalents(), updatedHeroes);
        //Update available Hero
        List<String> availableHeroes = new ArrayList<>(Arrays.asList(testing.getAvailableHeroes()));
        if(!availableHeroes.contains(heroName)){
            availableHeroes.add(heroName);
            testing.setAvailableHeroes(availableHeroes.toArray(new String[0]));
        }

        dialogMakerHelper("recruitDialog-1;:"+heroName+" have been added to your party");
        System.out.println("Added " + heroName + " to the party! Party now has: " + Arrays.toString(updatedHeroes));
    }
    /**
     * Register battle victory dialogs
     * Registers the base "battle_victory" dialog structure
     */
    private static void registerBattleVictoryDialogs() {
        // Register base battle victory dialog structure
        // The actual hero-specific dialogs will be created dynamically in showBattleVictoryDialog()
        // But we can register a template or placeholder if needed
    }
    
    /**
     * Show battle victory dialog based on hero conditions
     * @param battleSystem The battle system to get heroes from
     */
    public static void showBattleVictoryDialog(BattleSystem battleSystem) {
        Observer.characterSlot[] heroes = battleSystem.getAllHeroes();
        
        // Check if all heroes have HP > maxHp/2
        boolean allHeroesHealthy = true;
        List<Observer.characterSlot> aliveHeroes = new ArrayList<>();
        
        for (Observer.characterSlot hero : heroes) {
            if (hero != null && hero.getCurrentHp() > 0) {
                aliveHeroes.add(hero);
                float maxHp = hero.getCharacter().getHp();
                if (hero.getCurrentHp() <= maxHp *0.9) {
                    allHeroesHealthy = false;
                    break;
                }
            }
        }
        
        // If all heroes are healthy, show "it was easy" dialog
        if (allHeroesHealthy && !aliveHeroes.isEmpty()) {
            // Randomly select one hero to say "it was easy"
            Random random = new Random();
            Observer.characterSlot selectedHero = aliveHeroes.get(random.nextInt(aliveHeroes.size()));
            String selectedHeroName = selectedHero.getCharacter().getName();
            
            // Check if Flamita exists and wasn't the first speaker
            Observer.characterSlot flamita = null;
            for (Observer.characterSlot hero : heroes) {
                if (hero != null && hero.getCharacter().getName().equals("Flamita")) {
                    flamita = hero;
                    break;
                }
            }
            
            // Build dialog string using helper format
            String dialogString = "battle_victory;" + selectedHeroName + ":It was easy!";
            
            // If Flamita exists and wasn't the first speaker, add her response
            if (flamita != null && !selectedHeroName.equals("Flamita")) {
                dialogString += ";Flamita:It's because you have me";
            }
            
            // Use dialogMakerHelper to create the dialog
            dialogMakerHelper(dialogString);
            
            // Start the dialog sequence
            showDialogByTitle("battle_victory");
        }
    }
    
    /**
     * Register a test dialog between Azar and Leuna (10 lines)
     * Now uses dialogMakerHelper for easier dialog creation
     */
    private static void registerAzarLeunaDialog() {
        // Base dialog (5 lines) with purpose smallTalk
        String dialogString = "azar_leuna;Azar:Line 1;Leuna:Line 2;Azar:Line 3;Leuna:Line 4;Azar:Line 5";
        dialogMakerHelperWithPurpose(dialogString, "smallTalk");

        // Branch dialogs
        String dialogString2 = "azar_leuna_option1;Azar:Option1_1;Leuna:Option1_2";
        String dialogString3 = "azar_leuna_option2;Azar:Option2_1;Leuna:Option2_2";
        String dialogString4 = "azar_leuna_continue1;Azar:Line 6;Leuna:Line 7";

        dialogMakerHelper(dialogString2);
        dialogMakerHelper(dialogString3);
        dialogMakerHelper(dialogString4);

        DialogLibrary library = DialogLibrary.getInstance();

        // Add options after line 5 (azar_leuna_5)
        DialogEntry branchEntry = library.getDialog("azar_leuna_5");
        if (branchEntry != null) {
            branchEntry.getOptions().clear();
            branchEntry.withOption(new DialogOption("Option 1").withNextDialog("azar_leuna_option1_1"));
            branchEntry.withOption(new DialogOption("Option 2").withNextDialog("azar_leuna_option2_1"));
        }

        // Link ends of option dialogs to continuation
        linkChainTo("azar_leuna_option1", "azar_leuna_continue1_1");
        linkChainTo("azar_leuna_option2", "azar_leuna_continue1_1");
    }

    private static void registerIntroDialog(){
        String dialogString = "intro;" +
                ":In a world that literally anything can happen...;" +
                ":depends on the author's creativity...;" +
                ":there are someone who is wondering...;" +
                "???:Damn it.Nothing interesting this week...and they said this is the city of mystery...;" +
                ":(The door suddenly open behind him);" +
                "???:Hey Azar I got something that might catch your interest ;" +
                "Azar:Please tell me you got a big one Clint...;" +
                "Clint:Obviously.(Take a seat).So currently people located the dungeon and they are excited about it;" +
                "Azar:And why do you think this would make me feel the same thing like them?;" +
                "Clint:They named it Lost Dungeon;" +
                "Azar:Like... the Lost Forest?;" +
                "Clint:Ye... it worked just like the Lost Forest;" +
                "Clint:When you entered, you will find yourself in a random place that look nothing alike a normal dungeon;" +
                "Clint:Even when you formed a party and head inside the dungeon,each person in your party would still get teleported to a different location so there’s no way you can build up a party when once you come inside;" +
                "Azar:And is there a way out?;" +
                "Clint:You could get out in a few ways... one of them is simply just walk until you get to the right place and teleport back to the entry of the dungeon;" +
                "Azar:Normal teleport spell won’t work in there?;" +
                "Clint:Yes... many experts said they cannot identify their current location to cast a teleport spell nor have the ability to use mana to reach and select a point outside the dungeon;" +
                "Azar:So basically coming there is basically suicide, why do people still go there?;" +
                "Clint:Actually they found a lot of unique and special equipment and relic which have a HUGGGGGGE price on industry;" +
                "Azar:That promising? How about the chance of getting out of there?;" +
                "Clint:Well… there’s a report that every 100 guys get in... only 4 could get out;" +
                "Azar:There’s no fucking way I’m risking my life for wealth;" +
                "Clint:Chill out...I don't want to lose my best detective too. The reason I called you in is because I have something you might want to check. (Show a piece of device that even the dev doesn't even know wtf is that thing?);" +
                "Clint:This...Thing can create a map of the surrounding area and also be able to teleport out at anytime;" +
                "Azar:Isn’t that too useful!? Just how much did you invest to this expedition?;" +
                "Clint:By taking your monthly salary :3. Oh and it allows you to let you bring one more with you to get teleport at the same place as you too!;" +
                "Azar:How to use it then?;" +
                "Clint:Just use teleport spell with this device as starting target and it will fill out the end target for you at the entry of dungeon.;" +
                "Azar:Fine...I'll take this case then.;" +
                "Clint:Well then...Wish you luck, Azar!;" +
                ":And then our Azar decides to go to a place that will change his life forever...again?";
        dialogMakerHelper(dialogString);
    }

    private static void registerFlamitaHaveAWalk(){


        dialogMakerHelper("FlamitaHaveAWalk_Start;Flamita:Lucky for you I have some free time right now...So what are you calling me for?");
        dialogMakerHelper("FlamitaHaveAWalk_Other;Flamita:Well the dev is tired for these kind of dialog... choose another one");
        dialogMakerHelper("FlamitaHaveAWalk_Ignari;" +
                "Flamita:Eh...How long did you know I'm an Ignari;" +
                "Flamita:Anyway, an Ignari is just a human with blessing of Phoenix;" +
                "Flamita:Not only we cannot die under any normal circumstances but we also can use Phoenix energy to heal or cast some powerful spell;" +
                "Flamita:But we can still die by age and the Phoenix energy regeneration speed is not that fast;" +
                "Azar:It still doesn't seem fair for others...;" +
                "Flamita:Don't worry only pure royal of Pyro kingdom are Ignari;" +
                "Azar:Why don’t you make a massive army then...?;" +
                "Flamita:THAT... doesn’t sound ethical...And also Phoenix blessing is weaker for each people it bless;" +
                "Azar:I’m sorry...So how many Ignari are there left?;" +
                "Flamita:Hmmm...3...and...'him'... if he doesn’t count;" +
                "Azar:'him'?;" +
                "Flamita:Just forget it...3 Ignari are left;" +
                "Azar:They are all your family to you, right?;" +
                "Flamita:That would be correct;" +
                "Azar:(I remember the king of Pyro kingdom is still alive. If she has a mother...or a sibling...);" +
                "Azar:Well...That’s good to know...I'll call you later when I need to go to Lost Dungeon together with me;" +
                "Flamita:You know I never miss a chance to be stronger. You can count on me");

        DialogLibrary library = DialogLibrary.getInstance();
        linkChainTo("FlamitaHaveAWalk_Other","FlamitaHaveAWalk_Start_1");
        DialogEntry branchEntry = library.getDialog("FlamitaHaveAWalk_Start_1");
        if (branchEntry != null) {
            branchEntry.getOptions().clear();
            branchEntry.withOption(new DialogOption("Talk about other").withNextDialog("FlamitaHaveAWalk_Other_1"));
            branchEntry.withOption(new DialogOption("Ask about Ignari").withNextDialog("FlamitaHaveAWalk_Ignari_1"));
        }
    }
    
    /**
     * Show the Azar and Leuna test dialog
     * This can be called from anywhere to test the dialog system
     */
    public static void showAzarLeunaDialog() {
        showDialogByTitle("azar_leuna");
    }

    /**
     * Show a random dialog matching the given purpose.
     * @param purpose the purpose tag to filter dialogs
     */
    public static void showRandomDialogWithPurpose(String purpose) {
        if (purpose == null || purpose.trim().isEmpty()) {
            System.out.println("showRandomDialogWithPurpose: purpose cannot be empty");
            return;
        }
        DialogLibrary library = DialogLibrary.getInstance();
        DialogContext context = new DialogContext();
        DialogEntry entry = library.getDialogByPurpose(purpose, context);
        if (entry == null) {
            System.out.println("No dialogs found with purpose: " + purpose);
            return;
        }
        String firstId = entry.getId();
        DialogSystem system = DialogSystem.getInstance();
        List<String> dialogIds = new ArrayList<>();
        dialogIds.add(firstId);
        system.startDialog(dialogIds, context);
    }
    
    /**
     * Helper function to convert a string format into dialog entries
     * Format: "Title;Hero1:line1;Hero2:line2;Hero1:line3"
     * - Title is the base ID
     * - Each dialog entry will have ID "Title_number" (e.g., "Title_1", "Title_2")
     * - Semicolon (;) separates each value
     * - Colon (:) separates hero name from text
     * - All \n characters are removed before processing
     * 
     * @param dialogString The string to convert (format: "Title;Hero1:line1;Hero2:line2;...")
     */
    public static void dialogMakerHelper(String dialogString) {
        if (dialogString == null || dialogString.trim().isEmpty()) {
            System.out.println("DialogMakerHelper: Empty or null string provided");
            return;
        }
        
        // Remove all \n characters
        String cleanedString = dialogString.replaceAll("\n", "").trim();
        
        // Split by semicolon to get parts
        String[] parts = cleanedString.split(";");
        
        if (parts.length < 2) {
            System.out.println("DialogMakerHelper: Invalid format. Need at least 'Title;Hero:text'");
            return;
        }
        
        // First part is the title/base ID
        String title = parts[0].trim();
        if (title.isEmpty()) {
            System.out.println("DialogMakerHelper: Title cannot be empty");
            return;
        }
        
        DialogLibrary library = DialogLibrary.getInstance();
        List<DialogEntry> dialogEntries = new ArrayList<>();
        
        // Process remaining parts (Hero:text pairs)
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) {
                continue; // Skip empty parts
            }
            
            // Split by colon to get hero name and text
            int colonIndex = part.indexOf(':');
            if (colonIndex == -1) {
                System.out.println("DialogMakerHelper: Invalid format at part " + i + ": '" + part + "'. Expected 'Hero:text'");
                continue;
            }
            
            String heroName = part.substring(0, colonIndex).trim();
            String text = part.substring(colonIndex + 1).trim();
            
            if (heroName.isEmpty()) {
                heroName = null; // No speaker (narrator)
            }
            
            if (text.isEmpty()) {
                System.out.println("DialogMakerHelper: Empty text at part " + i);
                continue;
            }
            
            // Create dialog entry with ID "Title_number"
            String dialogId = title + "_" + i;
            DialogEntry entry = new DialogEntry(dialogId, heroName, text);
            
            // Chain to next dialog (except for the last one)
            if (i < parts.length - 1) {
                String nextDialogId = title + "_" + (i + 1);
                entry.withNextDialog(nextDialogId);
            }
            
            dialogEntries.add(entry);
        }
        
        // Register all dialog entries
        for (DialogEntry entry : dialogEntries) {
            library.register(entry);
        }
        
        System.out.println("DialogMakerHelper: Registered " + dialogEntries.size() + " dialog entries with base ID: " + title);
    }

    /**
     * Helper that also assigns a purpose to each entry.
     */
    public static void dialogMakerHelperWithPurpose(String dialogString, String purpose) {
        if (dialogString == null || dialogString.trim().isEmpty()) {
            System.out.println("DialogMakerHelperWithPurpose: Empty or null string provided");
            return;
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            System.out.println("DialogMakerHelperWithPurpose: Purpose cannot be empty");
            return;
        }

        String cleanedString = dialogString.replaceAll("\\n", "").trim();
        String[] parts = cleanedString.split(";");
        if (parts.length < 2) {
            System.out.println("DialogMakerHelperWithPurpose: Invalid format. Need at least 'Title;Hero:text'");
            return;
        }
        String title = parts[0].trim();
        if (title.isEmpty()) {
            System.out.println("DialogMakerHelperWithPurpose: Title cannot be empty");
            return;
        }

        DialogLibrary library = DialogLibrary.getInstance();
        List<DialogEntry> dialogEntries = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) {
                continue;
            }
            int colonIndex = part.indexOf(':');
            if (colonIndex == -1) {
                System.out.println("DialogMakerHelperWithPurpose: Invalid format at part " + i + ": '" + part + "'. Expected 'Hero:text'");
                continue;
            }
            String heroName = part.substring(0, colonIndex).trim();
            String text = part.substring(colonIndex + 1).trim();
            if (heroName.isEmpty()) {
                heroName = null;
            }
            if (text.isEmpty()) {
                System.out.println("DialogMakerHelperWithPurpose: Empty text at part " + i);
                continue;
            }
            String dialogId = title + "_" + i;
            DialogEntry entry = new DialogEntry(dialogId, heroName, text);
            if(i==1){
                entry.withPurpose(purpose);
            }
            if (i < parts.length - 1) {
                String nextDialogId = title + "_" + (i + 1);
                entry.withNextDialog(nextDialogId);
            }
            dialogEntries.add(entry);
        }
        for (DialogEntry entry : dialogEntries) {
            library.register(entry);
        }
        System.out.println("DialogMakerHelperWithPurpose: Registered " + dialogEntries.size() + " dialog entries with base ID: " + title + " and purpose: " + purpose);
    }

    /**
     * Link the last dialog in a chain (created by dialogMakerHelper) to a target dialog ID.
     */
    private static void linkChainTo(String title, String targetDialogId) {
        DialogLibrary library = DialogLibrary.getInstance();
        int idx = 1;
        DialogEntry last = null;
        while (true) {
            String id = title + "_" + idx;
            DialogEntry entry = library.getDialog(id);
            if (entry == null) {
                break;
            }
            last = entry;
            idx++;
        }
        if (last != null) {
            List<String> next = last.getNextDialogIds();
            next.clear();
            next.add(targetDialogId);
        }
    }
    
    /**
     * Show a dialog created by dialogMakerHelper
     * @param title The base title/ID used when creating the dialog
     */
    public static void showDialogByTitle(String title) {
        DialogSystem system = DialogSystem.getInstance();
        DialogContext context = new DialogContext();
        
        // Start with the first dialog (title_1)
        String firstDialogId = title + "_1";
        List<String> dialogIds = new ArrayList<>();
        dialogIds.add(firstDialogId);
        boolean run = system.startDialog(dialogIds, context);
        if(!run){return;}
        //If intro then show the menu
        if(title.equals("intro")||title.contains("Walk")){
            system.setOnDialogEnd(() -> {
                // Show menu UI instead of map
                if (menuUI != null) {
                    menuUI.show();
                }
                system.setOnDialogEnd(() -> {
                    if (mapUI != null) {
                        mapUI.showSelectedPath();
                    }
                });
            });
        }

    }
    
    /**
     * Insert a dialog line into an existing dialog at a specific position
     * @param title The base title/ID of the dialog to insert into
     * @param position The position to insert at (1-based, e.g., 2 means after the first line)
     * @param insertString The string to insert in format "Hero:Line"
     */
    public static void insertToDialog(String title, int position, String insertString) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("insertToDialog: Title cannot be empty");
            return;
        }
        
        if (position < 1) {
            System.out.println("insertToDialog: Position must be at least 1");
            return;
        }
        
        if (insertString == null || insertString.trim().isEmpty()) {
            System.out.println("insertToDialog: Insert string cannot be empty");
            return;
        }
        
        // Remove all \n characters
        String cleanedString = insertString.replaceAll("\n", "").trim();
        
        // Parse the insert string (format: "Hero:Line")
        int colonIndex = cleanedString.indexOf(':');
        if (colonIndex == -1) {
            System.out.println("insertToDialog: Invalid format. Expected 'Hero:Line'");
            return;
        }
        
        String heroName = cleanedString.substring(0, colonIndex).trim();
        String text = cleanedString.substring(colonIndex + 1).trim();
        
        if (text.isEmpty()) {
            System.out.println("insertToDialog: Text cannot be empty");
            return;
        }
        
        if (heroName.isEmpty()) {
            heroName = null; // No speaker (narrator)
        }
        
        DialogLibrary library = DialogLibrary.getInstance();
        
        // Create the new dialog entry with insert ID (use timestamp or counter to ensure uniqueness)
        String insertDialogId = title + "_insert_" + position + "_" + System.currentTimeMillis();
        DialogEntry insertEntry = new DialogEntry(insertDialogId, heroName, text);
        
        // Traverse the actual chain to find what's at the target position
        // Start from the first dialog
        String firstDialogId = title + "_1";
        DialogEntry currentEntry = library.getDialog(firstDialogId);
        
        if (currentEntry == null) {
            System.out.println("insertToDialog: Dialog chain not found. First dialog '" + firstDialogId + "' does not exist.");
            return;
        }
        
        // Special case: inserting at position 1
        if (position == 1) {
            // The new entry becomes the first, pointing to what was previously first
            insertEntry.withNextDialog(firstDialogId);
            library.register(insertEntry);
            System.out.println("insertToDialog: Inserted dialog at position " + position + " (beginning of dialog). Note: You may need to update the starting dialog ID to: " + insertDialogId);
            return;
        }
        
        // Traverse the chain to find the dialog at position (position - 1)
        // We need to insert AFTER the dialog at position (position - 1)
        DialogEntry previousEntry = null;
        int currentPosition = 1;
        
        // Traverse until we reach position (position - 1)
        while (currentPosition < position && currentEntry != null) {
            previousEntry = currentEntry;
            
            // Get the next dialog in the chain
            List<String> nextIds = currentEntry.getNextDialogIds();
            if (nextIds.isEmpty()) {
                // Reached end of chain before reaching target position
                break;
            }
            
            // Follow the first next dialog (assuming linear chain)
            String nextId = nextIds.get(0);
            currentEntry = library.getDialog(nextId);
            
            // Update tracking
            if (currentEntry != null) {
                currentPosition++;
            } else {
                break;
            }
        }
        
        // Check if we found the right position
        if (currentPosition < position - 1) {
            System.out.println("insertToDialog: Cannot reach position " + position + ". Chain only has " + currentPosition + " dialogs.");
            return;
        }
        
        // Now previousEntry is the dialog at position (position - 1)
        // currentEntry (or what previousEntry points to) is what's at position
        // We want to insert between them
        
        if (previousEntry == null) {
            System.out.println("insertToDialog: Could not find dialog at position " + (position - 1));
            return;
        }
        
        // Get what previousEntry currently points to (this is what will come after our insert)
        List<String> nextDialogs = previousEntry.getNextDialogIds();
        String nextDialogId = nextDialogs.isEmpty() ? null : nextDialogs.get(0);
        
        // Update previous entry to point to insert entry
        nextDialogs.clear();
        nextDialogs.add(insertDialogId);
        
        // Insert entry points to what previousEntry was pointing to
        if (nextDialogId != null) {
            insertEntry.withNextDialog(nextDialogId);
        }
        
        library.register(insertEntry);
        System.out.println("insertToDialog: Inserted dialog at position " + position);
    }
    
    /**
     * Insert multiple dialog lines into an existing dialog at a specific position
     * The insertString uses the same format as dialogMakerHelper: "Hero1:line1;Hero2:line2;Hero3:line3"
     * This function will insert all lines sequentially starting at the given position
     * 
     * @param title The base title/ID of the dialog to insert into
     * @param position The position to start inserting at (1-based)
     * @param insertString The string containing multiple dialog lines in format "Hero1:line1;Hero2:line2;..."
     */
    public static void insertDialogToDialog(String title, int position, String insertString) {
        if (insertString == null || insertString.trim().isEmpty()) {
            System.out.println("insertDialogToDialog: Insert string cannot be empty");
            return;
        }
        
        // Remove all \n characters
        String cleanedString = insertString.replaceAll("\n", "").trim();
        
        // Split by semicolon to get individual dialog lines
        String[] parts = cleanedString.split(";");
        
        if (parts.length == 0) {
            System.out.println("insertDialogToDialog: No dialog lines found in insert string");
            return;
        }
        
        // Insert each line sequentially, starting at the given position
        // Each insertion shifts subsequent positions, so we insert in order
        int currentPosition = position;
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                insertToDialog(title, currentPosition, trimmedPart);
                // After inserting at currentPosition, the next insert should be at currentPosition + 1
                currentPosition++;
            }
        }
        
        System.out.println("insertDialogToDialog: Inserted " + parts.length + " dialog lines starting at position " + position);
    }
    
    /**
     * Print all registered dialogs to console
     */
    public static void printAllRegisteredDialogs() {
        DialogLibrary library = DialogLibrary.getInstance();
        Map<String, DialogEntry> allDialogs = library.getAllDialogs();
        
        System.out.println("\n========== ALL REGISTERED DIALOGS ==========");
        System.out.println("Total dialogs registered: " + allDialogs.size());
        System.out.println("--------------------------------------------");
        
        if (allDialogs.isEmpty()) {
            System.out.println("No dialogs registered yet.");
        } else {
            // Sort by ID for easier reading
            List<String> sortedIds = new ArrayList<>(allDialogs.keySet());
            sortedIds.sort(String::compareTo);
            
            for (String id : sortedIds) {
                DialogEntry entry = allDialogs.get(id);
                String speaker = entry.getSpeakerName() != null ? entry.getSpeakerName() : "[Narrator]";
                String text = entry.getText();
                List<String> nextDialogs = entry.getNextDialogIds();
                
                System.out.println("ID: " + id);
                System.out.println("  Speaker: " + speaker);
                System.out.println("  Text: " + text);
                if (!nextDialogs.isEmpty()) {
                    System.out.println("  Next: " + String.join(", ", nextDialogs));
                }
                System.out.println();
            }
        }
        
        System.out.println("==========================================\n");
    }
}

