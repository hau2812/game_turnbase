package demo;

import items.*;
import characters.Observer;
import characters.Characters;
import battle.BattleSystem;
import battle.BattleUI;
import java.util.ArrayList;

/**
 * Demo class to show the improved battle item system
 */
public class BattleItemDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Battle Item System Demo ===");
        System.out.println("This demo shows the new battle item system with:");
        System.out.println("- Real item names displayed in battle slots");
        System.out.println("- One item per slot limitation");
        System.out.println("- Proper item usage in combat\n");
        
        // Initialize registries
        Characters.CharacterRegistry.init();
        Observer.CharacterSlotRegistry.init();
        ItemRegistry.init();
        
        // Create inventory
        Inventory inventory = new Inventory();
        
        // Create battle system and UI
        BattleSystem battleSystem = new BattleSystem();
        BattleUI battleUI = new BattleUI(battleSystem);
        battleUI.setInventory(inventory); // Connect inventory to battle UI
        
        // Get character slots
        Observer.characterSlot hero1 = Observer.CharacterSlotRegistry.getByName("Hero");
        Observer.characterSlot hero2 = Observer.CharacterSlotRegistry.getByName("Hero2");
        
        if (hero1 == null || hero2 == null) {
            System.out.println("Error: Could not find test characters");
            return;
        }
        
        // Create and add consumable items
        createAndAddConsumables(inventory);
        
        System.out.println("=== Initial Character Stats ===");
        displayCharacterStats(hero1, "Hero1");
        displayCharacterStats(hero2, "Hero2");
        
        System.out.println("\n=== Battle Item Management Demo ===");
        demonstrateBattleItemManagement(inventory);
        
        System.out.println("\n=== Item Usage Demo ===");
        demonstrateItemUsage(inventory, hero1, hero2);
        
        System.out.println("\n=== Final Character Stats ===");
        displayCharacterStats(hero1, "Hero1");
        displayCharacterStats(hero2, "Hero2");
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void createAndAddConsumables(Inventory inventory) {
        // Create various consumable items
        ConsumableItem healthPotion = new ConsumableItem(
            "health_potion", "Health Potion", "Restores 100 HP", 50, Item.ItemRarity.COMMON,
            ConsumableItem.ConsumableType.HEAL_HP, 100, 0, "Instantly restores health"
        );
        
        ConsumableItem manaPotion = new ConsumableItem(
            "mana_potion", "Mana Potion", "Restores 50 MP", 40, Item.ItemRarity.COMMON,
            ConsumableItem.ConsumableType.HEAL_MP, 50, 0, "Instantly restores mana"
        );
        
        ConsumableItem elixir = new ConsumableItem(
            "elixir", "Elixir", "Restores both HP and MP", 100, Item.ItemRarity.UNCOMMON,
            ConsumableItem.ConsumableType.HEAL_BOTH, 75, 0, "Restores both health and mana"
        );
        
        ConsumableItem strengthPotion = new ConsumableItem(
            "strength_potion", "Strength Potion", "Increases attack power", 80, Item.ItemRarity.UNCOMMON,
            ConsumableItem.ConsumableType.BUFF_ATTACK, 20, 3, "Temporarily increases attack"
        );
        
        // Add to inventory
        inventory.addItem(healthPotion, 3);
        inventory.addItem(manaPotion, 2);
        inventory.addItem(elixir, 1);
        inventory.addItem(strengthPotion, 1);
        
        System.out.println("Created and added consumable items to inventory");
    }
    
    private static void displayCharacterStats(Observer.characterSlot character, String name) {
        Characters.character charData = character.getCharacter();
        System.out.println(name + " (" + charData.getName() + "):");
        System.out.println("  HP: " + character.getCurrentHp() + "/" + charData.getHp());
        System.out.println("  MP: " + character.getCurrentMp() + "/" + charData.getMp());
        System.out.println("  ATK: " + charData.getAtk());
    }
    
    private static void demonstrateBattleItemManagement(Inventory inventory) {
        System.out.println("Adding items to battle selection...");
        
        // Try to add items to battle consumables
        boolean success1 = inventory.addBattleConsumable("health_potion");
        System.out.println("Added Health Potion: " + success1);
        
        boolean success2 = inventory.addBattleConsumable("mana_potion");
        System.out.println("Added Mana Potion: " + success2);
        
        boolean success3 = inventory.addBattleConsumable("elixir");
        System.out.println("Added Elixir: " + success3);
        
        // Try to add duplicate item (should fail)
        boolean success4 = inventory.addBattleConsumable("health_potion");
        System.out.println("Tried to add duplicate Health Potion: " + success4 + " (should be false)");
        
        // Try to add 4th item (should fail - max 3)
        boolean success5 = inventory.addBattleConsumable("strength_potion");
        System.out.println("Tried to add 4th item: " + success5 + " (should be false)");
        
        // Display current battle consumables
        System.out.println("\nCurrent battle consumables:");
        for (int i = 0; i < 3; i++) {
            if (i < inventory.getBattleConsumables().size()) {
                ConsumableItem item = inventory.getBattleConsumables().get(i);
                System.out.println("  Slot " + (i + 1) + ": " + item.getName() + 
                                 " - " + item.getEffectDescription() + " (" + item.getEffectValue() + ")");
            } else {
                System.out.println("  Slot " + (i + 1) + ": Empty");
            }
        }
    }
    
    private static void demonstrateItemUsage(Inventory inventory, Observer.characterSlot hero1, Observer.characterSlot hero2) {
        System.out.println("Using battle consumables...");
        
        // Use Health Potion on Hero1
        if (inventory.useBattleConsumable(0, hero1)) {
            System.out.println("Used Health Potion on Hero1");
        } else {
            System.out.println("Failed to use Health Potion on Hero1");
        }
        
        // Use Mana Potion on Hero2
        if (inventory.useBattleConsumable(0, hero2)) { // Index 0 because Health Potion was removed
            System.out.println("Used Mana Potion on Hero2");
        } else {
            System.out.println("Failed to use Mana Potion on Hero2");
        }
        
        // Display remaining battle consumables
        System.out.println("\nRemaining battle consumables:");
        for (int i = 0; i < 3; i++) {
            if (i < inventory.getBattleConsumables().size()) {
                ConsumableItem item = inventory.getBattleConsumables().get(i);
                System.out.println("  Slot " + (i + 1) + ": " + item.getName());
            } else {
                System.out.println("  Slot " + (i + 1) + ": Empty");
            }
        }
    }
}
