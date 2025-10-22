package demo;

import items.*;
import characters.Observer;
import characters.Characters;

/**
 * Demo class to show the improved equipment system with per-character equipment
 * and enhanced consumable display
 */
public class EquipmentDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Equipment System Demo ===");
        System.out.println("This demo shows the new per-character equipment system");
        System.out.println("and enhanced consumable item display.\n");
        
        // Initialize registries
        Characters.CharacterRegistry.init();
        Observer.CharacterSlotRegistry.init();
        
        // Create inventory
        Inventory inventory = new Inventory();
        
        // Get character slots from registry
        Observer.characterSlot hero1 = Observer.CharacterSlotRegistry.getByName("Hero");
        Observer.characterSlot hero2 = Observer.CharacterSlotRegistry.getByName("Hero2");
        
        if (hero1 == null || hero2 == null) {
            System.out.println("Error: Could not find test characters");
            return;
        }
        
        // Create various equipment items
        createAndAddEquipment(inventory);
        
        // Create various consumable items
        createAndAddConsumables(inventory);
        
        System.out.println("=== Initial Character Stats ===");
        displayCharacterStats(hero1, "Hero1");
        displayCharacterStats(hero2, "Hero2");
        
        System.out.println("\n=== Equipment Demo ===");
        demonstrateEquipmentSystem(inventory, hero1, hero2);
        
        System.out.println("\n=== Consumable Demo ===");
        demonstrateConsumableSystem(inventory);
        
        System.out.println("\n=== Final Character Stats ===");
        displayCharacterStats(hero1, "Hero1");
        displayCharacterStats(hero2, "Hero2");
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static void createAndAddEquipment(Inventory inventory) {
        // Create weapons
        EquipmentItem ironSword = new EquipmentItem(
            "iron_sword", "Iron Sword", "A sturdy iron sword", 100, Item.ItemRarity.COMMON,
            EquipmentItem.EquipmentType.WEAPON, EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 0, 25, 0, 0, 0, 0)
        );
        
        EquipmentItem magicStaff = new EquipmentItem(
            "magic_staff", "Magic Staff", "A staff that enhances magical power", 200, Item.ItemRarity.RARE,
            EquipmentItem.EquipmentType.WEAPON, EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 50, 5, 30, 0, 0, 0)
        );
        
        // Create armor
        EquipmentItem leatherArmor = new EquipmentItem(
            "leather_armor", "Leather Armor", "Basic leather protection", 80, Item.ItemRarity.COMMON,
            EquipmentItem.EquipmentType.ARMOR, EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(100, 0, 0, 0, 15, 10, 0)
        );
        
        EquipmentItem plateArmor = new EquipmentItem(
            "plate_armor", "Plate Armor", "Heavy metal armor", 300, Item.ItemRarity.RARE,
            EquipmentItem.EquipmentType.ARMOR, EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(200, 0, 0, 0, 25, 20, -5)
        );
        
        // Create accessories
        EquipmentItem healthRing = new EquipmentItem(
            "health_ring", "Ring of Health", "Increases maximum health", 150, Item.ItemRarity.UNCOMMON,
            EquipmentItem.EquipmentType.ACCESSORY, EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(150, 0, 0, 0, 0, 0, 0)
        );
        
        EquipmentItem speedBoots = new EquipmentItem(
            "speed_boots", "Boots of Speed", "Increases movement speed", 120, Item.ItemRarity.UNCOMMON,
            EquipmentItem.EquipmentType.ACCESSORY, EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(0, 0, 0, 0, 0, 0, 10)
        );
        
        // Add to inventory
        inventory.addItem(ironSword, 1);
        inventory.addItem(magicStaff, 1);
        inventory.addItem(leatherArmor, 1);
        inventory.addItem(plateArmor, 1);
        inventory.addItem(healthRing, 1);
        inventory.addItem(speedBoots, 1);
        
        System.out.println("Created and added equipment items to inventory");
    }
    
    private static void createAndAddConsumables(Inventory inventory) {
        // Create healing items
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
        
        // Create buff items
        ConsumableItem strengthPotion = new ConsumableItem(
            "strength_potion", "Strength Potion", "Increases attack power", 80, Item.ItemRarity.UNCOMMON,
            ConsumableItem.ConsumableType.BUFF_ATTACK, 20, 3, "Temporarily increases attack"
        );
        
        ConsumableItem speedPotion = new ConsumableItem(
            "speed_potion", "Speed Potion", "Increases speed", 60, Item.ItemRarity.COMMON,
            ConsumableItem.ConsumableType.BUFF_SPEED, 5, 2, "Temporarily increases speed"
        );
        
        // Add to inventory
        inventory.addItem(healthPotion, 3);
        inventory.addItem(manaPotion, 2);
        inventory.addItem(elixir, 1);
        inventory.addItem(strengthPotion, 1);
        inventory.addItem(speedPotion, 2);
        
        System.out.println("Created and added consumable items to inventory");
    }
    
    private static void displayCharacterStats(Observer.characterSlot character, String name) {
        Characters.character charData = character.getCharacter();
        System.out.println(name + " (" + charData.getName() + "):");
        System.out.println("  HP: " + charData.getHp() + "/" + charData.getHp());
        System.out.println("  MP: " + charData.getMp() + "/" + charData.getMp());
        System.out.println("  ATK: " + charData.getAtk());
        System.out.println("  MATK: " + charData.getMatk());
        System.out.println("  DEF: " + charData.getDef());
        System.out.println("  RES: " + charData.getRes());
        System.out.println("  SPD: " + charData.getSpd());
    }
    
    private static void demonstrateEquipmentSystem(Inventory inventory, Observer.characterSlot hero1, Observer.characterSlot hero2) {
        System.out.println("Equipping Iron Sword to Hero1...");
        inventory.equipItem("iron_sword", hero1);
        
        System.out.println("Equipping Magic Staff to Hero2...");
        inventory.equipItem("magic_staff", hero2);
        
        System.out.println("Equipping Leather Armor to Hero1...");
        inventory.equipItem("leather_armor", hero1);
        
        System.out.println("Equipping Plate Armor to Hero2...");
        inventory.equipItem("plate_armor", hero2);
        
        System.out.println("Equipping Ring of Health to Hero1...");
        inventory.equipItem("health_ring", hero1);
        
        System.out.println("Equipping Boots of Speed to Hero2...");
        inventory.equipItem("speed_boots", hero2);
        
        // Show equipped items
        System.out.println("\nEquipped items:");
        showEquippedItems(inventory, hero1, "Hero1");
        showEquippedItems(inventory, hero2, "Hero2");
    }
    
    private static void showEquippedItems(Inventory inventory, Observer.characterSlot character, String name) {
        System.out.println(name + " equipped items:");
        for (EquipmentItem.EquipmentSlot slot : EquipmentItem.EquipmentSlot.values()) {
            EquipmentItem item = inventory.getEquippedItem(character, slot);
            if (item != null) {
                System.out.println("  " + slot.getDisplayName() + ": " + item.getName() + " (" + item.getStatBonus().toString() + ")");
            } else {
                System.out.println("  " + slot.getDisplayName() + ": Empty");
            }
        }
    }
    
    private static void demonstrateConsumableSystem(Inventory inventory) {
        System.out.println("Adding consumables to battle selection...");
        
        inventory.addBattleConsumable("health_potion");
        inventory.addBattleConsumable("mana_potion");
        inventory.addBattleConsumable("elixir");
        
        System.out.println("Battle consumables:");
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
}
