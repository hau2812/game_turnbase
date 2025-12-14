package map;

import characters.Characters;
import characters.Observer;
import abilities.Ability;
import event.MapEvent;
import items.ItemRegistry;
import items.Item;
import items.ConsumableItem;
import items.EquipmentItem;
import java.util.*;

/**
 * He thong tao map random voi events va battles
 * Chi giu co dinh su kien cuoi va boss
 */
public class    RandomMapGenerator {
    
    private static final Random random = new Random();
    
    // Cac loai node co the random
    public enum RandomNodeType {
        BATTLE,
        EVENT,
        SHOP,
        REST
    }
    
    // Pool cac loai enemy khac nhau
    private static class EnemyTemplate {
        String name;
        int baseHp;
        int baseAtk;
        int baseDef;
        int baseSpd;
        int baseMp;
        String[] skillIds;
        String uniqueValue; // Co the null
        
        EnemyTemplate(String name, int baseHp, int baseAtk, int baseDef, int baseSpd, int baseMp, String[] skillIds, String uniqueValue) {
            this.name = name;
            this.baseHp = baseHp;
            this.baseAtk = baseAtk;
            this.baseDef = baseDef;
            this.baseSpd = baseSpd;
            this.baseMp = baseMp;
            this.skillIds = skillIds;
            this.uniqueValue = uniqueValue;
        }
    }
    
