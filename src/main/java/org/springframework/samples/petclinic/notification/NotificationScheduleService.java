package org.springframework.samples.petclinic.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for processing notification schedules and generating notifications.
 *
 * @author Claude
 */
@Service
public class NotificationScheduleService {

	private final NotificationScheduleRepository notificationScheduleRepository;

	private final NotificationRepository notificationRepository;

	@Autowired
	public NotificationScheduleService(NotificationScheduleRepository notificationScheduleRepository,
			NotificationRepository notificationRepository) {
		this.notificationScheduleRepository = notificationScheduleRepository;
		this.notificationRepository = notificationRepository;
	}

	/**
	 * Find all notification schedules
	 * @return a list of all notification schedules
	 */
	@Transactional(readOnly = true)
	public List<NotificationSchedule> findAllNotificationSchedules() {
		return notificationScheduleRepository.findAll();
	}

	/**
	 * Find a notification schedule by id
	 * @param id the id to search for
	 * @return the notification schedule if found
	 */
	@Transactional(readOnly = true)
	public NotificationSchedule findNotificationScheduleById(int id) {
		return notificationScheduleRepository.findById(id);
	}

	/**
	 * Save a notification schedule
	 * @param notificationSchedule the notification schedule to save
	 */
	@Transactional
	public void saveNotificationSchedule(NotificationSchedule notificationSchedule) {
		notificationScheduleRepository.save(notificationSchedule);
	}

	/**
	 * Delete a notification schedule
	 * @param notificationSchedule the notification schedule to delete
	 */
	@Transactional
	public void deleteNotificationSchedule(NotificationSchedule notificationSchedule) {
		notificationScheduleRepository.delete(notificationSchedule);
	}

	/**
	 * Process notification schedules and generate notifications This method will be
	 * called automatically on a schedule
	 */
	@Scheduled(fixedRate = 60000) // Run every minute
	@Transactional
	public void processNotificationSchedules() {
		LocalDateTime now = LocalDateTime.now();
		List<NotificationSchedule> dueSchedules = notificationScheduleRepository.findActiveSchedulesDue(now);

		for (NotificationSchedule schedule : dueSchedules) {
			// Generate notification from the schedule
			Notification notification = schedule.generateNotification();

			// Store the notification
			notificationRepository.save(notification);

			// If this is a one-time schedule, disable it
			if (schedule.getVisit() != null) {
				schedule.setEnabled(false);
				notificationScheduleRepository.save(schedule);
			}
		}
	}

	/**
	 * Create a notification schedule for an upcoming visit
	 */
	@Transactional
	public NotificationSchedule createVisitReminderSchedule(NotificationSchedule notificationSchedule) {
		notificationScheduleRepository.save(notificationSchedule);
		return notificationSchedule;
	}

}
