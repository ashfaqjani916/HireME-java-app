package com.hireme.internship;

import com.hireme.internship.model.User;
import com.hireme.internship.service.ReminderService;
import com.hireme.internship.ui.LoginUI;
import com.hireme.internship.ui.MainFrame;
import com.hireme.internship.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;

public class InternshipReminderApp {

  public static void main(String[] args) {
    // Enable native look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Initialize database
    try {
      DatabaseUtils.initializeDatabase();
      System.out.println("Database connection established successfully.");
    } catch (Exception e) {
      System.err.println("Error connecting to database: " + e.getMessage());
      System.err.println("Make sure you've started the PostgreSQL Docker container using 'docker-compose up -d'");
      // Continue with app even if DB connection fails
    }

    // Initialize the reminder service
    ReminderService reminderService = new ReminderService();

    // Launch the login UI instead of directly creating a user
    EventQueue.invokeLater(() -> {
      LoginUI loginUI = new LoginUI(reminderService);
      loginUI.setVisible(true);
    });

    // Add shutdown hook to close database connection
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      DatabaseUtils.closeConnection();
    }));
  }
}
