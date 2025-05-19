package org.springframework.samples.petclinic.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

@ExtendWith(MockitoExtension.class)
public class NotificationSchedulerServiceTests {

	@Mock
	private NotificationScheduleRepository scheduleRepository;

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private NotificationTemplateService templateService;

	@Mock
	private EmailNotificationService emailNotificationService;

	@Mock
	private SmsNotificationService smsNotificationService;

	@InjectMocks
	private NotificationSchedulerService schedulerService;

	private Owner owner;

	private Pet pet;

	private NotificationSchedule schedule;

	private Notification notification;

	@BeforeEach
	void setUp() {
		// Setup test data
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setEmail("john.doe@example.com");
		owner.setTelephone("1234567890");
		owner.setNotificationPreference(NotificationPreference.EMAIL);

		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("Dog");

		pet = new Pet();
		pet.setId(1);
		pet.setName("Max");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(dog);
		// pet.setOwner(owner);

		// Create a notification schedule
		schedule = new NotificationSchedule();
		schedule.setId(1);
		schedule.setMessageTemplate("Hello {ownerFirstName}, your pet {petName} has an upcoming appointment.");
		schedule.setType(NotificationType.APPOINTMENT_REMINDER);
		schedule.setScheduledTime(LocalDateTime.now().minusMinutes(5)); // Due 5 minutes
																		// ago
		schedule.setEnabled(true);
		schedule.setOwner(owner);
		schedule.setPet(pet);

		// Create a notification that the schedule would generate
		notification = new Notification();
		notification.setId(1);
		notification.setMessage("Hello John, your pet Max has an upcoming appointment.");
		notification.setType(NotificationType.APPOINTMENT_REMINDER);
		notification.setStatus(NotificationStatus.PENDING);
		notification.setScheduledTime(LocalDateTime.now().minusMinutes(5));
		notification.setOwner(owner);
		notification.setPet(pet);
	}

	@Test
	void shouldProcessDueSchedules() {
		// Given
		when(scheduleRepository.findActiveSchedulesDue(any(LocalDateTime.class))).thenReturn(Arrays.asList(schedule));
		when(templateService.processSchedule(schedule))
			.thenReturn("Hello John, your pet Max has an upcoming appointment.");
		when(emailNotificationService.send(any(Notification.class))).thenReturn(true);

		// When
		schedulerService.processScheduledNotifications();

		// Then
		verify(scheduleRepository).findActiveSchedulesDue(any(LocalDateTime.class));
		// The repository save is called twice - once after creating the notification and
		// again when updating its status
		verify(notificationRepository, times(2)).save(any(Notification.class));
		verify(emailNotificationService).send(any(Notification.class));
		verify(smsNotificationService, never()).send(any(Notification.class)); // Owner
																				// prefers
																				// email
																				// only
		verify(scheduleRepository).save(schedule);
	}

	@Test
	void shouldNotProcessWhenNoSchedulesDue() {
		// Given
		when(scheduleRepository.findActiveSchedulesDue(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

		// When
		schedulerService.processScheduledNotifications();

		// Then
		verify(scheduleRepository).findActiveSchedulesDue(any(LocalDateTime.class));
		verify(notificationRepository, never()).save(any(Notification.class));
		verify(emailNotificationService, never()).send(any(Notification.class));
		verify(smsNotificationService, never()).send(any(Notification.class));
	}

	@Test
	void shouldProcessPendingNotifications() {
		// Given
		when(notificationRepository.findPendingNotifications(any(LocalDateTime.class)))
			.thenReturn(Arrays.asList(notification));
		when(emailNotificationService.send(notification)).thenReturn(true);

		// When
		schedulerService.retryPendingNotifications();

		// Then
		verify(notificationRepository).findPendingNotifications(any(LocalDateTime.class));
		verify(emailNotificationService).send(notification);
		verify(smsNotificationService, never()).send(notification);
	}

	@Test
	void shouldSendBasedOnPreference() {
		// Given - owner prefers both email and SMS
		owner.setNotificationPreference(NotificationPreference.BOTH);

		when(scheduleRepository.findActiveSchedulesDue(any(LocalDateTime.class))).thenReturn(Arrays.asList(schedule));
		when(templateService.processSchedule(schedule))
			.thenReturn("Hello John, your pet Max has an upcoming appointment.");
		when(emailNotificationService.send(any(Notification.class))).thenReturn(true);
		when(smsNotificationService.send(any(Notification.class))).thenReturn(true);

		// When
		schedulerService.processScheduledNotifications();

		// Then
		verify(emailNotificationService).send(any(Notification.class));
		verify(smsNotificationService).send(any(Notification.class));
	}

	@Test
	void shouldSkipNotificationWhenPreferenceIsNone() {
		// Given - owner prefers no notifications
		owner.setNotificationPreference(NotificationPreference.NONE);

		when(scheduleRepository.findActiveSchedulesDue(any(LocalDateTime.class))).thenReturn(Arrays.asList(schedule));
		when(templateService.processSchedule(schedule))
			.thenReturn("Hello John, your pet Max has an upcoming appointment.");

		// When
		schedulerService.processScheduledNotifications();

		// Then
		verify(notificationRepository, times(2)).save(any(Notification.class)); // Initial
																				// save
																				// and
																				// status
																				// update
		verify(emailNotificationService, never()).send(any(Notification.class));
		verify(smsNotificationService, never()).send(any(Notification.class));
	}

}
