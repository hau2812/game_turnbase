package org.example;

import abilities.Ability;
import characters.Characters;
import characters.Observer;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import map.GameMap;
import map.MapUI;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    private Line blueLine;
    private Line greenLine;
	private Line redLine;
	private Line yellowLine;

    private Rectangle redBox;

    private Rectangle blueHealthBar;
    private Rectangle greenHealthBar;
    private Rectangle redHealthBar;
	private Rectangle red2HealthBar;
	private Rectangle blueMpBar;
	private Rectangle greenMpBar;

    private Rectangle blueHealthBorder;
    private Rectangle greenHealthBorder;
    private Rectangle redHealthBorder;
	private Rectangle red2HealthBorder;
	private Rectangle blueMpBorder;
	private Rectangle greenMpBorder;

    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;

    private boolean moving = true;

    private double lineSpeed = 0.5;
	private double blueLineSpeed = lineSpeed;
	private double redLineSpeed = lineSpeed;
	private double yellowLineSpeed = lineSpeed;
    private double greenLineSpeed = lineSpeed;

    private double healthBarWidth = 200;
    private double healthBarHeight = 20;

    private Text blueHPText;
    private Text greenHPText;
    private Text redHPText;
	private Text red2HPText;
	private Text blueMPText;
	private Text greenMPText;

    // === Both sides ===
    private Observer.characterSlot heroSlot;
    private Observer.characterSlot heroSlot2;
    private Observer.characterSlot enemySlot;
	private Observer.characterSlot enemySlot2;
	private Observer.characterSlot selectedTarget;

    // Hero Skill buttons
    private Rectangle skill1Box;
    private Rectangle skill2Box;
    private Rectangle skill3Box;
    private Observer.characterSlot currentActingHero;
    //Turn
    private Line turnOf = null;

    //AI?
    private boolean autoEnemy=true;
    
    // Map system
    private GameMap gameMap;
    private MapUI mapUI;
    private boolean inMapMode = false;
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Hero vs Enemy");

    }

    private void updateMpUI(Observer.characterSlot hero) {
        double ratio = hero.getCurrentMp() / hero.getCharacter().getMp();
        if (hero == heroSlot) {
            blueMpBar.setWidth(healthBarWidth * Math.max(0, Math.min(1, ratio)));
            blueMPText.setText("MP: " + (int) hero.getCurrentMp() + " / " + (int) hero.getCharacter().getMp());
        } else if (hero == heroSlot2) {
            greenMpBar.setWidth(healthBarWidth * Math.max(0, Math.min(1, ratio)));
            greenMPText.setText("MP: " + (int) hero.getCurrentMp() + " / " + (int) hero.getCharacter().getMp());
        }
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

    private void renderHeroSkillsFor(Observer.characterSlot hero) {
        createAndRenderHeroSkillBoxes(hero);
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

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);
        // Init registry
        Observer.CharacterSlotRegistry.init();

        // Hero slots
        heroSlot = Observer.CharacterSlotRegistry.getByName("Hero");
        heroSlot2 = Observer.CharacterSlotRegistry.getByName("Hero2");

		// Enemy slots
		enemySlot = Observer.CharacterSlotRegistry.getByName("Enemy");
		enemySlot2 = Observer.CharacterSlotRegistry.getByName("Enemy2");
		selectedTarget = enemySlot; // default target


        // --- Health values ---
        double blueHP = heroSlot.getCurrentHp();
        double blueMaxHP = heroSlot.getCharacter().getHp();
        double greenHP = heroSlot2.getCurrentHp();
        double greenMaxHP = heroSlot2.getCharacter().getHp();

		double redHP = enemySlot.getCurrentHp();
		double redMaxHP = enemySlot.getCharacter().getHp();
		double red2HP = enemySlot2.getCurrentHp();
		double red2MaxHP = enemySlot2.getCharacter().getHp();

        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);

        // Blue line (Hero 1)
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth / 1.75);
        blueLine.setEndX(barX + barWidth / 1.75);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);

        // Green line (Hero 2)
        greenLine = new Line();
        greenLine.setStroke(Color.LIMEGREEN);
        greenLine.setStrokeWidth(3);
        greenLine.setStartX(barX + barWidth / 1.9);
        greenLine.setEndX(barX + barWidth / 1.9);
        greenLine.setStartY(barY);
        greenLine.setEndY(barY + barHeight);

		// Red line (Enemy 1)
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth/1.5);
        redLine.setEndX(barX + barWidth/1.5);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);

		// Yellow line (Enemy 2)
		yellowLine = new Line();
		yellowLine.setStroke(Color.YELLOW);
		yellowLine.setStrokeWidth(3);
		yellowLine.setStartX(barX + barWidth/1.33);
		yellowLine.setEndX(barX + barWidth/1.33);
		yellowLine.setStartY(barY);
		yellowLine.setEndY(barY + barHeight);

        getGameScene().addUINode(blueLine);
        getGameScene().addUINode(greenLine);
		getGameScene().addUINode(redLine);
		getGameScene().addUINode(yellowLine);

        heroSlot.setLine(blueLine);
        heroSlot2.setLine(greenLine);
		enemySlot.setLine(redLine);
		enemySlot2.setLine(yellowLine);
        // Enemy red box
