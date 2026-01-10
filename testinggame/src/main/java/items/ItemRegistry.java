package items;

import java.util.*;

/**
 * Registry for all available items in the game
 */
public class ItemRegistry {
    private static final Map<String, Item> registry = new HashMap<>();
    
    public static void init() {
        // Clear existing registry
        registry.clear();
        // ===================== STORY ITEMS =====================
        registerItem(new StoryItem("necro_sword","Necro Sword",
                        "A sword that consume their user strength to make a lethal cut at their enemy",
                        0,
                        Item.ItemRarity.LEGENDARY));
        registerItem(new StoryItem("radiant_core","Radiant Core",
                "A high power mana crystal from the heaven",
                0,
                Item.ItemRarity.LEGENDARY));
        registerItem(new StoryItem("Phoenix_feather","Phoenix Feather",
                "Feather of a Phoenix, it has many use, invulnerable for a while, generate high mana or even bring back from the death",
                0,
                Item.ItemRarity.LEGENDARY));
        registerItem(new StoryItem("litaru_sword","Litaru Sword",
                "In one human life, one bright movement is enough",
                0,
                Item.ItemRarity.LEGENDARY));


        // ===================== CONSUMABLE ITEMS =====================
        
        // Healing Potions
        registerItem(new ConsumableItem(
            "health_potion_small", "Small Health Potion", "Restores 100 HP",
            200, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.HEAL_HP, 100, 0, "Heal 100 HP"
        ));
        
        registerItem(new ConsumableItem(
            "health_potion_medium", "Medium Health Potion", "Restores 250 HP",
            300, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.HEAL_HP, 250, 0, "Heal 250 HP"
        ));
        
        registerItem(new ConsumableItem(
            "health_potion_large", "Large Health Potion", "Restores 500 HP",
            500, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.HEAL_HP, 500, 0, "Heal 500 HP"
        ));

        registerItem(new ConsumableItem(
                "potion_of_revival", "Potion of Revival", "Bring one hero back to the fight",
                1000, Item.ItemRarity.RARE, ConsumableItem.ConsumableType.HEAL_HP, 50, 0, "revive"
        ));
        
        // Mana Potions
        registerItem(new ConsumableItem(
            "mana_potion_small", "Small Mana Potion", "Restores 50 MP",
            200, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.HEAL_MP, 50, 0, "Restore 50 MP"
        ));
        
        registerItem(new ConsumableItem(
            "mana_potion_medium", "Medium Mana Potion", "Restores 150 MP",
            300, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.HEAL_MP, 150, 0, "Restore 150 MP"
        ));
        
        registerItem(new ConsumableItem(
            "mana_potion_large", "Large Mana Potion", "Restores 300 MP",
            500, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.HEAL_MP, 300, 0, "Restore 300 MP"
        ));
        
        // Combined Potions
        registerItem(new ConsumableItem(
            "elixir_small", "Small Elixir", "Restores 150 HP and 75 MP",
            300, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.HEAL_BOTH, 150, 0, "Heal 150 HP & 75 MP"
        ));
        
        registerItem(new ConsumableItem(
            "elixir_large", "Large Elixir", "Restores 400 HP and 200 MP",
            500, Item.ItemRarity.RARE, ConsumableItem.ConsumableType.HEAL_BOTH, 400, 0, "Heal 400 HP & 200 MP"
        ));
        
        // Status Effect Items (Enemy targeting)
        registerItem(new ConsumableItem(
            "fire_bomb", "Fire Bomb", "Burns enemy for 3 turns",
            200, Item.ItemRarity.COMMON, ConsumableItem.ConsumableType.BURN, ConsumableItem.TargetType.ENEMY, 50, 3, "Burn for 50 damage/turn"
        ));
        
        registerItem(new ConsumableItem(
            "poison_dart", "Poison Dart", "Poisons enemy for 5 turns",
            200, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.POISON, ConsumableItem.TargetType.ENEMY, 30, 5, "Poison for 30 damage/turn"
        ));
        
        registerItem(new ConsumableItem(
            "ice_shard", "Ice Shard", "Freezes enemy for 1 turns",
            750, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.FREEZE, ConsumableItem.TargetType.ENEMY, 0, 1, "Freeze for 1 turns"
        ));
        
        // Debuff Items
        registerItem(new ConsumableItem(
            "weakness_potion", "Weakness Potion", "Weakens enemy attack for 4 turns",
            350, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.DEBUFF_ENEMY, ConsumableItem.TargetType.ENEMY, 25, 4, "Reduce enemy ATK by 25%"
        ));
        
        // Buff Items
        registerItem(new ConsumableItem(
            "strength_potion", "Strength Potion", "Increases attack by 20% for 3 turns",
            250, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.BUFF_ATTACK, 20, 3, "+20 ATK for 5 turns"
        ));
        
        registerItem(new ConsumableItem(
            "defense_potion", "Defense Potion", "Increases defense by 30% for 5 turns",
            250, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.BUFF_DEFENSE, 30, 3, "+15 DEF for 5 turns"
        ));
        
        registerItem(new ConsumableItem(
            "speed_potion", "Speed Potion", "Increases speed by 25% for 3 turns",
            250, Item.ItemRarity.UNCOMMON, ConsumableItem.ConsumableType.BUFF_SPEED, 25, 3, "+10 SPD for 5 turns"
        ));
        
        registerItem(new ConsumableItem(
            "shield_potion", "Shield Potion", "Creates a protective barrier",
            400, Item.ItemRarity.RARE, ConsumableItem.ConsumableType.SHIELD, 100, 3, "Shield for 100 damage"
        ));
        
        // ===================== EQUIPMENT ITEMS =====================
        
        // Weapons
        registerItem(new EquipmentItem(
            "iron_sword", "Iron Sword", "A sturdy iron sword",
            200, Item.ItemRarity.COMMON, EquipmentItem.EquipmentType.WEAPON, 
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 0, 15, 0, 0, 0, 0)
        ));

