package map;

import java.util.List;
import java.util.ArrayList;

public class MapPath {
    public enum PathType {
        FOREST("Forest Path", "Duong rung - Nhieu quai thu, it su kien"),
        MOUNTAIN("Mountain Path", "Duong nui - It quai, nhieu su kien nguy hiem"),
        VILLAGE("Village Path", "Duong lang - Can bang, co shop va NPC");

        private final String displayName;
        private final String description;

        PathType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    private String id;
    private PathType pathType;
    private List<MapNode> nodes;
    public int currentNodeIndex;
    private boolean completed;

    public MapPath(String id, PathType pathType) {
        this.id = id;
        this.pathType = pathType;
        this.nodes = new ArrayList<>();
        this.currentNodeIndex = 0;
        this.completed = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public PathType getPathType() { return pathType; }
    public void setPathType(PathType pathType) { this.pathType = pathType; }

    public List<MapNode> getNodes() { return nodes; }
    public void setNodes(List<MapNode> nodes) { this.nodes = nodes; }
    public void addNode(MapNode node) { this.nodes.add(node); }

    public int getCurrentNodeIndex() { return currentNodeIndex; }
    public void setCurrentNodeIndex(int currentNodeIndex) { this.currentNodeIndex = currentNodeIndex; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public MapNode getCurrentNode() {
        if (currentNodeIndex >= 0 && currentNodeIndex < nodes.size()) {
            return nodes.get(currentNodeIndex);
        }
        return null;
    }

    public MapNode getNextNode() {
        if (currentNodeIndex + 1 < nodes.size()) {
            return nodes.get(currentNodeIndex + 1);
        }
        return null;
    }

    public boolean moveToNextNode() {
        if (currentNodeIndex + 1 < nodes.size()) {
            currentNodeIndex++;
            return true;
        } else {
            completed = true;
            return false;
        }
    }

    public boolean canProgress() {
        MapNode current = getCurrentNode();
        return current != null && current.isCompleted();
    }

    public int getProgress() {
        return currentNodeIndex;
    }

    public int getTotalNodes() {
        return nodes.size();
    }
}
