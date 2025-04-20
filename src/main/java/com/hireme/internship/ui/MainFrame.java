package com.hireme.internship.ui;

import com.hireme.internship.model.Group;
import com.hireme.internship.model.Internship;
import com.hireme.internship.model.User;
import com.hireme.internship.service.ReminderService;
import com.hireme.internship.utils.DatabaseUtils;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;

public class MainFrame extends Frame {
  private final ReminderService reminderService;
  private final User currentUser;

  private CardLayout cardLayout;
  private Panel mainPanel;
  private Panel internshipsPanel;
  private Panel groupsPanel;
  private Panel sharedInternshipsPanel;

  private java.awt.List internshipList;
  private java.awt.List groupList;
  private java.awt.List sharedInternshipsList;

  private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
  private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Light Blue
  private final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
  private final Color TEXT_COLOR = new Color(44, 62, 80); // Dark Blue Gray
  private final Color ACCENT_COLOR = new Color(231, 76, 60); // Red

  private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
  private final Font HEADING_FONT = new Font("Arial", Font.BOLD, 16);
  private final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public MainFrame(User user, ReminderService reminderService) {
    this.currentUser = user;
    this.reminderService = reminderService;

    setTitle("HireME Internship Reminder");
    setSize(900, 650);
    setBackground(BACKGROUND_COLOR);
    setResizable(true);
    setLocationRelativeTo(null);

    // Set layout
    setLayout(new BorderLayout(10, 10));

    initializeUI();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        reminderService.stop();
        dispose();
        System.exit(0);
      }
    });
  }

  private void initializeUI() {
    // Create header panel with app name and user info
    Panel headerPanel = createHeaderPanel();
    add(headerPanel, BorderLayout.NORTH);

    // Create navigation sidebar
    Panel navPanel = createNavigationPanel();
    add(navPanel, BorderLayout.WEST);

    // Main content area with card layout
    mainPanel = new Panel();
    cardLayout = new CardLayout();
    mainPanel.setLayout(cardLayout);

    // Create internships panel
    internshipsPanel = createInternshipsPanel();
    mainPanel.add(internshipsPanel, "internships");

    // Create shared internships panel
    sharedInternshipsPanel = createSharedInternshipsPanel();
    mainPanel.add(sharedInternshipsPanel, "shared");

    // Create groups panel
    groupsPanel = createGroupsPanel();
    mainPanel.add(groupsPanel, "groups");

    // Add main panel to frame
    add(mainPanel, BorderLayout.CENTER);

    // Create status bar
    Panel statusPanel = createStatusBar();
    add(statusPanel, BorderLayout.SOUTH);

    // Show internships panel by default
    cardLayout.show(mainPanel, "internships");

    // Load data
    loadUserInternships();
    loadUserGroups();
    loadSharedInternships();
  }

  private Panel createHeaderPanel() {
    Panel headerPanel = new Panel();
    headerPanel.setBackground(PRIMARY_COLOR);
    headerPanel.setLayout(new BorderLayout());

    Label titleLabel = new Label("HireME Internship Reminder", Label.CENTER);
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(Color.WHITE);

    Label userLabel = new Label("Welcome, " + currentUser.getName());
    userLabel.setFont(REGULAR_FONT);
    userLabel.setForeground(Color.WHITE);

    headerPanel.add(titleLabel, BorderLayout.CENTER);
    headerPanel.add(userLabel, BorderLayout.EAST);

    // Add padding
    headerPanel.setPreferredSize(new Dimension(900, 60));

    return headerPanel;
  }

  private Panel createNavigationPanel() {
    Panel navPanel = new Panel();
    navPanel.setLayout(new GridLayout(6, 1, 0, 10));
    navPanel.setBackground(SECONDARY_COLOR);
    navPanel.setPreferredSize(new Dimension(200, 550));

    // Add padding
    navPanel.add(new Label(""));

    // Internships button
    Button internshipsButton = new Button("My Internships");
    internshipsButton.setFont(HEADING_FONT);
    internshipsButton.setForeground(TEXT_COLOR);
    internshipsButton.setBackground(BACKGROUND_COLOR);
    internshipsButton.addActionListener(e -> cardLayout.show(mainPanel, "internships"));
    navPanel.add(internshipsButton);

    // Shared Internships button
    Button sharedButton = new Button("Shared Internships");
    sharedButton.setFont(HEADING_FONT);
    sharedButton.setForeground(TEXT_COLOR);
    sharedButton.setBackground(BACKGROUND_COLOR);
    sharedButton.addActionListener(e -> {
      loadSharedInternships();
      cardLayout.show(mainPanel, "shared");
    });
    navPanel.add(sharedButton);

    // Groups button
    Button groupsButton = new Button("Groups");
    groupsButton.setFont(HEADING_FONT);
    groupsButton.setForeground(TEXT_COLOR);
    groupsButton.setBackground(BACKGROUND_COLOR);
    groupsButton.addActionListener(e -> cardLayout.show(mainPanel, "groups"));
    navPanel.add(groupsButton);

    // Add padding
    navPanel.add(new Label(""));

    return navPanel;
  }

  private Panel createInternshipsPanel() {
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBackground(BACKGROUND_COLOR);

    // Title
    Label titleLabel = new Label("My Internships", Label.CENTER);
    titleLabel.setFont(HEADING_FONT);
    titleLabel.setForeground(TEXT_COLOR);
    panel.add(titleLabel, BorderLayout.NORTH);

    // Internship list
    internshipList = new java.awt.List(10);
    internshipList.setFont(REGULAR_FONT);
    internshipList.setBackground(Color.WHITE);
    panel.add(internshipList, BorderLayout.CENTER);

    // Button panel
    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buttonPanel.setBackground(BACKGROUND_COLOR);

    Button addButton = new Button("Add Internship");
    styleButton(addButton);
    addButton.addActionListener(e -> addInternshipDialog());

    Button removeButton = new Button("Remove Internship");
    styleButton(removeButton);
    removeButton.addActionListener(e -> removeSelectedInternship());

    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private Panel createGroupsPanel() {
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBackground(BACKGROUND_COLOR);

    // Title
    Label titleLabel = new Label("My Groups", Label.CENTER);
    titleLabel.setFont(HEADING_FONT);
    titleLabel.setForeground(TEXT_COLOR);
    panel.add(titleLabel, BorderLayout.NORTH);

    // Group list
    groupList = new java.awt.List(10);
    groupList.setFont(REGULAR_FONT);
    groupList.setBackground(Color.WHITE);
    panel.add(groupList, BorderLayout.CENTER);

    // Button panel
    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buttonPanel.setBackground(BACKGROUND_COLOR);

    Button createButton = new Button("Create Group");
    styleButton(createButton);
    createButton.addActionListener(e -> createGroupDialog());

    Button joinButton = new Button("Join Group");
    styleButton(joinButton);
    joinButton.addActionListener(e -> joinGroupDialog());

    Button leaveButton = new Button("Leave Group");
    styleButton(leaveButton);
    leaveButton.addActionListener(e -> leaveSelectedGroup());

    Button shareButton = new Button("Share Internship");
    styleButton(shareButton);
    shareButton.addActionListener(e -> shareInternshipDialog());

    buttonPanel.add(createButton);
    buttonPanel.add(joinButton);
    buttonPanel.add(leaveButton);
    buttonPanel.add(shareButton);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private Panel createStatusBar() {
    Panel statusPanel = new Panel();
    statusPanel.setLayout(new BorderLayout());
    statusPanel.setBackground(PRIMARY_COLOR);
    statusPanel.setPreferredSize(new Dimension(900, 30));

    Label statusLabel = new Label("Ready", Label.LEFT);
    statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    statusLabel.setForeground(Color.WHITE);

    statusPanel.add(statusLabel, BorderLayout.WEST);

    return statusPanel;
  }

  private void styleButton(Button button) {
    button.setFont(REGULAR_FONT);
    button.setBackground(SECONDARY_COLOR);
    button.setForeground(Color.WHITE);
    button.setPreferredSize(new Dimension(150, 30));
  }

  private void loadUserInternships() {
    internshipList.removeAll();
    for (Internship internship : currentUser.getSavedInternships()) {
      internshipList.add(formatInternshipDisplay(internship));
    }
  }

  private void loadUserGroups() {
    groupList.removeAll();
    for (Group group : currentUser.getGroups()) {
      groupList.add(group.getName() + " (" + group.getMembers().size() + " members)");
    }
  }

  private String formatInternshipDisplay(Internship internship) {
    return internship.getCompany() + " - " + internship.getPosition() +
        " (Due: " + dateFormat.format(internship.getDeadline()) + ")";
  }

  private void addInternshipDialog() {
    Dialog dialog = new Dialog(this, "Add New Internship", true);
    dialog.setLayout(new GridLayout(6, 2, 10, 10));
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(BACKGROUND_COLOR);

    Label companyLabel = new Label("Company:");
    TextField companyField = new TextField(20);

    Label positionLabel = new Label("Position:");
    TextField positionField = new TextField(20);

    Label descriptionLabel = new Label("Description:");
    TextField descriptionField = new TextField(20);

    Label deadlineLabel = new Label("Deadline (yyyy-MM-dd):");
    TextField deadlineField = new TextField(20);
    deadlineField.setText("yyyy-MM-dd");

    Label urlLabel = new Label("URL:");
    TextField urlField = new TextField(20);

    Button okButton = new Button("Add");
    Button cancelButton = new Button("Cancel");

    styleDialog(companyLabel, positionLabel, descriptionLabel, deadlineLabel, urlLabel);

    okButton.addActionListener(e -> {
      try {
        String company = companyField.getText().trim();
        String position = positionField.getText().trim();
        String description = descriptionField.getText().trim();
        Date deadline = dateFormat.parse(deadlineField.getText().trim());
        String url = urlField.getText().trim();

        if (company.isEmpty() || position.isEmpty() || description.isEmpty() || url.isEmpty()) {
          showErrorDialog("All fields are required");
          return;
        }

        Internship internship = new Internship(company, position, description, deadline, url);
        currentUser.addInternship(internship);
        loadUserInternships();
        dialog.dispose();

      } catch (ParseException ex) {
        showErrorDialog("Invalid date format. Please use yyyy-MM-dd");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(companyLabel);
    dialog.add(companyField);
    dialog.add(positionLabel);
    dialog.add(positionField);
    dialog.add(descriptionLabel);
    dialog.add(descriptionField);
    dialog.add(deadlineLabel);
    dialog.add(deadlineField);
    dialog.add(urlLabel);
    dialog.add(urlField);
    dialog.add(okButton);
    dialog.add(cancelButton);

    dialog.setVisible(true);
  }

  private void styleDialog(Label... labels) {
    for (Label label : labels) {
      label.setFont(REGULAR_FONT);
      label.setForeground(TEXT_COLOR);
    }
  }

  private void removeSelectedInternship() {
    int selectedIndex = internshipList.getSelectedIndex();
    if (selectedIndex != -1) {
      Dialog confirmDialog = new Dialog(this, "Confirm Removal", true);
      confirmDialog.setLayout(new BorderLayout(10, 10));
      confirmDialog.setSize(300, 150);
      confirmDialog.setLocationRelativeTo(this);
      confirmDialog.setBackground(BACKGROUND_COLOR);

      Label message = new Label("Are you sure you want to remove this internship?", Label.CENTER);
      message.setFont(REGULAR_FONT);
      message.setForeground(TEXT_COLOR);

      Panel buttonPanel = new Panel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

      Button yesButton = new Button("Yes");
      Button noButton = new Button("No");

      yesButton.addActionListener(e -> {
        Internship selected = (Internship) currentUser.getSavedInternships().toArray()[selectedIndex];
        currentUser.removeInternship(selected);
        loadUserInternships();
        confirmDialog.dispose();
      });

      noButton.addActionListener(e -> confirmDialog.dispose());

      buttonPanel.add(yesButton);
      buttonPanel.add(noButton);

      confirmDialog.add(message, BorderLayout.CENTER);
      confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

      confirmDialog.setVisible(true);
    } else {
      showErrorDialog("Please select an internship to remove");
    }
  }

  private void createGroupDialog() {
    Dialog dialog = new Dialog(this, "Create Group", true);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(300, 150);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(BACKGROUND_COLOR);

    Panel inputPanel = new Panel();
    inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    Label nameLabel = new Label("Group Name:");
    nameLabel.setFont(REGULAR_FONT);
    nameLabel.setForeground(TEXT_COLOR);

    TextField nameField = new TextField(20);

    inputPanel.add(nameLabel);
    inputPanel.add(nameField);

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    Button createButton = new Button("Create");
    Button cancelButton = new Button("Cancel");

    createButton.addActionListener(e -> {
      String groupName = nameField.getText().trim();
      if (!groupName.isEmpty()) {
        Group group = new Group(groupName);
        group.addMember(currentUser);
        loadUserGroups();
        dialog.dispose();
      } else {
        showErrorDialog("Group name cannot be empty");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(createButton);
    buttonPanel.add(cancelButton);

    dialog.add(inputPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
  }

  private void joinGroupDialog() {
    Dialog dialog = new Dialog(this, "Join Group", true);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(300, 150);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(BACKGROUND_COLOR);

    Panel inputPanel = new Panel();
    inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    Label nameLabel = new Label("Group Name:");
    nameLabel.setFont(REGULAR_FONT);
    nameLabel.setForeground(TEXT_COLOR);

    TextField nameField = new TextField(20);

    inputPanel.add(nameLabel);
    inputPanel.add(nameField);

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    Button joinButton = new Button("Join");
    Button cancelButton = new Button("Cancel");

    joinButton.addActionListener(e -> {
      String groupName = nameField.getText().trim();
      if (!groupName.isEmpty()) {
        Group group = new Group(groupName);
        group.addMember(currentUser);
        loadUserGroups();
        dialog.dispose();
      } else {
        showErrorDialog("Group name cannot be empty");
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(joinButton);
    buttonPanel.add(cancelButton);

    dialog.add(inputPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
  }

  private void leaveSelectedGroup() {
    int selectedIndex = groupList.getSelectedIndex();
    if (selectedIndex != -1) {
      Dialog confirmDialog = new Dialog(this, "Confirm Leave", true);
      confirmDialog.setLayout(new BorderLayout(10, 10));
      confirmDialog.setSize(300, 150);
      confirmDialog.setLocationRelativeTo(this);
      confirmDialog.setBackground(BACKGROUND_COLOR);

      Label message = new Label("Are you sure you want to leave this group?", Label.CENTER);
      message.setFont(REGULAR_FONT);
      message.setForeground(TEXT_COLOR);

      Panel buttonPanel = new Panel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

      Button yesButton = new Button("Yes");
      Button noButton = new Button("No");

      yesButton.addActionListener(e -> {
        Group selected = (Group) currentUser.getGroups().toArray()[selectedIndex];
        selected.removeMember(currentUser);
        loadUserGroups();
        confirmDialog.dispose();
      });

      noButton.addActionListener(e -> confirmDialog.dispose());

      buttonPanel.add(yesButton);
      buttonPanel.add(noButton);

      confirmDialog.add(message, BorderLayout.CENTER);
      confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

      confirmDialog.setVisible(true);
    } else {
      showErrorDialog("Please select a group to leave");
    }
  }

  private void shareInternshipDialog() {
    if (currentUser.getGroups().isEmpty()) {
      showErrorDialog("You don't have any groups to share with.");
      return;
    }

    if (currentUser.getSavedInternships().isEmpty()) {
      showErrorDialog("You don't have any internships to share.");
      return;
    }

    Dialog dialog = new Dialog(this, "Share Internship", true);
    dialog.setLayout(new GridLayout(3, 2, 10, 10));
    dialog.setSize(400, 200);
    dialog.setLocationRelativeTo(this);
    dialog.setBackground(BACKGROUND_COLOR);

    Label groupLabel = new Label("Select Group:");
    groupLabel.setFont(REGULAR_FONT);
    groupLabel.setForeground(TEXT_COLOR);

    Choice groupChoice = new Choice();
    for (Group group : currentUser.getGroups()) {
      groupChoice.add(group.getName());
    }

    Label internshipLabel = new Label("Select Internship:");
    internshipLabel.setFont(REGULAR_FONT);
    internshipLabel.setForeground(TEXT_COLOR);

    Choice internshipChoice = new Choice();

    // List of internships to choose from
    java.util.List<Internship> internshipsList = new ArrayList<>(currentUser.getSavedInternships());
    for (int i = 0; i < internshipsList.size(); i++) {
      Internship internship = internshipsList.get(i);
      internshipChoice.add(formatInternshipDisplay(internship));
    }

    Button shareButton = new Button("Share");
    Button cancelButton = new Button("Cancel");

    shareButton.addActionListener(e -> {
      int selectedInternshipIndex = internshipChoice.getSelectedIndex();
      String selectedGroupName = groupChoice.getSelectedItem();

      if (selectedInternshipIndex >= 0 && selectedInternshipIndex < internshipsList.size()) {
        Internship selectedInternship = internshipsList.get(selectedInternshipIndex);

        try {
          // Store in database using DatabaseUtils
          boolean success = DatabaseUtils.shareInternship(
              UUID.randomUUID().toString(), // Generate a unique ID
              selectedInternship.getCompany(),
              selectedInternship.getPosition(),
              selectedInternship.getDescription(),
              selectedInternship.getDeadline(),
              selectedInternship.getUrl(),
              selectedGroupName,
              currentUser.getName());

          if (success) {
            showInfoDialog("Internship shared successfully with group: " + selectedGroupName);
          } else {
            showErrorDialog("Failed to share internship. Check database connection.");
          }
        } catch (Exception ex) {
          showErrorDialog("Error sharing internship: " + ex.getMessage());
        }
      }

      dialog.dispose();
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(groupLabel);
    dialog.add(groupChoice);
    dialog.add(internshipLabel);
    dialog.add(internshipChoice);
    dialog.add(shareButton);
    dialog.add(cancelButton);

    dialog.setVisible(true);
  }

  private void showErrorDialog(String message) {
    Dialog errorDialog = new Dialog(this, "Error", true);
    errorDialog.setLayout(new BorderLayout(10, 10));
    errorDialog.setSize(300, 150);
    errorDialog.setLocationRelativeTo(this);
    errorDialog.setBackground(BACKGROUND_COLOR);

    Label errorLabel = new Label(message, Label.CENTER);
    errorLabel.setFont(REGULAR_FONT);
    errorLabel.setForeground(ACCENT_COLOR);

    Button okButton = new Button("OK");
    okButton.addActionListener(e -> errorDialog.dispose());

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(okButton);

    errorDialog.add(errorLabel, BorderLayout.CENTER);
    errorDialog.add(buttonPanel, BorderLayout.SOUTH);

    errorDialog.setVisible(true);
  }

  private void showInfoDialog(String message) {
    Dialog infoDialog = new Dialog(this, "Information", true);
    infoDialog.setLayout(new BorderLayout(10, 10));
    infoDialog.setSize(300, 150);
    infoDialog.setLocationRelativeTo(this);
    infoDialog.setBackground(BACKGROUND_COLOR);

    Label infoLabel = new Label(message, Label.CENTER);
    infoLabel.setFont(REGULAR_FONT);
    infoLabel.setForeground(TEXT_COLOR);

    Button okButton = new Button("OK");
    okButton.addActionListener(e -> infoDialog.dispose());

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(okButton);

    infoDialog.add(infoLabel, BorderLayout.CENTER);
    infoDialog.add(buttonPanel, BorderLayout.SOUTH);

    infoDialog.setVisible(true);
  }

  /**
   * Create the shared internships panel
   */
  private Panel createSharedInternshipsPanel() {
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout(10, 10));
    panel.setBackground(BACKGROUND_COLOR);

    // Title
    Label titleLabel = new Label("Internships Shared With Me", Label.CENTER);
    titleLabel.setFont(HEADING_FONT);
    titleLabel.setForeground(TEXT_COLOR);
    panel.add(titleLabel, BorderLayout.NORTH);

    // Shared internships list
    sharedInternshipsList = new java.awt.List(10);
    sharedInternshipsList.setFont(REGULAR_FONT);
    sharedInternshipsList.setBackground(Color.WHITE);
    panel.add(sharedInternshipsList, BorderLayout.CENTER);

    // Button panel
    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    buttonPanel.setBackground(BACKGROUND_COLOR);

    Button refreshButton = new Button("Refresh");
    styleButton(refreshButton);
    refreshButton.addActionListener(e -> loadSharedInternships());

    Button viewDetailsButton = new Button("View Details");
    styleButton(viewDetailsButton);
    viewDetailsButton.addActionListener(e -> viewSharedInternshipDetails());

    Button saveButton = new Button("Save to My Internships");
    styleButton(saveButton);
    saveButton.addActionListener(e -> saveSharedInternship());

    buttonPanel.add(refreshButton);
    buttonPanel.add(viewDetailsButton);
    buttonPanel.add(saveButton);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Load shared internships from database
   */
  private void loadSharedInternships() {
    sharedInternshipsList.removeAll();

    // Get shared internships for the current user's groups
    java.util.List<Internship> sharedInternships = com.hireme.internship.utils.DatabaseUtils
        .getSharedInternshipsForUser(currentUser);

    for (Internship internship : sharedInternships) {
      String displayText = formatInternshipDisplay(internship);
      if (internship.getMetadata() != null && !internship.getMetadata().isEmpty()) {
        displayText += " - " + internship.getMetadata();
      }
      sharedInternshipsList.add(displayText);
    }

    if (sharedInternshipsList.getItemCount() == 0) {
      sharedInternshipsList.add("No shared internships found");
    }
  }

  /**
   * View details of a shared internship
   */
  private void viewSharedInternshipDetails() {
    int selectedIndex = sharedInternshipsList.getSelectedIndex();
    if (selectedIndex != -1 && !sharedInternshipsList.getItem(selectedIndex).equals("No shared internships found")) {
      // For demo purposes, just show a simple detail dialog with the selection
      showInfoDialog("Internship Details", sharedInternshipsList.getItem(selectedIndex));
    } else {
      showErrorDialog("Please select an internship to view");
    }
  }

  /**
   * Save a shared internship to my internships
   */
  private void saveSharedInternship() {
    int selectedIndex = sharedInternshipsList.getSelectedIndex();
    if (selectedIndex != -1 && !sharedInternshipsList.getItem(selectedIndex).equals("No shared internships found")) {
      // In a real app, we would parse the selection and create a new Internship
      // object
      // For demo purposes, create a dummy internship
      try {
        String selected = sharedInternshipsList.getItem(selectedIndex);
        String[] parts = selected.split(" - ");

        if (parts.length >= 2) {
          String company = parts[0];
          String position = parts[1].split(" \\(Due:")[0];

          java.util.Date deadline = new java.util.Date(); // Default to today

          // Try to parse deadline if present
          if (selected.contains("Due:")) {
            try {
              String dateStr = selected.split("Due: ")[1].split("\\)")[0];
              deadline = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception e) {
              // Use default date if parsing fails
            }
          }

          Internship newInternship = new Internship(
              company,
              position,
              "Details from shared internship",
              deadline,
              "https://example.com");

          currentUser.addInternship(newInternship);
          loadUserInternships();
          showInfoDialog("Success", "Internship saved to your list");
        }
      } catch (Exception e) {
        showErrorDialog("Error saving internship: " + e.getMessage());
      }
    } else {
      showErrorDialog("Please select an internship to save");
    }
  }

  /**
   * Show an information dialog with title
   */
  private void showInfoDialog(String title, String message) {
    Dialog infoDialog = new Dialog(this, title, true);
    infoDialog.setLayout(new BorderLayout(10, 10));
    infoDialog.setSize(400, 200);
    infoDialog.setLocationRelativeTo(this);
    infoDialog.setBackground(BACKGROUND_COLOR);

    TextArea messageArea = new TextArea(message, 5, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
    messageArea.setEditable(false);
    messageArea.setFont(REGULAR_FONT);
    messageArea.setForeground(TEXT_COLOR);

    Button okButton = new Button("OK");
    okButton.addActionListener(e -> infoDialog.dispose());

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(okButton);

    infoDialog.add(messageArea, BorderLayout.CENTER);
    infoDialog.add(buttonPanel, BorderLayout.SOUTH);

    infoDialog.setVisible(true);
  }
}
