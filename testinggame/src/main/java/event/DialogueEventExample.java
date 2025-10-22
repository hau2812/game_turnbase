package event;

import core.DialogueGame;
import map.MapNode;
import characters.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Example of how to create dialogue events for map nodes
 */
public class DialogueEventExample {

    /**
     * Creates a forest encounter dialogue event
     * @return A DialogueEvent with multiple choices
     */
    public static DialogueEvent createForestEncounterEvent() {
        DialogueEvent event = new DialogueEvent("A mysterious encounter in the forest");
        
        // Add dialogue lines
        event.addLine("??? : *rustling in the bushes*");
        event.addLine("You : Who's there? Show yourself!");
        event.addLine("Stranger : Easy there, traveler. I mean no harm.");
        
        // Set the index where options should appear (0-based)
        event.setOptionIndex(3);
        
        // Add dialogue options with their consequences
        event.addOption("Ask about the area", unused -> {
            System.out.println("Player chose to ask about the area");
            // Here you can add game logic like increasing knowledge, adding map markers, etc.
        });
        
        event.addOption("Request assistance", unused -> {
            System.out.println("Player chose to request assistance");
            // Here you can heal the player, give items, etc.
        });
        
        event.addOption("Threaten the stranger", unused -> {
            System.out.println("Player chose to threaten the stranger");
            // Here you can trigger a battle, lose reputation, etc.
        });
        
        return event;
    }

    /**
     * Creates an ancient ruins dialogue event
     * @return A DialogueEvent with choices that affect gameplay
     */
    public static DialogueEvent createAncientRuinsEvent(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        DialogueEvent event = new DialogueEvent("Ancient ruins with mysterious energy");
        
        event.addLine("*The stone doorway pulses with an eerie blue light*");
        event.addLine("You : These symbols... they're unlike anything I've seen before.");
        event.addLine("*The air around you feels charged with strange energy*");
        
        event.setOptionIndex(3);
        
        // Option 1: Heal the player
        event.addOption("Touch the glowing symbols", unused -> {
            System.out.println("Player chose to touch the symbols");
            if (hero1 != null) {
                float healAmount = 300;
                float currentHp = hero1.getCurrentHp();
                float maxHp = hero1.getCharacter().getHp();
                hero1.setCurrentHp(Math.min(currentHp + healAmount, maxHp));
                System.out.println("Hero healed for " + healAmount + " HP");
            }
            if (hero2 != null) {
                float healAmount = 300;
                float currentHp = hero2.getCurrentHp();
                float maxHp = hero2.getCharacter().getHp();
                hero2.setCurrentHp(Math.min(currentHp + healAmount, maxHp));
            }
        });
        
        // Option 2: Restore MP
        event.addOption("Study the ancient text", unused -> {
            System.out.println("Player chose to study the text");
            if (hero1 != null) {
                float mpAmount = 200;
                float currentMp = hero1.getCurrentMp();
                float maxMp = hero1.getCharacter().getMp();
                hero1.setCurrentMp(Math.min(currentMp + mpAmount, maxMp));
                System.out.println("Hero restored " + mpAmount + " MP");
            }
            if (hero2 != null) {
                float mpAmount = 200;
                float currentMp = hero2.getCurrentMp();
                float maxMp = hero2.getCharacter().getMp();
                hero2.setCurrentMp(Math.min(currentMp + mpAmount, maxMp));
            }
        });
        
        // Option 3: Take damage (risk/reward)
        event.addOption("Force the door open", unused -> {
            System.out.println("Player chose to force the door");
            if (hero1 != null) {
                float damage = 150;
                hero1.setCurrentHp(Math.max(0, hero1.getCurrentHp() - damage));
                System.out.println("Hero took " + damage + " damage");
            }
            if (hero2 != null) {
                float damage = 150;
                hero2.setCurrentHp(Math.max(0, hero2.getCurrentHp() - damage));
            }
            // You could add some special reward here to balance the damage
        });
        
        return event;
    }
    
    /**
     * Example of how to connect a dialogue event to a map node
     * Note: In the actual game, MapUI handles the DialogueGame connection automatically
     * This is just an example of how to manually create and attach a dialogue event
     * @return A map node with a dialogue event
     */
    public static MapNode createEventNode() {
        // Create a new EVENT type map node
        MapNode node = new MapNode("ev001", "Forest Clearing",
                                   "A mysterious clearing in the forest",
                                   MapNode.NodeType.EVENT, 10, 5);

        // Create and attach a dialogue event
        DialogueEvent event = createForestEncounterEvent();
        
        // Attach the event to the node
        node.setEvent(event);

        // Note: The DialogueGame connection is handled automatically by MapUI
        // when the node is activated. You don't need to manually connect it.

        return node;
    }

    /**
     * Example of how to manually test a dialogue event with a DialogueGame instance
     * This is useful for testing dialogue events outside of the map system
     * @param dialogueGame The DialogueGame instance to use for testing
     * @param event The DialogueEvent to test
     */
    public static void testDialogueEvent(DialogueGame dialogueGame, DialogueEvent event) {
        // Connect the event to the dialogue game
        event.setDialogueGame(dialogueGame);

        // Set what happens when dialogue completes
        dialogueGame.setOnDialogueComplete(() -> {
            System.out.println("Dialogue completed!");
            // Here you can put code to return to the map view, update game state, etc.
        });
        
        // Trigger the dialogue
        event.trigger();
    }
}
