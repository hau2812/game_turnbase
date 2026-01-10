package characters;

import abilities.Ability;
import battle.BattleSystem;
import battle.BattleUI;
import characters.Observer.characterSlot;
import characters.Characters.character;
import items.Inventory;
import org.example.testing;

/**
 * Utility class for managing special character talents and abilities
 * This makes it easy to add new special functions for characters
 */
public class SpecialTalents {
    
    // Talent names - centralized for consistency
    public static final String BERSERKER_RAGE = "BerserkerRage";
    public static final String MANA_SHIELD = "MANA_SHIELD";
    public static final String CRITICAL_STRIKE = "CriticalStrike";
    public static final String REGENERATION = "Regeneration";
    public static final String MP_REGENERATION = "MpRegeneration";
    public static final String BURNING_RAGE = "Burning rage";
    public static final String ENRAGE = "Enrage";
    public static final String GUTS = "Guts";
    public static final String PHASE1 = "Phase 1";
    public static final String MOON_BARRIER = "MOON BARRIER";
    public static final String REGEN_BARRIER = "REGEN BARRIER";
    
    // Static reference to BattleUI for use in static methods
    private static BattleUI battleUI;
    private static BattleSystem battleSystem;
    public static Inventory inventory;
    
    /**
     * Set the BattleUI reference for use in static methods
     * @param ui The BattleUI instance to use
     */
    public static void setBattleUI(BattleUI ui) {
        battleUI = ui;
    }
    public static void setBattleSystem(BattleSystem system) {
        battleSystem = system;
    }
    public static void setInventory(Inventory system) {
        inventory = system;
    }
    
