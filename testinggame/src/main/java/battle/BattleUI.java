package battle;

import abilities.Ability;
import characters.Observer;
import com.almasb.fxgl.app.GameApplication;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static com.almasb.fxgl.dsl.FXGL.*;

public class  BattleUI {
    
    /**
     * Helper class to hold Burning Rage bar data
     */
    private static class BurningRageBarData {
        public Observer.characterSlot characterSlot;
        public Rectangle rageBar;
        public double baseX;
        public double baseY;
        
        public BurningRageBarData(Observer.characterSlot slot, Rectangle bar, double x, double y) {
            this.characterSlot = slot;
            this.rageBar = bar;
            this.baseX = x;
            this.baseY = y;
        }
    }
    
    /**
     * Helper class to hold Health bar data
     */
    private static class HealthBarData {
        public Observer.characterSlot characterSlot;
        public Rectangle healthBar;
        public Rectangle healthBorder;
        public Rectangle mpBar;
        public Rectangle mpBorder;
        public Text hpText;
        public Text mpText;
        public Text nameText;
        public double baseX;
        public double baseY;
        
        public HealthBarData(Observer.characterSlot slot, Rectangle healthBar, Rectangle healthBorder, 
                           Rectangle mpBar, Rectangle mpBorder, Text hpText, Text mpText, Text nameText, 
                           double x, double y) {
            this.characterSlot = slot;
            this.healthBar = healthBar;
            this.healthBorder = healthBorder;
            this.mpBar = mpBar;
            this.mpBorder = mpBorder;
            this.hpText = hpText;
            this.mpText = mpText;
            this.nameText = nameText;
            this.baseX = x;
            this.baseY = y;
        }
    }
    
    // Lines
    private Line blueLine;
    private Line greenLine;
    private Line redLine;
    private Line yellowLine;
    
    // Health bars - using list-based approach
    private java.util.List<HealthBarData> healthBars = new java.util.ArrayList<>();
    
    // Burning Rage bars (red bars that show accumulated rage)
    private java.util.List<BurningRageBarData> burningRageBars = new java.util.ArrayList<>();
    
    // Skill boxes
    private Rectangle skill1Box;
    private Rectangle skill2Box;
    private Rectangle skill3Box;
    
    // Layout constants
    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;
    private double healthBarWidth = 200;
    private double healthBarHeight = 20;
    
    // Battle system reference
    private BattleSystem battleSystem;
    
    public BattleUI(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;
    }
    
    public void initializeUI() {
        createTimingBar();
        createLines();
        createHealthBars();
        createHealthText();
        createNameText();
        addUIElements();
        setupTargetSelection();
        
        // Update health and MP bars with current values for all characters
        updateAllHealthAndMpBars();
    }
    
