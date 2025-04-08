import java.util.*;

class User {
    String username;
    String email;
    String password;
    int highScore;

    User(String username, String email, String password, int highScore) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.highScore = highScore;
    }
}

public class Config {
    public int highScore = 0;
    public int currentScore = 0;
    public float volume = 1.0f; // Default volume (1.0 = 100%)
    public int difficulty = 0;
    public int timeLeft = 15;
    public boolean playing = false;
    public boolean isAccess = false;
    public String previousWord = "";
    public String definition = "";
    public String audioUrl = "";
    public String currentUser = "";
    public List<User> users = new ArrayList<>();
}
