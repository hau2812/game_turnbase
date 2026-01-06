package items;

import characters.Observer;
import characters.BuffDebuff;
import characters.SpecialTalents;

/**
 * Consumable items that can be used during battle
 */
public class ConsumableItem extends Item {
    private ConsumableType type;
    private TargetType targetType;
    private float effectValue;
    private int duration; // For temporary effects (0 = instant)
    private String effectDescription;
    
    public enum ConsumableType {
        HEAL_HP("Heal HP"),
        HEAL_MP("Heal MP"),
        HEAL_BOTH("Heal HP & MP"),
        DAMAGE_OVER_TIME("Damage Over Time"),
        BUFF_ATTACK("Buff Attack"),
        BUFF_DEFENSE("Buff Defense"),
        BUFF_SPEED("Buff Speed"),
        DEBUFF_ENEMY("Debuff Enemy"),
        BURN("Burn"),
        POISON("Poison"),
        FREEZE("Freeze"),
        SHIELD("Shield");
        
        private final String displayName;
        
        ConsumableType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    public enum TargetType {
        SELF("Self"),
        ALLY("Ally"),
        ENEMY("Enemy"),
        ANY("Any");
        
        private final String displayName;
        
        TargetType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    public ConsumableItem(String id, String name, String description, int baseValue, 
                         ItemRarity rarity, ConsumableType type, TargetType targetType, 
                         float effectValue, int duration, String effectDescription) {
        super(id, name, description, baseValue, rarity);
        this.type = type;
        this.targetType = targetType;
        this.effectValue = effectValue;
        this.duration = duration;
        this.effectDescription = effectDescription;
    }
    
    // Legacy constructor for backward compatibility
    public ConsumableItem(String id, String name, String description, int baseValue, 
                         ItemRarity rarity, ConsumableType type, float effectValue, 
                         int duration, String effectDescription) {
        this(id, name, description, baseValue, rarity, type, TargetType.ALLY, effectValue, duration, effectDescription);
    }
    
    // Getters
    public ConsumableType getType() { return type; }
    public TargetType getTargetType() { return targetType; }
    public float getEffectValue() { return effectValue; }
    public int getDuration() { return duration; }
    public String getEffectDescription() { return effectDescription; }
    
    @Override
    public ItemType getItemType() {
        return ItemType.CONSUMABLE;
    }
    
    /**
     * Apply the consumable effect to a character
     */
    public void applyEffect(Observer.characterSlot target) {
        if (target == null || target.getCurrentHp() <= 0) return;
        
        switch (type) {
            case HEAL_HP:
                target.setCurrentHp(Math.min(target.getCharacter().getHp(), 
                    target.getCurrentHp() + effectValue));
                break;
                
            case HEAL_MP:
                target.setCurrentMp(Math.min(target.getCharacter().getMp(), 
                    target.getCurrentMp() + effectValue));
                break;
                
            case HEAL_BOTH:
                target.setCurrentHp(Math.min(target.getCharacter().getHp(), 
                    target.getCurrentHp() + effectValue));
                target.setCurrentMp(Math.min(target.getCharacter().getMp(), 
                    target.getCurrentMp() + effectValue));
                break;
                
            case DAMAGE_OVER_TIME:
                target.setCurrentHp(Math.max(0, target.getCurrentHp() - effectValue));
                break;
                
            case BUFF_ATTACK:
                BuffDebuff atkBuff = BuffDebuff.getByName("Strength Boost").copy();
                atkBuff.setValue(effectValue/100);
                atkBuff.setDuration(duration);
                SpecialTalents.applyBuffDebuff(target, atkBuff);
                SpecialTalents.applyStatModifications(target,null);
                break;
                
            case BUFF_DEFENSE:
                BuffDebuff defBuff = BuffDebuff.getByName("Defend Boost").copy();
                defBuff.setValue(effectValue/100);
                defBuff.setDuration(duration);
                SpecialTalents.applyBuffDebuff(target, defBuff);
                SpecialTalents.applyStatModifications(target,null);
                break;
                
            case BUFF_SPEED:
                BuffDebuff spdBuff = BuffDebuff.getByName("Speed Boost").copy();
                spdBuff.setValue(effectValue/100);
                spdBuff.setDuration(duration);
                SpecialTalents.applyBuffDebuff(target, spdBuff);
                SpecialTalents.applyStatModifications(target,null);
                break;
                
            case SHIELD:
                // Add shield effect (would need to implement shield system)
                break;
                
            case BURN:
                // Add burn debuff
                addDebuff(target, "Burn", "DOT", effectValue, duration);
                break;
                
            case POISON:
                // Add poison debuff
                addDebuff(target, "Poison", "DOT", effectValue, duration);
                break;
                
            case FREEZE:
                // Add freeze debuff (reduces speed)
                addDebuff(target, "Frozen", "Stunned", 0, duration);
                break;
                
            case DEBUFF_ENEMY:
                // Generic debuff (reduces attack)
                addDebuff(target, "Weakened", "ATK", -effectValue/100f, duration);
                break;
                
            default:
                // For other effects, just log
                System.out.println("Applied " + type.getDisplayName() + " effect: " + effectDescription);
                break;
        }
    }
    
    /**
     * Add a debuff effect to a character
     */
    private void addDebuff(Observer.characterSlot target, String name, String effect, float value, int duration) {
        if (target == null || target.getActiveEffects() == null) return;
        
        // Create new debuff with correct constructor parameters
        BuffDebuff debuff = new BuffDebuff(name, "Debuff", duration, effect, value, 1, 3, "Item");
        
        // Add to target's active effects
        target.getActiveEffects().add(debuff);
        
        System.out.println("Applied " + name + " debuff to " + target.getCharacter().getName() + 
                         " for " + duration + " turns");
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Effect: %s, Target: %s)", 
            rarity.getDisplayName(), name, description, effectDescription, targetType.getDisplayName());
    }
}
