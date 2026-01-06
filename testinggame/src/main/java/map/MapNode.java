package map;

import event.GameEvent;
import characters.Characters;
import characters.Observer;
import java.util.List;
import java.util.ArrayList;

public class MapNode {
    public enum NodeType {
        START,
        BATTLE,
        EVENT,
        SHOP,
        REST,
        BOSS,
        RECRUIT
    }

    private String id;
    private String name;
    private String description;
    private NodeType type;
    private List<Observer.characterSlot> enemies;
    private GameEvent event;
    private boolean completed;
    private int positionX;
    private int positionY;
    public MapNode(String id, String name, String description, NodeType type, int x, int y) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.positionX = x;
        this.positionY = y;
        this.enemies = new ArrayList<>();
        this.completed = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

    public List<Observer.characterSlot> getEnemies() { return enemies; }
    public void setEnemies(List<Observer.characterSlot> enemies) { this.enemies = enemies; }
    public void addEnemy(Observer.characterSlot enemy) { this.enemies.add(enemy); }

    public GameEvent getEvent() { return event; }
    public void setEvent(GameEvent event) { this.event = event; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getPositionX() { return positionX; }
    public void setPositionX(int positionX) { this.positionX = positionX; }

    public int getPositionY() { return positionY; }
    public void setPositionY(int positionY) { this.positionY = positionY; }

    public void activate() {
        switch (type) {
            case START:
                // Starting point - just mark as completed
                break;
            case BATTLE:
                // Start battle with enemies
                break;
            case EVENT:
                break;
            case RECRUIT:
                break;
            case SHOP:
                // Open shop interface
                break;
            case REST:
                // Restore HP/MP
                break;
            case BOSS:
                // Start boss battle
                break;
        }
        completed = true;
    }
}
