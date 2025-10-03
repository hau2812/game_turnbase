package map;

import characters.Characters;
import characters.Observer;
import abilities.Ability;
import java.util.List;
import java.util.ArrayList;

public class GameMap {
    private List<MapPath> paths;
    private MapPath selectedPath;
    private MapNode bossNode;
    private boolean mapCompleted;

    public GameMap() {
        this.paths = new ArrayList<>();
        this.mapCompleted = false;
        initializeMap();
    }

    private void initializeMap() {
        // Create the three paths
        MapPath forestPath = new MapPath("forest", MapPath.PathType.FOREST);
        MapPath mountainPath = new MapPath("mountain", MapPath.PathType.MOUNTAIN);
        MapPath villagePath = new MapPath("village", MapPath.PathType.VILLAGE);

        // Initialize Forest Path (nhiều battle, ít event)
        initializeForestPath(forestPath);
        
        // Initialize Mountain Path (ít battle, nhiều event nguy hiểm)
        initializeMountainPath(mountainPath);
        
        // Initialize Village Path (cân bằng, có shop)
        initializeVillagePath(villagePath);

        // Create Boss Node (chung cho tất cả paths)
        bossNode = new MapNode("boss", "Dragon's Lair", "Hang của rồng boss cuối cùng", 
                              MapNode.NodeType.BOSS, 400, 100);
        // Add boss enemy
        Characters.character boss = createBossCharacter();
        bossNode.addEnemy(createEnemySlotWithSkills(boss));

        paths.add(forestPath);
        paths.add(mountainPath);
        paths.add(villagePath);
    }

    private void initializeForestPath(MapPath path) {
        // Start node
        MapNode start = new MapNode("forest_start", "Forest Entrance", "Lối vào rừng sâu", 
                                   MapNode.NodeType.START, 100, 300);
        path.addNode(start);

        // Battle nodes
        MapNode battle1 = new MapNode("forest_battle1", "Wolf Pack", "Đàn sói hoang dã", 
                                     MapNode.NodeType.BATTLE, 150, 280);
        battle1.addEnemy(createEnemySlotWithSkills(createForestEnemy("Wolf", 300, 150)));
        battle1.addEnemy(createEnemySlotWithSkills(createForestEnemy("Wolf", 300, 150)));
        path.addNode(battle1);

        MapNode battle2 = new MapNode("forest_battle2", "Bear Cave", "Hang gấu", 
                                     MapNode.NodeType.BATTLE, 200, 260);
        battle2.addEnemy(createEnemySlotWithSkills(createForestEnemy("Bear", 500, 300)));
        path.addNode(battle2);

        // Event node
        MapNode event1 = new MapNode("forest_event1", "Ancient Tree", "Cây cổ thụ bí ẩn", 
                                    MapNode.NodeType.EVENT, 250, 240);
        event1.setEvent(new event.MapEvent("Bạn tìm thấy một cây cổ thụ với trái cây kỳ lạ.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                // Forest healing event
                float healAmount = 200 + (int)(Math.random() * 300);
                if (hero1 != null && hero1.getCurrentHp() > 0) {
                    hero1.setCurrentHp(Math.min(hero1.getCharacter().getHp(), hero1.getCurrentHp() + healAmount));
                }
                if (hero2 != null && hero2.getCurrentHp() > 0) {
                    hero2.setCurrentHp(Math.min(hero2.getCharacter().getHp(), hero2.getCurrentHp() + healAmount));
                }
                System.out.println("Cả hai hero được hồi " + (int)healAmount + " HP!");
            }
        });
        path.addNode(event1);