//        redBox = new Rectangle(30, 30, Color.RED);
//        redBox.setTranslateX(500);
//        redBox.setTranslateY(500);
//        getGameScene().addUINode(redBox);

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

        // Borders
        blueHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        blueHealthBorder.setStroke(Color.BLACK);
        blueHealthBorder.setStrokeWidth(2);
        blueHealthBorder.setTranslateX(blueHealthX);
        blueHealthBorder.setTranslateY(blueHealthY);

        greenHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        greenHealthBorder.setStroke(Color.BLACK);
        greenHealthBorder.setStrokeWidth(2);
        greenHealthBorder.setTranslateX(greenHealthX);
        greenHealthBorder.setTranslateY(greenHealthY);

        blueMpBorder = new Rectangle(healthBarWidth, 10, Color.TRANSPARENT);
        blueMpBorder.setStroke(Color.BLACK);
        blueMpBorder.setStrokeWidth(1);
        blueMpBorder.setTranslateX(blueMpX);
        blueMpBorder.setTranslateY(blueMpY);

        greenMpBorder = new Rectangle(healthBarWidth, 10, Color.TRANSPARENT);
        greenMpBorder.setStroke(Color.BLACK);
        greenMpBorder.setStrokeWidth(1);
        greenMpBorder.setTranslateX(greenMpX);
        greenMpBorder.setTranslateY(greenMpY);

		redHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        redHealthBorder.setStroke(Color.BLACK);
        redHealthBorder.setStrokeWidth(2);
        redHealthBorder.setTranslateX(redHealthX);
        redHealthBorder.setTranslateY(redHealthY);

		red2HealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
		red2HealthBorder.setStroke(Color.BLACK);
		red2HealthBorder.setStrokeWidth(2);
		red2HealthBorder.setTranslateX(red2HealthX);
		red2HealthBorder.setTranslateY(red2HealthY);

        // Health bars
        blueHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.BLUE);
        blueHealthBar.setTranslateX(blueHealthX);
        blueHealthBar.setTranslateY(blueHealthY);

        greenHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.LIMEGREEN);
        greenHealthBar.setTranslateX(greenHealthX);
        greenHealthBar.setTranslateY(greenHealthY);

        blueMpBar = new Rectangle(healthBarWidth, 10, Color.DODGERBLUE);
        blueMpBar.setTranslateX(blueMpX);
        blueMpBar.setTranslateY(blueMpY);

        greenMpBar = new Rectangle(healthBarWidth, 10, Color.MEDIUMSEAGREEN);
        greenMpBar.setTranslateX(greenMpX);
        greenMpBar.setTranslateY(greenMpY);

		redHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.RED);
        redHealthBar.setTranslateX(redHealthX);
        redHealthBar.setTranslateY(redHealthY);

		red2HealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.CRIMSON);
		red2HealthBar.setTranslateX(red2HealthX);
		red2HealthBar.setTranslateY(red2HealthY);

        // HP text
        blueHPText = new Text("HP: " + (int) blueHP + " / " + (int) blueMaxHP);
        blueHPText.setFont(new Font(16));
        blueHPText.setFill(Color.WHITE);
        blueHPText.setTranslateX(blueHealthBorder.getTranslateX() + 4);
        blueHPText.setTranslateY(blueHealthBorder.getTranslateY() + healthBarHeight - 5);

        greenHPText = new Text("HP: " + (int) greenHP + " / " + (int) greenMaxHP);
        greenHPText.setFont(new Font(16));
        greenHPText.setFill(Color.WHITE);
        greenHPText.setTranslateX(greenHealthBorder.getTranslateX() + 4);
        greenHPText.setTranslateY(greenHealthBorder.getTranslateY() + healthBarHeight - 5);

        blueMPText = new Text("MP: " + (int) heroSlot.getCurrentMp() + " / " + (int) heroSlot.getCharacter().getMp());
        blueMPText.setFont(new Font(12));
        blueMPText.setFill(Color.WHITE);
        blueMPText.setTranslateX(blueMpBorder.getTranslateX() + 4);
        blueMPText.setTranslateY(blueMpBorder.getTranslateY() + 8);

        greenMPText = new Text("MP: " + (int) heroSlot2.getCurrentMp() + " / " + (int) heroSlot2.getCharacter().getMp());
        greenMPText.setFont(new Font(12));
        greenMPText.setFill(Color.WHITE);
        greenMPText.setTranslateX(greenMpBorder.getTranslateX() + 4);
        greenMPText.setTranslateY(greenMpBorder.getTranslateY() + 8);

        redHPText = new Text("HP: " + (int) redHP + " / " + (int) redMaxHP);
        redHPText.setFont(new Font(16));
        redHPText.setFill(Color.BLACK);
        redHPText.setTranslateX(redHealthBorder.getTranslateX() + 4);
        redHPText.setTranslateY(redHealthBorder.getTranslateY() + healthBarHeight - 5);

        red2HPText = new Text("HP: " + (int) red2HP + " / " + (int) red2MaxHP);
        red2HPText.setFont(new Font(16));
        red2HPText.setFill(Color.BLACK);
        red2HPText.setTranslateX(red2HealthBorder.getTranslateX() + 4);
        red2HPText.setTranslateY(red2HealthBorder.getTranslateY() + healthBarHeight - 5);

        // Add UI
        getGameScene().addUINode(blueHealthBorder);
        getGameScene().addUINode(blueHealthBar);
        getGameScene().addUINode(greenHealthBorder);
        getGameScene().addUINode(greenHealthBar);
        getGameScene().addUINode(blueMpBorder);
        getGameScene().addUINode(blueMpBar);
        getGameScene().addUINode(greenMpBorder);
        getGameScene().addUINode(greenMpBar);
		getGameScene().addUINode(redHealthBorder);
		getGameScene().addUINode(redHealthBar);
		getGameScene().addUINode(red2HealthBorder);
		getGameScene().addUINode(red2HealthBar);
        getGameScene().addUINode(blueHPText);
        getGameScene().addUINode(greenHPText);
        getGameScene().addUINode(blueMPText);
        getGameScene().addUINode(greenMPText);
		getGameScene().addUINode(redHPText);
		getGameScene().addUINode(red2HPText);

		// Target selection on click
		Runnable highlightSelection = () -> {
			redHealthBorder.setStroke(selectedTarget == enemySlot ? Color.GOLD : Color.BLACK);
			redHealthBorder.setStrokeWidth(selectedTarget == enemySlot ? 3 : 2);
			red2HealthBorder.setStroke(selectedTarget == enemySlot2 ? Color.GOLD : Color.BLACK);
			red2HealthBorder.setStrokeWidth(selectedTarget == enemySlot2 ? 3 : 2);
		};
		redHealthBar.setOnMouseClicked(e -> { selectedTarget = enemySlot; highlightSelection.run(); });
		redHealthBorder.setOnMouseClicked(e -> { selectedTarget = enemySlot; highlightSelection.run(); });
		redHPText.setOnMouseClicked(e -> { selectedTarget = enemySlot; highlightSelection.run(); });
		red2HealthBar.setOnMouseClicked(e -> { selectedTarget = enemySlot2; highlightSelection.run(); });
		red2HealthBorder.setOnMouseClicked(e -> { selectedTarget = enemySlot2; highlightSelection.run(); });
		red2HPText.setOnMouseClicked(e -> { selectedTarget = enemySlot2; highlightSelection.run(); });
		highlightSelection.run();

        // --- Hero Skills (initially for Hero 1) ---
        currentActingHero = heroSlot;
        createAndRenderHeroSkillBoxes(currentActingHero);

        // --- Enemy Skills (optional: for testing) ---
