package dialog;

import audio.AudioManager;
import battle.BattleSystem;
import battle.BattleUI;
import characters.BuffDebuff;
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
        registerFlamitaBossFight();
        registerFirstRecruitDialogs("Flamita");
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
        if(testing.hasFlamitaBoss) {
            available.remove("Flamita");
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
                if(registerFirstRecruitDialogs(selectedHero)&&library.getDialog("recruitDialog"+selectedHero+"_1")!=null){
                    option.withNextDialog("recruitDialog"+selectedHero+"_1");
                }
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



        if(!testing.getStatus().contains("met"+heroName)){
            testing.addStatus("met"+heroName);
        }
        System.out.println("Added " + heroName + " to the party! Party now has: " + Arrays.toString(updatedHeroes));
    }
    /**
     * Register battle victory dialogs
     * Registers the base "battle_victory" dialog structure
     */
    private static boolean registerFirstRecruitDialogs(String heroName){
        if(false&&testing.getStatus().contains("met"+heroName)){
            return false;
        }else{
            if(heroName.equals("Flamita")) {
                dialogMakerHelper("recruitDialogFlamita;" +
                        ":From afar our party can see a female warrior welding a flame sword slashing her enemy;" +
                        "???:Hmm...How can I getting stronger if these... thing are so weak...;" +
                        "???:...;" +
                        ":(suddenly she swing a flaming wave into our party...);" +
                        "???:Who's there ?;" +
                        "Azar:(Jumping out)Chill... we are just walking by...");

                dialogMakerHelper("recruitDialogFlamitaBasic;" +
                        "???:So you are those idiots who try to get some relic from here hah?;" +
                        "Azar:Ehm...Ye... kinda like that;" +
                        "???:Just stay out of my way...;" +
                        "Azar:(So she came here not to get relic...and she doesn't look like she'd accidentally walk in here...that only mean...);" +
                        "Azar:So...ehm...I've just loot a map pointing at somewhere...There could be a boss...Wanna come ?:>;" +
                        "???:...Sure thing... just don't waste my time;" +
                        "Azar:And your name are...?;" +
                        "Flamita:Call me Flamita;");
                dialogMakerHelper("recruitDialogFlamitaHaveMet;" +
                        "Flamita:Oh...Azar...why need to be so sneaky;" +
                        "Azar:How could I see you that far._.?;" +
                        "Flamita:Have a slot?;" +
                        "Azar:Of course;");
                dialogMakerHelper("recruitDialogFlamitaHaveMet2;" +
                        ":Ok the dialog should end here but what if Flamita have never met Azar then...");
                dialogMakerHelper("recruitDialogFlamitaWithLeuna;" +
                        "Leuna:Can't believe I find you here Flamita...;" +
                        "Flamita:Of course you guy getting here too...;" +
                        "Flamita:But who's this guy... where's your 'Master'?;" +
                        "Leuna:He tell me to investigate this place...and this guy is a guinea pig;" +
                        "Flamita:Cool...take me with you;" +
                        "Azar:(whisper)Hey Leuna you know this girl?;" +
                        "Leuna:She's a friend from work...Just don't stand in her way and you'll be fine...(or if you as strong as 'him')");

                dialogMakerHelper("recruitDialogFlamitaWithChigon;" +
                        "Chigon:Ah...that the 'Matchstick' girl, what'cha doin' here?;" +
                        "Flamita:Oh...the 'Mixed Grill' seem very active ha? No crawling behind your 'Master' today?;" +
                        "Chigon:Bah...too bad he's busy...don't worry this guy is 'fun' too, we're going to beat some big guy...wanna join ?;" +
                        "Flamita:How about we do one round before we decide that ?;" +
                        "Chigon:Nah...'Master' will be sad if I not focusing on duties...but we can 'play' after this >:];");

                if(testing.getStatus().contains("metFlamita")){
                    linkChainTo("recruitDialogFlamita", "recruitDialogFlamitaHaveMet_1");
                    if(battleSystem!=null&&battleSystem.hasHeroName("Leuna")||battleSystem.hasHeroName("Chigon")){
                        linkChainToTheEndOf("recruitDialogFlamita", "recruitDialogFlamitaHaveMet2_1");
                    }
                }else {
                    linkChainTo("recruitDialogFlamita", "recruitDialogFlamitaBasic_1");
                }
                if(battleSystem!=null) {
                    if (battleSystem.hasHeroName("Leuna")) {
                        linkChainToTheEndOf("recruitDialogFlamita", "recruitDialogFlamitaWithLeuna_1");
                    }
                    if (battleSystem.hasHeroName("Chigon")) {
                        linkChainToTheEndOf("recruitDialogFlamita", "recruitDialogFlamitaWithChigon_1");
                    }
                }
                linkChainToTheEndOf("recruitDialogFlamitaWithChigon","recruitDialog-1_1");

            }
            return true;
        }
    }

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
            showDialogByTitle("battle_victory",null);
        }if(battleSystem.containDefeatEnemies("Flamita ?")){
            showBattleVictoryDialogFlamitaBoss();
        }else if(battleSystem.containDefeatEnemies("Flamita The Immortal Phoenix")){
            showBattleVictoryDialogFlamitaBoss2();
            testing.return_time+=1;
        }else if(battleSystem.containDefeatEnemies("Virell the Lifedrinker Sentinel")){
            showBattleVictoryDialogVirell();
            testing.storyItemInventory.addStoryItem("necro_sword");
        }else if(battleSystem.containDefeatEnemies("Solareth the Living Beacon")){
            showBattleVictoryDialogSolareth();
            testing.storyItemInventory.addStoryItem("radiant_core");
        }else if(battleSystem.containDefeatEnemies("Spiritual Monster")){
            showDialogByTitle("LitaruEndDialog4","menu");
            testing.storyItemInventory.addStoryItem("litaru_sword");
            testing.storyItemInventory.removeStoryItem("necro_sword");
            testing.storyItemInventory.removeStoryItem("radiant_core");
            testing.storyItemInventory.removeStoryItem("Phoenix_feather");
        }
    }
    public static void showBattleVictoryDialogFlamitaBoss(){
        dialogMakerHelper("FlamitaBossBattleVictory;" +
                "Azar:Ok she is stunned...let's get out of here before she get back up;" +
                "Flamita?:(shit...I'm so careless)");
        dialogMakerHelper("FlamitaBossBattleVictoryWithPieberry;" +
                "Pieberry:You guy get out of here first, I'll catch up later;" +
                "Azar:What?...No...What are you waiting for?;" +
                "Pieberry:I have some business to do;" +
                "Azar:(Shit... I started the teleport spell so there are no way to wait);" +
                "Azar:(If I stop the spell then I could not have enough mana to cast it again);" +
                "Azar:I have no idea what you're trying to do...but play safe please...;" +
                "Pieberry:Don't worry I've face thing more dangerous than this;" +
                ":After Azar and his party get out it's the time Flamita get back up;" +
                "Flamita:I don't know If this is brave or stupid, but isn't get out of here is your goal?;" +
                "Pieberry:No...Not while you are infected, leaving you here only make it worse for you...or the other;" +
                "Flamita:What are you taking about?;" +
                "Pieberry:Dark Magic Stone... a dangerous thing at my world somehow get to here, I hope you are the first and only one who have been infected;" +
                "Flamita:What a nonsense...You are not my target so get out of my way!;" +
                "Pieberry:No...I must cure you first;" +
                "Pieberry:I...Piebe...no...I...Lucia...daughter of Temple Lord Elysion shall purify you");
        if(battleSystem.hasHeroName("Pieberry")){
            linkChainTo("FlamitaBossBattleVictory","FlamitaBossBattleVictoryWithPieberry_1");
        }
        showDialogByTitle("FlamitaBossBattleVictory","flamitaBossBattle");
    }
    public static void showBattleVictoryDialogFlamitaBoss2(){
        dialogMakerHelper("FlamitaBoss2BattleVictory;" +
                ":The sounds of burning fires, howling winds, and lightning strikes continue echoed everywhere;" +
                "Flamita:This is the best you got ?;" +
                "Lucia:Ha...(This is bad... she's more powerful than I though...);" +
                "Flamita:Keep entertaining me!(Send multiple fire wave toward Lucia);" +
                "Lucia:(narrowly dodge)(I can't fully use the Spring power due to lack of connection between me and my world);" +
                "Lucia:(Beating her down seem not an option...I need to some how approach her and purify some dark energy then she will get weaker;" +
                "Lucia:(But how...);" +
                "???:Tch...guess it’s my turn to step in again;" +
                ":From the ground, massive blocks of ice rose up, trapping Flamita up;" +
                "Pieberry:Luna!!!;" +
                "Luna:Looking at me don't solve the problem, hurry up and do your thing:/;" +
                "Pieberry:Right...!;" +
                ":Dashing through the flame she channel up her very last of her energy to cast her most proud spell;" +
                ":The dazzling light shine up...too much that even the dev can't see anything and must shifts the scene to Azar;" +
                ":(Outside the dungeon);" +
                ":Azar:(I should make a report about this);" +
                "(A random poster):Warning...Crazy red women inside");
        showDialogByTitle("FlamitaBoss2BattleVictory","");
    }
    public static void showBattleVictoryDialogVirell(){
        dialogMakerHelper("battleVictoryDialogVirell;" +
                "Azar:Okay...here our sword (pick up the sword from the guardian dead body);" +
                "Azar:Legend said the sword will eventually consume all of it user's health...;" +
                "Azar:And after they die, the power of the sword will get out of the sword, seek out and revive the guardian and another cycle begin...;" +
                "Litaru:What a mischievous sword :>;" +
                "Azar:Better not use this thing... How did you even know about this place though;" +
                "Litaru:Hey, don’t underestimate how much an Aureaxis agent knows~;" +
                "Azar:Right...Anyway that done for today...Next thing is...'Radiant Core' right?;" +
                "Litaru:Ye~Call me when you're ready;");
        showDialogByTitle("battleVictoryDialogVirell","");
    }
    public static void showBattleVictoryDialogSolareth(){
        dialogMakerHelper("battleVictorySolareth;" +
                "Azar:Ok...Radiant Core...;" +
                "Azar:This thing can even lit the forge for a day;" +
                "Litaru:Hey Azar look!(pointing in front of her);" +
                "Azar:...?;");
        dialogMakerHelper("battleVictorySolareth2;" +
                "Azar:...;" +
                "Litaru:Sunset is my favorite time of day!;" +
                "Litaru:Doesn't it beautiful Azar...?;" +
                "Azar:...;" +
                ":From the mountain peak, the sunset spills over the city like a sea of gold — sunlight fading across the rooftops, bathing the world in a beauty both majestic and gentle;" +
                "Azar:Yes...Yes it is...;" +
                "Litaru:I love it because it exists only for a fleeting moment, marking the most beautiful time of the day;" +
                ":(After a while watch the sunset together...);" +
                "Azar:Well... we should head back, it's getting dark;" +
                "Litaru:Ye, you right;" +
                "Azar:...no way i'm gonna walk back to the city...let's teleport back;");
        dialogMakerHelper("battleVictorySolareth3;" +
                ":(At the city...);" +
                "Azar:Ok so last piece is the Phoenix feather...Because Phoenix went extinct for long time, finding them is pretty hard;" +
                "Litaru:I can get one by...order one from Aureaxis but it gonna take a while...;" +
                "Azar:I'll try to get one somehow;" +
                "Litaru:Okay...If you find it first then tell me and it's finally time to crack the case;" +
                "Azar:(Phoenix ha...I wonder...;" +
                "dev:Ok there's no way you don't know where to get it...right?");
        linkChainToTheEndOf("battleVictorySolareth","battleVictorySolareth2_1");
        linkChainToTheEndOf("battleVictorySolareth","battleVictorySolareth3_1");
        showDialogByTitle("battleVictorySolareth","");
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
        dialogMakerHelper("FlamitaHaveAWalk_Feather;" +
                "Flamita:Wha...Why do you thing I have it?;" +
                "Azar:...seriously?;" +
                "Flamita:Ok I... does have it, but why do you even need it?;" +
                "Azar:For a case I just got;" +
                "Flamita:Do you even know how to use it?;" +
                "Azar:Don't worry we have many file about it;" +
                "Flamita:Fine...;" +
                "Azar:So you gonna give it to me :> ?;" +
                "Flamita:Not here dumb ass, I'll send it into your office;" +
                "Azar:Nice... thanks Flamita, I owe you this;" +
                "Flamita:Just make sure you don't waste my feather;" +
                "Azar:Sure...sure...;" +
                "Azar:(Wait... when she say 'her's feather' which mean 'her's feather' or 'her's feather'...");
                DialogLibrary library = DialogLibrary.getInstance();
        linkChainTo("FlamitaHaveAWalk_Other","FlamitaHaveAWalk_Start_1");
        DialogEntry branchEntry = library.getDialog("FlamitaHaveAWalk_Start_1");
        if (branchEntry != null) {
            branchEntry.getOptions().clear();
            branchEntry.withOption(new DialogOption("Talk about other").withNextDialog("FlamitaHaveAWalk_Other_1"));
            branchEntry.withOption(new DialogOption("Ask about Ignari").withNextDialog("FlamitaHaveAWalk_Ignari_1"));
            if(testing.getStatus().contains("battleVictorySolareth")&&!testing.getStatus().contains("FlamitaHaveAWalk_Feather")) {
                branchEntry.withOption(new DialogOption("Ask about Phoenix Feather").withNextDialog("FlamitaHaveAWalk_Feather_1"));
                testing.storyItemInventory.addStoryItem("Phoenix_feather");
            }
        }
    }

    public static void registerFlamitaBossFight(){
        dialogMakerHelper("FlamitaBossFightBegin;" +
                ":You see a red hair girl standing next to the way out...");
        dialogMakerHelper("FlamitaBossFightBeginHaveMet;" +
                "Azar:Flamita? What are you doing here?;" +
                "Flamita?:Azar...I'm waiting for you...;" +
                "Azar:Me...? Why... ?;" +
                "Flamita?:I know you have a map for this dungeon...give it to me and no one get hurt;" +
                "Azar:No...you can't have it...it's our way out;" +
                "Flamita?:I didn't ask...and I'll not say again! Leave it now!;" +
                "Azar:Shit...she's crazy...but she is covering the way out...we need to get through her somehow;");
        dialogMakerHelper("FlamitaBossFightBeginHaveNotMet;" +
                "Azar:Ehm...Would you mind to stand aside...We kinda in a hurry;" +
                "???:A...I'm watting for you;" +
                "Azar:Me...? Why... ?;" +
                "???:I know you have a map for this dungeon...give it to me and no one get hurt;" +
                "Azar:No...you can't have it...it's our way out;" +
                "???:I didn't ask...and I'll not say again! Leave it now!;" +
                "Azar:Shit...she's crazy...but she is covering the way out...we need to get through her somehow;");
        dialogMakerHelper("FlamitaBossFightBeginHaveLeuna;" +
                "Leuna:Flamita? I know your reason but does it worth messing with us;" +
                "Flamita?:I don't care...As long as I can become stronger;" +
                "Leuna:Shit...this is not a battle we can win Azar... try escape somehow;");
        dialogMakerHelper("FlamitaBossFightBeginHaveChigon;" +
                "Chigon:A...the 'Matchstick' is burning...;" +
                "Flamita:Get out of my way...this time I won't hold back;" +
                "Chigon:Oh...ehm...my belly is not full so maybe I can't perform at my peak Azar :P;");
        dialogMakerHelper("FlamitaBossFightBeginHavePieberry;" +
                "Pieberry:Wait...some familiar energy is coming out from her;" +
                "Pieberry:This...can't be...Azar you guy should get out of here quickly;");
        dialogMakerHelper("FlamitaBossFightBegin2;" +
                ":The battle with Flamita begin");
        if(testing.getStatus().contains("metFlamita")){
            linkChainTo("FlamitaBossFightBegin","FlamitaBossFightBeginHaveMet_1");
        }else{
            linkChainTo("FlamitaBossFightBegin","FlamitaBossFightBeginHaveNotMet_1");
        }
        if(battleSystem!=null) {
            if (battleSystem.hasHeroName("Leuna")) {
                linkChainToTheEndOf("FlamitaBossFightBegin", "FlamitaBossFightBeginHaveLeuna_1");
            }
            if (battleSystem.hasHeroName("Chigon")) {
                linkChainToTheEndOf("FlamitaBossFightBegin", "FlamitaBossFightBeginHaveChigon_1");
            }
            if (battleSystem.hasHeroName("Pieberry")) {
                linkChainToTheEndOf("FlamitaBossFightBegin", "FlamitaBossFightBeginHavePieberry_1");
            }
            linkChainToTheEndOf("FlamitaBossFightBegin", "FlamitaBossFightBegin2_1");
        }
    }

    public static void showOfficeDialog() {
        dialogMakerHelperWithPurpose("officeDialogBasic1;" +
                "Azar:(Every day is the same...There are no clues for 'it'...);" +
                "Azar:(How long that I have been doing this...);" +
                "Azar:(...);" +
                "Azar:(Maybe...maybe one day...)","officeDialog");
        dialogMakerHelperWithPurpose("officeDialogBasic2;" +
                "Azar:(Lost dungeon ha...What a joke...);" +
                "Azar:(Now people seem to know how to play with this thing...);" +
                "Azar:(Some day the amount of people getting out even higher than getting in...);" +
                "Azar:(But where did the monster get in though...)","officeDialog");
        dialogMakerHelperWithPurpose("officeDialogBasic3;" +
                "Azar:(Who's 'him' they are talking about...);" +
                "Azar:(But he's said to be stronger than Flamita...);" +
                "Azar:(Hope he's a chill guy...)","officeDialog");
        dialogMakerHelper("officeDialogLitaru;" +
                ":With many success investigate run on Lost Dungeon, Azar has be able to bring back a lot of relic and information for the center;" +
                ":But seem like he doesn't have any feeling of satisfaction...;" +
                "Clint:I have heard about your accomplishment Azar... and you deserve a vacation;" +
                "Azar:I don't need a vacation...;" +
                "Clint:Right...Anyway there're a new case about some kind of cult that just has rife at a random village near the city;" +
                "Clint:I'll need you to investigate it and report as soon as possible;" +
                "Azar:Sure...just leave the case file there and I'll get going;" +
                "Clint:...and one more thing Azar...;" +
                "Azar:What else?;" +
                "Clint:How long until you decide to have a family?;" +
                "Azar:Wha...What kind of question is that?;" +
                "Clint:You have working here for 10 years Azar...Don't you thing it's time to take some rest?;" +
                "Azar:I'm still doing good, and also I cannot stop until I find out 'what happened' then;" +
                "Clint:Seem like I can't stop you Azar...but I must say...;" +
                "Clint:Having a purpose in life is good but...You must also seek happiness for yourself;" +
                "Azar:...;" +
                "Clint:Ah...what I'm I saying... Anyway just be careful Azar, cult thing is sometime very ominous");
        dialogMakerHelper("officeDialogAfterVirell;" +
                ":Azar seem to checking some file...;" +
                "Clint:Hey...How is it going Azar?;" +
                "Azar:So far so good...I got every info I need to deal with this case;" +
                "Clint:I saw you bring the Necro Sword to your warehouse, I'll not ask why you need it but is it ok to go get it alone?;" +
                "Azar:Why do you thing I'd go alone?;" +
                "Clint:Oh come on… like I don’t know you already...;" +
                "Clint:The great Azar always 'Well this case needs to be kept confidential, no ally allow';" +
                "Azar:...;" +
                "Clint:Except when going to public dungeon, you always investigate by yourself;" +
                "Azar:Okay...Okay...This time I didn't go alone;" +
                "Clint:WHAT!? WITH WHO!?;" +
                "Azar:There're a girl work at Aureaxis seem to have the same case as me;" +
                "Clint:A GIRL!?;" +
                "Clint:Ok so when do I can get your wedding invitation card;" +
                "Azar:The fuck? We just met... and beside we're just partner of this one case;" +
                "Clint:Oh~I~Don't~Thing~So~Azar~;" +
                "Azar:...;" +
                "Clint:Anyway...All the way from Aureaxis ha? I'll check about that girl for you;" +
                "Azar:Hey I didn't said I ne...;" +
                "Clint:I~'ll~Get~Going~;" +
                "Azar:...");
        dialogMakerHelper("officeDialogAfterSolareth;" +
                "Clint:I have check all the operational cooperation file with Aureaxis but I have no information about your Litaru;" +
                "Azar:(Your? ._.) Seem like she maybe a secret agent then?;" +
                "Clint:I don't know Azar... They always have paper work for every guy work in there;" +
                "Azar:Why do you thing they have to tell you everything;" +
                "Clint:Well...ye... you got a point...;" +
                "Clint:But you need to be careful Azar...;" +
                "Azar:Sure...sure...;");
        dialogMakerHelper("officeDialogAfterLitaru;" +
                "Clint:Oh I found some file about your case Azar;" +
                "Clint:This thing is so old...like 50 years before;" +
                "Azar:WOW!?So helpful...thank you Clint ._.;" +
                "Clint:What?;" +
                "Azar:I've already done this case Clint;" +
                "Clint:Oh...eh...How about the Litaru of your?;" +
                "Azar:She gone...;" +
                "Clint:What...she left this city to go back already?;" +
                "Clint:It's fine Azar...life is full of opportunity;" +
                "Azar:Ye~yea...;" +
                "Clint:Well...you might take some rest, good work Azar;" +
                "Azar:...;");
        if(!testing.getStatus().contains("watchedOfficeDialogLitaru")&&testing.return_time>=5){
            showOnlyDialogByTitle("officeDialogLitaru","menu");
            testing.addStatus("watchedOfficeDialogLitaru");
        }
        else if(!testing.storyItemInventory.hasStoryItem("litaru_sword")&&testing.getStatus().contains("battleVictoryDialogVirell")&&!testing.getStatus().contains("officeDialogAfterVirell")){
            showDialogByTitle("officeDialogAfterVirell","menu");
        }
        else if(!testing.storyItemInventory.hasStoryItem("litaru_sword")&&testing.getStatus().contains("battleVictorySolareth")&&!testing.getStatus().contains("officeDialogAfterSolareth")){
            showDialogByTitle("officeDialogAfterSolareth","menu");
        }
        else if(testing.storyItemInventory.hasStoryItem("litaru_sword")&&!testing.getStatus().contains("officeDialogAfterLitaru")){
            showDialogByTitle("officeDialogAfterLitaru","menu");
        }
        else {
            showRandomDialogWithPurpose("officeDialog", "menu");
        }
    }

    public static void showLitaruStartDialog() {
        dialogMakerHelper("LitaruStart;" +
                ":(At a random village...);" +
                "Azar:Okay...They're really seem to doing something;" +
                ":(Hiding at a bush Azar can see a bunch of people doing something like a ritual);" +
                "Azar:I might have to take a note at this...;" +
                "???:(whisper)Ye...they seem doing something menacing ha...;" +
                "Azar:What the...who's there;" +
                "???:Chill...You are a detective too right?;" +
                "Azar:How...did you know? And how did you find me?;" +
                "???:Well this is the first best hiding spot from the city way to here;" +
                "???:A good view, a discreet place, and no one will go to here,... only a good detective will know this spot;" +
                "Azar:And you are...?;" +
                "???:I'm just a detective from Aureaxis(the biggest center kingdom);" +
                "Azar:Really? From all the way there in Aureaxis? Show me your name card;" +
                "???:Here...:>;" +
                "Azar:So...Miss Litaru right?;" +
                "Litaru:Ah... just call me Litaru;" +
                "Azar:And my name's Azar;" +
                "Litaru:Oh I know...;" +
                "Azar:Really?;" +
                "Litaru:Who doesn't know the best detective in the city Azar?;" +
                "Azar:Well...So how long did you on this case? Did you find anything ?;" +
                "Litaru:It's seem like they're going to perform some kind of ritual at a unknow place;" +
                "Litaru:They need some ingredients too and are buying them with the high prize;" +
                "Azar:Great... Then just get one ingredient and go trade with them to get inside and catch them;" +
                "Litaru:The problem is they only buy all ingredients at once;" +
                "Azar:Damn...What are those ? Maybe I can ask my superior to get some;" +
                "Litaru:'Necro Sword','Radiant Core' and a'Phoenix feather';" +
                "Azar:Ok they don't have it ._.;" +
                "Litaru:Well...I know where to find it though;" +
                "Azar:Hmmm...Seem like we gonna have an adventure;" +
                "Litaru:We:>?;" +
                "Azar:Well...you're here mean you're on this case too right...?;" +
                "Litaru:Sure...let's work this out together...'partner':>;" +
                "Azar:...");
        showDialogByTitle("LitaruStart","menu");
    }
    public static void showLitaruEndDialog(){
        dialogMakerHelper("LitaruEndDialog;" +
                "Litaru:Oh...that was quick;" +
                "Litaru:Hmmm...They said to buy the item in the next few day so... we'll need to wait;" +
                "Azar:Ah...that fine...I'll need to rest for a bit for the show down battle with them;" +
                "Litaru:Ehe...I don't thing you will need to fight at all;" +
                "Azar:Oh...Why are you so sure?;" +
                "Litaru:Detective instinct:>;" +
                "Azar:._.???;");
        dialogMakerHelper("LitaruEndDialog2;" +
                "Litaru:Oh...it's the day ha?;" +
                "Azar:Yep, time to find out what they really do;" +
                "Litaru:You got the items ?;" +
                "Azar:All in this backpack;" +
                "Litaru:Then let's go!;" +
                ":(after a while of walking to the village);" +
                ":(at the village...);" +
                "Litaru:Give me the items, they know me so I should be the one who do the trade;" +
                "Azar:Sure (give the items);" +
                ":(walking...);" +
                "Azar:Wait...why it's so quiet, where's everyone?;" +
                "Litaru:They've left for the ritual...;" +
                "Azar:What...?;");
        dialogMakerHelper("LitaruEndDialog3;" +
                "Litaru:Sorry Azar...but this's the only way...(take all the item they have collected so far and show it in front of her);"+
                "Azar:What are you talking about ???;" +
                "Litaru:The ritual...must be completed;" +
                "Azar:Wha...;" +
                ":(suddenly monsters from every where spawning at thin air, the wind howling, the dark cloud hide the sunlight...);" +
                "Litaru:A...So it's time...;" +
                "Azar:Hey Litaru it's dangerous here, how about we go somewhere and talk about this first;" +
                "Litaru:Don't worry Azar...You're not their target;" +
                "Litaru:(one hand hold the Necro Sword, the other hand slam the Radian Core at the sword);" +
                ":(the energy blooming out, get fed by the sword, the sword is become more active than ever);" +
                "Azar:What are you doing Litaru???;" +
                ":(one monster rush in, pass by Azar jump straight at Litaru);" +
                ":(she hold the sword tight, slashing down the monster with one hit);" +
                ":(but as the negative effect kicking in, her arm show many dark line getting longer);" +
                "Litaru:Ah...(Squeeze the Phoenix Feather, it disappear and releasing Phoenix energy, calming the impact of the sword);" +
                "Azar:No way...;" +
                "Litaru:Azar...like the sunset, people only need one bright moment to be remember...;" +
                "Litaru:So please Azar...Bear witness to this moment of mine;");
        dialogMakerHelper("LitaruEndDialog4;" +
                ":(as the Phoenix energy run out, Litaru fall from the air, Azar rushed in, catch and carry her...);" +
                ":(the spiritual monster seem to complete their task, slowly disappear);" +
                "Litaru:A...sorry that I have lied to you Azar;" +
                ":(her body, no longer protected by Phoenix energy, slowly wither away);" +
                "Azar:Why...why it has to be you?;" +
                "Litaru:Well...a long time ago, the people of this village had kill a innocent child by accident;" +
                "Litaru:Her mother, furiously use all her might to cast a curse on this village;" +
                "Litaru:Each 50 years, one young person who had been affected will be consume by the dark spirit;" +
                "Litaru:I was born just to be dead Azar, alone, cold in the darkness...;" +
                "Litaru:But luckily I have met you, you have show me the last happiness of life;" +
                "Litaru:Sorry I have made you go though this...;" +
                "Litaru:Promise me Azar...that you will find yourself some happiness after this...;" +
                "Azar:I...I promise;" +
                "Litaru:Ehe...thank you Azar, thank you for everything;" +
                ":(as the dark cloud fade away, the sun shine again at two partner...);" +
                "Litaru:Oh...the sun...this is...sunrise right...? So beautiful...;" +
                "Litaru:So...warm...;" +
                ":(her body slowly disappear, leaving Azar standing there);" +
                ":(...);" +
                ":(picking up the sword left by her, now the power have left away, it now only a empty shell);" +
                ":(but in Azar eye, this sword is more than what we can imagine);");
        if(testing.status.contains("LitaruEndDialog")&&!testing.status.contains("LitaruEndDialog2")){
            showDialogByTitle("LitaruEndDialog2","");
            DialogSystem system = DialogSystem.getInstance();
            system.setOnDialogEnd(() -> {
                AudioManager.getInstance().setMusicVolume(0.5);
                showDialogByTitle("LitaruEndDialog3","litaru_last_battle");
                AudioManager.getInstance().playMusic("litaru.mp3",true);
            });
        }else{
            showDialogByTitle("LitaruEndDialog","menu");

        }
    }
    public static void showStartDungeonDialog(){
        dialogMakerHelper("StartDungeonLitaruDialog;" +
                "Azar:So how exactly did you on to this?;" +
                "Litaru:Well...it's actually my vacation, I went to this city because of the delicious food;" +
                "Litaru:But when I walking around the city I heard a suspicious guy talking with a merchant about these kind of stuff;" +
                "Litaru:After that I know my vacation is about to an end...I follow him. get to the village and then found you;" +
                "Azar:Ah...I see...;" +
                "Azar:...maybe I need a vacation after this too...;");
        dialogMakerHelper("StartDungeonLitaruDialog2;" +
                "Azar:Damn all the way to the top of this mountain...this gonna be tough;" +
                "Litaru:Knowing this will be hard... then why don't you call for your party?;");
        dialogMakerHelper("StartDungeonLitaruDialog2Next;" +
                "Azar:(What have I just thinking about ???);" +
                "Azar:Because I want this to be kept confidential;" +
                "Litaru:But sharing this with me is fine?;" +
                "Azar:Well...You're a detective too so... I guess...;" +
                "Azar:Beside, I want to know how an elite from Aureaxis work too;" +
                "Litaru:Ah...Ok,then I can't let you down:>");
        DialogLibrary library = DialogLibrary.getInstance();
        DialogEntry branchEntry = library.getDialog("StartDungeonLitaruDialog2_2");
        if (branchEntry != null) {
            branchEntry.getOptions().clear();
            branchEntry.withOption(new DialogOption("I don't have any budget").withNextDialog("StartDungeonLitaruDialog2Next_1"));
            branchEntry.withOption(new DialogOption("I want only 2 of us together").withNextDialog("StartDungeonLitaruDialog2Next_1"));
            branchEntry.withOption(new DialogOption("The great Azar is enough").withNextDialog("StartDungeonLitaruDialog2Next_1"));
        }
        if(testing.status != null && testing.status.contains("LitaruStart")&&!testing.status.contains("StartDungeonLitaruDialog")){
            showDialogByTitle("StartDungeonLitaruDialog","");
        }else if(testing.status != null && testing.storyItemInventory.hasStoryItem("necro_sword") && !testing.status.contains("StartDungeonLitaruDialog2") ){
            showDialogByTitle("StartDungeonLitaruDialog2","");
        }else{
            mapUI.showSelectedPath();
        }
    }


    public static void showRandomDialogWithPurpose(String purpose,String returnPlace) {
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
        if("menu".equals(returnPlace)){
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
        system.startDialog(dialogIds, context);
    }

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
     * Link the end of a dialog chain (by following nextDialog pointers) to a target dialog ID.
     * Unlike linkChainTo which relies on incremental IDs, this walks the actual nextDialog chain
     * starting from title_1 until a dialog with no nextDialog is found (or a loop is detected).
     */
    private static void linkChainToTheEndOf(String title, String targetDialogId) {
        DialogLibrary library = DialogLibrary.getInstance();
        
        // Start from the first dialog in the chain
        String currentId = title + "_1";
        DialogEntry currentEntry = library.getDialog(currentId);
        if (currentEntry == null) {
            System.out.println("linkChainToTheEndOf: start dialog not found for title: " + title);
            return;
        }
        
        DialogEntry last = null;
        // Guard against accidental cycles
        int guard = 0;
        while (currentEntry != null && guard < 1000) {
            last = currentEntry;
            List<String> nextIds = currentEntry.getNextDialogIds();
            if (nextIds == null || nextIds.isEmpty()) {
                break; // reached the end of chain
            }
            // Assume linear chain: follow the first next dialog
            String nextId = nextIds.get(0);
            currentEntry = library.getDialog(nextId);
            guard++;
        }
        
        if (guard >= 1000) {
            System.out.println("linkChainToTheEndOf: guard limit reached, possible loop for title: " + title);
            return;
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
    public static void showDialogByTitle(String title,String returnPlace) {
        DialogSystem system = DialogSystem.getInstance();
        DialogContext context = new DialogContext();
        
        // Clear any existing filter
        system.setNextDialogFilter(null);
        
        // Start with the first dialog (title_1)
        String firstDialogId = title + "_1";
        List<String> dialogIds = new ArrayList<>();
        dialogIds.add(firstDialogId);
        boolean run = system.startDialog(dialogIds, context);
        if(!run){return;}
        //If intro then show the menu
        if("menu".equals(returnPlace)){
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
        else if("flamitaBossBattle".equals(returnPlace)&&battleSystem.hasHeroName("Pieberry")){
            system.setOnDialogEnd(() -> {
                //Go battle with flamita
                if (mapUI != null && battleSystem != null) {
                    // Get gameMap from mapUI
                    map.GameMap gameMap = mapUI.getGameMap();
                    if (gameMap != null) {
                        // Create Flamita boss fight node
                        map.MapNode flamitaBossNode = gameMap.addFlamitaTheImmortalPhoenixBossFight("forest");
                        
                        if (flamitaBossNode != null) {
                            // Set heroes to only Lucia
                            String[] luciaOnly = {"Lucia"};
                            battleSystem.configureBattle(false, luciaOnly);
                            
                            // Set up battle with map enemies
                            List<Observer.characterSlot> mapEnemies = flamitaBossNode.getEnemies();
                            if (!mapEnemies.isEmpty()) {
                                Observer.characterSlot enemy1 = mapEnemies.get(0);
                                Observer.characterSlot enemy2 = mapEnemies.size() > 1 ? mapEnemies.get(1) : null;
                                Observer.characterSlot enemy3 = mapEnemies.size() > 2 ? mapEnemies.get(2) : null;
                                
                                battleSystem.clearEnemyData();
                                battleSystem.setMapEnemies(enemy1, enemy2, enemy3);
                                
                                // Switch to battle mode
                                mapUI.requestBattleMode();
                            }
                        }
                    }
                }

                system.setOnDialogEnd(() -> {
                    if (mapUI != null) {
                        mapUI.showSelectedPath();
                    }
                });
            });
        }
        else if("litaru_last_battle".equals(returnPlace)) {
            system.setOnDialogEnd(() -> {
                //Go battle with flamita
                if (mapUI != null && battleSystem != null) {
                    // Get gameMap from mapUI
                    map.GameMap gameMap = mapUI.getGameMap();
                    if (gameMap != null) {
                        // Create Flamita boss fight node
                        map.MapNode flamitaBossNode = gameMap.addLitaruFight("forest");

                        if (flamitaBossNode != null) {
                            // Set heroes to only Lucia
                            String[] litaruOnly = {"Litaru "};
                            battleSystem.configureBattle(false, litaruOnly);
                            battleSystem.getHeroSlot().addBuffDebuff(BuffDebuff.getByName("Invulnerable").copy().withDuration(9999));
                            battleSystem.getHeroSlot().addBuffDebuff(BuffDebuff.getByName("Necro Sword").copy().withDuration(9999));
                            // Set up battle with map enemies
                            List<Observer.characterSlot> mapEnemies = flamitaBossNode.getEnemies();
                            if (!mapEnemies.isEmpty()) {
                                Observer.characterSlot enemy1 = mapEnemies.get(0);
                                Observer.characterSlot enemy2 = mapEnemies.size() > 1 ? mapEnemies.get(1) : null;
                                Observer.characterSlot enemy3 = mapEnemies.size() > 2 ? mapEnemies.get(2) : null;

                                battleSystem.clearEnemyData();
                                battleSystem.setMapEnemies(enemy1, enemy2, enemy3);

                                // Switch to battle mode
                                mapUI.requestBattleMode();
                            }
                        }
                    }
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
     * Show a dialog created by dialogMakerHelper, but only continue if next dialogs have the same base title.
     * This method will only show dialogs in the chain that start with the same base title as the first dialog.
     * @param title The base title/ID used when creating the dialog
     * @param returnPlace Where to return after dialog ends ("menu" or null for map)
     */
    public static void showOnlyDialogByTitle(String title, String returnPlace) {
        DialogSystem system = DialogSystem.getInstance();
        DialogContext context = new DialogContext();
        
        // Extract base title (everything before the last underscore and number)
        // For example: "dialogTitle_2" -> "dialogTitle"
        String baseTitle = title;
        int lastUnderscore = title.lastIndexOf('_');
        if (lastUnderscore > 0) {
            // Check if after underscore is a number
            String afterUnderscore = title.substring(lastUnderscore + 1);
            try {
                Integer.parseInt(afterUnderscore);
                // If it's a number, extract base title
                baseTitle = title.substring(0, lastUnderscore);
            } catch (NumberFormatException e) {
                // Not a number, use full title as base
                baseTitle = title;
            }
        }
        
        final String finalBaseTitle = baseTitle;
        
        // Set filter to only allow dialogs with the same base title
        system.setNextDialogFilter(dialogId -> {
            // Extract base title from dialog ID
            String dialogBaseTitle = dialogId;
            int underscore = dialogId.lastIndexOf('_');
            if (underscore > 0) {
                String afterUnderscore = dialogId.substring(underscore + 1);
                try {
                    Integer.parseInt(afterUnderscore);
                    dialogBaseTitle = dialogId.substring(0, underscore);
                } catch (NumberFormatException e) {
                    // Not a number format, use full ID
                    dialogBaseTitle = dialogId;
                }
            }
            // Only allow if base titles match
            return finalBaseTitle.equals(dialogBaseTitle);
        });
        
        // Start with the first dialog (title_1)
        String firstDialogId = title + "_1";
        List<String> dialogIds = new ArrayList<>();
        dialogIds.add(firstDialogId);
        boolean run = system.startDialog(dialogIds, context);
        if(!run){
            system.setNextDialogFilter(null); // Clear filter on failure
            return;
        }
        
        // Set return place callback
        if("menu".equals(returnPlace)){
            system.setOnDialogEnd(() -> {
                // Clear filter when dialog ends
                system.setNextDialogFilter(null);
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
        } else {
            // Clear filter when dialog ends
            system.setOnDialogEnd(() -> {
                system.setNextDialogFilter(null);
                if (mapUI != null) {
                    mapUI.showSelectedPath();
                }
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

