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
    }

    // ===================== REGISTRY =====================
    public class CharacterSlotRegistry {
        private static final Map<String, characterSlot> registry = new HashMap<>();



        public static characterSlot getByName(String name) {
            return registry.get(name);
        }

        public static Collection<characterSlot> getAll() {
            return registry.values();
        }

        public static void init() {

            // Ensure skills and characters are initialized
            Ability.SkillRegistry.init();
            Characters.CharacterRegistry.init();

            //Hero 1
            // Get the Hero character
            Characters.character hero = Characters.CharacterRegistry.getByName("Hero");

            // Create empty skills list
            ArrayList<Ability.skill> heroSkills = new ArrayList<>();
            heroSkills.add(Ability.SkillRegistry.getByName("Slash"));
            heroSkills.add(Ability.SkillRegistry.getByName("Fireball"));
            heroSkills.add(Ability.SkillRegistry.getByName("Heal"));

            // Create a slot for Hero
            characterSlot heroSlot = new characterSlot(
                    1,
                    hero,
                    hero,               // no transformed version yet
                    heroSkills,
                    hero.getHp(),       // start with full HP
                    hero.getMp()        // start with full MP
            );

            //Hero 2
            Characters.character hero2 = Characters.CharacterRegistry.getByName("Hero2");
            ArrayList<Ability.skill> hero2Skills = new ArrayList<>();
            hero2Skills.add(Ability.SkillRegistry.getByName("Slash"));
            hero2Skills.add(Ability.SkillRegistry.getByName("Fireball"));
            hero2Skills.add(Ability.SkillRegistry.getByName("heavy attack"));

            characterSlot hero2Slot = new characterSlot(
                    4,
                    hero2,
                    hero2,
                    hero2Skills,
                    hero2.getHp(),
                    hero2.getMp()
            );

            //Enemy
            // Get the Enemy character
            Characters.character enemy = Characters.CharacterRegistry.getByName("Enemy");
            // Create empty skills list
            ArrayList<Ability.skill> enemySkills = new ArrayList<>();
            enemySkills.add(Ability.SkillRegistry.getByName("Slash"));
            enemySkills.add(Ability.SkillRegistry.getByName("Fireball"));
            enemySkills.add(Ability.SkillRegistry.getByName("Heal"));

            // Create a slot for Hero
            characterSlot enemySlot = new characterSlot(
                    2,
                    enemy,
                    enemy,               // no transformed version yet
                    enemySkills,
                    enemy.getHp(),       // start with full HP
                    enemy.getMp()        // start with full MP
            );


            // Register the slot
            registry.put(hero.getName(), heroSlot);
            registry.put(hero2.getName(), hero2Slot);
            registry.put(enemy.getName(), enemySlot);



             //Enemy
            // Get the Enemy character
            Characters.character enemy2 = Characters.CharacterRegistry.getByName("Enemy2");
            // Create empty skills list
            ArrayList<Ability.skill> enemySkills2 = new ArrayList<>();
            enemySkills2.add(Ability.SkillRegistry.getByName("Slash"));
            enemySkills2.add(Ability.SkillRegistry.getByName("Fireball"));
            enemySkills2.add(Ability.SkillRegistry.getByName("Heal"));

            // Create a slot for Hero
            characterSlot enemySlot2 = new characterSlot(
                    3,
                    enemy2,
                    enemy2,               // no transformed version yet
                    enemySkills2,
                    enemy2.getHp(),       // start with full HP
                    enemy2.getMp()        // start with full MP
            );


            // Register the slot
            registry.put(hero.getName(), heroSlot);
            registry.put(hero2.getName(), hero2Slot);
            registry.put(enemy.getName(), enemySlot);
            registry.put(enemy2.getName(), enemySlot2);

        }
    }
}
