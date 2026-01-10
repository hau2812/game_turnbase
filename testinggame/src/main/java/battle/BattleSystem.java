package battle;

import abilities.Ability;
import audio.AudioManager;
import characters.BuffDebuff;
import characters.Characters;
import characters.Observer;
import characters.SpecialTalents;
import dialog.DialogRegistrations;
import items.EquipmentItem;
import javafx.scene.shape.Line;
import ui.SimpleLine;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    public ArrayList<String> defeatEnemies = new ArrayList<>();

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
        this.partyMp = Math.max(0,Math.min(partyMp,maxPartyMp));
        // Update the party MP bar in the UI
        if (battleUI != null) {
            battleUI.updatePartyMpBar();
        }
    }

    public ArrayList<String> getDefeatEnemies() {
        return defeatEnemies;
    }
    public boolean containDefeatEnemies(String name){
        for(String deName: defeatEnemies){
            if(deName.equals(name)){
                return true;
            }
        }
        return false;
    }

    public void setDefeatEnemies(ArrayList<String> defeatEnemies) {
        this.defeatEnemies = defeatEnemies;
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
    private Runnable onBattleLost;
    
    // Audio system
    public static AudioManager audioManager;
    
    // Flag to control battle loop
    private boolean battleLoopActive = false;
    
    // Frame-rate independent battle loop timer
    private ScheduledExecutorService battleLoopExecutor;
    private static final long BATTLE_LOOP_INTERVAL_MS = 5; // 5ms = 200 updates per second
    
    // Track used skills per enemy for weighted skill selection
    // Map<enemyName, Map<skillId, usageCount>>
    private Map<String, Map<Integer, Integer>> enemySkillUsageCount = new HashMap<>();
    // Map<enemyName, Set<skillId>> - skills that have been used enough to affect selection
    private Map<String, Set<Integer>> enemyUsedSkillList = new HashMap<>();
    
    // Track total damage dealt to enemies for subBar movement (reset when CBUlt is cast)
    private double totalDamageToEnemies = 0.0;
    // Track how much damage has been "consumed" for subBar movement (to move only once per 100 damage)
    private double consumedDamageForSubBar = 0.0;
    
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

    public void removeAllHeroes() {
        heroSlot = null;
        heroSlot2 = null;
        heroSlot3 = null;
    }
    public int getIndex(Observer.characterSlot slot){
        if(slot == heroSlot){return 0;}
        else if(slot == heroSlot2){return 1;}
        else if(slot == heroSlot3){return 2;}
        else if(slot == enemySlot){return 3;}
        else if(slot == enemySlot2){return 4;}
        else if(slot == enemySlot3){return 5;}
        else return -1;
    }

    public void resetHeroes() {
        removeAllBuffsDebuffsFromHeroes();
        healToFullAllHeroes();
        timeStop = 0;
        timeStopSlot = null;
    }
    
    /**
     * Get all enemy slots as an array
     */
    public Observer.characterSlot[] getAllEnemies() {
        return new Observer.characterSlot[]{enemySlot, enemySlot2, enemySlot3};
    }
    /**
     * Get all alive enemy slots as an array
     */
    public ArrayList<Observer.characterSlot> getAllAliveEnemies() {
        ArrayList<Observer.characterSlot> result = new ArrayList<>();
        for(Observer.characterSlot slot : getAllEnemies()) {
            if(slot!=null&&slot.getCurrentHp()>0){
                result.add(slot);
            }
        }
        return result;
    }
    public boolean hasAliveEnemy() {
        if(enemySlot!=null){
            return true;
        }
        if(enemySlot2!=null){
            return true;
        }
        if(enemySlot3!=null){
            return true;
        }
        return false;
    }

    /**
     * Get all enemy with prey
     */
    public Observer.characterSlot getPreyEnemies() {
        for(Observer.characterSlot slot : getAllEnemies()) {
            if(slot!=null&&slot.containsBuffDebuff("Prey")){
                return slot;
            }
        }

        return null;
    }
    
    /**
     * Remove all buffs and debuffs from all heroes
     */
    public void removeAllBuffsDebuffsFromHeroes() {
        Observer.characterSlot[] heroes = getAllHeroes();
        
        for (Observer.characterSlot hero : heroes) {
            if (hero != null) {
                // Clear all active effects
                if (hero.getActiveEffects() != null) {
                    hero.getActiveEffects().clear();
                }
                hero.getCharacter().getUniqueValues().remove(hero.getCharacter().getUniqueValue("Guts"));
                
                // Reset stat modifications to base values
                SpecialTalents.resetStatModification(hero);
                
                // Update UI if available
                if (battleUI != null) {
                    battleUI.updateHealthUI(hero);
                    battleUI.updateBarrierBar(hero);
                }
            }
        }
    }
    public void healToFullAllHeroes() {
        for(Observer.characterSlot slot : getAllHeroes()) {
            if(slot!=null){
                slot.setCurrentHp(slot.getCharacter().getHp());
                slot.setCurrentMp(slot.getCharacter().getMp());
            }
        }
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
    public void removeAllCharacters(){
        heroSlot = heroSlot2 = heroSlot3 = enemySlot = enemySlot2 = enemySlot3 = null;
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

    private double getNextLineAct(Line currentLine) {
        double nextX = battleUI.getBarX()+battleUI.getBarWidth();
        for(Line line:battleUI.getAllLines()){
            if(line!=null&&line!=currentLine){
                if(line.getStartX()<nextX){
                    nextX = line.getStartX();
                }
            }
        }
        return nextX;
    }
    
    /**
     * Get the corresponding speed for a character slot
     */
    public double getSpeedForCharacter(Observer.characterSlot slot) {
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

    public void pushAllLineToStart(){
        for (Observer.characterSlot slot : getAllCharacters()) {
            if(slot!=null){
                if(slot.getLine()!=null){
                    double barX = battleUI.getBarX();
                    slot.getLine().setStartX(barX+slot.getCharacter().getAV());
                    slot.getLine().setEndX(barX+slot.getCharacter().getAV());
                }
            }
        }
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
    public boolean hasHeroName(String name){
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                if (hero.getCharacter().getName().equals(name)) {
                    return true;
                }
            }

        }
        return false;
    }
    public Observer.characterSlot getHeroByName(String name){
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                if (hero.getCharacter().getName().equals(name)) {
                    return hero;
                }
            }

        }
        return null;
    }
    public Observer.characterSlot hasRageEmpowermentHost(){
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                if(hero.getBuffDebuffByName("Rage empowerment host") != null) {
                    return hero;
                }
            }
        }
        return null;
    }
    public Observer.characterSlot hasRageEmpowerment(){
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                if(hero.getBuffDebuffByName("Rage empowerment") != null) {
                    return hero;
                }
            }
        }
        return null;
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

        // Hero slots - use configured heroes
        Observer.CharacterSlotRegistry.init();