    //public static final String OUFUULINK = "Oufuu link";
    // Battle system reference
    //private BattleSystem battleSystem;
    /**
     * Calculate actual damage after special talent effects (like mana shield)
     * Returns the amount of damage that should actually be applied to HP
     */
    public static float calculateActualDamage(characterSlot slot, float damageAmount) {
        if(damageAmount<0){

            return damageAmount;
        }
        if(testing.EASY_MODE==true&&battleSystem.isHero(slot)){
            damageAmount/=10;
        }
        character character = slot.getCharacter();
        float actualDamage = damageAmount;
        // Barrier - absorb damage with barrier stacks instead of HP
        if (slot.getActiveEffects() != null) {
            for (characters.BuffDebuff effect : slot.getActiveEffects()) {
                if ("BARRIER".equals(effect.getEffects()) && effect.getStack() > 0) {
                    if(slot.getFloatBuffDebuffByName("Moon shield")!=0&&battleSystem.getSlotByName("Leuna")!=null) {
                        Observer.characterSlot Leuna = battleSystem.getSlotByName("Leuna");
                        float damageToMana = Math.min(actualDamage/2, Leuna.currentMp);
                        Leuna.regenerateMp(-damageToMana);
                        actualDamage -= damageToMana;
                        battleUI.updateMpUI(slot);
                        if(actualDamage == 0){return 0;}

                    }
                        float barrierAmount = effect.getStack(); // Barrier amount equals stack count
                        float damageToBarrier = Math.min(actualDamage, barrierAmount);

                        // Reduce barrier stacks by damage amount
                        effect.setStack((int) (barrierAmount - damageToBarrier));
                        actualDamage -= damageToBarrier;

                        System.out.println(character.getName() + "'s Barrier absorbed " + damageToBarrier + " damage! Barrier: " + (int) barrierAmount + " -> " + effect.getStack());

                        // Remove barrier effect if stacks reach 0
                        if (effect.getStack() <= 0) {
                            slot.getActiveEffects().remove(effect);
                            System.out.println(character.getName() + "'s Barrier is broken!");
                        }
                        break; // Only process one barrier effect

                }else if("Invulnerable".equals(effect.getEffects())){
                    return 0;

                }
                else if("Combo Target".equals(effect.getName())){
                    Observer.characterSlot chigon =  battleSystem.getEnemySlot();
                    BuffDebuff comboTarget = chigon.getBuffDebuffByName("Combo Target");
                    if(comboTarget != null && comboTarget.getStack()>1) {
                        comboTarget.setStack(comboTarget.getStack() - 1);
                    }else{
                        chigon.removeBuffDebuffByName("Combo Target");
                        
                        // Complete trial cleanup (applies Stunned and removes debuffs)
                        if (battleSystem != null) {
                            battleSystem.completeTrialCleanup();
                        }
                    }
                    return 0;
                }
                else if("Power Target".equals(effect.getName())){
                    Observer.characterSlot chigon =  battleSystem.getEnemySlot();
                    BuffDebuff powerTarget = chigon.getBuffDebuffByName("Power Target");
                    if(powerTarget != null) {
                        int powerDamage = (int)actualDamage;
                        if(powerTarget.getStack() > powerDamage) {
                            // Reduce stack by damage amount
                            powerTarget.setStack(powerTarget.getStack() - powerDamage);
                        } else {
                            // Stack is depleted, remove Power Target
                            chigon.removeBuffDebuffByName("Power Target");
                            
                            // Complete trial cleanup (applies Stunned and removes debuffs)
                            if (battleSystem != null) {
                                battleSystem.completeTrialCleanup();
                            }
                        }
                    }
                    return 0;
                }
                else if("Vulnerable".equals(effect.getEffects())){
                    actualDamage*=1.5f;
                }
            }
        }

        // Mana Shield - absorb damage with MP instead of HP
        if (character.getUniqueValue(MANA_SHIELD) != null) {
            float manaShieldAmount = character.getUniqueValueAsFloat(MANA_SHIELD);
            if (manaShieldAmount > 0 && slot.getCurrentMp() > 0) {
                float mpToUse = Math.min(actualDamage, slot.getCurrentMp())/2;
                slot.setCurrentMp(slot.getCurrentMp() - mpToUse);
                actualDamage -= mpToUse;
                System.out.println(character.getName() + " used " + mpToUse + " MP to absorb damage!");
            }
        }
        if (character.getUniqueValue(GUTS) != null && character.getUniqueValueAsFloat(GUTS) > 0 && slot.getCurrentHp() <= damageAmount) {
            actualDamage = slot.currentHp-1;
            character.addToUniqueValue(GUTS,-1);
            System.out.println(character.getName() + " don't let the battle end " + (int)character.getUniqueValueAsFloat(GUTS) + " time remaining!");

        }

        // Burning Rage Protection - prevents HP from dropping below 1
        if (character.getUniqueValue(BURNING_RAGE) != null) {
            float currentHp = slot.getCurrentHp();
            float currentRage = getCurrentBurningRage(slot);
            
            // Check if this damage would reduce HP to 0 or below
            if (currentHp - actualDamage <= 0 && currentRage > 0) {
                float damageToHp = currentHp - 1; // Maximum damage to HP (leave 1 HP)
                float damageToRage = actualDamage - damageToHp; // Remaining damage goes to rage
                
                if (damageToRage <= currentRage) {
                    // Damage is less than or equal to burning rage
                    // HP stays at 1, rage absorbs the excess damage
                    actualDamage = damageToHp; // Only apply damage that reduces HP to 1
                    float rageConsumed = damageToRage;
                    character.addToUniqueValue(BURNING_RAGE,-rageConsumed);
                    System.out.println(character.getName() + " used " + rageConsumed + " Burning Rage to prevent death! HP: " + currentHp + " -> 1");
                } else {
                    // Damage is greater than burning rage
                    // HP goes to 0, all rage is consumed
                    actualDamage = damageToHp + currentRage; // Apply damage that reduces HP to 1 + all rage
                    System.out.println(character.getName() + " used all " + currentRage + " Burning Rage but still died!");
                }
            }else if(slot.containsBuffDebuff("Rage absorption")){
                slot.getCharacter().addToUniqueValue("Burning rage",-actualDamage);
                actualDamage=0;
            }
        }
        //--Boss Flamita special--------------------------------------------------------------------------------------
        if(character.getName().equals("Flamita ?")&& character.getUniqueValueAsFloat(PHASE1) > 0 && slot.getCurrentHp() <= actualDamage&& slot.getFloatBuffDebuffByName("Guts")==0){
            actualDamage = 0;
            slot.setCurrentHp(character.getHp()/2);
            character.addToUniqueValue(PHASE1,-1);
            character.addToUniqueValue(GUTS,1);
            character.addToUniqueValue("Phase 2",1);
            if(character.getUniqueValueAsFloat(BURNING_RAGE) < character.getHp()/2){
                character.setUniqueValue(BURNING_RAGE,character.getHp()/2 +"");
            }
        }else if(character.getUniqueValue("Phase 2")!=null&&character.getUniqueValueAsFloat(PHASE1) > 0 && slot.getCurrentHp() <= actualDamage&& slot.getFloatBuffDebuffByName("Guts")==0) {
            actualDamage = 0;
            slot.setCurrentHp(character.getHp());
            character.getUniqueValues().remove(character.getUniqueValue("Phase 1"));
            character.addToUniqueValue("Phase 2",1);
        }else if(character.getUniqueValue("Phase 3")!=null&&character.getUniqueValueAsFloat("Phase 2") > 0 && slot.getCurrentHp() <= actualDamage&& slot.getFloatBuffDebuffByName("Guts")==0) {
            actualDamage = 0;
            if(slot.getCharacter().getName().equals("Flamita The Immortal Phoenix")){
                character.getUniqueValues().remove(character.getUniqueValue("Phase 2"));
                character.addToUniqueValue("Phase 3",1);
                character.setUniqueValue("Burning rage","0");
                slot.setCurrentHp(1);
                slot.removeBuffDebuffByName("Rage absorption");
                slot.addBuffDebuff(BuffDebuff.getByName("Invulnerable").withDuration(999));
                slot.addBuffDebuff(BuffDebuff.getByName("Resurrection").withDuration(999));
                if(battleSystem.getEnemySlot2()!=null){
                    System.out.println("ok");
                    battleSystem.applyDamage(battleSystem.getEnemySlot2(),9999);
                }
                if(battleSystem.getEnemySlot3()!=null){
                    System.out.println("ok");
                    battleSystem.applyDamage(battleSystem.getEnemySlot3(),9999);
                }
                battleSystem.spawnFireOrb();

            }else {
                slot.setCurrentHp(character.getHp());
                character.getUniqueValues().remove(character.getUniqueValue("Phase 2"));
                character.addToUniqueValue("Phase 3",1);
            }

        }

        //--Boss Flamita special--------------------------------------------------------------------------------------

        if(character.getUniqueValue("Elysion Regeneration") != null && actualDamage >= slot.getCurrentHp()&&!slot.containsBuffDebuff("Elysion break down")) {
            //System.out.println("");
            actualDamage = 0;
            slot.setCurrentHp(1);
            slot.heal(character.getUniqueValueAsFloat("Elysion Regeneration"));
            character.setUniqueValue("Elysion Regeneration","0");
            slot.addBuffDebuff(BuffDebuff.getByName("Elysion break down").copy());
        }
        
        return Math.max(0, actualDamage); // Can't have negative damage
    }
    
