package test;

import items.*;
import characters.Observer;
import characters.Characters;
import java.util.ArrayList;

/**
 * Test class to demonstrate the new per-character equipment system
 */
public class EquipmentTest {
    
    public static void main(String[] args) {
        // Create test inventory
        Inventory inventory = new Inventory();
        
        // Create test characters
        Characters.character hero1 = Characters.CharacterRegistry.getByName("Hero");
        Characters.character hero2 = Characters.CharacterRegistry.getByName("Hero2");
        
        if (hero1 == null || hero2 == null) {
            System.out.println("Error: Could not find test characters");
            return;
        }
        
        // Create character slots
        Observer.characterSlot hero1Slot = new Observer.characterSlot(
            hero1.getId(), 
            hero1, 
            new Characters.character(hero1), 
            new ArrayList<>(), 
            hero1.getHp(), 
            hero1.getMp()
        );
        Observer.characterSlot hero2Slot = new Observer.characterSlot(
            hero2.getId(), 
            hero2, 
            new Characters.character(hero2), 
            new ArrayList<>(), 
            hero2.getHp(), 
            hero2.getMp()
        );
        
        // Create test equipment
        EquipmentItem sword = new EquipmentItem(
            "sword_001", 
            "Iron Sword", 
            "A sturdy iron sword", 
            100, 
            Item.ItemRarity.COMMON,
            EquipmentItem.EquipmentType.WEAPON,
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 0, 20, 0, 0, 0, 0)
        );
        
        EquipmentItem armor = new EquipmentItem(
            "armor_001", 
            "Leather Armor", 
            "Basic leather protection", 
            80, 
            Item.ItemRarity.COMMON,
            EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(50, 0, 0, 0, 10, 5, 0)
        );
        
        EquipmentItem ring = new EquipmentItem(
            "ring_001", 
            "Magic Ring", 
            "A ring that enhances magical abilities", 
            150, 
            Item.ItemRarity.RARE,
            EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(0, 30, 0, 15, 0, 0, 5)
        );
        
        // Add items to inventory
        inventory.addItem(sword, 1);
        inventory.addItem(armor, 1);
        inventory.addItem(ring, 1);
        
        System.out.println("=== Equipment Test ===");
        System.out.println("Initial stats:");
        System.out.println("Hero1: HP=" + hero1.getHp() + ", ATK=" + hero1.getAtk() + ", DEF=" + hero1.getDef());
        System.out.println("Hero2: HP=" + hero2.getHp() + ", ATK=" + hero2.getAtk() + ", DEF=" + hero2.getDef());
        
        // Equip sword to Hero1
        System.out.println("\nEquipping Iron Sword to Hero1...");
        if (inventory.equipItem("sword_001", hero1Slot)) {
            System.out.println("Successfully equipped Iron Sword to Hero1");
            System.out.println("Hero1 stats after sword: HP=" + hero1.getHp() + ", ATK=" + hero1.getAtk() + ", DEF=" + hero1.getDef());
        }
        
        // Equip armor to Hero2
        System.out.println("\nEquipping Leather Armor to Hero2...");
        if (inventory.equipItem("armor_001", hero2Slot)) {
            System.out.println("Successfully equipped Leather Armor to Hero2");
            System.out.println("Hero2 stats after armor: HP=" + hero2.getHp() + ", ATK=" + hero2.getAtk() + ", DEF=" + hero2.getDef());
        }
        
        // Equip ring to Hero1
        System.out.println("\nEquipping Magic Ring to Hero1...");
        if (inventory.equipItem("ring_001", hero1Slot)) {
            System.out.println("Successfully equipped Magic Ring to Hero1");
            System.out.println("Hero1 stats after ring: HP=" + hero1.getHp() + ", ATK=" + hero1.getAtk() + ", DEF=" + hero1.getDef() + ", MP=" + hero1.getMp() + ", MATK=" + hero1.getMatk() + ", SPD=" + hero1.getSpd());
        }
        
        // Check equipped items
        System.out.println("\n=== Equipped Items ===");
        EquipmentItem hero1Weapon = inventory.getEquippedItem(hero1Slot, EquipmentItem.EquipmentSlot.WEAPON);
        EquipmentItem hero1Accessory = inventory.getEquippedItem(hero1Slot, EquipmentItem.EquipmentSlot.ACCESSORY);
        EquipmentItem hero2Armor = inventory.getEquippedItem(hero2Slot, EquipmentItem.EquipmentSlot.ARMOR);
        
        System.out.println("Hero1 Weapon: " + (hero1Weapon != null ? hero1Weapon.getName() : "None"));
        System.out.println("Hero1 Accessory: " + (hero1Accessory != null ? hero1Accessory.getName() : "None"));
        System.out.println("Hero2 Armor: " + (hero2Armor != null ? hero2Armor.getName() : "None"));
        
        // Test unequipping
        System.out.println("\n=== Unequipping Test ===");
        if (inventory.unequipItem(EquipmentItem.EquipmentSlot.WEAPON, hero1Slot)) {
            System.out.println("Successfully unequipped weapon from Hero1");
            System.out.println("Hero1 stats after unequip: HP=" + hero1.getHp() + ", ATK=" + hero1.getAtk() + ", DEF=" + hero1.getDef());
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
