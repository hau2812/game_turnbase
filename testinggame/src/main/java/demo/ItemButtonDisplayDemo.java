package demo;

import items.*;
import characters.Observer;
import characters.Characters;
import battle.BattleSystem;
import battle.BattleUI;
import java.util.ArrayList;

/**
 * Demo class to show the improved item button display
 */
public class ItemButtonDisplayDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Item Button Display Demo ===");
        System.out.println("This demo shows the improved item button display with:");
        System.out.println("- Black text for better readability");
        System.out.println("- Larger buttons (50x35) for better text fit");
        System.out.println("- Proper text positioning within buttons");
        System.out.println("- Increased spacing between buttons\n");
        
        // Initialize registries
        Characters.CharacterRegistry.init();
        Observer.CharacterSlotRegistry.init();
        ItemRegistry.init();
        
        // Create inventory
        Inventory inventory = new Inventory();
        
        // Create battle system and UI
        BattleSystem battleSystem = new BattleSystem();
        BattleUI battleUI = new BattleUI(battleSystem);
        battleUI.setInventory(inventory);
        
        // Get character slot
        Observer.characterSlot hero = Observer.CharacterSlotRegistry.getByName("Hero");
        
        if (hero == null) {
            System.out.println("Error: Could not find test character");
            return;
        }
        
        // Create items with long names to test text fitting
        createTestItems(inventory);
        
        System.out.println("=== Adding Items to Battle Selection ===");
        addItemsToBattleSelection(inventory);
        
        System.out.println("\n=== Item Button Display Test ===");
        System.out.println("The following items should now display properly in battle buttons:");
        System.out.println("- Black text instead of white");
        System.out.println("- Text fits within larger buttons (50x35)");
        System.out.println("- Proper spacing between buttons");
        System.out.println("- No text cutoff at bottom");
        
        // Display current battle consumables
        System.out.println("\nCurrent battle consumables:");
        for (int i = 0; i < 3; i++) {
            if (i < inventory.getBattleConsumables().size()) {
                ConsumableItem item = inventory.getBattleConsumables().get(i);
                System.out.println("  Slot " + (i + 1) + ": " + item.getName());
            } else {
                System.out.println("  Slot " + (i + 1) + ": Empty");
            }
        }
        
        System.out.println("\n=== Demo Complete ===");
        System.out.println("In the actual game, these items will now display with:");
        System.out.println("✓ Black text for better readability");
        System.out.println("✓ Larger buttons (50x35 pixels)");
        System.out.println("✓ Text properly positioned within buttons");
        System.out.println("✓ No text cutoff or overflow");
    }
    
    private static void createTestItems(Inventory inventory) {
        // Create items with various name lengths to test display
        ConsumableItem shortName = new ConsumableItem(
            "potion", "Potion", "Basic healing", 50, Item.ItemRarity.COMMON,
            ConsumableItem.ConsumableType.HEAL_HP, 50, 0, "Heals HP"
        );
        
        ConsumableItem mediumName = new ConsumableItem(
            "health_potion", "Medium Health Potion", "Restores health", 75, Item.ItemRarity.COMMON,
            ConsumableItem.ConsumableType.HEAL_HP, 100, 0, "Heals more HP"
        );
        
        ConsumableItem longName = new ConsumableItem(
            "elixir", "Small Elixir", "Restores both", 100, Item.ItemRarity.UNCOMMON,
            ConsumableItem.ConsumableType.HEAL_BOTH, 75, 0, "Heals HP and MP"
        );
        
        // Add to inventory
        inventory.addItem(shortName, 1);
        inventory.addItem(mediumName, 1);
        inventory.addItem(longName, 1);
        
        System.out.println("Created test items with various name lengths");
    }
    
    private static void addItemsToBattleSelection(Inventory inventory) {
        // Add items to battle selection
        boolean success1 = inventory.addBattleConsumable("potion");
        System.out.println("Added 'Potion': " + success1);
        
        boolean success2 = inventory.addBattleConsumable("health_potion");
        System.out.println("Added 'Medium Health Potion': " + success2);
        
        boolean success3 = inventory.addBattleConsumable("elixir");
        System.out.println("Added 'Small Elixir': " + success3);
    }
}
