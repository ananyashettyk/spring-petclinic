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
 * Service responsible for dispatching notifications using the appropriate notification
 * services.
 *
 * @author Claude
 */
@Service
public class NotificationDispatchService {

	private static final Logger log = LoggerFactory.getLogger(NotificationDispatchService.class);

	private final NotificationRepository notificationRepository;

	private final List<NotificationService> notificationServices;

	@Autowired
	public NotificationDispatchService(NotificationRepository notificationRepository,
			List<NotificationService> notificationServices) {
		this.notificationRepository = notificationRepository;
		this.notificationServices = notificationServices;
	}

	/**
	 * Process pending notifications and dispatch them using the appropriate services.
	 * This method runs on a schedule.
	 */
	@Scheduled(fixedRate = 60000) // Run every minute
	@Transactional
	public void dispatchPendingNotifications() {
		LocalDateTime now = LocalDateTime.now();
		List<Notification> pendingNotifications = notificationRepository.findPendingNotifications(now);

		log.info("Found {} pending notifications to dispatch", pendingNotifications.size());

		for (Notification notification : pendingNotifications) {
			dispatchNotification(notification);
		}
	}

	/**
	 * Dispatch a single notification using appropriate services based on owner
	 * preferences.
	 * @param notification The notification to send
	 */
	@Transactional
	public void dispatchNotification(Notification notification) {
		boolean sent = false;

		// Try to send the notification using each available service that can handle it
		for (NotificationService service : notificationServices) {
			if (service.canHandle(notification)) {
				boolean success = service.send(notification);
				sent = sent || success;
			}
		}

		// If no service could handle the notification or all attempts failed
		if (!sent && notification.getStatus() == NotificationStatus.PENDING) {
			log.warn("No notification service could send notification (id={})", notification.getId());
			notification.setStatus(NotificationStatus.SKIPPED);
		}

		// Save the notification with updated status
		notificationRepository.save(notification);
	}

	/**
	 * Immediately send a notification (for testing or on-demand notifications).
	 * @param notification The notification to send
	 * @return true if the notification was sent by at least one service, false otherwise
	 */
	@Transactional
	public boolean sendImmediately(Notification notification) {
		boolean sent = false;

		for (NotificationService service : notificationServices) {
			if (service.canHandle(notification)) {
				boolean success = service.send(notification);
				sent = sent || success;
			}
		}

		// Update notification status if no service could handle it
		if (!sent && notification.getStatus() == NotificationStatus.PENDING) {
			notification.setStatus(NotificationStatus.SKIPPED);
		}

		// Save the notification with updated status
		notificationRepository.save(notification);

		return sent;
	}

}
