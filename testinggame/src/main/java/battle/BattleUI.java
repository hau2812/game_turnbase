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

public class BattleUI {
    
    // Lines
    private Line blueLine;
    private Line greenLine;
    private Line redLine;
    private Line yellowLine;
    
    // Health bars
    private Rectangle blueHealthBar;
    private Rectangle greenHealthBar;
    private Rectangle redHealthBar;
    private Rectangle red2HealthBar;
    private Rectangle blueMpBar;
    private Rectangle greenMpBar;
    
    // Health bar borders
    private Rectangle blueHealthBorder;
    private Rectangle greenHealthBorder;
    private Rectangle redHealthBorder;
    private Rectangle red2HealthBorder;
    private Rectangle blueMpBorder;
    private Rectangle greenMpBorder;
    
    // Text elements
    private Text blueHPText;
    private Text greenHPText;
    private Text redHPText;
    private Text red2HPText;
    private Text blueMPText;
    private Text greenMPText;
    
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
        addUIElements();
        setupTargetSelection();
    }
    
    private void createTimingBar() {
        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);
    }
    
    private void createLines() {
        // Blue line (Hero 1)
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth / 1.75);
        blueLine.setEndX(barX + barWidth / 1.75);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);

        // Green line (Hero 2) - conditional
        if (battleSystem.getHeroSlot2() != null) {
            greenLine = new Line();
            greenLine.setStroke(Color.LIMEGREEN);
            greenLine.setStrokeWidth(3);
            greenLine.setStartX(barX + barWidth / 1.9);
            greenLine.setEndX(barX + barWidth / 1.9);
            greenLine.setStartY(barY);
            greenLine.setEndY(barY + barHeight);
        }

        // Red line (Enemy 1)
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth/1.5);
        redLine.setEndX(barX + barWidth/1.5);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);

        // Yellow line (Enemy 2) - conditional
        if (battleSystem.getEnemySlot2() != null) {
            yellowLine = new Line();
            yellowLine.setStroke(Color.YELLOW);
            yellowLine.setStrokeWidth(3);
            yellowLine.setStartX(barX + barWidth/1.33);
            yellowLine.setEndX(barX + barWidth/1.33);
            yellowLine.setStartY(barY);
            yellowLine.setEndY(barY + barHeight);
        }

        // Add lines to scene
        getGameScene().addUINode(blueLine);
        if (greenLine != null) {
            getGameScene().addUINode(greenLine);
        }
        getGameScene().addUINode(redLine);
        if (yellowLine != null) {
            getGameScene().addUINode(yellowLine);
        }

        // Set lines to character slots
        battleSystem.getHeroSlot().setLine(blueLine);
        if (battleSystem.getHeroSlot2() != null) {
            battleSystem.getHeroSlot2().setLine(greenLine);
        }
        battleSystem.getEnemySlot().setLine(redLine);
        if (battleSystem.getEnemySlot2() != null) {
            battleSystem.getEnemySlot2().setLine(yellowLine);
        }
    }
    
    private void createHealthBars() {
        // Health bar positions
        double blueHealthX = 50;
        double blueHealthY = 300;
        double greenHealthX = 50;
        double greenHealthY = 350;
        double blueMpX = 50;
        double blueMpY = 325;
        double greenMpX = 50;
        double greenMpY = 375;

        double redHealthX = 550;
        double redHealthY = 300;
        double red2HealthX = 550;
        double red2HealthY = 350;

        // Create borders
        blueHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        blueHealthBorder.setStroke(Color.BLACK);
        blueHealthBorder.setStrokeWidth(2);
        blueHealthBorder.setTranslateX(blueHealthX);
        blueHealthBorder.setTranslateY(blueHealthY);

        if (battleSystem.getHeroSlot2() != null) {
            greenHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
            greenHealthBorder.setStroke(Color.BLACK);
            greenHealthBorder.setStrokeWidth(2);
            greenHealthBorder.setTranslateX(greenHealthX);
            greenHealthBorder.setTranslateY(greenHealthY);
        }

        blueMpBorder = new Rectangle(healthBarWidth, 10, Color.TRANSPARENT);
        blueMpBorder.setStroke(Color.BLACK);
        blueMpBorder.setStrokeWidth(1);
        blueMpBorder.setTranslateX(blueMpX);
        blueMpBorder.setTranslateY(blueMpY);

        if (battleSystem.getHeroSlot2() != null) {
            greenMpBorder = new Rectangle(healthBarWidth, 10, Color.TRANSPARENT);
            greenMpBorder.setStroke(Color.BLACK);
            greenMpBorder.setStrokeWidth(1);
            greenMpBorder.setTranslateX(greenMpX);
            greenMpBorder.setTranslateY(greenMpY);
        }

        redHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        redHealthBorder.setStroke(Color.BLACK);
        redHealthBorder.setStrokeWidth(2);
        redHealthBorder.setTranslateX(redHealthX);
        redHealthBorder.setTranslateY(redHealthY);

        if (battleSystem.getEnemySlot2() != null) {
            red2HealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
            red2HealthBorder.setStroke(Color.BLACK);
            red2HealthBorder.setStrokeWidth(2);
            red2HealthBorder.setTranslateX(red2HealthX);
            red2HealthBorder.setTranslateY(red2HealthY);
        }

        // Create health bars
        blueHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.BLUE);
        blueHealthBar.setTranslateX(blueHealthX);
        blueHealthBar.setTranslateY(blueHealthY);

        if (battleSystem.getHeroSlot2() != null) {
            greenHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.LIMEGREEN);
            greenHealthBar.setTranslateX(greenHealthX);
            greenHealthBar.setTranslateY(greenHealthY);
        }

        blueMpBar = new Rectangle(healthBarWidth, 10, Color.DODGERBLUE);
        blueMpBar.setTranslateX(blueMpX);
        blueMpBar.setTranslateY(blueMpY);

        if (battleSystem.getHeroSlot2() != null) {
            greenMpBar = new Rectangle(healthBarWidth, 10, Color.MEDIUMSEAGREEN);
            greenMpBar.setTranslateX(greenMpX);
            greenMpBar.setTranslateY(greenMpY);
        }

        redHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.RED);
        redHealthBar.setTranslateX(redHealthX);
        redHealthBar.setTranslateY(redHealthY);

        if (battleSystem.getEnemySlot2() != null) {
            red2HealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.CRIMSON);
            red2HealthBar.setTranslateX(red2HealthX);
            red2HealthBar.setTranslateY(red2HealthY);
        }
    }
    
    private void createHealthText() {
        Observer.characterSlot heroSlot = battleSystem.getHeroSlot();
        Observer.characterSlot heroSlot2 = battleSystem.getHeroSlot2();
        Observer.characterSlot enemySlot = battleSystem.getEnemySlot();
        Observer.characterSlot enemySlot2 = battleSystem.getEnemySlot2();
        
        // HP text
        blueHPText = new Text("HP: " + (int) heroSlot.getCurrentHp() + " / " + (int) heroSlot.getCharacter().getHp());
        blueHPText.setFont(new Font(16));
        blueHPText.setFill(Color.WHITE);
        blueHPText.setTranslateX(blueHealthBorder.getTranslateX() + 4);
        blueHPText.setTranslateY(blueHealthBorder.getTranslateY() + healthBarHeight - 5);

        if (heroSlot2 != null) {
            greenHPText = new Text("HP: " + (int) heroSlot2.getCurrentHp() + " / " + (int) heroSlot2.getCharacter().getHp());
            greenHPText.setFont(new Font(16));
            greenHPText.setFill(Color.WHITE);
            greenHPText.setTranslateX(greenHealthBorder.getTranslateX() + 4);
            greenHPText.setTranslateY(greenHealthBorder.getTranslateY() + healthBarHeight - 5);
        }

        blueMPText = new Text("MP: " + (int) heroSlot.getCurrentMp() + " / " + (int) heroSlot.getCharacter().getMp());
        blueMPText.setFont(new Font(12));
        blueMPText.setFill(Color.WHITE);
        blueMPText.setTranslateX(blueMpBorder.getTranslateX() + 4);
        blueMPText.setTranslateY(blueMpBorder.getTranslateY() + 8);

        if (heroSlot2 != null) {
            greenMPText = new Text("MP: " + (int) heroSlot2.getCurrentMp() + " / " + (int) heroSlot2.getCharacter().getMp());
            greenMPText.setFont(new Font(12));
            greenMPText.setFill(Color.WHITE);
            greenMPText.setTranslateX(greenMpBorder.getTranslateX() + 4);
            greenMPText.setTranslateY(greenMpBorder.getTranslateY() + 8);
        }

        redHPText = new Text("HP: " + (int) enemySlot.getCurrentHp() + " / " + (int) enemySlot.getCharacter().getHp());
        redHPText.setFont(new Font(16));
        redHPText.setFill(Color.BLACK);
        redHPText.setTranslateX(redHealthBorder.getTranslateX() + 4);
        redHPText.setTranslateY(redHealthBorder.getTranslateY() + healthBarHeight - 5);

        if (enemySlot2 != null) {
            red2HPText = new Text("HP: " + (int) enemySlot2.getCurrentHp() + " / " + (int) enemySlot2.getCharacter().getHp());
            red2HPText.setFont(new Font(16));
            red2HPText.setFill(Color.BLACK);
            red2HPText.setTranslateX(red2HealthBorder.getTranslateX() + 4);
            red2HPText.setTranslateY(red2HealthBorder.getTranslateY() + healthBarHeight - 5);
        }
    }
    
    private void addUIElements() {
        // Add UI elements to scene
        getGameScene().addUINode(blueHealthBorder);
        getGameScene().addUINode(blueHealthBar);
        if (greenHealthBorder != null) {
            getGameScene().addUINode(greenHealthBorder);
        }
        if (greenHealthBar != null) {
            getGameScene().addUINode(greenHealthBar);
        }
        getGameScene().addUINode(blueMpBorder);
        getGameScene().addUINode(blueMpBar);
        if (greenMpBorder != null) {
            getGameScene().addUINode(greenMpBorder);
        }
        if (greenMpBar != null) {
            getGameScene().addUINode(greenMpBar);
        }
        getGameScene().addUINode(redHealthBorder);
        getGameScene().addUINode(redHealthBar);
        if (red2HealthBorder != null) {
            getGameScene().addUINode(red2HealthBorder);
        }
        if (red2HealthBar != null) {
            getGameScene().addUINode(red2HealthBar);
        }
        getGameScene().addUINode(blueHPText);
        if (greenHPText != null) {
            getGameScene().addUINode(greenHPText);
        }
        getGameScene().addUINode(blueMPText);
        if (greenMPText != null) {
            getGameScene().addUINode(greenMPText);
        }
        getGameScene().addUINode(redHPText);
        if (red2HPText != null) {
            getGameScene().addUINode(red2HPText);
        }
    }
    
    private void setupTargetSelection() {
        // Target selection on click
        Runnable highlightSelection = () -> {
            redHealthBorder.setStroke(battleSystem.getSelectedTarget() == battleSystem.getEnemySlot() ? Color.GOLD : Color.BLACK);
            redHealthBorder.setStrokeWidth(battleSystem.getSelectedTarget() == battleSystem.getEnemySlot() ? 3 : 2);
            if (red2HealthBorder != null) {
                red2HealthBorder.setStroke(battleSystem.getSelectedTarget() == battleSystem.getEnemySlot2() ? Color.GOLD : Color.BLACK);
                red2HealthBorder.setStrokeWidth(battleSystem.getSelectedTarget() == battleSystem.getEnemySlot2() ? 3 : 2);
            }
        };
        
        redHealthBar.setOnMouseClicked(e -> { 
            battleSystem.setSelectedTarget(battleSystem.getEnemySlot()); 
            highlightSelection.run(); 
        });
        redHealthBorder.setOnMouseClicked(e -> { 
            battleSystem.setSelectedTarget(battleSystem.getEnemySlot()); 
            highlightSelection.run(); 
        });
        redHPText.setOnMouseClicked(e -> { 
            battleSystem.setSelectedTarget(battleSystem.getEnemySlot()); 
            highlightSelection.run(); 
        });
        
        if (red2HealthBar != null) {
            red2HealthBar.setOnMouseClicked(e -> { 
                battleSystem.setSelectedTarget(battleSystem.getEnemySlot2()); 
                highlightSelection.run(); 
            });
        }
        if (red2HealthBorder != null) {
            red2HealthBorder.setOnMouseClicked(e -> { 
                battleSystem.setSelectedTarget(battleSystem.getEnemySlot2()); 
                highlightSelection.run(); 
            });
        }
        if (red2HPText != null) {
            red2HPText.setOnMouseClicked(e -> { 
                battleSystem.setSelectedTarget(battleSystem.getEnemySlot2()); 
                highlightSelection.run(); 
            });
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
                    "Push: " + (int)(attacker.getCharacter().getAV() * skill.getAVScale());
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
                if (skill.getAtkScale() < 0) {
                    resolvedTarget = attacker; // heals or negative dmg goes to self/ally
                } else {
                    resolvedTarget = target != null ? target : battleSystem.getSelectedTarget();
                }
                // Enforce affordability for heroes before calling useSkill
                if ((attacker == battleSystem.getHeroSlot() || attacker == battleSystem.getHeroSlot2()) && attacker.getCurrentMp() < skill.getMpCost()) {
                    return;
                }
                battleSystem.useSkill(attacker, resolvedTarget, skill);
                battleSystem.setMoving(true);
            }
        });

        // Attach detail ref to the box so we can remove it later
        box.setUserData(skillDetail);

        getGameScene().addUINode(box);
        return box;
    }
    
    private void updateSkillAffordabilityVisual(Rectangle box, Observer.characterSlot hero, Ability.skill skill) {
        boolean available = skill != null;
        boolean affordable = available && hero.getCurrentMp() >= skill.getMpCost();
        if (!available) {
            box.setOpacity(0.3);
            box.setStroke(Color.GRAY);
        } else {
            box.setOpacity(affordable ? 1.0 : 0.4);
            box.setStroke(affordable ? Color.BLACK : Color.GRAY);
        }
    }
    
    public void updateMpUI(Observer.characterSlot hero) {
        double ratio = hero.getCurrentMp() / hero.getCharacter().getMp();
        if (hero == battleSystem.getHeroSlot()) {
            blueMpBar.setWidth(healthBarWidth * Math.max(0, Math.min(1, ratio)));
            blueMPText.setText("MP: " + (int) hero.getCurrentMp() + " / " + (int) hero.getCharacter().getMp());
        } else if (hero == battleSystem.getHeroSlot2() && battleSystem.getHeroSlot2() != null) {
            if (greenMpBar != null && greenMPText != null) {
                greenMpBar.setWidth(healthBarWidth * Math.max(0, Math.min(1, ratio)));
                greenMPText.setText("MP: " + (int) hero.getCurrentMp() + " / " + (int) hero.getCharacter().getMp());
            }
        }
    }
    
    public void updateHealthUI(Observer.characterSlot slot) {
        double ratio = slot.getCurrentHp() / slot.getCharacter().getHp();

        if (slot == battleSystem.getHeroSlot()) {
            blueHealthBar.setWidth(healthBarWidth * ratio);
            blueHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
        } else if (slot == battleSystem.getHeroSlot2() && battleSystem.getHeroSlot2() != null) {
            if (greenHealthBar != null && greenHPText != null) {
                greenHealthBar.setWidth(healthBarWidth * ratio);
                greenHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            }
        } else if (slot == battleSystem.getEnemySlot()) {
            redHealthBar.setWidth(healthBarWidth * ratio);
            redHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
        } else if (slot == battleSystem.getEnemySlot2() && battleSystem.getEnemySlot2() != null) {
            if (red2HealthBar != null && red2HPText != null) {
                red2HealthBar.setWidth(healthBarWidth * ratio);
                red2HPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            }
        }
    }
    
    public void hideAllCombatUI() {
        // Hide health bars and related UI
        if (blueHealthBorder != null) blueHealthBorder.setVisible(false);
        if (blueHealthBar != null) blueHealthBar.setVisible(false);
        if (greenHealthBorder != null) greenHealthBorder.setVisible(false);
        if (greenHealthBar != null) greenHealthBar.setVisible(false);
        if (redHealthBorder != null) redHealthBorder.setVisible(false);
        if (redHealthBar != null) redHealthBar.setVisible(false);
        if (red2HealthBorder != null) red2HealthBorder.setVisible(false);
        if (red2HealthBar != null) red2HealthBar.setVisible(false);
        
        if (blueMpBorder != null) blueMpBorder.setVisible(false);
        if (blueMpBar != null) blueMpBar.setVisible(false);
        if (greenMpBorder != null) greenMpBorder.setVisible(false);
        if (greenMpBar != null) greenMpBar.setVisible(false);
        
        // Hide text
        if (blueHPText != null) blueHPText.setVisible(false);
        if (greenHPText != null) greenHPText.setVisible(false);
        if (redHPText != null) redHPText.setVisible(false);
        if (red2HPText != null) red2HPText.setVisible(false);
        if (blueMPText != null) blueMPText.setVisible(false);
        if (greenMPText != null) greenMPText.setVisible(false);
        
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

    public void showAllCombatUI() {
        // Show health bars and related UI
        if (blueHealthBorder != null) blueHealthBorder.setVisible(true);
        if (blueHealthBar != null) blueHealthBar.setVisible(true);
        if (greenHealthBorder != null) greenHealthBorder.setVisible(true);
        if (greenHealthBar != null) greenHealthBar.setVisible(true);
        if (redHealthBorder != null) redHealthBorder.setVisible(true);
        if (redHealthBar != null) redHealthBar.setVisible(true);
        if (red2HealthBorder != null) red2HealthBorder.setVisible(true);
        if (red2HealthBar != null) red2HealthBar.setVisible(true);
        
        if (blueMpBorder != null) blueMpBorder.setVisible(true);
        if (blueMpBar != null) blueMpBar.setVisible(true);
        if (greenMpBorder != null) greenMpBorder.setVisible(true);
        if (greenMpBar != null) greenMpBar.setVisible(true);
        
        // Show text
        if (blueHPText != null) blueHPText.setVisible(true);
        if (greenHPText != null) greenHPText.setVisible(true);
        if (redHPText != null) redHPText.setVisible(true);
        if (red2HPText != null) red2HPText.setVisible(true);
        if (blueMPText != null) blueMPText.setVisible(true);
        if (greenMPText != null) greenMPText.setVisible(true);
        
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
    public void setRedLine(Line redLine) { this.redLine = redLine; }
    public void setYellowLine(Line yellowLine) { this.yellowLine = yellowLine; }
}
