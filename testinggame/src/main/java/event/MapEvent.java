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
        super("Bạn tìm thấy một suối nước thiêng trong rừng. Nước suối có thể hồi phục sức khỏe.");
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
        System.out.println("Cả hai hero được hồi " + (int)healAmount + " HP!");
    }
}

class ForestTreasureEvent extends MapEvent {
    public ForestTreasureEvent() {
        super("Bạn phát hiện một kho báu cũ ẩn dưới rễ cây. Có thể chứa vật phẩm quý giá.");
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
                System.out.println("Bạn tìm thấy thuốc hồi phục! +150 HP và +100 MP cho cả đội!");
                break;
            case 1:
                // Big heal
                healCharacter(hero1, 400);
                healCharacter(hero2, 400);
                System.out.println("Bạn tìm thấy thuốc hồi phục mạnh! +400 HP cho cả đội!");
                break;
            case 2:
                // Trap damage
                damageCharacter(hero1, 100);
                damageCharacter(hero2, 100);
                System.out.println("Đó là một cái bẫy! Cả đội mất 100 HP!");
                break;
        }
    }
}

// Mountain Events
class MountainRockslideEvent extends MapEvent {
    public MountainRockslideEvent() {
        super("Lở đá từ trên cao! Bạn phải nhanh chóng tìm nơi trú ẩn.");
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
            System.out.println("Bạn không kịp trốn! Mất " + (int)damage + " HP!");
        } else if (outcome < 7) {
            // Neutral outcome - minor damage
            float damage = 50 + random.nextInt(100); // 50-150 damage
            damageCharacter(hero1, damage);
            damageCharacter(hero2, damage);
            System.out.println("Bạn trốn được phần lớn nhưng vẫn bị thương nhẹ. Mất " + (int)damage + " HP.");
        } else {
            // Good outcome - find treasure in the rubble
            healCharacter(hero1, 100);
            healCharacter(hero2, 100);
            restoreMp(hero1, 150);
            restoreMp(hero2, 150);
            System.out.println("Bạn tìm thấy kho báu trong đống đá! +100 HP và +150 MP!");
        }
    }
}

class MountainCaveEvent extends MapEvent {
    public MountainCaveEvent() {
        super("Một hang động bí ẩn xuất hiện trước mặt. Bên trong có ánh sáng kỳ lạ.");
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
                System.out.println("Tinh thể ma thuật tăng cường sức mạnh! +300 HP và +200 MP!");
                break;
            case 1:
                // Cursed cave - lose MP
                hero1.setCurrentMp(Math.max(0, hero1.getCurrentMp() - 100));
                hero2.setCurrentMp(Math.max(0, hero2.getCurrentMp() - 100));
                System.out.println("Hang động bị nguyền rủa! Mất 100 MP!");
                break;
            case 2:
                // Ancient wisdom - restore all MP
                restoreMp(hero1, hero1.getCharacter().getMp());
                restoreMp(hero2, hero2.getCharacter().getMp());
                System.out.println("Tri thức cổ xưa phục hồi toàn bộ MP!");
                break;
            case 3:
                // Cave monster - take damage
                damageCharacter(hero1, 200);
                damageCharacter(hero2, 200);
                System.out.println("Quái vật hang động tấn công! Mất 200 HP!");
                break;
        }
    }
}

class MountainSummitEvent extends MapEvent {
    public MountainSummitEvent() {
        super("Bạn đã đến đỉnh núi thiêng. Các vị thần núi ban phước lành cho hành trình của bạn.");
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
        
        System.out.println("Phước lành của núi thiêng! +500 HP và +300 MP cho cả đội!");
    }
}

// Village Events
class VillageShopEvent extends MapEvent {
    public VillageShopEvent() {
        super("Cửa hàng làng có nhiều vật phẩm hữu ích. Chủ cửa hàng thân thiện với khách hàng.");
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
        System.out.println("Bạn mua thuốc hồi phục! +250 HP và +150 MP!");
    }
}

class VillageRestEvent extends MapEvent {
    public VillageRestEvent() {
        super("Quán trọ ấm cúng với giường êm ái. Bạn có thể nghỉ ngơi và hồi phục sức lực.");
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
        System.out.println("Nghỉ ngơi hoàn toàn hồi phục HP và MP!");
    }
}

class VillageNPCEvent extends MapEvent {
    public VillageNPCEvent() {
        super("Một người dân làng có câu chuyện thú vị và có thể giúp đỡ bạn.");
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
                System.out.println("Người dân tốt bụng cho thuốc! +200 HP!");
                break;
            case 1:
                // Wise elder
                restoreMp(hero1, 200);
                restoreMp(hero2, 200);
                System.out.println("Người già khôn ngoan dạy phép thuật! +200 MP!");
                break;
            case 2:
                // Merchant
                healCharacter(hero1, 150);
                healCharacter(hero2, 150);
                restoreMp(hero1, 100);
                restoreMp(hero2, 100);
                System.out.println("Thương gia bán đồ giá rẻ! +150 HP và +100 MP!");
                break;
        }
    }
}