    /**
     * Apply special talent effects when a character takes damage
     */
    public static void onDamageTaken(characterSlot slot, float damageAmount) {
        character character = slot.getCharacter();
        
        // Berserker Rage - increases attack based on HP lost
        if (character.getUniqueValue(BERSERKER_RAGE) != null) {
            character.addToUniqueValue(BERSERKER_RAGE, damageAmount);
            System.out.println(character.getName() + " lost " + damageAmount + " HP. Berserker rage: " + 
                character.getUniqueValueAsFloat(BERSERKER_RAGE));
        }
        
        // Burning Rage - increases by 1 for each 1 HP lost
        if (character.getUniqueValue(BURNING_RAGE) != null) {
            if(slot.currentHp>0) {
                if(inventory.containsEquipment(slot,"ashbringer")){
                    character.addToUniqueValue(BURNING_RAGE, damageAmount*1.25f);
                }else {
                    character.addToUniqueValue(BURNING_RAGE, damageAmount);
                }
            }else{
                character.setUniqueValue(BURNING_RAGE,"0");
            }
            if(character.getUniqueValueAsFloat(BURNING_RAGE) > character.getHp() ){
                character.getUniqueValue(BURNING_RAGE).setValue(character.hp+"");
            }
            System.out.println(character.getName() + " lost " + damageAmount + " HP. Burning rage: " + 
                character.getUniqueValueAsFloat(BURNING_RAGE));
        }
        if(character.getUniqueValue("Elysion Regeneration") != null){
            character.addToUniqueValue("Elysion Regeneration",damageAmount/4);
        }
        if(character.getName().equals("Flamita The Immortal Phoenix")){
            character.addToUniqueValue("actionCount",(int)(damageAmount/100));
        }
        if(character.getName().equals("Chigon The All Mighty Dragon")&&slot.getBuffDebuffByName("Dragon breath!")!=null){
            slot.setCurrentHp(slot.getCurrentHp()+damageAmount);
        }

    }
    