//        heroSlot=null;
//        heroSlot2=null;
//        heroSlot3=null;
        if(selectedHeroes.length > 0&&heroSlot==null) {
            heroSlot = Observer.CharacterSlotRegistry.getByName(selectedHeroes[0]);
        }

        if(selectedHeroes.length > 1&&heroSlot2==null) {
            heroSlot2 = Observer.CharacterSlotRegistry.getByName(selectedHeroes[1]);
        }

        if(selectedHeroes.length > 2&&heroSlot3==null) {
            heroSlot3 = Observer.CharacterSlotRegistry.getByName(selectedHeroes[2]);
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
    
    public void setOnBattleLost(Runnable callback) {
        this.onBattleLost = callback;
    }

    private static final Map<String, Runnable> SPECIAL_MUSIC = Map.of(
            "Flamita ?", () -> audioManager.playFlamitaMusic(),
            "Flamita The Immortal Phoenix", () -> audioManager.playFlamitaMusic(),
            "Mabel", () -> audioManager.playMabelMusic(),
            "Chigon The All Mighty Dragon",()-> audioManager.playMusic("Dragon Slayer.mp3",true)
            //"Electra", () -> audioManager.playElectraMusic()
    );
    public void playBattleMusic() {
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
        if(heroSlot!=null&&heroSlot.getCharacter().getName().equals("Litaru ")){
            System.out.println("skip music");
        }else {
            playBattleMusic();
        }
        // Configure which characters to create
        
        // Init registry
        Observer.CharacterSlotRegistry.init();
        timeStop=0;
        timeStopSlot=null;
        //Remove list
        setDefeatEnemies(new ArrayList<String>());


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
        if(hasHeroName("Ina")){
            getSlotByName("Ina").setCurrentMp(5);
        }
        //Wait there are more ?
        if(enemySlot!=null&&enemySlot.getCharacter().getName().equals("Chigon The All Mighty Dragon")){
            enemySlot.setCurrentMp(40);
        }
        selectedTarget = enemySlot; // default target
        selectedAllyTarget = heroSlot; // ally selected initially
        selectedEnemyTarget = enemySlot; // default enemy target

        // Set current acting hero
        currentActingHero = heroSlot;
        // Set equipment stat
        for(Observer.characterSlot hero: getAllHeroes()){
            if(hero!=null){
                SpecialTalents.applyStatModifications(hero,null);
            }
        }
        pushAllLineToStart();
    }
    
    public void startBattleLoop() {
        // Stop any existing battle loop first
        stopBattleLoop();

        // Start new battle loop using ScheduledExecutorService for frame-rate independent timing
        battleLoopActive = true;
        
        // Use ScheduledExecutorService for frame-rate independent timing
        battleLoopExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BattleLoopThread");
            t.setDaemon(true);
            return t;
        });
        
        // Schedule updates at fixed 5ms intervals (frame-rate independent)
        battleLoopExecutor.scheduleAtFixedRate(
            () -> {
                if (battleLoopActive) {
                    // Run on JavaFX application thread
                    javafx.application.Platform.runLater(() -> {
                        if (battleLoopActive) {
                            updateLines();
                        }
                    });
                }
            },
            0,
            BATTLE_LOOP_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
    }
    
    public void stopBattleLoop() {
        // Stop the battle loop
        battleLoopActive = false;
        setMoving(false);
        
        // Shutdown the executor
        if (battleLoopExecutor != null) {
            battleLoopExecutor.shutdown();
            try {
                if (!battleLoopExecutor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    battleLoopExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                battleLoopExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            battleLoopExecutor = null;
        }
    }

    public void useSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        //Push & reduce mp
        if (isEnemy(attacker)) {
            if(!attacker.containsEffectBuffDebuff("NoMp")) {
                if (attacker.getCharacter().getName().equals("Chigon The All Mighty Dragon")) {
                    if(!attacker.containsBuffDebuff("Taunt")){
                        attacker.setCurrentMp(Math.min(attacker.getCharacter().getMp(), attacker.getCurrentMp() - skill.getMpCost()));
                    }
                } else {
                    attacker.setCurrentMp(Math.min(attacker.getCharacter().getMp(), attacker.getCurrentMp() - skill.getMpCost()));
                }
            }
            double push = attacker.getCharacter().getAV() * skill.getAVScale();
            pushCharacterLine(attacker, push);
        }

        Line attackerLine = attacker.getLine();
        if(attackerLine == null || turnOf == null || 
           attackerLine != turnOf) {
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
        double specialDmgBonus = calculateSpecialDmgBonus(attacker,target, skill);
        double equipmentDmgBonus = handleSpecialEquipmentDamage(attacker,target,skill,baseDamage);
        double finalDmg = baseDamage+burnBonus+talentBonus+specialDmgBonus+equipmentDmgBonus;
        //Calculate damage after def/res reduction
        finalDmg = characters.SpecialTalents.calculateDefResReduction(skill,attacker, target, finalDmg);

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
                effectCopy.setSource(attacker.getCharacter().getName());
                // Apply the effect to the target
                characters.SpecialTalents.applyBuffDebuff(target, effectCopy);
            }
        }
        
        // Update barrier bar for target if effects were applied
        if (battleUI != null && skill.getSkillEffects() != null && !skill.getSkillEffects().isEmpty()) {
            battleUI.updateBarrierBar(target);
        }


        // Reduce duration of buff/debuff effects at the end of turn
        characters.SpecialTalents.onTurnEnd(attacker);
        //Visual fix
        if(skill.getAVScale()==0.0f){
            for(Observer.characterSlot slot: getAllCharacters()){
                if(slot!=null&&slot!=attacker){
                    pushCharacterLine(slot,getSpeedForCharacter(slot));
                }
            }
        }

        System.out.println(attacker.getCharacter().getName()+" use "+skill.getName()+  " at "+ target.getCharacter().getName()+" for "+ finalDmg+" damage ");
    }
    
    /**
     * Handle cleanup when an enemy dies (switch target, remove health bar, clear enemySlot)
     * @param slot The character slot to check (should be an enemy)
     */
    private void handleDeadEnemyCleanup(Observer.characterSlot slot) {
        if (slot == null || !isEnemy(slot) || slot.getCurrentHp() > 0) {
            return; // Not an enemy or not dead
        }
        
        // If the selected enemy target is the one that died, switch to another alive enemy
        if (selectedEnemyTarget == slot) {
            ArrayList<Observer.characterSlot> aliveEnemies = getAllAliveEnemies();
            if (!aliveEnemies.isEmpty()) {
                selectedEnemyTarget = aliveEnemies.get(0); // Select first alive enemy
                selectedTarget = selectedEnemyTarget; // Keep for backward compatibility
                if (battleUI.highlightSelection != null) {
                    battleUI.highlightSelection.run();
                }
            } else {
                selectedEnemyTarget = null;
                selectedTarget = null;
            }
        }
        //Add to list
        defeatEnemies.add(slot.getCharacter().getName());
        // Remove health bar for dead enemy
        if (battleUI != null) {
            battleUI.removeHealthBarForEnemy(slot);
        }
        //If fireOrb
        if(slot.getCharacter().getName().contains("Fire Orb")){
            if(enemySlot.getCharacter().getUniqueValueAsFloat("Phase 3") > 0){
                pushCharacterLine(enemySlot,100);
            }else {
                enemySlot.getCharacter().addToUniqueValue("Burning rage", -enemySlot.getCharacter().getUniqueValueAsFloat("Burning rage") / 2);
            }
        }

        // Clear the enemySlot reference
        if (slot == enemySlot) {
            enemySlot = null;
        } else if (slot == enemySlot2) {
            enemySlot2 = null;
        } else if (slot == enemySlot3) {
            enemySlot3 = null;
        }

        if(slot.getCharacter().getName().equals("Spiritual Monster")&&enemySlot==null&&enemySlot2==null&&enemySlot3==null){
            spawnSpiritualMonster();
        }

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
        if(actingEnemy.getCurrentHp()<=0){
            battleUI.updateHealthUI(actingEnemy);
            defeatEnemies.add(actingEnemy.getCharacter().getName());
            removeCharacterLine(actingEnemy);
            handleDeadEnemyCleanup(actingEnemy);
            checkVictoryCondition();
            moving = true;
            return;
        }

        // Update UI after turn-based effects
        if (battleUI != null) {
            battleUI.updateHealthUI(actingEnemy);
        }
        //Check Stunned
        if(actingEnemy.containsEffectBuffDebuff("Stunned")){
            moving = true;
            pushCharacterLine(actingEnemy,actingEnemy.getCharacter().getAV());
            SpecialTalents.onTurnEnd(actingEnemy);
            return;
        }
        // Filter skills based on MP availability
        ArrayList<Ability.skill> availableSkills = new ArrayList<>();
        
        // If enemy has MP > 0, filter skills by MP cost
        if (actingEnemy.getCharacter().getMp() > 0) {
            for (Ability.skill skill : actingEnemy.getSkills()) {
                // Check if skill is not null
                if (skill == null) continue;
                
                // Special condition for skills
                if (skill.getName().equals("Eternal darkness")) {
                    if (hasHeroWithVoidBurn(10)) {
                        availableSkills.add(skill);
                    }
                }else if(skill.getName().equals("Family united")){
                    if ((int)actingEnemy.getCurrentHp() == 1) {
                        availableSkills.add(skill);
                    }

                }else if(skill.getName().equals("Daddy fury")) {
                    if ((int) actingEnemy.getFloatBuffDebuffByName("Oufuu atk up") >= BuffDebuff.getByName("Oufuu atk up").getMaxStack()) {
                        availableSkills.add(skill);
                    }
                }else if(skill.getName().equals("CBMark")) {
                    if (!actingEnemy.containsBuffDebuff("Taunt")&&actingEnemy.getCurrentMp()>=skill.getMpCost()) {
                        availableSkills.add(skill);
                    }
                }
                else if(skill.getName().equals("CBS2")){
                    if(!actingEnemy.containsBuffDebuff("Taunt")){
                        availableSkills.add(skill);
                    }
                }
                else if(skill.getName().equals("CBMark2")){
                    if(!actingEnemy.containsBuffDebuff("Taunt")&&!actingEnemy.containsBuffDebuff("Challenge2")&&actingEnemy.getCurrentMp()>=skill.getMpCost()&&!(actingEnemy.getCharacter().getUniqueValueAsFloat("Phase 1")>0)) {
                        availableSkills.add(skill);
                    }
                }
                else if(skill.getName().equals("CBUlt")){
                    if(!actingEnemy.containsBuffDebuff("Dragon breath!")&&!actingEnemy.containsBuffDebuff("Taunt")&&!actingEnemy.containsBuffDebuff("Challenge2")&&actingEnemy.getCurrentMp()>=skill.getMpCost()&&(actingEnemy.getCharacter().getUniqueValueAsFloat("Phase 3")>0)){
                        availableSkills.add(skill);
                    }
                }
                else {
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
                availableSkills.add(skill);
            }
        }
        
        // If no skills are available, try to give basic attack
        if (availableSkills.isEmpty()) {
            // Fallback: try to give basic attack (Slash)
            Ability.skill basicAttack = Ability.SkillRegistry.getById(1);
            availableSkills.add(basicAttack);
        }
        Ability.skill chosenSkill;
        if(actingEnemy.getCharacter().getName().equals("Flamita The Immortal Phoenix")){
            ArrayList<Ability.skill>availableSkills2=getFlamitaBossSkill(actingEnemy,availableSkills);
            chosenSkill = selectWeightedSkill(actingEnemy, availableSkills2, random);
            System.out.println(availableSkills);
        }else {
            // Choose random skill from available skills (weighted only for bosses)
            chosenSkill = selectWeightedSkill(actingEnemy, availableSkills, random);
        }
        // Check if chosen skill is valid
        if (chosenSkill == null) {
            moving = true;
            return;
        }
        
        // Pick random living hero as target if offensive
        if (chosenSkill.getTarget().equals("Self")) {
            useSkill(actingEnemy, actingEnemy, chosenSkill);
        }else if(chosenSkill.getTarget().equals("Ally")){
            useSkill(actingEnemy,getRandomAliveEnemy(),chosenSkill);
        }else if(chosenSkill.getName().equals("Family united")){
            useSkill(actingEnemy, enemySlot2, chosenSkill);
        }else if(chosenSkill.getTarget().equals("Aoe enemy") || chosenSkill.getTarget().equals("Aoe ally")){
            executeAoeSkill(actingEnemy, chosenSkill);
        }else {
            Observer.characterSlot targetHero = getRandomAliveHero();

            if(actingEnemy.containsBuffDebuff("Taunt")){
                Observer.characterSlot taunted = getHeroByName(actingEnemy.getBuffDebuffByName("Taunt").getSource());
                if(taunted.getCurrentHp()>0){
                    targetHero = taunted;
                }else{
                    actingEnemy.removeBuffDebuffByName("Taunt");
                }
            }
            if (targetHero == null) {
                // No heroes alive, shouldn't happen but handle gracefully
                moving = true;
                return;
            }
            useSkill(actingEnemy, targetHero, chosenSkill);

        }
        if(actingEnemy.getCharacter().getName().equals("Flamita The Immortal Phoenix")) {
            afterFlamitaBossSkill(actingEnemy,chosenSkill);
            if(actingEnemy.getCharacter().getUniqueValueAsFloat("Phase 3")>0&&enemySlot2==null&&enemySlot3==null){
                spawnFireOrb();
            }
        }
        
        // Track skill usage for weighted selection
        trackEnemySkillUsage(actingEnemy, chosenSkill);

        //Continue the AV bar
        if(!chosenSkill.getName().equals("CBMark2")) {
            moving = true;
        }
    }
    
    /**
     * Check if an enemy is a boss (name contains "the")
     */
    private boolean isBossEnemy(Observer.characterSlot enemy) {
        if (enemy == null || enemy.getCharacter() == null) return false;
        String enemyName = enemy.getCharacter().getName();
        return enemyName != null && enemyName.toLowerCase().contains("the");
    }
    
    /**
     * Track enemy skill usage for weighted selection (only for bosses)
     * Basic skills (-20 < mp < 20) need to be used 5 times before affecting selection
     */
    private void trackEnemySkillUsage(Observer.characterSlot enemy, Ability.skill skill) {
        if (enemy == null || skill == null) return;
        
        // Only track for boss enemies (names containing "the")
        // Exclude Flamita boss from tracking
        if (!isBossEnemy(enemy)) return;
        
        String enemyName = enemy.getCharacter().getName();
        if (enemyName.equals("Flamita The Immortal Phoenix")) return;
        int skillId = skill.getId();
        float mpCost = skill.getMpCost();
        
        // Initialize maps if needed
        enemySkillUsageCount.putIfAbsent(enemyName, new HashMap<>());
        enemyUsedSkillList.putIfAbsent(enemyName, new HashSet<>());
        
        Map<Integer, Integer> usageCount = enemySkillUsageCount.get(enemyName);
        Set<Integer> usedSkills = enemyUsedSkillList.get(enemyName);
        
        // Check if it's a basic skill (-20 < mp < 20)
        boolean isBasicSkill = mpCost > -20 && mpCost < 20;
        
        // Always increment usage count for tracking
        int currentCount = usageCount.getOrDefault(skillId, 0);
        currentCount++;
        usageCount.put(skillId, currentCount);
        
        if (isBasicSkill) {
            // Only add to used list after 5 uses
            if (currentCount >= 5) {
                usedSkills.add(skillId);
            }
        } else {
            // For non-basic skills, add to used list immediately
            if (!usedSkills.contains(skillId)) {
                usedSkills.add(skillId);
            }
        }
    }
    
    /**
     * Select a skill using weighted random selection (only for bosses)
     * For each skill in usedSkillList, add all other skills once to the pool
     * For non-boss enemies, returns a simple random selection
     */
    private Ability.skill selectWeightedSkill(Observer.characterSlot enemy, ArrayList<Ability.skill> availableSkills, Random random) {
        if (availableSkills.isEmpty()) {
            return null;
        }
        
        // Only use weighted selection for boss enemies (names containing "the")
        // Exclude Flamita boss from weighted selection
        String enemyName = enemy.getCharacter().getName();
        if (!isBossEnemy(enemy) || enemyName.equals("Flamita The Immortal Phoenix")) {
            // Simple random selection for non-boss enemies and Flamita
            return availableSkills.get(random.nextInt(availableSkills.size()));
        }
        Set<Integer> usedSkills = enemyUsedSkillList.getOrDefault(enemyName, new HashSet<>());
        Map<Integer, Integer> usageCount = enemySkillUsageCount.getOrDefault(enemyName, new HashMap<>());
        
        // Build weighted pool
        ArrayList<Ability.skill> weightedPool = new ArrayList<>();
        
        // Add all available skills at least once
        weightedPool.addAll(availableSkills);
        
        // For each used skill, add all other skills multiple times based on usage count
        for (Integer usedSkillId : usedSkills) {
            // Get the effective usage count for this skill
            int skillUsageCount = usageCount.getOrDefault(usedSkillId, 0);
            
            // Find the skill object to check if it's basic
            // First try availableSkills, then try enemy's full skill list, then SkillRegistry
            Ability.skill usedSkillObj = null;
            for (Ability.skill s : availableSkills) {
                if (s.getId() == usedSkillId) {
                    usedSkillObj = s;
                    break;
                }
            }
            
            // If not found in available skills, check enemy's full skill list
            if (usedSkillObj == null && enemy.getSkills() != null) {
                for (Ability.skill s : enemy.getSkills()) {
                    if (s != null && s.getId() == usedSkillId) {
                        usedSkillObj = s;
                        break;
                    }
                }
            }
            
            // If still not found, try SkillRegistry
            if (usedSkillObj == null) {
                usedSkillObj = Ability.SkillRegistry.getById(usedSkillId);
            }
            
            int effectiveUses = skillUsageCount;
            if (usedSkillObj != null) {
                float mpCost = usedSkillObj.getMpCost();
                boolean isBasicSkill = mpCost > -20 && mpCost < 20;
                if (isBasicSkill) {
                    // For basic skills, effective uses = floor(usageCount / 5)
                    effectiveUses = skillUsageCount / 5;
                }
            }
            
            // Add all other skills to the pool 'effectiveUses' times
            for (int i = 0; i < effectiveUses; i++) {
                for (Ability.skill skill : availableSkills) {
                    if (skill.getId() != usedSkillId) {
                        weightedPool.add(skill);
                    }
                }
            }
        }
        
        // Log weighted pool for debugging
        System.out.println("=== Weighted Pool for " + enemyName + " ===");
        
        // Print available skills
        StringBuilder availableSkillsStr = new StringBuilder("[");
        for (int i = 0; i < availableSkills.size(); i++) {
            Ability.skill s = availableSkills.get(i);
            availableSkillsStr.append(s.getName()).append("(ID:").append(s.getId()).append(")");
            if (i < availableSkills.size() - 1) availableSkillsStr.append(", ");
        }
        availableSkillsStr.append("]");
        System.out.println("Available skills: " + availableSkillsStr.toString());
        
        // Print used skills with their usage counts
        StringBuilder usedSkillsStr = new StringBuilder("[");
        boolean first = true;
        for (Integer skillId : usedSkills) {
            if (!first) usedSkillsStr.append(", ");
            int count = usageCount.getOrDefault(skillId, 0);
            usedSkillsStr.append("ID:").append(skillId).append("(count:").append(count).append(")");
            first = false;
        }
        usedSkillsStr.append("]");
        System.out.println("Used skills (affecting weights): " + usedSkillsStr.toString());
        
        // Count occurrences of each skill in the weighted pool
        Map<String, Integer> skillWeights = new HashMap<>();
        for (Ability.skill skill : weightedPool) {
            String skillKey = skill.getName() + "(ID:" + skill.getId() + ")";
            skillWeights.put(skillKey, skillWeights.getOrDefault(skillKey, 0) + 1);
        }
        System.out.println("Weighted pool composition:");
        for (Map.Entry<String, Integer> entry : skillWeights.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> weight: " + entry.getValue());
        }
        System.out.println("Total pool size: " + weightedPool.size());
        System.out.println("================================");
        
        // Randomly select from weighted pool
        if (weightedPool.isEmpty()) {
            return availableSkills.get(random.nextInt(availableSkills.size()));
        }
        
        Ability.skill selectedSkill = weightedPool.get(random.nextInt(weightedPool.size()));
        System.out.println("Selected skill: " + selectedSkill.getName() + "(ID:" + selectedSkill.getId() + ")");
        return selectedSkill;
    }
    private ArrayList<Ability.skill> getFlamitaBossSkill(Observer.characterSlot actingEnemy,ArrayList<Ability.skill> availableSkills){
        if(actingEnemy.getCharacter().getUniqueValueAsFloat("Phase 3")>0){
            availableSkills.removeIf(skill -> skill.getId() != 6);
            SpecialTalents.applyBuffDebuff(actingEnemy,BuffDebuff.getByName("Combustion").copy());
            return availableSkills;
        }
        int actionCount = (int)actingEnemy.getCharacter().getUniqueValueAsFloat("actionCount");
        int action46 = (int)actingEnemy.getCharacter().getUniqueValueAsFloat("action46");
        int action47 = (int)actingEnemy.getCharacter().getUniqueValueAsFloat("action47");
        int chance46 = 0;
        int chance47 = 0;
        boolean use46 = false;
        boolean use47 = false;
        if(actionCount>action46){chance46=actionCount-action46;}
        if(actionCount>action47){chance47=actionCount-action47;}
        int ranInt;
        if(chance46+chance47<=100){ranInt=random(1,100);}
        else{ranInt=random(1,chance46+chance47);}
        if(ranInt<chance46&&enemySlot2==null&&enemySlot3==null){
            use46=true;
        }else if(ranInt<chance46+chance47 && actingEnemy.getCharacter().getUniqueValueAsFloat("Phase 2")>0 && actingEnemy.getCharacter().getUniqueValueAsFloat("Burning rage")>300){
            use47=true;
        }
        if (use46) {
            availableSkills.removeIf(skill -> skill.getId() != 46);
        }
        else if (use47) {
            availableSkills.removeIf(skill -> skill.getId() != 47);
        }
        else{
            availableSkills.removeIf(skill -> skill.getId() == 46 || skill.getId() == 47);
        }
        return availableSkills;
    }
    private void afterFlamitaBossSkill(Observer.characterSlot attacker, Ability.skill skill){
        if(skill.getId()<=7){
            attacker.getCharacter().addToUniqueValue("actionCount",10);
        }else if(skill.getId()==8){
            attacker.getCharacter().addToUniqueValue("actionCount",0);
        }else if(skill.getId()==9){
            attacker.getCharacter().addToUniqueValue("actionCount",5);
        }else if(skill.getId()==46){
            attacker.getCharacter().addToUniqueValue("actionCount",-50);
            if(attacker.getCharacter().getUniqueValueAsFloat("Phase 2")>0) {
                attacker.getCharacter().addToUniqueValue("action46", +12);
                attacker.getCharacter().addToUniqueValue("action47", -12);
            }
        }else if(skill.getId()==47){
            attacker.getCharacter().addToUniqueValue("actionCount",-75);
            if(attacker.getCharacter().getUniqueValueAsFloat("Phase 2")>0) {
                attacker.getCharacter().addToUniqueValue("action47", +19);
                attacker.getCharacter().addToUniqueValue("action46", -19);
            }
        }
    }
    private void updateLines() {
        if (!moving || battleUI == null) {
            return;
        }
        if(defeatEnemies.contains("Oufuu daddy")) {
            checkVictoryCondition();
        }
        //Handle confront

        // Handle Rage empowerment mechanics
        if(hasHeroName("Flatina")) {
            Observer.characterSlot Flatina = hasRageEmpowermentHost();
            Observer.characterSlot Flatina2 = getHeroByName("Flatina");
            Observer.characterSlot rageHero = hasRageEmpowerment();
            if (Flatina != null) {
                if (Flatina.getCharacter().getUniqueValueAsFloat("Burning rage") > 0) {
                    Flatina.getCharacter().addToUniqueValue("Burning rage", -0.2f);
                    SpecialTalents.applyBuffDebuff(rageHero, BuffDebuff.getByName("Rage empowerment"));
                } else {
                    Flatina.getActiveEffects().remove(Flatina.getBuffDebuffByName("Rage empowerment host"));
                    rageHero.getActiveEffects().remove(rageHero.getBuffDebuffByName("Rage empowerment"));
                }
                SpecialTalents.applyStatModifications(rageHero, null);
                battleUI.updateHealthUI(rageHero);
                battleUI.updateBurningRageBar(rageHero);
                battleUI.updateBurningRageBar(Flatina);
            }
            if(Flatina2.getBuffDebuffByName("Burning guts cd")!=null) {

                BuffDebuff cd = BuffDebuff.getByName("Burning guts cd").copy();
                cd.setStack(-1);
                SpecialTalents.applyBuffDebuff(Flatina2,cd);
                if(Flatina2.getBuffDebuffByName("Burning guts cd").getStack()==0) {
                    Flatina2.getActiveEffects().remove(Flatina2.getBuffDebuffByName("Burning guts cd"));
                }

            }
        }

        //Handle Flamita boss fight
        if(enemySlot!=null&&enemySlot.getCharacter().getName().equals("Flamita The Immortal Phoenix")){
            if(enemySlot.getCharacter().getUniqueValueAsFloat("Burning rage") < enemySlot.getCharacter().getHp()&&enemySlot.getCharacter().getUniqueValueAsFloat("Phase 3")==0) {
                if (enemySlot2 != null) {
                    enemySlot.getCharacter().addToUniqueValue("Burning rage", 0.5f);
                }
                if (enemySlot3 != null) {
                    enemySlot.getCharacter().addToUniqueValue("Burning rage", 0.5f);
                }
            }
            if(enemySlot.getBuffDebuffByName("Rage absorption")!=null){
                if(enemySlot.getLine().getStartX()-battleUI.getBarX()<2&&enemySlot.getBuffDebuffByName("Rage absorption").getDuration()>1){
                    enemySlot.getBuffDebuffByName("Rage absorption").setDuration(1);
                    pushCharacterLine(enemySlot,198);
                }
                if(enemySlot.getCharacter().getUniqueValueAsFloat("Burning rage") > 0) {
                    enemySlot.getCharacter().setHp(enemySlot.getCharacter().getHp() + 3);
                    enemySlot.getBaseCharacter().setHp(enemySlot.getBaseCharacter().getHp() + 3);
                    enemySlot.getCharacter().addToUniqueValue("Burning rage", -1f);
                }else{
                    enemySlot.removeBuffDebuffByName("Rage absorption");
                    enemySlot.addBuffDebuff(BuffDebuff.getByName("Stunned").copy());
                }
            }
            battleUI.updateBurningRageBar(enemySlot);
            battleUI.updateHealthUI(enemySlot);
            if(enemySlot.getBuffDebuffByName("Resurrection")!=null){
                enemySlot.getLine().setStartX(battleUI.getBarX()+200);
                if(enemySlot2==null&&enemySlot3==null){
                    enemySlot.removeBuffDebuffByName("Resurrection");
                }else{
                    enemySlot.heal(3);
                }
            }else if(enemySlot.getCharacter().getUniqueValueAsFloat("Phase 3")>0&&enemySlot.getCurrentHp()>0){
                int burn = 1;
                if(enemySlot.containsBuffDebuff("Combustion")){
                    burn=enemySlot.getBuffDebuffByName("Combustion").getStack()/4;
                }
                burn=Math.max(burn, 1);
                enemySlot.setCurrentHp(enemySlot.getCurrentHp()-burn);
                if(enemySlot.getCurrentHp()<=0){
                    applyDamage(enemySlot2,9999);
                    applyDamage(enemySlot3,9999);
                    defeatEnemies.add("Flamita The Immortal Phoenix");
                    checkVictoryCondition();
                    return;
                }
            }
        }

        //Handle prey mechanics
        //Handle Dragon breath
        if(hasHeroName("Chigon")){
            Observer.characterSlot prey = getPreyEnemies();
            Observer.characterSlot Chigon = getHeroByName("Chigon");
            if(prey!=null&&!prey.containsBuffDebuff("Brave")){
                if(prey.getLine().getStartX()-4<= battleUI.getBarX()) {
                    if(!Chigon.containsBuffDebuff("Dragon breath")) {
                        resetLine(Chigon);
                    }else{
                        applyDamage(prey,Chigon.getCharacter().getAtk());
                    }
                    Chigon.addBuffDebuff(BuffDebuff.getByName("Swap").copy());
                    prey.removeBuffDebuffByName("Prey");
                    prey.addBuffDebuff(BuffDebuff.getByName("Brave").copy());
                }
            }
            if(prey!=null&&prey.containsBuffDebuff("Brave")){
                prey.removeBuffDebuffByName("Prey");
            }
            if(Chigon.containsBuffDebuff("Dragon breath")){
                if(partyMp>0) {
                    setPartyMp(Math.max(0,getPartyMp()-0.5f));
                    SpecialTalents.applyStatModifications(Chigon, null);
                    for (Observer.characterSlot enemy : getAllAliveEnemies()) {
                        if(enemy!=prey) {
                            applyDamage(enemy, Chigon.getCharacter().getAtk() * 0.01);
                        }else{
                            applyDamage(enemy, Chigon.getCharacter().getAtk() * 0.02);
                        }
                    }
                }else{
                    Chigon.getActiveEffects().remove(Chigon.getBuffDebuffByName("Dragon breath"));
                }
            }
        }
        if(hasHeroName("Litaru ")){
            heroSlot.setCurrentHp(Math.max(0,heroSlot.getCurrentHp()-0.1f));
            battleUI.updateHealthUI(heroSlot);
            if(heroSlot.getCurrentHp()<=0){
                onBattleWon.run();
            }
        }
        //Chigon boss------------------------------------------------------------------------------------
        if(enemySlot!=null&&enemySlot.getCharacter().getName().equals("Chigon The All Mighty Dragon")){
            if(enemySlot.getBuffDebuffByName("Dragon breath!")!=null){
                for(Observer.characterSlot hero : getAllHeroes()){
                    if(hero.getCurrentHp()>0){
                        double dmg = SpecialTalents.calculateDefResReduction(Ability.SkillRegistry.getById(59),enemySlot,hero,enemySlot.getCharacter().getAtk()*0.002);
                        applyDamage(hero,dmg);
                    }
                }
                if(enemySlot.getLine().getStartX()<battleUI.getBarX()+2){
                    pushCharacterLine(enemySlot,198);
                    SpecialTalents.applyBuffDebuff(enemySlot,BuffDebuff.getByName("Flame increment").copy());
                }
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
                        // For heroes with Banish, don't move their line (stuck at end of barX)
                        if (isHero(character) && character.getBuffDebuffByName("Banish") != null) {
                            // Keep line stuck at end of barX
                            double barX = battleUI.getBarX();
                            double barWidth = battleUI.getBarWidth();
                            characterLine.setStartX(barX + barWidth);
                            characterLine.setEndX(barX + barWidth);
                            continue;
                        }
                        double speed = getSpeedForCharacter(character);
                        moveLine(characterLine, speed);
                    }
                }
            }
        }
        
        // Update subBars for CBUlt mechanic
        if (battleUI != null) {
            battleUI.updateSubBars();
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
                        // Check if all heroes are defeated
                        checkLoseCondition();
                        moving = true;
                    }
                    battleUI.refreshAllCharacterUI();
                    if (currentActingHero != null && battleUI != null) {
                        battleUI.renderHeroSkillsFor(currentActingHero);
                    }
                    //autocast
                    if(currentActingHero != null && currentActingHero.containsBuffDebuff("Last dance")) {
                        Ability.skill skill = currentActingHero.getSkills().get(0);
                        useSkill(currentActingHero,getRandomAliveEnemy(),skill);
                        currentActingHero.setCurrentMp(0);
                        pushCharacterLine(currentActingHero,currentActingHero.getCharacter().getAV()*skill.getAVScale());
                        moving=true;
                    }
                }
            }
            if(timeStopSlot != null) {
                double push = timeStopSlot.getCharacter().getAV() * timeStopSlot.getSkills().get(0).getAVScale();
                if(timeStopSlot.getCharacter().getName().equals("Ina")&&timeStop>push+1){
                    useSkill(timeStopSlot,getRandomAliveEnemy(),timeStopSlot.getSkills().get(0));
                    timeStopSlot.getCharacter().setMp(0);
                    pushCharacterLine(timeStopSlot, push);
                    moving = true;
                }
            }
            return;

        }

        line.setStartX(newX);
        line.setEndX(newX);
    }

    public void applyDamage(Observer.characterSlot slot, double amount) {
        if(slot == null) return;
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
            
            // Track damage dealt to enemies for subBar movement
            if (isEnemy(slot) && actualAmount > 0) {
                totalDamageToEnemies += actualAmount;
            }
            
            //Chigon party mp
            if(hasHeroName("Chigon")&&isEnemy(slot)&&!getHeroByName("Chigon").containsBuffDebuff("Dragon breath")){
                setPartyMp(getPartyMp()+(int)amount*0.1f);
            }
            if(hasHeroName("Lucia")&&isEnemy(slot)){
                Characters.character Lucia = getHeroByName("Lucia").getCharacter();
                Lucia.addToUniqueValue("Elysion Regeneration",(float)amount/2);
            }

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
                
                // If a hero with a trial debuff dies, complete the trial
                if (isHero(slot)) {
                    BuffDebuff comboTrial = slot.getBuffDebuffByName("Combo Trial");
                    BuffDebuff powerTrial = slot.getBuffDebuffByName("Power Trial");
                    BuffDebuff recoveryTrial = slot.getBuffDebuffByName("Recovery Trial");
                    
                    if (comboTrial != null || powerTrial != null || recoveryTrial != null) {
                        // Complete trial cleanup (removes trial debuff, applies Stunned, removes Banish, etc.)
                        completeTrialCleanup();
                    }
                }
                
                // Handle dead enemy cleanup (switch target, remove health bar, clear enemySlot)
                handleDeadEnemyCleanup(slot);
                
                // Check if all enemies are defeated
                checkVictoryCondition();
                
                // Check if all heroes are defeated
                checkLoseCondition();
            }
        }
    }
    
    /**
     * Complete trial cleanup - applies Stunned, removes Taunt, Challenge2, and Banish, restores health bars
     */
    public void completeTrialCleanup() {
        Observer.characterSlot enemySlot = getEnemySlot();
        if (enemySlot != null) {
            // Apply Stunned debuff to enemySlot
            characters.SpecialTalents.applyBuffDebuff(enemySlot, 
                characters.BuffDebuff.getByName("Stunned").copy().withDuration(3));
            
            // Remove Taunt, Challenge2, and Invulnerable from enemySlot
            enemySlot.removeBuffDebuffByName("Taunt");
            enemySlot.removeBuffDebuffByName("Challenge2");
            enemySlot.removeBuffDebuffByName("Invulnerable");
            if (battleUI != null) {
                battleUI.updateBarrierBar(enemySlot);
            }
        }
        
        // Remove trial debuff and Banish from all heroes, restore health bar colors
        Observer.characterSlot[] allHeroes = getAllHeroes();
        for (Observer.characterSlot hero : allHeroes) {
            if (hero != null && hero.getCurrentHp() > 0) {
                // Remove trial debuff from hero
                BuffDebuff comboTrial = hero.getBuffDebuffByName("Combo Trial");
                BuffDebuff powerTrial = hero.getBuffDebuffByName("Power Trial");
                BuffDebuff recoveryTrial = hero.getBuffDebuffByName("Recovery Trial");
                if (comboTrial != null) hero.removeBuffDebuffByName("Combo Trial");
                if (powerTrial != null) hero.removeBuffDebuffByName("Power Trial");
                if (recoveryTrial != null) hero.removeBuffDebuffByName("Recovery Trial");
                
                // Remove Banish from hero
                BuffDebuff banish = hero.getBuffDebuffByName("Banish");
                if (banish != null) {
                    hero.removeBuffDebuffByName("Banish");
                    // Restore health bar color
                    if (battleUI != null) {
                        battleUI.updateHealthUI(hero);
                    }
                }
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
    
    /**
     * Get total damage dealt to enemies since CBUlt was cast (for subBar movement)
     */
    public double getTotalDamageToEnemies() { return totalDamageToEnemies; }
    
    /**
     * Get and consume damage for subBar movement (returns pixels to move, only moves once per 100 damage)
     * @return Number of pixels to move right (1px per 100 damage threshold crossed)
     */
    public double consumeDamageForSubBar() {
        // Calculate how many 100-damage thresholds have been crossed
        double thresholdsCrossed = Math.floor((totalDamageToEnemies - consumedDamageForSubBar) / 100.0);
        
        // Update consumed damage to the next threshold
        if (thresholdsCrossed > 0) {
            consumedDamageForSubBar += thresholdsCrossed * 100.0;
            return thresholdsCrossed; // Return pixels to move (1px per 100 damage)
        }
        
        return 0.0;
    }
    
    /**
     * Reset damage tracking (called when subBars are cleared)
     */
    public void resetDamageTracking() {
        totalDamageToEnemies = 0.0;
        consumedDamageForSubBar = 0.0;
    }
    
    /**
     * Handle subBar reaching left edge (losing condition)
     */
    public void handleSubBarLeftEdge() {
        Observer.characterSlot enemySlot = getEnemySlot();
        if (enemySlot != null) {
            // Apply Mercy (3 turns) buff to enemySlot
            characters.SpecialTalents.applyBuffDebuff(enemySlot, 
                characters.BuffDebuff.getByName("Mercy").copy().withDuration(3));
            SpecialTalents.applyBuffDebuff(enemySlot,BuffDebuff.getByName("Invulnerable").copy().withDuration(3));
            enemySlot.removeBuffDebuffByName("Dragon breath!");
            enemySlot.removeBuffDebuffByName("Flame increment");
            // Update barrier bar to show Mercy buff
            if (battleUI != null) {
                battleUI.updateBarrierBar(enemySlot);
            }
        }
        
        // Set all heroes health to 1
        Observer.characterSlot[] heroes = getAllHeroes();
        float totalHeroMaxHp = 0.0f;
        for (Observer.characterSlot hero : heroes) {
            if (hero != null && hero.getCurrentHp() > 0) {
                totalHeroMaxHp += hero.getCharacter().getHp();
                hero.setCurrentHp(1.0f);
                if (battleUI != null) {
                    battleUI.updateHealthUI(hero);
                }
            }
        }
        
        // Heal enemySlot by 25% of total of all heroes' max health
        if (enemySlot != null && totalHeroMaxHp > 0) {
            float healAmount = totalHeroMaxHp * 0.25f;
            float currentHp = enemySlot.getCurrentHp();
            float maxHp = enemySlot.getCharacter().getHp();
            float newHp = Math.min(maxHp, currentHp + healAmount);
            enemySlot.setCurrentHp(newHp);
            if (battleUI != null) {
                battleUI.updateHealthUI(enemySlot);
            }
        }
        DialogRegistrations.showDialogByTitle("ChigonMockingDialog","current");

    }
    
    /**
     * Handle subBar reaching right edge (winning condition)
     */
    public void handleSubBarRightEdge() {
        Observer.characterSlot enemySlot = getEnemySlot();
        if (enemySlot != null) {
            // Apply Stunned (3 turns) and Vulnerable (3 turns) to enemySlot
            characters.SpecialTalents.applyBuffDebuff(enemySlot, 
                characters.BuffDebuff.getByName("Stunned").copy().withDuration(3));
            characters.SpecialTalents.applyBuffDebuff(enemySlot, 
                characters.BuffDebuff.getByName("Vulnerable").copy().withDuration(3));
            enemySlot.removeBuffDebuffByName("Dragon breath!");
            enemySlot.removeBuffDebuffByName("Flame increment");
            // Update barrier bar to show buffs
            if (battleUI != null) {
                battleUI.updateBarrierBar(enemySlot);
            }
        }
    }
    
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
            if(heroSlot != null&&!heroSlot.getCharacter().getName().equals("Litaru ")) {
                audioManager.playVictoryMusic();
            }
            if (onBattleWon != null) {
                onBattleWon.run();
            }
        }
        if(defeatEnemies.contains("Oufuu daddy")){
            if (onBattleWon != null) {
                audioManager.playVictoryMusic();
                onBattleWon.run();
            }
        }
    }
    
    private void checkLoseCondition() {
        // Check if all heroes are defeated
        Observer.characterSlot[] heroes = getAllHeroes();
        boolean allHeroesDefeated = true;
        
        for (Observer.characterSlot hero : heroes) {
            if (hero != null && hero.getCurrentHp() > 0) {
                allHeroesDefeated = false;
                break;
            }
        }
        
        if (allHeroesDefeated&&heroSlot != null&&!heroSlot.getCharacter().getName().equals("Litaru ")) {
            System.out.println("Defeat! All heroes are defeated!");
            // Play defeat music (if available) or stop battle music
            // audioManager.playDefeatMusic(); // Uncomment if you add defeat music
            
            if (onBattleLost != null) {
                onBattleLost.run();
            }
        }
    }
//------------------------------------------------DAMAGE BONUS------------------------------------------------------------------
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
            if(SpecialTalents.inventory.containsEquipment(attacker,"ashbringer")){
                rageGained*=1.25f;
            }
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
                    float healingAmount = rageConsumed * (rageConsumed / (rageConsumed + Math.min(1000,currentHp)));
                    dmg = -healingAmount; // Negative damage = healing
                    System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + "! Consumed " + rageConsumed + " rage to heal for " + healingAmount + " HP!");
                }
            } else if (skill.getName().equals("Rage Burst")) {
                float currentHp = Math.max(100, attacker.getCurrentHp());
                float maxHp = attacker.getCharacter().getHp();
                dmg =Math.min(1000, 3 * maxHp * (rageConsumed / (10*currentHp + rageConsumed)));
                float heal = (float)dmg/2;
                applyDamage(attacker,-heal);
                battleUI.updateHealthUI(attacker);
                System.out.println(attacker.getCharacter().getName() + " used " + skill.getName() + "! Consumed " + rageConsumed + " rage for " + dmg + " damage!");
            } else {
                // Other rage skills: add rage as bonus damage
                dmg += rageConsumed*1.5;
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
    private float calculateSpecialDmgBonus(Observer.characterSlot attacker,Observer.characterSlot target, Ability.skill skill) {
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
        }else if (skill.getName().equals("Aramute's obliterated")) {
            float currentHp = attacker.getCurrentHp();
            float currentMp = attacker.getCurrentMp();
            int duration = 0;
            if(attacker.getBuffDebuffByName("Invulnerable")!=null){
                duration = attacker.getBuffDebuffByName("Invulnerable").getDuration();
                attacker.removeBuffDebuffByName("Invulnerable");
            }
            applyDamage(attacker,currentHp);
            attacker.regenerateMp(-currentMp);
            specialDmgBonus = (currentHp+currentMp);
            if(duration > 0) {
                attacker.addBuffDebuff(BuffDebuff.getByName("Invulnerable").copy().withDuration(duration));
            }
            battleUI.updateMpUI(attacker);
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
            specialDmgBonus = attacker.getFloatBuffDebuffByName("Judgment")*attacker.getCharacter().getAtk()/4;
        }else if (attacker.getBuffDebuffByName("Sunset")!=null){
            specialDmgBonus = attacker.getCharacter().getHp() - attacker.getCurrentHp();
        }
        //Check buff
        if(isEnemy(attacker)&&target.containsBuffDebuff("Prey")){
            specialDmgBonus = attacker.getCharacter().getAtk();
        }
        
        return specialDmgBonus;
    }

