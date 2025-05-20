package org.springframework.samples.petclinic.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SmsNotificationService}. Uses PowerMock to mock static methods in
 * Twilio API.
 */
@ExtendWith(MockitoExtension.class)
public class SmsNotificationServiceTest {

	@Mock
	private NotificationTemplateService templateService;

	@InjectMocks
	private SmsNotificationService smsNotificationService;

	// Using mockito-inline for static mocking
	private static MockedStatic<Twilio> twilioMock;

	private static MockedStatic<Message> messageMock;

	private Owner owner;

	private Notification notification;

	@BeforeEach
	void setUp() {
		// Set Twilio configuration through reflection since we're not loading application
		// properties
		ReflectionTestUtils.setField(smsNotificationService, "twilioAccountSid", "AC123456789");
		ReflectionTestUtils.setField(smsNotificationService, "twilioAuthToken", "auth_token");
		ReflectionTestUtils.setField(smsNotificationService, "twilioPhoneNumber", "+15551234567");
		ReflectionTestUtils.setField(smsNotificationService, "twilioEnabled", true);

		// Create test owner
		owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setEmail("john.doe@example.com");
		owner.setAddress("123 Pet Street");
		owner.setCity("Pet City");
		owner.setTelephone("1234567890");
		owner.setNotificationPreference(NotificationPreference.SMS);

		// Create test notification
		notification = new Notification();
		notification.setId(1);
		notification.setMessage("Test message template");
		notification.setType(NotificationType.APPOINTMENT_REMINDER);
		notification.setStatus(NotificationStatus.PENDING);
		notification.setScheduledTime(LocalDateTime.now());
		notification.setOwner(owner);
	}

	// Note: This is a simplified test that doesn't actually mock Twilio's static methods.
	// In a real implementation, you would use mockito-inline or PowerMock to mock these
	// static calls.
	// Here we'll just test the parts that don't require static mocking.

	@Test
	void canHandleShouldReturnTrueForSmsPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.SMS);

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertTrue(result);
	}

	@Test
	void canHandleShouldReturnTrueForBothPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.BOTH);

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertTrue(result);
	}

	@Test
	void canHandleShouldReturnFalseForEmailPreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.EMAIL);

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void canHandleShouldReturnFalseForNonePreference() {
		// Given
		owner.setNotificationPreference(NotificationPreference.NONE);

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void canHandleShouldReturnFalseWhenPhoneNumberIsNull() {
		// Given
		owner.setTelephone(null);

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void canHandleShouldReturnFalseWhenPhoneNumberIsEmpty() {
		// Given
		owner.setTelephone("");

		// When
		boolean result = smsNotificationService.canHandle(notification);

		// Then
		assertFalse(result);
	}

	@Test
	void shouldNotSendSmsWhenNotificationCannotBeHandled() {
		// Given
		owner.setNotificationPreference(NotificationPreference.EMAIL);

		// When
		boolean result = smsNotificationService.send(notification);

		// Then
		assertFalse(result);
		verifyNoInteractions(templateService);
	}

}