        registerItem(new EquipmentItem(
                "light_shield", "Light Shield", "A sturdy shield",
                300, Item.ItemRarity.COMMON, EquipmentItem.EquipmentType.WEAPON,
                EquipmentItem.EquipmentSlot.WEAPON,
                new EquipmentItem.StatBonus(100, 0, 0, 0, 20, 20, 0)
        ));

        registerItem(new EquipmentItem(
                "heavy_shield", "Heavy Shield", "A sturdier shield",
                900, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.WEAPON,
                EquipmentItem.EquipmentSlot.WEAPON,
                new EquipmentItem.StatBonus(200, 0, 0, 0, 50, 50, -1)
        ));
        
        registerItem(new EquipmentItem(
            "steel_sword", "Steel Sword", "A sharp steel blade",
            500, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.WEAPON,
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 0, 25, 0, 0, 0, 0)
        ));
        
        registerItem(new EquipmentItem(
            "magic_staff", "Magic Staff", "A staff imbued with magical power",
            400, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.WEAPON,
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 50, 20, 0, 0, 0, 0)
        ));
        
        registerItem(new EquipmentItem(
            "flame_blade", "Flame Blade", "Apply 1 stack of burn debuff to enemy each attack",
            800, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.WEAPON,
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(0, 0, 35, 10, 0, 0, 0)
        ));
        
        registerItem(new EquipmentItem(
            "legendary_sword", "Excalibur", "The legendary sword of kings",
            1500, Item.ItemRarity.LEGENDARY, EquipmentItem.EquipmentType.WEAPON,
            EquipmentItem.EquipmentSlot.WEAPON,
            new EquipmentItem.StatBonus(100, 50, 50, 0, 0, 0, 2)
        ));

        registerItem(new EquipmentItem(
                "ashbringer", "Ashbringer", "Gain 1.25x time amount of burning rage",
                1500, Item.ItemRarity.LEGENDARY, EquipmentItem.EquipmentType.WEAPON,
                EquipmentItem.EquipmentSlot.WEAPON,
                new EquipmentItem.StatBonus(200, 0, 25, 0, 0, 0, 0)
        ));

        registerItem(new EquipmentItem(
                "blue_flower_staff", "Blue Flower Staff", "Regen 10% of hp and 10% of mp each time the user act",
                1500, Item.ItemRarity.LEGENDARY, EquipmentItem.EquipmentType.WEAPON,
                EquipmentItem.EquipmentSlot.WEAPON,
                new EquipmentItem.StatBonus(0, 100, 25, 0, 10,10 , 0)
        ));
        
        // Armor
        registerItem(new EquipmentItem(
            "leather_armor", "Leather Armor", "Basic leather protection",
            150, Item.ItemRarity.COMMON, EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(50, 0, 0, 0, 10, 5, 0)
        ));
        
        registerItem(new EquipmentItem(
            "chain_mail", "Chain Mail", "Interlocked metal rings",
            750, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(150, 0, 0, 0, 25, 25, -1)
        ));
        
        registerItem(new EquipmentItem(
            "plate_armor", "Plate Armor", "Heavy metal plates",
            1500, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(250, 0, 0, 0, 40, 40, -2)
        ));
        
        registerItem(new EquipmentItem(
            "mage_robes", "Mage Robes", "Enchanted robes for spellcasters",
            500, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(50, 100, 15, 0, 5, 20, 0)
        ));

        registerItem(new EquipmentItem(
                "ice_witch_scarf", "Ice Witch Scarf", "Gain immunity to frozen",
                600, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.ARMOR,
                EquipmentItem.EquipmentSlot.ARMOR,
                new EquipmentItem.StatBonus(50, 50, 10, 0, 5, 5, 0)
        ));
        
        registerItem(new EquipmentItem(
            "dragon_scale_armor", "Dragon Scale Armor", "Armor forged from dragon scales",
            3000, Item.ItemRarity.LEGENDARY, EquipmentItem.EquipmentType.ARMOR,
            EquipmentItem.EquipmentSlot.ARMOR,
            new EquipmentItem.StatBonus(300, 100, 0, 0, 50, 30, 0)
        ));
        
        // Accessories
        registerItem(new EquipmentItem(
            "power_ring", "Ring of Power", "Increases all combat abilities",
            332, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(0, 0, 10, 10, 5, 5, 0)
        ));
        
        registerItem(new EquipmentItem(
            "speed_boots", "Boots of Speed", "Lightweight boots that enhance movement",
            333, Item.ItemRarity.UNCOMMON, EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(0, 0, 0, 0, 0, 0, 3)
        ));
        
        registerItem(new EquipmentItem(
            "health_amulet", "Amulet of Vitality", "Increases maximum health",
            600, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(200, 0, 0, 0, 0, 0, 0)
        ));
        
        registerItem(new EquipmentItem(
            "mana_crystal", "Crystal of Mana", "Increases magical power",
            800, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(0, 150, 10, 0, 0, 10, 0)
        ));

        registerItem(new EquipmentItem(
                "heart_of_fury", "Heart of Fury", "Gain 1.5x damage when current hp below 36%",
                800, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.ACCESSORY,
                EquipmentItem.EquipmentSlot.ACCESSORY,
                new EquipmentItem.StatBonus(0, 0, 10, 0, 0, 0, 0)
        ));

        registerItem(new EquipmentItem(
                "fire_ring", "Fire Ring", "Attack apply 'Ignite' debuff to the enemy (make Burn debuff stack up to 20)",
                800, Item.ItemRarity.RARE, EquipmentItem.EquipmentType.ACCESSORY,
                EquipmentItem.EquipmentSlot.ACCESSORY,
                new EquipmentItem.StatBonus(50, 50, 10, 0, 0, 0, 0)
        ));
        
        registerItem(new EquipmentItem(
            "crown_of_kings", "Crown of Kings", "The ultimate accessory for rulers",
            3000, Item.ItemRarity.LEGENDARY, EquipmentItem.EquipmentType.ACCESSORY,
            EquipmentItem.EquipmentSlot.ACCESSORY,
            new EquipmentItem.StatBonus(300, 200, 30, 30, 20, 20, 3)
        ));
    }
    
    private static void registerItem(Item item) {
        registry.put(item.getId(), item);
    }
    
    public static Item getItem(String itemId) {
        return registry.get(itemId);
    }
    
    public static Collection<Item> getAllItems() {
        return registry.values();
    }
    public static Collection<Item> getAllItemsButNoStoryItem() {
        return registry.values()
                .stream()
                .filter(item -> item.getItemType() != Item.ItemType.STORY_ITEM)
                .toList(); // Java 16+
    }

    public static List<Item> getItemsByType(Item.ItemType type) {
        List<Item> result = new ArrayList<>();
        for (Item item : registry.values()) {
            if (item.getItemType() == type) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static List<Item> getItemsByRarity(Item.ItemRarity rarity) {
        List<Item> result = new ArrayList<>();
        for (Item item : registry.values()) {
            if (item.getRarity() == rarity) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static List<ConsumableItem> getConsumableItems() {
        List<ConsumableItem> result = new ArrayList<>();
        for (Item item : registry.values()) {
            if (item instanceof ConsumableItem) {
                result.add((ConsumableItem) item);
            }
        }
        return result;
    }
    
    public static List<EquipmentItem> getEquipmentItems() {
        List<EquipmentItem> result = new ArrayList<>();
        for (Item item : registry.values()) {
            if (item instanceof EquipmentItem) {
                result.add((EquipmentItem) item);
            }
        }
        return result;
    }
}
