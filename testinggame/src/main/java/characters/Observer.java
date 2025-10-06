package characters;

import abilities.Ability;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Observer {

    public class characterSlot {
        int id;
        Characters.character character;        // base character
        Characters.character baseCharacter;   // transformed/altered state (optional)
        ArrayList<Ability.skill> skills;       // skills assigned to this slot
        ArrayList<BuffDebuff> activeEffects;   // active buffs/debuffs
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

        public Line getLine() {return line;}
        public void setLine(Line line) {this.line = line;}

        public ArrayList<Ability.skill> getSkills() { return skills; }
        public void setSkills(ArrayList<Ability.skill> skills) { this.skills = skills; }
        
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
            currentHp += amount;
            if(currentHp>character.getHp()){
                currentHp = character.getHp();
            }
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





        }
    }
}
