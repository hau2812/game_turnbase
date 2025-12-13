package save;

import java.util.List;

/**
 * Represents the data structure for a save file
 */
public class SaveData {
    private List<String> availableCharacters;
    private int goldCoin;
    private int returnTime;
    private String status;
    
    // Default constructor for JSON deserialization
    public SaveData() {
    }
    
    // Constructor with parameters
    public SaveData(List<String> availableCharacters, int goldCoin, int returnTime, String status) {
        this.availableCharacters = availableCharacters;
        this.goldCoin = goldCoin;
        this.returnTime = returnTime;
        this.status = status;
    }
    
    // Getters and setters
    public List<String> getAvailableCharacters() {
        return availableCharacters;
    }
    
    public void setAvailableCharacters(List<String> availableCharacters) {
        this.availableCharacters = availableCharacters;
    }
    
    public int getGoldCoin() {
        return goldCoin;
    }
    
    public void setGoldCoin(int goldCoin) {
        this.goldCoin = goldCoin;
    }
    
    public int getReturnTime() {
        return returnTime;
    }
    
    public void setReturnTime(int returnTime) {
        this.returnTime = returnTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