    /**
     * Apply special talent effects when a character is healed
     */
    public static void onHealingReceived(characterSlot slot, float healingAmount) {
        character character = slot.getCharacter();
        
        // Berserker Rage - reduce rage when healed
        if (character.getUniqueValue(BERSERKER_RAGE) != null) {
            float rageReduction = healingAmount * 0.5f;
            float currentRage = character.getUniqueValueAsFloat(BERSERKER_RAGE);
            float newRage = Math.max(0, currentRage - rageReduction);
            character.setUniqueValue(BERSERKER_RAGE, String.valueOf(newRage));
            System.out.println(character.getName() + " healed for " + healingAmount + ". Berserker rage reduced to: " + newRage);
        }
        if(slot.getCharacter().getName().equals("Flamita ?")&&slot.getCharacter().getUniqueValueAsFloat("Phase 2")==1){
            slot.getCharacter().setUniqueValue("Guts","1");
        }
    }
    
    /**
     * Calculate damage bonus from special talents
     */
    public static float getDamageBonus(characterSlot attacker) {
        character character = attacker.getCharacter();
        float bonus = 0f;
        
        // Berserker Rage bonus
        if (character.getUniqueValue(BERSERKER_RAGE) != null) {
            bonus += character.getUniqueValueAsFloat(BERSERKER_RAGE);
            character.addToUniqueValue(BERSERKER_RAGE, -bonus);
        }
        
        // Critical Strike chance
        if (character.getUniqueValue(CRITICAL_STRIKE) != null) {
            float critChance = character.getUniqueValueAsFloat(CRITICAL_STRIKE);
            if (Math.random() < critChance) {
                bonus += character.getAtk() * 0.5f; // 50% bonus damage on crit
                System.out.println(character.getName() + " scored a critical hit!");
            }
        }
        
        return bonus;
    }
    
    /**
     * Apply turn-based effects (called each turn)
     */
    public static void onTurnStart(characterSlot slot) {
        character character = slot.getCharacter();
        
        // Process buff/debuff effects first
        processBuffDebuffEffects(slot);

        // Regeneration - heal a small amount each turn
        if (character.getUniqueValue(REGENERATION) != null) {
            float regenAmount = character.getUniqueValueAsFloat(REGENERATION);
            if (regenAmount > 0) {
                float newHp = Math.min(character.getHp(), slot.getCurrentHp() + regenAmount);
                slot.setCurrentHp(newHp);
                System.out.println(character.getName() + " regenerated " + regenAmount + " HP!");
            }
        }
        
        // MP Regeneration - restore MP each turn, capped at max MP
        if (character.getUniqueValue(MP_REGENERATION) != null) {
            float mpRegenAmount = character.getUniqueValueAsFloat(MP_REGENERATION);
            if (mpRegenAmount > 0) {
                float currentMp = slot.getCurrentMp();
                float maxMp = character.getMp();
                float newMp = Math.min(maxMp, currentMp + mpRegenAmount);
                slot.setCurrentMp(newMp);
                float actualRegen = newMp - currentMp;
                if (actualRegen > 0) {
                    System.out.println(character.getName() + " regenerated " + actualRegen + " MP!");
                }
            }
        }
        if (character.getUniqueValue("Elysion Regeneration") != null) {
            float regenAmount = character.getUniqueValueAsFloat("Elysion Regeneration")/3;
            character.addToUniqueValue("Elysion Regeneration", -regenAmount);
            battleSystem.applyDamage(slot,-regenAmount);
            slot.regenerateMp(regenAmount/2);
        }
        if(slot.getFloatBuffDebuffByName("Regen barrier")>0&&slot.getFloatBuffDebuffByName("Conserve")==0){
            BuffDebuff shield = new BuffDebuff("Barrier", "Buff", 3, "BARRIER", 1f, 1, 999, "Skill");
            shield.setStack((int)slot.getFloatBuffDebuffByName("Regen barrier"));
            applyBuffDebuff(slot,shield);
            battleUI.updateBarrierBar(slot);
        }
    }
    
