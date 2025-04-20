package com.hireme.internship.service;

import com.hireme.internship.model.Internship;
import com.hireme.internship.model.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderService {
  private static final int REMINDER_DAYS = 3; // Send reminder 3 days before deadline

  private final EmailService emailService;
  private final ScheduledExecutorService scheduler;
  private final Set<User> users;

  public ReminderService() {
    this.emailService = new EmailService();
    this.scheduler = Executors.newScheduledThreadPool(1);
    this.users = new HashSet<>();
  }

  public void start() {
    // Check for deadlines daily at midnight
    scheduler.scheduleAtFixedRate(this::checkDeadlines, 0, 1, TimeUnit.DAYS);
  }

  public void stop() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public void registerUser(User user) {
    users.add(user);
  }

  public void unregisterUser(User user) {
    users.remove(user);
  }

  private void checkDeadlines() {
    Date now = new Date();
    long currentTimeMillis = now.getTime();

    // Target time: REMINDER_DAYS days from now
    long targetTimeMillis = currentTimeMillis + (REMINDER_DAYS * 24 * 60 * 60 * 1000);
    Date targetDate = new Date(targetTimeMillis);

    Set<Internship> processedInternships = new HashSet<>();

    for (User user : users) {
      // First check user's saved internships
      for (Internship internship : user.getSavedInternships()) {
        if (!processedInternships.contains(internship) && shouldSendReminder(internship, now, targetDate)) {
          emailService.sendReminderEmail(user, internship, REMINDER_DAYS);
          processedInternships.add(internship);
        }
      }

      // Then check internships shared in user's groups
      for (com.hireme.internship.model.Group group : user.getGroups()) {
        for (Internship internship : group.getSharedInternships()) {
          if (!processedInternships.contains(internship) && shouldSendReminder(internship, now, targetDate)) {
            emailService.sendReminderEmail(user, internship, REMINDER_DAYS);
            processedInternships.add(internship);
          }
        }
      }
    }
  }

  private boolean shouldSendReminder(Internship internship, Date now, Date targetDate) {
    Date deadline = internship.getDeadline();

    // If deadline is between now and target date (e.g., 3 days from now)
    return deadline.after(now) && deadline.before(targetDate);
  }
}
