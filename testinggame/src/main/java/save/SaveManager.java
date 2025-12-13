package save;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages save file operations (create, read, delete)
 * Saves to a "saves" folder in the working directory
 */
public class SaveManager {
    private static final String SAVES_DIR = "saves";
    private static final String SAVE_FILE_EXTENSION = ".save";
    
    /**
     * Get the saves directory path
     */
    private static Path getSavesDirectory() {
        Path savesDir = Paths.get(SAVES_DIR);
        if (!Files.exists(savesDir)) {
            try {
                Files.createDirectories(savesDir);
            } catch (IOException e) {
                System.err.println("Failed to create saves directory: " + e.getMessage());
            }
        }
        return savesDir;
    }
    
    /**
     * Get the full path to a save file
     * @param saveName Name of the save (without extension)
     * @return Path to the save file
     */
    private static Path getSaveFilePath(String saveName) {
        return getSavesDirectory().resolve(saveName + SAVE_FILE_EXTENSION);
    }
    
    /**
     * Save game data to a file
     * @param saveName Name of the save file (without extension)
     * @param saveData The data to save
     * @return true if save was successful, false otherwise
     */
    public static boolean saveGame(String saveName, SaveData saveData) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.err.println("Save name cannot be empty");
            return false;
        }
        
        if (saveData == null) {
            System.err.println("Save data cannot be null");
            return false;
        }
        
        Path savePath = getSaveFilePath(saveName);
        
        try {
            // Create saves directory if it doesn't exist
            Files.createDirectories(savePath.getParent());
            
            // Write save data as a simple text format
            // Format: Line 1: Available characters (comma-separated)
            //         Line 2: Gold coin
            //         Line 3: Status string
            try (PrintWriter writer = new PrintWriter(new FileWriter(savePath.toFile()))) {
                // Write available characters
                if (saveData.getAvailableCharacters() != null && !saveData.getAvailableCharacters().isEmpty()) {
                    writer.println(String.join(",", saveData.getAvailableCharacters()));
                } else {
                    writer.println(); // Empty line
                }
                
                // Write gold coin
                writer.println(saveData.getGoldCoin());
                
                // Write status string
                if (saveData.getStatus() != null) {
                    writer.println(saveData.getStatus());
                } else {
                    writer.println(); // Empty line
                }
            }
            
            System.out.println("Game saved successfully to: " + savePath.toAbsolutePath());
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Load game data from a file
     * @param saveName Name of the save file (without extension)
     * @return SaveData object if load was successful, null otherwise
     */
    public static SaveData loadGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.err.println("Save name cannot be empty");
            return null;
        }
        
        Path savePath = getSaveFilePath(saveName);
        
        if (!Files.exists(savePath)) {
            System.err.println("Save file not found: " + savePath.toAbsolutePath());
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(savePath)) {
            SaveData saveData = new SaveData();
            
            // Read available characters (line 1)
            String charactersLine = reader.readLine();
            if (charactersLine != null && !charactersLine.trim().isEmpty()) {
                String[] characters = charactersLine.split(",");
                List<String> characterList = new ArrayList<>();
                for (String character : characters) {
                    String trimmed = character.trim();
                    if (!trimmed.isEmpty()) {
                        characterList.add(trimmed);
                    }
                }
                saveData.setAvailableCharacters(characterList);
            } else {
                saveData.setAvailableCharacters(new ArrayList<>());
            }
            
            // Read gold coin (line 2)
            String goldLine = reader.readLine();
            if (goldLine != null && !goldLine.trim().isEmpty()) {
                try {
                    saveData.setGoldCoin(Integer.parseInt(goldLine.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid gold coin value in save file, defaulting to 0");
                    saveData.setGoldCoin(0);
                }
            } else {
                saveData.setGoldCoin(0);
            }
            
            // Read status string (line 3)
            String statusLine = reader.readLine();
            if (statusLine != null) {
                saveData.setStatus(statusLine);
            } else {
                saveData.setStatus("");
            }
            
            System.out.println("Game loaded successfully from: " + savePath.toAbsolutePath());
            return saveData;
            
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if a save file exists
     * @param saveName Name of the save file (without extension)
     * @return true if the save file exists, false otherwise
     */
    public static boolean saveExists(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return false;
        }
        
        Path savePath = getSaveFilePath(saveName);
        return Files.exists(savePath);
    }
    
    /**
     * Delete a save file
     * @param saveName Name of the save file (without extension)
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteSave(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.err.println("Save name cannot be empty");
            return false;
        }
        
        Path savePath = getSaveFilePath(saveName);
        
        if (!Files.exists(savePath)) {
            System.err.println("Save file not found: " + savePath.toAbsolutePath());
            return false;
        }
        
        try {
            Files.delete(savePath);
            System.out.println("Save file deleted: " + savePath.toAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to delete save file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * List all available save files
     * @return List of save file names (without extension)
     */
    public static List<String> listSaves() {
        List<String> saves = new ArrayList<>();
        Path savesDir = getSavesDirectory();
        
        if (!Files.exists(savesDir)) {
            return saves;
        }
        
        try {
            Files.list(savesDir)
                .filter(path -> path.toString().endsWith(SAVE_FILE_EXTENSION))
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    String saveName = fileName.substring(0, fileName.length() - SAVE_FILE_EXTENSION.length());
                    saves.add(saveName);
                });
        } catch (IOException e) {
            System.err.println("Failed to list save files: " + e.getMessage());
        }
        
        return saves;
    }
}

