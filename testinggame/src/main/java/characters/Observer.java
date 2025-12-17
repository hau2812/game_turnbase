package characters;

import abilities.Ability;
import battle.BattleSystem;
import items.EquipmentItem;
import javafx.scene.shape.Line;
import ui.SimpleLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Observer {
    
    // Static reference to BattleSystem for party MP management
    public static final BattleSystem[] battleSystemInstance = new BattleSystem[1];
    
    /**
     * Set the BattleSystem instance for party MP management
     * @param battleSystem The BattleSystem instance
     */
    public static void setBattleSystem(BattleSystem battleSystem) {
        battleSystemInstance[0] = battleSystem;
    }
    
    /**
     * Get the current BattleSystem instance
     * @return The BattleSystem instance, or null if not set
     */
    public static BattleSystem getBattleSystem() {
        return battleSystemInstance[0];
    }

    public class characterSlot {
        int id;
        Characters.character character;        // base character
        Characters.character baseCharacter;   // transformed/altered state (optional)
        ArrayList<Ability.skill> skills;       // skills assigned to this slot
        ArrayList<BuffDebuff> activeEffects;   // active buffs/debuffs
        ArrayList<EquipmentItem> equipments;
        float currentHp;
        float currentMp;
        Line line;


        public characterSlot(int id, Characters.character character, Characters.character baseCharacter,
                             ArrayList<Ability.skill> skills, float currentHp, float currentMp) {
            this.id = id;
            this.character = character;
            this.baseCharacter = baseCharacter;
            this.skills = skills;
            this.activeEffects = new ArrayList<>();
            this.currentHp = currentHp;
            this.currentMp = currentMp;
        }

        public ArrayList<EquipmentItem> getEquipment() {
            return equipments;
        }

        public void setEquipment(ArrayList<EquipmentItem> equipment) {
            this.equipments = equipment;
        }
        public void addEquipment(EquipmentItem equipment) {
            this.equipments.add(equipment);
        }
        public void removeEquipment(EquipmentItem equipment) {
            this.equipments.remove(equipment);
        }

        public Line getLine() {return line;}
        public void setLine(Line line) {this.line = line;}

        public ArrayList<Ability.skill> getSkills() { return skills; }
        public void setSkills(ArrayList<Ability.skill> skills) { this.skills = skills; }
        public void addSkills(ArrayList<Ability.skill> skills) {
            this.skills.addAll(skills);
        }
        
        public ArrayList<BuffDebuff> getActiveEffects() { return activeEffects; }
        public void setActiveEffects(ArrayList<BuffDebuff> activeEffects) { this.activeEffects = activeEffects; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public Characters.character getCharacter() { return character; }
        public void setCharacter(Characters.character character) { this.character = character; }

        public Characters.character getBaseCharacter() { return baseCharacter; }
        public void setBaseCharacter(Characters.character baseCharacter) { this.baseCharacter = baseCharacter; }

        public float getCurrentHp() { return currentHp; }
        public void setCurrentHp(float currentHp) { this.currentHp = currentHp; }

        public float getCurrentMp() { return currentMp; }
        public void setCurrentMp(float currentMp) { this.currentMp = currentMp; }

        public void heal(float amount){
            currentHp = Math.max(0,Math.min(currentHp+amount,character.getHp()));
        }
        public void regenerateMp(float amount){
            currentMp = Math.max(0,Math.min(currentMp+amount,character.getMp()));
            if(amount<0&&battleSystemInstance[0].getSlotByName("Leuna")!=null){
                // When mana is consumed (amount < 0), increase party MP by the amount consumed
                if(battleSystemInstance[0] != null) {
                    float currentPartyMp = battleSystemInstance[0].getPartyMp();
                    battleSystemInstance[0].setPartyMp(currentPartyMp - amount/10); // -amount because amount is negative
                }
            }
        }

        public BuffDebuff getBuffDebuffByName(String name){
            for (BuffDebuff effect : activeEffects) {
                if (effect.getName().equals(name)) {
                    return effect;
                }
            }
            return null; // Return null if not found
        }
        public void removeBuffDebuffByName(String name){
            for (BuffDebuff effect : activeEffects) {
                if (effect.getName().equals(name)) {
                    activeEffects.remove(effect);
                    return;
                }
            }
        }

        public float getFloatBuffDebuffByName(String name){
            BuffDebuff buff = getBuffDebuffByName(name);
            if(buff!=null){
                return buff.getStack();
            }
            return 0;
        }

        /**
         * Adds a BuffDebuff to this character slot's active effects
         * @param buffDebuff The BuffDebuff to add
         */
        public void addBuffDebuff(BuffDebuff buffDebuff) {
            if (buffDebuff != null) {
                activeEffects.add(buffDebuff);
            }
        }

        /**
         * Returns the count of buffs (not debuffs) in this character slot's active effects
         * @return The number of buffs
         */
        public int getBuffCount() {
            int buffCount = 0;
            for (BuffDebuff effect : activeEffects) {
                if (effect != null && "Buff".equals(effect.getType())) {
                    buffCount++;
                }
            }
            return buffCount;
        }

        /**
         * Checks if this character slot contains a BuffDebuff with the specified name
         * @param name The name of the BuffDebuff to check for
         * @return true if the BuffDebuff exists, false otherwise
         */
        public boolean containsBuffDebuff(String name) {
            for (BuffDebuff effect : activeEffects) {
                if (effect != null && effect.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ===================== REGISTRY =====================
    public class CharacterSlotRegistry {
        private static final Map<String, characterSlot> registry = new HashMap<>();

        // Characters are created by default - comment out lines to disable them

        public static characterSlot getByName(String name) {
            return registry.get(name);
        }

        public static Collection<characterSlot> getAll() {
            return registry.values();
        }
        
        /**
         * Helper function to add a skill to the skills list, using N/A if skill name is null
         * @param skills The skills list to add to
         * @param skillName The skill name (null will use N/A)
         * @param characterName Character name for error reporting
         */
        private static void addSkillToList(ArrayList<Ability.skill> skills, String skillName, String characterName) {
            String actualSkillName = (skillName != null) ? skillName : "N/A";
            Ability.skill skill = Ability.SkillRegistry.getByName(actualSkillName);
            if (skill != null) {
                skills.add(skill);
            } else {
                System.err.println("Skill '" + actualSkillName + "' not found for character '" + characterName + "'");
            }
        }

        /**
         * Helper function to create and register a character slot with skills
         * @param characterName The name of the character (must exist in CharacterRegistry)
         * @param skill1 First skill name (null will use N/A)
         * @param skill2 Second skill name (null will use N/A)
         * @param skill3 Third skill name (null will use N/A)
         * @param skill4 Fourth skill name (null will use N/A)
         * @return The created character slot
         */
        public static characterSlot createCharacterSlot(String characterName, String skill1, String skill2, String skill3, String skill4) {
            // Get the character from registry

            Characters.character character = Characters.CharacterRegistry.getByName(characterName);
            Characters.character baseCharacter = new Characters.character(character); // Create copy
            
            // Create skills list using helper function
            ArrayList<Ability.skill> skills = new ArrayList<>();
            addSkillToList(skills, skill1, characterName);
            addSkillToList(skills, skill2, characterName);
            addSkillToList(skills, skill3, characterName);
            addSkillToList(skills, skill4, characterName);
            
            // Create character slot
            characterSlot slot = new characterSlot(
                character.getId(),
                character,
                baseCharacter,
                skills,
                character.getHp(),
                character.getMp()
            );
            
            // Register the slot
            registry.put(character.getName(), slot);
            
            return slot;
        }

        public static void init() {

            // Ensure skills and characters are initialized
            Ability.SkillRegistry.init();
            Characters.CharacterRegistry.init();

            //Hero 1
            characterSlot heroSlot = createCharacterSlot("Hero", "Slash", "Fireball", "heavy attack", null);
            // Hero 2
            characterSlot hero2Slot = createCharacterSlot("Hero2", "Charge attack", "Fireball", "Heal", "Barrier");
            // Hero 3
            characterSlot hero3Slot = createCharacterSlot("Flamita", "Rage Strike", "Burning slash", "Rage Heal", "Rage Burst");
            // Hero 4
            characterSlot hero4Slot = createCharacterSlot("Pieberry", "Charge attack", "5-Orb Flame", "7-Fork Lightning", "Ecarr Vertel");

            createCharacterSlot("Ina", "Light attack", "Energy charge", "Let me absorb you", "Absolute teleportation");
            createCharacterSlot("Leuna", "Charge attack", "Moon light", "Moon wave", "Absolute barrier");
            createCharacterSlot("Flatina", "Rage Strike", "Amber sacrifice", "Rage empowerment", "Burning guts");

            createCharacterSlot("Chigon", "Tiger claw", "Dragon tail", "Eagle fang", "Dragon breath");
            addSkillToList(registry.get("Chigon").getSkills(),"Weakening","Chigon");
            addSkillToList(registry.get("Chigon").getSkills(),"Push","Chigon");
            addSkillToList(registry.get("Chigon").getSkills(),"Taunt","Chigon");
            addSkillToList(registry.get("Chigon").getSkills(),"Backstep","Chigon");

            createCharacterSlot("Lucia", "Arua's Arrow", "Aimhard's Absolute Defense", "Durok's Gravity Field", "Aramute's obliterated");
            addSkillToList(registry.get("Lucia").getSkills(),"Arua's Lighting Bolt","Lucia");





        }
    }
}
