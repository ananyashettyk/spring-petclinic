INSERT INTO vets VALUES (1, 'James', 'Carter');
INSERT INTO vets VALUES (2, 'Helen', 'Leary');
INSERT INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (1, 'radiology');
INSERT INTO specialties VALUES (2, 'surgery');
INSERT INTO specialties VALUES (3, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (1, 'cat');
INSERT INTO types VALUES (2, 'dog');
INSERT INTO types VALUES (3, 'lizard');
INSERT INTO types VALUES (4, 'snake');
INSERT INTO types VALUES (5, 'bird');
INSERT INTO types VALUES (6, 'hamster');

INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023', 'george.franklin@example.com', 'EMAIL');
INSERT INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749', 'betty.davis@example.com', 'SMS');
INSERT INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763', 'eduardo.rodriquez@example.com', 'BOTH');
INSERT INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198', 'harold.davis@example.com', 'NONE');
INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765', 'peter.mctavish@example.com', 'EMAIL');
INSERT INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654', 'jean.coleman@example.com', 'SMS');
INSERT INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387', 'jeff.black@example.com', 'BOTH');
INSERT INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683', 'maria.escobito@example.com', 'EMAIL');
INSERT INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435', 'david.schroeder@example.com', 'NONE');
INSERT INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487', 'carlos.estaban@example.com', 'SMS');


INSERT INTO pets VALUES (1, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (2, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (3, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (4, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (5, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (6, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (7, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (8, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (9, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (10, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (11, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (12, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (13, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (1, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits VALUES (2, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits VALUES (3, 8, '2013-01-03', 'neutered');
INSERT INTO visits VALUES (4, 7, '2013-01-04', 'spayed');

-- Sample data for notifications table
INSERT INTO notifications VALUES (1, 'Reminder: Your appointment with Dr. Carter for Leo is tomorrow at 10:00 AM', 'EMAIL', 'PENDING', '2025-05-20 10:00:00', NULL, 1, 1);
INSERT INTO notifications VALUES (2, 'Reminder: Basil is due for annual checkup next week', 'SMS', 'SENT', '2025-05-15 09:00:00', '2025-05-15 09:05:23', 2, 2);
INSERT INTO notifications VALUES (3, 'Time for Rosy''s heartworm medication', 'EMAIL', 'PENDING', '2025-05-25 08:00:00', NULL, 3, 3);
INSERT INTO notifications VALUES (4, 'Reminder: Jewel has an appointment on Monday for vaccinations', 'SMS', 'FAILED', '2025-05-18 11:00:00', '2025-05-18 11:02:45', 3, 4);
INSERT INTO notifications VALUES (5, 'Reminder: Iggy''s dental cleaning is scheduled for next Friday', 'EMAIL', 'SENT', '2025-05-10 14:00:00', '2025-05-10 14:01:12', 4, 5);

-- Sample data for notification_schedules table
INSERT INTO notification_schedules VALUES (1, 'Reminder: Your pet {petName} has an appointment tomorrow', 'EMAIL', '2025-06-15 09:00:00', 1, TRUE, 1, 1, 1);
INSERT INTO notification_schedules VALUES (2, 'Time for {petName}''s annual checkup', 'SMS', '2025-06-20 10:00:00', NULL, TRUE, 2, 2, NULL);
INSERT INTO notification_schedules VALUES (3, '{petName} is due for heartworm medication', 'BOTH', '2025-06-10 08:00:00', NULL, TRUE, 3, 3, NULL);
INSERT INTO notification_schedules VALUES (4, 'Reminder: {petName}''s vaccines are due', 'EMAIL', '2025-07-05 11:00:00', 7, TRUE, 4, 5, 2);
INSERT INTO notification_schedules VALUES (5, 'Reminder: {petName} has a follow-up appointment scheduled', 'SMS', '2025-07-15 14:00:00', 2, FALSE, 6, 7, 4);