    private void updateAllHealthAndMpBars() {
        // Update health bars for all characters
        for (HealthBarData healthBarData : healthBars) {
            updateHealthUI(healthBarData.characterSlot);
            if (healthBarData.mpBar != null) {
                updateMpUI(healthBarData.characterSlot);
            }
        }
        
        // Update all Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            updateBurningRageBar(rageBarData.characterSlot);
        }
    }
    
    /**
     * Refresh HP and MP UI for all character slots
     * This method updates all characters' health and MP bars in one call
     */
    public void refreshAllCharacterUI() {
        // Update health and MP bars for all characters
        if (battleSystem.getHeroSlot() != null) {
            updateHealthUI(battleSystem.getHeroSlot());
            updateMpUI(battleSystem.getHeroSlot());
        }
        if (battleSystem.getHeroSlot2() != null) {
            updateHealthUI(battleSystem.getHeroSlot2());
            updateMpUI(battleSystem.getHeroSlot2());
        }
        if (battleSystem.getEnemySlot() != null) {
            updateHealthUI(battleSystem.getEnemySlot());
        }
        if (battleSystem.getEnemySlot2() != null) {
            updateHealthUI(battleSystem.getEnemySlot2());
        }
    }
    
    private void createTimingBar() {
        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);
    }
    
    private void createLines() {
        // Blue line (Hero 1) - only if hero exists and has HP > 0
        if (battleSystem.getHeroSlot() != null && battleSystem.getHeroSlot().getCurrentHp() > 0) {
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth / 1.75);
        blueLine.setEndX(barX + barWidth / 1.75);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);
        }

        // Green line (Hero 2) - only if hero exists and has HP > 0
        if (battleSystem.getHeroSlot2() != null && battleSystem.getHeroSlot2().getCurrentHp() > 0) {
            greenLine = new Line();
            greenLine.setStroke(Color.LIMEGREEN);
            greenLine.setStrokeWidth(3);
            greenLine.setStartX(barX + barWidth / 1.9);
            greenLine.setEndX(barX + barWidth / 1.9);
            greenLine.setStartY(barY);
            greenLine.setEndY(barY + barHeight);
        }

        // Red line (Enemy 1) - only if enemy exists and has HP > 0
        if (battleSystem.getEnemySlot() != null && battleSystem.getEnemySlot().getCurrentHp() > 0) {
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth/1.5);
        redLine.setEndX(barX + barWidth/1.5);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);
        }

        // Yellow line (Enemy 2) - only if enemy exists and has HP > 0
        if (battleSystem.getEnemySlot2() != null && battleSystem.getEnemySlot2().getCurrentHp() > 0) {
            yellowLine = new Line();
            yellowLine.setStroke(Color.YELLOW);
            yellowLine.setStrokeWidth(3);
            yellowLine.setStartX(barX + barWidth/1.33);
            yellowLine.setEndX(barX + barWidth/1.33);
            yellowLine.setStartY(barY);
            yellowLine.setEndY(barY + barHeight);
        }

        // Add lines to scene - only if they were created
        if (blueLine != null) {
        getGameScene().addUINode(blueLine);
        }
        if (greenLine != null) {
            getGameScene().addUINode(greenLine);
        }
        if (redLine != null) {
        getGameScene().addUINode(redLine);
        }
        if (yellowLine != null) {
            getGameScene().addUINode(yellowLine);
        }

        // Set lines to character slots - only if characters exist and have HP > 0
        if (battleSystem.getHeroSlot() != null && battleSystem.getHeroSlot().getCurrentHp() > 0) {
        battleSystem.getHeroSlot().setLine(blueLine);
        }
        if (battleSystem.getHeroSlot2() != null && battleSystem.getHeroSlot2().getCurrentHp() > 0) {
            battleSystem.getHeroSlot2().setLine(greenLine);
        }
        if (battleSystem.getEnemySlot() != null && battleSystem.getEnemySlot().getCurrentHp() > 0) {
        battleSystem.getEnemySlot().setLine(redLine);
        }
        if (battleSystem.getEnemySlot2() != null && battleSystem.getEnemySlot2().getCurrentHp() > 0) {
            battleSystem.getEnemySlot2().setLine(yellowLine);
        }
    }
    
    private void createHealthBars() {
        // Clear existing health bars
        healthBars.clear();
        
        // Define character slots and their positions/colors
        Observer.characterSlot[] allSlots = {
            battleSystem.getHeroSlot(),
            battleSystem.getHeroSlot2(),
            battleSystem.getEnemySlot(),
            battleSystem.getEnemySlot2()
        };
        
        // Position data: [healthX, healthY, mpX, mpY]
        double[][] positions = {
            {50, 300, 50, 325},   // Hero (blue)
            {50, 350, 50, 375},   // Hero2 (green)
            {550, 300, 550, 325}, // Enemy (red)
            {550, 350, 550, 375}  // Enemy2 (crimson)
        };
        
        // Health bar colors
        Color[] healthColors = {Color.BLUE, Color.LIMEGREEN, Color.RED, Color.CRIMSON};
        Color[] mpColors = {Color.DODGERBLUE, Color.MEDIUMSEAGREEN, Color.DODGERBLUE, Color.DODGERBLUE};
        
        // Create health bars for all existing characters
        for (int i = 0; i < allSlots.length; i++) {
            Observer.characterSlot slot = allSlots[i];
            if (slot == null) continue;
            
            double[] pos = positions[i];
            double healthX = pos[0];
            double healthY = pos[1];
            double mpX = pos[2];
            double mpY = pos[3];
            
            // Create health border
            Rectangle healthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
            healthBorder.setStroke(Color.BLACK);
            healthBorder.setStrokeWidth(2);
            healthBorder.setTranslateX(healthX);
            healthBorder.setTranslateY(healthY);
            
            // Create health bar
            Rectangle healthBar = new Rectangle(healthBarWidth, healthBarHeight, healthColors[i]);
            healthBar.setTranslateX(healthX);
            healthBar.setTranslateY(healthY);
            
            // Create MP border and bar (only for heroes)
            Rectangle mpBorder = null;
            Rectangle mpBar = null;
            if (i < 2) { // Only heroes have MP
                mpBorder = new Rectangle(healthBarWidth, 10, Color.TRANSPARENT);
                mpBorder.setStroke(Color.BLACK);
                mpBorder.setStrokeWidth(1);
                mpBorder.setTranslateX(mpX);
                mpBorder.setTranslateY(mpY);
                
                mpBar = new Rectangle(healthBarWidth, 10, mpColors[i]);
                mpBar.setTranslateX(mpX);
                mpBar.setTranslateY(mpY);
            }
            
            // Create text elements (will be created in createHealthText)
            Text hpText = null;
            Text mpText = null;
            Text nameText = null;
            
            // Add to health bars list
            healthBars.add(new HealthBarData(slot, healthBar, healthBorder, 
                mpBar, mpBorder, hpText, mpText, nameText, healthX, healthY));
        }
        
        // Create Burning Rage bars (red bars that show accumulated rage)
        createBurningRageBars();
    }
    
    private void createBurningRageBars() {
        // Clear existing rage bars
        burningRageBars.clear();
        
        // Create rage bars for all characters that have Burning Rage talent
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            
            if (hasBurningRage(slot)) {
                Rectangle rageBar = new Rectangle(0, healthBarHeight, Color.DARKRED);
                rageBar.setTranslateX(healthBarData.healthBorder.getTranslateX());
                rageBar.setTranslateY(healthBarData.healthBorder.getTranslateY());
                rageBar.setVisible(false); // Initially hidden
                rageBar.setMouseTransparent(true); // Make non-interactive so clicks pass through
                
                burningRageBars.add(new BurningRageBarData(slot, rageBar, 
                    healthBarData.healthBorder.getTranslateX(), healthBarData.healthBorder.getTranslateY()));
            }
        }
    }
    
    /**
     * Check if a character has the Burning Rage talent
     */
    private boolean hasBurningRage(Observer.characterSlot slot) {
        return slot.getCharacter().getUniqueValue("Burning rage") != null;
    }
    
    private void createHealthText() {
        // Create text elements for all health bars
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            
            // Create HP text
            Text hpText = new Text("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            hpText.setFont(new Font(16));
            hpText.setFill(slot == battleSystem.getHeroSlot() || slot == battleSystem.getHeroSlot2() ? Color.WHITE : Color.BLACK);
            hpText.setTranslateX(healthBarData.healthBorder.getTranslateX() + 4);
            hpText.setTranslateY(healthBarData.healthBorder.getTranslateY() + healthBarHeight - 5);
            healthBarData.hpText = hpText;
            
            // Create MP text (only for heroes)
            if (healthBarData.mpBorder != null) {
                Text mpText = new Text("MP: " + (int) slot.getCurrentMp() + " / " + (int) slot.getCharacter().getMp());
                mpText.setFont(new Font(12));
                mpText.setFill(Color.WHITE);
                mpText.setTranslateX(healthBarData.mpBorder.getTranslateX() + 4);
                mpText.setTranslateY(healthBarData.mpBorder.getTranslateY() + 8);
                healthBarData.mpText = mpText;
            }
        }
    }
    
    private void createNameText() {
        // Create name text for all health bars
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            
            Text nameText = new Text(slot.getCharacter().getName());
            nameText.setFont(new Font(18));
            nameText.setFill(Color.BLACK);
            nameText.setTranslateX(healthBarData.healthBorder.getTranslateX() + 4);
            nameText.setTranslateY(healthBarData.healthBorder.getTranslateY() - 5);
            healthBarData.nameText = nameText;
        }
    }
    
    private void addUIElements() {
        // Add health bar elements to scene
        for (HealthBarData healthBarData : healthBars) {
            getGameScene().addUINode(healthBarData.healthBorder);
            getGameScene().addUINode(healthBarData.healthBar);
            
            if (healthBarData.mpBorder != null) {
                getGameScene().addUINode(healthBarData.mpBorder);
            }
            if (healthBarData.mpBar != null) {
                getGameScene().addUINode(healthBarData.mpBar);
            }
        }
        
        // Add Burning Rage bars (drawn on top of health bars but below text)
        for (BurningRageBarData rageBarData : burningRageBars) {
            getGameScene().addUINode(rageBarData.rageBar);
        }
        
        // Add text elements last so they appear on top of everything
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.hpText != null) {
                getGameScene().addUINode(healthBarData.hpText);
            }
            if (healthBarData.mpText != null) {
                getGameScene().addUINode(healthBarData.mpText);
            }
            if (healthBarData.nameText != null) {
                getGameScene().addUINode(healthBarData.nameText);
            }
        }
    }
    
    private void setupTargetSelection() {
        // Target selection on click
        Runnable highlightSelection = () -> {
            // Update highlighting for all health bars
            for (HealthBarData healthBarData : healthBars) {
                Observer.characterSlot slot = healthBarData.characterSlot;
                boolean isEnemy = (slot == battleSystem.getEnemySlot() || slot == battleSystem.getEnemySlot2());
                boolean isAlly = (slot == battleSystem.getHeroSlot() || slot == battleSystem.getHeroSlot2());
                
                if (isEnemy) {
                    boolean isSelected = battleSystem.getSelectedEnemyTarget() == slot;
                    healthBarData.healthBorder.setStroke(isSelected ? Color.GOLD : Color.BLACK);
                    healthBarData.healthBorder.setStrokeWidth(isSelected ? 3 : 2);
                } else if (isAlly) {
                    boolean isSelected = battleSystem.getSelectedAllyTarget() == slot;
                    healthBarData.healthBorder.setStroke(isSelected ? Color.GOLD : Color.BLACK);
                    healthBarData.healthBorder.setStrokeWidth(isSelected ? 3 : 2);
                }
            }
        };
        
        // Set up click handlers for all health bars
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            boolean isEnemy = (slot == battleSystem.getEnemySlot() || slot == battleSystem.getEnemySlot2());
            boolean isAlly = (slot == battleSystem.getHeroSlot() || slot == battleSystem.getHeroSlot2());
            
            // Set up click handlers for health bar, border, and HP text
            if (healthBarData.healthBar != null) {
                healthBarData.healthBar.setOnMouseClicked(e -> {
                    if (isEnemy) {
                        battleSystem.setSelectedEnemyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    } else if (isAlly) {
                        battleSystem.setSelectedAllyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    }
            highlightSelection.run(); 
        });
            }
            
            if (healthBarData.healthBorder != null) {
                healthBarData.healthBorder.setOnMouseClicked(e -> {
                    if (isEnemy) {
                        battleSystem.setSelectedEnemyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    } else if (isAlly) {
                        battleSystem.setSelectedAllyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    }
                highlightSelection.run(); 
            });
        }
            
            if (healthBarData.hpText != null) {
                healthBarData.hpText.setOnMouseClicked(e -> {
                    if (isEnemy) {
                        battleSystem.setSelectedEnemyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    } else if (isAlly) {
                        battleSystem.setSelectedAllyTarget(slot);
                        battleSystem.setSelectedTarget(slot); // Keep for backward compatibility
                    }
                highlightSelection.run(); 
            });
        }
        }
        
        highlightSelection.run();
    }
    
    public void renderHeroSkillsFor(Observer.characterSlot hero) {
        createAndRenderHeroSkillBoxes(hero);
    }
    
    private void createAndRenderHeroSkillBoxes(Observer.characterSlot hero) {
        // Remove existing boxes if already created
        if (skill1Box != null) {
            Object ud = skill1Box.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(skill1Box);
        }
        if (skill2Box != null) {
            Object ud = skill2Box.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(skill2Box);
        }
        if (skill3Box != null) {
            Object ud = skill3Box.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(skill3Box);
        }

        Ability.skill s1 = hero.getSkills().size() > 0 ? hero.getSkills().get(0) : null;
        Ability.skill s2 = hero.getSkills().size() > 1 ? hero.getSkills().get(1) : null;
        Ability.skill s3 = hero.getSkills().size() > 2 ? hero.getSkills().get(2) : null;
        skill1Box = createSkillBox(hero, s1, 50, 500, Color.LIGHTBLUE, null);
        skill2Box = createSkillBox(hero, s2, 100, 500, Color.CYAN, null);
        skill3Box = createSkillBox(hero, s3, 150, 500, Color.DARKBLUE, null);

        // Visually disable boxes if MP insufficient
        updateSkillAffordabilityVisual(skill1Box, hero, s1);
        updateSkillAffordabilityVisual(skill2Box, hero, s2);
        updateSkillAffordabilityVisual(skill3Box, hero, s3);
    }
    
    private Rectangle createSkillBox(Observer.characterSlot attacker, Ability.skill skill,
                                     double x, double y, Color color,
                                     Observer.characterSlot target) {
        Rectangle box = new Rectangle(30, 30, color);
        box.setTranslateX(x);
        box.setTranslateY(y);

        String detailText;
        if (skill == null) {
            detailText = "not available";
        } else {
            detailText = skill.getName() + "\n" +
                    "Damage: " + (int)(attacker.getCharacter().getAtk() * skill.getAtkScale()) + "\n" +
                    "Push: " + (int)(attacker.getCharacter().getAV() * skill.getAVScale())+ "\n" +
                    "Mp Cost: " + (int)(skill.getMpCost())
            ;
        }
        Text skillDetail = new Text(detailText);
        skillDetail.setFont(new Font(14));
        skillDetail.setFill(Color.BLACK);
        skillDetail.setVisible(false);
        // Center on screen with wrapping and centered alignment
        double wrapWidth = 300;
        skillDetail.setWrappingWidth(wrapWidth);
        skillDetail.setTextAlignment(TextAlignment.CENTER);
        skillDetail.setTranslateX((getAppWidth() - wrapWidth) / 2.0);
        skillDetail.setTranslateY(getAppHeight() / 2.0 - 60);
        // Prevent flicker when moving mouse from box over the text
        skillDetail.setMouseTransparent(true);

        getGameScene().addUINode(skillDetail);

        // Show / hide when hovering
        box.setOnMouseEntered(e -> skillDetail.setVisible(true));
        box.setOnMouseExited(e -> skillDetail.setVisible(false));

        // On click -> use skill
        box.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !battleSystem.isMoving()) {
                if (skill == null) {
                    return; // not available
                }
                Observer.characterSlot resolvedTarget;
                if (skill.getTarget().equals("Self")) {
                    // For self-targeting skills, always target the attacker
                    resolvedTarget = attacker;
                } else if (skill.getTarget().equals("Ally")) {
                    // For ally-targeting skills, use the selected ally target
                    resolvedTarget = battleSystem.getSelectedAllyTarget();
                    if (resolvedTarget == null) {
                        // Fallback to attacker if no ally is selected
                        resolvedTarget = attacker;
                    }
                } else if (skill.getTarget().equals("Single Enemy")) {
                    // For enemy-targeting skills, use the selected enemy target
                    resolvedTarget = battleSystem.getSelectedEnemyTarget();
                    if (resolvedTarget == null) {
                        // Fallback to first enemy if no enemy is selected
                        resolvedTarget = battleSystem.getEnemySlot();
                    }
                } else if (skill.getAtkScale() < 0) {
                    resolvedTarget = attacker; // negative dmg goes to self
                } else {
                    // Default behavior - use the general selected target
                    resolvedTarget = target != null ? target : battleSystem.getSelectedTarget();
                }
                // Enforce affordability for heroes before calling useSkill
                if (attacker == battleSystem.getHeroSlot() || attacker == battleSystem.getHeroSlot2()) {
                    // Check MP affordability
                    if (attacker.getCurrentMp() < skill.getMpCost()) {
                        return; // Not enough MP
                    }
                    // Check Burning Rage affordability
                    if (!characters.SpecialTalents.hasEnoughBurningRage(attacker, skill.getBurningRageRequired())) {
                        return; // Not enough Burning Rage
                    }
                }
                battleSystem.setMoving(true);
                battleSystem.useSkill(attacker, resolvedTarget, skill);

            }
        });

        // Attach detail ref to the box so we can remove it later
        box.setUserData(skillDetail);

        getGameScene().addUINode(box);
        return box;
    }
    
    private void updateSkillAffordabilityVisual(Rectangle box, Observer.characterSlot hero, Ability.skill skill) {
        boolean available = skill != null;
        boolean hasEnoughMp = available && hero.getCurrentMp() >= skill.getMpCost();
        boolean hasEnoughRage = available && characters.SpecialTalents.hasEnoughBurningRage(hero, skill.getBurningRageRequired());
        boolean affordable = hasEnoughMp && hasEnoughRage;
        
        if (!available) {
            box.setOpacity(0.3);
            box.setStroke(Color.GRAY);
            box.setMouseTransparent(true); // Make completely unclickable for unavailable skills
        } else {
            box.setOpacity(affordable ? 1.0 : 0.4);
            box.setMouseTransparent(false); // Keep hover functionality for available skills
            // Use different colors to indicate what's missing
            if (!hasEnoughMp && !hasEnoughRage) {
                box.setStroke(Color.RED); // Missing both MP and Rage
            } else if (!hasEnoughMp) {
                box.setStroke(Color.BLUE); // Missing MP
            } else if (!hasEnoughRage) {
                box.setStroke(Color.ORANGE); // Missing Rage
            } else {
                box.setStroke(Color.BLACK); // Can afford
            }
        }
    }
    
    public void updateMpUI(Observer.characterSlot hero) {
        // Find the health bar data for this character
        HealthBarData healthBarData = findHealthBarData(hero);
        if (healthBarData == null || healthBarData.mpBar == null || healthBarData.mpText == null) return;
        
        double ratio = hero.getCurrentMp() / hero.getCharacter().getMp();
        healthBarData.mpBar.setWidth(healthBarWidth * Math.max(0, Math.min(1, ratio)));
        healthBarData.mpText.setText("MP: " + (int) hero.getCurrentMp() + " / " + (int) hero.getCharacter().getMp());
    }
    
    public void updateHealthUI(Observer.characterSlot slot) {
        // Find the health bar data for this character
        HealthBarData healthBarData = findHealthBarData(slot);
        if (healthBarData == null || healthBarData.healthBar == null || healthBarData.hpText == null) return;
        
        double ratio = slot.getCurrentHp() / slot.getCharacter().getHp();
        healthBarData.healthBar.setWidth(healthBarWidth * ratio);
        healthBarData.hpText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
    }
    
    /**
     * Finds the health bar data for a specific character slot
     * @param slot The character slot
     * @return The corresponding health bar data, or null if not found
     */
    private HealthBarData findHealthBarData(Observer.characterSlot slot) {
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.characterSlot == slot) {
                return healthBarData;
            }
        }
        return null;
    }
    
    public void hideAllCombatUI() {
        // Hide health bars and related UI
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.healthBorder != null) healthBarData.healthBorder.setVisible(false);
            if (healthBarData.healthBar != null) healthBarData.healthBar.setVisible(false);
            if (healthBarData.mpBorder != null) healthBarData.mpBorder.setVisible(false);
            if (healthBarData.mpBar != null) healthBarData.mpBar.setVisible(false);
            if (healthBarData.hpText != null) healthBarData.hpText.setVisible(false);
            if (healthBarData.mpText != null) healthBarData.mpText.setVisible(false);
            if (healthBarData.nameText != null) healthBarData.nameText.setVisible(false);
        }
        
        // Hide Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) rageBarData.rageBar.setVisible(false);
        }
        
        // Hide lines
        if (blueLine != null) blueLine.setVisible(false);
        if (greenLine != null) greenLine.setVisible(false);
        if (redLine != null) redLine.setVisible(false);
        if (yellowLine != null) yellowLine.setVisible(false);
        
        // Hide skill boxes
        if (skill1Box != null) skill1Box.setVisible(false);
        if (skill2Box != null) skill2Box.setVisible(false);
        if (skill3Box != null) skill3Box.setVisible(false);
    }
    
    public void clearAllBattleUI() {
        
        // Remove health bar elements from scene
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.healthBorder != null) {
                getGameScene().removeUINode(healthBarData.healthBorder);
            }
            if (healthBarData.healthBar != null) {
                getGameScene().removeUINode(healthBarData.healthBar);
            }
            if (healthBarData.mpBorder != null) {
                getGameScene().removeUINode(healthBarData.mpBorder);
            }
            if (healthBarData.mpBar != null) {
                getGameScene().removeUINode(healthBarData.mpBar);
            }
            if (healthBarData.hpText != null) {
                getGameScene().removeUINode(healthBarData.hpText);
            }
            if (healthBarData.mpText != null) {
                getGameScene().removeUINode(healthBarData.mpText);
            }
            if (healthBarData.nameText != null) {
                getGameScene().removeUINode(healthBarData.nameText);
            }
        }
        
        // Remove Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) {
                getGameScene().removeUINode(rageBarData.rageBar);
            }
        }
        
        // Remove lines
        if (blueLine != null) {
            getGameScene().removeUINode(blueLine);
        }
        if (greenLine != null) {
            getGameScene().removeUINode(greenLine);
        }
        if (redLine != null) {
            getGameScene().removeUINode(redLine);
        }
        if (yellowLine != null) {
            getGameScene().removeUINode(yellowLine);
        }
        
        // Remove skill boxes
        if (skill1Box != null) {
            getGameScene().removeUINode(skill1Box);
        }
        if (skill2Box != null) {
            getGameScene().removeUINode(skill2Box);
        }
        if (skill3Box != null) {
            getGameScene().removeUINode(skill3Box);
        }
        
        // Clear health bars and Burning Rage bars lists
        healthBars.clear();
        burningRageBars.clear();
        
        blueLine = null;
        greenLine = null;
        redLine = null;
        yellowLine = null;
        
        skill1Box = null;
        skill2Box = null;
        skill3Box = null;
        
    }

    public void showAllCombatUI() {
        // Show health bars and related UI
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.healthBorder != null) healthBarData.healthBorder.setVisible(true);
            if (healthBarData.healthBar != null) healthBarData.healthBar.setVisible(true);
            if (healthBarData.mpBorder != null) healthBarData.mpBorder.setVisible(true);
            if (healthBarData.mpBar != null) healthBarData.mpBar.setVisible(true);
            if (healthBarData.hpText != null) healthBarData.hpText.setVisible(true);
            if (healthBarData.mpText != null) healthBarData.mpText.setVisible(true);
            if (healthBarData.nameText != null) healthBarData.nameText.setVisible(true);
        }
        
        // Show Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) rageBarData.rageBar.setVisible(true);
        }
        
        // Show lines
        if (blueLine != null) blueLine.setVisible(true);
        if (greenLine != null) greenLine.setVisible(true);
        if (redLine != null) redLine.setVisible(true);
        if (yellowLine != null) yellowLine.setVisible(true);
        
        // Show skill boxes
        if (skill1Box != null) skill1Box.setVisible(true);
        if (skill2Box != null) skill2Box.setVisible(true);
        if (skill3Box != null) skill3Box.setVisible(true);
    }
    
    // Getters for BattleSystem
    public Line getBlueLine() { return blueLine; }
    public Line getGreenLine() { return greenLine; }
    public Line getRedLine() { return redLine; }
    public Line getYellowLine() { return yellowLine; }
    public double getBarX() { return barX; }
    public double getBarWidth() { return barWidth; }
    
    // Setters for line management
    public void setBlueLine(Line blueLine) { this.blueLine = blueLine; }
    public void setGreenLine(Line greenLine) { this.greenLine = greenLine; }
    public void setRedLine(Line redLine) { this.redLine = redLine; }
    public void setYellowLine(Line yellowLine) { this.yellowLine = yellowLine; }
    
    /**
     * Updates the Burning Rage bar for a character
     * @param slot The character slot to update
     */
    public void updateBurningRageBar(Observer.characterSlot slot) {
        if (slot == null) return;
        
        // Find the rage bar data for this character
        BurningRageBarData rageBarData = findRageBarData(slot);
        if (rageBarData == null) return;
        
        // Get the Burning Rage value
        float burningRage = slot.getCharacter().getUniqueValueAsFloat("Burning rage");
        if (burningRage <= 0) {
            // Hide the rage bar if no rage
            rageBarData.rageBar.setVisible(false);
            return;
        }
        
        // Calculate rage bar properties based on health + rage vs max HP
        float currentHp = slot.getCurrentHp();
        float maxHp = slot.getCharacter().getHp();
        float totalHp = currentHp + burningRage;
        
        double rageBarWidth = (burningRage / maxHp) * healthBarWidth;
        double rageBarX;
        
        if (totalHp <= maxHp) {
            // When health + rage <= max HP: draw rage bar from right end of current health bar
            double currentHpWidth = (currentHp / maxHp) * healthBarWidth;
            rageBarX = currentHpWidth; // Start after current health
        } else {
            // When health + rage > max HP: draw rage bar from right to left (current behavior)
            rageBarX = healthBarWidth - rageBarWidth; // Start from right edge
        }
        
        // Update the rage bar
        rageBarData.rageBar.setWidth(rageBarWidth);
        rageBarData.rageBar.setTranslateX(rageBarData.baseX + rageBarX);
        rageBarData.rageBar.setVisible(true);
    }
    
    /**
     * Finds the rage bar data for a specific character slot
     * @param slot The character slot
     * @return The corresponding rage bar data, or null if not found
     */
    private BurningRageBarData findRageBarData(Observer.characterSlot slot) {
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.characterSlot == slot) {
                return rageBarData;
            }
        }
        return null;
    }
}