        // Final battle before boss
        MapNode finalBattle = new MapNode("forest_final", "Forest Guardian", "Thủ hộ rừng", 
                                         MapNode.NodeType.BATTLE, 300, 220);
        finalBattle.addEnemy(createEnemySlotWithSkills(createForestEnemy("Forest Guardian", 800, 400)));
        path.addNode(finalBattle);
    }

    private void initializeMountainPath(MapPath path) {
        // Start node
        MapNode start = new MapNode("mountain_start", "Mountain Base", "Chân núi", 
                                   MapNode.NodeType.START, 100, 200);
        path.addNode(start);

        // Event nodes (nhiều event nguy hiểm)
        MapNode event1 = new MapNode("mountain_event1", "Rockslide", "Lở đá", 
                                    MapNode.NodeType.EVENT, 150, 180);
        event1.setEvent(new event.MapEvent("Lở đá từ trên cao! Bạn phải nhanh chóng tìm nơi trú ẩn.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                int outcome = (int)(Math.random() * 10);
                if (outcome < 3) {
                    float damage = 150 + (int)(Math.random() * 200);
                    if (hero1 != null) hero1.setCurrentHp(Math.max(0, hero1.getCurrentHp() - damage));
                    if (hero2 != null) hero2.setCurrentHp(Math.max(0, hero2.getCurrentHp() - damage));
                    System.out.println("Bạn không kịp trốn! Mất " + (int)damage + " HP!");
                } else if (outcome < 7) {
                    float damage = 50 + (int)(Math.random() * 100);
                    if (hero1 != null) hero1.setCurrentHp(Math.max(0, hero1.getCurrentHp() - damage));
                    if (hero2 != null) hero2.setCurrentHp(Math.max(0, hero2.getCurrentHp() - damage));
                    System.out.println("Bạn trốn được phần lớn nhưng vẫn bị thương nhẹ. Mất " + (int)damage + " HP.");
                } else {
                    if (hero1 != null) {
                        hero1.setCurrentHp(Math.min(hero1.getCharacter().getHp(), hero1.getCurrentHp() + 100));
                        hero1.setCurrentMp(Math.min(hero1.getCharacter().getMp(), hero1.getCurrentMp() + 150));
                    }
                    if (hero2 != null) {
                        hero2.setCurrentHp(Math.min(hero2.getCharacter().getHp(), hero2.getCurrentHp() + 100));
                        hero2.setCurrentMp(Math.min(hero2.getCharacter().getMp(), hero2.getCurrentMp() + 150));
                    }
                    System.out.println("Bạn tìm thấy kho báu trong đống đá! +100 HP và +150 MP!");
                }
            }
        });
        path.addNode(event1);

        MapNode event2 = new MapNode("mountain_event2", "Mysterious Cave", "Hang động bí ẩn", 
                                    MapNode.NodeType.EVENT, 200, 160);
        event2.setEvent(new event.MapEvent("Một hang động bí ẩn xuất hiện trước mặt. Bên trong có ánh sáng kỳ lạ.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                int outcome = (int)(Math.random() * 4);
                switch (outcome) {
                    case 0:
                        if (hero1 != null) {
                            hero1.setCurrentHp(Math.min(hero1.getCharacter().getHp(), hero1.getCurrentHp() + 300));
                            hero1.setCurrentMp(Math.min(hero1.getCharacter().getMp(), hero1.getCurrentMp() + 200));
                        }
                        if (hero2 != null) {
                            hero2.setCurrentHp(Math.min(hero2.getCharacter().getHp(), hero2.getCurrentHp() + 300));
                            hero2.setCurrentMp(Math.min(hero2.getCharacter().getMp(), hero2.getCurrentMp() + 200));
                        }
                        System.out.println("Tinh thể ma thuật tăng cường sức mạnh! +300 HP và +200 MP!");
                        break;
                    case 1:
                        if (hero1 != null) hero1.setCurrentMp(Math.max(0, hero1.getCurrentMp() - 100));
                        if (hero2 != null) hero2.setCurrentMp(Math.max(0, hero2.getCurrentMp() - 100));
                        System.out.println("Hang động bị nguyền rủa! Mất 100 MP!");
                        break;
                    case 2:
                        if (hero1 != null) hero1.setCurrentMp(hero1.getCharacter().getMp());
                        if (hero2 != null) hero2.setCurrentMp(hero2.getCharacter().getMp());
                        System.out.println("Tri thức cổ xưa phục hồi toàn bộ MP!");
                        break;
                    case 3:
                        if (hero1 != null) hero1.setCurrentHp(Math.max(0, hero1.getCurrentHp() - 200));
                        if (hero2 != null) hero2.setCurrentHp(Math.max(0, hero2.getCurrentHp() - 200));
                        System.out.println("Quái vật hang động tấn công! Mất 200 HP!");
                        break;
                }
            }
        });
        path.addNode(event2);

        // One battle
        MapNode battle1 = new MapNode("mountain_battle1", "Mountain Troll", "Troll núi", 
                                     MapNode.NodeType.BATTLE, 250, 140);
        battle1.addEnemy(createEnemySlotWithSkills(createMountainEnemy("Mountain Troll", 600, 350)));
        path.addNode(battle1);

        // Final event
        MapNode finalEvent = new MapNode("mountain_final", "Summit Trial", "Thử thách đỉnh núi", 
                                        MapNode.NodeType.EVENT, 300, 120);
        finalEvent.setEvent(new event.MapEvent("Bạn đã đến đỉnh núi thiêng. Các vị thần núi ban phước lành cho hành trình của bạn.") {
            @Override
            public void trigger() {
                System.out.println(getDescription());
            }
            
            @Override
            public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
                float healAmount = 500;
                float mpAmount = 300;
                
                if (hero1 != null) {
                    hero1.setCurrentHp(Math.min(hero1.getCharacter().getHp(), hero1.getCurrentHp() + healAmount));
                    hero1.setCurrentMp(Math.min(hero1.getCharacter().getMp(), hero1.getCurrentMp() + mpAmount));
                }
                if (hero2 != null) {
                    hero2.setCurrentHp(Math.min(hero2.getCharacter().getHp(), hero2.getCurrentHp() + healAmount));
                    hero2.setCurrentMp(Math.min(hero2.getCharacter().getMp(), hero2.getCurrentMp() + mpAmount));
                }
                
                System.out.println("Phước lành của núi thiêng! +500 HP và +300 MP cho cả đội!");
            }
        });
        path.addNode(finalEvent);
    }

    private void initializeVillagePath(MapPath path) {
        // Start node
        MapNode start = new MapNode("village_start", "Village Outskirts", "Ngoại ô làng", 
                                   MapNode.NodeType.START, 100, 400);
        path.addNode(start);

        // Shop node
        MapNode shop = new MapNode("village_shop", "Village Shop", "Cửa hàng làng", 
                                  MapNode.NodeType.SHOP, 150, 380);
        path.addNode(shop);

        // Battle node
        MapNode battle1 = new MapNode("village_battle1", "Bandits", "Bọn cướp", 
                                     MapNode.NodeType.BATTLE, 200, 360);
        battle1.addEnemy(createEnemySlotWithSkills(createVillageEnemy("Bandit", 350, 200)));
        battle1.addEnemy(createEnemySlotWithSkills(createVillageEnemy("Bandit", 350, 200)));
        path.addNode(battle1);

        // Rest node
        MapNode rest = new MapNode("village_rest", "Village Inn", "Quán trọ", 
                                  MapNode.NodeType.REST, 250, 340);
        path.addNode(rest);

        // Final battle
        MapNode finalBattle = new MapNode("village_final", "Bandit Leader", "Thủ lĩnh cướp", 
                                         MapNode.NodeType.BATTLE, 300, 320);
        finalBattle.addEnemy(createEnemySlotWithSkills(createVillageEnemy("Bandit Leader", 700, 400)));
        path.addNode(finalBattle);
    }

    // Helper methods to create enemies
    private Characters.character createForestEnemy(String name, int hp, int atk) {
        return new Characters.character(
            (int)(Math.random() * 1000), name, atk, 30, 20, 10, 15, hp, 50, new ArrayList<>()
        );
    }

    private Characters.character createMountainEnemy(String name, int hp, int atk) {
        return new Characters.character(
            (int)(Math.random() * 1000), name, atk, 40, 30, 15, 12, hp, 80, new ArrayList<>()
        );
    }

    private Characters.character createVillageEnemy(String name, int hp, int atk) {
        return new Characters.character(
            (int)(Math.random() * 1000), name, atk, 25, 15, 8, 18, hp, 40, new ArrayList<>()
        );
    }

    private Characters.character createBossCharacter() {
        return new Characters.character(
            9999, "Ancient Dragon", 1000, 800, 100, 80, 10, 5000, 1000, new ArrayList<>()
        );
    }
    
    // Helper method to create character slots with skills for map enemies
    private Observer.characterSlot createEnemySlotWithSkills(Characters.character character) {
        // Initialize skills registry if not already done
        Ability.SkillRegistry.init();
        
        // Create skills list with skills 1, 2, 3
        ArrayList<Ability.skill> enemySkills = new ArrayList<>();
        enemySkills.add(Ability.SkillRegistry.getById(1)); // Slash
        enemySkills.add(Ability.SkillRegistry.getById(2)); // Fireball
        enemySkills.add(Ability.SkillRegistry.getById(3)); // Heal
        
        // Create character slot
        return new Observer.characterSlot(
            character.getId(),
            character,
            character,
            enemySkills,
            character.getHp(),
            character.getMp()
        );
    }

    // Getters and Setters
    public List<MapPath> getPaths() { return paths; }
    public MapPath getSelectedPath() { return selectedPath; }
    public void setSelectedPath(MapPath selectedPath) { this.selectedPath = selectedPath; }
    
    public MapNode getBossNode() { return bossNode; }
    public boolean isMapCompleted() { return mapCompleted; }
    public void setMapCompleted(boolean mapCompleted) { this.mapCompleted = mapCompleted; }

    public boolean isReadyForBoss() {
        return selectedPath != null && selectedPath.isCompleted();
    }
}