    /**
     * Process buff/debuff effects at the start of turn (apply effects but don't reduce duration yet)
     */
    public static void processBuffDebuffEffects(characterSlot slot) {
        if (slot.getActiveEffects() == null || slot.getActiveEffects().isEmpty()) {
            resetStatModification(slot);
            return;
        }
        
        // Process each active effect
        for (int i = slot.getActiveEffects().size() - 1; i >= 0; i--) {
            BuffDebuff effect = slot.getActiveEffects().get(i);

            // Apply stat modifications
            applyStatModifications(slot, effect);
            
            // Process damage over time effects
            if (effect.getEffects().equals("DOT")) {
                float dotDamage = effect.getTotalValue();
                dotDamage = calculateActualDamage(slot, dotDamage);


                if(slot.getActiveEffects().get(0).getName().equals(GUTS)){
                    slot.setCurrentHp(Math.max(1, slot.getCurrentHp() - dotDamage));
                }else {
                    slot.setCurrentHp(Math.max(0, slot.getCurrentHp() - dotDamage));
                }
                System.out.println(slot.getCharacter().getName() + " takes " + dotDamage + " damage from " + effect.getName() + "!");
            }
            
            // Process healing over time effects
            if (effect.getEffects().equals("HOT")) {
                float hotHealing = effect.getTotalValue();
                float newHp = Math.min(slot.getCharacter().getHp(), slot.getCurrentHp() + hotHealing);
                slot.setCurrentHp(newHp);
                System.out.println(slot.getCharacter().getName() + " heals " + hotHealing + " HP from " + effect.getName() + "!");
            }

            if(effect.getName().equals("Gathering")) {
                slot.setCurrentMp(0);
            }
            
            // Note: Duration reduction moved to processBuffDebuffDurationReduction()
        }
    }
    
    /**
     * Reduce duration of buff/debuff effects at the end of turn
     */
    public static void processBuffDebuffDurationReduction(characterSlot slot) {
        if (slot.getActiveEffects() == null || slot.getActiveEffects().isEmpty()) {
            return;
        }
        if(slot.containsBuffDebuff("Conserve")){
            applyBuffDebuff(slot,BuffDebuff.getByName("Judgment").copy());
            battleUI.updateBarrierBar(slot);
            return;
        }
        
        // Process each active effect
        for (int i = slot.getActiveEffects().size() - 1; i >= 0; i--) {
            BuffDebuff effect = slot.getActiveEffects().get(i);
            
            // Reduce duration
            effect.reduceDuration();
            
            // Remove expired effects
            if (effect.isExpired()) {
                slot.getActiveEffects().remove(i);
                // Update UI if battleUI is available
                if (battleUI != null) {
                    battleUI.updateBarrierBar(slot);
                }
                System.out.println(slot.getCharacter().getName() + "'s " + effect.getName() + " effect expired!");
            }
        }
    }
    
    /**
     * Process effects at the end of turn (reduce duration)
     */
    public static void onTurnEnd(characterSlot slot) {
        // Reduce duration of buff/debuff effects
        processBuffDebuffDurationReduction(slot);
    }
    
