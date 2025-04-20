package com.hireme.internship.ui;

import com.hireme.internship.model.User;
import com.hireme.internship.service.ReminderService;
import com.hireme.internship.service.UserManager;

import java.awt.*;
import java.awt.event.*;

/**
 * Login screen for user authentication
 */
public class LoginUI extends Frame {

  private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
  private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Light Blue
  private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
  private final Color TEXT_COLOR = new Color(44, 62, 80); // Dark Blue Gray
  private final Color ACCENT_COLOR = new Color(231, 76, 60); // Red

  private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
  private final Font HEADING_FONT = new Font("Arial", Font.BOLD, 16);
  private final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

  private TextField emailField;
  private TextField passwordField;
  private Label messageLabel;
  private UserManager userManager;
  private ReminderService reminderService;

  public LoginUI(ReminderService reminderService) {
    this.reminderService = reminderService;
    this.userManager = UserManager.getInstance();

    setTitle("HireME Login");
    setSize(400, 350);
    setBackground(BACKGROUND_COLOR);
    setLayout(new BorderLayout(10, 10));
    setLocationRelativeTo(null);

    initializeUI();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
      }
    });
  }

  private void initializeUI() {
    // Header Panel
    Panel headerPanel = new Panel();
    headerPanel.setBackground(PRIMARY_COLOR);
    headerPanel.setLayout(new BorderLayout());

    Label titleLabel = new Label("HireME Internship Reminder", Label.CENTER);
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(Color.WHITE);

    headerPanel.add(titleLabel, BorderLayout.CENTER);
    headerPanel.setPreferredSize(new Dimension(400, 60));

    add(headerPanel, BorderLayout.NORTH);

    // Login Form Panel
    Panel formPanel = new Panel();
    formPanel.setLayout(new GridLayout(7, 1, 10, 10));
    formPanel.setBackground(BACKGROUND_COLOR);

    Label loginLabel = new Label("Login to your account", Label.CENTER);
    loginLabel.setFont(HEADING_FONT);
    loginLabel.setForeground(TEXT_COLOR);

    Label emailLabel = new Label("Email:");
    emailLabel.setFont(REGULAR_FONT);
    emailLabel.setForeground(TEXT_COLOR);

    emailField = new TextField(20);
    emailField.setFont(REGULAR_FONT);

    Label passwordLabel = new Label("Password:");
    passwordLabel.setFont(REGULAR_FONT);
    passwordLabel.setForeground(TEXT_COLOR);

    passwordField = new TextField(20);
    passwordField.setEchoChar('*');
    passwordField.setFont(REGULAR_FONT);

    messageLabel = new Label("", Label.CENTER);
    messageLabel.setFont(REGULAR_FONT);
    messageLabel.setForeground(ACCENT_COLOR);

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
    buttonPanel.setBackground(BACKGROUND_COLOR);

    Button loginButton = new Button("Login");
    loginButton.setPreferredSize(new Dimension(100, 30));
    loginButton.setBackground(SECONDARY_COLOR);
    loginButton.setForeground(Color.WHITE);
    loginButton.setFont(REGULAR_FONT);
    loginButton.addActionListener(e -> handleLogin());

    Button registerButton = new Button("Register");
    registerButton.setPreferredSize(new Dimension(100, 30));
    registerButton.setBackground(Color.GRAY);
    registerButton.setForeground(Color.WHITE);
    registerButton.setFont(REGULAR_FONT);
    registerButton.addActionListener(e -> showRegisterDialog());

    buttonPanel.add(loginButton);
    buttonPanel.add(registerButton);

    formPanel.add(loginLabel);
    formPanel.add(emailLabel);
    formPanel.add(emailField);
    formPanel.add(passwordLabel);
    formPanel.add(passwordField);
    formPanel.add(messageLabel);
    formPanel.add(buttonPanel);

    add(formPanel, BorderLayout.CENTER);

    // Add some padding
    add(new Panel(), BorderLayout.WEST);
    add(new Panel(), BorderLayout.EAST);
    add(new Panel(), BorderLayout.SOUTH);
  }

  private void handleLogin() {
    String email = emailField.getText().trim();
    String password = passwordField.getText().trim();

    if (email.isEmpty() || password.isEmpty()) {
      messageLabel.setText("Please enter both email and password");
      return;
    }

    User authenticatedUser = userManager.authenticate(email, password);

    if (authenticatedUser != null) {
      // Authentication successful
      reminderService.registerUser(authenticatedUser);
      reminderService.start();

      // Launch main application UI
      dispose();
      MainFrame mainFrame = new MainFrame(authenticatedUser, reminderService);
      mainFrame.setVisible(true);
    } else {
      // Authentication failed
      messageLabel.setText("Invalid email or password");
    }
  }

  private void showRegisterDialog() {
    Dialog registerDialog = new Dialog(this, "Register New Account", true);
    registerDialog.setSize(350, 250);
    registerDialog.setLayout(new GridLayout(6, 1, 10, 10));
    registerDialog.setLocationRelativeTo(this);
    registerDialog.setBackground(BACKGROUND_COLOR);

    Label nameLabel = new Label("Full Name:");
    TextField nameField = new TextField(20);

    Label emailLabel = new Label("Email:");
    TextField emailField = new TextField(20);

    Label passwordLabel = new Label("Password:");
    TextField passwordField = new TextField(20);
    passwordField.setEchoChar('*');

    Label messageLabel = new Label("", Label.CENTER);
    messageLabel.setForeground(ACCENT_COLOR);

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    Button registerButton = new Button("Register");
    registerButton.setBackground(SECONDARY_COLOR);
    registerButton.setForeground(Color.WHITE);

    Button cancelButton = new Button("Cancel");

    registerButton.addActionListener(e -> {
      String name = nameField.getText().trim();
      String email = emailField.getText().trim();
      String password = passwordField.getText().trim();

      if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        messageLabel.setText("All fields are required");
        return;
      }

      boolean success = userManager.registerUser(name, email, password);

      if (success) {
        registerDialog.dispose();
        this.messageLabel.setText("Registration successful! Please login.");
      } else {
        messageLabel.setText("Registration failed. Email already exists.");
      }
    });

    cancelButton.addActionListener(e -> registerDialog.dispose());

    buttonPanel.add(registerButton);
    buttonPanel.add(cancelButton);

    Panel namePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
    namePanel.add(nameLabel);
    namePanel.add(nameField);

    Panel emailPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
    emailPanel.add(emailLabel);
    emailPanel.add(emailField);

    Panel passwordPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
    passwordPanel.add(passwordLabel);
    passwordPanel.add(passwordField);

    registerDialog.add(namePanel);
    registerDialog.add(emailPanel);
    registerDialog.add(passwordPanel);
    registerDialog.add(messageLabel);
    registerDialog.add(buttonPanel);

    registerDialog.setVisible(true);
  }
}