//------------------------------------------------SPECIAL SKILLS------------------------------------------------------------------
    /**
     * Handle special skills that require unique processing
     * @param attacker The character using the skill
     * @param target The target of the skill
     * @param skill The skill being used
     * @return true if skill processing should end early, false to continue with normal processing
     */
    private boolean handleSpecialSkill(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill) {
        // Check Recovery Trial completion
        if (isHero(attacker) && attacker.getBuffDebuffByName("Recovery Trial") != null) {
            Observer.characterSlot[] allHeroes = getAllHeroes();
            boolean allOtherHeroesAtMax = true;
            for (Observer.characterSlot otherHero : allHeroes) {
                if (otherHero != null && otherHero != attacker && otherHero.getCurrentHp() > 0) {
                    if (otherHero.getCurrentHp() < otherHero.getCharacter().getHp()) {
                        allOtherHeroesAtMax = false;
                        break;
                    }
                }
            }
            // If all other heroes are at max HP, complete the trial
            if (allOtherHeroesAtMax) {
                // Complete trial cleanup (removes trial debuff, applies Stunned, removes Banish, etc.)
                completeTrialCleanup();
            }
        }
        
        if(skill.getName().equals("Let me absorb you")){
            if(timeStopSlot != null){
                if(timeStopSlot.getCharacter().getName().equals("Ina")) {
                    if (target.getCurrentHp() <= target.getCharacter().getHp() * 0.4 || target.getCharacter() == attacker.getCharacter()) {
                        timeStop += (float) 0.5;
                        resetLine(attacker);
                    } else {
                        target.heal(-target.getCharacter().getHp() * (float)0.4);
                        battleUI.updateHealthUI(target);
                        battleUI.updateBurningRageBar(target);
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
        else if(skill.getName().equals("Absolute teleportation")){
            //Buff
            BuffDebuff teleBuff = BuffDebuff.getByName("Conserve").copy();
            teleBuff.setStack(attacker.getBuffCount()+1);
            attacker.addBuffDebuff(teleBuff);
            attacker.regenerateMp(-skill.getMpCost());
            // Set timeStopSlot to attacker and initialize timeStop with black action bar length
            timeStopSlot = attacker;
            timeStop = (float)battleUI.getBarWidth();
            // Create the timeStop bar in the UI
            battleUI.createTimeStopBar();
            return true; // End skill processing early
        }else if(skill.getName().equals("Moon wave")){
            if(!target.getCharacter().getName().equals("Leuna")) {
                resetLine(target);
                battleUI.updateHealthUI(target);
                battleUI.updateBurningRageBar(target);
                return false; // End skill processing early
            }
        }else if(skill.getName().equals("Amber sacrifice")){
            if(target.getCurrentHp()>1) {
                applyDamage(target, target.getCurrentHp() / 2);
            }
        }
        else if(skill.getName().equals("Rage empowerment")){
            //Buff
            BuffDebuff rageBuff = BuffDebuff.getByName("Rage empowerment host").copy();
            attacker.addBuffDebuff(rageBuff);
        }
        else if(skill.getName().equals("Burning guts")){
            //Buff
            if(attacker.getBuffDebuffByName("Burning guts cd")==null){
                for(Observer.characterSlot slot : getAllHeroes()){
                    if(slot!=null){
                        slot.getCharacter().addToUniqueValue("Guts",1);
                    }

                }
                BuffDebuff cd = BuffDebuff.getByName("Burning guts cd").copy();
                cd.setStack(1000);
                attacker.addBuffDebuff(cd);
            }
        }
        else if(skill.getName().equals("Tiger claw")){
            applyDamage(attacker,-skill.getAtkScale()*attacker.getCharacter().getAtk()*0.2);
        }
        else if(skill.getName().equals("Dragon tail")){
            pushCharacterLine(target,attacker.getCharacter().getAtk()*0.1);
        }
        else if(skill.getName().equals("Push")){
            pushCharacterLine(target,attacker.getCharacter().getAtk()/target.getCharacter().getAtk()*target.getCharacter().getAV());
        }
        else if(skill.getName().equals("Dragon breath")){
            pushCharacterLine(attacker,200);
        }
        else if(skill.getName().equals("Arua's Arrow")){
            BuffDebuff arua = BuffDebuff.getByName("Arua's charge").copy();
            SpecialTalents.applyBuffDebuff(attacker, arua);
        }
        else if(skill.getName().equals("Arua's Lighting Bolt")){
            attacker.removeBuffDebuffByName("Arua's charge");
        }
        else if(skill.getName().equals("Channeling flame")){
            spawnFireOrb();
        }
        else if(skill.getName().equals("Steady")){
            attacker.getLine().setStartX(getNextLineAct(attacker.getLine())+1);
        }
        else if(skill.getName().equals("CBMark2")){
            setMoving(false);
            if (battleUI != null) {
                battleUI.showHeroSelectionForCBMark2();
            }
        }
        else if(skill.getName().equals("CBUlt")){
            // Reset damage tracking when CBUlt is cast
            totalDamageToEnemies = 0.0;
            consumedDamageForSubBar = 0.0;
            
            for(Observer.characterSlot slot : getAllHeroes()){
                if(slot!=null){
                    slot.setCurrentHp(slot.getCharacter().getHp()*0.6f);
                    if (battleUI != null) {
                        battleUI.updateHealthUI(slot);
                    }
                }
            }
            // Show subBars for CBUlt mechanic
            if (battleUI != null) {
                battleUI.showSubBarsForCBUlt();
            }
        }
        //Check buff
        if(target.getBuffDebuffByName("Challenge")!=null){
            target.removeBuffDebuffByName("Challenge");
            BuffDebuff taunt = BuffDebuff.getByName("Taunt").copy().withDuration(5);
            taunt.setSource(attacker.getCharacter().getName());
            SpecialTalents.applyBuffDebuff(target,taunt);
            SpecialTalents.applyBuffDebuff(target,BuffDebuff.getByName("Excite").copy().withDuration(5));
            SpecialTalents.applyBuffDebuff(attacker,BuffDebuff.getByName("Prey").copy().withDuration(999));
        }
        if(attacker.getBuffDebuffByName("Taunt")!=null&&target.getBuffDebuffByName("Prey")!=null){
            if(attacker.getBuffDebuffByName("Taunt").getDuration()<=1){
                target.removeBuffDebuffByName("Prey");
            }
        }

        handleSpecialEquipmentEffect(attacker,target,skill);
        // No special skill handled, continue with normal processing
        return false;
    }
    private void handleSpecialEquipmentEffect(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill){
        // Check if flame_blade is equipped
        if (SpecialTalents.inventory.containsEquipment(attacker, "flame_blade")) {
            if(isEnemy(target)) {
                SpecialTalents.applyBuffDebuff(target, BuffDebuff.getByName("Burn").copy());
            }
        }
        if (SpecialTalents.inventory.containsEquipment(attacker, "fire_ring")) {
            if (isEnemy(target)) {
                SpecialTalents.applyBuffDebuff(target, BuffDebuff.getByName("Ignite").copy());
            }
        }
        if (SpecialTalents.inventory.containsEquipment(attacker, "blue_flower_staff")) {
            applyDamage(attacker,-attacker.getCharacter().getHp()*0.1);
            attacker.regenerateMp(attacker.getCharacter().getMp()*0.1f);
        }
    }
    private double handleSpecialEquipmentDamage(Observer.characterSlot attacker, Observer.characterSlot target, Ability.skill skill, double baseDamage){
        double equipmentDamageBonus=0;
        if(SpecialTalents.inventory.containsEquipment(attacker, "heart_of_fury")&&attacker.getCurrentHp()<attacker.getCharacter().getHp()*0.36f) {
            equipmentDamageBonus=baseDamage*0.5;
        }
        return equipmentDamageBonus;
    }


    public void spawnFireOrb(){
        if(enemySlot2!=null){
            selectedEnemyTarget=enemySlot;
            enemySlot2=null;
        }
        if(enemySlot3!=null){
            selectedEnemyTarget=enemySlot;
            enemySlot3=null;
        }
        Characters.character fireOrb1 = new Characters.character(36,"Fire Orb",0,0,0,0,1,1000,0,null);
        Characters.character fireOrb2 = new Characters.character(37,"Fire Orb",0,0,0,0,1,1000,0,null);

        Characters.character fireOrb1b = new Characters.character(38,"Fire Orb",0,0,0,0,1,1000,0,null);
        Characters.character fireOrb2b= new Characters.character(39,"Fire Orb",0,0,0,0,1,1000,0,null);

        Observer.characterSlot slotFireOrb1 = new Observer.characterSlot(99,fireOrb1,fireOrb1b,null,1000,0);
        Observer.characterSlot slotFireOrb2 = new Observer.characterSlot(98,fireOrb2,fireOrb2b,null,1000,0);

        if(enemySlot.getCharacter().getUniqueValueAsFloat("Phase 3")>0&&enemySlot.getBuffDebuffByName("Resurrection")!=null){
            fireOrb1.setHp(3000);fireOrb2.setHp(3000);fireOrb1b.setHp(3000);fireOrb2b.setHp(3000);
            slotFireOrb1.setCurrentHp(3000);slotFireOrb2.setCurrentHp(3000);
        }

        enemySlot2=slotFireOrb1;
        enemySlot3=slotFireOrb2;

        // Add health bars for newly spawned enemies in battleUI
        if (battleUI != null) {
            battleUI.addHealthBarForEnemySlot(enemySlot2, 4); // Index 4 for enemy2
            battleUI.addHealthBarForEnemySlot(enemySlot3, 5); // Index 5 for enemy3
        }

    }
    public void spawnSpiritualMonster(){
        int ranHp1 = random(500,1500);
        int ranHp2 = random(500,1500);
        int ranHp3 = random(500,1500);
        Characters.character SM = new Characters.character(36,"Spiritual Monster",100,0,0,0,8,ranHp1,0,null);
        Characters.character SM2 = new Characters.character(SM);
            SM2.setHp(ranHp2);
        Characters.character SM3 = new Characters.character(SM);
            SM3.setHp(ranHp3);
        Characters.character SM1b = new Characters.character(SM);
        Characters.character SM2b = new Characters.character(SM2);
        Characters.character SM3b = new Characters.character(SM3);
        Ability.skill skill = Ability.SkillRegistry.getById(4);
        ArrayList<Ability.skill> skillA =  new ArrayList<>(){};
        skillA.add(skill);
        Observer.characterSlot SM1o = new Observer.characterSlot(1,SM,SM1b,skillA,SM.getHp(),0);
        Observer.characterSlot SM2o = new Observer.characterSlot(1,SM2,SM2b,skillA,SM2.getHp(),0);
        Observer.characterSlot SM3o = new Observer.characterSlot(1,SM3,SM3b,skillA,SM3.getHp(),0);

        setMapEnemies(SM1o,SM2o,SM3o);
        battleUI.addHealthBarForEnemySlot(enemySlot,3);
        battleUI.addHealthBarForEnemySlot(enemySlot2,4);
        battleUI.addHealthBarForEnemySlot(enemySlot3,5);
        battleUI.createLines();
        selectedEnemyTarget=enemySlot;
        selectedTarget=enemySlot;
        if (battleUI.highlightSelection != null) {
            battleUI.highlightSelection.run();
        }
    }


}
