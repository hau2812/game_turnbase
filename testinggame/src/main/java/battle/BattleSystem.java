package battle;

import abilities.Ability;
import audio.AudioManager;
import characters.BuffDebuff;
import characters.Characters;
import characters.Observer;
import javafx.scene.shape.Line;
import ui.SimpleLine;
import javafx.util.Duration;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BattleSystem {
    
    // Configuration
    
    // Character slots
    private Observer.characterSlot heroSlot;
    private Observer.characterSlot heroSlot2;
    private Observer.characterSlot heroSlot3;
    private Observer.characterSlot enemySlot;
    private Observer.characterSlot enemySlot2;
    private Observer.characterSlot enemySlot3;
    private Observer.characterSlot selectedTarget;
    private Observer.characterSlot selectedAllyTarget;
    private Observer.characterSlot selectedEnemyTarget;
    private Observer.characterSlot currentActingHero;
    
    // Combat state
    private boolean moving = false;
    private boolean autoEnemy = true;
    private boolean enemyActionTime = false;
    private Line turnOf = null;
    private Observer.characterSlot timeStopSlot = null;
    private float timeStop = 0;
    public float partyMp=0;
    public float maxPartyMp=500;
    /**
     * Get the current party MP value
     * @return Current party MP
     */
    public float getPartyMp() {
        return partyMp;
    }
    
    /**
     * Set the party MP value
     * @param partyMp New party MP value
     */
    public void setPartyMp(float partyMp) {
        this.partyMp = Math.min(partyMp,maxPartyMp);
        // Update the party MP bar in the UI
        if (battleUI != null) {
            battleUI.updatePartyMpBar();
        }
    }
    
    // Speed settings
    private double lineSpeed = 0.5;
    private double blueLineSpeed = lineSpeed;
    private double redLineSpeed = lineSpeed;
    private double yellowLineSpeed = lineSpeed;
    private double greenLineSpeed = lineSpeed;
    private double purpleLineSpeed = lineSpeed;
    private double orangeLineSpeed = lineSpeed;
    
    // UI reference (will be injected)
    private BattleUI battleUI;
    private boolean hideTalents = false;
    private String[] selectedHeroes = {"Flamita"}; // Default heroes
    // Callback for battle end
    private Runnable onBattleWon;
    
    // Audio system
    public static AudioManager audioManager;
    
    // Flag to control battle loop
    private boolean battleLoopActive = false;
    
    public BattleSystem() {
        this.audioManager = AudioManager.getInstance();
        // Set the BattleSystem reference in Observer for party MP management
        Observer.setBattleSystem(this);
    }
    
    // ===================== HELPER FUNCTIONS =====================
    
    /**
     * Check if a character slot is a hero
     */
    public boolean isHero(Observer.characterSlot slot) {
        return slot == heroSlot || slot == heroSlot2 || slot == heroSlot3;
    }
    
    /**
     * Check if a character slot is an enemy
     */
    public boolean isEnemy(Observer.characterSlot slot) {
        return slot == enemySlot || slot == enemySlot2 || slot == enemySlot3;
    }
    
    /**
     * Get all hero slots as an array
     */
    public Observer.characterSlot[] getAllHeroes() {
        return new Observer.characterSlot[]{heroSlot, heroSlot2, heroSlot3};
    }
    
    /**
     * Get all enemy slots as an array
     */
    public Observer.characterSlot[] getAllEnemies() {
        return new Observer.characterSlot[]{enemySlot, enemySlot2, enemySlot3};
    }
    
    /**
     * Get a random alive enemy
     * @return A random alive enemy, or null if no enemies are alive
     */
    private Observer.characterSlot getRandomAliveEnemy() {
        Observer.characterSlot[] allEnemies = getAllEnemies();
        java.util.List<Observer.characterSlot> aliveEnemies = new java.util.ArrayList<>();
        
        // Filter out null and dead enemies
        for (Observer.characterSlot enemy : allEnemies) {
            if (enemy != null && enemy.getCurrentHp() > 0) {
                aliveEnemies.add(enemy);
            }
        }
        
        if (aliveEnemies.isEmpty()) {
            return null;
        }
        
        Random random = new Random();
        return aliveEnemies.get(random.nextInt(aliveEnemies.size()));
    }
    
    /**
     * Get all character slots as an array
     */
    private Observer.characterSlot[] getAllCharacters() {
        return new Observer.characterSlot[]{heroSlot, heroSlot2, heroSlot3, enemySlot, enemySlot2, enemySlot3};
    }
    
    /**
     * Get the corresponding line for a character slot
     */
    private Line getLineForCharacter(Observer.characterSlot slot) {
        if (slot == heroSlot) return battleUI.getBlueLine();
        if (slot == heroSlot2) return battleUI.getGreenLine();
        if (slot == heroSlot3) return battleUI.getPurpleLine();
        if (slot == enemySlot) return battleUI.getRedLine();
        if (slot == enemySlot2) return battleUI.getYellowLine();
        if (slot == enemySlot3) return battleUI.getOrangeLine();
        return null;
    }
    
    /**
     * Get the corresponding speed for a character slot
     */
    private double getSpeedForCharacter(Observer.characterSlot slot) {
        if (slot == heroSlot) return blueLineSpeed;
        if (slot == heroSlot2) return greenLineSpeed;
        if (slot == heroSlot3) return purpleLineSpeed;
        if (slot == enemySlot) return redLineSpeed;
        if (slot == enemySlot2) return yellowLineSpeed;
        if (slot == enemySlot3) return orangeLineSpeed;
        return lineSpeed;
    }
    
    /**
     * Push a character's line back by the specified amount
     */
    public void pushCharacterLine(Observer.characterSlot character, double pushAmount) {
        Line characterLine = getLineForCharacter(character);
        if (characterLine != null) {
            double barX = battleUI.getBarX();
            double barWidth = battleUI.getBarWidth();
            double newX = Math.min(barX + barWidth, characterLine.getStartX() + pushAmount);
            characterLine.setStartX(newX);
            characterLine.setEndX(newX);
        }
    }

    /**
     * Remove the line for a character slot when they die
     */
    private void removeCharacterLine(Observer.characterSlot slot) {
        Line characterLine = getLineForCharacter(slot);
        if (characterLine != null) {
            getGameScene().removeUINode(characterLine);
            battleUI.setLineForCharacter(slot, null);
        }
    }
    
    /**
     * Get a random alive hero as target
     */
    private Observer.characterSlot getRandomAliveHero() {
        Observer.characterSlot[] heroes = getAllHeroes();
        java.util.List<Observer.characterSlot> aliveHeroes = new java.util.ArrayList<>();
        
        for (Observer.characterSlot hero : heroes) {
            if (hero != null && hero.getCurrentHp() > 0) {
                aliveHeroes.add(hero);
            }
        }
        
        if (aliveHeroes.isEmpty()) {
            return null;
        }
        
        Random random = new Random();
        return aliveHeroes.get(random.nextInt(aliveHeroes.size()));
    }
    public void resetLine(Observer.characterSlot slot) {
        double barX = battleUI.getBarX();
        slot.getLine().setStartX(barX);
        slot.getLine().setEndX(barX);
    }
    
    /**
     * Handle special skills that require unique processing
     * @param attacker The character using the skill
     * @param target The target of the skill
     * @param skill The skill being used
     * @return true if skill processing should end early, false to continue with normal processing
     */
    private boolean handleSpecialSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        // Handle "Let me absorb you" skill
        if(skill.getName().equals("Let me absorb you")){
            if(timeStopSlot != null){
                if(timeStopSlot.getCharacter().getName().equals("Ina")) {
                    if (target.getCurrentHp() <= target.getCharacter().getHp() * 0.4 || target.getCharacter() == attacker.getCharacter()) {
                        timeStop += (float) 0.5;
                        resetLine(attacker);
                    } else {
                        target.heal(-target.getCharacter().getHp() * (float)0.4);
                        battleUI.updateHealthUI(target);
                        timeStop = Math.min(200, timeStop + 100);
                        resetLine(attacker);
                    }
                    return true; // End skill processing early
                }
            } else {
                target.heal(-target.getCurrentHp() / 2);
                attacker.regenerateMp(3);
            }
            battleUI.updateHealthUI(target);
            battleUI.updateBurningRageBar(target);
            return true; // End skill processing early
        }
        
        // Handle "Absolute teleportation" skill
        else if(skill.getName().equals("Absolute teleportation")){
            //Buff
            BuffDebuff buff = BuffDebuff.getByName("Conserve").copy();
            buff.setStack(attacker.getBuffCount()+1);
            attacker.addBuffDebuff(buff);
            attacker.regenerateMp(-skill.getMpCost());
            // Set timeStopSlot to attacker and initialize timeStop with black action bar length
            timeStopSlot = attacker;
            timeStop = (float)battleUI.getBarWidth();
            // Create the timeStop bar in the UI
            battleUI.createTimeStopBar();
            return true; // End skill processing early
        }else if(skill.getName().equals("Moon wave")){
            resetLine(target);
            battleUI.updateHealthUI(target);
            battleUI.updateBurningRageBar(target);
            return false; // End skill processing early
        }
        
        // No special skill handled, continue with normal processing
        return false;
    }
    
    /**
     * Execute AOE (Area of Effect) skill that targets multiple characters
     * @param attacker The character using the skill
     * @param skill The AOE skill being used
     */
    public void executeAoeSkill(Observer.characterSlot attacker, Ability.skill skill) {
        String targetType = skill.getTarget();
        
        // Handle special case for "Family united" skill
        
        if ("Aoe enemy".equals(targetType)) {
            // If attacker is enemy, target all heroes; if attacker is hero, target all enemies
            if (isEnemy(attacker)) {
                // Enemy attacking heroes
                for (Observer.characterSlot hero : getAllHeroes()) {
                    if (hero != null && hero.getCurrentHp() > 0) {
                        useSkill(attacker, hero, skill);
                    }
                }
                // Special handling for "Oufuu atk up" removal after AOE enemy attack
                if (attacker.getBuffDebuffByName("Oufuu atk up") != null) {
                    attacker.getActiveEffects().remove(attacker.getBuffDebuffByName("Oufuu atk up"));
                    enemySlot.setCurrentHp(enemySlot.getCharacter().getHp());
                    enemySlot3.setCurrentHp(enemySlot.getCharacter().getHp());
                    battleUI.refreshAllCharacterUI();
                }
            } else {
                // Hero attacking enemies
                for (Observer.characterSlot enemy : getAllEnemies()) {
                    if (enemy != null && enemy.getCurrentHp() > 0) {
                        useSkill(attacker, enemy, skill);
                    }
                }
            }
        } else if ("Aoe ally".equals(targetType)) {
            // If attacker is enemy, target all enemies; if attacker is hero, target all heroes
            if (isEnemy(attacker)) {
                // Enemy targeting allies (other enemies)
                for (Observer.characterSlot enemy : getAllEnemies()) {
                    if (enemy != null && enemy.getCurrentHp() > 0) {
                        useSkill(attacker, enemy, skill);
                    }
                }
            } else {
                // Hero targeting allies (other heroes)
                for (Observer.characterSlot hero : getAllHeroes()) {
                    if (hero != null && hero.getCurrentHp() > 0) {
                        useSkill(attacker, hero, skill);
                    }
                }
            }
        }
    }
    
    /**
     * Check if any hero has Void burn debuff with specified minimum stacks
     */
    private boolean hasHeroWithVoidBurn(int minStacks) {
        for (Observer.characterSlot hero : getAllHeroes()) {
            if (hero != null && hero.getCurrentHp() > 0) {
                for (characters.BuffDebuff debuff : hero.getActiveEffects()) {
                    if ("Void burn".equals(debuff.getName()) && debuff.getStack() >= minStacks) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean hasIna(){
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                if (hero.getCharacter().getName().equals("Ina")) {
                    return true;
                }
            }

        }
        return false;
    }
    public Observer.characterSlot getSlotByName(String name) {
        for (Observer.characterSlot Slot : getAllCharacters()) {
            if(Slot!=null){
                if(Slot.getCharacter().getName().equals(name)){
                    return Slot;
            }

            }
        }
        return null;
    }
    
    public void setBattleUI(BattleUI battleUI) {
        this.battleUI = battleUI;
        // Pass hideTalents setting to BattleUI
        battleUI.setHideTalents(hideTalents);
        // Set BattleUI reference in SpecialTalents for static method access
        characters.SpecialTalents.setBattleUI(battleUI);

    }
    
    /**
     * Configure battle settings
     * @param hideTalents Whether to hide talent text in UI
     * @param selectedHeroes Array of hero names to use
     */
    public void configureBattle(boolean hideTalents, String[] selectedHeroes) {
        this.hideTalents = hideTalents;
        this.selectedHeroes = selectedHeroes.clone(); // Create a copy to avoid external modification
        
        // Update BattleUI if it's already set
        if (battleUI != null) {
            battleUI.setHideTalents(hideTalents);
        }
    }
    
    public boolean getHideTalents() {
        return this.hideTalents;
    }
    
    public String[] getSelectedHeroes() {
        return selectedHeroes.clone(); // Return a copy to avoid external modification
    }
    
    public String getSelectedHero() {
        // For backward compatibility, return the first selected hero
        return selectedHeroes.length > 0 ? selectedHeroes[0] : "Flamita";
    }
    
    public void setOnBattleWon(Runnable callback) {
        this.onBattleWon = callback;
    }

    private static final Map<String, Runnable> SPECIAL_MUSIC = Map.of(
            "Flamita ?", () -> audioManager.playFlamitaMusic(),
            "Mabel", () -> audioManager.playMabelMusic()
            //"Electra", () -> audioManager.playElectraMusic()
    );
    public void playBattleMusic() {
//        if(true){
//            return;
//        }
        for (Observer.characterSlot enemy : getAllEnemies()) {

            if (enemy!=null) {
                String name = enemy.getCharacter().getName();
                Runnable action = SPECIAL_MUSIC.get(name);
                if (action != null) {
                    action.run();
                    return; // Stop after first match
                }
            }else{
                break;
            }

        }
        audioManager.playBattleMusic();
    }

    public void initializeBattle() {

        // Start battle music
        playBattleMusic();
        // Configure which characters to create
        
        // Init registry
        Observer.CharacterSlotRegistry.init();
        timeStop=0;
        timeStopSlot=null;



        // Hero slots - use configured heroes
        if(heroSlot == null && selectedHeroes.length > 0) {
            heroSlot = Observer.CharacterSlotRegistry.getByName(selectedHeroes[0]);
        }

        if(heroSlot2 == null && selectedHeroes.length > 1) {
            heroSlot2 = Observer.CharacterSlotRegistry.getByName(selectedHeroes[1]);
        }

        if(heroSlot3 == null && selectedHeroes.length > 2) {
            heroSlot3 = Observer.CharacterSlotRegistry.getByName(selectedHeroes[2]);
        }
        //Where's your mana Ina
        if(hasIna()){
            getSlotByName("Ina").setCurrentMp(5);
        }
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

        SimpleLine attackerLine = attacker.getLine();
        if(attackerLine == null || turnOf == null || 
           attackerLine.getFxLine() != turnOf) {
            return;
        }
        
        // Handle special skills - if returns true, end skill processing early
        if (handleSpecialSkill(attacker, target, skill)) {
            return;
        }

        // Play skill sound effect based on skill type
        if (skill.getType().equals("Physical")) {
            audioManager.playSwordSlash();
        } else if (skill.getType().equals("Magic")) {
            audioManager.playMagicCast();
        } else if (skill.getType().equals("Heal")) {
            audioManager.playHealCast();
        }
        
        // Check MP for heroes and deduct if needed
        if (true) {
//            float mpCost = skill.getMpCost();
//            if (mpCost > 0 && attacker.getCurrentMp() < mpCost) {
//                return; // not enough MP
//            }
            if (true) {

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
        double baseDamage = attacker.getCharacter().getAtk() * skill.getAtkScale();
        double burnBonus = 0;
        // Handle Burning Rage effects and validation
        if(attacker.getCharacter().getUniqueValue("Burning rage")!=null){
            float rageConsumed = handleBurningRageEffects(attacker, skill);
            burnBonus = calculateDamageWithRage(rageConsumed, skill, attacker);
        }
        double talentBonus = calculateDamageWithTalentBonus(skill, attacker);
        double specialDmgBonus = calculateSpecialDmgBonus(attacker, skill);
        double finalDmg = baseDamage+burnBonus+talentBonus+specialDmgBonus;
        // Calculate final damage with Burning Rage bonuses, talent bonuses, and special skill bonuses
        applyDamage(target, finalDmg);
        // Delay hit effect animation by 0.5 seconds
        if (battleUI != null && target != null) {
            Observer.characterSlot finalTarget = target;
            runOnce(() -> {
                battleUI.playHitEffect(finalTarget, skill);
            }, Duration.millis(500));
        }

        // Apply skill effects (buffs/debuffs) to target
        if (skill.getSkillEffects() != null && !skill.getSkillEffects().isEmpty()) {
            for (Ability.skillEffect effect : skill.getSkillEffects()) {
                // Create a copy of the BuffDebuff with the skill's duration and stack
                BuffDebuff effectCopy = effect.getBuffDebuff().copy();
                effectCopy.setStack(effect.getStack());
                effectCopy.setDuration(effect.getDuration());
                // Apply the effect to the target
                characters.SpecialTalents.applyBuffDebuff(target, effectCopy);
            }
        }
        
        // Update barrier bar for target if effects were applied
        if (battleUI != null && skill.getSkillEffects() != null && !skill.getSkillEffects().isEmpty()) {
            battleUI.updateBarrierBar(target);
        }


        if (isEnemy(attacker)) {
            attacker.setCurrentMp(Math.min(attacker.getCharacter().getMp(),attacker.getCurrentMp()-skill.getMpCost()));
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            pushCharacterLine(attacker, push);
        }
        // Reduce duration of buff/debuff effects at the end of turn
        characters.SpecialTalents.onTurnEnd(attacker);
    }
    
    /**
     * Execute enemy turn with action time protection
     * @param enemy The enemy character slot to execute turn for
     */
    private void executeEnemyTurn(Observer.characterSlot enemy) {
        if (enemy == null || enemy.getCurrentHp() <= 0) {
            return;
        }
        
        enemyActionTime = true; // Prevent hero skill usage during enemy action
        runOnce(() -> {
            enemyTurn(enemy);
            enemyActionTime = false; // Re-enable hero skill usage after enemy action
        }, Duration.seconds(0.25));
    }
    
    private void enemyTurn(Observer.characterSlot actingEnemy) {
        Random random = new Random();
        if (actingEnemy == null || actingEnemy.getCurrentHp() <= 0) {
            moving = true;
            return;
        }
        
        // Apply turn-based effects (like regeneration)
        characters.SpecialTalents.onTurnStart(actingEnemy);
        if(actingEnemy.getCurrentHp()==0){
            battleUI.updateHealthUI(actingEnemy);
            removeCharacterLine(actingEnemy);
            checkVictoryCondition();
            moving = true;
            return;
        }

        // Update UI after turn-based effects
        if (battleUI != null) {
            battleUI.updateHealthUI(actingEnemy);
        }
        
        // Filter skills based on MP availability
        ArrayList<Ability.skill> availableSkills = new ArrayList<>();
        
        // If enemy has MP > 0, filter skills by MP cost
        if (actingEnemy.getCharacter().getMp() > 0) {
            for (Ability.skill skill : actingEnemy.getSkills()) {
                // Check if skill is not null
                if (skill == null) continue;
                
                // Special condition for Eternal darkness skill
                if (skill.getName().equals("Eternal darkness")) {
                    if (hasHeroWithVoidBurn(10)) {
                        availableSkills.add(skill);
                    }
                }else if(skill.getName().equals("Family united")){
                    if ((int)actingEnemy.getCurrentHp() == 1) {
                        availableSkills.add(skill);
                    }

                }else if(skill.getName().equals("Daddy fury")){
                    if((int)actingEnemy.getFloatBuffDebuffByName("Oufuu atk up")>=BuffDebuff.getByName("Oufuu atk up").getMaxStack()){
                        availableSkills.add(skill);
                    }
                }else {
                    // Normal MP cost check for other skills
                    float mpCost = skill.getMpCost();
                    if (mpCost <= 0 || actingEnemy.getCurrentMp() >= mpCost) {
                        availableSkills.add(skill);
                    }
                }
            }
        } else {
            // If enemy has no MP, only skills with negative or zero MP cost are available
            for (Ability.skill skill : actingEnemy.getSkills()) {
                // Check if skill is not null
                if (skill == null) continue;
                
                if (skill.getMpCost() <= 0) {
                    availableSkills.add(skill);
                }
            }
        }
        
        // If no skills are available, try to give basic attack
        if (availableSkills.isEmpty()) {
            // Fallback: try to give basic attack (Slash)
            Ability.skill basicAttack = Ability.SkillRegistry.getById(1);
            if (basicAttack != null) {
                Observer.characterSlot targetHero = getRandomAliveHero();
                if (targetHero != null) {
                    useSkill(actingEnemy, targetHero, basicAttack);
                }
            }
            moving = true;
            return;
        }
        
        // Choose random skill from available skills
        Ability.skill chosenSkill = availableSkills.get(random.nextInt(availableSkills.size()));
        
        // Check if chosen skill is valid
        if (chosenSkill == null) {
            moving = true;
            return;
        }
        
        // Pick random living hero as target if offensive
        if (chosenSkill.getAtkScale() < 0||chosenSkill.getTarget().equals("Self")) {
            useSkill(actingEnemy, actingEnemy, chosenSkill);
        }else if(chosenSkill.getName().equals("Family united")){
            useSkill(actingEnemy, enemySlot2, chosenSkill);
        }else if(chosenSkill.getTarget().equals("Aoe enemy") || chosenSkill.getTarget().equals("Aoe ally")){
            executeAoeSkill(actingEnemy, chosenSkill);
        }else {
            Observer.characterSlot targetHero = getRandomAliveHero();
            if (targetHero == null) {
                // No heroes alive, shouldn't happen but handle gracefully
                moving = true;
                return;
            }
            useSkill(actingEnemy, targetHero, chosenSkill);
        }
        moving = true;
//        System.out.println("SPD: "+actingEnemy.getCharacter().getSpd());
//        System.out.println("AV: "+actingEnemy.getCharacter().getAV());
//        System.out.println("MP: "+actingEnemy.getCurrentMp());
//        System.out.println(actingEnemy.getCharacter());
//        System.out.println(actingEnemy.getBaseCharacter());
//        System.out.println(actingEnemy.getActiveEffects());
    }
    
    private void updateLines() {
        if (!moving || battleUI == null) {
            return;
        }
        if(enemySlot2!=null) {
            if (enemySlot2.getCharacter().getName().equals("Oufuu daddy") && (int) enemySlot2.getCurrentHp() == 0) {
                enemySlot.setCurrentHp(0);
                enemySlot3.setCurrentHp(0);
                checkVictoryCondition();
            }
        }
        
        // Handle timeStop mechanics
        if (timeStop > 0) {
            //For Ina only
            if(timeStop == 1&&timeStopSlot.getCharacter().getName().equals("Ina")){
                executeAoeSkill(timeStopSlot,Ability.SkillRegistry.getById(26));
                timeStopSlot.getActiveEffects().remove(timeStopSlot.getBuffDebuffByName("Conserve"));
                timeStopSlot.getActiveEffects().remove(timeStopSlot.getBuffDebuffByName("Judgment"));
            }

            // Update timeStop bar
            battleUI.updateTimeStopBar(timeStop);
            timeStop -= lineSpeed;
            
            // Only timeStopSlot line can move during timeStop
            if (timeStopSlot != null) {
                Line timeStopLine = getLineForCharacter(timeStopSlot);
                if (timeStopLine != null) {
                    double speed = getSpeedForCharacter(timeStopSlot);
                    moveLine(timeStopLine, speed);
                }
            }
            
            // Clear timeStop when it reaches 0
            if (timeStop <= 0) {
                timeStop = 0;
                timeStopSlot = null;
                battleUI.removeTimeStopBar();
            }
        } else {
            // Normal line movement when not in timeStop
            Observer.characterSlot[] allCharacters = getAllCharacters();
            for (Observer.characterSlot character : allCharacters) {
                if (character != null) {
                    Line characterLine = getLineForCharacter(character);
                    if (characterLine != null) {
                        // For enemies, only move if they're alive
                        if (isEnemy(character) && character.getCurrentHp() <= 0) {
                            continue;
                        }
                        double speed = getSpeedForCharacter(character);
                        moveLine(characterLine, speed);
                    }
                }
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
                    executeEnemyTurn(enemySlot);
                }
                // Enemy 2 turn
                else if (line == battleUI.getYellowLine() && enemySlot2 != null && enemySlot2.getCurrentHp() > 0) {
                    executeEnemyTurn(enemySlot2);
                }
                // Enemy 3 turn
                else if (line == battleUI.getOrangeLine() && enemySlot3 != null && enemySlot3.getCurrentHp() > 0) {
                    executeEnemyTurn(enemySlot3);
                }
                else if (line == battleUI.getBlueLine() || (line == battleUI.getGreenLine() && heroSlot2 != null) || (line == battleUI.getPurpleLine() && heroSlot3 != null)) {
                    // When a hero's line reaches, render that hero's skills

                    if (line == battleUI.getBlueLine()) {
                        currentActingHero = heroSlot;
                    } else if (line == battleUI.getGreenLine()) {
                        currentActingHero = heroSlot2;
                    } else if (line == battleUI.getPurpleLine()) {
                        currentActingHero = heroSlot3;
                    }
                    characters.SpecialTalents.onTurnStart(currentActingHero);
                    if(currentActingHero.getCurrentHp()==0){
                        battleUI.updateHealthUI(currentActingHero);
                        removeCharacterLine(currentActingHero);
                        moving = true;
                    }
                    battleUI.refreshAllCharacterUI();
                    if (currentActingHero != null && battleUI != null) {
                        battleUI.renderHeroSkillsFor(currentActingHero);
                    }
                }
            }
            if(timeStopSlot != null) {
                double push = timeStopSlot.getCharacter().getAV() * timeStopSlot.getSkills().get(0).getAVScale();
                if(timeStopSlot.getCharacter().getName().equals("Ina")&&timeStop>push){
                    useSkill(timeStopSlot,getRandomAliveEnemy(),timeStopSlot.getSkills().get(0));
                    pushCharacterLine(timeStopSlot, push);
                    moving = true;
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
            // Play damage sound effect
            audioManager.playDamageTaken();
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
            battleUI.updateBurningRageBar(slot); // Update Burning Rage bar
            battleUI.updateBarrierBar(slot); // Update Barrier bar
            
            // Remove line if character dies
            if (slot.getCurrentHp() <= 0) {
                removeCharacterLine(slot);
                
                // Check if all enemies are defeated
                checkVictoryCondition();
            }
        }
    }
    
    // Getters for external access
    public Observer.characterSlot getHeroSlot() { return heroSlot; }
    public Observer.characterSlot getHeroSlot2() { return heroSlot2; }
    public Observer.characterSlot getHeroSlot3() { return heroSlot3; }
    public Observer.characterSlot getEnemySlot() { return enemySlot; }
    public Observer.characterSlot getEnemySlot2() { return enemySlot2; }
    public Observer.characterSlot getEnemySlot3() { return enemySlot3; }
    public Observer.characterSlot getSelectedTarget() { return selectedTarget; }
    public Observer.characterSlot getSelectedAllyTarget() { return selectedAllyTarget; }
    public Observer.characterSlot getSelectedEnemyTarget() { return selectedEnemyTarget; }
    public Observer.characterSlot getCurrentActingHero() { return currentActingHero; }
    
    public void setSelectedTarget(Observer.characterSlot target) { this.selectedTarget = target; }
    public void setSelectedAllyTarget(Observer.characterSlot target) { this.selectedAllyTarget = target; }
    public void setSelectedEnemyTarget(Observer.characterSlot target) { this.selectedEnemyTarget = target; }
    public void setMoving(boolean moving) { this.moving = moving; }
    public boolean isMoving() { return moving; }
    public boolean isEnemyActionTime() { return enemyActionTime; }
    public Line getTurnOf() { return turnOf; }
    
    public void setMapEnemies(Observer.characterSlot enemy1, Observer.characterSlot enemy2, Observer.characterSlot enemy3) {
        // Clear old enemy data first
        this.enemySlot = null;
        this.enemySlot2 = null;
        this.enemySlot3 = null;
        
        // Update enemy slots with map enemies
        if (enemy1 != null) {
            this.enemySlot = enemy1;
        }
        if (enemy2 != null) {
            this.enemySlot2 = enemy2;
        }
        if (enemy3 != null) {
            this.enemySlot3 = enemy3;
        }

        // Update selected target to first enemy
        this.selectedTarget = enemy1;
        this.selectedEnemyTarget = enemy1;
    }
    
    public void clearEnemyData() {
        // Clear all enemy data
        this.enemySlot = null;
        this.enemySlot2 = null;
        this.enemySlot3 = null;
        this.selectedTarget = null;
        this.selectedAllyTarget = null;
        this.selectedEnemyTarget = null;
        System.out.println("Enemy data cleared");
    }
    
    private void checkVictoryCondition() {
        // Check if all enemies are defeated
        Observer.characterSlot[] enemies = getAllEnemies();
        boolean allEnemiesDefeated = true;
        
        for (Observer.characterSlot enemy : enemies) {
            if (enemy != null && enemy.getCurrentHp() > 0) {
                allEnemiesDefeated = false;
                break;
            }
        }
        
        if (allEnemiesDefeated) {
            System.out.println("Victory! All enemies defeated!");
            // Play victory music
            audioManager.playVictoryMusic();
            if (onBattleWon != null) {
                onBattleWon.run();
            }
        }
    }
//------------------------------------------------BURNING RAGE------------------------------------------------------------------
    /**
     * Handle all Burning Rage interactions for a skill
     * @param attacker The character using the skill
     * @param skill The skill being used
     * @return The amount of rage consumed (for damage calculation), or -1 if skill cannot be used
     */
    private float handleBurningRageEffects(Observer.characterSlot attacker, Ability.skill skill) {
        float rageRequired = skill.getBurningRageRequired();
        float rageConsumed = skill.getBurningRageConsumed();
        float rageGained = skill.getBurningRageGained();
        
        // Check if character has enough Burning Rage
        if(rageConsumed > attacker.getCharacter().getUniqueValueAsFloat("Burning rage")) {
            rageConsumed = attacker.getCharacter().getUniqueValueAsFloat("Burning rage");
        }
        attacker.getCharacter().addToUniqueValue("Burning rage",-rageConsumed);
        // Gain Burning Rage after using skill
        if (rageGained > 0) {
            characters.SpecialTalents.gainBurningRage(attacker, rageGained);

        }
        
        // Update Burning Rage bar if it changed
        if (rageConsumed > 0 || rageGained > 0) {
            if (battleUI != null) {
                battleUI.updateBurningRageBar(attacker);
            }
        }
        
        return rageConsumed; // Return amount consumed for damage calculation
    }
    
    /**
     * Calculate damage with Burning Rage bonuses
     * @param rageConsumed The amount of Burning Rage consumed
     * @param skill The skill being used
     * @param attacker The character using the skill
     * @return The final damage amount
     */
    private double calculateDamageWithRage(float rageConsumed, Ability.skill skill, Observer.characterSlot attacker) {
        double dmg=0;

        // Special damage calculation for Burning Rage skills
        if (rageConsumed > 0) {
            if (skill.getName().equals("Rage Heal")) {
                if(attacker.getCharacter().getName().equals("Flamita ?")) {
                    float healingAmount = (float) (rageConsumed * 0.9);
                    dmg = -healingAmount; // Negative damage = healing
                    System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + "! Consumed " + rageConsumed + " rage to heal for " + healingAmount + " HP!");
                }else {
                    // Rage Heal: healing = rage * (rage / (rage + currentHP))
                    float currentHp = attacker.getCurrentHp();
                    float healingAmount = rageConsumed * (rageConsumed / (rageConsumed + currentHp));
                    dmg = -healingAmount; // Negative damage = healing
                    System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + "! Consumed " + rageConsumed + " rage to heal for " + healingAmount + " HP!");
                }
            } else if (skill.getName().equals("Rage Burst")) {
                float currentHp = Math.max(100, attacker.getCurrentHp());
                float maxHp = attacker.getCharacter().getHp();
                dmg = 3 * maxHp * (rageConsumed / (10*currentHp + rageConsumed));
                float heal = (float)dmg/2;
                attacker.heal(heal);
                battleUI.updateHealthUI(attacker);
                System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + "! Consumed " + rageConsumed + " rage for " + dmg + " damage!");
            } else {
                // Other rage skills: add rage as bonus damage
                dmg += rageConsumed;
                System.out.println(attacker.getCharacter().getName() + " consumed " + rageConsumed + " rage for +" + rageConsumed+ " bonus damage!");
            }
        }
        return dmg;
    }
    private double calculateDamageWithTalentBonus(Ability.skill skill, Observer.characterSlot attacker) {
        double dmg=0;
        // Apply special talent bonuses
        float talentBonus = characters.SpecialTalents.getDamageBonus(attacker);
        if (talentBonus > 0) {
            dmg += talentBonus;
            System.out.println(attacker.getCharacter().getName() + " talent bonus: +" + talentBonus + " damage!");
        }
        return dmg;
    }

//------------------------------------------------BURNING RAGE------------------------------------------------------------------

    /**
     * Calculate special damage bonus for special skills
     * @param attacker The character using the skill
     * @param skill The skill being used
     * @return Special damage bonus amount
     */
    private float calculateSpecialDmgBonus(Observer.characterSlot attacker, Ability.skill skill) {
        float specialDmgBonus = 0;
        
        // Check if this is a special skill
        if (skill.getName().equals("Ecarr Vertel")) {
            // Ecarr Vertel: Reduce HP to 1 and MP to 0, add reduced amounts to damage bonus
            float currentHp = attacker.getCurrentHp();
            float currentMp = attacker.getCurrentMp();
            
            // Calculate how much HP and MP will be reduced
            float hpReduction = Math.max(0, currentHp - 1);
            float mpReduction = currentMp; // Reduce all MP to 0
            
            // Add the reduced amounts to special damage bonus
            specialDmgBonus = hpReduction + mpReduction;
            
            // Apply the HP and MP reduction
            attacker.setCurrentHp(1); // Set HP to 1
            attacker.regenerateMp(-currentMp); // Set MP to 0
            
            System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + 
                             "! HP: " + currentHp + " -> 1, MP: " + currentMp + " -> 0, " +
                             "Special Damage Bonus: +" + specialDmgBonus);
            battleUI.refreshAllCharacterUI();
        }
        else if (skill.getName().equals("Eternal darkness")) {
            for(Observer.characterSlot hero : getAllHeroes()){
                if (hero != null && hero.getCurrentHp() > 0) {
                    characters.BuffDebuff voidBurn = hero.getBuffDebuffByName("Void burn");
                    if (voidBurn != null) {
                        // Apply damage equal to Void burn's total value
                        float dmg = voidBurn.getTotalValue()*voidBurn.getDuration();
                        applyDamage(hero, dmg);
                        
                        // Remove the Void burn debuff after applying damage
                        characters.SpecialTalents.removeBuffDebuff(hero, "Void burn");
                        
                        System.out.println("Eternal darkness consumed " + voidBurn.getStack() +" Burn last for "+ voidBurn.getDuration()+ " Void burn stacks for " + dmg + " damage on " + hero.getCharacter().getName() + "!");
                        
                        // Update UI to reflect changes
                        if (battleUI != null) {
                            battleUI.updateHealthUI(hero);
                        }
                    }
                }
            }
        } else if (skill.getName().equals("Head bump")){
            attacker.setCurrentHp(Math.max(1,attacker.getCurrentHp()-100));
            battleUI.updateHealthUI(attacker);
        }else if (skill.getId()==26){
            specialDmgBonus = attacker.getFloatBuffDebuffByName("Judgment")*attacker.getCharacter().getAtk()/2;
        }
        
        // Add more special skills here in the future
        // else if (skill.getName().equals("Another Special Skill")) {
        //     // Handle other special skills
        // }
        
        return specialDmgBonus;
    }

//------------------------------------------------SPECIAL SKILLS------------------------------------------------------------------
}