    // Pool enemy templates
    private static final EnemyTemplate[] FOREST_ENEMIES = {
        new EnemyTemplate("Forest Wolf", 300, 25, 15, 10, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Wild Bear", 500, 35, 20, 8, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Giant Spider", 400, 30, 18, 10, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Forest Troll", 600, 40, 25, 6, 0, new String[]{"1", "4", "0", "0"}, null),
        new EnemyTemplate("Shadow Beast", 250, 45, 15, 20, 0, new String[]{"1", "0", "0", "0"}, null)
    };
    
    private static final EnemyTemplate[] MOUNTAIN_ENEMIES = {
        new EnemyTemplate("Mountain Troll", 600, 50, 30, 12, 0, new String[]{"1", "4", "0", "0"}, null),
        new EnemyTemplate("Ice Elemental", 400, 35, 20, 15, 50, new String[]{"2", "0", "0", "0"}, null),
        new EnemyTemplate("Rock Golem", 800, 40, 35, 8, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Wind Spirit", 350, 30, 10, 25, 30, new String[]{"2", "0", "0", "0"}, null),
        new EnemyTemplate("Cave Bat", 250, 20, 5, 30, 0, new String[]{"1", "0", "0", "0"}, null)
    };
    
    private static final EnemyTemplate[] VILLAGE_ENEMIES = {
        new EnemyTemplate("Bandit", 350, 30, 15, 18, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Thief", 280, 25, 10, 25, 0, new String[]{"1", "0", "0", "0"}, null),
        new EnemyTemplate("Mercenary", 450, 35, 20, 15, 0, new String[]{"1", "4", "0", "0"}, null),
        new EnemyTemplate("Cultist", 380, 40, 12, 16, 40, new String[]{"2", "0", "0", "0"}, null),
        new EnemyTemplate("Outlaw", 320, 28, 18, 20, 0, new String[]{"1", "0", "0", "0"}, null)
    };
    
    /**
     * Tao random enemy dua tren path type
     */
    public static Observer.characterSlot createRandomEnemy(MapPath.PathType pathType, int difficultyLevel) {
        EnemyTemplate[] enemyPool = getEnemyPool(pathType);
        EnemyTemplate template = enemyPool[random.nextInt(enemyPool.length)];
        
        // Tang stats theo difficulty level
        float difficultyMultiplier = 1.0f + (difficultyLevel * 0.1f);
        
        int hp = (int)(template.baseHp * difficultyMultiplier);
        int atk = (int)(template.baseAtk * difficultyMultiplier);
        int def = (int)(template.baseDef * difficultyMultiplier);
        int spd = (int)(template.baseSpd * difficultyMultiplier);
        int mp = (int)(template.baseMp * difficultyMultiplier);
        
        // Tao character
        Characters.character enemy = new Characters.character(
            (int)(Math.random() * 1000),
            template.name + " (Lv." + difficultyLevel + ")",
            atk, def, 20, 10, spd, hp, mp, new ArrayList<>()
        );
        
        // Them unique value neu co
        if (template.uniqueValue != null) {
            String[] parts = template.uniqueValue.split(":");
            if (parts.length == 2) {
                enemy.setUniqueValue(parts[0], parts[1]);
            }
        }
        
        // Tao skills tu template
        ArrayList<Ability.skill> skills = new ArrayList<>();
        for (String skillId : template.skillIds) {
            int id = Integer.parseInt(skillId);
            if (id != 0) {
                Ability.skill skill = Ability.SkillRegistry.getById(id);
                if (skill != null) {
                    skills.add(skill);
                }
            }
        }
        
        // Ensure enemy has at least one skill (fallback to basic attack)
        if (skills.isEmpty()) {
            Ability.skill basicAttack = Ability.SkillRegistry.getById(1); // Slash
            if (basicAttack != null) {
                skills.add(basicAttack);
            }
        }
        
        // Tao character slot
        Observer.characterSlot enemySlot = new Observer.characterSlot(
            enemy.getId(),
            enemy,
            new Characters.character(enemy),
            skills,
            hp,
            mp
        );
        
        return enemySlot;
    }
    
    /**
     * Tao random battle node
     */
    public static MapNode createRandomBattleNode(String pathPrefix, int nodeNumber, MapPath.PathType pathType, int difficultyLevel) {
        MapNode battleNode = new MapNode(
            pathPrefix + "_battle" + nodeNumber,
            "Random Battle",
            "Mot cuoc chien bat ngo!",
            MapNode.NodeType.BATTLE,
            100 + (nodeNumber * 50),
            200 + (random.nextInt(200))
        );
        
        // Tao 1-3 enemies random
        int enemyCount = 1 + random.nextInt(3); // 1-3 enemies
        if(nodeNumber < 3) {
            enemyCount = 1;
        }
        for (int i = 0; i < enemyCount; i++) {
            Observer.characterSlot enemy = createRandomEnemy(pathType, difficultyLevel);
            battleNode.addEnemy(enemy);
        }
        
        return battleNode;
    }

    public static MapNode createRecruitNode(String pathPrefix, int nodeNumber, MapPath.PathType pathType) {
        MapNode recruitNode = new MapNode(
                pathPrefix + "_recruit" + nodeNumber,
                "Random Recruit",
                "You found an ally",
                MapNode.NodeType.RECRUIT,
                100 + (nodeNumber * 50),
                200 + (random.nextInt(200))
        );
        return recruitNode;
    }
    
    /**
     * Tao random event node
     */
    public static MapNode createRandomEventNode(String pathPrefix, int nodeNumber, MapPath.PathType pathType) {
        MapNode eventNode = new MapNode(
            pathPrefix + "_event" + nodeNumber,
            "Random Event",
            "Mot su kien bat ngo!",
            MapNode.NodeType.EVENT,
            100 + (nodeNumber * 50),
            200 + (random.nextInt(200))
        );
        
        // Random event type dua tren path
        MapEvent randomEvent = createRandomEvent(pathType);
        eventNode.setEvent(randomEvent);
        
        return eventNode;
    }
    
    /**
     * Tao random event dua tren path type
     */
    private static MapEvent createRandomEvent(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST:
                return createForestRandomEvent();
            case MOUNTAIN:
                return createMountainRandomEvent();
            case VILLAGE:
                return createVillageRandomEvent();
            default:
                return createGenericRandomEvent();
        }
    }
    
    private static MapEvent createForestRandomEvent() {
        // 30% chance for treasure event, 70% chance for regular event
        if (random.nextInt(10) < 3) {
            return createTreasureEvent();
        }

        String[] events = {
            "Ban tim thay mot suoi nuoc thieng trong rung.",
            "Mot con thu hoang da xuat hien va tang ban mot vat pham quy.",
            "Ban phat hien mot cay co thu voi trai cay ky la.",
            "Mot tinh linh rung ban phuoc lanh cho ban.",
            "Ban tim thay mot hang dong an voi kho bau."
        };
        
        String eventText = events[random.nextInt(events.length)];
        
        return new MapEvent(eventText) {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                int outcome = random.nextInt(3);
                switch (outcome) {
                    case 0: // Heal
                        float healAmount = 200 + random.nextInt(300);
                        healCharacter(hero1, healAmount);
                        healCharacter(hero2, healAmount);
                        System.out.println("Ca hai hero duoc hoi " + (int)healAmount + " HP!");
                        break;
                    case 1: // MP restore
                        float mpAmount = 100 + random.nextInt(200);
                        restoreMp(hero1, mpAmount);
                        restoreMp(hero2, mpAmount);
                        System.out.println("Ca hai hero duoc hoi " + (int)mpAmount + " MP!");
                        break;
                    case 2: // Both
                        float heal = 150 + random.nextInt(200);
                        float mp = 80 + random.nextInt(120);
                        healCharacter(hero1, heal);
                        healCharacter(hero2, heal);
                        restoreMp(hero1, mp);
                        restoreMp(hero2, mp);
                        System.out.println("Ca hai hero duoc hoi " + (int)heal + " HP va " + (int)mp + " MP!");
                        break;
                }
            }
        };
    }
    
    private static MapEvent createMountainRandomEvent() {
        // 25% chance for treasure event, 75% chance for regular event
        if (random.nextInt(10) < 2) {
            return createTreasureEvent();
        }

        String[] events = {
            "Ban tim thay mot hang dong bi an voi tinh the ma thuat.",
            "Mot con gio lanh thoi qua, mang theo suc manh co xua.",
            "Ban phat hien mot dai te co tren dinh nui.",
            "Mot con rong nho xuat hien va ban phuoc lanh.",
            "Ban tim thay mot mo tinh the bi bo hoang."
        };
        
        String eventText = events[random.nextInt(events.length)];
        
        return new MapEvent(eventText) {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                int outcome = random.nextInt(4);
                switch (outcome) {
                    case 0: // Major heal
                        float healAmount = 300 + random.nextInt(400);
                        healCharacter(hero1, healAmount);
                        healCharacter(hero2, healAmount);
                        System.out.println("Suc manh co xua hoi phuc " + (int)healAmount + " HP!");
                        break;
                    case 1: // Major MP
                        float mpAmount = 200 + random.nextInt(300);
                        restoreMp(hero1, mpAmount);
                        restoreMp(hero2, mpAmount);
                        System.out.println("Tinh the ma thuat hoi phuc " + (int)mpAmount + " MP!");
                        break;
                    case 2: // Both major
                        float heal = 250 + random.nextInt(300);
                        float mp = 150 + random.nextInt(250);
                        healCharacter(hero1, heal);
                        healCharacter(hero2, heal);
                        restoreMp(hero1, mp);
                        restoreMp(hero2, mp);
                        System.out.println("Phuoc lanh nui thieng! +" + (int)heal + " HP va +" + (int)mp + " MP!");
                        break;
                    case 3: // Danger but reward
                        float damage = 100 + random.nextInt(150);
                        float reward = 200 + random.nextInt(200);
                        damageCharacter(hero1, damage);
                        damageCharacter(hero2, damage);
                        healCharacter(hero1, reward);
                        healCharacter(hero2, reward);
                        System.out.println("Nguy hiem nhung co phan thuong! -" + (int)damage + " HP nhung +" + (int)reward + " HP!");
                        break;
                }
            }
        };
    }
    
