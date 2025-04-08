import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";

    // Gson instance
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Config config = loadConfig(); // Load config when the class is accessed

    // Load config from JSON file
    public static Config loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            System.out.println("Config file not found or invalid. Using default settings.");
            return new Config(); // Return default config if file doesn't exist
        }
    }

    // Save config to JSON file
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    // Getters
    public static int getHighScore() {
        return config.highScore;
    }

    public static int getCurrentScore(){
        return config.currentScore;
    }

    public static float getVolume() {
        return config.volume;
    }

    public static int getDifficulty(){
        return config.difficulty;
    }

    public static int getTime(){
        return config.timeLeft;
    }

    public static boolean getIsPlaying(){
        return config.playing;
    }

    public static boolean getIsAccess(){
        return config.isAccess;
    }

    public static String getPreviousWord(){
        return config.previousWord;
    }

    public static String getDefinition(){
        return config.definition;
    }

    public static String getAudioUrl(){
        return config.audioUrl;
    }

    public static String getCurrentUser(){
        return config.currentUser;
    }

    // Setters (also auto-save)
    public static void setHighScore(int highScore) {
        config.highScore = highScore;
        saveConfig();
    }

    public static void setCurrentScore(int score){
        config.currentScore = score;
        saveConfig();
    }

    public static void setVolume(float volume) {
        config.volume = volume;
        saveConfig();
    }

    public static void setDifficulty(int difficulty){
        config.difficulty = difficulty;
        saveConfig();
    }

    public static void setIsPlaying(boolean playing){
        config.playing = playing;
        saveConfig();
    }

    public static void setIsAccess(boolean isAccess){
        config.isAccess = isAccess;
        saveConfig();
    }

    public static void setTimeLeft(int time){
        config.timeLeft = time;
        saveConfig();
    }

    public static void setPreviousWord(String word){
        config.previousWord = word;
        saveConfig();
    }

    public static void setDefinition(String definition){
        config.definition = definition;
        saveConfig();
    }

    public static void setAudioUrl(String audioUrl){
        config.audioUrl = audioUrl;
        saveConfig();
    }

    public static void setCurrentUser(String user){
        config.currentUser = user;
        saveConfig();
    }
    
    // User handling
    public static boolean userExists(String username) {
        return config.users.stream().anyMatch(user -> user.username.equals(username));
    }
    public static boolean emailExists(String email) {
        return config.users.stream().anyMatch(user -> user.email.equals(email));
    }
    public static boolean changePasswordByEmail(String email, String newPassword) {
        for (User user : config.users) {
            if (user.email.equals(email)) { // Match user by email
                user.password = newPassword;  // Update the password
                saveConfig(); // Save changes to the config file
                return true; // Password change successful
            }
        }
        return false; 
    }
    
    
    public static boolean correctPass(String username, String password) {
        return config.users.stream().anyMatch(user -> user.username.equals(username) && user.password.equals(password));
    }

    public static boolean addUser(String username, String email, String password, int highScore) {
        if (userExists(username)) return false;
        config.users.add(new User(username, email, password, highScore));
        saveConfig();
        return true;
    }

    public static Integer getUserHighScore(String username) {
        return config.users.stream()
                .filter(user -> user.username.equals(username))
                .map(user -> user.highScore)
                .findFirst()
                .orElse(null);
    }
    public static void updateUserHighScore(String username, int newScore) {
        config.users.stream()
            .filter(user -> user.username.equals(username))
            .findFirst()
            .ifPresent(user -> {
                if (newScore > user.highScore) {
                    user.highScore = newScore;
                    saveConfig();
                }
            });
    }

    public static List<User> getAllUsersSortedByScore() {
        List<User> sortedUsers = new ArrayList<>(config.users);
        sortedUsers.sort((a, b) -> Integer.compare(b.highScore, a.highScore));
        return sortedUsers;
    }
}
