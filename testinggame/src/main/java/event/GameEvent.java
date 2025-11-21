package event;

public abstract class GameEvent {
    protected String description;

    public GameEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract void trigger();
}