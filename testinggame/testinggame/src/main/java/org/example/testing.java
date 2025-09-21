package org.example;

import abilities.Ability;
import characters.Characters;
import characters.Observer;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class testing extends GameApplication {

    private Line blueLine;
    private Line redLine;

    private Rectangle redBox;

    private Rectangle blueHealthBar;
    private Rectangle redHealthBar;

    private Rectangle blueHealthBorder;
    private Rectangle redHealthBorder;

    private double barX = 300;
    private double barY = 50;
    private double barWidth = 200;
    private double barHeight = 20;

    private boolean moving = true;

    private double lineSpeed = 0.5;
    private double blueLineSpeed = lineSpeed;

    private double healthBarWidth = 200;
    private double healthBarHeight = 20;

    private Text blueHPText;
    private Text redHPText;

    // === Both sides ===
    private Observer.characterSlot heroSlot;
    private Observer.characterSlot enemySlot;

    // Hero Skill buttons
    private Rectangle skill1Box;
    private Rectangle skill2Box;
    private Rectangle skill3Box;
    //Turn
    private Line turnOf = null;

    //AI?
    private boolean autoEnemy=true;
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Hero vs Enemy");
    }

    @Override
    protected void initGame() {
        // Init registry
        Observer.CharacterSlotRegistry.init();

        // Hero slot
        heroSlot = Observer.CharacterSlotRegistry.getByName("Hero");

        // Enemy slot (for now, clone hero for testing)
        enemySlot = Observer.CharacterSlotRegistry.getByName("Enemy");


        // --- Health values ---
        double blueHP = heroSlot.getCurrentHp();
        double blueMaxHP = heroSlot.getCharacter().getHp();

        double redHP = enemySlot.getCurrentHp();
        double redMaxHP = enemySlot.getCharacter().getHp();

        // Background black bar
        Rectangle blackBar = new Rectangle(barWidth, barHeight, Color.BLACK);
        blackBar.setTranslateX(barX);
        blackBar.setTranslateY(barY);
        getGameScene().addUINode(blackBar);

        // Blue line
        blueLine = new Line();
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(3);
        blueLine.setStartX(barX + barWidth / 1.75);
        blueLine.setEndX(barX + barWidth / 1.75);
        blueLine.setStartY(barY);
        blueLine.setEndY(barY + barHeight);

        // Red line
        redLine = new Line();
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(3);
        redLine.setStartX(barX + barWidth);
        redLine.setEndX(barX + barWidth);
        redLine.setStartY(barY);
        redLine.setEndY(barY + barHeight);

        getGameScene().addUINode(blueLine);
        getGameScene().addUINode(redLine);

        heroSlot.setLine(blueLine);
        enemySlot.setLine(redLine);
        // Enemy red box
//        redBox = new Rectangle(30, 30, Color.RED);
//        redBox.setTranslateX(500);
//        redBox.setTranslateY(500);
//        getGameScene().addUINode(redBox);

        // Health bar positions
        double blueHealthX = 50;
        double blueHealthY = 300;

        double redHealthX = 550;
        double redHealthY = 300;

        // Borders
        blueHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        blueHealthBorder.setStroke(Color.BLACK);
        blueHealthBorder.setStrokeWidth(2);
        blueHealthBorder.setTranslateX(blueHealthX);
        blueHealthBorder.setTranslateY(blueHealthY);

        redHealthBorder = new Rectangle(healthBarWidth, healthBarHeight, Color.TRANSPARENT);
        redHealthBorder.setStroke(Color.BLACK);
        redHealthBorder.setStrokeWidth(2);
        redHealthBorder.setTranslateX(redHealthX);
        redHealthBorder.setTranslateY(redHealthY);

        // Health bars
        blueHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.BLUE);
        blueHealthBar.setTranslateX(blueHealthX);
        blueHealthBar.setTranslateY(blueHealthY);

        redHealthBar = new Rectangle(healthBarWidth, healthBarHeight, Color.RED);
        redHealthBar.setTranslateX(redHealthX);
        redHealthBar.setTranslateY(redHealthY);

        // HP text
        blueHPText = new Text("HP: " + (int) blueHP + " / " + (int) blueMaxHP);
        blueHPText.setFont(new Font(16));
        blueHPText.setFill(Color.BLUE);
        blueHPText.setTranslateX(blueHealthBorder.getTranslateX());
        blueHPText.setTranslateY(blueHealthBorder.getTranslateY() - 5);

        redHPText = new Text("HP: " + (int) redHP + " / " + (int) redMaxHP);
        redHPText.setFont(new Font(16));
        redHPText.setFill(Color.RED);
        redHPText.setTranslateX(redHealthBorder.getTranslateX());
        redHPText.setTranslateY(redHealthBorder.getTranslateY() - 5);

        // Add UI
        getGameScene().addUINode(blueHealthBorder);
        getGameScene().addUINode(blueHealthBar);
        getGameScene().addUINode(redHealthBorder);
        getGameScene().addUINode(redHealthBar);
        getGameScene().addUINode(blueHPText);
        getGameScene().addUINode(redHPText);

        // --- Hero Skills ---
        skill1Box = createSkillBox(heroSlot, heroSlot.getSkills().get(0), 50, 500, Color.LIGHTBLUE, enemySlot);
        skill2Box = createSkillBox(heroSlot, heroSlot.getSkills().get(1), 100, 500, Color.CYAN, enemySlot);
        skill3Box = createSkillBox(heroSlot, heroSlot.getSkills().get(2), 150, 500, Color.DARKBLUE, heroSlot);

        // --- Enemy Skills (optional: also add clickable boxes for testing) ---
        Rectangle enemySkill1 = createSkillBox(enemySlot, enemySlot.getSkills().get(0), 550, 500, Color.PINK, heroSlot);
        Rectangle enemySkill2 = createSkillBox(enemySlot, enemySlot.getSkills().get(1), 600, 500, Color.PINK, heroSlot);
        Rectangle enemySkill3 = createSkillBox(enemySlot, enemySlot.getSkills().get(2), 650, 500, Color.PINK, enemySlot);

        // Move lines every frame
        run(() -> updateLines(), Duration.millis(5));
    }

    private Rectangle createSkillBox(Observer.characterSlot attacker, Ability.skill skill,
                                     double x, double y, Color color,
                                     Observer.characterSlot target) {
        Rectangle box = new Rectangle(30, 30, color);
        box.setTranslateX(x);
        box.setTranslateY(y);

        Text skillDetail = new Text(
                skill.getName() + "\n" +
                        "Damage: " + (int)(attacker.getCharacter().getAtk() * skill.getAtkScale()) + "\n" +
                        "Push: " + (int)(attacker.getCharacter().getAV() * skill.getAVScale())
        );
        skillDetail.setFont(new Font(14));
        skillDetail.setFill(Color.WHITE);
        skillDetail.setVisible(false);
        skillDetail.setTranslateX(x + 40); // offset to the right
        skillDetail.setTranslateY(y);

        getGameScene().addUINode(skillDetail);

        // Show / hide when hovering
        box.setOnMouseEntered(e -> skillDetail.setVisible(true));
        box.setOnMouseExited(e -> skillDetail.setVisible(false));

        // On click -> use skill
        box.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !moving) {
                useSkill(attacker, target, skill);
                moving = true;
            }
        });

        getGameScene().addUINode(box);
        return box;
    }


    private void useSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        if(attacker.getLine()!=turnOf) {
            return;
        }
        // Damage target
        double dmg = attacker.getCharacter().getAtk() * skill.getAtkScale();
        applyDamage(target, dmg);

        // Push line back (if attacker is hero)
        if (attacker == heroSlot) {
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            double newX = Math.min(barX + barWidth, blueLine.getStartX() + push);
            blueLine.setStartX(newX);
            blueLine.setEndX(newX);
        }else if (attacker == enemySlot) {
            // Enemy pushes red line
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            double newX = Math.min(barX + barWidth, redLine.getStartX() + push);
            redLine.setStartX(newX);
            redLine.setEndX(newX);
        }
    }
    //Enemy random atk
    private void enemyTurn() {
        Random random = new Random();

        // Pick a random skill
        Ability.skill chosenSkill = enemySlot.getSkills()
                .get(random.nextInt(enemySlot.getSkills().size()));

        // Enemy uses skill on hero
        if(chosenSkill.getAtkScale()<0){
            useSkill(enemySlot, enemySlot, chosenSkill);
        }else {
            useSkill(enemySlot, heroSlot, chosenSkill);
        }
        // Resume line movement
        moving = true;
    }


    private void updateLines() {
        if (!moving) {
            return;
        }

        moveLine(blueLine, blueLineSpeed, null);
        moveLine(redLine, lineSpeed, redBox);
    }

    private void moveLine(Line line, double speed, Rectangle box) {
        double newX = line.getStartX() - speed;

        if (newX <= barX) {
            turnOf = line;
            moving = false;
            if(autoEnemy) {
                // === Enemy turn check ===
                if (box != null) box.setFill(Color.GREEN);
                if (line == redLine) {
                    runOnce(() -> enemyTurn(), Duration.seconds(0.25)); // small delay for effect
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
        } else if (slot == enemySlot) {
            redHealthBar.setWidth(healthBarWidth * ratio);
            redHPText.setText("HP: " + (int) slot.getCurrentHp() + " / " + (int) slot.getCharacter().getHp());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
