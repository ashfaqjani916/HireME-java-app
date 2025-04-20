# HireME Internship Reminder

A Java AWT application that helps users manage internship opportunities by sending reminder emails before deadlines. Users can create groups with friends to share internships, and the platform ensures duplicate opportunities are filtered out to prevent multiple reminder emails for the same internship.

## Features

- Track internship opportunities with details such as company, position, description, application deadline, and URL
- Receive email reminders 3 days before application deadlines
- Create groups with friends to share interesting internship opportunities
- Automatic deduplication of internship opportunities to prevent multiple reminder emails
- Simple, intuitive GUI interface

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven for dependency management

### Configuration

Before running the application, make sure to update the email credentials in `EmailService.java`:

```java
private static final String EMAIL_USERNAME = "your-email@gmail.com";
private static final String EMAIL_PASSWORD = "your-app-password";
```

Note: If using Gmail, you'll need to generate an App Password.

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project with Maven:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -jar target/internship-reminder-1.0-SNAPSHOT.jar
   ```

## Usage

1. **Add Internships**: Click on "Add Internship" in the "My Internships" tab to add new internship opportunities
2. **Create Groups**: Go to the "Groups" tab and click "Create Group" to create a new group
3. **Share Internships**: Select a group and an internship, then click "Share Internship" to share with your group
4. **Receive Reminders**: The application will automatically send email reminders 3 days before internship deadlines

## Note

This is a simple application intended for educational purposes. In a production environment, you would want to:

- Add proper user authentication
- Store data in a database
- Add data validation and error handling
- Improve the UI/UX
- Add testing
