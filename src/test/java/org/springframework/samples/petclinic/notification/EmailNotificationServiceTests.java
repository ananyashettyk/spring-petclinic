package org.springframework.samples.petclinic.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmailNotificationService}.
 */
@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTests {

	@Mock
	private JavaMailSender emailSender;

	@Mock
	private NotificationTemplateService templateService;

	@InjectMocks
	private EmailNotificationService emailNotificationService;

	private Owner owner;

	private Notification notification;

	@BeforeEach
	void setUp() {
		// Set sender email through reflection since we're not loading application
		// properties
		ReflectionTestUtils.setField(emailNotificationService, "senderEmail", "test@petclinic.com");

		// Create test owner
		owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setEmail("john.doe@example.com");
		owner.setAddress("123 Pet Street");
		owner.setCity("Pet City");
		owner.setTelephone("1234567890");
		owner.setNotificationPreference(NotificationPreference.EMAIL);

		// Create test notification
		notification = new Notification();
		notification.setId(1);
		notification.setMessage("Test message template");
		notification.setType(NotificationType.APPOINTMENT_REMINDER);
		notification.setStatus(NotificationStatus.PENDING);
		notification.setScheduledTime(LocalDateTime.now());
		notification.setOwner(owner);
	}

	@Test
	void shouldSendEmailNotification() {
		// Given
		String processedMessage = "Dear John, this is your processed notification message";
		when(templateService.processNotification(notification)).thenReturn(processedMessage);

		// When
		boolean result = emailNotificationService.send(notification);

		// Then
		assertTrue(result);
		assertEquals(NotificationStatus.SENT, notification.getStatus());
		assertNotNull(notification.getSentTime());

		// Verify email was sent with correct content
		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(emailSender).send(messageCaptor.capture());

		SimpleMailMessage capturedMessage = messageCaptor.getValue();
		assertEquals("test@petclinic.com", capturedMessage.getFrom());
		assertEquals("john.doe@example.com", capturedMessage.getTo()[0]);
		assertEquals("Pet Clinic: Appointment Reminder", capturedMessage.getSubject());
		assertEquals(processedMessage, capturedMessage.getText());
	}

	@Test
	void shouldSkipNotificationWhenEmailMissing() {
		// Given
		owner.setEmail(null);

		// When
		boolean result = emailNotificationService.send(notification);

		// Then
		assertFalse(result);
		// Service should not attempt to send
		verifyNoInteractions(emailSender);
		verifyNoInteractions(templateService);
	}

	@Test
	void shouldSkipNotificationWhenPreferenceNotEmail() {
		// Given
		owner.setNotificationPreference(NotificationPreference.SMS);

		// When
		boolean result = emailNotificationService.send(notification);

		// Then
		assertFalse(result);
		verifyNoInteractions(emailSender);
		verifyNoInteractions(templateService);
	}

	@Test
	void canHandleShouldReturnTrueForEmailPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.EMAIL);

		// When
		boolean result = emailNotificationService.canHandle(notification);

		// Then
		assertTrue(result);
	}

	@Test
	void canHandleShouldReturnTrueForBothPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.BOTH);

		// When
		boolean result = emailNotificationService.canHandle(notification);

		// Then
		assertTrue(result);
	}

	@Test
	void canHandleShouldReturnFalseForSmsPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.SMS);

		// When
		boolean result = emailNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void canHandleShouldReturnFalseForNonePreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.NONE);

		// When
		boolean result = emailNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void shouldGenerateCorrectSubjectForDifferentNotificationTypes() {
		// Test different notification types and their corresponding subjects
		// Given
		when(templateService.processNotification(any(Notification.class))).thenReturn("Processed message");

		// For Appointment Reminder
		notification.setType(NotificationType.APPOINTMENT_REMINDER);
		emailNotificationService.send(notification);

		ArgumentCaptor<SimpleMailMessage> appointmentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(emailSender, times(1)).send(appointmentCaptor.capture());
		assertEquals("Pet Clinic: Appointment Reminder", appointmentCaptor.getValue().getSubject());

		// For Medication Reminder
		notification.setType(NotificationType.MEDICATION_REMINDER);
		reset(emailSender);
		emailNotificationService.send(notification);

		ArgumentCaptor<SimpleMailMessage> medicationCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(emailSender, times(1)).send(medicationCaptor.capture());
		assertEquals("Pet Clinic: Medication Reminder", medicationCaptor.getValue().getSubject());

		// For Vaccination Reminder
		notification.setType(NotificationType.VACCINATION_REMINDER);
		reset(emailSender);
		emailNotificationService.send(notification);

		ArgumentCaptor<SimpleMailMessage> vaccinationCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(emailSender, times(1)).send(vaccinationCaptor.capture());
		assertEquals("Pet Clinic: Vaccination Reminder", vaccinationCaptor.getValue().getSubject());
	}

}
