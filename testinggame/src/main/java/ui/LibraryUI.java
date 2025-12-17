package ui;

import abilities.Ability;
import characters.Characters;
import characters.Observer;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Library UI to show in-game information (heroes, bosses, dialogs).
 * For now, only Hero data is implemented.
 */
public class LibraryUI {

    private final Group mainContainer;

    // Top menu buttons
    private Rectangle heroTabButton;
    private Text heroTabText;
    private Rectangle bossTabButton;
    private Text bossTabText;
    private Rectangle dialogTabButton;
    private Text dialogTabText;

    // Left list (names)
    private Rectangle listBackground;
    private List<Rectangle> entryButtons = new ArrayList<>();
    private List<Text> entryTexts = new ArrayList<>();

    // Center hero data / TL, S1–S8 buttons
    private Rectangle heroDataBackground;
    private Text heroBaseInfoText;
    private Rectangle tlButton;
    private Text tlButtonText;
    private Rectangle s1Button;
    private Text s1ButtonText;
    private Rectangle s2Button;
    private Text s2ButtonText;
    private Rectangle s3Button;
    private Text s3ButtonText;
    private Rectangle s4Button;
    private Text s4ButtonText;
    private Rectangle s5Button;
    private Text s5ButtonText;
    private Rectangle s6Button;
    private Text s6ButtonText;
    private Rectangle s7Button;
    private Text s7ButtonText;
    private Rectangle s8Button;
    private Text s8ButtonText;

    // Right detail panel
    private Rectangle detailBackground;
    private Text detailTitleText;
    private Text detailBodyText;

    // Dialog tab UI elements
    private Rectangle dialogBoxArea; // Big area for dialog boxes (replaces heroDataBackground + detailBackground)
    private List<DialogBox> currentDialogBoxes = new ArrayList<>(); // Currently displayed dialog boxes

    private boolean visible = false;

    // Optional callback for when the library is closed via its own UI (Back button)
    private Runnable onCloseCallback;

    public LibraryUI() {
        mainContainer = new Group();
        initializeUI();
    }

    public Group getRoot() {
        return mainContainer;
    }

    /**
     * Set a callback to run when the library is closed via the Back button.
     * MenuUI can use this to restore its own state (e.g., Call Party button).
     */
    public void setOnClose(Runnable callback) {
        this.onCloseCallback = callback;
    }

    public void show() {
        if (!visible) {
            visible = true;
            mainContainer.setVisible(true);
            mainContainer.setMouseTransparent(false);
        }
        // Default to hero tab
        showHeroTab();
    }

    public void hide() {
        visible = false;
        mainContainer.setVisible(false);
        mainContainer.setMouseTransparent(true);
    }

    public boolean isVisible() {
        return visible;
    }

