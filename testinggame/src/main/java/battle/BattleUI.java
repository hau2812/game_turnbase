package battle;

import abilities.Ability;
import audio.AudioManager;
import characters.Observer;
import com.almasb.fxgl.app.GameApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ui.SimpleLine;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import items.ConsumableItem;
import items.Inventory;
import javafx.util.Duration;

import java.util.Objects;

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
        public ImageView idleSprite; // Sprite animation for idle animation
        public Timeline spriteAnimation; // Animation timeline for sprite
        public ImageView skillSprite; // Sprite for skill animation (temporary)
        public Timeline skillAnimation; // Animation timeline for skill sprite
        public ImageView enemySprite; // Enemy sprite (mirrored, for skill animations)
        public Timeline enemyIdleAnimation; // Enemy idle animation
        public double originalSpriteX; // Original X position of sprite
        public double originalSpriteY; // Original Y position of sprite
        public double baseX;
        public double baseY;
        public ImageView slashEffectSprite;
        public Timeline slashEffectTimeline;
        public ImageView igniteEffectSprite;
        public Timeline igniteEffectTimeline;
        public Polygon turnIndicator; // Green triangle to indicate current turn
        
        public HealthBarData(Observer.characterSlot slot, Rectangle healthBar, Rectangle healthBorder, 
                           Rectangle mpBar, Rectangle mpBorder, Text hpText, Text mpText, Text nameText, 
                           ImageView idleSprite, Timeline spriteAnimation, double x, double y) {
            this.characterSlot = slot;
            this.healthBar = healthBar;
            this.healthBorder = healthBorder;
            this.mpBar = mpBar;
            this.mpBorder = mpBorder;
            this.hpText = hpText;
            this.mpText = mpText;
            this.nameText = nameText;
            this.idleSprite = idleSprite;
            this.spriteAnimation = spriteAnimation;
            this.skillSprite = null;
            this.skillAnimation = null;
            this.enemySprite = null;
            this.enemyIdleAnimation = null;
            this.originalSpriteX = 0;
            this.originalSpriteY = 0;
            this.baseX = x;
            this.baseY = y;
            this.slashEffectSprite = null;
            this.slashEffectTimeline = null;
            this.igniteEffectSprite = null;
            this.igniteEffectTimeline = null;
            this.turnIndicator = null;
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
    
    // TimeStop bar (white bar that shows time stop duration)
    private Rectangle timeStopBar;
    private double timeStopBarMaxWidth;
    
    // Party MP bar (vertical bar on the left side)
    private Rectangle partyMpBar;
    private Rectangle partyMpBorder;
    private Text partyMpText;
    private double partyMpBarWidth = 20;
    private double partyMpBarHeight = 200;
    private double partyMpBarX = 10;
    private double partyMpBarY = 80;
    
    // Skill boxes
    private Rectangle skill1Box;
    private Rectangle skill2Box;
    private Rectangle skill3Box;
    private Rectangle skill4Box;
    
    // Item buttons
    private Rectangle item1Button;
    private Rectangle item2Button;
    private Rectangle item3Button;

    // Layout constants
    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;
    private double healthBarWidth = 200;
    private double healthBarHeight = 20;
    
    // Magic projectile animation constants
    private double magicProjectileWidth = 300;  // ADJUST THIS to change projectile size
    private double magicProjectileHeight = 300; // ADJUST THIS to change projectile size
    
    // Battle system reference
    private BattleSystem battleSystem;
    // Inventory system reference
    private Inventory inventory;
    // Hide talents text setting (Burning Rage bars still show)
    boolean hideTalents = true;
    // Audio system
    private AudioManager audioManager;
    
    public BattleUI(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;
        this.audioManager = AudioManager.getInstance();
    }
    
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // ===================== HELPER FUNCTIONS =====================
    
    /**
     * Normalizes a character name for sprite file lookup
     * - Converts to lowercase
     * - Replaces spaces with underscores
     * - Removes level information like "(lv.2)" or "(Lv.2)"
     * @param characterName The character name to normalize
     * @return Normalized name suitable for sprite filename
     */
    private String normalizeCharacterNameForSprite(String characterName) {
        if (characterName == null || characterName.isEmpty()) {
            return characterName;
        }
        
        String normalized = characterName.toLowerCase();
        
        // Remove level information patterns like "(lv.2)", "(Lv.2)", "(lv 2)", etc.
        normalized = normalized.replaceAll("\\(\\s*lv\\.?\\s*\\d+\\s*\\)", "");
        
        // Replace spaces with underscores
        normalized = normalized.replaceAll("\\s+", "_");
        
        // Remove any remaining parentheses or special characters that might cause issues
        normalized = normalized.replaceAll("[()]", "");
        
        // Trim any extra underscores
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.replaceAll("^_|_$", "");
        
        return normalized;
    }
    
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
        createPartyMpBar();
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
    
    private void createPartyMpBar() {
        // Create party MP border
        partyMpBorder = new Rectangle(partyMpBarWidth, partyMpBarHeight, Color.TRANSPARENT);
        partyMpBorder.setStroke(Color.BLACK);
        partyMpBorder.setStrokeWidth(2);
        partyMpBorder.setTranslateX(partyMpBarX);
        partyMpBorder.setTranslateY(partyMpBarY);
        
        // Create party MP bar
        partyMpBar = new Rectangle(partyMpBarWidth, partyMpBarHeight, Color.DARKBLUE);
        partyMpBar.setTranslateX(partyMpBarX);
        partyMpBar.setTranslateY(partyMpBarY);
        
        // Create party MP text
        partyMpText = new Text("Party MP: 0");
        partyMpText.setFont(new Font(12));
        partyMpText.setFill(Color.BLACK);
        partyMpText.setTranslateX(partyMpBarX - 5);
        partyMpText.setTranslateY(partyMpBarY - 20);
        partyMpText.setMouseTransparent(true);
        
        // Add to scene
        getGameScene().addUINode(partyMpBorder);
        getGameScene().addUINode(partyMpBar);
        getGameScene().addUINode(partyMpText);
        
        // Update with current party MP value
        updatePartyMpBar();
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
                {50, 375+25, 50, 400+25},   // Hero2 (green)
                {50, 450+50, 50, 475+50},   // Hero3 (purple)
                {550, 300, 550, 325}, // Enemy (red)
                {550, 375+25, 550, 375+25}, // Enemy2 (yellow)
                {550, 450+50, 550, 425+50}  // Enemy3 (orange)
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
            
            // Create idle sprite animation (for heroes and enemies)
            ImageView idleSprite = null;
            Timeline spriteAnimation = null;
            ImageView enemySprite = null;
            Timeline enemyIdleAnimation = null;
            
            if (i < 3) { // Heroes (Hero1, Hero2, Hero3)
                idleSprite = createIdleSprite(slot, healthX, healthY);
                if (idleSprite != null) {
                    spriteAnimation = createSpriteAnimation(idleSprite, slot);
                }
            } else { // Enemies (Enemy1, Enemy2, Enemy3)
                // Create enemy sprite (mirrored)
                enemySprite = createEnemySprite(slot, healthX, healthY);
                if (enemySprite != null) {
                    // Add to scene and make visible
                    getGameScene().addUINode(enemySprite);
                    enemySprite.setVisible(true);
                    // Create and start idle animation
                    enemyIdleAnimation = createSpriteAnimation(enemySprite, slot);
                    if (enemyIdleAnimation != null) {
                        enemyIdleAnimation.play();
                    }
                }
            }

            // Add to health bars list
            HealthBarData healthBarData = new HealthBarData(slot, healthBar, healthBorder, 
                mpBar, mpBorder, hpText, mpText, nameText, idleSprite, spriteAnimation, healthX, healthY);
            
            // Set enemy sprite and animation if created
            if (enemySprite != null) {
                healthBarData.enemySprite = enemySprite;
                healthBarData.enemyIdleAnimation = enemyIdleAnimation;
            }
            
            healthBars.add(healthBarData);

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
        debuffDetail.setTranslateY(getAppHeight() / 6.0);
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
                if (totalValue > 0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue) * 100) + "% ATK" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return (int)((totalValue) * 100) + "% ATK" + stackInfo;
                }
            case "DEF":
                if (totalValue > 0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue) * 100) + "% DEF" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return (int)((totalValue) * 100) + "% DEF" + stackInfo;
                }
            case "SPD":
                if (totalValue > 0f) {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return "+" + (int)((totalValue) * 100) + "% SPD" + stackInfo;
                } else {
                    String stackInfo = stack > 1 ? " (x" + stack + ")" : "";
                    return (int)((totalValue) * 100) + "% SPD" + stackInfo;
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
    
    /**
     * Creates an idle sprite ImageView for a hero character if the sprite file exists
     * @param slot The character slot
     * @param healthX The X position of the health bar
     * @param healthY The Y position of the health bar
     * @return ImageView with the sprite, or null if sprite file doesn't exist
     */
    private ImageView createIdleSprite(Observer.characterSlot slot, double healthX, double healthY) {
        if (slot == null || slot.getCharacter() == null) {
            return null;
        }

        String characterName = slot.getCharacter().getName();
        if (characterName == null || characterName.isEmpty()) {
            return null;
        }

        // Construct sprite filename: {characterName}_idle.png (case-insensitive matching)
        // FXGL automatically prepends /assets/textures/ to image paths
        // So "sprites/flamita_idle.png" becomes "/assets/textures/sprites/flamita_idle.png"
        String normalizedName = normalizeCharacterNameForSprite(characterName);
        String spriteFileName = normalizedName + "_idle.png";
        String spritePath = "sprites/" + spriteFileName;

        try {
            // Try to load the sprite image
            // Note: File should be located at: src/main/resources/assets/textures/sprites/{name}_idle.png
            Image spriteImage = getAssetLoader().loadImage(spritePath);

            // Immediately check if image is null or has error
            if (spriteImage == null || spriteImage.isError()) {
                return null;
            }

            // Check image URL - if it doesn't contain our expected path, it's likely an error placeholder
            String imageUrl = spriteImage.getUrl();
            // Check if URL contains normalized name (handles URL encoding and variations)
            if (imageUrl == null || (!imageUrl.contains(normalizedName) && !imageUrl.contains(spriteFileName))) {
                // URL doesn't match expected file - likely an error placeholder
                return null;
            }

            // Create ImageView but keep it invisible initially
            ImageView spriteView = new ImageView(spriteImage);
            spriteView.setVisible(false); // Start invisible until we verify it's valid

            // Add error listener to ensure it stays hidden if error occurs
            spriteImage.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    spriteView.setVisible(false);
                    // Remove from scene if it was added
                    try {
                        getGameScene().removeUINode(spriteView);
                    } catch (Exception e) {
                        // Already removed or not added yet
                    }
                }
            });

            // Wait for image to finish loading, then validate
            if (spriteImage.isBackgroundLoading()) {
                // Image is still loading - add listener to check when done
                spriteImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        // Image finished loading - check if valid
                        if (spriteImage.isError() || spriteImage.getWidth() <= 0 ||
                            !spriteImage.getUrl().contains(spriteFileName.toLowerCase())) {
                            spriteView.setVisible(false);
                        } else {
                            spriteView.setVisible(true);
                        }
                    }
                });
            } else {
                // Image already loaded - validate now
                double width = spriteImage.getWidth();
                double height = spriteImage.getHeight();
                if (width > 0 && height > 0 && !Double.isNaN(width) && !Double.isNaN(height) &&
                    !spriteImage.isError() && (imageUrl.contains(normalizedName) || imageUrl.contains(spriteFileName))) {
                    spriteView.setVisible(true);
                } else {
                    return null; // Invalid image, don't return the view
                }
            }

            // Set sprite size - ADJUST THESE VALUES TO MAKE SPRITE BIGGER/SMALLER
            // Current: 128x128 (2x the original 64x64 size)
            double spriteDisplayWidth = 100;  // ADJUST THIS to change sprite width
            double spriteDisplayHeight = 100; // ADJUST THIS to change sprite height
            spriteView.setFitWidth(spriteDisplayWidth);
            spriteView.setFitHeight(spriteDisplayHeight);
            spriteView.setPreserveRatio(true);

            // Position sprite to the right of the HP bar
            // NOTE: ADJUST THESE POSITION VALUES AS NEEDED
            // spriteX: horizontal position (to the right of health bar)
            // spriteY: vertical position (relative to health bar)
            double spriteX = healthX + healthBarWidth + 10; // 10 pixels to the right of health bar - ADJUST THIS
            double spriteY = healthY - 30; // Slightly above the health bar - ADJUST THIS

            spriteView.setTranslateX(spriteX);
            spriteView.setTranslateY(spriteY);
            spriteView.setMouseTransparent(true); // Allow mouse events to pass through

            // Store original position for skill animations
            HealthBarData healthBarData = findHealthBarData(slot);
            if (healthBarData != null) {
                healthBarData.originalSpriteX = spriteX;
                healthBarData.originalSpriteY = spriteY;
            }

            return spriteView;
        } catch (Exception e) {
            // Sprite file doesn't exist or couldn't be loaded - silently return null (no error image shown)
            return null;
        }
    }

    /**
     * Creates an animation timeline for a sprite sheet (64x64 pixels per frame)
     * @param spriteView The ImageView containing the sprite sheet
     * @param slot The character slot (for reference)
     * @return Timeline animation, or null if sprite is invalid
     */
    private Timeline createSpriteAnimation(ImageView spriteView, Observer.characterSlot slot) {
        if (spriteView == null || spriteView.getImage() == null) {
            return null;
        }

        Image spriteImage = spriteView.getImage();
        double imageWidth = spriteImage.getWidth();
        double imageHeight = spriteImage.getHeight();

        // Calculate number of frames (assuming 64x64 pixels per frame)
        int frameWidth = 64;
        int frameHeight = 64;
        int framesPerRow = (int) (imageWidth / frameWidth);
        int totalFrames = framesPerRow; // Assuming single row sprite sheet

        if (totalFrames <= 0) {
            return null;
        }

        // Set initial viewport to first frame
        spriteView.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameWidth, frameHeight));

        // Create animation timeline
        Timeline animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);

        // Animation speed: change frame every 0.15 seconds (adjust as needed)
        Duration frameDuration = Duration.millis(150);

        // Create keyframes for each frame
        for (int i = 0; i < totalFrames; i++) {
            final int frameIndex = i;
            KeyFrame keyFrame = new KeyFrame(
                frameDuration.multiply(i),
                e -> {
                    // Set viewport to show the current frame
                    double viewportX = frameIndex * frameWidth;
                    spriteView.setViewport(new javafx.geometry.Rectangle2D(
                        viewportX, 0, frameWidth, frameHeight
                    ));
                }
            );
            animation.getKeyFrames().add(keyFrame);
        }

        // Start the animation
        animation.play();

        return animation;
    }

    /**
     * Creates a skill animation sprite for a character
     * @param characterName The character name (e.g., "Flamita")
     * @param skillNumber The skill number (1, 2, 3, or 4)
     * @return ImageView with the skill sprite, or null if not found
     */
    private ImageView createSkillSprite(String characterName, int skillNumber) {
        if (characterName == null || characterName.isEmpty()) {
            return null;
        }

        // Construct sprite filename: {characterName}_skill_{skillNumber}.png
        String normalizedName = normalizeCharacterNameForSprite(characterName);
        String spriteFileName = normalizedName + "_skill_" + skillNumber + ".png";
        String spritePath = "sprites/" + spriteFileName;

        try {
            Image skillImage = getAssetLoader().loadImage(spritePath);

            if (skillImage == null || skillImage.isError()) {
                return null;
            }

            String imageUrl = skillImage.getUrl();
            // Check if URL contains normalized name (handles URL encoding and variations)
            if (imageUrl == null || (!imageUrl.contains(normalizedName) && !imageUrl.contains(spriteFileName))) {
                return null;
            }

            ImageView skillView = new ImageView(skillImage);
            skillView.setVisible(false);

            // Set sprite size (same as idle sprite)
            double spriteDisplayWidth = 100;
            double spriteDisplayHeight = 100;
            skillView.setFitWidth(spriteDisplayWidth);
            skillView.setFitHeight(spriteDisplayHeight);
            skillView.setPreserveRatio(true);
            skillView.setMouseTransparent(true);

            // Wait for image to finish loading
            if (skillImage.isBackgroundLoading()) {
                skillImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        if (!skillImage.isError() && skillImage.getWidth() > 0) {
                            skillView.setVisible(true);
                        }
                    }
                });
            } else {
                if (skillImage.getWidth() > 0 && !skillImage.isError()) {
                    skillView.setVisible(true);
                } else {
                    return null;
                }
            }

            return skillView;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates an animation timeline for a skill sprite sheet
     * @param spriteView The ImageView containing the skill sprite sheet
     * @param frameWidth Width of each frame (default 64)
     * @param frameHeight Height of each frame (default 64)
     * @return Timeline animation, or null if invalid
     */
    private Timeline createSkillSpriteAnimation(ImageView spriteView, int frameWidth, int frameHeight) {
        if (spriteView == null || spriteView.getImage() == null) {
            return null;
        }

        Image spriteImage = spriteView.getImage();
        double imageWidth = spriteImage.getWidth();
        double imageHeight = spriteImage.getHeight();

        int framesPerRow = (int) (imageWidth / frameWidth);
        int totalFrames = framesPerRow;

        if (totalFrames <= 0) {
            return null;
        }

        spriteView.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameWidth, frameHeight));

        Timeline animation = new Timeline();
        animation.setCycleCount(1); // Play once, not loop

        Duration frameDuration = Duration.millis(150);

        for (int i = 0; i < totalFrames; i++) {
            final int frameIndex = i;
            KeyFrame keyFrame = new KeyFrame(
                frameDuration.multiply(i),
                e -> {
                    double viewportX = frameIndex * frameWidth;
                    spriteView.setViewport(new javafx.geometry.Rectangle2D(
                        viewportX, 0, frameWidth, frameHeight
                    ));
                }
            );
            animation.getKeyFrames().add(keyFrame);
        }

        return animation;
    }

    /**
     * Creates an enemy sprite (mirrored) for skill animations
     * @param enemySlot The enemy character slot
     * @param enemyHealthX The X position of the enemy HP bar
     * @param enemyHealthY The Y position of the enemy HP bar
     * @return ImageView with the enemy sprite, or null if not found
     */
    private ImageView createEnemySprite(Observer.characterSlot enemySlot, double enemyHealthX, double enemyHealthY) {
        if (enemySlot == null || enemySlot.getCharacter() == null) {
            return null;
        }

        String characterName = enemySlot.getCharacter().getName();
        if (characterName == null || characterName.isEmpty()) {
            return null;
        }

        // Try to load enemy idle sprite
        String normalizedName = normalizeCharacterNameForSprite(characterName);
        String spriteFileName = normalizedName + "_idle.png";
        String spritePath = "sprites/" + spriteFileName;

        try {
            Image spriteImage = getAssetLoader().loadImage(spritePath);

            if (spriteImage == null || spriteImage.isError()) {
                return null;
            }

            String imageUrl = spriteImage.getUrl();
            if (imageUrl == null || (!imageUrl.contains(spriteFileName) && !imageUrl.contains(normalizedName))) {
                return null;
            }

            ImageView spriteView = new ImageView(spriteImage);
            spriteView.setVisible(false);

           

            // Set sprite size
            double spriteDisplayWidth = 100;
            double spriteDisplayHeight = 100;
            spriteView.setFitWidth(spriteDisplayWidth);
            spriteView.setFitHeight(spriteDisplayHeight);
            spriteView.setPreserveRatio(true);

            // Position sprite to the LEFT of the enemy HP bar
            // NOTE: ADJUST THESE POSITION VALUES AS NEEDED
            double spriteX = enemyHealthX - spriteDisplayWidth - 10; // 10 pixels to the left of health bar - ADJUST THIS
            double spriteY = enemyHealthY - 30; // Slightly above the health bar - ADJUST THIS

            spriteView.setTranslateX(spriteX);
            spriteView.setTranslateY(spriteY);
            spriteView.setMouseTransparent(true);

            if (!spriteImage.isBackgroundLoading()) {
                if (spriteImage.getWidth() > 0 && !spriteImage.isError()) {
                    spriteView.setVisible(true);
                } else {
                    return null;
                }
            } else {
                spriteImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        if (!spriteImage.isError() && spriteImage.getWidth() > 0) {
                            spriteView.setVisible(true);
                        }
                    }
                });
            }

            return spriteView;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Plays a skill animation sequence:
     * 1. Move hero sprite to left of selected enemy (if shouldMove is true)
     * 2. Show enemy sprite (mirrored, if shouldMove is true)
     * 3. Play skill animation
     * 4. Return hero to original position (if shouldMove is true)
     * 5. Resume idle animation
     * @param hero The hero using the skill
     * @param targetEnemy The enemy target (can be null if shouldMove is false)
     * @param skillNumber The skill number (1-4)
     * @param shouldMove Whether the hero should move to the enemy position
     */
    private void playSkillAnimationSequence(Observer.characterSlot hero, Observer.characterSlot targetEnemy, int skillNumber, boolean shouldMove) {
        if (hero == null) {
            return;
        }

        HealthBarData heroData = findHealthBarData(hero);
        if (heroData == null || heroData.idleSprite == null) {
            return;
        }

        String characterName = hero.getCharacter().getName();
        if (characterName == null || characterName.isEmpty()) {
            return;
        }

        // Store original position if not already stored
        if (heroData.originalSpriteX == 0 && heroData.originalSpriteY == 0) {
            heroData.originalSpriteX = heroData.idleSprite.getTranslateX();
            heroData.originalSpriteY = heroData.idleSprite.getTranslateY();
        }

        // Pause idle animation
        if (heroData.spriteAnimation != null) {
            heroData.spriteAnimation.pause();
        }

        if (shouldMove && targetEnemy != null) {
            // Moving to enemy - show enemy sprite and move hero
            HealthBarData enemyData = findHealthBarData(targetEnemy);
            if (enemyData == null) {
                return;
            }

            // Calculate target position (left of enemy HP bar)
            double enemyHealthX = enemyData.baseX;
            double enemyHealthY = enemyData.baseY;
            double targetX = enemyHealthX - 100 - 20; // 20 pixels to the left of enemy HP bar - ADJUST THIS
            double targetY = enemyHealthY - 30; // Slightly above enemy HP bar - ADJUST THIS

            // Create and show enemy sprite (mirrored)
            if (enemyData.enemySprite == null) {
                enemyData.enemySprite = createEnemySprite(targetEnemy, enemyHealthX, enemyHealthY);
                if (enemyData.enemySprite != null) {
                    getGameScene().addUINode(enemyData.enemySprite);
                    // Create enemy idle animation
                    enemyData.enemyIdleAnimation = createSpriteAnimation(enemyData.enemySprite, targetEnemy);
                }
            } else {
                enemyData.enemySprite.setVisible(true);
                if (enemyData.enemyIdleAnimation != null) {
                    enemyData.enemyIdleAnimation.play();
                }
            }

            // Animate hero sprite moving to target position (1.5x faster: 300ms -> 200ms)
            javafx.animation.TranslateTransition moveToEnemy = new javafx.animation.TranslateTransition(Duration.millis(200), heroData.idleSprite);
            moveToEnemy.setToX(targetX); // Absolute position
            moveToEnemy.setToY(targetY); // Absolute position

            moveToEnemy.setOnFinished(e -> {
                playSkillAnimationAtPosition(heroData, characterName, skillNumber, true);
            });

            moveToEnemy.play();
        } else {
            // Not moving - play skill animation at current position
            playSkillAnimationAtPosition(heroData, characterName, skillNumber, false);
        }
    }
    
    /**
     * Plays a magic projectile animation that moves from hero to target
     * @param hero The hero using the magic skill
     * @param target The target enemy
     * @param skill The magic skill being used
     */
    private void playMagicProjectileAnimation(Observer.characterSlot hero, Observer.characterSlot target, Ability.skill skill) {
        if (hero == null || target == null || skill == null) {
            return;
        }
        
        HealthBarData heroData = findHealthBarData(hero);
        HealthBarData targetData = findHealthBarData(target);
        
        if (heroData == null || targetData == null) {
            return;
        }
        
        // Get hero position (where projectile spawns)
        double heroX = heroData.baseX + healthBarWidth + 10 + 50; // Center of hero sprite
        double heroY = heroData.baseY - 30 + 50; // Center of hero sprite
        
        // Get target position (where projectile moves to)
        double targetX = targetData.baseX - 30; // Left of enemy health bar (where hit effect appears)
        double targetY = targetData.baseY - 30 + 50; // Center of enemy sprite area
        
        // Load skill-specific projectile image
        String skillName = skill.getName();
        String projectilePath = "sprites/" + skillName + ".png";
        
        try {
            Image projectileImage = getAssetLoader().loadImage(projectilePath);
            
            // Check if image is valid (similar to other animations)
            if (projectileImage == null || projectileImage.isError()) {
                return; // Don't run projectile animation if image not found
            }
            
            // Check if image is a placeholder/error image (FXGL returns 66x66 placeholder when file not found)
            double imageWidth = projectileImage.getWidth();
            double imageHeight = projectileImage.getHeight();
            if (imageWidth <= 0 || imageHeight <= 0) {
                return;
            }
            
            // Check if it's a placeholder image (66x66 is FXGL's default error image size)
            if (imageWidth < 64 || imageHeight < 64 || (imageWidth == 66 && imageHeight == 66)) {
                return; // Don't use placeholder images
            }
            
            // Check animation type: null or "projectile" = move from hero to enemy, "spawn" = play at enemy location
            String animationType = skill.getAnimation();
            boolean isSpawnAnimation = "spawn".equalsIgnoreCase(animationType);
            
            // Wait for image to finish loading if it's loading in background
            if (projectileImage.isBackgroundLoading()) {
                projectileImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        if (!projectileImage.isError() && projectileImage.getWidth() > 0) {
                            // Verify it's not a placeholder (placeholders are usually 66x66 or smaller than 64x64)
                            double imgWidth = projectileImage.getWidth();
                            double imgHeight = projectileImage.getHeight();
                            if (imgWidth < 64 || imgHeight < 64 || (imgWidth == 66 && imgHeight == 66)) {
                                return;
                            }
                            // Image loaded successfully, proceed with animation
                            if (isSpawnAnimation) {
                                createAndPlaySpawnAnimation(projectileImage, targetData, targetX, targetY);
                            } else {
                                createAndPlayProjectile(projectileImage, heroData, targetData, heroX, heroY, targetX, targetY);
                            }
                        }
                    }
                });
                // Return early, animation will start when image loads
                return;
            } else {
                // Image already loaded, validation already done above
                // Proceed with animation based on type
                if (isSpawnAnimation) {
                    createAndPlaySpawnAnimation(projectileImage, targetData, targetX, targetY);
                } else {
                    createAndPlayProjectile(projectileImage, heroData, targetData, heroX, heroY, targetX, targetY);
                }
            }
        } catch (Exception e) {
            // If loading fails, don't run projectile animation
            return;
        }
    }
    
    /**
     * Helper method to create and play spawn animation at target location (no movement)
     */
    private void createAndPlaySpawnAnimation(Image projectileImage, HealthBarData targetData,
                                            double targetX, double targetY) {
        try {
            // Create projectile ImageView with sprite sheet support
            ImageView projectile = new ImageView(projectileImage);
            projectile.setFitWidth(magicProjectileWidth);
            projectile.setFitHeight(magicProjectileHeight);
            projectile.setPreserveRatio(false); // Use exact dimensions for sprite sheets
            projectile.setMouseTransparent(true);
            projectile.setVisible(true);
            
            // No rotation needed for spawn animation (plays at target location)
            
            // Set up sprite sheet viewport (64x64 frames)
            int frameWidth = 64;
            int frameHeight = 64;
            double imageWidth = projectileImage.getWidth();
            double imageHeight = projectileImage.getHeight();
            int framesPerRow = (int) (imageWidth / frameWidth);
            int totalFrames = framesPerRow;
            
            if (totalFrames <= 0) {
                // Not a valid sprite sheet, don't run animation
                return;
            }
            
            // Set initial viewport to first frame
            projectile.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameWidth, frameHeight));
            
            // Position directly at target location (centered)
            double spawnX = targetX - (magicProjectileWidth / 2.0);
            double spawnY = targetY - (magicProjectileHeight / 2.0);
            projectile.setTranslateX(spawnX);
            projectile.setTranslateY(spawnY);
            
            getGameScene().addUINode(projectile);
            projectile.toFront();
            
            // Create frame animation timeline
            Timeline frameAnimation = new Timeline();
            frameAnimation.setCycleCount(1); // Play once
            
            Duration frameDuration = Duration.millis(100); // Frame rate for animation
            int lastFrameIndex = totalFrames - 1;
            
            // Create keyframes for each frame
            for (int i = 0; i < totalFrames; i++) {
                final int frameIndex = i;
                KeyFrame keyFrame = new KeyFrame(
                    frameDuration.multiply(i),
                    e -> {
                        double viewportX = frameIndex * frameWidth;
                        projectile.setViewport(new javafx.geometry.Rectangle2D(
                            viewportX, 0, frameWidth, frameHeight
                        ));
                    }
                );
                frameAnimation.getKeyFrames().add(keyFrame);
            }
            
            // When frame animation finishes, remove the projectile
            frameAnimation.setOnFinished(e -> {
                getGameScene().removeUINode(projectile);
            });
            
            // Start animation (no movement, just frame animation)
            frameAnimation.play();
            
        } catch (Exception e) {
            // If animation setup fails, don't run animation
            return;
        }
    }
    
    /**
     * Helper method to create and play the projectile animation (moves from hero to target)
     */
    private void createAndPlayProjectile(Image projectileImage, HealthBarData heroData, HealthBarData targetData,
                                        double heroX, double heroY, double targetX, double targetY) {
        try {
            // Create projectile ImageView with sprite sheet support
            ImageView projectile = new ImageView(projectileImage);
            projectile.setFitWidth(magicProjectileWidth);
            projectile.setFitHeight(magicProjectileHeight);
            projectile.setPreserveRatio(false); // Use exact dimensions for sprite sheets
            projectile.setMouseTransparent(true);
            projectile.setVisible(true);
            
            // Calculate rotation angle based on direction (from hero to target)
            double deltaX = targetX - heroX;
            double deltaY = targetY - heroY;
            double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
            projectile.setRotate(angle);
            
            // Set up sprite sheet viewport (64x64 frames)
            int frameWidth = 64;
            int frameHeight = 64;
            double imageWidth = projectileImage.getWidth();
            double imageHeight = projectileImage.getHeight();
            int framesPerRow = (int) (imageWidth / frameWidth);
            int totalFrames = framesPerRow;
            
            if (totalFrames <= 0) {
                // Not a valid sprite sheet, don't run projectile animation
                return;
            }
            
            // Set initial viewport to first frame
            projectile.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameWidth, frameHeight));
            
            // Position at hero location (centered)
            double startX = heroX - (magicProjectileWidth / 2.0);
            double startY = heroY - (magicProjectileHeight / 2.0);
            projectile.setTranslateX(startX);
            projectile.setTranslateY(startY);
            
            getGameScene().addUINode(projectile);
            projectile.toFront();
            
            // Create frame animation timeline
            Timeline frameAnimation = new Timeline();
            frameAnimation.setCycleCount(1); // Play once
            
            Duration frameDuration = Duration.millis(100); // Frame rate for projectile animation
            int lastFrameIndex = totalFrames - 1;
            
            // Create keyframes for each frame
            for (int i = 0; i < totalFrames; i++) {
                final int frameIndex = i;
                KeyFrame keyFrame = new KeyFrame(
                    frameDuration.multiply(i),
                    e -> {
                        double viewportX = frameIndex * frameWidth;
                        projectile.setViewport(new javafx.geometry.Rectangle2D(
                            viewportX, 0, frameWidth, frameHeight
                        ));
                    }
                );
                frameAnimation.getKeyFrames().add(keyFrame);
            }
            
            // When frame animation finishes, keep showing last frame
            frameAnimation.setOnFinished(e -> {
                // Show last frame
                double lastFrameX = lastFrameIndex * frameWidth;
                projectile.setViewport(new javafx.geometry.Rectangle2D(
                    lastFrameX, 0, frameWidth, frameHeight
                ));
            });
            
            // Animate projectile moving to target (centered)
            double endX = targetX - (magicProjectileWidth / 2.0);
            double endY = targetY - (magicProjectileHeight / 2.0);
            
            javafx.animation.TranslateTransition moveProjectile = new javafx.animation.TranslateTransition(
                Duration.millis(500), projectile);
            moveProjectile.setToX(endX);
            moveProjectile.setToY(endY);
            
            moveProjectile.setOnFinished(e -> {
                // Remove projectile when it reaches target
                getGameScene().removeUINode(projectile);
            });
            
            // Start both animations simultaneously
            frameAnimation.play();
            moveProjectile.play();
            
        } catch (Exception e) {
            // If animation setup fails, don't run projectile animation
            return;
        }
    }

    /**
     * Plays the skill animation at the hero's current position
     * @param heroData The hero's health bar data
     * @param characterName The character's name
     * @param skillNumber The skill number (1-4)
     * @param shouldReturn Whether to return to original position after animation
     */
    private void playSkillAnimationAtPosition(HealthBarData heroData, String characterName, int skillNumber, boolean shouldReturn) {
        // Check if we need to create or update the skill sprite for this skill number
        boolean needNewSprite = false;

        if (heroData.skillSprite == null) {
            // No sprite exists, need to create one
            needNewSprite = true;
        } else {
            // Check if the current sprite image matches the requested skill number
            Image currentImage = heroData.skillSprite.getImage();
            if (currentImage != null) {
                String imageUrl = currentImage.getUrl();
                String normalizedName = normalizeCharacterNameForSprite(characterName);
                String expectedFileName = normalizedName + "_skill_" + skillNumber + ".png";
                if (imageUrl == null || !imageUrl.contains(expectedFileName.toLowerCase())) {
                    // Image doesn't match - need to create new sprite
                    needNewSprite = true;
                    // Remove old sprite from scene
                    getGameScene().removeUINode(heroData.skillSprite);
                    if (heroData.skillAnimation != null) {
                        heroData.skillAnimation.stop();
                        heroData.skillAnimation = null;
                    }
                    heroData.skillSprite = null;
                }
            } else {
                // No image loaded, need to create new sprite
                needNewSprite = true;
            }
        }

        // Create skill sprite if needed
        if (needNewSprite) {
            heroData.skillSprite = createSkillSprite(characterName, skillNumber);
            if (heroData.skillSprite != null) {
                getGameScene().addUINode(heroData.skillSprite);
                // Reset animation since we have a new sprite
                heroData.skillAnimation = null;
            }
        }

        // Always update skill sprite position to match hero's current position
        if (heroData.skillSprite != null) {
            heroData.skillSprite.setTranslateX(heroData.idleSprite.getTranslateX());
            heroData.skillSprite.setTranslateY(heroData.idleSprite.getTranslateY());
        }

        // Hide idle sprite, show skill sprite
        if (heroData.skillSprite != null) {
            heroData.idleSprite.setVisible(false);
            heroData.skillSprite.setVisible(true);

            // Create and play skill animation
            if (heroData.skillAnimation == null) {
                heroData.skillAnimation = createSkillSpriteAnimation(heroData.skillSprite, 64, 64);
            }

            if (heroData.skillAnimation != null) {
                // Stop any ongoing animation and reset it
                heroData.skillAnimation.stop();
                heroData.skillAnimation.setOnFinished(skillFinished -> {
                    if (shouldReturn) {
                        // Skill animation finished - return to original position
                        returnHeroToOriginalPosition(heroData);
                    } else {
                        // Just resume idle animation at current position
                        heroData.skillSprite.setVisible(false);
                        heroData.idleSprite.setVisible(true);
                        if (heroData.spriteAnimation != null) {
                            heroData.spriteAnimation.play();
                        }
                    }
                });
                // Reset viewport to first frame
                heroData.skillSprite.setViewport(new javafx.geometry.Rectangle2D(0, 0, 64, 64));
                heroData.skillAnimation.play();
            } else {
                // No skill animation, just wait a bit then return/resume
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(500));
                pause.setOnFinished(pauseFinished -> {
                    if (shouldReturn) {
                        returnHeroToOriginalPosition(heroData);
                    } else {
                        heroData.skillSprite.setVisible(false);
                        heroData.idleSprite.setVisible(true);
                        if (heroData.spriteAnimation != null) {
                            heroData.spriteAnimation.play();
                        }
                    }
                });
                pause.play();
            }
        } else {
            // No skill sprite, just wait then return/resume
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(500));
            pause.setOnFinished(pauseFinished -> {
                if (shouldReturn) {
                    returnHeroToOriginalPosition(heroData);
                } else {
                    if (heroData.spriteAnimation != null) {
                        heroData.spriteAnimation.play();
                    }
                }
            });
            pause.play();
        }
    }

    /**
     * Returns the hero sprite to its original position and resumes idle animation
     * @param heroData The hero's health bar data
     */
    private void returnHeroToOriginalPosition(HealthBarData heroData) {
        if (heroData == null) return;

        // Hide skill sprite, show idle sprite
        if (heroData.skillSprite != null) {
            heroData.skillSprite.setVisible(false);
        }
        if (heroData.idleSprite != null) {
            heroData.idleSprite.setVisible(true);
        }

        // Animate hero sprite returning to original position
        javafx.animation.TranslateTransition returnHome = new javafx.animation.TranslateTransition(
            Duration.millis(300), heroData.idleSprite);
        returnHome.setToX(heroData.originalSpriteX); // Return to original absolute position
        returnHome.setToY(heroData.originalSpriteY); // Return to original absolute position

        returnHome.setOnFinished(e -> {
            // Resume idle animation
            if (heroData.spriteAnimation != null) {
                heroData.spriteAnimation.play();
            }

            // Don't hide enemy sprites - they should stay visible throughout battle
            // Enemy sprites are now created during health bar rendering and stay visible
        });

        returnHome.play();
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
        
        // Add idle sprites (drawn before text so text appears on top)
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.idleSprite != null) {
                // Double-check that the sprite image is valid before adding
                Image spriteImage = healthBarData.idleSprite.getImage();
                if (spriteImage != null && !spriteImage.isError() &&
                    spriteImage.getWidth() > 0 && spriteImage.getHeight() > 0) {
                    // Only add if image is valid (not an error placeholder)
                    getGameScene().addUINode(healthBarData.idleSprite);
                } else {
                    // Image is invalid, remove it from the data structure
                    healthBarData.idleSprite = null;
                    if (healthBarData.spriteAnimation != null) {
                        healthBarData.spriteAnimation.stop();
                        healthBarData.spriteAnimation = null;
                    }
                }
            }
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
        updateTurnIndicator(hero);
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
        skill1Box = createSkillBox(hero, s1, 50, 550, Color.LIGHTBLUE, null);
        skill2Box = createSkillBox(hero, s2, 100, 550, Color.CYAN, null);
        skill3Box = createSkillBox(hero, s3, 150, 550, Color.DARKBLUE, null);
        skill4Box = createSkillBox(hero, s4, 200, 550, Color.PURPLE, null);
        // Create item buttons
        createItemButtons(hero);
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
            
            // Show costs
            if (skill.getBurningRageRequired() > 0) {
                detailText += "Rage Required: " + (int)(skill.getBurningRageRequired()) + "\n";
            }
            if (skill.getMpCost() > 0) {
                detailText += "MP Cost: " + (int)(skill.getMpCost()) + "\n";
            }
            if (skill.getPartyMpCost() > 0) {
                detailText += "Party MP Cost: " + (int)(skill.getPartyMpCost())+ "\n";
            }
            detailText+=skill.getDescription();
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
        skillDetail.setTranslateY(getAppHeight() / 6.0 );
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
                } else if (skill.getTarget().equals("Single Enemy")||skill.getTarget().equals("Enemy")) {
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
                    // Check Party MP affordability
                    if (battleSystem.getPartyMp() < skill.getPartyMpCost()) {
                        return; // Not enough Party MP
                    }
                    // Check Burning Rage affordability
                    if (!characters.SpecialTalents.hasEnoughBurningRage(attacker, skill.getBurningRageRequired())) {
                        return; // Not enough Burning Rage
                    }
                }
                battleSystem.setMoving(true);

                //Reduce Mp
                if(!Objects.equals(skill.getName(), "Ecarr Vertel")) {
                    attacker.regenerateMp(-skill.getMpCost());
                }
                //Reduce Party MP
                if (skill.getPartyMpCost() > 0) {
                    float currentPartyMp = battleSystem.getPartyMp();
                    battleSystem.setPartyMp(currentPartyMp - skill.getPartyMpCost());
                }
                // Push line back for any character
                if (isHero(attacker)) {
                    double push = attacker.getCharacter().getAV() * skill.getAVScale();
                    battleSystem.pushCharacterLine(attacker, push);
                }

                // Determine which skill number this is (1, 2, 3, or 4)
                int skillNumber = 0;
                if (attacker.getSkills() != null) {
                    for (int i = 0; i < attacker.getSkills().size() && i < 4; i++) {
                        if (attacker.getSkills().get(i) != null && attacker.getSkills().get(i).equals(skill)) {
                            skillNumber = i + 1; // 1-based index
                            break;
                        }
                    }
                }

                // Check if skill targets enemies (should move to enemy)
                String skillTarget = skill.getTarget();
                String skillType = skill.getType();
                boolean isEnemyTargeting = skillTarget != null && (
                    skillTarget.equals("Enemy") ||
                    skillTarget.equals("Single Enemy") ||
                    skillTarget.equals("Aoe enemy")
                );
                // Magic skills don't move - they play at current position
                boolean isMagicSkill = "Magic".equalsIgnoreCase(skillType);

                // Play skill animation for all skills (1-4)
                if (skillNumber > 0 && isHero(attacker)) {
                    if (isMagicSkill && isEnemyTargeting && isEnemy(resolvedTarget)) {
                        // Magic skill targeting enemy - play projectile animation
                        playMagicProjectileAnimation(attacker, resolvedTarget, skill);
                    } else if (isEnemyTargeting && isEnemy(resolvedTarget) && !isMagicSkill) {
                        // Physical skill targeting enemy - move to enemy and play animation
                        playSkillAnimationSequence(attacker, resolvedTarget, skillNumber, true);
                    } else {
                        // Non-enemy target or other cases - play animation at current position (no movement)
                        playSkillAnimationSequence(attacker, null, skillNumber, false);
                    }
                }

                if(skill.getTarget().equals("Aoe enemy")||skill.getTarget().equals("Aoe ally")) {
                    battleSystem.executeAoeSkill(attacker, skill);

                }else{
                    battleSystem.useSkill(attacker, resolvedTarget, skill);
                }
                //Recover Mp for Ina
                if(battleSystem.hasIna()){
                    Observer.characterSlot Ina = battleSystem.getSlotByName("Ina");
                    if(Ina.getBuffDebuffByName("Conserve")==null&&Ina.getCurrentHp()>0) {
                        Ina.regenerateMp(1);
                        updateMpUI(Ina);
                    }
                }


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
        boolean hasEnoughPartyMp = available && battleSystem.getPartyMp() >= skill.getPartyMpCost();
        boolean hasEnoughRage = available && characters.SpecialTalents.hasEnoughBurningRage(hero, skill.getBurningRageRequired());
        boolean affordable = hasEnoughMp && hasEnoughRage && hasEnoughPartyMp;
        
        if (!available) {
            box.setOpacity(0.3);
            box.setStroke(Color.GRAY);
            box.setMouseTransparent(true); // Make completely unclickable for unavailable skills
        } else {
            box.setOpacity(affordable ? 1.0 : 0.4);
            box.setMouseTransparent(false); // Make unclickable if not affordable
            // Use different colors to indicate what's missing
            if (!hasEnoughMp && !hasEnoughRage && !hasEnoughPartyMp) {
                box.setStroke(Color.RED); // Missing MP, Rage, and Party MP
            } else if (!hasEnoughMp && !hasEnoughPartyMp) {
                box.setStroke(Color.BLUE); // Missing both MP types
            } else if (!hasEnoughMp) {
                box.setStroke(Color.CYAN); // Missing individual MP
            } else if (!hasEnoughPartyMp) {
                box.setStroke(Color.PURPLE); // Missing Party MP
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
    
    /**
     * Updates the party MP bar display
     */
    public void updatePartyMpBar() {
        if (partyMpBar == null || partyMpText == null) return;
        
        float currentPartyMp = battleSystem.getPartyMp();
        
        // For now, we'll use a simple scaling - you can adjust this based on your needs
        // Assuming max party MP of 100 for visualization purposes
        float maxPartyMp = battleSystem.maxPartyMp;
        double ratio = Math.max(0, Math.min(1, currentPartyMp / maxPartyMp));
        
        // Update bar height (fills from bottom to top)
        double currentHeight = partyMpBarHeight * ratio;
        partyMpBar.setHeight(currentHeight);
        partyMpBar.setTranslateY(partyMpBarY + (partyMpBarHeight - currentHeight));
        
        // Update text
        partyMpText.setText("Party MP: " + (int) currentPartyMp);
        
        // Update skill affordability when party MP changes
        updateSkillAffordabilityForCurrentHero();
    }
    
    /**
     * Updates skill affordability for the current acting hero
     */
    private void updateSkillAffordabilityForCurrentHero() {
        Observer.characterSlot currentHero = battleSystem.getCurrentActingHero();
        if (currentHero != null) {
            // Re-render skills to update affordability
            renderHeroSkillsFor(currentHero);
        }
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
     * Updates the turn indicator (green triangle) above the current acting hero
     * @param currentHero The hero whose turn it is
     */
    private void updateTurnIndicator(Observer.characterSlot currentHero) {
        if (currentHero == null) {
            // Hide all turn indicators if no hero is acting
            for (HealthBarData healthBarData : healthBars) {
                if (healthBarData.turnIndicator != null) {
                    healthBarData.turnIndicator.setVisible(false);
                }
            }
            return;
        }
        
        // Only show indicator for heroes (not enemies)
        if (isEnemy(currentHero)) {
            return;
        }
        
        // Find the health bar data for the current hero
        HealthBarData heroData = findHealthBarData(currentHero);
        if (heroData == null) {
            return;
        }
        
        // Hide all turn indicators first
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.turnIndicator != null) {
                healthBarData.turnIndicator.setVisible(false);
            }
        }
        
        // Create or show the turn indicator for the current hero
        if (heroData.turnIndicator == null) {
            // Create a small green triangle pointing up
            double triangleSize = 15;
            double centerX = heroData.baseX + healthBarWidth / 2.0;
            double topY = heroData.baseY - triangleSize - 5; // Position above health bar
            
            // Triangle points: bottom point (pointing up), top left, top right
            Polygon triangle = new Polygon(
                centerX, topY + triangleSize,  // Bottom point (pointing up toward health bar)
                centerX - triangleSize / 2, topY,  // Top left
                centerX + triangleSize / 2, topY   // Top right
            );
            triangle.setFill(Color.LIMEGREEN);
            triangle.setStroke(Color.DARKGREEN);
            triangle.setStrokeWidth(1);
            
            heroData.turnIndicator = triangle;
            getGameScene().addUINode(triangle);
        }
        
        // Show the turn indicator
        heroData.turnIndicator.setVisible(true);
    }
    
    /**
     * Plays a short hit effect (slash or ignite) on the provided target based on skill type.
     * Animates through the sprite sheet frames (64x64 each).
     */
    public void playHitEffect(Observer.characterSlot target, Ability.skill skill) {
        if (target == null || skill == null) {
            return;
        }
        
        String skillType = skill.getType();
        boolean isPhysical = "Physical".equalsIgnoreCase(skillType);
        boolean isMagic = "Magic".equalsIgnoreCase(skillType);
        if (!isPhysical && !isMagic) {
            return;
        }
        
        HealthBarData targetData = findHealthBarData(target);
        if (targetData == null) {
            return;
        }
        
        String effectName = isPhysical ? "slash" : "ignite";
        ImageView effectSprite = getOrCreateHitEffectSprite(targetData, effectName);
        if (effectSprite == null || effectSprite.getImage() == null) {
            return;
        }
        
        // Position effect based on target type
        // Heroes: effect appears to the right of health bar (where sprite is)
        // Enemies: effect appears to the left of health bar
        double spriteCenterX;
        double spriteCenterY = targetData.baseY - 30 + 50; // 50 = half of 100px sprite height
        
        if (isEnemy(target)) {
            // Enemy target: position to the left of enemy health bar
            spriteCenterX = targetData.baseX - 30; // 50 = half of 100px sprite width, positioned to the left
        } else {
            // Hero target: position to the right of health bar (where sprite would be)
            spriteCenterX = targetData.baseX + healthBarWidth + 10 + 50; // 50 = half of 100px sprite width
        }
        
        // Position effect sprite centered on calculated position
        effectSprite.setTranslateX(spriteCenterX - (effectSprite.getFitWidth() / 2.0));
        effectSprite.setTranslateY(spriteCenterY - (effectSprite.getFitHeight() / 2.0));
        effectSprite.setOpacity(1.0);
        effectSprite.setVisible(true);
        effectSprite.toFront();
        
        // Stop any existing animation
        Timeline existingTimeline = isPhysical ? targetData.slashEffectTimeline : targetData.igniteEffectTimeline;
        if (existingTimeline != null) {
            existingTimeline.stop();
        }
        
        // Create sprite sheet animation (64x64 frames)
        Timeline effectTimeline = createHitEffectAnimation(effectSprite, 64, 64);
        if (effectTimeline == null) {
            effectSprite.setVisible(false);
            return;
        }
        
        effectTimeline.setOnFinished(e -> {
            effectSprite.setVisible(false);
        });
        effectTimeline.play();
        
        // Store timeline reference
        if (isPhysical) {
            targetData.slashEffectTimeline = effectTimeline;
        } else {
            targetData.igniteEffectTimeline = effectTimeline;
        }
    }
    
    /**
     * Creates an animation timeline for a hit effect sprite sheet
     * @param spriteView The ImageView containing the effect sprite sheet
     * @param frameWidth Width of each frame (64 pixels)
     * @param frameHeight Height of each frame (64 pixels)
     * @return Timeline animation, or null if invalid
     */
    private Timeline createHitEffectAnimation(ImageView spriteView, int frameWidth, int frameHeight) {
        if (spriteView == null || spriteView.getImage() == null) {
            return null;
        }

        Image spriteImage = spriteView.getImage();
        double imageWidth = spriteImage.getWidth();
        double imageHeight = spriteImage.getHeight();

        // Calculate number of frames (assuming horizontal sprite sheet)
        int framesPerRow = (int) (imageWidth / frameWidth);
        int totalFrames = framesPerRow;

        if (totalFrames <= 0) {
            return null;
        }

        // Set initial viewport to first frame
        spriteView.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameWidth, frameHeight));

        Timeline animation = new Timeline();
        animation.setCycleCount(1); // Play once, not loop

        // Faster frame rate for hit effects (50ms per frame - twice as fast)
        Duration frameDuration = Duration.millis(50);

        // Create keyframes for each frame
        for (int i = 0; i < totalFrames; i++) {
            final int frameIndex = i;
            KeyFrame keyFrame = new KeyFrame(
                frameDuration.multiply(i),
                e -> {
                    double viewportX = frameIndex * frameWidth;
                    spriteView.setViewport(new javafx.geometry.Rectangle2D(
                        viewportX, 0, frameWidth, frameHeight
                    ));
                }
            );
            animation.getKeyFrames().add(keyFrame);
        }

        return animation;
    }
    
    private ImageView getOrCreateHitEffectSprite(HealthBarData targetData, String effectName) {
        ImageView cached = "slash".equals(effectName) ? targetData.slashEffectSprite : targetData.igniteEffectSprite;
        if (cached != null) {
            return cached;
        }
        
        try {
            Image effectImage = getAssetLoader().loadImage("sprites/" + effectName + ".png");
            if (effectImage == null || effectImage.isError()) {
                return null;
            }
            
            ImageView sprite = new ImageView(effectImage);
            // Set size to match frame size (64x64) scaled up for visibility
            sprite.setFitWidth(128); // 2x scale for 64x64 frames
            sprite.setFitHeight(128);
            sprite.setPreserveRatio(false); // Use exact dimensions for sprite sheets
            sprite.setVisible(false);
            sprite.setMouseTransparent(true);
            
            // Wait for image to finish loading before setting viewport
            if (effectImage.isBackgroundLoading()) {
                effectImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        if (!effectImage.isError() && effectImage.getWidth() > 0) {
                            // Set initial viewport to first frame (64x64)
                            sprite.setViewport(new javafx.geometry.Rectangle2D(0, 0, 64, 64));
                        }
                    }
                });
            } else {
                // Image already loaded, set viewport immediately
                if (effectImage.getWidth() > 0 && !effectImage.isError()) {
                    sprite.setViewport(new javafx.geometry.Rectangle2D(0, 0, 64, 64));
                }
            }
            
            getGameScene().addUINode(sprite);
            
            if ("slash".equals(effectName)) {
                targetData.slashEffectSprite = sprite;
            } else {
                targetData.igniteEffectSprite = sprite;
            }
            return sprite;
        } catch (Exception e) {
            return null;
        }
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
            if (healthBarData.idleSprite != null) healthBarData.idleSprite.setVisible(false);
            if (healthBarData.skillSprite != null) healthBarData.skillSprite.setVisible(false);
            // Don't hide enemy sprites - they should stay visible throughout battle
            // if (healthBarData.enemySprite != null) healthBarData.enemySprite.setVisible(false);
            if (healthBarData.spriteAnimation != null) healthBarData.spriteAnimation.pause();
            if (healthBarData.skillAnimation != null) healthBarData.skillAnimation.pause();
            if (healthBarData.enemyIdleAnimation != null) healthBarData.enemyIdleAnimation.pause();
            if (healthBarData.slashEffectSprite != null) healthBarData.slashEffectSprite.setVisible(false);
            if (healthBarData.igniteEffectSprite != null) healthBarData.igniteEffectSprite.setVisible(false);
            if (healthBarData.slashEffectTimeline != null) healthBarData.slashEffectTimeline.stop();
            if (healthBarData.igniteEffectTimeline != null) healthBarData.igniteEffectTimeline.stop();
        }
        
        // Hide Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) rageBarData.rageBar.setVisible(false);
        }
        
        // Hide Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.barrierBar != null) barrierBarData.barrierBar.setVisible(false);
        }
        
        // Hide party MP bar
        if (partyMpBar != null) partyMpBar.setVisible(false);
        if (partyMpBorder != null) partyMpBorder.setVisible(false);
        if (partyMpText != null) partyMpText.setVisible(false);
        
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
            
            // Stop and remove sprite animations
            if (healthBarData.spriteAnimation != null) {
                healthBarData.spriteAnimation.stop();
            }
            if (healthBarData.skillAnimation != null) {
                healthBarData.skillAnimation.stop();
            }
            if (healthBarData.enemyIdleAnimation != null) {
                healthBarData.enemyIdleAnimation.stop();
            }
            if (healthBarData.idleSprite != null) {
                getGameScene().removeUINode(healthBarData.idleSprite);
            }
            if (healthBarData.skillSprite != null) {
                getGameScene().removeUINode(healthBarData.skillSprite);
            }
            if (healthBarData.enemySprite != null) {
                getGameScene().removeUINode(healthBarData.enemySprite);
            }
            if (healthBarData.slashEffectSprite != null) {
                getGameScene().removeUINode(healthBarData.slashEffectSprite);
                healthBarData.slashEffectSprite = null;
                healthBarData.slashEffectTimeline = null;
            }
            if (healthBarData.igniteEffectSprite != null) {
                getGameScene().removeUINode(healthBarData.igniteEffectSprite);
                healthBarData.igniteEffectSprite = null;
                healthBarData.igniteEffectTimeline = null;
            }
            if (healthBarData.turnIndicator != null) {
                getGameScene().removeUINode(healthBarData.turnIndicator);
                healthBarData.turnIndicator = null;
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
        
        // Remove turn indicators
        for (HealthBarData healthBarData : healthBars) {
            if (healthBarData.turnIndicator != null) {
                getGameScene().removeUINode(healthBarData.turnIndicator);
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
        
        // Remove party MP bar
        if (partyMpBar != null) {
            getGameScene().removeUINode(partyMpBar);
        }
        if (partyMpBorder != null) {
            getGameScene().removeUINode(partyMpBorder);
        }
        if (partyMpText != null) {
            getGameScene().removeUINode(partyMpText);
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
        
        // Remove item buttons
        if (item1Button != null) {
            Object ud = item1Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item1Button);
        }
        if (item2Button != null) {
            Object ud = item2Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item2Button);
        }
        if (item3Button != null) {
            Object ud = item3Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item3Button);
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
        
        item1Button = null;
        item2Button = null;
        item3Button = null;

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
            if (healthBarData.idleSprite != null) healthBarData.idleSprite.setVisible(true);
            if (healthBarData.spriteAnimation != null) healthBarData.spriteAnimation.play();
            // Note: skillSprite and enemySprite are shown/hidden during skill animations
        }
        
        // Show Burning Rage bars
        for (BurningRageBarData rageBarData : burningRageBars) {
            if (rageBarData.rageBar != null) rageBarData.rageBar.setVisible(true);
        }
        
        // Show Barrier bars
        for (BarrierBarData barrierBarData : barrierBars) {
            if (barrierBarData.barrierBar != null) barrierBarData.barrierBar.setVisible(true);
        }
        
        // Show party MP bar
        if (partyMpBar != null) partyMpBar.setVisible(true);
        if (partyMpBorder != null) partyMpBorder.setVisible(true);
        if (partyMpText != null) partyMpText.setVisible(true);
        
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
    
    /**
     * Creates the timeStop bar (white bar positioned on top of black bar, with character lines above it)
     */
    public void createTimeStopBar() {
        // Remove existing timeStop bar if it exists
        removeTimeStopBar();
        
        // Create white timeStop bar positioned on top of black bar
        timeStopBar = new Rectangle(barWidth, barHeight, Color.WHITE);
        timeStopBar.setTranslateX(barX);
        timeStopBar.setTranslateY(barY); // Same Y as black bar to cover it
        timeStopBarMaxWidth = barWidth;
        
        getGameScene().addUINode(timeStopBar);
        
        // Re-add character lines after timeStop bar to ensure they appear on top
        reAddCharacterLines();
    }
    
    /**
     * Updates the timeStop bar width based on remaining timeStop duration
     * @param remainingTimeStop The remaining timeStop duration
     */
    public void updateTimeStopBar(float remainingTimeStop) {
        if (timeStopBar == null) return;
        
        // Calculate current width based on remaining timeStop relative to max width
        float timeStopRatio = Math.max(0, remainingTimeStop / (float)barWidth);
        double currentWidth = timeStopBarMaxWidth * timeStopRatio;
        
        timeStopBar.setWidth(currentWidth);
    }
    
    /**
     * Removes the timeStop bar from the UI
     */
    public void removeTimeStopBar() {
        if (timeStopBar != null) {
            getGameScene().removeUINode(timeStopBar);
            timeStopBar = null;
        }
    }
    
    /**
     * Re-adds character lines to ensure they appear above the timeStop bar
     */
    private void reAddCharacterLines() {
        // Remove all existing lines first
        if (blueLine != null) getGameScene().removeUINode(blueLine);
        if (greenLine != null) getGameScene().removeUINode(greenLine);
        if (purpleLine != null) getGameScene().removeUINode(purpleLine);
        if (redLine != null) getGameScene().removeUINode(redLine);
        if (yellowLine != null) getGameScene().removeUINode(yellowLine);
        if (orangeLine != null) getGameScene().removeUINode(orangeLine);
        
        // Re-add all lines (they will appear on top of the timeStop bar)
        if (blueLine != null) getGameScene().addUINode(blueLine);
        if (greenLine != null) getGameScene().addUINode(greenLine);
        if (purpleLine != null) getGameScene().addUINode(purpleLine);
        if (redLine != null) getGameScene().addUINode(redLine);
        if (yellowLine != null) getGameScene().addUINode(yellowLine);
        if (orangeLine != null) getGameScene().addUINode(orangeLine);
    }

    /**
     * Create item buttons for battle consumables
     */
    private void createItemButtons(Observer.characterSlot hero) {
        // Remove existing item buttons
        if (item1Button != null) {
            Object ud = item1Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item1Button);
        }
        if (item2Button != null) {
            Object ud = item2Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item2Button);
        }
        if (item3Button != null) {
            Object ud = item3Button.getUserData();
            if (ud instanceof Text) getGameScene().removeUINode((Text) ud);
            getGameScene().removeUINode(item3Button);
        }

        // Create item buttons (positioned below skill buttons, with more spacing)
        item1Button = createItemButton(hero, 0, 50+300, 550, Color.ORANGE);
        item2Button = createItemButton(hero, 1, 110+300, 550, Color.ORANGE); // Increased spacing: 100->110
        item3Button = createItemButton(hero, 2, 170+300, 550, Color.ORANGE); // Increased spacing: 150->170
    }

    /**
     * Create a single item button
     */
    private Rectangle createItemButton(Observer.characterSlot hero, int itemIndex, double x, double y, Color color) {
        Rectangle button = new Rectangle(50, 35, color); // Increased size: width 40->50, height 30->35
        button.setStroke(Color.BLACK);
        button.setStrokeWidth(2);
        button.setTranslateX(x);
        button.setTranslateY(y);

        // Get the actual consumable item from inventory
        String itemName = "Empty";
        if (inventory != null && itemIndex < inventory.getBattleConsumables().size()) {
            ConsumableItem consumable = inventory.getBattleConsumables().get(itemIndex);
            if (consumable != null) {
                itemName = consumable.getName();
            }
        }

        Text buttonText = new Text(itemName);
        buttonText.setFont(new Font(8)); // Slightly larger font since button is bigger
        buttonText.setFill(Color.BLACK); // Change to black for better readability
        buttonText.setTranslateX(x + 2);
        buttonText.setTranslateY(y + 22); // Adjusted for larger button
        buttonText.setWrappingWidth(46); // Increased wrapping width for larger button

        // Store text as user data for cleanup
        button.setUserData(buttonText);

        // Add click handler
        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                useBattleItem(hero, itemIndex);
            }
        });

        getGameScene().addUINode(button);
        getGameScene().addUINode(buttonText);

        return button;
    }

    /**
     * Use a battle consumable item
     */
    private void useBattleItem(Observer.characterSlot hero, int itemIndex) {
        if (inventory == null) {
            System.out.println("No inventory system connected");
            return;
        }

        // Check if item exists at this index
        if (itemIndex >= inventory.getBattleConsumables().size()) {
            System.out.println("No item at slot " + (itemIndex + 1));
            return;
        }

        ConsumableItem consumable = inventory.getBattleConsumables().get(itemIndex);
        if (consumable == null) {
            System.out.println("Item at slot " + (itemIndex + 1) + " is null");
            return;
        }

        // Determine target based on item's target type
        Observer.characterSlot target = determineTarget(consumable, hero);
        if (target == null) {
            System.out.println("No valid target for " + consumable.getName());
            return;
        }

        // Use the consumable item
        if (inventory.useBattleConsumable(itemIndex, target)) {
            System.out.println("Used " + consumable.getName() + " on " + target.getCharacter().getName());

            // Play sound effect
            audioManager.playButtonClick();

            // Update UI to reflect the change
            updateAllHealthAndMpBars();

            // Re-render item buttons to show updated state
            createItemButtons(hero);
        } else {
            System.out.println("Failed to use " + consumable.getName());
        }
    }

    /**
     * Determine the target for a consumable item based on its target type
     */
    private Observer.characterSlot determineTarget(ConsumableItem consumable, Observer.characterSlot hero) {
        switch (consumable.getTargetType()) {
            case SELF:
                return hero;

            case ALLY:
                // Use selected ally target, fallback to hero if none selected
                Observer.characterSlot allyTarget = battleSystem.getSelectedAllyTarget();
                return allyTarget != null ? allyTarget : hero;

            case ENEMY:
                // Use selected enemy target, fallback to first enemy if none selected
                Observer.characterSlot enemyTarget = battleSystem.getSelectedEnemyTarget();
                return enemyTarget != null ? enemyTarget : battleSystem.getEnemySlot();

            case ANY:
                // Use selected target (enemy or ally), fallback to hero
                Observer.characterSlot anyTarget = battleSystem.getSelectedTarget();
                return anyTarget != null ? anyTarget : hero;

            default:
                return hero;
        }
    }
}
