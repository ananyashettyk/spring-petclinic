package org.springframework.samples.petclinic.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to schedule and process notification sending at regular intervals. This service
 * handles checking for pending notification schedules and sending notifications according
 * to owner preferences.
 *
 * @author Claude
 */
@Service
public class NotificationSchedulerService {

	private static final Logger log = LoggerFactory.getLogger(NotificationSchedulerService.class);

	private final NotificationScheduleRepository scheduleRepository;

	private final NotificationRepository notificationRepository;

	private final NotificationTemplateService templateService;

	private final EmailNotificationService emailNotificationService;

	private final SmsNotificationService smsNotificationService;

	@Autowired
	public NotificationSchedulerService(NotificationScheduleRepository scheduleRepository,
			NotificationRepository notificationRepository, NotificationTemplateService templateService,
			EmailNotificationService emailNotificationService, SmsNotificationService smsNotificationService) {
		this.scheduleRepository = scheduleRepository;
		this.notificationRepository = notificationRepository;
		this.templateService = templateService;
		this.emailNotificationService = emailNotificationService;
		this.smsNotificationService = smsNotificationService;
	}

	/**
	 * Scheduled task that runs every minute to check for and process due notifications.
	 * This method will create and send notifications based on active schedules.
	 */
	@Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
	@Transactional
	public void processScheduledNotifications() {
		log.debug("Running scheduled notification check at {}", LocalDateTime.now());

		try {
			// Step 1: Find all active notification schedules that are due
			List<NotificationSchedule> dueSchedules = scheduleRepository.findActiveSchedulesDue(LocalDateTime.now());

			if (dueSchedules.isEmpty()) {
				log.debug("No notification schedules due for processing");
				return;
			}

			log.info("Found {} notification schedules due for processing", dueSchedules.size());

			// Step 2: Process each due schedule
			for (NotificationSchedule schedule : dueSchedules) {
				processSchedule(schedule);
			}

		}
		catch (Exception e) {
			log.error("Error occurred while processing scheduled notifications", e);
		}
	}

	/**
	 * Process a single notification schedule by generating and sending the notification.
	 * @param schedule the notification schedule to process
	 */
	private void processSchedule(NotificationSchedule schedule) {
		try {
			// Step 1: Generate the notification from the schedule
			Notification notification = schedule.generateNotification();

			// Step 2: Process the template to replace placeholders
			String processedMessage = templateService.processSchedule(schedule);
			notification.setMessage(processedMessage);

			// Step 3: Save the notification with PENDING status
			notificationRepository.save(notification);
			log.debug("Created notification {} from schedule {}", notification.getId(), schedule.getId());

			// Step 4: Send the notification based on owner's preference
			boolean sent = sendNotification(notification);

			// Step 5: Update the schedule status (disable it after processing)
			schedule.setEnabled(false);
			scheduleRepository.save(schedule);

			log.info("Processed notification schedule {}: notification {} - status: {}", schedule.getId(),
					notification.getId(), notification.getStatus());

		}
		catch (Exception e) {
			log.error("Failed to process notification schedule {}: {}", schedule.getId(), e.getMessage(), e);
		}
	}

	/**
	 * Send a notification using the appropriate notification service based on the owner's
	 * preference.
	 * @param notification the notification to send
	 * @return true if the notification was sent successfully by at least one service
	 */
	private boolean sendNotification(Notification notification) {
		NotificationPreference preference = notification.getOwner().getNotificationPreference();
		boolean sent = false;

		// Skip sending if the owner has opted out of notifications
		if (preference == NotificationPreference.NONE) {
			notification.setStatus(NotificationStatus.SKIPPED);
			notificationRepository.save(notification);
			log.info("Notification {} skipped: owner preference is set to NONE", notification.getId());
			return false;
		}

		// Try sending via email if the owner's preference includes email
		if (preference == NotificationPreference.EMAIL || preference == NotificationPreference.BOTH) {
			boolean emailSent = emailNotificationService.send(notification);
			sent = sent || emailSent;

			if (emailSent) {
				log.debug("Email notification sent successfully: {}", notification.getId());
			}
			else {
				log.warn("Failed to send email notification: {}", notification.getId());
			}
		}

		// Try sending via SMS if the owner's preference includes SMS
		if (preference == NotificationPreference.SMS || preference == NotificationPreference.BOTH) {
			boolean smsSent = smsNotificationService.send(notification);
			sent = sent || smsSent;

			if (smsSent) {
				log.debug("SMS notification sent successfully: {}", notification.getId());
			}
			else {
				log.warn("Failed to send SMS notification: {}", notification.getId());
			}
		}

		// Update notification status if it wasn't already updated by the notification
		// services
		if (notification.getStatus() == NotificationStatus.PENDING) {
			notification.setStatus(sent ? NotificationStatus.SENT : NotificationStatus.FAILED);
			notificationRepository.save(notification);
		}

		return sent;
	}

	/**
	 * Check and process any pending notifications that haven't been sent yet. This is
	 * useful for sending notifications that failed during previous runs.
	 */
	@Scheduled(cron = "0 0 * * * *") // Run every hour at minute 0
	@Transactional
	public void retryPendingNotifications() {
		log.debug("Checking for pending notifications to retry");

		List<Notification> pendingNotifications = notificationRepository.findPendingNotifications(LocalDateTime.now());

		if (!pendingNotifications.isEmpty()) {
			log.info("Found {} pending notifications to retry", pendingNotifications.size());

			for (Notification notification : pendingNotifications) {
				sendNotification(notification);
			}
		}
	}

}
