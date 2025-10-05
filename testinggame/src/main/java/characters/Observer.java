package characters;

import abilities.Ability;
import javafx.scene.shape.Line;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Observer {

    public class characterSlot {
        int id;
        Characters.character character;        // base character
        Characters.character characterAfter;   // transformed/altered state (optional)
        ArrayList<Ability.skill> skills;       // skills assigned to this slot
        float currentHp;
        float currentMp;
        Line line;

        public characterSlot(int id, Characters.character character, Characters.character characterAfter,
                             ArrayList<Ability.skill> skills, float currentHp, float currentMp) {
            this.id = id;
            this.character = character;
            this.characterAfter = characterAfter;
            this.skills = skills;
            this.currentHp = currentHp;
            this.currentMp = currentMp;
        }

        public Line getLine() {return line;}
        public void setLine(Line line) {this.line = line;}

        public ArrayList<Ability.skill> getSkills() { return skills; }
        public void setSkills(ArrayList<Ability.skill> skills) { this.skills = skills; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public Characters.character getCharacter() { return character; }
        public void setCharacter(Characters.character character) { this.character = character; }

        public Characters.character getCharacterAfter() { return characterAfter; }
        public void setCharacterAfter(Characters.character characterAfter) { this.characterAfter = characterAfter; }

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
         * Helper function to create and register a character slot with skills
         * @param characterName The name of the character (must exist in CharacterRegistry)
         * @param skill1 First skill name
         * @param skill2 Second skill name  
         * @param skill3 Third skill name
         * @return The created character slot
         */
        public static characterSlot createCharacterSlot(String characterName, String skill1, String skill2, String skill3) {
            // Get the character from registry
            Characters.character character = Characters.CharacterRegistry.getByName(characterName);
            if (character == null) {
                System.err.println("Character '" + characterName + "' not found in CharacterRegistry!");
                return null;
            }
            
            // Create skills list
            ArrayList<Ability.skill> skills = new ArrayList<>();
            if (skill1 != null) {
                Ability.skill skill = Ability.SkillRegistry.getByName(skill1);
                if (skill != null) {
                    skills.add(skill);
                } else {
                    System.err.println("Skill '" + skill1 + "' not found for character '" + characterName + "'");
                }
            }
            if (skill2 != null) {
                Ability.skill skill = Ability.SkillRegistry.getByName(skill2);
                if (skill != null) {
                    skills.add(skill);
                } else {
                    System.err.println("Skill '" + skill2 + "' not found for character '" + characterName + "'");
                }
            }
            if (skill3 != null) {
                Ability.skill skill = Ability.SkillRegistry.getByName(skill3);
                if (skill != null) {
                    skills.add(skill);
                } else {
                    System.err.println("Skill '" + skill3 + "' not found for character '" + characterName + "'");
                }
            }
            
            // Create character slot
            characterSlot slot = new characterSlot(
                character.getId(),
                character,
                character,
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
            characterSlot heroSlot = createCharacterSlot("Hero", "Slash", "Fireball", "heavy attack");
            // Hero 2
            characterSlot hero2Slot = createCharacterSlot("Hero2", "Charge attack", "Fireball", "Heal");
            // Hero 3
            characterSlot hero3Slot = createCharacterSlot("Flamita", "Rage Strike", "Rage Heal", "Rage Burst");





        }
    }
}