    private void initializeUI() {
        // Background for entire library area
        Rectangle bg = new Rectangle(800, 600, Color.rgb(0, 0, 0, 0.6));
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(2);
        bg.setTranslateX(0);
        bg.setTranslateY(0);

        // Back/close button (top right)
        Rectangle backButton = new Rectangle(80, 30, Color.rgb(120, 80, 80));
        backButton.setStroke(Color.WHITE);
        backButton.setStrokeWidth(1.5);
        backButton.setTranslateX(800 - 90);
        backButton.setTranslateY(20);

        Text backText = new Text("Back");
        backText.setFont(new Font(14));
        backText.setFill(Color.WHITE);
        backText.setMouseTransparent(true);
        backText.setTranslateX(800 - 70);
        backText.setTranslateY(40);

        backButton.setOnMouseEntered(e -> backButton.setFill(Color.rgb(150, 100, 100)));
        backButton.setOnMouseExited(e -> backButton.setFill(Color.rgb(120, 80, 80)));
        backButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                } else {
                    hide();
                }
            }
        });

        // Top tabs
        double tabWidth = 150;
        double tabHeight = 40;
        double tabY = 20;

        heroTabButton = createTabButton(50, tabY, tabWidth, tabHeight, "Hero data");
        heroTabText = createTabText(heroTabButton, "Hero data");

        bossTabButton = createTabButton(50 + tabWidth + 10, tabY, tabWidth, tabHeight, "Boss data");
        bossTabText = createTabText(bossTabButton, "Boss data");

        dialogTabButton = createTabButton(50 + (tabWidth + 10) * 2, tabY, tabWidth, tabHeight, "Dialog data");
        dialogTabText = createTabText(dialogTabButton, "Dialog data");

        heroTabButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showHeroTab();
            }
        });
        bossTabButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Boss data not implemented yet
            }
        });
        dialogTabButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showDialogTab();
            }
        });

        // Left list background
        listBackground = new Rectangle(220, 480, Color.rgb(30, 30, 30, 0.9));
        listBackground.setStroke(Color.WHITE);
        listBackground.setStrokeWidth(1.5);
        listBackground.setTranslateX(30);
        listBackground.setTranslateY(80);

        // Center hero data background
        heroDataBackground = new Rectangle(260, 480, Color.rgb(40, 40, 40, 0.9));
        heroDataBackground.setStroke(Color.WHITE);
        heroDataBackground.setStrokeWidth(1.5);
        heroDataBackground.setTranslateX(270);
        heroDataBackground.setTranslateY(80);

        heroBaseInfoText = new Text("");
        // Use monospaced font so aligned columns render correctly
        heroBaseInfoText.setFont(Font.font("Monospaced", 20));
        heroBaseInfoText.setFill(Color.WHITE);
        heroBaseInfoText.setWrappingWidth(240);
        heroBaseInfoText.setTranslateX(280);
        heroBaseInfoText.setTranslateY(110);
        heroBaseInfoText.setMouseTransparent(true);

        // TL / S1–S8 buttons (two rows of 4)
        double btnWidth = 40;
        double btnHeight = 25;
        double btnStartX = 280;
        double btnYBottom = 80 + 440; // bottom row near bottom of heroDataBackground
        double btnSpacing = 10;

        // Bottom row: TL, S1–S4
        tlButton = createSmallButton(btnStartX, btnYBottom, btnWidth, btnHeight, "TL");
        tlButtonText = createSmallButtonText(tlButton, "TL");

        s1Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 1, btnYBottom, btnWidth, btnHeight, "S1");
        s1ButtonText = createSmallButtonText(s1Button, "S1");

        s2Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 2, btnYBottom, btnWidth, btnHeight, "S2");
        s2ButtonText = createSmallButtonText(s2Button, "S2");

        s3Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 3, btnYBottom, btnWidth, btnHeight, "S3");
        s3ButtonText = createSmallButtonText(s3Button, "S3");

        s4Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 4, btnYBottom, btnWidth, btnHeight, "S4");
        s4ButtonText = createSmallButtonText(s4Button, "S4");

        // Top row: S5–S8
        double btnYTop = btnYBottom - (btnHeight + btnSpacing);
        s5Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 0, btnYTop, btnWidth, btnHeight, "S5");
        s5ButtonText = createSmallButtonText(s5Button, "S5");
        s6Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 1, btnYTop, btnWidth, btnHeight, "S6");
        s6ButtonText = createSmallButtonText(s6Button, "S6");
        s7Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 2, btnYTop, btnWidth, btnHeight, "S7");
        s7ButtonText = createSmallButtonText(s7Button, "S7");
        s8Button = createSmallButton(btnStartX + (btnWidth + btnSpacing) * 3, btnYTop, btnWidth, btnHeight, "S8");
        s8ButtonText = createSmallButtonText(s8Button, "S8");

        // Hide all skill buttons/texts initially (no hero selected yet)
        tlButton.setVisible(false);
        tlButtonText.setVisible(false);
        s1Button.setVisible(false);
        s1ButtonText.setVisible(false);
        s2Button.setVisible(false);
        s2ButtonText.setVisible(false);
        s3Button.setVisible(false);
        s3ButtonText.setVisible(false);
        s4Button.setVisible(false);
        s4ButtonText.setVisible(false);
        s5Button.setVisible(false);
        s5ButtonText.setVisible(false);
        s6Button.setVisible(false);
        s6ButtonText.setVisible(false);
        s7Button.setVisible(false);
        s7ButtonText.setVisible(false);
        s8Button.setVisible(false);
        s8ButtonText.setVisible(false);

        // Right detail panel
        detailBackground = new Rectangle(220, 480, Color.rgb(50, 50, 50, 0.9));
        detailBackground.setStroke(Color.WHITE);
        detailBackground.setStrokeWidth(1.5);
        detailBackground.setTranslateX(540);
        detailBackground.setTranslateY(80);

        detailTitleText = new Text("");
        detailTitleText.setFont(new Font(16));
        detailTitleText.setFill(Color.WHITE);
        detailTitleText.setTranslateX(550);
        detailTitleText.setTranslateY(110);
        detailTitleText.setMouseTransparent(true);

        detailBodyText = new Text("");
        detailBodyText.setFont(new Font(14));
        detailBodyText.setFill(Color.WHITE);
        detailBodyText.setWrappingWidth(200);
        detailBodyText.setTextAlignment(TextAlignment.LEFT);
        detailBodyText.setTranslateX(550);
        detailBodyText.setTranslateY(140);
        detailBodyText.setMouseTransparent(true);

        // Dialog box area (big area for dialog tab, replaces heroDataBackground + detailBackground)
        dialogBoxArea = new Rectangle(480, 480, Color.rgb(40, 40, 40, 0.9));
        dialogBoxArea.setStroke(Color.WHITE);
        dialogBoxArea.setStrokeWidth(1.5);
        dialogBoxArea.setTranslateX(270);
        dialogBoxArea.setTranslateY(80);
        dialogBoxArea.setVisible(false); // Hidden by default (hero tab is shown first)

        // Initially hidden
        mainContainer.getChildren().addAll(
                bg,
                listBackground,
                heroDataBackground,
                detailBackground,
                dialogBoxArea,
                heroBaseInfoText,
                tlButton, tlButtonText,
                s1Button, s1ButtonText,
                s2Button, s2ButtonText,
                s3Button, s3ButtonText,
                s4Button, s4ButtonText,
                s5Button, s5ButtonText,
                s6Button, s6ButtonText,
                s7Button, s7ButtonText,
                s8Button, s8ButtonText,
                detailTitleText,
                detailBodyText,
                backButton, backText,
                heroTabButton, heroTabText,
                bossTabButton, bossTabText,
                dialogTabButton, dialogTabText
        );

        mainContainer.setVisible(false);
        mainContainer.setMouseTransparent(true);
    }

    private Rectangle createTabButton(double x, double y, double w, double h, String label) {
        Rectangle r = new Rectangle(w, h, Color.rgb(80, 80, 80));
        r.setStroke(Color.WHITE);
        r.setStrokeWidth(1.5);
        r.setTranslateX(x);
        r.setTranslateY(y);
        return r;
    }

    private Text createTabText(Rectangle base, String label) {
        Text t = new Text(label);
        t.setFont(new Font(14));
        t.setFill(Color.WHITE);
        t.setMouseTransparent(true);
        t.setTranslateX(base.getTranslateX() + 10);
        t.setTranslateY(base.getTranslateY() + 24);
        return t;
    }

    private Rectangle createSmallButton(double x, double y, double w, double h, String label) {
        Rectangle r = new Rectangle(w, h, Color.rgb(90, 90, 90));
        r.setStroke(Color.WHITE);
        r.setStrokeWidth(1);
        r.setTranslateX(x);
        r.setTranslateY(y);
        r.setOnMouseEntered(e -> r.setFill(Color.rgb(110, 110, 110)));
        r.setOnMouseExited(e -> r.setFill(Color.rgb(90, 90, 90)));
        return r;
    }

    private Text createSmallButtonText(Rectangle base, String label) {
        Text t = new Text(label);
        t.setFont(new Font(12));
        t.setFill(Color.WHITE);
        t.setMouseTransparent(true);
        t.setTranslateX(base.getTranslateX() + 10);
        t.setTranslateY(base.getTranslateY() + 17);
        return t;
    }

    /**
     * Populate the left list with all hero names from CharacterRegistry.
     */
    private void populateHeroList() {
        // Clear old
        for (Rectangle r : entryButtons) {
            mainContainer.getChildren().remove(r);
        }
        for (Text t : entryTexts) {
            mainContainer.getChildren().remove(t);
        }
        entryButtons.clear();
        entryTexts.clear();

        List<Characters.character> heroes = new ArrayList<>(Characters.CharacterRegistry.getAll());
        heroes.remove(Characters.CharacterRegistry.getByName("Lucia"));
        double startX = listBackground.getTranslateX() + 10;
        double startY = listBackground.getTranslateY() + 10;
        double rowHeight = 30;

        int index = 0;
        for (Characters.character ch : heroes) {
            String name = ch.getName();

            Rectangle button = new Rectangle(listBackground.getWidth() - 20, rowHeight - 4, Color.rgb(60, 60, 60));
            button.setStroke(Color.WHITE);
            button.setStrokeWidth(1);
            button.setTranslateX(startX);
            button.setTranslateY(startY + index * rowHeight);

            Text text = new Text(name);
            text.setFont(new Font(14));
            text.setFill(Color.WHITE);
            text.setMouseTransparent(true);
            text.setTranslateX(startX + 10);
            text.setTranslateY(startY + index * rowHeight + 20);

            button.setOnMouseEntered(e -> button.setFill(Color.rgb(80, 80, 80)));
            button.setOnMouseExited(e -> button.setFill(Color.rgb(60, 60, 60)));

            final Characters.character selected = ch;
            button.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    showHeroBaseInfo(selected);
                    setupDetailButtons(selected);
                    // Clear any old skill detail when switching hero
                    detailTitleText.setText("");
                    detailBodyText.setText("");
                }
            });

            entryButtons.add(button);
            entryTexts.add(text);
            mainContainer.getChildren().addAll(button, text);

            index++;
        }
    }

    private void showHeroTab() {
        // Hide dialog tab elements
        dialogBoxArea.setVisible(false);
        clearDialogBoxes();

        // Show hero tab elements
        heroDataBackground.setVisible(true);
        detailBackground.setVisible(true);
        heroBaseInfoText.setVisible(true);
        detailTitleText.setVisible(true);
        detailBodyText.setVisible(true);

        populateHeroList();
        // Clear right detail
        detailTitleText.setText("");
        detailBodyText.setText("");
        heroBaseInfoText.setText("");

        // Hide all skill buttons when no hero is selected
        hideAllSkillButtons();
    }

    private void showHeroBaseInfo(Characters.character ch) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(ch.getName()).append("\n");
        sb.append(String.format("ATK:%4d  MATK:%4d%n", (int) ch.getAtk(), (int) ch.getMatk()));
        sb.append(String.format("DEF:%4d  RES :%4d%n", (int) ch.getDef(), (int) ch.getRes()));
        sb.append(String.format("SPD:%4d  AV  :%4d%n", (int) ch.getSpd(), (int) ch.getAV()));
        sb.append(String.format("HP :%4d  MP  :%4d%n", (int) ch.getHp(), (int) ch.getMp()));

        heroBaseInfoText.setText(sb.toString());
    }

    /**
     * Wire TL / S1–S8 buttons for a specific hero.
     * TL shows talent description from Characters.character.
     * S1–S4 show skill longdescription if available.
     */
    private void setupDetailButtons(Characters.character ch) {
        String heroName = ch.getName();
        // From CharacterSlotRegistry we can get the slot to know skills
        Observer.characterSlot slot =
                Observer.CharacterSlotRegistry.getByName(heroName);
        Ability.skill s1 = null;
        Ability.skill s2 = null;
        Ability.skill s3 = null;
        Ability.skill s4 = null;
        Ability.skill s5 = null;
        Ability.skill s6 = null;
        Ability.skill s7 = null;
        Ability.skill s8 = null;
        int skillCount = 0;
        if (slot != null && slot.getSkills() != null && !slot.getSkills().isEmpty()) {
            ArrayList<Ability.skill> skills = slot.getSkills();
            skillCount = skills.size();
            if (skillCount > 0) s1 = skills.get(0);
            if (skillCount > 1) s2 = skills.get(1);
            if (skillCount > 2) s3 = skills.get(2);
            if (skillCount > 3) s4 = skills.get(3);
            if (skillCount > 4) s5 = skills.get(4);
            if (skillCount > 5) s6 = skills.get(5);
            if (skillCount > 6) s7 = skills.get(6);
            if (skillCount > 7) s8 = skills.get(7);
        }

        // TL
        tlButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                detailTitleText.setText("Talent: " + heroName);
                String talent = ch.getTalentDiscription();
                if (talent == null) {
                    talent = "";
                }
                detailBodyText.setText(talent);
            }
        });

        // Helper to wire skill buttons
        // Show bottom row buttons when a hero is selected (at least one skill slot)
        boolean hasAnySkillSlot = (skillCount > 0);
        tlButton.setVisible(hasAnySkillSlot);
        tlButtonText.setVisible(hasAnySkillSlot);
        s1Button.setVisible(hasAnySkillSlot);
        s1ButtonText.setVisible(hasAnySkillSlot);
        s2Button.setVisible(hasAnySkillSlot);
        s2ButtonText.setVisible(hasAnySkillSlot);
        s3Button.setVisible(hasAnySkillSlot);
        s3ButtonText.setVisible(hasAnySkillSlot);
        s4Button.setVisible(hasAnySkillSlot);
        s4ButtonText.setVisible(hasAnySkillSlot);

        final Ability.skill fs1 = s1;
        s1Button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 1", fs1);
            }
        });

        final Ability.skill fs2 = s2;
        s2Button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 2", fs2);
            }
        });

        final Ability.skill fs3 = s3;
        s3Button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 3", fs3);
            }
        });

        final Ability.skill fs4 = s4;
        s4Button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 4", fs4);
            }
        });

        // Show S5–S8 only if hero has 8 skills
        boolean showExtraRow = (skillCount >= 8);
        s5Button.setVisible(showExtraRow);
        s5ButtonText.setVisible(showExtraRow);
        s6Button.setVisible(showExtraRow);
        s6ButtonText.setVisible(showExtraRow);
        s7Button.setVisible(showExtraRow);
        s7ButtonText.setVisible(showExtraRow);
        s8Button.setVisible(showExtraRow);
        s8ButtonText.setVisible(showExtraRow);

        final Ability.skill fs5 = s5;
        s5Button.setOnMouseClicked(e -> {
            if (showExtraRow && e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 5", fs5);
            }
        });

        final Ability.skill fs6 = s6;
        s6Button.setOnMouseClicked(e -> {
            if (showExtraRow && e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 6", fs6);
            }
        });

        final Ability.skill fs7 = s7;
        s7Button.setOnMouseClicked(e -> {
            if (showExtraRow && e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 7", fs7);
            }
        });

        final Ability.skill fs8 = s8;
        s8Button.setOnMouseClicked(e -> {
            if (showExtraRow && e.getButton() == MouseButton.PRIMARY) {
                showSkillDetail("Skill 8", fs8);
            }
        });
    }

    private void showSkillDetail(String label, Ability.skill skill) {
        if (skill == null) {
            detailTitleText.setText(label);
            detailBodyText.setText("");
            return;
        }

        detailTitleText.setText(label + ": " + skill.getName());

        StringBuilder fullText = new StringBuilder();

        // --- Numeric detail section ---
        boolean hasAnyStat = false;

        if (skill.getAtkScale() != 0f) {
            fullText.append("ATK Scale: ").append(skill.getAtkScale());
            hasAnyStat = true;
        }
        if (skill.getAVScale() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("AV Scale: ").append(skill.getAVScale());
            hasAnyStat = true;
        }
        if (skill.getMpCost() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("MP Cost: ").append((int) skill.getMpCost());
            hasAnyStat = true;
        }
        if (skill.getPartyMpCost() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("Party MP Cost: ").append((int) skill.getPartyMpCost());
            hasAnyStat = true;
        }
        if (skill.getBurningRageRequired() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("Burning Rage Required: ").append((int) skill.getBurningRageRequired());
            hasAnyStat = true;
        }
        if (skill.getBurningRageConsumed() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("Burning Rage Consumed: ").append((int) skill.getBurningRageConsumed());
            hasAnyStat = true;
        }
        if (skill.getBurningRageGained() != 0f) {
            if (hasAnyStat) fullText.append("\n");
            fullText.append("Burning Rage Gained: ").append((int) skill.getBurningRageGained());
            hasAnyStat = true;
        }

        // Blank line between stats and descriptions if we showed any stats
        if (hasAnyStat) {
            fullText.append("\n\n");
        }

        // --- Description section (short + optional long) ---
        String shortDesc = skill.getDescription();
        if (shortDesc == null) {
            shortDesc = "";
        }
        fullText.append(shortDesc);

        String longDesc = skill.getLongdescription();
        if (longDesc != null) {
            longDesc = longDesc.trim();
        }
        if (longDesc != null && !longDesc.isEmpty()) {
            fullText.append("\n\n");
            fullText.append(longDesc);
        }

        detailBodyText.setText(fullText.toString());
    }

    /**
     * Show the dialog tab with "overall" and hero names on the left,
     * and dialog boxes on the right.
     */
    private void showDialogTab() {
        // Hide hero tab elements
        heroDataBackground.setVisible(false);
        detailBackground.setVisible(false);
        heroBaseInfoText.setVisible(false);
        detailTitleText.setVisible(false);
        detailBodyText.setVisible(false);
        hideAllSkillButtons();

        // Show dialog box area
        dialogBoxArea.setVisible(true);

        // Populate left list with "overall" and hero names
        populateDialogList();

        // Default to showing "overall" dialogs
        showDialogBoxesForHost("overall");
    }

    /**
     * Populate the left list with "overall" and all hero names.
     */
    private void populateDialogList() {
        // Clear old
        for (Rectangle r : entryButtons) {
            mainContainer.getChildren().remove(r);
        }
        for (Text t : entryTexts) {
            mainContainer.getChildren().remove(t);
        }
        entryButtons.clear();
        entryTexts.clear();

        double startX = listBackground.getTranslateX() + 10;
        double startY = listBackground.getTranslateY() + 10;
        double rowHeight = 30;

        int index = 0;

        // Add "overall" entry
        Rectangle overallButton = new Rectangle(listBackground.getWidth() - 20, rowHeight - 4, Color.rgb(60, 60, 60));
        overallButton.setStroke(Color.WHITE);
        overallButton.setStrokeWidth(1);
        overallButton.setTranslateX(startX);
        overallButton.setTranslateY(startY + index * rowHeight);

        Text overallText = new Text("overall");
        overallText.setFont(new Font(14));
        overallText.setFill(Color.WHITE);
        overallText.setMouseTransparent(true);
        overallText.setTranslateX(startX + 10);
        overallText.setTranslateY(startY + index * rowHeight + 20);

        overallButton.setOnMouseEntered(e -> overallButton.setFill(Color.rgb(80, 80, 80)));
        overallButton.setOnMouseExited(e -> overallButton.setFill(Color.rgb(60, 60, 60)));
        overallButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showDialogBoxesForHost("overall");
            }
        });

        entryButtons.add(overallButton);
        entryTexts.add(overallText);
        mainContainer.getChildren().addAll(overallButton, overallText);
        index++;

        // Add all hero names
        List<Characters.character> heroes = new ArrayList<>(Characters.CharacterRegistry.getAll());
        for (Characters.character ch : heroes) {
            String name = ch.getName();

            Rectangle button = new Rectangle(listBackground.getWidth() - 20, rowHeight - 4, Color.rgb(60, 60, 60));
            button.setStroke(Color.WHITE);
            button.setStrokeWidth(1);
            button.setTranslateX(startX);
            button.setTranslateY(startY + index * rowHeight);

            Text text = new Text(name);
            text.setFont(new Font(14));
            text.setFill(Color.WHITE);
            text.setMouseTransparent(true);
            text.setTranslateX(startX + 10);
            text.setTranslateY(startY + index * rowHeight + 20);

            button.setOnMouseEntered(e -> button.setFill(Color.rgb(80, 80, 80)));
            button.setOnMouseExited(e -> button.setFill(Color.rgb(60, 60, 60)));

            final String heroName = name;
            button.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    showDialogBoxesForHost(heroName);
                }
            });

            entryButtons.add(button);
            entryTexts.add(text);
            mainContainer.getChildren().addAll(button, text);

            index++;
        }
    }

    /**
     * Show dialog boxes for a specific host ("overall" or hero name).
     * If host is a hero name, show dialogs where hostName matches.
     * If host is "overall", show dialogs where hostName doesn't match any hero name.
     */
    private void showDialogBoxesForHost(String hostName) {

        // Remove old dialog boxes
        clearDialogBoxes();

        // Get all hero names to check if host is a hero
        List<String> heroNames = new ArrayList<>();
        for (Characters.character ch : Characters.CharacterRegistry.getAll()) {
            heroNames.add(ch.getName());
        }

        List<DialogBox> boxesToShow = new ArrayList<>();
        java.util.Set<DialogBox> seenBoxes = new java.util.HashSet<>(); // Track seen boxes to avoid duplicates

        if ("overall".equals(hostName)) {
            // Show dialogs where hostName doesn't match any hero name
            List<String> allHosts = DialogBoxRegistry.getAllHosts();
            for (String host : allHosts) {
                if (!heroNames.contains(host)) {
                    for (DialogBox box : DialogBoxRegistry.getByHost(host)) {
                        if (!seenBoxes.contains(box)) {
                            boxesToShow.add(box);
                            seenBoxes.add(box);
                        }
                    }
                }
            }
            // Also show dialogs explicitly registered as "overall"
            for (DialogBox box : DialogBoxRegistry.getByHost("overall")) {
                if (!seenBoxes.contains(box)) {
                    boxesToShow.add(box);
                    seenBoxes.add(box);
                }
            }
        } else {
            // Show dialogs where hostName matches the selected hero
            boxesToShow.addAll(DialogBoxRegistry.getByHost(hostName));
        }

        // Display dialog boxes in a grid
        double startX = dialogBoxArea.getTranslateX() + 10;
        double startY = dialogBoxArea.getTranslateY() + 10;
        double boxWidth = 200;
        double boxHeight = 40;
        double spacingX = 20;
        double spacingY = 20;
        int boxesPerRow = 2;

        for (int i = 0; i < boxesToShow.size(); i++) {
            DialogBox box = boxesToShow.get(i);
            int row = i / boxesPerRow;
            int col = i % boxesPerRow;

            double x = startX + col * (boxWidth + spacingX);
            double y = startY + row * (boxHeight + spacingY);

            box.setPosition(x, y);
            box.updateClickability(); // Update clickability based on current status

            // Only add if not already in the scene
            if (box.getContainer().getParent() == null) {
                mainContainer.getChildren().add(box.getContainer());
            }
            currentDialogBoxes.add(box);
        }
    }

    /**
     * Clear all currently displayed dialog boxes.
     */
    private void clearDialogBoxes() {
        for (DialogBox box : currentDialogBoxes) {
            mainContainer.getChildren().remove(box.getContainer());
        }
        currentDialogBoxes.clear();
    }

    /**
     * Hide all skill buttons and their texts.
     */
    private void hideAllSkillButtons() {
        if (tlButton != null) {
            tlButton.setVisible(false);
            tlButtonText.setVisible(false);
            s1Button.setVisible(false);
            s1ButtonText.setVisible(false);
            s2Button.setVisible(false);
            s2ButtonText.setVisible(false);
            s3Button.setVisible(false);
            s3ButtonText.setVisible(false);
            s4Button.setVisible(false);
            s4ButtonText.setVisible(false);
            s5Button.setVisible(false);
            s5ButtonText.setVisible(false);
            s6Button.setVisible(false);
            s6ButtonText.setVisible(false);
            s7Button.setVisible(false);
            s7ButtonText.setVisible(false);
            s8Button.setVisible(false);
            s8ButtonText.setVisible(false);
        }
    }

    /**
     * Update clickability of all displayed dialog boxes.
     * Call this when testing.status changes.
     */
    public void updateDialogBoxClickability() {
        for (DialogBox box : currentDialogBoxes) {
            box.updateClickability();
        }
    }
}


