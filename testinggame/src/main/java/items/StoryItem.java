package items;

/**
 * Story items that are collected during gameplay but don't appear in regular inventory
 */
public class StoryItem extends Item {
    
    public StoryItem(String id, String name, String description, int baseValue, ItemRarity rarity) {
        super(id, name, description, baseValue, rarity);
    }
    
    @Override
    public ItemType getItemType() {
        return ItemType.STORY_ITEM;
    }
}

