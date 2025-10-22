package items;

import characters.Characters;

/**
 * Equipment items that can be equipped to characters to boost stats
 */
public class EquipmentItem extends Item {
    private EquipmentType type;
    private EquipmentSlot slot;
    private StatBonus statBonus;
    
    public enum EquipmentType {
        WEAPON("Weapon"),
        ARMOR("Armor"),
        ACCESSORY("Accessory"),
        SHIELD("Shield");
        
        private final String displayName;
        
        EquipmentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    public enum EquipmentSlot {
        WEAPON("Weapon"),
        ARMOR("Armor"),
        ACCESSORY("Accessory");
        
        private final String displayName;
        
        EquipmentSlot(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    public static class StatBonus {
        private float hpBonus = 0;
        private float mpBonus = 0;
        private float atkBonus = 0;
        private float matkBonus = 0;
        private float defBonus = 0;
        private float resBonus = 0;
        private float spdBonus = 0;
        
        public StatBonus() {}
        
        public StatBonus(float hp, float mp, float atk, float matk, float def, float res, float spd) {
            this.hpBonus = hp;
            this.mpBonus = mp;
            this.atkBonus = atk;
            this.matkBonus = matk;
            this.defBonus = def;
            this.resBonus = res;
            this.spdBonus = spd;
        }
        
        // Getters
        public float getHpBonus() { return hpBonus; }
        public float getMpBonus() { return mpBonus; }
        public float getAtkBonus() { return atkBonus; }
        public float getMatkBonus() { return matkBonus; }
        public float getDefBonus() { return defBonus; }
        public float getResBonus() { return resBonus; }
        public float getSpdBonus() { return spdBonus; }
        
        // Setters
        public void setHpBonus(float hpBonus) { this.hpBonus = hpBonus; }
        public void setMpBonus(float mpBonus) { this.mpBonus = mpBonus; }
        public void setAtkBonus(float atkBonus) { this.atkBonus = atkBonus; }
        public void setMatkBonus(float matkBonus) { this.matkBonus = matkBonus; }
        public void setDefBonus(float defBonus) { this.defBonus = defBonus; }
        public void setResBonus(float resBonus) { this.resBonus = resBonus; }
        public void setSpdBonus(float spdBonus) { this.spdBonus = spdBonus; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (hpBonus != 0) sb.append("HP: +").append((int)hpBonus).append(" ");
            if (mpBonus != 0) sb.append("MP: +").append((int)mpBonus).append(" ");
            if (atkBonus != 0) sb.append("ATK: +").append((int)atkBonus).append(" ");
            if (matkBonus != 0) sb.append("MATK: +").append((int)matkBonus).append(" ");
            if (defBonus != 0) sb.append("DEF: +").append((int)defBonus).append(" ");
            if (resBonus != 0) sb.append("RES: +").append((int)resBonus).append(" ");
            if (spdBonus != 0) sb.append("SPD: +").append((int)spdBonus).append(" ");
            return sb.toString().trim();
        }
    }
    
    public EquipmentItem(String id, String name, String description, int baseValue, 
                        ItemRarity rarity, EquipmentType type, EquipmentSlot slot, 
                        StatBonus statBonus) {
        super(id, name, description, baseValue, rarity);
        this.type = type;
        this.slot = slot;
        this.statBonus = statBonus;
    }
    
    // Getters
    public EquipmentType getType() { return type; }
    public EquipmentSlot getSlot() { return slot; }
    public StatBonus getStatBonus() { return statBonus; }
    
    @Override
    public ItemType getItemType() {
        return ItemType.EQUIPMENT;
    }
    
    /**
     * Apply stat bonuses to a character
     */
    public void applyStats(Characters.character character) {
        if (character == null || statBonus == null) return;
        
        character.setHp(character.getHp() + statBonus.getHpBonus());
        character.setMp(character.getMp() + statBonus.getMpBonus());
        character.setAtk(character.getAtk() + statBonus.getAtkBonus());
        character.setMatk(character.getMatk() + statBonus.getMatkBonus());
        character.setDef(character.getDef() + statBonus.getDefBonus());
        character.setRes(character.getRes() + statBonus.getResBonus());
        character.setSpd(character.getSpd() + statBonus.getSpdBonus());
        character.updateAV();
    }
    
    /**
     * Remove stat bonuses from a character
     */
    public void removeStats(Characters.character character) {
        if (character == null || statBonus == null) return;
        
        character.setHp(character.getHp() - statBonus.getHpBonus());
        character.setMp(character.getMp() - statBonus.getMpBonus());
        character.setAtk(character.getAtk() - statBonus.getAtkBonus());
        character.setMatk(character.getMatk() - statBonus.getMatkBonus());
        character.setDef(character.getDef() - statBonus.getDefBonus());
        character.setRes(character.getRes() - statBonus.getResBonus());
        character.setSpd(character.getSpd() - statBonus.getSpdBonus());
        character.updateAV();
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s: %s)", 
            rarity.getDisplayName(), name, description, slot.getDisplayName(), statBonus.toString());
    }
}
