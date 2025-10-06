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
        
        // Burning Rage interaction properties
        float burningRageRequired;  // Minimum Burning Rage required to use skill
        float burningRageConsumed;  // Amount of Burning Rage consumed when using skill
        float burningRageGained;    // Amount of Burning Rage gained when using skill
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
            this.burningRageRequired = 0;
            this.burningRageConsumed = 0;
            this.burningRageGained = 0;
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
            this.burningRageRequired = 0;
            this.burningRageConsumed = 0;
            this.burningRageGained = 0;
        }
        
        // New constructor for Burning Rage skills
        public skill(int id, String name, String description, String type, String target, float atkScale, float AVScale, float mpCost, float partyMpCost, float burningRageRequired, float burningRageConsumed, float burningRageGained) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.target = target;
            this.atkScale = atkScale;
            this.AVScale = AVScale;
            this.mpCost = mpCost;
            this.partyMpCost = partyMpCost;
            this.burningRageRequired = burningRageRequired;
            this.burningRageConsumed = burningRageConsumed;
            this.burningRageGained = burningRageGained;
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
        
        // Burning Rage getters and setters
        public float getBurningRageRequired() {
            return burningRageRequired;
        }

        public void setBurningRageRequired(float burningRageRequired) {
            this.burningRageRequired = burningRageRequired;
        }

        public float getBurningRageConsumed() {
            return burningRageConsumed;
        }

        public void setBurningRageConsumed(float burningRageConsumed) {
            this.burningRageConsumed = burningRageConsumed;
        }

        public float getBurningRageGained() {
            return burningRageGained;
        }

        public void setBurningRageGained(float burningRageGained) {
            this.burningRageGained = burningRageGained;
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
                    0,
                    "N/A",
                    "N/A",
                    "N/A",
                    "N/A",
                    0.0f,
                    0.0f,
                    0,
                    0

            ));
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
                    1.5f,
                    50,
                    0
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
            register(new Ability.skill(
                    5,
                    "Charge attack",
                    "a charge attack that gain a lot of mana.",
                    "Physical",
                    "Single Enemy",
                    0.5f,
                    1.0f,
                    -100,
                    0
            ));
            register(new Ability.skill(
                    6,
                    "Burning slash",
                    "an attack that consuming burning rage to deal massive damage",
                    "Physical",
                    "Single Enemy",
                    1f,
                    1.2f,
                    0,
                    0,
                    50,
                    50,
                    0
            ));
            
            // Burning Rage skills
            register(new Ability.skill(
                    7,
                    "Rage Strike",
                    "Powerful attack that gain Burning Rage",
                    "Physical",
                    "Single Enemy",
                    1.0f,
                    1.0f,
                    0,
                    0,
                    0,    // Requires 50 Burning Rage
                    0,    // Consumes 50 Burning Rage
                    50      // Gains 0 Burning Rage
            ));
            
            register(new Ability.skill(
                    8,
                    "Rage Burst",
                    "Consumes all Burning Rage to deal damage based on max HP and rage ratio",
                    "Physical",
                    "Single Enemy",
                    0.0f,  // Base damage is 0, all damage comes from rage
                    1.0f,
                    0,
                    0,
                    200,
                    999,   // Consumes all Burning Rage (999 = all)
                    0      // Gains 0 Burning Rage
            ));
            
            register(new Ability.skill(
                    9,
                    "Rage Heal",
                    "Convert Burning Rage into healing for self",
                    "Support",
                    "Self",
                    0.0f, // Healing
                    1.0f,
                    0,
                    0,
                    0,     // No minimum rage required (handled specially)
                    999,    // Consumes 30 Burning Rage
                    0      // Gains 0 Burning Rage
            ));
            
            register(new Ability.skill(
                    10,
                    "Berserker's Fury",
                    "Gain extra Burning Rage when taking damage",
                    "Support",
                    "Self",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    0,     // No Burning Rage required
                    0,     // Consumes 0 Burning Rage
                    25     // Gains 25 Burning Rage
            ));
            register(new Ability.skill(
                    11,
                    "5-Orb Flame",
                    "Medium flame spell circle from another world",
                    "Magic",
                    "Single Enemy",
                    2.0f,
                    1.0f,
                    150,
                    0
            ));
            register(new Ability.skill(
                    12,
                    "7-Fork Lightning",
                    "High weight spell circle from another world",
                    "Magic",
                    "Single Enemy",
                    3.0f,
                    1.0f,
                    300,
                    0
            ));
            register(new Ability.skill(
                    13,
                    "Ecarr Vertel",
                    "An incinerating spell that utilizes all of your physical and mental strength to create an explosion",
                    "Magic",
                    "Single Enemy",
                    2.0f,
                    1.0f,
                    200,
                    0
            ));
        }
    }

}
