package characters;

import java.util.HashMap;
import java.util.Map;

public class BuffDebuff {
    private String name;
    private String type; // "Buff" or "Debuff"
    private int duration;
    private String effects; // What stat it affects (e.g., "ATK", "DEF", "SPD")
    private float value; // Multiplier or flat value
    private int stack;
    private int maxStack;
    private String source; // What created this effect
    
    // Registry for storing predefined BuffDebuff effects
    private static final Map<String, BuffDebuff> registry = new HashMap<>();
    
    public BuffDebuff(String name, String type, int duration, String effects, float value, int stack, int maxStack, String source) {
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.effects = effects;
        this.value = value;
        this.stack = stack;
        this.maxStack = maxStack;
        this.source = source;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public String getEffects() { return effects; }
    public void setEffects(String effects) { this.effects = effects; }
    
    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }
    
    public int getStack() { return stack; }
    public void setStack(int stack) { this.stack = stack; }
    
    public int getMaxStack() { return maxStack; }
    public void setMaxStack(int maxStack) { this.maxStack = maxStack; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    /**
     * Get the total value combining base value and stack count
     * For multiplicative effects: totalValue = value * stack
     * For additive effects: totalValue = value + (stack - 1) * value
     * @return The total effective value
     */
    
    // Registry methods
    public static void register(BuffDebuff buffDebuff) {
        registry.put(buffDebuff.getName(), buffDebuff);
    }
    
    public static BuffDebuff getByName(String name) {
        return registry.get(name);
    }
    
    public static Map<String, BuffDebuff> getRegistry() {
        return registry;
    }
    
    // Initialize predefined BuffDebuff effects
    public static void init() {
        // ATK Buffs
        register(new BuffDebuff("Strength Boost", "Buff", 3, "ATK", 0.2f, 1, 3, "Skill"));
        register(new BuffDebuff("Berserker Rage", "Buff", 2, "ATK", 0.5f, 1, 1, "Skill"));
        register(new BuffDebuff("Oufuu atk up", "Buff", 999, "ATK", 1.0f, 1, 2, "Skill"));

        // DEF Buffs
        register(new BuffDebuff("Iron Skin", "Buff", 4, "DEF", 0.3f, 1, 2, "Skill"));
        register(new BuffDebuff("Shield", "Buff", 2, "DEF", 0.8f, 1, 1, "Skill"));
        
        // SPD Buffs
        register(new BuffDebuff("Haste", "Buff", 3, "SPD", 0.4f, 1, 2, "Skill"));
        register(new BuffDebuff("Wind Speed", "Buff", 2, "SPD", 0.6f, 1, 1, "Skill"));
        register(new BuffDebuff("Gathering", "Buff", 10, "SPD", 20.0f, 1, 1, "Skill"));
        // ATK Debuffs
        register(new BuffDebuff("Weakness", "Debuff", 3, "ATK", -0.1f, 1, 5, "Skill"));
        register(new BuffDebuff("Cripple", "Debuff", 2, "ATK", 0.6f, 1, 1, "Skill"));
        
        // DEF Debuffs
        register(new BuffDebuff("Vulnerability", "Debuff", 4, "DEF", 0.7f, 1, 2, "Skill"));
        register(new BuffDebuff("Expose", "Debuff", 2, "DEF", 0.5f, 1, 1, "Skill"));
        
        // SPD Debuffs
        register(new BuffDebuff("Slow", "Debuff", 3, "SPD", 0.8f, 1, 2, "Skill"));
        register(new BuffDebuff("Paralyze", "Debuff", 2, "SPD", 0.6f, 1, 1, "Skill"));
        
        // Special Effects
        register(new BuffDebuff("Poison", "Debuff", 5, "DOT", 10f, 1, 3, "Skill"));
        register(new BuffDebuff("Burn", "Debuff", 3, "DOT", 15f, 1, 5, "Skill"));
        register(new BuffDebuff("Void burn", "Debuff", 2, "DOT", 5f, 1, 99, "Skill"));

        register(new BuffDebuff("Regeneration", "Buff", 4, "HOT", 25f, 1, 2, "Skill"));
        register(new BuffDebuff("Mana Shield", "Buff", 3, "MP_COST", 0.5f, 1, 1, "Skill"));
        register(new BuffDebuff("Barrier", "Buff", 3, "BARRIER", 1f, 1, 999, "Skill"));
    }
    
    // Method to check if this effect can stack
    public boolean canStack() {
        return stack < maxStack;
    }
    
    // Method to add a stack
    public void addStack(int stack) {
        if (canStack()) {
            this.stack+=stack;
        }
    }
    
    // Method to reduce duration by 1
    public void reduceDuration() {
        if (duration > 0) {
            duration--;
        }
    }
    
    // Method to check if effect is expired
    public boolean isExpired() {
        return duration <= 0;
    }
    
    // Method to get the total effect value (value * stack)
    public float getTotalValue() {
        return (float) value * stack;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s: %.1f x%d (%d turns)", 
            name, type, effects, value, stack, duration);
    }
}
