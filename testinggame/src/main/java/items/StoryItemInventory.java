package items;

import java.util.*;

/**
 * Inventory system specifically for story items
 * Story items don't appear in regular inventory but are stored separately
 */
public class StoryItemInventory {
    private Set<String> storyItems; // Set of story item IDs (no duplicates, no quantity)
    private Map<String, StoryItem> itemRegistry; // itemId -> StoryItem object
    
    public StoryItemInventory() {
        this.storyItems = new HashSet<>();
        this.itemRegistry = new HashMap<>();
    }
    
    /**
     * Add a story item to inventory
     */
    public void addStoryItem(StoryItem item) {
        if (item == null) return;
        
        String itemId = item.getId();
        storyItems.add(itemId);
        itemRegistry.put(itemId, item);
    }
    public void removeStoryItem(StoryItem item) {
        if (item == null) return;

        String itemId = item.getId();
        storyItems.remove(itemId);
        itemRegistry.remove(itemId, item);
    }
    
    /**
     * Add a story item by ID (if registered in ItemRegistry)
     */
    public void addStoryItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) return;
        
        Item item = ItemRegistry.getItem(itemId);
        if (item instanceof StoryItem) {
            addStoryItem((StoryItem) item);
        }
    }
    public void removeStoryItem(String itemId) {
        if (itemId == null || itemId.isEmpty()) return;

        Item item = ItemRegistry.getItem(itemId);
        if (item instanceof StoryItem) {
            removeStoryItem((StoryItem) item);
        }
    }
    
    /**
     * Check if player has a story item
     */
    public boolean hasStoryItem(String itemId) {
        return storyItems.contains(itemId);
    }
    
    /**
     * Get all story item IDs
     */
    public Set<String> getAllStoryItemIds() {
        return new HashSet<>(storyItems);
    }
    
    /**
     * Get story item object by ID
     */
    public StoryItem getStoryItem(String itemId) {
        return itemRegistry.get(itemId);
    }
    
    /**
     * Get all story items as a list
     */
    public List<StoryItem> getAllStoryItems() {
        List<StoryItem> items = new ArrayList<>();
        for (String itemId : storyItems) {
            StoryItem item = itemRegistry.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
    
    /**
     * Clear all story items (for testing/reset)
     */
    public void clear() {
        storyItems.clear();
        itemRegistry.clear();
    }
}

