import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.net.URL;
import javax.swing.*;
import java.util.List;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class App extends JFrame {
    private String currentUser = ConfigManager.getCurrentUser() == null ? "": ConfigManager.getCurrentUser();
    private CardLayout cardLayout;
    private DefaultTableModel model;
    private JTable leaderboardTable;
    private JPanel mainPanel, gamePanel;
    private JLabel timerLabel, scoreLabel, definitionLabel, gameHighScoreLabel, wordLabel;
    private JTextField guessField;
    private JButton submitButton;
    private Timer countdownTimer;
    private int timeLeft = ConfigManager.getTime();
    private int score = ConfigManager.getCurrentScore();
    private JButton playButton, startButton;
    private int highScore = ConfigManager.getUserHighScore(currentUser) == null ? ConfigManager.getHighScore(): ConfigManager.getUserHighScore(currentUser);
    private float voiceVolume = ConfigManager.getVolume();
    private String previousWord;
    private String currentWord = ConfigManager.getPreviousWord();
    private String definition = ConfigManager.getDefinition(); 
    private String definitionState = definition.isEmpty() ? "Loading Definition" : definition;
    private String audioUrl = ConfigManager.getAudioUrl();
    public static int savedDifficulty = ConfigManager.getDifficulty();
    final String[] selectedItem = new String[1];
    private static final int TIME_LIMIT = 15; // 15 seconds
    private int correctGuess = 0;
    private JLabel personalScoreLabel;
    private String[] columnNames = {"Rank", "Top Players", "High Score"};
    private boolean playing = ConfigManager.getIsPlaying();
    private boolean isAccess = ConfigManager.getIsAccess();
    private String buttonState = playing == false ? "Play" : "Resume";
    
    public App() {
        setTitle("EcoBee");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Font font = new Font("Arial", Font.BOLD, 16);

        Color buttonColor = new Color(34, 177, 76); // Dark Green
        Color bgColor = new Color(245, 245, 220); // Beige
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(bgColor);
        this.setJMenuBar(menuBar);

        JMenu menu = new JMenu("Menu");
        menu.setFont(font);
        menuBar.add(menu);

        JMenuItem home = new JMenuItem("Home");
        home.setFont(font);
        home.setBackground(bgColor);
        JMenuItem settings = new JMenuItem("Settings");
        settings.setFont(font);
        settings.setBackground(bgColor);
        JMenuItem exit = new JMenuItem("Logout and Exit");
        exit.setFont(font);
        exit.setBackground(bgColor);
        menu.add(home);
        menu.add(settings);
        menu.add(exit);
        
        // Main Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(bgColor);
       
        //Main page
        JPanel homePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        homePanel.setBackground(bgColor);
        
        //EcoBee Label
        JLabel welcomeLabel = new JLabel("Welcome to");
        welcomeLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.03; // Small weight to prevent stretching
        gbc.anchor = GridBagConstraints.CENTER;
        homePanel.add(welcomeLabel, gbc);

        JLabel ecoBeeLabel = new JLabel("EcoBee");
        ecoBeeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        ecoBeeLabel.setForeground(new Color(34, 177, 76));
        ecoBeeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        
        gbc.anchor = GridBagConstraints.CENTER;
        homePanel.add(ecoBeeLabel, gbc);

        //Start button
        startButton = new JButton("START");
        startButton.setBackground(buttonColor); // Dark Green
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setPreferredSize(new Dimension(140, 50));
        startButton.setBorder(BorderFactory.createEmptyBorder());
        startButton.setOpaque(true);
        startButton.setContentAreaFilled(true);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));

        gbc.gridy = 2;
        gbc.weighty = 0.03; // Ensures vertical centering by pushing down the label
        gbc.anchor = GridBagConstraints.CENTER;
        homePanel.add(startButton, gbc);
        
        //Login page
        JPanel login = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.NONE;
        login.setBackground(bgColor);
        JLabel loginLabel = new JLabel("Login Form", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.03;
        login.add(loginLabel, gbc);
        
        JPanel userNamePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        userNamePanel.setBackground(bgColor);
        JLabel userNameLabel = new JLabel("Username: ", SwingConstants.CENTER);
        userNameLabel.setFont(font);
        
        JTextField userNameField = new JTextField(10);
        userNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        userNamePanel.add(userNameLabel);
        userNamePanel.add(userNameField);
        
        JLabel passwordLabel = new JLabel("Password: ", SwingConstants.CENTER);
        passwordLabel.setFont(font);
        
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        userNamePanel.add(passwordLabel);
        userNamePanel.add(passwordField);
        
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(bgColor);
        showPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton forgotButton = new JButton("Forgot password");
        forgotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                cardLayout.show(mainPanel, "change");
            }
        });
        userNamePanel.add(forgotButton);
        userNamePanel.add(showPassword);
        
        // Action Listener to toggle password visibility
        showPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPassword.isSelected()) {
                    passwordField.setEchoChar((char) 0); // Show password
                } else {
                    passwordField.setEchoChar('•'); // Hide password
                }
            }
        });
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(140, 30));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String username = userNameField.getText().trim();
                String password = passwordField.getText().trim();
                if(ConfigManager.userExists(username)){
                    if(ConfigManager.correctPass(username, password)){
                        isAccess = true;
                        currentUser = username;
                        ConfigManager.setCurrentUser(username);
                        ConfigManager.setIsAccess(isAccess);
                        highScore = ConfigManager.getUserHighScore(username);
                        //highScoreLabel.setText("High Score: " + highScore);
                        String updateHighScore = username + ", Your High Score is: " + highScore;
                        personalScoreLabel.setText(updateHighScore);
                        gameHighScoreLabel.setText("High Score" + highScore);
                        cardLayout.show(mainPanel, "Home");
                    } else {
                        JOptionPane.showMessageDialog(login, "Incorrect Password!");
                    }
                } else {
                    JOptionPane.showMessageDialog(login, "User not found");
                }
            }
        });
        
        JButton createButton = new JButton("Create Account");
        createButton.setBackground(buttonColor);
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(140, 30));
        createButton.setBorder(BorderFactory.createEmptyBorder());
        createButton.setOpaque(true);
        createButton.setContentAreaFilled(true);
        createButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                cardLayout.show(mainPanel, "create");
            }
        });
        userNamePanel.add(loginButton);
        userNamePanel.add(createButton);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.03;
        login.add(userNamePanel, gbc);

        //Create Account Page
        JPanel createAccountPanel = new JPanel(new GridBagLayout());
        createAccountPanel.setBackground(bgColor);

        JLabel createLabel = new JLabel("Create Account", SwingConstants.CENTER);
        createLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.03;
        createAccountPanel.add(createLabel, gbc);
        
        JPanel createAccForm = new JPanel(new GridLayout(6, 2, 10, 10));
        createAccForm.setBackground(bgColor);

        JLabel email = new JLabel("Email: ", SwingConstants.CENTER);
        email.setFont(font);
        JTextField emailField = new JTextField(10);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        createAccForm.add(email);
        createAccForm.add(emailField);

        JLabel userName = new JLabel("Username: ", SwingConstants.CENTER);
        userName.setFont(font);
        
        JTextField newUserNameField = new JTextField(10);
        newUserNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        createAccForm.add(userName);
        createAccForm.add(newUserNameField);
        
        JLabel newPasswordLabel = new JLabel("Password: ", SwingConstants.CENTER);
        newPasswordLabel.setFont(font);
        
        JPasswordField newPasswordField = new JPasswordField(10);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 18));
        createAccForm.add(newPasswordLabel);
        createAccForm.add(newPasswordField);
        
        JLabel confirmLabel = new JLabel("Confirm Password: ", SwingConstants.CENTER);
        confirmLabel.setFont(font);
        
        JPasswordField confirmField = new JPasswordField(10);
        confirmField.setFont(new Font("Arial", Font.PLAIN, 18));
        createAccForm.add(confirmLabel);
        createAccForm.add(confirmField);
        
        JCheckBox showPassword2 = new JCheckBox("Show Password");
        showPassword2.setBackground(bgColor);
        showPassword2.setFont(new Font("Arial", Font.PLAIN, 14));
        
        createAccForm.add(new JLabel());
        createAccForm.add(showPassword2);
        
        //Show password
        showPassword2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPassword2.isSelected()) {
                    newPasswordField.setEchoChar((char) 0);
                    confirmField.setEchoChar((char) 0); // Show password
                } else {
                    confirmField.setEchoChar('•');
                    newPasswordField.setEchoChar('•'); // Hide password
                }
            }
        });
        
        JButton already = new JButton("Proceed to login");
        already.setBackground(new Color(34, 177, 76));
        already.setForeground(Color.WHITE);
        already.setFont(new Font("Arial", Font.BOLD, 16));
        already.setFocusPainted(false);
        already.setBorderPainted(false);
        already.setPreferredSize(new Dimension(140, 30));
        already.setBorder(BorderFactory.createEmptyBorder());
        already.setOpaque(true);
        already.setContentAreaFilled(true);
        already.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        already.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                cardLayout.show(mainPanel, "login");
            }
        });
        
        JButton createNewButton = new JButton("Create Account");
        createNewButton.setBackground(new Color(34, 177, 76));
        createNewButton.setForeground(Color.WHITE);
        createNewButton.setFont(new Font("Arial", Font.BOLD, 16));
        createNewButton.setFocusPainted(false);
        createNewButton.setBorderPainted(false);
        createNewButton.setPreferredSize(new Dimension(140, 30));
        createNewButton.setBorder(BorderFactory.createEmptyBorder());
        createNewButton.setOpaque(true);
        createNewButton.setContentAreaFilled(true);
        createNewButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        createNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String email = emailField.getText().trim();
                String userName = newUserNameField.getText().trim();
                String password = newPasswordField.getText().trim();
                String confirmPass = confirmField.getText().trim();
                if(ConfigManager.userExists(userName)){
                    JOptionPane.showMessageDialog(createAccountPanel, "User already exist");
                } else {
                    //Check email validity
                    if(!isValidEmail(email)){
                        return;
                    }
                    if(ConfigManager.emailExists(email)){
                        JOptionPane.showMessageDialog(createAccountPanel, "Email already exist");
                        return;
                    }
                    //Check password strength
                    if(password.length()>=6){
                        if(password.matches(".*[!`~<>?@#$%^&*()_\\-\\+\\=\\/|,.:;\\[\\]{}\'\"].*")){
                            if(password.equals(confirmPass)){
                                currentUser = userName;
                                ConfigManager.addUser(userName, email, password, 0);
                                isAccess = true;
                                ConfigManager.setCurrentUser(userName);
                                ConfigManager.setIsAccess(isAccess);
                                highScore = ConfigManager.getUserHighScore(userName);
                                String updateHighScore = userName + ", Your High Score is: " + highScore;
                                updateTable();
                                personalScoreLabel.setText(updateHighScore);
                                gameHighScoreLabel.setText("High Score" + highScore);
                                emailField.setText("");
                                newUserNameField.setText("");
                                newPasswordField.setText("");
                                confirmField.setText("");
                                cardLayout.show(mainPanel, "Home");
                            } else {
                                JOptionPane.showMessageDialog(createAccountPanel, "Password does not match");
                            }
                        }else {
                            JOptionPane.showMessageDialog(createAccountPanel, "Weak Password: Must contain atleast 1 special character");
                        }
                        
                    }else {
                        JOptionPane.showMessageDialog(createAccountPanel, "Password must be atleast 6 characters or longer");
                    }
                }
            }
        });
        createAccForm.add(already);
        createAccForm.add(createNewButton);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.03;
        createAccountPanel.add(createAccForm, gbc);

        //Change password window
        JPanel changePassPanel = new JPanel(new GridBagLayout());
        changePassPanel.setBackground(bgColor);
        
        JLabel changeLabel = new JLabel("Change Password", SwingConstants.CENTER);
        changeLabel.setFont(new Font("Arial", Font.BOLD, 35));;
        
        gbc.gridx = 0;
        changePassPanel.add(changeLabel, gbc);

        JPanel changePassForm = new JPanel(new GridLayout(6, 2, 10, 10));
        changePassForm.setBackground(bgColor);

        JLabel email2 = new JLabel("Email: ", SwingConstants.CENTER);
        email2.setFont(font);
        JTextField emailField2 = new JTextField(10);
        emailField2.setFont(new Font("Arial", Font.PLAIN, 18));
        changePassForm.add(email2);
        changePassForm.add(emailField2);

        JTextField userNameField2 = new JTextField(10);

        JLabel userName2 = new JLabel("Username: ", SwingConstants.CENTER);
        userName2.setFont(font);
        userNameField2.setFont(new Font("Arial", Font.PLAIN, 18));
        changePassForm.add(userName2);
        changePassForm.add(userNameField2);
        
        JLabel newPasswordLabel2 = new JLabel("New Password: ", SwingConstants.CENTER);
        newPasswordLabel2.setFont(font);
        
        JPasswordField newPasswordField2 = new JPasswordField(10);
        newPasswordField2.setFont(new Font("Arial", Font.PLAIN, 18));
        changePassForm.add(newPasswordLabel2);
        changePassForm.add(newPasswordField2);
        
        JLabel confirmLabel2 = new JLabel("Confirm New Password: ", SwingConstants.CENTER);
        confirmLabel2.setFont(font);
        
        JPasswordField confirmField2 = new JPasswordField(10);
        confirmField2.setFont(new Font("Arial", Font.PLAIN, 18));
        changePassForm.add(confirmLabel2);
        changePassForm.add(confirmField2);
        
        JCheckBox showPassword3 = new JCheckBox("Show Password");
        showPassword3.setBackground(bgColor);
        showPassword3.setFont(new Font("Arial", Font.PLAIN, 14));
        changePassForm.add(new JLabel()); // Empty label for spacing
        changePassForm.add(showPassword3);
        
        //Show password
        showPassword3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPassword3.isSelected()) {
                    newPasswordField2.setEchoChar((char) 0);
                    confirmField2.setEchoChar((char) 0); // Show password
                } else {
                    confirmField.setEchoChar('•');
                    newPasswordField2.setEchoChar('•'); // Hide password
                }
            }
        });
        JButton already2 = new JButton("Return to login");
        already2.setBackground(new Color(34, 177, 76));
        already2.setForeground(Color.WHITE);
        already2.setFont(new Font("Arial", Font.BOLD, 16));
        already2.setFocusPainted(false);
        already2.setBorderPainted(false);
        already2.setPreferredSize(new Dimension(140, 30));
        already2.setBorder(BorderFactory.createEmptyBorder());
        already2.setOpaque(true);
        already2.setContentAreaFilled(true);
        already2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        already2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                cardLayout.show(mainPanel, "login");
            }
        });
        
        JButton changePassButton = new JButton("Change Pass");
        changePassButton.setBackground(new Color(34, 177, 76));
        changePassButton.setForeground(Color.WHITE);
        changePassButton.setFont(new Font("Arial", Font.BOLD, 16));
        changePassButton.setFocusPainted(false);
        changePassButton.setBorderPainted(false);
        changePassButton.setPreferredSize(new Dimension(140, 30));
        changePassButton.setBorder(BorderFactory.createEmptyBorder());
        changePassButton.setOpaque(true);
        changePassButton.setContentAreaFilled(true);
        changePassButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        changePassButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String email = emailField2.getText().trim();
                String userName = userNameField2.getText().trim();
                String password = newPasswordField2.getText().trim();
                String confirmPass = confirmField2.getText().trim();
                if((ConfigManager.userExists(userName)) && (ConfigManager.emailExists(email))){
                    if(password.equals(confirmPass)){
                        if(ConfigManager.changePasswordByEmail(email, password)){
                            JOptionPane.showMessageDialog(changePassPanel, "Password Changed Successfully");
                            emailField2.setText("");
                            userNameField2.setText("");
                            newPasswordField2.setText("");
                            confirmField2.setText("");
                        } else {
                            JOptionPane.showMessageDialog(changePassPanel, "Password change Unsuccessful");
                        }
                    } else {
                        JOptionPane.showMessageDialog(changePassPanel, "Password does not match");
                    }
                } else {
                    JOptionPane.showMessageDialog(changePassPanel, "User Not Found");
                }
                
            }
        });
        changePassForm.add(already2);
        changePassForm.add(changePassButton);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.03;
        changePassPanel.add(changePassForm, gbc);
        //User tab
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBackground(bgColor);
        //Leaderboard Label
        JLabel leaderboardLabel = new JLabel("Leaderboard");
        leaderboardLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        userPanel.add(leaderboardLabel, gbc);

        // Leaderboard Table
        List<User> users = ConfigManager.getAllUsersSortedByScore();
        Object[][] data = new Object[users.size()][3];

        // Populate the leaderboard
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = i + 1;  // Rank
            data[i][1] = users.get(i).username;
            data[i][2] = users.get(i).highScore;
        }

        // Create table model and table
        model = new DefaultTableModel(data, columnNames);
        leaderboardTable = new JTable(model);
        leaderboardTable.setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setEnabled(false); // Disable editing
        leaderboardTable.setShowVerticalLines(true);
        leaderboardTable.setShowHorizontalLines(true);
        leaderboardTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        leaderboardTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));

        // Add the leaderboard instead of highScoreLabel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        userPanel.add(scrollPane, gbc);

        //High score 
        personalScoreLabel = new JLabel(currentUser + ", Your High Score is: " + highScore);
        personalScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        personalScoreLabel.setForeground(new Color(34, 177, 76)); // Green color for emphasis
        personalScoreLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2; // Position after the leaderboard (was 1 before)
        gbc.weighty = 0.02; // Adjust weight to prevent stretching
        userPanel.add(personalScoreLabel, gbc);
        // Difficulty Dropdown
        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyDropdown = new JComboBox<>(difficulties);
        difficultyDropdown.setFont(new Font("Arial", Font.BOLD, 18));
        difficultyDropdown.setBackground(buttonColor);
        difficultyDropdown.setPreferredSize(new Dimension(140, 50));
        difficultyDropdown.setForeground(Color.WHITE);
        difficultyDropdown.setOpaque(true);
        difficultyDropdown.setFocusable(false);
        difficultyDropdown.setBorder(BorderFactory.createEmptyBorder());
        UIManager.put("ComboBox.buttonArrowColor", Color.WHITE);
        UIManager.put("ComboBox.buttonBackground", buttonColor);
        UIManager.put("ComboBox.buttonHighlight", buttonColor);
        UIManager.put("ComboBox.buttonShadow", buttonColor);
        
        difficultyDropdown.setSelectedIndex(savedDifficulty);
    
        difficultyDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                selectedItem[0] = (String) difficultyDropdown.getSelectedItem();
                System.out.println("Selected item: " + selectedItem[0]);
                switch (selectedItem[0]){
                    case "Easy": savedDifficulty = 0; break;
                    case "Medium": savedDifficulty = 1; break;
                    case "Hard": savedDifficulty = 2; break;
                    default : savedDifficulty = 0; break;
                }
                ConfigManager.setDifficulty(savedDifficulty);
            }
        });
        gbc.gridy = 3;
        gbc.weighty = 0.0;
        userPanel.add(difficultyDropdown, gbc);

        // Play Button
        playButton = new JButton(buttonState);
        playButton.setBackground(buttonColor); // Dark Green
        playButton.setForeground(Color.WHITE);
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);
        playButton.setPreferredSize(new Dimension(140, 50));
        playButton.setBorder(BorderFactory.createEmptyBorder());
        playButton.setOpaque(true);
        playButton.setContentAreaFilled(true);
        playButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        
        gbc.gridy = 4;
        gbc.weighty = 0.0; // Ensures vertical centering by pushing down the label
        gbc.anchor = GridBagConstraints.CENTER;
        userPanel.add(playButton, gbc);

        //Settings tab
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(bgColor);

        JLabel voiceLabel = new JLabel("Voice volume: ");
        voiceLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(voiceLabel, gbc);

        JSlider voiceVolumeSlider = new JSlider(0, 100, (int)(voiceVolume*100));
        voiceVolumeSlider.setMajorTickSpacing(20);
        voiceVolumeSlider.setMinorTickSpacing(5);
        voiceVolumeSlider.setPaintLabels(true);
        voiceVolumeSlider.setPaintTicks(true);
        voiceVolumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e){
                int sliderValue = voiceVolumeSlider.getValue();
                voiceVolume = sliderValue / 100f;
                System.out.println(voiceVolume);
                ConfigManager.setVolume(voiceVolume);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        settingsPanel.add(voiceVolumeSlider, gbc);

        gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(bgColor);
       
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        timerLabel = new JLabel(":" + timeLeft + " SECONDS", SwingConstants.CENTER);
        topPanel.setBackground(bgColor);
        timerLabel.setForeground(new Color(34, 177, 76));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        gameHighScoreLabel = new JLabel("High Score: " + highScore, SwingConstants.CENTER);

        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(gameHighScoreLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Remove HORIZONTAL stretching
        gbc.weightx = 0.5; // Prevent over-expansion
        gamePanel.add(topPanel, gbc);

        //Audio player button
        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        JButton audioButton = new JButton("Hear the word");
       
        audioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                playAudio(audioUrl);
            } 
        });
        audioButton.setPreferredSize(new Dimension(150, 40));

        controlPanel.add(audioButton);
        
        //Pause Button
        JButton pauseButton = new JButton("Pause");
        
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                countdownTimer.stop();
                String[] options = {"Continue", "Quit"};
                int optionChoice = JOptionPane.showOptionDialog(
                    gamePanel, 
                    "Do you want to continue? ", 
                    "Paused...", 
                    0, 
                    0, 
                    null, 
                    options, 
                    options[0]);
                    if(optionChoice == 0){
                        countdownTimer.start();
                    } else {
                        gameOver();
                    }
            }
        });
        pauseButton.setPreferredSize(new Dimension(150, 40));
        controlPanel.add(pauseButton);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        
        gamePanel.add(controlPanel, gbc);

        // Definition Label (Multiline)
        definitionLabel = new JLabel("<html><center>"+definitionState+"</center></html>", SwingConstants.CENTER);
        definitionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        definitionLabel.setPreferredSize(new Dimension(500, 100));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gamePanel.add(definitionLabel, gbc);
        
        //Text Display field
        wordLabel = new JLabel(" ", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 20));
        wordLabel.setOpaque(true);
        wordLabel.setPreferredSize(new Dimension(500, 80));
        wordLabel.setBackground(Color.WHITE);
        wordLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        gbc.gridy = 3;
        gamePanel.add(wordLabel, gbc);

        JLabel spellLabel = new JLabel("Spell it: ");
        spellLabel.setFont(font);
        
        // Input Field
        guessField = new JTextField(20);
        guessField.setFont(new Font("Arial", Font.PLAIN, 18));
        guessField.setBorder(new LineBorder(Color.WHITE, 2, true));
        guessField.setMaximumSize(new Dimension(150, 30));
        
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()!=10){
                    wordLabel.setText(guessField.getText());
                }
            }
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==10){
                 submitButton.doClick();
                }
             }
        });
        JPanel spellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        spellPanel.setBackground(bgColor);
        spellPanel.add(spellLabel);
        spellPanel.add(guessField);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gamePanel.add(spellPanel, gbc);

        // Submit Button
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(buttonColor); // Dark Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder());
        submitButton.setOpaque(true);
        submitButton.setContentAreaFilled(true);
        submitButton.setPreferredSize(new Dimension(300, 40));
        submitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(savedDifficulty);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gamePanel.add(submitButton, gbc);

        mainPanel.add(homePanel, "Main");
        mainPanel.add(login, "login");
        mainPanel.add(createAccountPanel, "create");
        mainPanel.add(changePassPanel, "change");
        mainPanel.add(userPanel, "Home");
        mainPanel.add(settingsPanel, "Settings");
        mainPanel.add(gamePanel, "Game");

        // Button Actions
        home.addActionListener(e -> {
            if(playing){
                showDialog();
            } else {
                cardLayout.show(mainPanel, "Main");
            }
        });
        
        startButton.addActionListener(e -> {
            System.out.println(isAccess);
            if(isAccess == true){
                cardLayout.show(mainPanel, "Home");
            } else {
                cardLayout.show(mainPanel, "login");
            }
        });
        
        settings.addActionListener(e -> {
            if(isAccess){
                cardLayout.show(mainPanel, "Settings");
            } else {
                JOptionPane.showMessageDialog(homePanel, "You must login first!");
            }
            
        });
        exit.addActionListener(e -> {
            if(countdownTimer != null){
                countdownTimer.stop();
            }
            int choice = showLogoutDialog();
            if(choice==0){
                isAccess = false;
                currentUser = "";
                ConfigManager.setIsAccess(isAccess);
                ConfigManager.setCurrentUser(currentUser);
                if(playing){
                    gameOver();
                }
                dispose();
            }else if(choice == 1){
                isAccess = false;
                currentUser = "";
                ConfigManager.setIsAccess(isAccess);
                ConfigManager.setCurrentUser(currentUser);
                if(playing){
                    gameOver();
                }
                cardLayout.show(mainPanel, "login");
            } else if (choice==2){
                ConfigManager.setIsPlaying(playing);
                ConfigManager.setCurrentScore(score);
                ConfigManager.setTimeLeft(timeLeft);
                ConfigManager.setAudioUrl(audioUrl);
                ConfigManager.setDefinition(definition);
                dispose();
                
            }else {
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });
        
        playButton.addActionListener(e -> {
            // Ensure we start a new round after the game is over
            if(playing == false){
                System.out.println();
                playButton.setText("Resume");
                startNewRound(savedDifficulty);
                cardLayout.show(mainPanel, "Game");
            } else {
                startTimer();
                cardLayout.show(mainPanel, "Game");
                playAudio(audioUrl);
            }
            
        });
        
        // Adding components to Frame
        //add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                if(countdownTimer != null){
                    countdownTimer.stop();
                }
                int choice = showExitDialog();
                if(choice==0){
                    ConfigManager.setIsPlaying(playing);
                    ConfigManager.setCurrentScore(score);
                    ConfigManager.setTimeLeft(timeLeft);
                    ConfigManager.setAudioUrl(audioUrl);
                    ConfigManager.setDefinition(definition);
                    dispose();
                } else if (choice==1){
                    if(playing){
                        gameOver();
                    }
                    dispose();
                }else {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
        if (email.matches(emailRegex)) {
            System.out.println("Valid email format.");
            return true;
        } else {
            System.out.println("Invalid email format.");
            return false;
        }
    }
    private void startNewRound(int difficulty) {
        wordLabel.setForeground(Color.BLACK);
        resetTimer();
        
        while (true) { 
            if(playing == true){
                currentWord = previousWord;
            } else {
                currentWord = RandomWord.getRandomWord(difficulty); 
                if(currentWord.equalsIgnoreCase(previousWord)){
                    System.out.println("Same word generated");
                    continue;
                }
                
            }
            
            try {
                String definitionResponse = API_REQUEST.getWordDefinition(currentWord);
                String[] parts = definitionResponse.split("\\|\\|"); // Split definition and audio URL
                definition = parts[0];
                audioUrl = parts.length > 1 ? parts[1] : "No audio";
    
                if (definition.equals("Definition not found.")) {
                    System.out.println("Skipping invalid word: " + currentWord);
                    continue;
                }
    
                definitionLabel.setText("<html><center>" + definition + "</center></html>");
                
                // Check if no audio file is found
                if(audioUrl.equals("No audio")) {
                    System.out.println("No audio found skipping word... the word is: " + currentWord);
                    continue;
                } 

                playAudio(audioUrl);
                playing = true;
                
                ConfigManager.setPreviousWord(currentWord);
    
                break; // Exit loop once a valid word is found
    
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                definitionLabel.setText("<html><center>Error fetching definition!</center></html>");
            }
        }
    
        String underlinedHint = "_ ".repeat(currentWord.length());
        wordLabel.setText(underlinedHint);
        guessField.setText(""); 
        guessField.requestFocusInWindow();
    
        resetTimer();
        startTimer();
    }
    
    public void playAudio(String audioUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(audioUrl);
                InputStream audioStream = url.openStream();
                BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);

                AdvancedPlayer player = new AdvancedPlayer(bufferedStream, new JavaSoundAudioDeviceWithVolume(voiceVolume));
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        System.out.println("Playback finished.");
                    }
                });
                player.play();
            } catch (IOException | JavaLayerException e) {
                System.out.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    private void resetTimer() {
        // Stop and reset the timer if it's already running
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    
        // Reinitialize the timer with the starting time
        if(correctGuess >= 5 && correctGuess < 10){
            timeLeft = 12;
        } else if(correctGuess >= 10 && correctGuess < 15){
            timeLeft = 9;
        } else if(correctGuess >= 15){
            timeLeft = 6;
        } else {
            timeLeft = TIME_LIMIT;
        }
        
        timerLabel.setText(":" + timeLeft + " SECONDS");
    }
    
    
    private void startTimer() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--; // Decrease time left
                timerLabel.setText(":" + timeLeft + " SECONDS"); // Update the timer label

                if (timeLeft <= 0) {
                    countdownTimer.stop(); // Stop the timer when time runs out
                    gameOver(); // Trigger game over
                }
            }
        });
        countdownTimer.start(); // Start the new timer
    }
    
    private void checkAnswer(int difficulty) {
        String guess = guessField.getText().trim();
        currentWord = currentWord.toLowerCase();
        countdownTimer.stop();
    
        if (guess.equalsIgnoreCase(currentWord)) {
            if(savedDifficulty == 0){
                score += 10; // Increase score for correct guess
            } else if(savedDifficulty == 1){
                score += 20;
            } else if(savedDifficulty == 2){
                score += 30;
            }
            correctGuess++;
            
            scoreLabel.setText("Score: " + score); 
            
    
            // Update high score if necessary
            if (score > highScore) {
                highScore = score;  // Set high score to current score
                gameHighScoreLabel.setText("High Score: " + highScore);
                ConfigManager.setHighScore(score);
                ConfigManager.updateUserHighScore(currentUser, highScore);
                updateTable();
                personalScoreLabel.setText(currentUser +", Your High Score is: " + highScore); 
            }
            previousWord = currentWord;
            playing = false;
            ConfigManager.setIsPlaying(playing);
            startNewRound(savedDifficulty); // Start a new round
        } else {
            wordLabel.setForeground(Color.RED);
            gameOver(); // End the game if the guess is incorrect
        }
    }

    private void updateTable(){
        List<User> users = ConfigManager.getAllUsersSortedByScore();
                Object[][] data = new Object[users.size()][3];
                for (int i = 0; i < users.size(); i++) {
                    data[i][0] = i + 1;  
                    data[i][1] = users.get(i).username;
                    data[i][2] = users.get(i).highScore;
                }
                DefaultTableModel newmodel = new DefaultTableModel(data, columnNames);
                leaderboardTable.setModel(newmodel);
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
                    leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }
    }

    private void gameOver() {
        playing = false;
        playButton.setText("Play");
        String[] options = {"Play Again", "Quit for now"};
        int choice = JOptionPane.showOptionDialog(
                    gamePanel, 
                    "Game Over!\nFinal Score: " + score + "\nHigh Score: " + highScore + "\nThe word is: "+ currentWord, 
                    "Game Over!", 
                    0, 
                    0, 
                    null, 
                    options, 
                    options[0]);
    
        // Set the high score label to the correct value
        personalScoreLabel.setText(currentUser + "High Score: " + highScore); // Update high score label
        
        correctGuess = 0;
        score = 0;
        ConfigManager.setCurrentScore(score);
        timeLeft = TIME_LIMIT;
        scoreLabel.setText("Score: " + score);
        timerLabel.setText(":" + timeLeft + " SECONDS");
        definitionLabel.setText(""); // Clear the definition
    
        // Stop the timer
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        if(choice == 0){
            startNewRound(savedDifficulty);
        } else{
            // Show the home panel after game over
            cardLayout.show(mainPanel, "Home");
        }
        
    }

    private void showDialog(){
        int choice = JOptionPane.showConfirmDialog(gamePanel, 
        "Are you sure? This action will pause the game.", 
        "Confirmation", 
        0);
        if(choice == 0){
            countdownTimer.stop();
            cardLayout.show(mainPanel, "Home");
        }
    }
    private int showLogoutDialog(){
        String[] options = {"Logout & Quit", "Logout", "Quit", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                    null, 
                    "Are you sure? ", 
                    "Closing app...", 
                    0, 
                    0, 
                    null, 
                    options, 
                    options[0]);
        return choice;
    }

    private int showExitDialog(){
        String[] options = {"Quit & Save", "Quit", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                    null, 
                    "Are you sure? ", 
                    "Closing app...", 
                    0, 
                    0, 
                    null, 
                    options, 
                    options[0]);
        return choice;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}