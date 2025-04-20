package com.hireme.internship.service;

import com.hireme.internship.model.Internship;
import com.hireme.internship.model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class EmailService {
  // For demonstration purposes only - in a real app, these would come from
  // configuration
  private static final String EMAIL_USERNAME = "ashfaqthegamer@gmail.com";
  private static final String EMAIL_PASSWORD = "your-app-password";

  public void sendReminderEmail(User user, Internship internship, int daysLeft) {
    try {
      // Setup mail server properties
      Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");

      // Create a session with authentication
      Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
        }
      });

      // Create and format message
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(EMAIL_USERNAME));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String deadlineStr = dateFormat.format(internship.getDeadline());

      message.setSubject("Reminder: " + internship.getCompany() + " internship deadline in " + daysLeft + " days");

      String messageBody = String.format(
          "Dear %s,\n\n" +
              "This is a reminder that the application deadline for the %s position at %s is approaching!\n\n" +
              "Position: %s\n" +
              "Company: %s\n" +
              "Deadline: %s (%d days left)\n" +
              "Description: %s\n\n" +
              "Application URL: %s\n\n" +
              "Good luck with your application!\n\n" +
              "HireME Internship Reminder",
          user.getName(),
          internship.getPosition(),
          internship.getCompany(),
          internship.getPosition(),
          internship.getCompany(),
          deadlineStr,
          daysLeft,
          internship.getDescription(),
          internship.getUrl());

      message.setText(messageBody);

      // Send message
      Transport.send(message);

      System.out.println("Reminder email sent to " + user.getEmail() + " for " + internship.getCompany());

    } catch (MessagingException e) {
      System.out.println("Failed to send email: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
