package items;

import audio.AudioManager;
import battle.BattleSystem;
import battle.BattleUI;
import characters.Observer;
import dialog.DialogRegistrations;
import javafx.scene.shape.Line;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;

/**
 * Inventory system to manage items and equipment
 */
public class Inventory {
    private Map<String, Integer> items; // itemId -> quantity
    private Map<String, Item> itemRegistry; // itemId -> Item object
    private Map<EquipmentItem.EquipmentSlot, EquipmentItem> equippedItems; // Legacy - will be removed
    private Map<String, Map<EquipmentItem.EquipmentSlot, EquipmentItem>> characterEquipment; // characterName -> slot -> equipment
    private List<ConsumableItem> battleConsumables; // Max 3 items for battle
    private int gold;
    
    public Inventory() {
        this.items = new HashMap<>();
        this.itemRegistry = new HashMap<>();
        this.equippedItems = new HashMap<>();
        this.characterEquipment = new HashMap<>();
        this.battleConsumables = new ArrayList<>();
        this.gold = 300; // Starting gold
    }
    
    // ===================== ITEM MANAGEMENT =====================
    
    /**
     * Add an item to inventory
     */
    public void addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return;
        
        String itemId = item.getId();
        items.put(itemId, items.getOrDefault(itemId, 0) + quantity);
        itemRegistry.put(itemId, item);
    }
    
    /**
     * Remove an item from inventory
     */
    public boolean removeItem(String itemId, int quantity) {
        if (!items.containsKey(itemId) || items.get(itemId) < quantity) {
            return false;
        }
        
        int newQuantity = items.get(itemId) - quantity;
        if (newQuantity <= 0) {
            items.remove(itemId);
            itemRegistry.remove(itemId);
        } else {
            items.put(itemId, newQuantity);
        }
        return true;
    }
    public boolean removeQuantity(String itemId, int quantity) {
        if (!items.containsKey(itemId)) {
            return false;
        }
        items.put(itemId,items.get(itemId) - quantity);
        if (items.get(itemId) <= 0) {
            items.remove(itemId);
        }
        return true;
    }
    /**
     * Get item quantity
     */
    public int getItemQuantity(String itemId) {
        return items.getOrDefault(itemId, 0);
    }
    
    /**
     * Get all items in inventory
     */
    public Map<String, Integer> getAllItems() {
        return new HashMap<>(items);
    }
    
    /**
     * Get item object by ID
     */
    public Item getItem(String itemId) {
        return itemRegistry.get(itemId);
    }
    
    /**
     * Remove all items from inventory
     * This clears all items, item registry, equipped items, character equipment, and battle consumables
     * Note: Gold is not cleared by this method
     */
    public void removeAllItems() {
        items.clear();
        itemRegistry.clear();
        equippedItems.clear();
        characterEquipment.clear();
        battleConsumables.clear();
    }
    
    // ===================== EQUIPMENT MANAGEMENT =====================
    
    /**
     * Equip an item to a character
     */
    public boolean equipItem(String itemId, Observer.characterSlot character) {
        Item item = getItem(itemId);
        if (item == null || item.getItemType() != Item.ItemType.EQUIPMENT) {
            return false;
        }
        
        EquipmentItem equipment = (EquipmentItem) item;
        EquipmentItem.EquipmentSlot slot = equipment.getSlot();
        String characterName = character.getCharacter().getName();
        
        // Unequip current item in slot for this character
        unequipItem(slot, character);
        
        // Initialize character equipment map if needed
        if (!characterEquipment.containsKey(characterName)) {
            characterEquipment.put(characterName, new HashMap<>());
        }
        
        // Equip new item to character
        characterEquipment.get(characterName).put(slot, equipment);
        //character.addEquipment(equipment);

        // Remove from inventory
        removeItem(itemId, 1);
        //Check if dialog
        DialogRegistrations.showEquipmentDialog(characterName+equipment.getName()+"Dialog");
        return true;
    }
    
    /**
     * Unequip an item from a character
     */
    public boolean unequipItem(EquipmentItem.EquipmentSlot slot, Observer.characterSlot character) {
        String characterName = character.getCharacter().getName();
        
        // Check if character has equipment in this slot
        if (!characterEquipment.containsKey(characterName)) {
            return false;
        }
        
        EquipmentItem equipped = characterEquipment.get(characterName).get(slot);
        if (equipped == null) return false;
        
        // Remove stats
        equipped.removeStats(character.getCharacter());
        
        // Add back to inventory
        addItem(equipped, 1);
        
        // Remove from character equipment
        characterEquipment.get(characterName).remove(slot);
        //reset Hp/Mp
        if(character.getCurrentHp()>character.getCharacter().getHp()){
            character.setCurrentHp(character.getCharacter().getHp());
        }
        if(character.getCurrentMp()>character.getCharacter().getMp()){
            character.setCurrentMp(character.getCharacter().getMp());
        }
        return true;
    }
    
    /**
     * Get equipped item in slot
     */
    public EquipmentItem getEquippedItem(EquipmentItem.EquipmentSlot slot) {
        return equippedItems.get(slot);
    }
    
    /**
     * Get equipped item for specific character and slot
     */
    public EquipmentItem getEquippedItem(Observer.characterSlot character, EquipmentItem.EquipmentSlot slot) {
        String characterName = character.getCharacter().getName();
        
        if (!characterEquipment.containsKey(characterName)) {
            return null;
        }
        
        return characterEquipment.get(characterName).get(slot);
    }
    
    /**
     * Get all equipped items for a specific character
     */
    public Map<EquipmentItem.EquipmentSlot, EquipmentItem> getEquippedItems(Observer.characterSlot character) {
        String characterName = character.getCharacter().getName();
        
        if (!characterEquipment.containsKey(characterName)) {
            return new HashMap<>();
        }
        
        return new HashMap<>(characterEquipment.get(characterName));
    }
    
    /**
     * Check if a character has a specific equipment item by ID
     * @param characterSlot The character slot to check
     * @param itemId The item ID to search for
     * @return true if the character has the equipment, false otherwise
     */
    public boolean containsEquipment(Observer.characterSlot characterSlot, String itemId) {
        if (characterSlot == null || itemId == null) {
            return false;
        }
        
        Map<EquipmentItem.EquipmentSlot, EquipmentItem> equippedItems = getEquippedItems(characterSlot);
        
        for (EquipmentItem item : equippedItems.values()) {
            if (item != null && itemId.equals(item.getId())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get all equipped items (legacy method)
     */
    public Map<EquipmentItem.EquipmentSlot, EquipmentItem> getEquippedItems() {
        return new HashMap<>(equippedItems);
    }
    
    // ===================== BATTLE CONSUMABLES =====================
    
    /**
     * Add consumable to battle selection (max 3, 1 per slot)
     */
    public boolean addBattleConsumable(String itemId) {
        if (battleConsumables.size() >= 3) return false;
        
        Item item = getItem(itemId);
        if (item == null || item.getItemType() != Item.ItemType.CONSUMABLE) {
            return false;
        }
        
        ConsumableItem consumable = (ConsumableItem) item;
        
        // Check if this item is already in battle consumables
        int count=0;
        for (ConsumableItem existing : battleConsumables) {
            if (existing.getId().equals(itemId)) {
                count++;
            }
        }
        if(count==items.get(itemId)) {
            return false;
        }
        
        battleConsumables.add(consumable);
        return true;
    }
    
    /**
     * Remove consumable from battle selection
     */
    public boolean removeBattleConsumable(int index) {
        if (index < 0 || index >= battleConsumables.size()) return false;
        
        battleConsumables.remove(index);
        return true;
    }
    
    /**
     * Get battle consumables
     */
    public List<ConsumableItem> getBattleConsumables() {
        return new ArrayList<>(battleConsumables);
    }
    
    /**
     * Use a battle consumable
     */
    public boolean useBattleConsumable(int index, Observer.characterSlot target) {
        if (index < 0 || index >= battleConsumables.size()) return false;
        
        ConsumableItem consumable = battleConsumables.get(index);
        //revive
        if(consumable.id.equals("potion_of_revival")&&target.getCurrentHp()<=0){
            target.setCurrentHp(0.001f);
            DialogRegistrations.getBattleUI().createLines();
            DialogRegistrations.getBattleUI().updateHealthUI(target);
        }

        consumable.applyEffect(target);
        //remove items quantity
        Item item = battleConsumables.get(index);
        removeQuantity(item.getId(),1);
        // Remove from battle consumables after use
        battleConsumables.remove(index);
        AudioManager.getInstance().playSound("magic_cast.wav");
        return true;
    }
    
    // ===================== GOLD MANAGEMENT =====================
    
    public int getGold() { return gold; }
    
    public void addGold(int amount) {
        gold += amount;
    }
    
    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }
    
    // ===================== UTILITY METHODS =====================
    
    /**
     * Check if player can afford an item
     */
    public boolean canAfford(Item item) {
        return gold >= item.getValue();
    }
    
    /**
     * Buy an item from shop
     */
    public boolean buyItem(Item item, int quantity) {
        int totalCost = item.getValue() * quantity;
        if (!canAfford(item) || !spendGold(totalCost)) {
            return false;
        }
        
        addItem(item, quantity);
        return true;
    }
    
    /**
     * Sell an item to shop
     */
    public boolean sellItem(String itemId, int quantity) {
        if (!removeItem(itemId, quantity)) return false;
        
        Item item = getItem(itemId);
        int sellValue = item != null ? item.getValue() / 2 : 0; // Sell for half price
        addGold(sellValue * quantity);
        
        return true;
    }
    
    /**
     * Give all items from ItemRegistry, each 3 times
     */
    public void giveAllItems() {
        Collection<Item> allItems = ItemRegistry.getAllItems();
        for (Item item : allItems) {
            if (item != null) {
                addItem(item, 3);
            }
        }
    }
    
    /**
     * Get inventory summary
     */
    public String getInventorySummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY ===\n");
        sb.append("Gold: ").append(gold).append("\n\n");
        
        sb.append("Items:\n");
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            Item item = itemRegistry.get(entry.getKey());
            if (item != null) {
                sb.append("- ").append(item.getName()).append(" x").append(entry.getValue()).append("\n");
            }
        }
        
        sb.append("\nEquipped Items:\n");
        for (Map.Entry<EquipmentItem.EquipmentSlot, EquipmentItem> entry : equippedItems.entrySet()) {
            sb.append("- ").append(entry.getKey().getDisplayName()).append(": ").append(entry.getValue().getName()).append("\n");
        }
        
        sb.append("\nBattle Consumables:\n");
        for (int i = 0; i < battleConsumables.size(); i++) {
            sb.append("- ").append(i + 1).append(". ").append(battleConsumables.get(i).getName()).append("\n");
        }
        
        return sb.toString();
    }
}
