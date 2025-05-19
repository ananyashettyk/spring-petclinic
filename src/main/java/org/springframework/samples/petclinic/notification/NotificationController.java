package org.springframework.samples.petclinic.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for handling notification-related API endpoints. Provides
 * functionalities for retrieving notifications, updating notification preferences, and
 * testing the notification system.
 *
 * @author Claude
 */
@RestController
@RequestMapping("/api")
public class NotificationController {

	private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

	private final NotificationRepository notificationRepository;

	private final OwnerRepository ownerRepository;

	private final EmailNotificationService emailNotificationService;

	private final SmsNotificationService smsNotificationService;

	private final NotificationTemplateService templateService;

	@Autowired
	public NotificationController(NotificationRepository notificationRepository, OwnerRepository ownerRepository,
			EmailNotificationService emailNotificationService, SmsNotificationService smsNotificationService,
			NotificationTemplateService templateService) {
		this.notificationRepository = notificationRepository;
		this.ownerRepository = ownerRepository;
		this.emailNotificationService = emailNotificationService;
		this.smsNotificationService = smsNotificationService;
		this.templateService = templateService;
	}

	/**
	 * Get all notifications for a specific owner.
	 * @param ownerId the ID of the owner whose notifications to retrieve
	 * @return a list of notifications
	 */
	@GetMapping("/notifications")
	public ResponseEntity<List<Notification>> getNotifications(@RequestParam(required = false) Integer ownerId) {
		List<Notification> notifications;

		if (ownerId != null) {
			Optional<Owner> ownerOpt = ownerRepository.findById(ownerId);
			if (ownerOpt.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with ID: " + ownerId);
			}
			notifications = notificationRepository.findByOwner(ownerOpt.get());
		}
		else {
			notifications = notificationRepository.findAll();
		}

		return ResponseEntity.ok(notifications);
	}

	/**
	 * Update an owner's notification preferences.
	 * @param ownerId the ID of the owner to update
	 * @param preferenceRequest the request containing the new preference
	 * @return the updated owner details
	 */
	@PutMapping("/owners/{ownerId}/notification-preferences")
	public ResponseEntity<Map<String, Object>> updateNotificationPreference(@PathVariable Integer ownerId,
			@RequestBody NotificationPreferenceRequest preferenceRequest) {

		Optional<Owner> ownerOpt = ownerRepository.findById(ownerId);
		if (ownerOpt.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with ID: " + ownerId);
		}

		Owner owner = ownerOpt.get();

		try {
			NotificationPreference preference = NotificationPreference.valueOf(preferenceRequest.getPreference());
			owner.setNotificationPreference(preference);
			ownerRepository.save(owner);

			Map<String, Object> response = new HashMap<>();
			response.put("ownerId", owner.getId());
			response.put("firstName", owner.getFirstName());
			response.put("lastName", owner.getLastName());
			response.put("notificationPreference", owner.getNotificationPreference());

			log.info("Updated notification preference for owner {}: {}", ownerId, preference);
			return ResponseEntity.ok(response);
		}
		catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification preference: "
					+ preferenceRequest.getPreference() + ". Valid values are: EMAIL, SMS, BOTH, NONE");
		}
	}

	/**
	 * Send a test notification to an owner.
	 * @param testRequest the request containing the owner ID and optional message
	 * @return the result of the notification test
	 */
	@PostMapping("/notifications/test")
	public ResponseEntity<Map<String, Object>> testNotification(@RequestBody TestNotificationRequest testRequest) {
		Optional<Owner> ownerOpt = ownerRepository.findById(testRequest.getOwnerId());
		if (ownerOpt.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Owner not found with ID: " + testRequest.getOwnerId());
		}

		Owner owner = ownerOpt.get();

		// Create a test notification
		String message = testRequest.getMessage() != null ? testRequest.getMessage()
				: "This is a test notification from Pet Clinic.";

		Notification notification = new Notification(message, NotificationType.APPOINTMENT_REMINDER,
				LocalDateTime.now(), owner);

		// Set pet if provided
		if (testRequest.getPetId() != null && !owner.getPets().isEmpty()) {
			owner.getPets()
				.stream()
				.filter(pet -> pet.getId().equals(testRequest.getPetId()))
				.findFirst()
				.ifPresent(notification::setPet);
		}

		// Save the notification with PENDING status
		notification.setStatus(NotificationStatus.PENDING);
		notificationRepository.save(notification);

		// Process the notification message with template service
		String processedMessage = templateService.processNotification(notification);
		notification.setMessage(processedMessage);

		// Send the notification based on preference
		boolean emailSent = false;
		boolean smsSent = false;
		NotificationPreference preference = owner.getNotificationPreference();

		if (preference == NotificationPreference.EMAIL || preference == NotificationPreference.BOTH) {
			emailSent = emailNotificationService.send(notification);
		}

		if (preference == NotificationPreference.SMS || preference == NotificationPreference.BOTH) {
			smsSent = smsNotificationService.send(notification);
		}

		// Update notification in repository with final status
		notificationRepository.save(notification);

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("notificationId", notification.getId());
		response.put("ownerId", owner.getId());
		response.put("status", notification.getStatus());
		response.put("emailSent", emailSent);
		response.put("smsSent", smsSent);
		response.put("preference", preference);

		if (preference == NotificationPreference.NONE) {
			response.put("message", "No notification sent: owner preference is set to NONE");
		}

		return ResponseEntity.ok(response);
	}

}
