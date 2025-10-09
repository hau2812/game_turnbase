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
    int currentNodeNumber = 1;
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
        paths.add(forestPath);
        paths.add(mountainPath);
        paths.add(villagePath);

        //Custom boss
        //addFlamitaBossFight("forest");
        addMabelBossFight("forest");
        // Initialize paths với random system
        initializeRandomPath(forestPath);
        initializeRandomPath(mountainPath);
        initializeRandomPath(villagePath);

        // Create Boss Node (chung cho tat ca paths) - CO DINH
        bossNode = new MapNode("boss", "Dragon's Lair", "Hang cua rong boss cuoi cung", 
                              MapNode.NodeType.BOSS, 400, 100);
        // Add boss enemy - CO DINH
        Characters.character boss = createBossCharacter();
        // Boss co skills manh me
        bossNode.addEnemy(createEnemySlotWithSkills(boss, 2, 12, 13, 0));



    }

    /**
     * Khởi tạo path với hệ thống random
     */
    private void initializeRandomPath(MapPath path) {
        String pathName = path.getId().toLowerCase();
        
        // Start node - CO DINH
        MapNode start = new MapNode(pathName + "_start", 
                                   getPathStartName(path.getPathType()), 
                                   getPathStartDescription(path.getPathType()), 
                                   MapNode.NodeType.START, 100, getPathStartY(path.getPathType()));
        path.addNode(start);



        // Random nodes (4-6 nodes) - RANDOM
        int nodeCount = 1 + (int)(Math.random() * 0); // 4-6 random nodes
        for (int i = 1; i <= nodeCount; i++) {
            MapNode randomNode = createRandomNode(pathName, i, path.getPathType());
            path.addNode(randomNode);
            if(path.getId().equals("forest")) {
                currentNodeNumber++;
            }
        }

        // Final event/battle - CO DINH (nhung khac nhau cho moi path)
        MapNode finalNode = createFixedFinalNode(pathName, path.getPathType());
        path.addNode(finalNode);

    }
    
    /**
     * Tạo random node dựa trên path type
     */
    private MapNode createRandomNode(String pathName, int nodeNumber, MapPath.PathType pathType) {
        RandomMapGenerator.RandomNodeType nodeType = RandomMapGenerator.getRandomNodeType(pathType);
        
        switch (nodeType) {
            case BATTLE:
                return RandomMapGenerator.createRandomBattleNode(pathName, nodeNumber, pathType, nodeNumber);
            case EVENT:
                return RandomMapGenerator.createRandomEventNode(pathName, nodeNumber, pathType);
            case SHOP:
                return RandomMapGenerator.createRandomShopNode(pathName, nodeNumber);
            case REST:
                return RandomMapGenerator.createRandomRestNode(pathName, nodeNumber);
            default:
                return RandomMapGenerator.createRandomBattleNode(pathName, nodeNumber, pathType, nodeNumber);
        }
    }
    
    /**
     * Tạo final node cố định cho mỗi path
     */
    private MapNode createFixedFinalNode(String pathName, MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST:
                // Forest Guardian - CO DINH
                MapNode forestFinal = new MapNode(pathName + "_final", "Forest Guardian", "Thu ho rung", 
                                                 MapNode.NodeType.BATTLE, 100 + (currentNodeNumber * 50), 220);
                currentNodeNumber++;
                Characters.character forestGuardian = new Characters.character(
                    (int)(Math.random() * 1000), "Forest Guardian", 75, 30, 20, 10, 5, 800, 0, new ArrayList<>()
                );
                forestGuardian.setUniqueValue("Regeneration","200");
                forestFinal.addEnemy(createEnemySlotWithSkills(forestGuardian,1,0,0,0));
                return forestFinal;
                // Forest Guardian - CO DINH


            case MOUNTAIN:
                // Mountain Summit Event - CO DINH
                MapNode mountainFinal = new MapNode(pathName + "_final", "Summit Trial", "Thu thach dinh nui", 
                                                   MapNode.NodeType.EVENT, 300, 120);
                mountainFinal.setEvent(new event.MapEvent("Ban da den dinh nui thieng. Cac vi than nui ban phuoc lanh cho hanh trinh cua ban.") {
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
                        
                        System.out.println("Phuoc lanh cua nui thieng! +500 HP va +300 MP cho ca doi!");
                    }
                });
                return mountainFinal;
                
            case VILLAGE:
                // Bandit Leader - CO DINH
                MapNode villageFinal = new MapNode(pathName + "_final", "Bandit Leader", "Thu linh cuop", 
                                                  MapNode.NodeType.BATTLE, 300, 320);
                Characters.character banditLeader = new Characters.character(
                    (int)(Math.random() * 1000), "Bandit Leader", 45, 25, 15, 8, 20, 700, 0, new ArrayList<>()
                );
                villageFinal.addEnemy(createEnemySlotWithSkills(banditLeader,1,4,0,0));
                return villageFinal;
                
            default:
                // Default final battle
                return RandomMapGenerator.createRandomBattleNode(pathName, 999, pathType, 5);
        }
    }
    
    // Helper methods cho path initialization
    private String getPathStartName(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST: return "Forest Entrance";
            case MOUNTAIN: return "Mountain Base";
            case VILLAGE: return "Village Outskirts";
            default: return "Path Entrance";
        }
    }
    
    private String getPathStartDescription(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST: return "Loi vao rung sau";
            case MOUNTAIN: return "Chan nui";
            case VILLAGE: return "Ngoai o lang";
            default: return "Diem khoi dau";
        }
    }
    
    private int getPathStartY(MapPath.PathType pathType) {
        switch (pathType) {
            case FOREST: return 300;
            case MOUNTAIN: return 200;
            case VILLAGE: return 400;
            default: return 250;
        }
    }

    // Các method cũ đã được thay thế bởi initializeRandomPath()
    // Giữ lại các helper methods để tạo enemy

    // Helper methods to create enemies - giữ lại cho tương lai nếu cần

    private Characters.character createBossCharacter() {
        return new Characters.character(
            9999, "Ancient Dragon", 1000, 800, 100, 80, 10, 5000, 1000, new ArrayList<>()
        );
    }
    
    /**
     * Add a custom node to a specific path
     * @param pathId The ID of the path to add the node to ("forest", "mountain", "village")
     * @param nodeId Unique identifier for the node
     * @param nodeName Display name of the node
     * @param nodeDescription Description of the node
     * @param nodeType Type of the node (BATTLE, EVENT, SHOP, REST, START, BOSS)
     * @param x X coordinate for the node
     * @param y Y coordinate for the node
     * @param enemyCharacter Character to use as enemy (null if not a battle node)
     * @param skillIds Array of skill IDs for the enemy (can be null if not a battle node)
     * @return The created MapNode, or null if path not found
     */
    public MapNode addCustomNode(String pathId, String nodeId, String nodeName, String nodeDescription, 
                                MapNode.NodeType nodeType, int x, int y, 
                                Characters.character enemyCharacter, int[] skillIds) {
        // Find the path
        MapPath targetPath = null;
        for (MapPath path : paths) {
            if (path.getId().equalsIgnoreCase(pathId)) {
                targetPath = path;
                break;
            }
        }
        
        if (targetPath == null) {
            System.out.println("Path with ID '" + pathId + "' not found!");
            return null;
        }
        
        // Create the custom node
        MapNode customNode = new MapNode(nodeId, nodeName, nodeDescription, nodeType, x, y);
        
        // Add enemy if this is a battle node and enemy character is provided
        if (nodeType == MapNode.NodeType.BATTLE && enemyCharacter != null && skillIds != null) {
            // Convert int array to individual parameters for createEnemySlotWithSkills
            int skill1 = skillIds.length > 0 ? skillIds[0] : 0;
            int skill2 = skillIds.length > 1 ? skillIds[1] : 0;
            int skill3 = skillIds.length > 2 ? skillIds[2] : 0;
            int skill4 = skillIds.length > 3 ? skillIds[3] : 0;
            
            customNode.addEnemy(createEnemySlotWithSkills(enemyCharacter, skill1, skill2, skill3, skill4));
        }
        
        // Add the node to the path
        targetPath.addNode(customNode);
        currentNodeNumber++;


        
        System.out.println("Added custom node '" + nodeName + "' to path '" + pathId + "'");
        return customNode;
    }
    
    /**
     * Convenience method to add the Flamita Boss Fight node
     * @param pathId The path to add the Flamita boss to
     * @return The created MapNode
     */
    public MapNode addFlamitaBossFight(String pathId) {
        // Create corrupted Flamita character
        Characters.character corruptedFlamita = new Characters.character(
                (int)(Math.random() * 1000), "Flamita ?", 100, 30, 20, 10, 5, 1000, 0, new ArrayList<>()
        );
        corruptedFlamita.setUniqueValue("Burning rage", "0");
        corruptedFlamita.setUniqueValue("Guts", "1");
        
        // Skill IDs: 6, 7, 8, 9
        int[] skillIds = {6, 7, 8, 9};
        System.out.println(currentNodeNumber);
        // Add the custom node
        return addCustomNode(pathId, "flamita_boss", "?", "Internal Burning", 
                           MapNode.NodeType.BATTLE, 100+currentNodeNumber*50, 400, corruptedFlamita, skillIds);
    }
    
    /**
     * Convenience method to add the Mabel Boss Fight node
     * @param pathId The path to add the Mabel boss to
     * @return The created MapNode
     */
    public MapNode addMabelBossFight(String pathId) {
        // Create Mabel character
        Characters.character mabel = new Characters.character(
                (int)(Math.random() * 1000), "Mabel", 100, 30, 20, 10, 15, 5000, 40, new ArrayList<>()
        );
        
        // Skill IDs: 15, 16, 17, 0
        int[] skillIds = {15, 16, 17, 0};
        
        // Add the custom node
        MapNode mabelNode = addCustomNode(pathId, "mabel_boss", "?", "?",
                                        MapNode.NodeType.BATTLE, 100+currentNodeNumber*50, 370, mabel, skillIds);
        
        // Set custom MP for Mabel (as specified in your code)
        if (mabelNode != null && mabelNode.getEnemies() != null && !mabelNode.getEnemies().isEmpty()) {
            Observer.characterSlot mabelSlot = mabelNode.getEnemies().get(0);
            mabelSlot.setCurrentMp(20);
        }
        
        return mabelNode;
    }
    
    /**
     * Add a custom event node
     * @param pathId The path to add the event to
     * @param nodeId Unique identifier for the node
     * @param nodeName Display name of the node
     * @param nodeDescription Description of the node
     * @param x X coordinate for the node
     * @param y Y coordinate for the node
     * @param event The MapEvent to associate with this node
     * @return The created MapNode
     */
    public MapNode addCustomEventNode(String pathId, String nodeId, String nodeName, String nodeDescription,
                                     int x, int y, event.MapEvent event) {
        MapNode eventNode = addCustomNode(pathId, nodeId, nodeName, nodeDescription, 
                                        MapNode.NodeType.EVENT, x, y, null, null);
        if (eventNode != null && event != null) {
            eventNode.setEvent(event);
        }
        return eventNode;
    }
    
    // Helper method to create character slots with skills for map enemies
    private Observer.characterSlot createEnemySlotWithSkills(Characters.character character,int skill1, int skill2, int skill3, int skill4) {
        // Initialize skills registry if not already done
        Ability.SkillRegistry.init();
        Characters.character baseCharacter = new Characters.character(character);
        // Create skills list with skills 1, 2, 3, 4
        ArrayList<Ability.skill> enemySkills = new ArrayList<>();
        if(skill1 != 0){
            Ability.skill skill = Ability.SkillRegistry.getById(skill1);
            if(skill != null) enemySkills.add(skill);
        }
        if(skill2 != 0){
            Ability.skill skill = Ability.SkillRegistry.getById(skill2);
            if(skill != null) enemySkills.add(skill);
        }
        if(skill3 != 0){
            Ability.skill skill = Ability.SkillRegistry.getById(skill3);
            if(skill != null) enemySkills.add(skill);
        }
        if(skill4 != 0){
            Ability.skill skill = Ability.SkillRegistry.getById(skill4);
            if(skill != null) enemySkills.add(skill);
        }

        // Create character slot
        return new Observer.characterSlot(
                character.getId(),
                character,
                baseCharacter,
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
