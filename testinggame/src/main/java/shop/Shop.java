package shop;

import items.*;
import java.util.*;

/**
 * Shop system for buying and selling items
 */
public class Shop {
    private List<ShopItem> availableItems;
    private Random random;
    
    public static class ShopItem {
        private Item item;
        private int quantity;
        private int price; // Can be different from item's base value
        
        public ShopItem(Item item, int quantity, int price) {
            this.item = item;
            this.quantity = quantity;
            this.price = price;
        }
        
        public Item getItem() { return item; }
        public int getQuantity() { return quantity; }
        public int getPrice() { return price; }
        
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPrice(int price) { this.price = price; }
        
        public boolean canBuy(int amount) {
            return quantity >= amount;
        }
        
        public void buy(int amount) {
            quantity = Math.max(0, quantity - amount);
        }
    }
    
    public Shop() {
        this.availableItems = new ArrayList<>();
        this.random = new Random();
        generateShopItems();
    }
    
    /**
     * Generate random items for the shop
     */
    private void generateShopItems() {
        availableItems.clear();
        
        // Get all available items
        List<Item> allItems = new ArrayList<>(ItemRegistry.getAllItems());
        
        // Select 6-10 random items
        int itemCount = 6 + random.nextInt(5);
        Collections.shuffle(allItems);
        
        for (int i = 0; i < Math.min(itemCount, allItems.size()); i++) {
            Item item = allItems.get(i);
            int quantity = getRandomQuantity(item);
            int price = getShopPrice(item);
            
            availableItems.add(new ShopItem(item, quantity, price));
        }
    }
    
    /**
     * Get random quantity for an item based on its rarity
     */
    private int getRandomQuantity(Item item) {
        switch (item.getRarity()) {
            case COMMON:
                return 3 + random.nextInt(5); // 3-7
            case UNCOMMON:
                return 2 + random.nextInt(3); // 2-4
            case RARE:
                return 1 + random.nextInt(2); // 1-2
            case EPIC:
                return random.nextInt(2) + 1; // 1-2
            case LEGENDARY:
                return 1; // Always 1
            default:
                return 1;
        }
    }
    
    /**
     * Get shop price (usually 10-20% more than base value)
     */
    private int getShopPrice(Item item) {
        int basePrice = item.getValue();
        int markup = (int)(basePrice * (0.1 + random.nextDouble() * 0.1)); // 10-20% markup
        return basePrice + markup;
    }
    
    /**
     * Buy an item from the shop
     */
    public boolean buyItem(String itemId, int quantity, int playerGold) {
        ShopItem shopItem = findShopItem(itemId);
        if (shopItem == null || !shopItem.canBuy(quantity)) {
            return false;
        }
        
        int totalCost = shopItem.getPrice() * quantity;
        if (playerGold < totalCost) {
            return false;
        }
        
        shopItem.buy(quantity);
        return true;
    }
    
    /**
     * Sell an item to the shop (returns gold value)
     */
    public int sellItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return 0;
        
        // Shop buys items for 50% of their value
        int sellPrice = (item.getValue() / 2) * quantity;
        return sellPrice;
    }
    
    /**
     * Find a shop item by item ID
     */
    private ShopItem findShopItem(String itemId) {
        for (ShopItem shopItem : availableItems) {
            if (shopItem.getItem().getId().equals(itemId)) {
                return shopItem;
            }
        }
        return null;
    }
    
    /**
     * Get all available items in the shop
     */
    public List<ShopItem> getAvailableItems() {
        return new ArrayList<>(availableItems);
    }
    
    /**
     * Refresh shop with new items
     */
    public void refreshShop() {
        generateShopItems();
    }
    
    /**
     * Get shop summary for display
     */
    public String getShopSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SHOP ===\n");
        
        for (ShopItem shopItem : availableItems) {
            if (shopItem.getQuantity() > 0) {
                sb.append("- ").append(shopItem.getItem().getName())
                  .append(" x").append(shopItem.getQuantity())
                  .append(" - ").append(shopItem.getPrice()).append(" gold\n");
            }
        }
        
        return sb.toString();
    }
}
