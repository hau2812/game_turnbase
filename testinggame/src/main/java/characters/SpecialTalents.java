package characters;

import characters.Observer.characterSlot;
import characters.Characters.character;

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
    
    /**
     * Calculate actual damage after special talent effects (like mana shield)
     * Returns the amount of damage that should actually be applied to HP
     */
    public static float calculateActualDamage(characterSlot slot, float damageAmount) {
        character character = slot.getCharacter();
        float actualDamage = damageAmount;
        
        // Mana Shield - absorb damage with MP instead of HP
        if (character.getUniqueValue(MANA_SHIELD) != null) {
            float manaShieldAmount = character.getUniqueValueAsFloat(MANA_SHIELD);
            if (manaShieldAmount > 0 && slot.getCurrentMp() > 0) {
                float mpToUse = Math.min(damageAmount, slot.getCurrentMp())/2;
                slot.setCurrentMp(slot.getCurrentMp() - mpToUse);
                actualDamage -= mpToUse;
                System.out.println(character.getName() + " used " + mpToUse + " MP to absorb damage!");
            }
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
        
        // Regeneration - heal a small amount each turn
        if (character.getUniqueValue(REGENERATION) != null) {
            float regenAmount = character.getUniqueValueAsFloat(REGENERATION);
            if (regenAmount > 0) {
                float newHp = Math.min(character.getHp(), slot.getCurrentHp() + regenAmount);
                slot.setCurrentHp(newHp);
                System.out.println(character.getName() + " regenerated " + regenAmount + " HP!");
            }
        }
    }
    
    /**
     * Create a character with berserker talent
     */
    public static character createBerserker(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        uniqueValues.add(new Characters.uniqueValue(BERSERKER_RAGE, "0"));
        
        return new character(id, name, atk, matk, def, res, spd, hp, mp, uniqueValues);
    }
    
    /**
     * Create a character with mana shield talent
     */
    public static character createMage(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        uniqueValues.add(new Characters.uniqueValue(MANA_SHIELD, "1")); // 1 = active
        
        return new character(id, name, atk, matk, def, res, spd, hp, mp, uniqueValues);
    }
    
    /**
     * Create a character with critical strike talent
     */
    public static character createRogue(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        uniqueValues.add(new Characters.uniqueValue(CRITICAL_STRIKE, "0.3")); // 30% crit chance
        
        return new character(id, name, atk, matk, def, res, spd, hp, mp, uniqueValues);
    }
    
    /**
     * Create an enemy with regeneration talent
     */
    public static character createRegenerationEnemy(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp, float regenAmount) {
        java.util.ArrayList<Characters.uniqueValue> uniqueValues = new java.util.ArrayList<>();
        uniqueValues.add(new Characters.uniqueValue(REGENERATION, String.valueOf(regenAmount)));
        
        return new character(id, name, atk, matk, def, res, spd, hp, mp, uniqueValues);
    }
}