    private static MapEvent createVillageRandomEvent() {
        // 40% chance for shop event, 60% chance for regular event
        if (random.nextInt(10) < 4) {
            return createShopEvent();
        }

        String[] events = {
            "Ban gap mot nguoi dan lang tot bung.",
            "Mot thuong gia giau co moi ban vao nha.",
            "Ban phat hien mot quan tro am cung.",
            "Mot nguoi gia khon ngoan day ban bi quyet.",
            "Ban tim thay mot cua hang ban do quy."
        };
        
        String eventText = events[random.nextInt(events.length)];
        
        return new MapEvent(eventText) {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                int outcome = random.nextInt(3);
                switch (outcome) {
                    case 0: // Shop benefits
                        float healAmount = 250 + random.nextInt(200);
                        float mpAmount = 150 + random.nextInt(150);
                        healCharacter(hero1, healAmount);
                        healCharacter(hero2, healAmount);
                        restoreMp(hero1, mpAmount);
                        restoreMp(hero2, mpAmount);
                        System.out.println("Mua sam tai lang! +" + (int)healAmount + " HP va +" + (int)mpAmount + " MP!");
                        break;
                    case 1: // Rest benefits
                        float heal = 300 + random.nextInt(250);
                        healCharacter(hero1, heal);
                        healCharacter(hero2, heal);
                        System.out.println("Nghi ngoi tai quan tro! +" + (int)heal + " HP!");
                        break;
                    case 2: // Wisdom benefits
                        float mp = 200 + random.nextInt(200);
                        restoreMp(hero1, mp);
                        restoreMp(hero2, mp);
                        System.out.println("Tri thuc cua nguoi gia! +" + (int)mp + " MP!");
                        break;
                }
            }
        };
    }
    
    private static MapEvent createGenericRandomEvent() {
        return new MapEvent("Mot su kien bat ngo xay ra!") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                float healAmount = 150 + random.nextInt(200);
                healCharacter(hero1, healAmount);
                healCharacter(hero2, healAmount);
                System.out.println("Su kien bat ngo! +" + (int)healAmount + " HP!");
            }
        };
    }
    
    /**
     * Tao random shop node
     */
    public static MapNode createRandomShopNode(String pathPrefix, int nodeNumber) {
        MapNode shopNode = new MapNode(
            pathPrefix + "_shop" + nodeNumber,
            "Random Shop",
            "Mot cua hang bat ngo!",
            MapNode.NodeType.SHOP,
            100 + (nodeNumber * 50),
            200 + (random.nextInt(200) - 100)
        );
        
        // Shop luon co loi ich
        shopNode.setEvent(new MapEvent("Ban tim thay mot cua hang voi nhieu vat pham huu ich.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                float healAmount = 200 + random.nextInt(150);
                float mpAmount = 100 + random.nextInt(100);
                healCharacter(hero1, healAmount);
                healCharacter(hero2, healAmount);
                restoreMp(hero1, mpAmount);
                restoreMp(hero2, mpAmount);
                System.out.println("Mua sam tai cua hang! +" + (int)healAmount + " HP va +" + (int)mpAmount + " MP!");
            }
        });
        
        return shopNode;
    }
    
    /**
     * Tao random rest node
     */
    public static MapNode createRandomRestNode(String pathPrefix, int nodeNumber) {
        MapNode restNode = new MapNode(
            pathPrefix + "_rest" + nodeNumber,
            "Safe Haven",
            "Mot noi an toan de nghi ngoi.",
            MapNode.NodeType.REST,
            100 + (nodeNumber * 50),
            200 + (random.nextInt(200) - 100)
        );
        
        // Rest luon hoi phuc tot
        restNode.setEvent(new MapEvent("Ban tim thay mot noi an toan de nghi ngoi va hoi phuc suc luc.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                // Rest hoi phuc mot phan lon HP va MP
                float healAmount = 400 + random.nextInt(300);
                float mpAmount = 200 + random.nextInt(200);
                healCharacter(hero1, healAmount);
                healCharacter(hero2, healAmount);
                restoreMp(hero1, mpAmount);
                restoreMp(hero2, mpAmount);
                System.out.println("Nghi ngoi hoan toan! +" + (int)healAmount + " HP va +" + (int)mpAmount + " MP!");
            }
        });
        
        return restNode;
    }
    
    /**
     * Random node type voi ty le khac nhau
     */
    public static RandomNodeType getRandomNodeType(MapPath.PathType pathType) {
        int roll = random.nextInt(100);
        
        switch (pathType) {
            case FOREST:
                // Forest: 60% battle, 30% event, 5% shop, 5% rest
                if (roll < 60) return RandomNodeType.BATTLE;
                if (roll < 90) return RandomNodeType.EVENT;
                if (roll < 95) return RandomNodeType.SHOP;
                return RandomNodeType.REST;
                
            case MOUNTAIN:
                // Mountain: 30% battle, 50% event, 10% shop, 10% rest
                if (roll < 30) return RandomNodeType.BATTLE;
                if (roll < 80) return RandomNodeType.EVENT;
                if (roll < 90) return RandomNodeType.SHOP;
                return RandomNodeType.REST;
                
            case VILLAGE:
                // Village: 40% battle, 20% event, 25% shop, 15% rest
                if (roll < 40) return RandomNodeType.BATTLE;
                if (roll < 60) return RandomNodeType.EVENT;
                if (roll < 85) return RandomNodeType.SHOP;
                return RandomNodeType.REST;
                
            default:
                // Default: 50% battle, 40% event, 5% shop, 5% rest
                if (roll < 50) return RandomNodeType.BATTLE;
                if (roll < 90) return RandomNodeType.EVENT;
                if (roll < 95) return RandomNodeType.SHOP;
                return RandomNodeType.REST;
        }
    }
    
    /**
     * Lay enemy pool theo path type
     */
    private static EnemyTemplate[] getEnemyPool(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST:
                return FOREST_ENEMIES;
            case MOUNTAIN:
                return MOUNTAIN_ENEMIES;
            case VILLAGE:
                return VILLAGE_ENEMIES;
            default:
                return FOREST_ENEMIES;
        }
    }

    /**
     * Create a treasure event that gives items to the player
     */
    public static MapEvent createTreasureEvent() {
        String[] treasureEvents = {
            "Ban tim thay mot kho bau cu an!",
            "Mot kho bau bi bo hoang xuat hien truoc mat ban!",
            "Ban phat hien mot hop kho bau ky la!",
            "Mot kho bau ma thuat duoc an giu trong hang dong!",
            "Ban tim thay kho bau cua mot nha tham hiem truoc day!"
        };

        String eventText = treasureEvents[random.nextInt(treasureEvents.length)];

        return new MapEvent(eventText) {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }

            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                // Give random items to player
                List<Item> allItems = new ArrayList<>(ItemRegistry.getAllItems());
                Collections.shuffle(allItems);

                // Give 1-3 random items
                int itemCount = 1 + random.nextInt(3);
                System.out.println("Ban tim thay " + itemCount + " vat pham trong kho bau:");

                for (int i = 0; i < Math.min(itemCount, allItems.size()); i++) {
                    Item item = allItems.get(i);
                    int quantity = getRandomItemQuantity(item);

                    // Add to inventory (this would need to be connected to the actual inventory system)
                    System.out.println("- " + item.getName() + " x" + quantity + " (" + item.getRarity().getDisplayName() + ")");
                }

                // Also give some healing
                float healAmount = 100 + random.nextInt(200);
                healCharacter(hero1, healAmount);
                healCharacter(hero2, healAmount);
                System.out.println("Ca hai hero duoc hoi " + (int)healAmount + " HP!");
            }
        };
    }

    /**
     * Get random quantity for an item based on its rarity
     */
    private static int getRandomItemQuantity(Item item) {
        switch (item.getRarity()) {
            case COMMON:
                return 1 + random.nextInt(3); // 1-3
            case UNCOMMON:
                return 1 + random.nextInt(2); // 1-2
            case RARE:
                return 1; // Always 1
            case EPIC:
                return 1; // Always 1
            case LEGENDARY:
                return 1; // Always 1
            default:
                return 1;
        }
    }

    /**
     * Create a shop event that gives gold and items
     */
    public static MapEvent createShopEvent() {
        String[] shopEvents = {
            "Ban gap mot thuong gia lang thang va mua mot so vat pham!",
            "Mot cua hang nho xuat hien va ban mua mot so do dung!",
            "Ban tim thay mot thuong gia va trao doi vat pham!",
            "Mot nguoi ban hang di qua va ban mua mot so thuoc!"
        };

        String eventText = shopEvents[random.nextInt(shopEvents.length)];

        return new MapEvent(eventText) {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }

            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                // Give gold
                int goldAmount = 100 + random.nextInt(300);
                System.out.println("Ban nhan duoc " + goldAmount + " gold!");

                // Give some consumable items
                List<ConsumableItem> consumables = ItemRegistry.getConsumableItems();
                Collections.shuffle(consumables);

                int itemCount = 1 + random.nextInt(2); // 1-2 items
                System.out.println("Ban mua duoc " + itemCount + " vat pham:");

                for (int i = 0; i < Math.min(itemCount, consumables.size()); i++) {
                    ConsumableItem item = consumables.get(i);
                    int quantity = 1 + random.nextInt(2); // 1-2 quantity
                    System.out.println("- " + item.getName() + " x" + quantity);
                }

                // Also give some healing
                float healAmount = 150 + random.nextInt(200);
                healCharacter(hero1, healAmount);
                healCharacter(hero2, healAmount);
                System.out.println("Ca hai hero duoc hoi " + (int)healAmount + " HP!");
            }
        };
    }


}