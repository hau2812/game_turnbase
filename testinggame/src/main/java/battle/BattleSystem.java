package battle;

import abilities.Ability;
import characters.Observer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BattleSystem {
    
    // Configuration
    private boolean includeHero2;
    private boolean includeEnemy2;
    
    // Character slots
    private Observer.characterSlot heroSlot;
    private Observer.characterSlot heroSlot2;
    private Observer.characterSlot enemySlot;
    private Observer.characterSlot enemySlot2;
    private Observer.characterSlot selectedTarget;
    private Observer.characterSlot selectedAllyTarget;
    private Observer.characterSlot selectedEnemyTarget;
    private Observer.characterSlot currentActingHero;
    
    // Combat state
    private boolean moving = false;
    private boolean autoEnemy = true;
    private Line turnOf = null;
    
    // Speed settings
    private double lineSpeed = 0.5;
    private double blueLineSpeed = lineSpeed;
    private double redLineSpeed = lineSpeed;
    private double yellowLineSpeed = lineSpeed;
    private double greenLineSpeed = lineSpeed;
    
    // UI reference (will be injected)
    private BattleUI battleUI;
    
    // Callback for battle end
    private Runnable onBattleWon;
    
    // Flag to control battle loop
    private boolean battleLoopActive = false;
    
    public BattleSystem(boolean includeHero2, boolean includeEnemy2) {
        this.includeHero2 = includeHero2;
        this.includeEnemy2 = includeEnemy2;
    }
    
    public void setBattleUI(BattleUI battleUI) {
        this.battleUI = battleUI;
    }
    
    public void setOnBattleWon(Runnable callback) {
        this.onBattleWon = callback;
    }
    
    public void initializeBattle() {
        // Configure which characters to create
        Observer.CharacterSlotRegistry.createHero2 = includeHero2;
        Observer.CharacterSlotRegistry.createEnemy2 = includeEnemy2;
        
        // Init registry
        Observer.CharacterSlotRegistry.init();

        // Hero slots
        if(heroSlot == null) {heroSlot = Observer.CharacterSlotRegistry.getByName("Hero");}

        if(heroSlot2 == null) {heroSlot2 = Observer.CharacterSlotRegistry.getByName("Hero2");}

        // Enemy slots
        //if(enemySlot == null) {enemySlot = Observer.CharacterSlotRegistry.getByName("Enemy");}
        //if(enemySlot2 == null) {enemySlot2 = Observer.CharacterSlotRegistry.getByName("Enemy2");}
        selectedTarget = enemySlot; // default target
        selectedAllyTarget = heroSlot; // ally selected initially
        selectedEnemyTarget = enemySlot; // default enemy target

        // Set current acting hero
        currentActingHero = heroSlot;
    }
    
    public void startBattleLoop() {
        // Stop any existing battle loop first
        stopBattleLoop();
        
        // Start new battle loop
        battleLoopActive = true;
        startBattleLoopRecursive();
    }
    
    private void startBattleLoopRecursive() {
        if (battleLoopActive) {
            updateLines();
            runOnce(() -> startBattleLoopRecursive(), Duration.millis(5));
        }
    }
    
    public void stopBattleLoop() {
        // Stop the battle loop
        battleLoopActive = false;
        setMoving(false);
    }
    
    public void useSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        if(attacker.getLine() != turnOf) {
            return;
        }
        
        // Check MP for heroes and deduct if needed
        if (attacker == heroSlot || attacker == heroSlot2) {
            float mpCost = skill.getMpCost();
            if (mpCost > 0 && attacker.getCurrentMp() < mpCost) {
                return; // not enough MP
            }
            if (true) {
                attacker.setCurrentMp(attacker.getCurrentMp() - mpCost);
                if(attacker.getCurrentMp() > attacker.getCharacterAfter().getMp()) {
                    attacker.setCurrentMp(attacker.getCharacterAfter().getMp());
                }
                if (battleUI != null) {
                    battleUI.updateMpUI(attacker);
                }
                // re-render to refresh affordability
                if (attacker == currentActingHero && battleUI != null) {
                    battleUI.renderHeroSkillsFor(attacker);
                }
            }
        }
        
        // Calculate base damage
        double dmg = attacker.getCharacter().getAtk() * skill.getAtkScale();
        
        // Apply special talent bonuses
        float talentBonus = characters.SpecialTalents.getDamageBonus(attacker);
        if (talentBonus > 0) {
            dmg += talentBonus;
            System.out.println(attacker.getCharacter().getName() + " talent bonus: +" + talentBonus + " damage!");
        }
        
        applyDamage(target, dmg);

        // Push line back (if attacker is hero)
        if (attacker == heroSlot || attacker == heroSlot2) {
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            Line heroLine = attacker == heroSlot ? battleUI.getBlueLine() : battleUI.getGreenLine();
            if (heroLine != null) {
                double barX = battleUI.getBarX();
                double barWidth = battleUI.getBarWidth();
                double newX = Math.min(barX + barWidth, heroLine.getStartX() + push);
                heroLine.setStartX(newX);
                heroLine.setEndX(newX);
            }
        } else if (attacker == enemySlot || attacker == enemySlot2) {
            // Enemy pushes red line
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            Line enemyLine = attacker == enemySlot ? battleUI.getRedLine() : battleUI.getYellowLine();
            if (enemyLine != null) {
                double barX = battleUI.getBarX();
                double barWidth = battleUI.getBarWidth();
                double newX = Math.min(barX + barWidth, enemyLine.getStartX() + push);
                enemyLine.setStartX(newX);
                enemyLine.setEndX(newX);
            }
        }
    }
    
    private void enemyTurn(Observer.characterSlot actingEnemy) {
        Random random = new Random();
        if (actingEnemy == null || actingEnemy.getCurrentHp() <= 0) {
            moving = true;
            return;
        }
        
        // Apply turn-based effects (like regeneration)
        characters.SpecialTalents.onTurnStart(actingEnemy);
        
        // Update UI after turn-based effects
        if (battleUI != null) {
            battleUI.updateHealthUI(actingEnemy);
        }
        Ability.skill chosenSkill = actingEnemy.getSkills()
                .get(random.nextInt(actingEnemy.getSkills().size()));
        // Pick random living hero as target if offensive
        if (chosenSkill.getAtkScale() < 0) {
            useSkill(actingEnemy, actingEnemy, chosenSkill);
        } else {
            boolean hero1Alive = heroSlot.getCurrentHp() > 0;
            boolean hero2Alive = heroSlot2 != null && heroSlot2.getCurrentHp() > 0;
            Observer.characterSlot targetHero;
            if (hero1Alive && hero2Alive) {
                targetHero = random.nextBoolean() ? heroSlot : heroSlot2;
            } else if (hero1Alive) {
                targetHero = heroSlot;
            } else if (hero2Alive) {
                targetHero = heroSlot2;
            } else {
                // No heroes alive, shouldn't happen but handle gracefully
                return;
            }
            useSkill(actingEnemy, targetHero, chosenSkill);
        }
        moving = true;
    }
    
    private void updateLines() {
        if (!moving) {
            return;
        }

        if (battleUI != null) {
            moveLine(battleUI.getBlueLine(), blueLineSpeed);
            if (battleUI.getRedLine() != null && enemySlot.getCurrentHp() > 0) {
                moveLine(battleUI.getRedLine(), redLineSpeed);
            }
            if (battleUI.getYellowLine() != null && enemySlot2 != null && enemySlot2.getCurrentHp() > 0) {
                moveLine(battleUI.getYellowLine(), yellowLineSpeed);
            }
            if (battleUI.getGreenLine() != null && heroSlot2 != null) {
                moveLine(battleUI.getGreenLine(), greenLineSpeed);
            }
        }
    }
    
    private void moveLine(Line line, double speed) {
        if (line == null) return;
        
        double newX = line.getStartX() - speed;
        double barX = battleUI.getBarX();

        if (newX <= barX) {
            turnOf = line;
            moving = false;
            if(autoEnemy) {
                // Enemy 1 turn
                if (line == battleUI.getRedLine() && enemySlot.getCurrentHp() > 0) {

                    runOnce(() -> enemyTurn(enemySlot), Duration.seconds(0.25));
                }
                // Enemy 2 turn
                else if (line == battleUI.getYellowLine() && enemySlot2 != null && enemySlot2.getCurrentHp() > 0) {

                    runOnce(() -> enemyTurn(enemySlot2), Duration.seconds(0.25));
                }
                else if (line == battleUI.getBlueLine() || (line == battleUI.getGreenLine() && heroSlot2 != null)) {
                    // When a hero's line reaches, render that hero's skills

                    currentActingHero = (line == battleUI.getBlueLine()) ? heroSlot : heroSlot2;
                    characters.SpecialTalents.onTurnStart(currentActingHero);
                    battleUI.refreshAllCharacterUI();
                    if (currentActingHero != null && battleUI != null) {
                        battleUI.renderHeroSkillsFor(currentActingHero);
                    }
                }
            }

            return;

        }

        line.setStartX(newX);
        line.setEndX(newX);
    }
    
    private void applyDamage(Observer.characterSlot slot, double amount) {
        float oldHp = slot.getCurrentHp();
        float actualAmount = (float)amount;

        // Calculate actual damage after special talents (like mana shield)
        if (amount > 0) { // Damage taken
            actualAmount = characters.SpecialTalents.calculateActualDamage(slot, (float)amount);
        }

        // Apply the actual damage/healing to HP
        slot.setCurrentHp((float)Math.max(0, slot.getCurrentHp() - actualAmount));
        if(slot.getCurrentHp() > slot.getCharacter().getHp()) {
            slot.setCurrentHp(slot.getCharacter().getHp());
        }

        // Handle special talents after damage is applied
        if (amount > 0) { // Damage taken
            characters.SpecialTalents.onDamageTaken(slot, actualAmount);
        } else if (amount < 0) { // Healing received
            characters.SpecialTalents.onHealingReceived(slot, Math.abs((float)amount));
        }
        
        if (battleUI != null) {
            battleUI.updateHealthUI(slot);
            battleUI.updateMpUI(slot); // Update MP bar when mana shield is used
            
            // Remove line if character dies
            if (slot.getCurrentHp() <= 0) {
                if (slot == enemySlot && battleUI.getRedLine() != null) {
                    getGameScene().removeUINode(battleUI.getRedLine());
                    battleUI.setRedLine(null);
                } else if (slot == enemySlot2 && battleUI.getYellowLine() != null) {
                    getGameScene().removeUINode(battleUI.getYellowLine());
                    battleUI.setYellowLine(null);
                } else if (slot == heroSlot && battleUI.getBlueLine() != null) {
                    getGameScene().removeUINode(battleUI.getBlueLine());
                    battleUI.setBlueLine(null);
                } else if (slot == heroSlot2 && battleUI.getGreenLine() != null) {
                    getGameScene().removeUINode(battleUI.getGreenLine());
                    battleUI.setGreenLine(null);
                }
                
                // Check if all enemies are defeated
                checkVictoryCondition();
            }
        }
    }
    
    // Getters for external access
    public Observer.characterSlot getHeroSlot() { return heroSlot; }
    public Observer.characterSlot getHeroSlot2() { return heroSlot2; }
    public Observer.characterSlot getEnemySlot() { return enemySlot; }
    public Observer.characterSlot getEnemySlot2() { return enemySlot2; }
    public Observer.characterSlot getSelectedTarget() { return selectedTarget; }
    public Observer.characterSlot getSelectedAllyTarget() { return selectedAllyTarget; }
    public Observer.characterSlot getSelectedEnemyTarget() { return selectedEnemyTarget; }
    public Observer.characterSlot getCurrentActingHero() { return currentActingHero; }
    
    public void setSelectedTarget(Observer.characterSlot target) { this.selectedTarget = target; }
    public void setSelectedAllyTarget(Observer.characterSlot target) { this.selectedAllyTarget = target; }
    public void setSelectedEnemyTarget(Observer.characterSlot target) { this.selectedEnemyTarget = target; }
    public void setMoving(boolean moving) { this.moving = moving; }
    public boolean isMoving() { return moving; }
    public Line getTurnOf() { return turnOf; }
    
    public void setMapEnemies(Observer.characterSlot enemy1, Observer.characterSlot enemy2) {
        // Clear old enemy data first
        this.enemySlot = null;
        this.enemySlot2 = null;
        
        // Update enemy slots with map enemies
        if (enemy1 != null) {
            this.enemySlot = enemy1;
        }
        if (enemy2 != null) {
            this.enemySlot2 = enemy2;
        }
        
        // Update selected target to first enemy
        this.selectedTarget = enemy1;
        this.selectedEnemyTarget = enemy1;
    }
    
    public void clearEnemyData() {
        // Clear all enemy data
        this.enemySlot = null;
        this.enemySlot2 = null;
        this.selectedTarget = null;
        this.selectedAllyTarget = null;
        this.selectedEnemyTarget = null;
        System.out.println("Enemy data cleared");
    }
    
    private void checkVictoryCondition() {
        // Check if all enemies are defeated
        boolean enemy1Defeated = enemySlot == null || enemySlot.getCurrentHp() <= 0;
        boolean enemy2Defeated = enemySlot2 == null || enemySlot2.getCurrentHp() <= 0;
        
        if (enemy1Defeated && enemy2Defeated) {
            System.out.println("Victory! All enemies defeated!");
            if (onBattleWon != null) {
                onBattleWon.run();
            }
        }
    }
}
