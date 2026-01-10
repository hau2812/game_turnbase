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
    public BuffDebuff withDuration(int duration) { this.duration = duration; return this; }
    
    public String getEffects() { return effects; }
    public void setEffects(String effects) { this.effects = effects; }
    
    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }
    
    public int getStack() { return stack; }
    public void setStack(int stack) { this.stack = stack; }
    public BuffDebuff withStack(int stack) { this.stack = stack; return this; }
    
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
    
    /**
     * Creates a copy of this BuffDebuff with the same properties
     * @return A new BuffDebuff instance with identical properties
     */
    public BuffDebuff copy() {
        return new BuffDebuff(this.name, this.type, this.duration, this.effects, 
                            this.value, this.stack, this.maxStack, this.source);
    }
    
    // Initialize predefined BuffDebuff effects
    public static void init() {
        // ATK Buffs
        register(new BuffDebuff("Strength Boost", "Buff", 3, "ATK", 0.2f, 1, 3, "Skill"));
        register(new BuffDebuff("Berserker Rage", "Buff", 2, "ATK", 0.5f, 1, 1, "Skill"));
        register(new BuffDebuff("Oufuu atk up", "Buff", 999, "ATK", 0.5f, 1, 4, "Skill"));
        register(new BuffDebuff("Charging", "Buff", 3, "ATK", 0.2f, 1, 5, "Skill"));
        register(new BuffDebuff("Boiling blood", "Buff", 2, "ATK", 0.5f, 1, 1, "Skill"));
        register(new BuffDebuff("Combustion", "Buff", 999, "ATK", 0.2f, 1, 999, "Skill"));
        register(new BuffDebuff("Necro Sword", "Buff", 9999, "ATK", 5.0f, 1, 1, "Skill"));
        register(new BuffDebuff("Flame increment", "Buff", 9999, "ATK", 0.25f, 1, 999, "Skill"));

        // DEF Buffs
        register(new BuffDebuff("Defend Boost", "Buff", 4, "DEF", 0.3f, 1, 3, "Skill"));
        register(new BuffDebuff("Iron Skin", "Buff", 4, "DEF", 0.3f, 1, 2, "Skill"));
        register(new BuffDebuff("Shield", "Buff", 2, "DEF", 0.8f, 1, 1, "Skill"));

        // HP Buffs
        register(new BuffDebuff("Rage empowerment", "Buff", 2, "HP", 0.001f, 1, 1000, "Skill"));
        register(new BuffDebuff("Wither", "Debuff", 2, "HP", -0.1f, 1, 1000, "Skill"));

        // SPD Buffs
        register(new BuffDebuff("Speed Boost", "Buff", 3, "SPD", 0.4f, 1, 3, "Skill"));

        register(new BuffDebuff("Haste", "Buff", 3, "SPD", 0.4f, 1, 2, "Skill"));
        register(new BuffDebuff("Wind Speed", "Buff", 2, "SPD", 0.6f, 1, 1, "Skill"));
        register(new BuffDebuff("Gathering", "Buff", 20, "SPD", 100.0f, 1, 1, "Skill"));
        register(new BuffDebuff("Conserve", "Buff", 1, "SPD", 0.2f, 5, 999, "Skill"));
        register(new BuffDebuff("Judgment", "Buff", 1, "SPD", 0.0f, 1, 999, "Skill"));
        register(new BuffDebuff("Last dance", "Buff", 1, "SPD", 10.0f, 1, 1, "Skill"));
        register(new BuffDebuff("Excite", "Buff", 1, "SPD", 1.0f, 1, 1, "Skill"));

        // ATK Debuffs
        register(new BuffDebuff("Weakness", "Debuff", 3, "ATK", -0.1f, 1, 5, "Skill"));
        register(new BuffDebuff("Cripple", "Debuff", 2, "ATK", 0.6f, 1, 1, "Skill"));
        
        // DEF Debuffs
        register(new BuffDebuff("Vulnerability", "Debuff", 4, "DEF", 0.7f, 1, 2, "Skill"));
        register(new BuffDebuff("Expose", "Debuff", 2, "DEF", 0.5f, 1, 1, "Skill"));
        
        // SPD Debuffs
        register(new BuffDebuff("Slow", "Debuff", 3, "SPD", -0.3f, 1, 1, "Skill"));
        register(new BuffDebuff("Paralyze", "Debuff", 2, "SPD", 0.6f, 1, 1, "Skill"));
        
        // Special Effects
        register(new BuffDebuff("Poison", "Debuff", 5, "DOT", 10f, 1, 3, "Skill"));
        register(new BuffDebuff("Burn", "Debuff", 3, "DOT", 20f, 1, 10, "Skill"));
        register(new BuffDebuff("Void burn", "Debuff", 2, "DOT", 10f, 1, 99, "Skill"));
        register(new BuffDebuff("Ignite", "Debuff", 999, "DOT", 0f, 1, 1, "Skill"));


        register(new BuffDebuff("Regeneration", "Buff", 4, "HOT", 25f, 1, 10, "Skill"));
        register(new BuffDebuff("Mana Shield", "Buff", 3, "MP_COST", 0.5f, 1, 1, "Skill"));
        register(new BuffDebuff("Barrier", "Buff", 3, "BARRIER", 1f, 1, 999, "Skill"));
        register(new BuffDebuff("Moon shield", "Buff", 3, "MOON BARRIER", 1f, 1, 1, "Skill"));
        register(new BuffDebuff("Regen barrier", "Buff", 3, "REGEN BARRIER", 1f, 1, 999, "Skill"));

        register(new BuffDebuff("Rage empowerment host", "Buff", 999, "ATK", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Burning guts cd", "Buff", 999, "ATK", 0f, 1000, 1001, "Skill"));
        register(new BuffDebuff("Prey", "Debuff", 999, "mark", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Brave", "Debuff", 3, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Swap", "Buff", 1, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Taunt", "Buff", 2, "Taunt", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Dragon breath", "Buff", 1, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Invulnerable", "Buff", 2, "Invulnerable", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Vulnerable", "Buff", 2, "Vulnerable", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Rage absorption", "Buff", 2, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Stunned", "Debuff", 1, "Stunned", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Frozen", "Debuff", 1, "Stunned", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Mercy", "Debuff", 3, "Stunned", 0f, 1, 1, "Skill"));


        register(new BuffDebuff("Resurrection", "Buff", 1, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Sunset", "Buff", 1, "", 0f, 1, 1, "Skill"));

        register(new BuffDebuff("Arua's charge", "Buff", 999, "", 0f, 1, 3, "Skill"));
        register(new BuffDebuff("Elysion break down", "Debuff", 5, "", 0f, 1, 1, "Skill"));

        register(new BuffDebuff("Challenge", "Debuff", 999, "NoMp", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Challenge2", "Debuff", 999, "NoMp", 0f, 1, 1, "Skill"));

        register(new BuffDebuff("Confront", "Debuff", 999, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Banish", "Debuff", 999, "", 0f, 1, 1, "Skill"));

        register(new BuffDebuff("Power Trial", "Debuff", 999, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Power Target", "Debuff", 999, "", 0f, 750, 1, "Skill"));

        register(new BuffDebuff("Combo Trial", "Debuff", 999, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Combo Target", "Debuff", 999, "", 0f, 5, 9999, "Skill"));

        register(new BuffDebuff("Recovery Trial", "Debuff", 999, "", 0f, 1, 1, "Skill"));
        register(new BuffDebuff("Dragon breath!", "Buff", 999, "", 0f, 1, 1, "Skill"));

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
        return String.format("%s (%s) - %s: %.1f x%d (%d turns)(%d max stack)",
            name, type, effects, value, stack, duration,maxStack);
    }
}
