package abilities;

import java.util.HashMap;
import java.util.Map;

public interface Ability {
    public class skill {
        int id;
        String name;
        String description;
        String type;
        String target;
        float atkScale;
        float AVScale;
        float mpCost;
        float partyMpCost;
        //Contructor----------------------------------------------------------------------------------
        public skill(int id, String name, String description, String type, String target, float atkScale, float AVScale, float mpCost, float partyMpCost) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.target = target;
            this.atkScale = atkScale;
            this.AVScale = AVScale;
            this.mpCost = mpCost;
            this.partyMpCost = partyMpCost;
        }

        public skill(int id, String name, String description, String type, String target, float atkScale, float AVScale) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.target = target;
            this.atkScale = atkScale;
            this.AVScale = AVScale;
            this.mpCost = 0;
            this.partyMpCost = 0;
        }

        //-------------------------------------------------------------------------------------------
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public float getAtkScale() {
            return atkScale;
        }

        public void setAtkScale(float atkScale) {
            this.atkScale = atkScale;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public float getAVScale() {
            return AVScale;
        }

        public void setAVScale(float AVScale) {
            this.AVScale = AVScale;
        }

        public float getMpCost() {
            return mpCost;
        }

        public void setMpCost(float mpCost) {
            this.mpCost = mpCost;
        }

        public float getPartyMpCost() {
            return partyMpCost;
        }

        public void setPartyMpCost(float partyMpCost) {
            this.partyMpCost = partyMpCost;
        }
    }

    public class SkillRegistry {
        private static final Map<Integer, skill> skillsById = new HashMap<>();
        private static final Map<String, skill> skillsByName = new HashMap<>();

        private static void register(Ability.skill skill) {
            skillsById.put(skill.id, skill);
            skillsByName.put(skill.name.toLowerCase(), skill);
        }

        public static Ability.skill getById(int id) {
            return skillsById.get(id);
        }

        public static Ability.skill getByName(String name) {
            return skillsByName.get(name.toLowerCase());
        }

        public static void init() {
            register(new Ability.skill(
                    1,
                    "Slash",
                    "A basic sword attack.",
                    "Physical",
                    "Single Enemy",
                    1.0f,
                    1.0f,
                    -50,
                    0

            ));

            register(new Ability.skill(
                    2,
                    "Fireball",
                    "A fireball that burns the enemy.",
                    "Magic",
                    "Single Enemy",
                    1.5f,
                    1.5f
            ));

            register(new Ability.skill(
                    3,
                    "Heal",
                    "Restore a small amount of HP.",
                    "Support",
                    "Ally",
                    -1.5f,
                    1.0f,
                    300,
                    0
            ));
            register(new Ability.skill(
                    4,
                    "heavy attack",
                    "a heavy attack that deals a lot of damage.",
                    "Physical",
                    "Single Enemy",
                    2.0f,
                    1.0f,
                    100,
                    0
            ));
        }
    }

}
