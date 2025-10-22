package event;

import map.MapPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random dialogue scripts for map events
 */
public class RandomDialogueGenerator {
    private static final Random random = new Random();
    
    // Forest dialogue templates
    private static final String[][] FOREST_DIALOGUES = {
        {
            "Mysterious Voice : The forest whispers ancient secrets...",
            "You : Who's there? Show yourself!",
            "Forest Spirit : I am the guardian of these woods. What brings you here?",
            "You : I seek passage through this forest."
        },
        {
            "You : This place feels magical...",
            "Elder Tree : Indeed, young traveler. The forest has seen many ages.",
            "You : An talking tree? Amazing!",
            "Elder Tree : Take this blessing for your journey."
        },
        {
            "Wild Animal : *Growl*",
            "You : Easy there, I mean no harm.",
            "Wild Animal : *Sniff* You smell... friendly.",
            "You : Here, have some food. Let's be friends."
        },
        {
            "You : I found a strange glowing fountain...",
            "Fountain Spirit : Drink from my waters, brave one.",
            "You : Is it safe?",
            "Fountain Spirit : Only those pure of heart may benefit from my gift."
        }
    };
    
    // Mountain dialogue templates
    private static final String[][] MOUNTAIN_DIALOGUES = {
        {
            "You : The wind is so strong up here...",
            "Mountain Sage : These peaks test the worthy.",
            "You : Who are you, elder?",
            "Mountain Sage : One who has climbed higher than most. Take my wisdom."
        },
        {
            "Ice Elemental : Freezing winds... eternal cold...",
            "You : This cold is unbearable!",
            "Ice Elemental : Your determination warms my frozen heart.",
            "You : Thank you for sparing me."
        },
        {
            "You : What's this ancient altar doing here?",
            "Ancient Voice : A place of power from the old world...",
            "You : The stones are glowing!",
            "Ancient Voice : Accept the blessing of the mountain."
        },
        {
            "Dragon Spirit : A mortal dares to climb this high?",
            "You : I seek to reach the summit!",
            "Dragon Spirit : Your courage is admirable. I shall aid you.",
            "You : Thank you, great one!"
        }
    };
    
    // Village dialogue templates
    private static final String[][] VILLAGE_DIALOGUES = {
        {
            "Villager : Welcome, traveler! Our humble village is safe.",
            "You : Thank you. I could use some rest.",
            "Villager : Please, stay at the inn. First night is free!",
            "You : You're too kind."
        },
        {
            "Merchant : Looking for supplies, friend?",
            "You : What do you have?",
            "Merchant : The finest goods from all the lands!",
            "You : I'll take a look."
        },
        {
            "Old Woman : You have the look of a hero about you...",
            "You : I'm just a traveler, ma'am.",
            "Old Woman : No... destiny walks with you. Take this charm.",
            "You : Thank you, wise one."
        },
        {
            "Innkeeper : Hard day's journey?",
            "You : You have no idea...",
            "Innkeeper : Rest here. Hot meal and warm bed await.",
            "You : That sounds perfect."
        }
    };
    
    /**
     * Generate a random dialogue script based on path type
     */
    public static List<String> generateRandomDialogue(MapPath.PathType pathType) {
        String[][] dialoguePool = getDialoguePool(pathType);
        String[] selectedDialogue = dialoguePool[random.nextInt(dialoguePool.length)];
        
        List<String> script = new ArrayList<>();
        for (String line : selectedDialogue) {
            script.add(line);
        }
        
        return script;
    }
    
    /**
     * Generate a random dialogue with choices
     */
    public static DialogueWithChoices generateDialogueWithChoices(MapPath.PathType pathType) {
        List<String> script = generateRandomDialogue(pathType);
        
        // Add a choice at the end
        script.add("What will you do?");
        
        DialogueWithChoices result = new DialogueWithChoices();
        result.script = script;
        result.optionIndex = script.size() - 1;
        
        // Generate choices based on path type
        switch (pathType) {
            case FOREST:
                result.options = new String[]{
                    "Accept the forest's blessing",
                    "Decline politely",
                    "Ask for more information"
                };
                break;
            case MOUNTAIN:
                result.options = new String[]{
                    "Accept the mountain's gift",
                    "Continue climbing alone",
                    "Request guidance"
                };
                break;
            case VILLAGE:
                result.options = new String[]{
                    "Accept the hospitality",
                    "Decline and move on",
                    "Trade with locals"
                };
                break;
            default:
                result.options = new String[]{
                    "Accept",
                    "Decline",
                    "Ask questions"
                };
                break;
        }
        
        return result;
    }
    
    private static String[][] getDialoguePool(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST:
                return FOREST_DIALOGUES;
            case MOUNTAIN:
                return MOUNTAIN_DIALOGUES;
            case VILLAGE:
                return VILLAGE_DIALOGUES;
            default:
                return FOREST_DIALOGUES;
        }
    }
    
    /**
     * Helper class to hold dialogue with choice information
     */
    public static class DialogueWithChoices {
        public List<String> script;
        public int optionIndex;
        public String[] options;
    }
}

