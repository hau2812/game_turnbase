package demo;

import items.*;
import characters.Observer;
import characters.Characters;
import battle.BattleSystem;
import battle.BattleUI;

/**
 * Demo class to show the new enemy targeting and debuff system for consumable items
 */
public class EnemyTargetingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Enemy Targeting & Debuff Demo ===");
        System.out.println("This demo shows the new consumable item system with:");
        System.out.println("- Enemy targeting capability");
        System.out.println("- Debuff effects (Burn, Poison, Freeze, Weakness)");
        System.out.println("- Proper target selection based on item type\n");
        
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
        
        // Get character slots
        Observer.characterSlot hero = Observer.CharacterSlotRegistry.getByName("Hero");
        Observer.characterSlot enemy = Observer.CharacterSlotRegistry.getByName("Enemy");
        
        if (hero == null || enemy == null) {
            System.out.println("Error: Could not find test characters");
            return;
        }
        
        // Create enemy-targeting items
        createEnemyTargetingItems(inventory);
        
        System.out.println("=== Initial Character Stats ===");
        displayCharacterStats(hero, "Hero");
        displayCharacterStats(enemy, "Enemy");
        
        System.out.println("\n=== Enemy Targeting Items Demo ===");
        demonstrateEnemyTargetingItems(inventory, hero, enemy);
        
        System.out.println("\n=== Final Character Stats ===");
        displayCharacterStats(hero, "Hero");
        displayCharacterStats(enemy, "Enemy");
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void createEnemyTargetingItems(Inventory inventory) {
        // Create enemy-targeting consumable items
        ConsumableItem fireBomb = new ConsumableItem(
            "fire_bomb", "Fire Bomb", "Burns enemy for 3 turns",
            80, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.BURN, 
            ConsumableItem.TargetType.ENEMY, 50, 3, "Burn for 50 damage/turn"
        );
        
        ConsumableItem poisonDart = new ConsumableItem(
            "poison_dart", "Poison Dart", "Poisons enemy for 5 turns",
            100, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.POISON,
            ConsumableItem.TargetType.ENEMY, 30, 5, "Poison for 30 damage/turn"
        );
        
        ConsumableItem iceShard = new ConsumableItem(
            "ice_shard", "Ice Shard", "Freezes enemy for 2 turns",
            120, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.FREEZE,
            ConsumableItem.TargetType.ENEMY, 0, 2, "Freeze for 2 turns"
        );
        
        ConsumableItem weaknessPotion = new ConsumableItem(
            "weakness_potion", "Weakness Potion", "Weakens enemy attack for 4 turns",
            90, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.DEBUFF_ENEMY,
            ConsumableItem.TargetType.ENEMY, 25, 4, "Reduce enemy ATK by 25%"
        );
        
        // Add to inventory
        inventory.addItem(fireBomb, 1);
        inventory.addItem(poisonDart, 1);
        inventory.addItem(iceShard, 1);
        inventory.addItem(weaknessPotion, 1);
        
        System.out.println("Created enemy-targeting consumable items");
    }
    
    private static void displayCharacterStats(Observer.characterSlot character, String name) {
        Characters.character charData = character.getCharacter();
        System.out.println(name + " (" + charData.getName() + "):");
        System.out.println("  HP: " + character.getCurrentHp() + "/" + charData.getHp());
        System.out.println("  MP: " + character.getCurrentMp() + "/" + charData.getMp());
        System.out.println("  ATK: " + charData.getAtk());
        System.out.println("  DEF: " + charData.getDef());
        System.out.println("  SPD: " + charData.getSpd());
        
        // Show active effects
        if (character.getActiveEffects() != null && !character.getActiveEffects().isEmpty()) {
            System.out.println("  Active Effects:");
            for (characters.BuffDebuff effect : character.getActiveEffects()) {
                System.out.println("    - " + effect.toString());
            }
        } else {
            System.out.println("  Active Effects: None");
        }
    }
    
    private static void demonstrateEnemyTargetingItems(Inventory inventory, Observer.characterSlot hero, Observer.characterSlot enemy) {
        System.out.println("Adding enemy-targeting items to battle selection...");
        
        // Add items to battle selection
        boolean success1 = inventory.addBattleConsumable("fire_bomb");
        System.out.println("Added Fire Bomb: " + success1);
        
        boolean success2 = inventory.addBattleConsumable("poison_dart");
        System.out.println("Added Poison Dart: " + success2);
        
        boolean success3 = inventory.addBattleConsumable("ice_shard");
        System.out.println("Added Ice Shard: " + success3);
        
        System.out.println("\nUsing enemy-targeting items...");
        
        // Use Fire Bomb on enemy
        if (inventory.useBattleConsumable(0, enemy)) {
            System.out.println("Used Fire Bomb on enemy - should apply Burn debuff");
        }
        
        // Use Poison Dart on enemy
        if (inventory.useBattleConsumable(0, enemy)) {
            System.out.println("Used Poison Dart on enemy - should apply Poison debuff");
        }
        
        // Use Ice Shard on enemy
        if (inventory.useBattleConsumable(0, enemy)) {
            System.out.println("Used Ice Shard on enemy - should apply Freeze debuff");
        }
        
        System.out.println("\nRemaining battle consumables:");
        for (int i = 0; i < 3; i++) {
            if (i < inventory.getBattleConsumables().size()) {
                ConsumableItem item = inventory.getBattleConsumables().get(i);
                System.out.println("  Slot " + (i + 1) + ": " + item.getName() + " (Target: " + item.getTargetType().getDisplayName() + ")");
            } else {
                System.out.println("  Slot " + (i + 1) + ": Empty");
            }
        }
    }
}
