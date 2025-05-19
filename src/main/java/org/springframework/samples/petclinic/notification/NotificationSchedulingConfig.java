package org.springframework.samples.petclinic.notification;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduling for the notification system. This enables
 * Spring's @Scheduled annotation functionality.
 *
 * @author Claude
 */
@Configuration
@EnableScheduling
public class NotificationSchedulingConfig {

	// This configuration class enables @Scheduled annotations
	// No additional beans or methods needed

}
