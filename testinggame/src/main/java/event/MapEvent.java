package event;

import characters.Observer;
import java.util.Random;

public abstract class MapEvent extends GameEvent {
    protected Random random;
    
    public MapEvent(String description) {
        super(description);
        this.random = new Random();
    }
    
    // Abstract method for different event outcomes
    public abstract void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2);
    
    // Helper method to apply healing
    protected void healCharacter(Observer.characterSlot character, float amount) {
        if (character != null && character.getCurrentHp() > 0) {
            float newHp = Math.min(character.getCharacter().getHp(), character.getCurrentHp() + amount);
            character.setCurrentHp(newHp);
        }
    }
    
    // Helper method to apply MP restoration
    protected void restoreMp(Observer.characterSlot character, float amount) {
        if (character != null && character.getCurrentHp() > 0) {
            float newMp = Math.min(character.getCharacter().getMp(), character.getCurrentMp() + amount);
            character.setCurrentMp(newMp);
        }
    }
    
    // Helper method to apply damage
    protected void damageCharacter(Observer.characterSlot character, float amount) {
        if (character != null && character.getCurrentHp() > 0) {
            float newHp = Math.max(0, character.getCurrentHp() - amount);
            character.setCurrentHp(newHp);
        }
    }
}

// Forest Events
class ForestHealingEvent extends MapEvent {
    public ForestHealingEvent() {
        super("Ban tim thay mot suoi nuoc thieng trong rung. Nuoc suoi co the hoi phuc suc khoe.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        float healAmount = 200 + random.nextInt(300); // 200-500 HP
        healCharacter(hero1, healAmount);
        healCharacter(hero2, healAmount);
        System.out.println("Ca hai hero duoc hoi " + (int)healAmount + " HP!");
    }
}

class ForestTreasureEvent extends MapEvent {
    public ForestTreasureEvent() {
        super("Ban phat hien mot kho bau cu an duoi re cay. Co the chua vat pham quy gia.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        int outcome = random.nextInt(3);
        switch (outcome) {
            case 0:
                // Heal + MP
                healCharacter(hero1, 150);
                healCharacter(hero2, 150);
                restoreMp(hero1, 100);
                restoreMp(hero2, 100);
                System.out.println("Ban tim thay thuoc hoi phuc! +150 HP va +100 MP cho ca doi!");
                break;
            case 1:
                // Big heal
                healCharacter(hero1, 400);
                healCharacter(hero2, 400);
                System.out.println("Ban tim thay thuoc hoi phuc manh! +400 HP cho ca doi!");
                break;
            case 2:
                // Trap damage
                damageCharacter(hero1, 100);
                damageCharacter(hero2, 100);
                System.out.println("Do la mot cai bay! Ca doi mat 100 HP!");
                break;
        }
    }
}

// Mountain Events
class MountainRockslideEvent extends MapEvent {
    public MountainRockslideEvent() {
        super("Lo da tu tren cao! Ban phai nhanh chong tim noi tru an.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        int outcome = random.nextInt(10);
        if (outcome < 3) {
            // Bad outcome - take damage
            float damage = 150 + random.nextInt(200); // 150-350 damage
            damageCharacter(hero1, damage);
            damageCharacter(hero2, damage);
            System.out.println("Ban khong kip tron! Mat " + (int)damage + " HP!");
        } else if (outcome < 7) {
            // Neutral outcome - minor damage
            float damage = 50 + random.nextInt(100); // 50-150 damage
            damageCharacter(hero1, damage);
            damageCharacter(hero2, damage);
            System.out.println("Ban tron duoc phan lon nhung van bi thuong nhe. Mat " + (int)damage + " HP.");
        } else {
            // Good outcome - find treasure in the rubble
            healCharacter(hero1, 100);
            healCharacter(hero2, 100);
            restoreMp(hero1, 150);
            restoreMp(hero2, 150);
            System.out.println("Ban tim thay kho bau trong dong da! +100 HP va +150 MP!");
        }
    }
}

class MountainCaveEvent extends MapEvent {
    public MountainCaveEvent() {
        super("Mot hang dong bi an xuat hien truoc mat. Ben trong co anh sang ky la.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        int outcome = random.nextInt(4);
        switch (outcome) {
            case 0:
                // Crystal power - boost stats temporarily (simplified as heal + MP)
                healCharacter(hero1, 300);
                healCharacter(hero2, 300);
                restoreMp(hero1, 200);
                restoreMp(hero2, 200);
                System.out.println("Tinh the ma thuat tang cuong suc manh! +300 HP va +200 MP!");
                break;
            case 1:
                // Cursed cave - lose MP
                hero1.setCurrentMp(Math.max(0, hero1.getCurrentMp() - 100));
                hero2.setCurrentMp(Math.max(0, hero2.getCurrentMp() - 100));
                System.out.println("Hang dong bi nguyen rua! Mat 100 MP!");
                break;
            case 2:
                // Ancient wisdom - restore all MP
                restoreMp(hero1, hero1.getCharacter().getMp());
                restoreMp(hero2, hero2.getCharacter().getMp());
                System.out.println("Tri thuc co xua hoi phuc toan bo MP!");
                break;
            case 3:
                // Cave monster - take damage
                damageCharacter(hero1, 200);
                damageCharacter(hero2, 200);
                System.out.println("Quai vat hang dong tan cong! Mat 200 HP!");
                break;
        }
    }
}

class MountainSummitEvent extends MapEvent {
    public MountainSummitEvent() {
        super("Ban da den dinh nui thieng. Cac vi than nui ban phuoc lanh cho hanh trinh cua ban.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        // Always positive - major blessing
        float healAmount = 500;
        float mpAmount = 300;
        
        healCharacter(hero1, healAmount);
        healCharacter(hero2, healAmount);
        restoreMp(hero1, mpAmount);
        restoreMp(hero2, mpAmount);
        
        System.out.println("Phuoc lanh cua nui thieng! +500 HP va +300 MP cho ca doi!");
    }
}

// Village Events
class VillageShopEvent extends MapEvent {
    public VillageShopEvent() {
        super("Cua hang lang co nhieu vat pham huu ich. Chu cua hang than thien voi khach hang.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        // Shop always provides moderate benefits
        healCharacter(hero1, 250);
        healCharacter(hero2, 250);
        restoreMp(hero1, 150);
        restoreMp(hero2, 150);
        System.out.println("Ban mua thuoc hoi phuc! +250 HP va +150 MP!");
    }
}

class VillageRestEvent extends MapEvent {
    public VillageRestEvent() {
        super("Quan tro am cung voi giuong em ai. Ban co the nghi ngoi va hoi phuc suc luc.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        // Rest always fully heals
        hero1.setCurrentHp(hero1.getCharacter().getHp());
        hero2.setCurrentHp(hero2.getCharacter().getHp());
        hero1.setCurrentMp(hero1.getCharacter().getMp());
        hero2.setCurrentMp(hero2.getCharacter().getMp());
        System.out.println("Nghi ngoi hoan toan hoi phuc HP va MP!");
    }
}

class VillageNPCEvent extends MapEvent {
    public VillageNPCEvent() {
        super("Mot nguoi dan lang co cau chuyen thu vi va co the giup do ban.");
    }
    
    @Override
    public void trigger() {
        System.out.println(getDescription());
    }
    
    @Override
    public void applyEffect(Observer.characterSlot hero1, Observer.characterSlot hero2) {
        int outcome = random.nextInt(3);
        switch (outcome) {
            case 0:
                // Helpful villager
                healCharacter(hero1, 200);
                healCharacter(hero2, 200);
                System.out.println("Nguoi dan tot bung cho thuoc! +200 HP!");
                break;
            case 1:
                // Wise elder
                restoreMp(hero1, 200);
                restoreMp(hero2, 200);
                System.out.println("Nguoi gia khon ngoan day phep thuat! +200 MP!");
                break;
            case 2:
                // Merchant
                healCharacter(hero1, 150);
                healCharacter(hero2, 150);
                restoreMp(hero1, 100);
                restoreMp(hero2, 100);
                System.out.println("Thuong gia ban do gia re! +150 HP va +100 MP!");
                break;
        }
    }
}