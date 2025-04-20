package com.hireme.internship.utils;

import com.hireme.internship.model.Group;
import com.hireme.internship.model.Internship;
import com.hireme.internship.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database utility class for handling PostgreSQL connections and operations
 */
public class DatabaseUtils {
  private static final Logger LOGGER = Logger.getLogger(DatabaseUtils.class.getName());

  // Database connection properties
  // Connect to the Docker PostgreSQL container
  private static final String DB_URL = "jdbc:postgresql://localhost:5432/hireme_db";
  private static final String DB_USER = "hireme_user";
  private static final String DB_PASSWORD = "hireme_password";

  private static Connection connection = null;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Gets a connection to the database
   * 
   * @return Connection object
   * @throws SQLException if connection fails
   */
  public static Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      try {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        LOGGER.info("Connected to PostgreSQL database");
      } catch (ClassNotFoundException e) {
        LOGGER.log(Level.SEVERE, "PostgreSQL JDBC driver not found", e);
        throw new SQLException("PostgreSQL JDBC driver not found", e);
      }
    }
    return connection;
  }

  /**
   * Initialize database by creating required tables if they don't exist
   */
  public static void initializeDatabase() {
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      // Create shared_internships table
      String createSharedInternshipsTable = "CREATE TABLE IF NOT EXISTS shared_internships (" +
          "id SERIAL PRIMARY KEY, " +
          "internship_id VARCHAR(255) NOT NULL, " +
          "company VARCHAR(255) NOT NULL, " +
          "position VARCHAR(255) NOT NULL, " +
          "description TEXT, " +
          "deadline DATE NOT NULL, " +
          "url VARCHAR(255), " +
          "group_name VARCHAR(255) NOT NULL, " +
          "shared_by VARCHAR(255) NOT NULL, " +
          "shared_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
          ")";

      stmt.execute(createSharedInternshipsTable);

      // Create users table
      createUsersTable();

      LOGGER.info("Database tables initialized");

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error initializing database", e);
    }
  }

  /**
   * Create users table
   */
  public static void createUsersTable() throws SQLException {
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
          "id SERIAL PRIMARY KEY, " +
          "name VARCHAR(255) NOT NULL, " +
          "email VARCHAR(255) NOT NULL UNIQUE, " +
          "password VARCHAR(255) NOT NULL, " +
          "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
          ")";

      stmt.execute(createUsersTable);
      LOGGER.info("Users table initialized");
    }
  }

  /**
   * Save a user to the database
   */
  public static boolean saveUser(User user, String password) throws SQLException {
    String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?) " +
        "ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name, password = EXCLUDED.password";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, user.getName());
      pstmt.setString(2, user.getEmail());
      pstmt.setString(3, password); // In a real app, this would be hashed

      int rowsAffected = pstmt.executeUpdate();
      return rowsAffected > 0;
    }
  }

  /**
   * Get all users from the database
   */
  public static List<User> getAllUsers() throws SQLException {
    String sql = "SELECT * FROM users";
    List<User> users = new ArrayList<>();

    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        User user = new User(
            rs.getString("name"),
            rs.getString("email"));
        users.add(user);
      }
    }

    return users;
  }

  /**
   * Validate user credentials
   */
  public static boolean validateUserCredentials(String email, String password) throws SQLException {
    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, email);
      pstmt.setString(2, password); // In a real app, we would check against hashed password

      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next(); // Returns true if a matching user was found
      }
    }
  }

  /**
   * Share an internship with a group
   * 
   * @param internshipId Internship ID
   * @param company      Company name
   * @param position     Position title
   * @param description  Job description
   * @param deadline     Application deadline
   * @param url          Job posting URL
   * @param groupName    Group name
   * @param sharedBy     User who shared
   * @return true if successful, false otherwise
   */
  public static boolean shareInternship(String internshipId, String company, String position,
      String description, java.util.Date deadline,
      String url, String groupName, String sharedBy) {
    String sql = "INSERT INTO shared_internships (internship_id, company, position, description, " +
        "deadline, url, group_name, shared_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, internshipId);
      pstmt.setString(2, company);
      pstmt.setString(3, position);
      pstmt.setString(4, description);
      pstmt.setDate(5, new java.sql.Date(deadline.getTime()));
      pstmt.setString(6, url);
      pstmt.setString(7, groupName);
      pstmt.setString(8, sharedBy);

      int rowsAffected = pstmt.executeUpdate();
      return rowsAffected > 0;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error sharing internship", e);
      return false;
    }
  }

  /**
   * Get all internships shared with a specific group
   * 
   * @param groupName Group name
   * @return ResultSet containing shared internships
   */
  public static ResultSet getSharedInternships(String groupName) {
    try {
      Connection conn = getConnection();
      String sql = "SELECT * FROM shared_internships WHERE group_name = ? ORDER BY shared_date DESC";
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, groupName);
      return pstmt.executeQuery();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error retrieving shared internships", e);
      return null;
    }
  }

  /**
   * Get all shared internships for a specific user based on their group
   * memberships
   */
  public static List<Internship> getSharedInternshipsForUser(User user) {
    List<Internship> sharedInternships = new ArrayList<>();

    // For demo purposes, let's retrieve all shared internships
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM shared_internships ORDER BY shared_date DESC")) {

      while (rs.next()) {
        Internship internship = new Internship(
            rs.getString("company"),
            rs.getString("position"),
            rs.getString("description"),
            rs.getDate("deadline"),
            rs.getString("url"));
        sharedInternships.add(internship);
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error retrieving shared internships for user", e);
    }

    return sharedInternships;
  }

  /**
   * Close database resources properly
   */
  public static void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
        LOGGER.info("Database connection closed");
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Error closing database connection", e);
      }
    }
  }
}
