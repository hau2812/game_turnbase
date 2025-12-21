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
        String longdescription;
        String type;
        String target;
        float atkScale;
        float AVScale;
        float mpCost;
        float partyMpCost;
        String animation;
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
            this.longdescription = "";
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

        // Constructor with explicit longdescription
        public skill(int id, String name, String description, String type, String target,
                     float atkScale, float AVScale, float mpCost, float partyMpCost, String longdescription) {
            this(id, name, description, type, target, atkScale, AVScale, mpCost, partyMpCost);
            this.longdescription = longdescription;
        }

        public skill(int id, String name, String description, String type, String target, float atkScale, float AVScale) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.longdescription = "";
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
            this.longdescription = "";
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

        // Burning Rage constructor with explicit longdescription
        public skill(int id, String name, String description, String type, String target,
                     float atkScale, float AVScale, float mpCost, float partyMpCost,
                     float burningRageRequired, float burningRageConsumed, float burningRageGained,
                     String longdescription) {
            this(id, name, description, type, target, atkScale, AVScale, mpCost, partyMpCost,
                    burningRageRequired, burningRageConsumed, burningRageGained);
            this.longdescription = longdescription;
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

        public String getLongdescription() {
            return longdescription;
        }

        public void setLongdescription(String longdescription) {
            this.longdescription = longdescription;
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

        public String getAnimation() {
            return animation;
        }

        public void setAnimation(String animation) {
            this.animation = animation;
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

        @Override
        public String toString() {
            return "skill{" +
                    "name='" + name + '\'' +
                    '}';
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
                    0,
                    ""

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
                    0,
                    ""

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
                    0,
                    "Add 1 stack burn for 3 turn"
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
                    200,
                    0,
                    "Add 1 stack Regeneration for 3 turn"
            ));
                Ability.skill heal = getByName("Heal");
                if (heal != null) {
                    heal.addEffect("Regeneration", 3, 1);  // Regeneration for 3 turns, 1 stack
                }
            register(new Ability.skill(
                    4,
                    "heavy attack",
                    "A heavy attack that deals a lot of damage.",
                    "Physical",
                    "Single Enemy",
                    2.0f,
                    1.0f,
                    100,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    5,
                    "Charge attack",
                    "A charge attack that gain a lot of mana.",
                    "Physical",
                    "Single Enemy",
                    0.5f,
                    1.0f,
                    -100,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    6,
                    "Burning slash",
                    "An attack that consuming burning rage to deal more damage",
                    "Physical",
                    "Single Enemy",
                    1.5f,
                    1.2f,
                    0,
                    0,
                    50,
                    50,
                    0,
                    "dmg + rageConsumed*1.5"
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
                    50,      // Gains 0 Burning Rage
                    ""
            ));
            
            register(new Ability.skill(
                    8,
                    "Rage Burst",
                    "Consumes all Burning Rage to deal damage based on max HP and rage ratio and heal up to half of max Hp",
                    "Physical",
                    "Single Enemy",
                    0.0f,  // Base damage is 0, all damage comes from rage
                    1.0f,
                    0,
                    0,
                    200,
                    500,   // Consumes all Burning Rage (999 = all)
                    0,      // Gains 0 Burning Rage
                    "dmg = 3 * maxHp * (rageConsumed / (10*currentHp + rageConsumed));"
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
                    300,    // Consumes 30 Burning Rage
                    0,      // Gains 0 Burning Rage
                    "healingAmount = rageConsumed * (rageConsumed / (rageConsumed + currentHp))"
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
                    25,     // Gains 25 Burning Rage
                    ""
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
                    0,
                    "2 stack Burn for 3 turn"
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
                    0,
                    "5 stack weakness for 2 turn"
            ));
            Ability.skill forkLightning = getByName("7-Fork Lightning");
            forkLightning.setAnimation("spawn");
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
                    0,
                    "Hp=1,Mp=0,specialDmgBonus = hpReduction + mpReduction"
            ));
            Ability.skill Ecarr_Vertel = getByName("Ecarr Vertel");
            Ecarr_Vertel.setAnimation("spawn");
            
            register(new Ability.skill(
                    14,
                    "Barrier",
                    "Creates a protective barrier that absorbs damage",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    50,
                    0,
                    ""
            ));
            // Add Barrier effect to the Barrier skill
            Ability.skill barrier = getByName("Barrier");
            if (barrier != null) {
                barrier.addEffect("Barrier", 3, 100);
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
                    0,
                    ""
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
                    0,
                    ""
            ));
            Ability.skill voidStep = getByName("Void step");
            if (voidStep != null) {
                voidStep.addEffect("Gathering", 20, 1);
                voidStep.addEffect("Weakness", 20, 5);
            }

            register(new Ability.skill(
                    17,
                    "Eternal darkness",
                    "All shall be gone within the void",
                    "Magic",
                    "All enemy",
                    0.0f,
                    2.0f,
                    0,
                    0,
                    ""
            ));



            register(new Ability.skill(
                    18,
                    "Family united",
                    "Increase stat for Oufuu daddy",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill familyUnited = getByName("Family united");
            if (familyUnited != null) {
                familyUnited.addEffect("Oufuu atk up", 999, 1);
            }
            register(new Ability.skill(
                    19,
                    "Head bump",
                    "It's trying...",
                    "Physical",
                    "Enemy",
                    1.0f,
                    1.0f,
                    10,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    20,
                    "HEAD BUMP!!!",
                    "Watch and learn",
                    "Physical",
                    "Enemy",
                    1.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    21,
                    "Daddy fury",
                    "Why the small one matter",
                    "Physical",
                    "Aoe enemy",
                    2.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    22,
                    "Light attack",
                    "A quick slash that you didn't saw that coming",
                    "Physical",
                    "Enemy",
                    0.5f,
                    0.5f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    23,
                    "Energy charge",
                    "Gain a stackable attack buff",
                    "Heal",
                    "Self",
                    0.0f,
                    0.5f,
                    0,
                    0,
                    "2 Charging stack for 3 turn"
            ));
            Ability.skill energyCharge = getByName("Energy charge");
            if (energyCharge != null) {
                energyCharge.addEffect("Charging", 3, 2);
            }

            register(new Ability.skill(
                    24,
                    "Let me absorb you",
                    "Absorb an ally hp to gain mp",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    "Absorb 50% an ally current hp to gain 3 mp\n" +
                            "In Absolute teleportation absorb 40% an ally MaxHp to gain 100AV time stop"
            ));

            register(new Ability.skill(
                    25,
                    "Absolute teleportation",
                    "An extended version... for only the one",
                    "Heal",
                    "Self",
                    0.0f,
                    0.0f,
                    15,
                    0,
                    "Gain Conserve(0.2spd buff) stack equal amount of buff\n" +
                            "Prevent all characters other than self from moving, auto cast S1 on random alive enemy until not enough time stop AV for a S1,gain 1 stack of Judgment for each slash\n" +
                            "After get out of time stop,cast aoe skill scale with Judgment stack (25%atkScale for each stack)"
            ));

            register(new Ability.skill(
                    26,
                    "Judgment cut",
                    "You shall die",
                    "Physical",
                    "Aoe enemy",
                    0.0f,
                    0.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    27,
                    "Moon light",
                    "Grant a barrier and a buff",
                    "Heal",
                    "Aoe ally",
                    0.0f,
                    1.5f,
                    100,
                    0,
                    "Give Moon shield buff to allies\n" +
                            "When an ally have shield and Moon shield take damage, reduce 50% of damage to her Mp instead"
            ));
            Ability.skill moonLight = getByName("Moon light");
            if (moonLight != null) {
                moonLight.addEffect("Barrier", 3, 100);
                moonLight.addEffect("Moon shield", 999, 1);
            }

            register(new Ability.skill(
                    28,
                    "Moon wave",
                    "An ally instantly get a turn and speed buff for 1 turn",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    200,
                    0,
                    "Can't buff to herself"
            ));
            Ability.skill moonWave = getByName("Moon wave");
            if (moonWave != null) {
                moonWave.addEffect("Wind Speed", 1, 1);
            }

            register(new Ability.skill(
                    29,
                    "Absolute barrier",
                    "Grant all ally a buff which regen barrier each turn",
                    "Heal",
                    "Aoe ally",
                    0.0f,
                    1.5f,
                    0,
                    300,
                    "+50 barrier/turn for all allies"
            ));
            Ability.skill absoluteBarrier = getByName("Absolute barrier");
            if (absoluteBarrier != null) {
                absoluteBarrier.addEffect("Regen barrier", 10, 50);
            }
            //Flatina
            register(new Ability.skill(
                    30,
                    "Amber sacrifice",
                    "Consume hp of an ally to gain a huge attack buff",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill amberSacrifice = getByName("Amber sacrifice");
            if (amberSacrifice != null) {
                amberSacrifice.addEffect("Boiling blood", 3, 1);
            }
            register(new Ability.skill(
                    31,
                    "Rage empowerment",
                    "Consume burning rage each AV to increase maxHP for an ally\n",
                    "Heal",
                    "Ally",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    50,    // Requires 50 Burning Rage
                    0,    // Consumes 50 Burning Rage
                    0,      // Gains 0 Burning Rage
                    ""
            ));
            Ability.skill rageEmpowerment = getByName("Rage empowerment");
            if (rageEmpowerment != null) {
                rageEmpowerment.addEffect("Rage empowerment", 2, 1);
            }
            register(new Ability.skill(
                    32,
                    "Burning guts",
                    "All allies gain 1 guts",
                    "Heal",
                    "All ally",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    33,
                    "Tiger claw",
                    "Deal damage and heal",
                    "Physical",
                    "Enemy",
                    1.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    34,
                    "Dragon tail",
                    "Deal aoe damage and push them back",
                    "Physical",
                    "Aoe enemy",
                    1.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    35,
                    "Eagle fang",
                    "Deal damage and mark an enemy",
                    "Physical",
                    "Enemy",
                    0.75f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill eagleFang = getByName("Eagle fang");
            if (eagleFang != null) {
                eagleFang.addEffect("Prey", 999, 1);
            }

            register(new Ability.skill(
                    36,
                    "Weakening",
                    "Weakness 5 to prey",
                    "Physical",
                    "Enemy",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill Weakening = getByName("Weakening");
            if (Weakening != null) {
                Weakening.addEffect("Weakness", 1, 5);
            }

            register(new Ability.skill(
                    37,
                    "Push",
                    "Push prey back",
                    "Physical",
                    "Enemy",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    38,
                    "Taunt",
                    "Taunt prey",
                    "Physical",
                    "Enemy",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill Taunt = getByName("Taunt");
            if (Taunt != null) {
                Taunt.addEffect("Taunt", 2, 1);
            }

            register(new Ability.skill(
                    39,
                    "Backstep",
                    "Use one of her skill instead",
                    "non",
                    "Self",
                    0.0f,
                    0.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    40,
                    "Dragon breath",
                    "Continue consume party mp to deal damage to all enemy",
                    "non",
                    "Self",
                    0.0f,
                    0.1f,
                    0,
                    0,
                    ""
            ));
            Ability.skill dragonBreath = getByName("Dragon breath");
            if (dragonBreath != null) {
                dragonBreath.addEffect("Dragon breath", 2, 1);
            }

            register(new Ability.skill(
                    41,
                    "Arua's Arrow",
                    "Channels a portion of Arua's Strength into the form of an arrow",
                    "Magic",
                    "Enemy",
                    1.0f,
                    0.75f,
                    -100,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    42,
                    "Arua's Lighting Bolt",
                    "A powerful offensive spell that embodies Arua's Blessing in the form of lightning bolt",
                    "Magic",
                    "Enemy",
                    3.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill aruaLightingBolt = getByName("Arua's Lighting Bolt");
            if (aruaLightingBolt != null) {
                aruaLightingBolt.addEffect("Weakness", 2, 5);
            }

            register(new Ability.skill(
                    43,
                    "Aimhard's Absolute Defense",
                    "Aimhard's Blessing creates a binding of absolute defense around Lucia",
                    "Heal",
                    "Self",
                    0.0f,
                    1.0f,
                    300,
                    0,
                    ""
            ));
            Ability.skill Aimhard = getByName("Aimhard's Absolute Defense");
            if (Aimhard != null) {
                Aimhard.addEffect("Invulnerable", 3, 1);
            }
            register(new Ability.skill(
                    44,
                    "Durok's Gravity Field",
                    "Unleashes a powerful gravitational field that drastically decreases an enemy's ability to move.",
                    "Magic",
                    "Enemy",
                    0.0f,
                    0.5f,
                    300,
                    0,
                    ""
            ));
            Ability.skill Durok = getByName("Durok's Gravity Field");
            if (Durok != null) {
                Durok.addEffect("Slow", 3, 1);
                Durok.addEffect("Vulnerable", 3, 1);
            }
            register(new Ability.skill(
                    45,
                    "Aramute's obliterated",
                    "Consume every Hp and Mp to deal that much damage",
                    "Magic",
                    "Enemy",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    46,
                    "Channeling flame",
                    "Create fire orb to gain burning rage",
                    "Heal",
                    "Self",
                    0.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));

            register(new Ability.skill(
                    47,
                    "Rage absorption",
                    "Consume burning rage to gain maxHP",
                    "Heal",
                    "Self",
                    0.0f,
                    2.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill rageAbsorption = getByName("Rage absorption");
            if (rageAbsorption != null) {
                rageAbsorption.addEffect("Rage absorption", 3, 1);
            }
            register(new Ability.skill(
                    48,
                    "Life absorption",
                    "Consume health to deal damage to their maxHP",
                    "Physical",
                    "Enemy",
                    1.0f,
                    1.0f,
                    0,
                    0,
                    ""
            ));
            Ability.skill absorption = getByName("Life absorption");
            if (absorption != null) {
                absorption.addEffect("Wither",999,1);
            }
            register(new Ability.skill(
                    49,
                    "Regeneration",
                    "Heal",
                    "Heal",
                    "Self",
                    0.0f,
                    0.75f,
                    200,
                    0,
                    ""
            ));
            Ability.skill Regeneration = getByName("Regeneration");
            if (Regeneration != null) {
                Regeneration.addEffect("Regeneration", 3, 4);
            }
            register(new Ability.skill(
                    50,
                    "Sweep",
                    "Deal aoe damage",
                    "Physical",
                    "Aoe enemy",
                    1.0f,
                    1.0f,
                    150,
                    0,
                    ""
            ));
            register(new Ability.skill(
                    51,
                    "Last dance",
                    "Get 10x speed and a uto deal single target damage 20 time",
                    "Heal",
                    "Self",
                    0.0f,
                    0.0f,
                    300,
                    0,
                    ""
            ));
            Ability.skill Lastdance = getByName("Last dance");
            if (Lastdance != null) {
                Lastdance.addEffect("Last dance", 30, 1);
            }
            register(new Ability.skill(
                    52,
                    "Sunset",
                    "Gain flat damage bonus scale with lost hp",
                    "Heal",
                    "Self",
                    0.0f,
                    0.0f,
                    200,
                    0,
                    ""
            ));
            Ability.skill Sunset = getByName("Sunset");
            if (Sunset != null) {
                Sunset.addEffect("Sunset", 30, 1);
            }
        }

    }


}
