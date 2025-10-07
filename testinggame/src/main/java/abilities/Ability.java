package abilities;

import characters.BuffDebuff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Ability {
    
    public class skillEffect {
        BuffDebuff buffDebuff;
        int duration;
        int stack;
        
        public skillEffect(BuffDebuff buffDebuff, int duration, int stack) {
            this.buffDebuff = buffDebuff;
            this.duration = duration;
            this.stack = stack;
        }
        
        public BuffDebuff getBuffDebuff() { return buffDebuff; }
        public void setBuffDebuff(BuffDebuff buffDebuff) { this.buffDebuff = buffDebuff; }
        
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        
        public int getStack() { return stack; }
        public void setStack(int stack) { this.stack = stack; }
    }
    
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
        
        // Skill effects (buffs/debuffs)
        ArrayList<skillEffect> skillEffects;
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
            this.skillEffects = new ArrayList<>();
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
            this.skillEffects = new ArrayList<>();
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
            this.skillEffects = new ArrayList<>();
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
        
        public ArrayList<skillEffect> getSkillEffects() { return skillEffects; }
        public void setSkillEffects(ArrayList<skillEffect> skillEffects) { this.skillEffects = skillEffects; }
        
        /**
         * Helper method to easily add effects to a skill
         * @param effectName The name of the BuffDebuff effect
         * @param duration Duration in turns
         * @param stack Number of stacks
         */
        public void addEffect(String effectName, int duration, int stack) {
            BuffDebuff effect = BuffDebuff.getByName(effectName);
            if (effect != null) {
                this.skillEffects.add(new skillEffect(effect, duration, stack));
            } else {
                System.err.println("Warning: BuffDebuff effect '" + effectName + "' not found!");
            }
        }
        
        /**
         * Helper method to add multiple effects at once
         * @param effects Array of effect data: {effectName, duration, stack}
         */
        public void addEffects(String[][] effects) {
            for (String[] effectData : effects) {
                if (effectData.length >= 3) {
                    addEffect(effectData[0], Integer.parseInt(effectData[1]), Integer.parseInt(effectData[2]));
                }
            }
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
            // Initialize BuffDebuff registry
            BuffDebuff.init();
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
                // Example: Adding multiple effects at once using the array method
                Ability.skill fireball = getByName("Fireball");
                if (fireball != null) {
                    String[][] effects = {
                            {"Burn", "3", "1"},           // Burn for 3 turns, 1 stack
                            //{"Weakness", "2", "1"}
                    };
                    fireball.addEffects(effects);
                }
            register(new Ability.skill(
                    3,
                    "Heal",
                    "Restore a small amount of HP.",
                    "Heal",
                    "Ally",
                    -1.5f,
                    1.0f,
                    300,
                    0
            ));
                Ability.skill heal = getByName("Heal");
                if (heal != null) {
                    heal.addEffect("Regeneration", 3, 1);  // Regeneration for 3 turns, 1 stack
                }
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
                    "Heal",
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
                Ability.skill orbFlame = getByName("5-Orb Flame");
                if (orbFlame != null) {
                    orbFlame.addEffect("Burn", 3, 2);
                }
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
            Ability.skill forkLightning = getByName("7-Fork Lightning");
            if (forkLightning != null) {
                //forkLightning.addEffect("Burn", 2, 1);        // Burn for 2 turns, 1 stack
                forkLightning.addEffect("Weakness", 2, 5);    // Weakness for 1 turn, 1 stack
            }
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
            
            register(new Ability.skill(
                    14,
                    "Barrier",
                    "Creates a protective barrier that absorbs damage",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    50,
                    0
            ));
            // Add Barrier effect to the Barrier skill
            Ability.skill barrier = getByName("Barrier");
            if (barrier != null) {
                barrier.addEffect("Barrier", 3, 100);  // 50 barrier points for 3 turns
            }

            register(new Ability.skill(
                    15,
                    "Calling void",
                    "A quick attack that infect Void Burn",
                    "Magic",
                    "Enemy",
                    0.1f,
                    1.0f,
                    -10,
                    0
            ));
            Ability.skill callingVoid = getByName("Calling void");
            if (callingVoid != null) {
                callingVoid.addEffect("Void burn", 3, 1);  // 50 barrier points for 3 turns
            }

            register(new Ability.skill(
                    16,
                    "Void step",
                    "You can't catch her",
                    "Heal",
                    "Self",
                    0.0f,
                    1.0f,
                    40,
                    0
            ));
            Ability.skill voidStep = getByName("Void step");
            if (voidStep != null) {
                voidStep.addEffect("Gathering", 10, 1);
                voidStep.addEffect("Weakness", 10, 5);
            }

            register(new Ability.skill(
                    17,
                    "Eternal darkness",
                    "All shall be gone with the void",
                    "Magic",
                    "All enemy",
                    0.0f,
                    2.0f,
                    0,
                    0
            ));
        }
    }

}