    /**
     * Apply stat modifications from buff/debuff effects
     */
    public static void resetStatModification(characterSlot slot) {

        character character = slot.getCharacter();
        character baseCharacter = slot.getBaseCharacter();

        character.setHp(baseCharacter.getHp());
        character.setMp(baseCharacter.getMp());
        character.setAtk(baseCharacter.getAtk());
        character.setMatk(baseCharacter.getMatk());
        character.setDef(baseCharacter.getDef());
        character.setRes(baseCharacter.getRes());
        character.setSpd(baseCharacter.getSpd());
        character.updateAV();
        equipmentStatModification(slot);
    }
    public static void equipmentStatModification(characterSlot slot) {
        if (inventory == null) {
            return; // Can't apply equipment stats without inventory reference
        }

        character character = slot.getCharacter();
        
        // Get all equipped items for this character
        java.util.Map<items.EquipmentItem.EquipmentSlot, items.EquipmentItem> equippedItems = inventory.getEquippedItems(slot);
        
        if (equippedItems == null || equippedItems.isEmpty()) {
            return; // No equipment to apply
        }

        // Sum up stat bonuses from all equipped items
        float totalHpBonus = 0;
        float totalMpBonus = 0;
        float totalAtkBonus = 0;
        float totalMatkBonus = 0;
        float totalDefBonus = 0;
        float totalResBonus = 0;
        float totalSpdBonus = 0;
        
        for (items.EquipmentItem equipment : equippedItems.values()) {
            if (equipment != null && equipment.getStatBonus() != null) {
                items.EquipmentItem.StatBonus statBonus = equipment.getStatBonus();
                totalHpBonus += statBonus.getHpBonus();
                totalMpBonus += statBonus.getMpBonus();
                totalAtkBonus += statBonus.getAtkBonus();
                totalMatkBonus += statBonus.getMatkBonus();
                totalDefBonus += statBonus.getDefBonus();
                totalResBonus += statBonus.getResBonus();
                totalSpdBonus += statBonus.getSpdBonus();
            }
        }

        // Apply stat bonuses to character
        character.setHp(character.getHp() + totalHpBonus);
        if(!character.getName().equals("Ina")) {
            character.setMp(character.getMp() + totalMpBonus);
        }
        character.setAtk(character.getAtk() + totalAtkBonus);
        character.setMatk(character.getMatk() + totalMatkBonus);
        character.setDef(character.getDef() + totalDefBonus);
        character.setRes(character.getRes() + totalResBonus);
        character.setSpd(character.getSpd() + totalSpdBonus);
        character.updateAV();

    }
    public static void  applyStatModifications(characterSlot slot, BuffDebuff effect) {
        if(slot == null) {
            System.out.println("the slot is null");
            return;
        }
        character character = slot.getCharacter();
        // Reset character stats to base character stats (this also applies equipment)
        resetStatModification(slot);
        float totalAtk = 1;
        float totalDef = 1;
        float totalSpd = 1;
        float totalHp = 1;
        // Apply all active effects
        for (BuffDebuff activeEffect : slot.getActiveEffects()) {
            if (activeEffect.getEffects().equals("ATK")) {
                totalAtk+=activeEffect.getTotalValue();
            } else if (activeEffect.getEffects().equals("DEF")) {
                totalDef+=activeEffect.getTotalValue();
            } else if (activeEffect.getEffects().equals("SPD")) {
                totalSpd+=activeEffect.getTotalValue();
            } else if (activeEffect.getEffects().equals("HP")) {
                totalHp+=activeEffect.getTotalValue();
            }
        }
        if(slot.getCharacter().getName().equals("Flamita The Immortal Phoenix")){
            totalAtk+=(slot.getCharacter().getHp()-5000)/5000;
        }
        // Use character's current stats (which include equipment) as base for multipliers
        // This preserves equipment bonuses while applying buff/debuff multipliers
        character.setAtk(character.getAtk()*totalAtk);
        character.setDef(character.getDef()*totalDef);
        character.setSpd(character.getSpd()*totalSpd);
        character.setHp(character.getHp()*totalHp);
        character.updateAV();
    }
    
    /**
     * Apply a buff/debuff effect to a character
     */
    public static void applyBuffDebuff(characterSlot slot, BuffDebuff effect) {

        if(inventory.containsEquipment(slot,"ice_witch_scarf")&&effect.getName().equals("Frozen")){
            return;
        }
        if(effect.getName().equals("Prey")){
            if(battleSystem.getPreyEnemies()!=null){
                battleSystem.getPreyEnemies().removeBuffDebuffByName(effect.getName());
            }
        }
        if (slot.getActiveEffects() == null) {
            slot.setActiveEffects(new java.util.ArrayList<>());
        }
        
        // Check if character already has this effect
        BuffDebuff existingEffect = null;
        for (BuffDebuff activeEffect : slot.getActiveEffects()) {
            if (activeEffect.getName().equals(effect.getName())) {
                existingEffect = activeEffect;
                break;
            }
        }
        
        if (existingEffect != null) {
            //Check Ignite
            if(effect.getName().equals("Ignite")&&slot.getBuffDebuffByName("Burn")!=null){
                slot.getBuffDebuffByName("Burn").setMaxStack(20);
            }

            // Character already has this effect
            if (existingEffect.canStack()) {

                // Add a stack
                existingEffect.addStack(effect.getStack());
                if(existingEffect.getStack()>existingEffect.getMaxStack()){
                    existingEffect.setStack(existingEffect.getMaxStack());
                }
                //System.out.println(slot.getCharacter().getName() + "'s " + effect.getName() + " effect stacked! Stacks: " + existingEffect.getStack());
            } //else {
                // Overwrite duration
                existingEffect.setDuration(effect.getDuration());
                //System.out.println(slot.getCharacter().getName() + "'s " + effect.getName() + " effect duration refreshed!");
            //}
        } else {
            // Add new effect
            slot.getActiveEffects().add(effect);
            //System.out.println(slot.getCharacter().getName() + " gained " + effect.getName() + " effect!");
        }
        
        // Apply stat modifications immediately
        applyStatModifications(slot, effect);
        if(effect.getEffects().equals("HP")){
            slot.setCurrentHp(Math.min(slot.getCurrentHp(),slot.getCharacter().getHp()));
            battleUI.updateHealthUI(slot);
        }
    }
    
