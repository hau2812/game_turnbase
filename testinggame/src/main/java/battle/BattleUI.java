package battle;

import abilities.Ability;
import audio.AudioManager;
import characters.Observer;
import com.almasb.fxgl.app.GameApplication;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ui.SimpleLine;
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
     * Helper class to hold Barrier bar data
     */
    private static class BarrierBarData {
        public Observer.characterSlot characterSlot;
        public Rectangle barrierBar;
        public double baseX;
        public double baseY;
        
        public BarrierBarData(Observer.characterSlot slot, Rectangle bar, double x, double y) {
            this.characterSlot = slot;
            this.barrierBar = bar;
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
    private Line purpleLine;
    private Line redLine;
    private Line yellowLine;
    private Line orangeLine;
    
    // Health bars - using list-based approach
    private java.util.List<HealthBarData> healthBars = new java.util.ArrayList<>();
    
    // Burning Rage bars (red bars that show accumulated rage)
    private java.util.List<BurningRageBarData> burningRageBars = new java.util.ArrayList<>();
    
    // Barrier bars (light blue bars that show barrier points)
    private java.util.List<BarrierBarData> barrierBars = new java.util.ArrayList<>();
    
    // Skill boxes
    private Rectangle skill1Box;
    private Rectangle skill2Box;
    private Rectangle skill3Box;
    private Rectangle skill4Box;
    
    // Layout constants
    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;
    private double healthBarWidth = 200;
    private double healthBarHeight = 20;
    
    // Battle system reference
    private BattleSystem battleSystem;
    // Hide talents text setting (Burning Rage bars still show)
    boolean hideTalents = true;
    // Audio system
    private AudioManager audioManager;
    
    public BattleUI(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;
        this.audioManager = AudioManager.getInstance();
    }
    
    // ===================== HELPER FUNCTIONS =====================
    
    /**
     * Check if a character slot is a hero
     */
    private boolean isHero(Observer.characterSlot slot) {
        return slot == battleSystem.getHeroSlot() || 
               slot == battleSystem.getHeroSlot2() || 
               slot == battleSystem.getHeroSlot3();
    }
    
    /**
     * Check if a character slot is an enemy
     */
    private boolean isEnemy(Observer.characterSlot slot) {
        return slot == battleSystem.getEnemySlot() || 
               slot == battleSystem.getEnemySlot2() || 
               slot == battleSystem.getEnemySlot3();
    }
    
    /**
     * Get all hero slots as an array
     */
    private Observer.characterSlot[] getAllHeroes() {
        return new Observer.characterSlot[]{
            battleSystem.getHeroSlot(), 
            battleSystem.getHeroSlot2(), 
            battleSystem.getHeroSlot3()
        };
    }
    
    /**
     * Get all enemy slots as an array
     */
    private Observer.characterSlot[] getAllEnemies() {
        return new Observer.characterSlot[]{
            battleSystem.getEnemySlot(), 
            battleSystem.getEnemySlot2(), 
            battleSystem.getEnemySlot3()
        };
    }
    
    /**
     * Get all character slots as an array
     */
    private Observer.characterSlot[] getAllCharacters() {
        return new Observer.characterSlot[]{
            battleSystem.getHeroSlot(), 
            battleSystem.getHeroSlot2(), 
            battleSystem.getHeroSlot3(),
            battleSystem.getEnemySlot(), 
            battleSystem.getEnemySlot2(), 
            battleSystem.getEnemySlot3()
        };
    }
    
    /**
     * Line data structure for creating lines
     */
    private static class LineData {
        public Observer.characterSlot character;
        public Line line;
        public Color color;
        public double position;
        
        public LineData(Observer.characterSlot character, Line line, Color color, double position) {
            this.character = character;
            this.line = line;
            this.color = color;
            this.position = position;
        }
    }
    
    /**
     * Create a single line for a character
     */
    private Line createSingleLine(Observer.characterSlot character, Color color, double position) {
        if (character == null || character.getCurrentHp() <= 0) {
            return null;
        }
        
        Line line = new Line();
        line.setStroke(color);
        line.setStrokeWidth(3);
        line.setStartX(barX + barWidth / position);
        line.setEndX(barX + barWidth / position);
        line.setStartY(barY);
        line.setEndY(barY + barHeight);
        
        return line;
    }
    
    /**
     * Get all line data for all characters
     */
    private LineData[] getAllLineData() {
        return new LineData[]{
            new LineData(battleSystem.getHeroSlot(), blueLine, Color.BLUE, 1.75),
            new LineData(battleSystem.getHeroSlot2(), greenLine, Color.LIMEGREEN, 1.9),
            new LineData(battleSystem.getHeroSlot3(), purpleLine, Color.PURPLE, 2.1),
            new LineData(battleSystem.getEnemySlot(), redLine, Color.RED, 1.5),
            new LineData(battleSystem.getEnemySlot2(), yellowLine, Color.YELLOW, 1.33),
            new LineData(battleSystem.getEnemySlot3(), orangeLine, Color.ORANGE, 1.2)
        };
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
        
        // Update all Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            updateBarrierBar(barrierBarData.characterSlot);
        }
    }
    
    /**
     * Refresh HP and MP UI for all character slots
     * This method updates all characters' health and MP bars in one call
     */
    public void refreshAllCharacterUI() {
        // Update health and MP bars for all characters
        Observer.characterSlot[] allCharacters = getAllCharacters();
        for (Observer.characterSlot character : allCharacters) {
            if (character != null) {
                updateHealthUI(character);
                if (isHero(character)) {
                    updateMpUI(character);
                }
            }
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
        // Create all lines using helper function
        blueLine = createSingleLine(battleSystem.getHeroSlot(), Color.BLUE, 1.75);
        greenLine = createSingleLine(battleSystem.getHeroSlot2(), Color.LIMEGREEN, 1.9);
        purpleLine = createSingleLine(battleSystem.getHeroSlot3(), Color.PURPLE, 2.1);
        redLine = createSingleLine(battleSystem.getEnemySlot(), Color.RED, 1.5);
        yellowLine = createSingleLine(battleSystem.getEnemySlot2(), Color.YELLOW, 1.33);
        orangeLine = createSingleLine(battleSystem.getEnemySlot3(), Color.ORANGE, 1.2);

        // Add lines to scene - only if they were created
        Line[] allLines = {blueLine, greenLine, purpleLine, redLine, yellowLine, orangeLine};
        for (Line line : allLines) {
            if (line != null) {
                getGameScene().addUINode(line);
            }
        }

        // Set lines to character slots - only if characters exist and have HP > 0
        Observer.characterSlot[] allCharacters = getAllCharacters();
        Line[] allLineRefs = {blueLine, greenLine, purpleLine, redLine, yellowLine, orangeLine};
        
        for (int i = 0; i < allCharacters.length; i++) {
            Observer.characterSlot character = allCharacters[i];
            Line line = allLineRefs[i];
            if (character != null && character.getCurrentHp() > 0 && line != null) {
                character.setLine(SimpleLine.fromFxLine(line));
            }
        }
    }
    
    private void createHealthBars() {
        // Clear existing health bars
        healthBars.clear();
        
        // Define character slots and their positions/colors
        Observer.characterSlot[] allSlots = getAllCharacters();
        
        // Position data: [healthX, healthY, mpX, mpY]
        double[][] positions = {
                {50, 300, 50, 325},   // Hero (blue)
                {50, 375, 50, 400},   // Hero2 (green)
                {50, 450, 50, 475},   // Hero3 (purple)
                {550, 300, 550, 325}, // Enemy (red)
                {550, 375, 550, 375}, // Enemy2 (yellow)
                {550, 450, 550, 425}  // Enemy3 (orange)
        };
        
        // Health bar colors
        Color[] healthColors = {Color.BLUE, Color.LIMEGREEN, Color.PURPLE, Color.RED, Color.YELLOW, Color.ORANGE};
        Color[] mpColors = {Color.DODGERBLUE, Color.MEDIUMSEAGREEN, Color.MEDIUMPURPLE, Color.DODGERBLUE, Color.DODGERBLUE, Color.DODGERBLUE};
        
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
            if (i < 3) { // Only heroes have MP (Hero1, Hero2, Hero3)
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
            
            // Add hover functionality to show debuffs (for both heroes and enemies)
            addDebuffHoverToHealthBar(healthBar, healthBorder, slot);
        }
        
        // Create Burning Rage bars (red bars that show accumulated rage)
        createBurningRageBars();
        
        // Create Barrier bars (light blue bars that show barrier points)
        createBarrierBars();
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
    
    private void createBarrierBars() {
        // Clear existing barrier bars
        barrierBars.clear();
        
        // Create barrier bars for all characters (they will be shown/hidden based on barrier effects)
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            
            // Create barrier bar (light blue, 1/5 height of health bar)
            Rectangle barrierBar = new Rectangle(0, healthBarHeight / 2, Color.LIGHTSEAGREEN);
            barrierBar.setTranslateX(healthBarData.healthBorder.getTranslateX());
            barrierBar.setTranslateY(healthBarData.healthBorder.getTranslateY()); // Position at top of health bar
            barrierBar.setVisible(false); // Initially hidden
            barrierBar.setMouseTransparent(true); // Make non-interactive so clicks pass through
            
            barrierBars.add(new BarrierBarData(slot, barrierBar, 
                healthBarData.healthBorder.getTranslateX(), healthBarData.healthBorder.getTranslateY()));
        }
    }
    
    /**
     * Check if a character has the Burning Rage talent
     */
    private boolean hasBurningRage(Observer.characterSlot slot) {
        return slot.getCharacter().getUniqueValue("Burning rage") != null;
    }
    
    /**
     * Check if a character has an active barrier effect
     */
    private boolean hasBarrier(Observer.characterSlot slot) {
        if (slot.getActiveEffects() == null) return false;
        
        for (characters.BuffDebuff effect : slot.getActiveEffects()) {
            if ("BARRIER".equals(effect.getEffects()) && effect.getStack() > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add hover functionality to health bar to show debuff information
     */
    private void addDebuffHoverToHealthBar(Rectangle healthBar, Rectangle healthBorder, Observer.characterSlot slot) {
        // Create debuff detail text (similar to skill tooltip)
        Text debuffDetail = new Text();
        debuffDetail.setFont(new Font(14));
        debuffDetail.setFill(Color.BLACK);
        debuffDetail.setVisible(false);
        
        // Center on screen with wrapping and centered alignment (same as skill tooltip)
        double wrapWidth = 300;
        debuffDetail.setWrappingWidth(wrapWidth);
        debuffDetail.setTextAlignment(TextAlignment.CENTER);
        debuffDetail.setTranslateX((getAppWidth() - wrapWidth) / 2.0);
        debuffDetail.setTranslateY(getAppHeight() / 2.0 - 60);
        // Prevent flicker when moving mouse from bar over the text
        debuffDetail.setMouseTransparent(true);
        
        getGameScene().addUINode(debuffDetail);
        
        // Show / hide when hovering over health bar or border
        healthBar.setOnMouseEntered(e -> {
            String debuffText = generateDebuffText(slot);
            debuffDetail.setText(debuffText);
            debuffDetail.setVisible(true);
        });
        healthBar.setOnMouseExited(e -> debuffDetail.setVisible(false));
        
        healthBorder.setOnMouseEntered(e -> {
            String debuffText = generateDebuffText(slot);
            debuffDetail.setText(debuffText);
            debuffDetail.setVisible(true);
        });
        healthBorder.setOnMouseExited(e -> debuffDetail.setVisible(false));
        
        // Attach detail ref to the health bar so we can remove it later
        healthBar.setUserData(debuffDetail);
    }
    
    /**
     * Generate debuff text for a character (shows both uniqueValue effects and BuffDebuff effects)
     */
    private String generateDebuffText(Observer.characterSlot slot) {
        StringBuilder debuffText = new StringBuilder();
        //debuffText.append(slot.getCharacter().getName()).append(" - Active Effects:\n\n");        
        
        boolean hasUniqueEffects = false;
        boolean hasBuffDebuffEffects = false;
        
        // === UNIQUE VALUE EFFECTS (Special Talents) ===
        if (!hideTalents) {
            
            debuffText.append("Special Talents:\n");
            
            // Show Burning Rage if present
            if (slot.getCharacter().getUniqueValue("Burning rage") != null) {
                float currentRage = characters.SpecialTalents.getCurrentBurningRage(slot);
                if (currentRage > 0) {
                    debuffText.append("🔥 Burning Rage: ").append((int)currentRage).append("\n");
                    hasUniqueEffects = true;
                }
            }
            
            // Show Regeneration if present
            if (slot.getCharacter().getUniqueValue("Regeneration") != null) {
                float regenAmount = slot.getCharacter().getUniqueValueAsFloat("Regeneration");
                if (regenAmount > 0) {
                    debuffText.append("💚 Regeneration: +").append((int)regenAmount).append(" HP/turn\n");
                    hasUniqueEffects = true;
                }
            }
            
            // Show MP Regeneration if present
            if (slot.getCharacter().getUniqueValue("MpRegeneration") != null) {
                float mpRegenAmount = slot.getCharacter().getUniqueValueAsFloat("MpRegeneration");
                if (mpRegenAmount > 0) {
                    debuffText.append("💙 MP Regeneration: +").append((int)mpRegenAmount).append(" MP/turn\n");
                    hasUniqueEffects = true;
                }
            }
            
            // Show Mana Shield if present
            if (slot.getCharacter().getUniqueValue("MANA_SHIELD") != null) {
                float shieldAmount = slot.getCharacter().getUniqueValueAsFloat("MANA_SHIELD");
                if (shieldAmount > 0) {
                    debuffText.append("🛡️ Mana Shield: Active\n");
                    hasUniqueEffects = true;
                }
            }
            
            // Show Berserker Rage if present
            if (slot.getCharacter().getUniqueValue("BerserkerRage") != null) {
                float berserkerRage = slot.getCharacter().getUniqueValueAsFloat("BerserkerRage");
                if (berserkerRage > 0) {
                    debuffText.append("⚔️ Berserker Rage: +").append((int)berserkerRage).append(" ATK\n");
                    hasUniqueEffects = true;
                }
            }
            
            if (!hasUniqueEffects) {
                debuffText.append("None\n");
            }
        }
        
        // === BUFF/DEBUFF EFFECTS ===
        debuffText.append("\nBuff/Debuff Effects:\n");
        
        if (slot.getActiveEffects() != null && !slot.getActiveEffects().isEmpty()) {
            for (characters.BuffDebuff effect : slot.getActiveEffects()) {
                String effectIcon = getEffectIcon(effect.getType(), effect.getEffects());
                String effectDescription = getEffectDescription(effect);
                debuffText.append(effectIcon).append(" ").append(effect.getName())
                         .append(" (").append(effect.getDuration()).append(" turns)")
                         .append(": ").append(effectDescription).append("\n");
                hasBuffDebuffEffects = true;
            }
        }
        
        if (!hasBuffDebuffEffects) {
            debuffText.append("None");
        }
        
        return debuffText.toString();
    }
    
    /**
     * Get appropriate emoji icon for effect type and effect
     */
    private String getEffectIcon(String type, String effects) {
        if ("Buff".equals(type)) {
            switch (effects) {
                case "ATK": return "⚔️";
                case "DEF": return "🛡️";
                case "SPD": return "💨";
                case "HOT": return "💚";
                case "MP_COST": return "💙";
                case "BARRIER": return "🔰";
                default: return "✨";
            }
        } else if ("Debuff".equals(type)) {
            switch (effects) {
                case "ATK": return "💔";
                case "DEF": return "🩸";
                case "SPD": return "🐌";
                case "DOT": return "☠️";
                default: return "💀";
            }
        }
        return "❓";
    }
    
    /**
     * Get description for effect based on its properties using total value (value * stack)
     */
    private String getEffectDescription(characters.BuffDebuff effect) {
        String effects = effect.getEffects();
        float totalValue = effect.getTotalValue(); // This combines value and stack
        int stack = effect.getStack();
        
        switch (effects) {
            case "ATK":
                if (totalValue > 1.0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue - 1.0f) * 100) + "% ATK" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "-" + (int)((1.0f - totalValue) * 100) + "% ATK" + stackInfo;
                }
            case "DEF":
                if (totalValue > 1.0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue - 1.0f) * 100) + "% DEF" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "-" + (int)((1.0f - totalValue) * 100) + "% DEF" + stackInfo;
                }
            case "SPD":
                if (totalValue > 1.0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue - 1.0f) * 100) + "% SPD" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "-" + (int)((1.0f - totalValue) * 100) + "% SPD" + stackInfo;
                }
            case "DOT":
                String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                return totalValue + " damage/turn" + stackInfo;
            case "HOT":
                String hotStackInfo = stack > 1 ? " (x" + stack + ")" : "";
                return "+" + totalValue + " HP/turn" + hotStackInfo;
            case "MP_COST":
                String mpStackInfo = stack > 1 ? " (x" + stack + ")" : "";
                return "-" + (int)((1.0f - totalValue) * 100) + "% MP cost" + mpStackInfo;
            case "BARRIER":
                String barrierStackInfo = stack > 1 ? " (x" + stack + ")" : "";
                return stack + " barrier points" + barrierStackInfo;
            default:
                String defaultStackInfo = stack > 1 ? " (x" + stack + ")" : "";
                return "Effect: " + effects + " (" + totalValue + ")" + defaultStackInfo;
        }
    }
    
    private void createHealthText() {
        // Create text elements for all health bars
        for (HealthBarData healthBarData : healthBars) {
            Observer.characterSlot slot = healthBarData.characterSlot;
            
            // Create HP text
            Text hpText = new Text("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            hpText.setFont(new Font(16));
            hpText.setFill(isHero(slot) ? Color.WHITE : Color.BLACK);
            hpText.setTranslateX(healthBarData.healthBorder.getTranslateX() + 4);
            hpText.setTranslateY(healthBarData.healthBorder.getTranslateY() + healthBarHeight - 5);
            hpText.setMouseTransparent(true); // Allow mouse events to pass through to health bar
            healthBarData.hpText = hpText;
            
            // Create MP text (only for heroes)
            if (healthBarData.mpBorder != null) {
                Text mpText = new Text("MP: " + (int) slot.getCurrentMp() + " / " + (int) slot.getCharacter().getMp());
                mpText.setFont(new Font(12));
                mpText.setFill(Color.WHITE);
                mpText.setTranslateX(healthBarData.mpBorder.getTranslateX() + 4);
                mpText.setTranslateY(healthBarData.mpBorder.getTranslateY() + 8);
                mpText.setMouseTransparent(true); // Allow mouse events to pass through
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
            nameText.setMouseTransparent(true); // Allow mouse events to pass through
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
        
        // Add Barrier bars (drawn on top of health bars but below text)
        for (BarrierBarData barrierBarData : barrierBars) {
            getGameScene().addUINode(barrierBarData.barrierBar);
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
                boolean isEnemy = isEnemy(slot);
                boolean isAlly = isHero(slot);
                
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
            boolean isEnemy = isEnemy(slot);
            boolean isAlly = isHero(slot);
            
            // Set up click handlers for health bar, border, and HP text
            if (healthBarData.healthBar != null) {
                healthBarData.healthBar.setOnMouseClicked(e -> {
                    audioManager.playButtonClick();
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
                    audioManager.playButtonClick();
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
        if (skill4Box != null) {
            Object ud = skill4Box.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(skill4Box);
        }


        Ability.skill s1 = hero.getSkills().size() > 0 ? hero.getSkills().get(0) : Ability.SkillRegistry.getByName("N/A");
        Ability.skill s2 = hero.getSkills().size() > 1 ? hero.getSkills().get(1) : Ability.SkillRegistry.getByName("N/A");
        Ability.skill s3 = hero.getSkills().size() > 2 ? hero.getSkills().get(2) : Ability.SkillRegistry.getByName("N/A");
        Ability.skill s4 = hero.getSkills().size() > 3 ? hero.getSkills().get(3) : Ability.SkillRegistry.getByName("N/A");
        skill1Box = createSkillBox(hero, s1, 50, 500, Color.LIGHTBLUE, null);
        skill2Box = createSkillBox(hero, s2, 100, 500, Color.CYAN, null);
        skill3Box = createSkillBox(hero, s3, 150, 500, Color.DARKBLUE, null);
        skill4Box = createSkillBox(hero, s4, 200, 500, Color.PURPLE, null);

        // Visually disable boxes if MP insufficient
        updateSkillAffordabilityVisual(skill1Box, hero, s1);
        updateSkillAffordabilityVisual(skill2Box, hero, s2);
        updateSkillAffordabilityVisual(skill3Box, hero, s3);
        updateSkillAffordabilityVisual(skill4Box, hero, s4);
    }
    
    private Rectangle createSkillBox(Observer.characterSlot attacker, Ability.skill skill,
                                     double x, double y, Color color,
                                     Observer.characterSlot target) {
        Rectangle box = new Rectangle(30, 30, color);
        box.setTranslateX(x);
        box.setTranslateY(y);

        String detailText;
        if (skill == null || skill.getName().equals("N/A")) {
            detailText = "No skill assigned";
        } else {
            detailText = skill.getName() + "\n" +
                    "Damage: " + (int)(attacker.getCharacter().getAtk() * skill.getAtkScale()) + "\n" +
                    "Push: " + (int)(attacker.getCharacter().getAV() * skill.getAVScale())+ "\n";
            
            // Show Rage Required if skill has rage cost, otherwise show MP Cost
            if (skill.getBurningRageRequired() > 0) {
                detailText += "Rage Required: " + (int)(skill.getBurningRageRequired());
            } else {
                detailText += "Mp Cost: " + (int)(skill.getMpCost());
            }
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
            if (e.getButton() == MouseButton.PRIMARY && !battleSystem.isMoving() && !battleSystem.isEnemyActionTime()) {
                if (skill == null || skill.getName().equals("N/A")) {
                    return; // not available or N/A skill
                }
                // Play skill selection sound
                audioManager.playButtonClick();
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
                if (isHero(attacker)) {
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
        boolean available = skill != null && !skill.getName().equals("N/A");
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
        
        // Hide Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.barrierBar != null) barrierBarData.barrierBar.setVisible(false);
        }
        
        // Hide lines
        Line[] allLines = {blueLine, greenLine, purpleLine, redLine, yellowLine, orangeLine};
        for (Line line : allLines) {
            if (line != null) line.setVisible(false);
        }
        
        // Hide skill boxes
        if (skill1Box != null) skill1Box.setVisible(false);
        if (skill2Box != null) skill2Box.setVisible(false);
        if (skill3Box != null) skill3Box.setVisible(false);
        if (skill4Box != null) skill4Box.setVisible(false);
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
            
            // Remove debuff tooltip if it exists
            if (healthBarData.healthBar != null) {
                Object userData = healthBarData.healthBar.getUserData();
                if (userData instanceof Text) {
                    getGameScene().removeUINode((Text) userData);
                }
            }
        }
        
        // Remove Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) {
                getGameScene().removeUINode(rageBarData.rageBar);
            }
        }
        
        // Remove Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.barrierBar != null) {
                getGameScene().removeUINode(barrierBarData.barrierBar);
            }
        }
        
        // Remove lines

        if (blueLine != null) {
            getGameScene().removeUINode(blueLine);
        }
        if (greenLine != null) {
            getGameScene().removeUINode(greenLine);
        }
        if (purpleLine != null) {
            getGameScene().removeUINode(purpleLine);
        }
        if (redLine != null) {
            getGameScene().removeUINode(redLine);
        }
        if (yellowLine != null) {
            getGameScene().removeUINode(yellowLine);
        }
        if (orangeLine != null) {
            getGameScene().removeUINode(orangeLine);
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
        if (skill4Box != null) {
            getGameScene().removeUINode(skill4Box);
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
        skill4Box = null;
        
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
        
        // Show Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.barrierBar != null) barrierBarData.barrierBar.setVisible(true);
        }
        
        // Show lines
        Line[] allLines = {blueLine, greenLine, purpleLine, redLine, yellowLine, orangeLine};
        for (Line line : allLines) {
            if (line != null) line.setVisible(true);
        }
        
        // Show skill boxes
        if (skill1Box != null) skill1Box.setVisible(true);
        if (skill2Box != null) skill2Box.setVisible(true);
        if (skill3Box != null) skill3Box.setVisible(true);
        if (skill4Box != null) skill4Box.setVisible(true);
    }
    
    // Getters for BattleSystem
    public Line getBlueLine() { return blueLine; }
    public Line getGreenLine() { return greenLine; }
    public Line getPurpleLine() { return purpleLine; }
    public Line getRedLine() { return redLine; }
    public Line getYellowLine() { return yellowLine; }
    public Line getOrangeLine() { return orangeLine; }
    public double getBarX() { return barX; }
    public double getBarWidth() { return barWidth; }
    
    // Setters for line management
    public void setBlueLine(Line blueLine) { this.blueLine = blueLine; }
    public void setGreenLine(Line greenLine) { this.greenLine = greenLine; }
    public void setPurpleLine(Line purpleLine) { this.purpleLine = purpleLine; }
    public void setRedLine(Line redLine) { this.redLine = redLine; }
    public void setYellowLine(Line yellowLine) { this.yellowLine = yellowLine; }
    public void setOrangeLine(Line orangeLine) { this.orangeLine = orangeLine; }
    
    /**
     * Set the line for a character slot to null
     */
    public void setLineForCharacter(Observer.characterSlot slot, Line line) {
        if (slot == null) return;
        
        // Get the battle system to access character slots
        if (battleSystem == null) return;
        
        if (slot == battleSystem.getHeroSlot()) {
            setBlueLine(line);
        } else if (slot == battleSystem.getHeroSlot2()) {
            setGreenLine(line);
        } else if (slot == battleSystem.getHeroSlot3()) {
            setPurpleLine(line);
        } else if (slot == battleSystem.getEnemySlot()) {
            setRedLine(line);
        } else if (slot == battleSystem.getEnemySlot2()) {
            setYellowLine(line);
        } else if (slot == battleSystem.getEnemySlot3()) {
            setOrangeLine(line);
        }
    }
    
    public void setHideTalents(boolean hideTalents) {
        this.hideTalents = hideTalents;
    }
    
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
    
    /**
     * Updates the Barrier bar for a character
     * @param slot The character slot to update
     */
    public void updateBarrierBar(Observer.characterSlot slot) {
        if (slot == null) return;
        
        // Find the barrier bar data for this character
        BarrierBarData barrierBarData = findBarrierBarData(slot);
        if (barrierBarData == null) return;
        
        // Get the barrier effect
        characters.BuffDebuff barrierEffect = null;
        if (slot.getActiveEffects() != null) {
            for (characters.BuffDebuff effect : slot.getActiveEffects()) {
                if ("BARRIER".equals(effect.getEffects()) && effect.getStack() > 0) {
                    barrierEffect = effect;
                    break;
                }
            }
        }
        
        if (barrierEffect == null || barrierEffect.getStack() <= 0) {
            // Hide the barrier bar if no barrier
            barrierBarData.barrierBar.setVisible(false);
            return;
        }
        
        // Show the barrier bar
        barrierBarData.barrierBar.setVisible(true);
        
        // Calculate barrier bar length based on barrier amount relative to max HP
        float maxHp = slot.getCharacter().getHp();
        float barrierAmount = barrierEffect.getStack();
        float barrierRatio = Math.min(1.0f, barrierAmount / maxHp); // Cap at 100% of max HP
        
    // Set barrier bar width
    double barrierBarWidth = healthBarWidth * barrierRatio;
    barrierBarData.barrierBar.setWidth(barrierBarWidth);
    
    // Position the barrier bar at the left edge of the health bar (draw from left to right)
    double healthBarX = barrierBarData.baseX;
    barrierBarData.barrierBar.setTranslateX(healthBarX);
    }
    
    /**
     * Finds the barrier bar data for a specific character slot
     * @param slot The character slot
     * @return The corresponding barrier bar data, or null if not found
     */
    private BarrierBarData findBarrierBarData(Observer.characterSlot slot) {
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.characterSlot == slot) {
                return barrierBarData;
            }
        }
        return null;
    }
}
