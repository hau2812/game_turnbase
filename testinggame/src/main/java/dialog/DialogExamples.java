package dialog;

import characters.Observer;

/**
 * Example dialog registrations
 * This file shows how to register dialogs with the system
 */
public class DialogExamples {
    
    public static void registerExampleDialogs() {
        DialogLibrary library = DialogLibrary.getInstance();
        
        // Example 1: Simple greeting dialog
        DialogEntry greeting1 = new DialogEntry("greeting_hero1_hero2", "Hero1", "Good morning!!!")
            .withPurpose("greeting")
            .withCondition(ctx -> ctx.hasHero("Hero1") && ctx.hasHero("Hero2"))
            .withImage("assets/textures/sprites/hero1_portrait.png");
        
        library.register(greeting1);
        
        // Example 2: Greeting with mood variant
        DialogEntry greeting2 = new DialogEntry("greeting_hero2", "Hero2", "Good morning")
            .withPurpose("greeting")
            .withCondition(ctx -> ctx.hasHero("Hero2"))
            .withVariant(new DialogVariant("Good morning!!!", 
                ctx -> ctx.getHeroMood("Hero2") > 1))
            .withVariant(new DialogVariant("yawn!!", 
                ctx -> ctx.getHeroMood("Hero2") <= 1))
            .withImage("assets/textures/sprites/hero2_portrait.png");
        
        library.register(greeting2);
        
        // Example 3: Dialog with options
        DialogEntry campfireTalk = new DialogEntry("campfire_talk_1", "Hero1", 
            "The campfire is warm. Should we rest?")
            .withPurpose("campfire_talk")
            .withCondition(ctx -> ctx.hasHero("Hero1"))
            .withOption(new DialogOption("Yes, let's rest")
                .withAction(ctx -> {
                    // Increase HP for all heroes
                    for (Observer.characterSlot hero : ctx.getHeroes()) {
                        if (hero != null) {
                            hero.setCurrentHp(hero.getCharacter().getHp());
                        }
                    }
                    return null;
                })
                .withNextDialog("campfire_rest"))
            .withOption(new DialogOption("No, let's continue")
                .withNextDialog("campfire_continue"));
        
        library.register(campfireTalk);
        
        // Example 4: Follow-up dialog
        DialogEntry campfireRest = new DialogEntry("campfire_rest", null, 
            "The party rests by the campfire and regains their strength.")
            .withNextDialog("campfire_talk_2");
        
        library.register(campfireRest);
        
        // Example 5: Dialog that adds more dialogs dynamically
        DialogEntry campfireTalk2 = new DialogEntry("campfire_talk_2", "Hero2", 
            "That was refreshing!")
            .withOnShowAction(ctx -> {
                // Dynamically add more dialogs based on context
                DialogSystem.getInstance().addDialogsByPurpose("campfire_talk", ctx, true);
                return null;
            });
        
        library.register(campfireTalk2);
    }
    
    /**
     * Example: Start a campfire dialog scene
     */
    public static void startCampfireDialog(Observer.characterSlot[] heroes) {
        DialogContext context = new DialogContext(heroes);
        DialogSystem system = DialogSystem.getInstance();
        
        // Add greeting dialogs (randomly select one if multiple match)
        system.addDialogsByPurpose("greeting", context, true);
        
        // Add campfire talk dialogs
        system.addDialogsByPurpose("campfire_talk", context, true);
        
        // Start the dialog sequence
        // The system will process them in order
    }
}