    /**
     * Remove a specific buff/debuff effect from a character
     */
    public static void removeBuffDebuff(characterSlot slot, String effectName) {
        if (slot.getActiveEffects() == null) {
            return;
        }
        
        for (int i = slot.getActiveEffects().size() - 1; i >= 0; i--) {
            BuffDebuff effect = slot.getActiveEffects().get(i);
            if (effect.getName().equals(effectName)) {
                slot.getActiveEffects().remove(i);
                System.out.println(slot.getCharacter().getName() + "'s " + effectName + " effect was removed!");
                // Reapply stat modifications after removal
                applyStatModifications(slot, null);
                break;
            }
        }
    }
    /**
     * Calculate damage after def/res reduction
     * @param skill The skill being used
     * @param target The target character slot
     * @param damage The damage before reduction
     * @return The damage after def/res reduction
     */
    public static double calculateDefResReduction(Ability.skill skill, characterSlot attacker , characterSlot target, double damage) {
        if (skill == null || target == null) {
            return damage;
        }
        // Check skill type
        String skillType = skill.getType();
        if (skillType == null) {
            return damage;
        }
        
        float defResValue;
        
        // Use DEF for Physical, RES for Magic
        if ("Physical".equals(skillType)) {
            defResValue = target.getCharacter().getDef();
        } else if ("Magic".equals(skillType)) {
            defResValue = target.getCharacter().getRes();
        } else {
            // Not a damage skill (e.g., Heal), return original damage
            return damage;
        }
        
        // Formula: dmgAfterDef = dmg * (1 - (def/(def+100)))
        double reductionFactor = 1.0 - (defResValue / (defResValue + 100.0));
        return damage * reductionFactor;
    }










    
    /**
     * Create a character with berserker talent
     */
    public static character createBerserker(int id, String name, float atk, float matk, float def,
                                            float res, float spd, float hp, float mp, String talentDiscription) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        //uniqueValues.add(new Characters.uniqueValue(BERSERKER_RAGE, "0"));

        return new character(id, name, atk, matk, def, res, spd, hp, mp, talentDiscription, uniqueValues);
    }
    
    /**
     * Create a character with mana shield talent
     */
    public static character createMage(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp, String talentDiscription) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        uniqueValues.add(new Characters.uniqueValue(MANA_SHIELD, "1")); // 1 = active
        
        return new character(id, name, atk, matk, def, res, spd, hp, mp, talentDiscription, uniqueValues);
    }
    

    /**
     * Check if a character has enough Burning Rage to use a skill
     */
    public static boolean hasEnoughBurningRage(characterSlot slot, float requiredRage) {
        if (slot == null || slot.getCharacter().getUniqueValue(BURNING_RAGE) == null) {
            return requiredRage <= 0; // No rage required or character has no rage talent
        }
        return slot.getCharacter().getUniqueValueAsFloat(BURNING_RAGE) >= requiredRage;
    }
    
    /**
     * Consume Burning Rage from a character
     */

    
    /**
     * Gain Burning Rage for a character
     */
    public static void gainBurningRage(characterSlot slot, float amount) {
        if (slot == null || slot.getCharacter().getUniqueValue(BURNING_RAGE) == null) {
            return; // Character has no rage talent
        }
        
        slot.getCharacter().addToUniqueValue(BURNING_RAGE, amount);
        if(slot.getCharacter().getHp() < slot.getCharacter().getUniqueValueAsFloat(BURNING_RAGE)) {
            slot.getCharacter().setUniqueValue(BURNING_RAGE,slot.getCharacter().hp+"");
        }
        System.out.println(slot.getCharacter().getName() + " gained " + amount + " Burning Rage!");
    }
    
    /**
     * Get current Burning Rage amount for a character
     */
    public static float getCurrentBurningRage(characterSlot slot) {
        if (slot == null || slot.getCharacter().getUniqueValue(BURNING_RAGE) == null) {
            return 0;
        }
        return slot.getCharacter().getUniqueValueAsFloat(BURNING_RAGE);
    }
}
