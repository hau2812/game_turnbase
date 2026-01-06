package items;

/**
 * Base class for all items in the game
 */
public abstract class Item {
    protected String id;
    protected String name;
    protected String description;
    protected int value; // Gold cost
    protected ItemRarity rarity;
    
    public enum ItemRarity {
        COMMON("Common", 1.0f),
        UNCOMMON("Uncommon", 1.0f),
        RARE("Rare", 1.0f),
        EPIC("Epic", 1.0f),
        LEGENDARY("Legendary", 1.0f);
        
        private final String displayName;
        private final float valueMultiplier;
        
        ItemRarity(String displayName, float valueMultiplier) {
            this.displayName = displayName;
            this.valueMultiplier = valueMultiplier;
        }
        
        public String getDisplayName() { return displayName; }
        public float getValueMultiplier() { return valueMultiplier; }
    }
    
    public Item(String id, String name, String description, int baseValue, ItemRarity rarity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = (int)(baseValue * rarity.getValueMultiplier());
        this.rarity = rarity;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getValue() { return value; }
    public ItemRarity getRarity() { return rarity; }
    
    // Abstract methods
    public abstract ItemType getItemType();
    
    public enum ItemType {
        CONSUMABLE,
        EQUIPMENT,
        STORY_ITEM
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s", rarity.getDisplayName(), name, description);
    }
}
