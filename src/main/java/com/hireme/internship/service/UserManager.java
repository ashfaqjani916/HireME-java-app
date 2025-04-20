package com.hireme.internship.service;

import com.hireme.internship.model.User;
import com.hireme.internship.utils.DatabaseUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages users in the system with authentication
 */
public class UserManager {
  private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

  // In-memory store for demo purposes
  private static final Map<String, User> usersByEmail = new HashMap<>();
  private static final Map<String, String> userPasswords = new HashMap<>();

  private static User currentUser;
  private static UserManager instance;

  private UserManager() {
    initializeDatabase();

    // Add demo users for testing if none exist
    if (getAllUsers().isEmpty()) {
      registerUser("John Doe", "john@example.com", "password1");
      registerUser("Jane Smith", "jane@example.com", "password2");
      registerUser("Bob Johnson", "bob@example.com", "password3");
    }
  }

  /**
   * Get the singleton instance
   */
  public static synchronized UserManager getInstance() {
    if (instance == null) {
      instance = new UserManager();
    }
    return instance;
  }

  /**
   * Initialize the users table in database
   */
  private void initializeDatabase() {
    try {
      DatabaseUtils.createUsersTable();
      loadUsersFromDatabase();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize user database", e);
      // Create demo users in memory as fallback
    }
  }

  /**
   * Load users from database
   */
  private void loadUsersFromDatabase() {
    try {
      List<User> users = DatabaseUtils.getAllUsers();
      for (User user : users) {
        usersByEmail.put(user.getEmail(), user);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to load users from database", e);
    }
  }

  /**
   * Register a new user
   */
  public boolean registerUser(String name, String email, String password) {
    if (usersByEmail.containsKey(email)) {
      return false; // User already exists
    }

    User newUser = new User(name, email);

    try {
      boolean success = DatabaseUtils.saveUser(newUser, password);
      if (success) {
        usersByEmail.put(email, newUser);
        userPasswords.put(email, password); // For in-memory authentication
        return true;
      }
      return false;
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error registering user in database", e);

      // Fallback to in-memory registration
      usersByEmail.put(email, newUser);
      userPasswords.put(email, password);
      return true;
    }
  }

  /**
   * Authenticate a user
   */
  public User authenticate(String email, String password) {
    try {
      if (DatabaseUtils.validateUserCredentials(email, password)) {
        currentUser = usersByEmail.get(email);
        return currentUser;
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, "Database authentication failed, trying in-memory", e);

      // Fallback to in-memory authentication
      if (usersByEmail.containsKey(email) && userPasswords.getOrDefault(email, "").equals(password)) {
        currentUser = usersByEmail.get(email);
        return currentUser;
      }
    }

    return null;
  }

  /**
   * Get the current authenticated user
   */
  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Set current user (for logout/switch user)
   */
  public void setCurrentUser(User user) {
    currentUser = user;
  }

  /**
   * Get all users in the system
   */
  public List<User> getAllUsers() {
    return new ArrayList<>(usersByEmail.values());
  }

  /**
   * Get a user by email
   */
  public User getUserByEmail(String email) {
    return usersByEmail.get(email);
  }

  /**
   * Logout the current user
   */
  public void logout() {
    currentUser = null;
  }
}