//        Rectangle enemySkill1 = createSkillBox(enemySlot, enemySlot.getSkills().get(0), 550, 500, Color.PINK, heroSlot);
//        Rectangle enemySkill2 = createSkillBox(enemySlot, enemySlot.getSkills().get(1), 600, 500, Color.PINK, heroSlot);
//        Rectangle enemySkill3 = createSkillBox(enemySlot, enemySlot.getSkills().get(2), 650, 500, Color.PINK, enemySlot);

        // Initialize Map System
        gameMap = new GameMap();
        mapUI = new MapUI(gameMap);

        // Add instruction text
        Text instructionText = new Text("Nhấn M để mở Map - Chọn đường đi của bạn!");
        instructionText.setFont(new Font(14));
        instructionText.setFill(Color.DARKBLUE);
        instructionText.setTranslateX(250);
        instructionText.setTranslateY(580);
        getGameScene().addUINode(instructionText);

        // Move lines every frame
		run(() -> updateLines(), Duration.millis(5));
    }

    @Override
    protected void initInput() {
        // Toggle between combat and map mode with M key
        onKeyDown(KeyCode.M, () -> {
            toggleMapMode();

        });
        
        // ESC to exit map mode
        onKeyDown(KeyCode.N, () -> {
            if (inMapMode||true) {
                exitMapMode();
            }
        });
    }

    private void toggleMapMode() {
        if (inMapMode) {
            exitMapMode();
        } else {
            enterMapMode();
        }
    }

    private void enterMapMode() {
        inMapMode = true;
        moving = false; // Pause combat
        
        // Hide combat UI elements temporarily
        hideAllCombatUI();
        
        // Show map UI
        mapUI.showPathSelection();
    }

    private void exitMapMode() {
        inMapMode = false;
        
        // Hide map UI
        mapUI.hide();
        
        // Show combat UI elements again
        showAllCombatUI();
        
        moving = true; // Resume combat
    }

    private void hideAllCombatUI() {
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

    private void showAllCombatUI() {
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
            if (e.getButton() == MouseButton.PRIMARY && !moving) {
                if (skill == null) {
                    return; // not available
                }
				Observer.characterSlot resolvedTarget;
				if (skill.getAtkScale() < 0) {
					resolvedTarget = attacker; // heals or negative dmg goes to self/ally
				} else {
					resolvedTarget = target != null ? target : selectedTarget;
				}
                // Enforce affordability for heroes before calling useSkill
                if ((attacker == heroSlot || attacker == heroSlot2) && attacker.getCurrentMp() < skill.getMpCost()) {
                    return;
                }
                useSkill(attacker, resolvedTarget, skill);
				moving = true;
			}
		});

        // Attach detail ref to the box so we can remove it later
        box.setUserData(skillDetail);

        getGameScene().addUINode(box);
        return box;
    }


    private void useSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        if(attacker.getLine()!=turnOf) {
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
                updateMpUI(attacker);
                // re-render to refresh affordability
                if (attacker == currentActingHero) {
                    renderHeroSkillsFor(attacker);
                }
            }
        }
        // Damage target
        double dmg = attacker.getCharacter().getAtk() * skill.getAtkScale();
        applyDamage(target, dmg);

        // Push line back (if attacker is hero)
        if (attacker == heroSlot || attacker == heroSlot2) {
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            Line heroLine = attacker == heroSlot ? blueLine : greenLine;
            double newX = Math.min(barX + barWidth, heroLine.getStartX() + push);
            heroLine.setStartX(newX);
            heroLine.setEndX(newX);
        }else if (attacker == enemySlot || attacker == enemySlot2) {
            // Enemy pushes red line
			double push = attacker.getCharacter().getAV() * skill.getAVScale();
            Line enemyLine = attacker == enemySlot ? redLine : yellowLine;
            if (enemyLine == null) return;
			double newX = Math.min(barX + barWidth, enemyLine.getStartX() + push);
			enemyLine.setStartX(newX);
			enemyLine.setEndX(newX);
        }
    }
    //Enemy random atk
	private void enemyTurn(Observer.characterSlot actingEnemy) {
		Random random = new Random();
		if (actingEnemy == null || actingEnemy.getCurrentHp() <= 0) {
			moving = true;
			return;
		}
		Ability.skill chosenSkill = actingEnemy.getSkills()
				.get(random.nextInt(actingEnemy.getSkills().size()));
        // Pick random living hero as target if offensive
        if (chosenSkill.getAtkScale() < 0) {
            useSkill(actingEnemy, actingEnemy, chosenSkill);
        } else {
            boolean hero1Alive = heroSlot.getCurrentHp() > 0;
            boolean hero2Alive = heroSlot2.getCurrentHp() > 0;
            Observer.characterSlot targetHero;
            if (hero1Alive && hero2Alive) {
                targetHero = random.nextBoolean() ? heroSlot : heroSlot2;
            } else if (hero1Alive) {
                targetHero = heroSlot;
            } else {
                targetHero = heroSlot2;
            }
            useSkill(actingEnemy, targetHero, chosenSkill);
        }
		moving = true;
	}


    private void updateLines() {
        if (!moving) {
            return;
        }

        moveLine(blueLine, blueLineSpeed, null);
        if (redLine != null && enemySlot.getCurrentHp() > 0) {
            moveLine(redLine, redLineSpeed, null);
        }
        if (yellowLine != null && enemySlot2.getCurrentHp() > 0) {
            moveLine(yellowLine, yellowLineSpeed, null);
        }
        moveLine(greenLine, greenLineSpeed, null);
    }

	private void moveLine(Line line, double speed, Rectangle box) {
        double newX = line.getStartX() - speed;

        if (newX <= barX) {
            turnOf = line;
            moving = false;
            if(autoEnemy) {
				// Enemy 1 turn
                if (line == redLine && enemySlot.getCurrentHp() > 0) {
					runOnce(() -> enemyTurn(enemySlot), Duration.seconds(0.25));
				}
				// Enemy 2 turn
                else if (line == yellowLine && enemySlot2.getCurrentHp() > 0) {
					runOnce(() -> enemyTurn(enemySlot2), Duration.seconds(0.25));
                } else if (line == blueLine || line == greenLine) {
                    // When a hero's line reaches, render that hero's skills
                    currentActingHero = (line == blueLine) ? heroSlot : heroSlot2;
                    renderHeroSkillsFor(currentActingHero);
				}
			}
            return;
        }

        line.setStartX(newX);
        line.setEndX(newX);
    }

	private void applyDamage(Observer.characterSlot slot, double amount) {
        slot.setCurrentHp((float)Math.max(0, slot.getCurrentHp() - amount));
        if(slot.getCurrentHp()>slot.getCharacter().getHp()) {
            slot.setCurrentHp(slot.getCharacter().getHp());
        }
        double ratio = slot.getCurrentHp() / slot.getCharacter().getHp();

        if (slot == heroSlot) {
            blueHealthBar.setWidth(healthBarWidth * ratio);
            blueHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
        } else if (slot == heroSlot2) {
            greenHealthBar.setWidth(healthBarWidth * ratio);
            greenHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
        } else if (slot == enemySlot) {
            redHealthBar.setWidth(healthBarWidth * ratio);
            redHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            if (slot.getCurrentHp() <= 0 && redLine != null) {
                getGameScene().removeUINode(redLine);
                redLine = null;
            }
		} else if (slot == enemySlot2) {
			red2HealthBar.setWidth(healthBarWidth * ratio);
			red2HPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
            if (slot.getCurrentHp() <= 0 && yellowLine != null) {
                getGameScene().removeUINode(yellowLine);
                yellowLine = null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
