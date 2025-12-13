package dialog;

import characters.Observer;
import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed to dialogs, containing heroes, mood, and custom objects
 */
public class DialogContext {
    private Map<String, Object> contextData;
    private Observer.characterSlot[] heroes;
    
    public DialogContext() {
        this.contextData = new HashMap<>();
        this.heroes = new Observer.characterSlot[0];
    }
    
    public DialogContext(Observer.characterSlot[] heroes) {
        this();
        this.heroes = heroes;
    }
    
    /**
     * Store any object in context
     */
    public void put(String key, Object value) {
        contextData.put(key, value);
    }
    
    /**
     * Retrieve object from context
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = contextData.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Get a hero by name
     */
    public Observer.characterSlot getHero(String name) {
        for (Observer.characterSlot hero : heroes) {
            if (hero != null && hero.getCharacter().getName().equals(name)) {
                return hero;
            }
        }
        return null;
    }
    
    /**
     * Check if a hero exists in the party
     */
    public boolean hasHero(String name) {
        return getHero(name) != null;
    }
    
    /**
     * Get hero mood (if stored in unique values)
     */
    public float getHeroMood(String heroName) {
        Observer.characterSlot hero = getHero(heroName);
        if (hero != null) {
            try {
                return hero.getCharacter().getUniqueValueAsFloat("Mood");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Get all heroes
     */
    public Observer.characterSlot[] getHeroes() {
        return heroes;
    }
    
    /**
     * Set heroes
     */
    public void setHeroes(Observer.characterSlot[] heroes) {
        this.heroes = heroes;
    }
}

