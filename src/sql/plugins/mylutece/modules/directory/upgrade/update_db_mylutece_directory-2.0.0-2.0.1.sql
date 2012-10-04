ALTER TABLE mylutece_directory_user ADD COLUMN last_login TIMESTAMP DEFAULT '1980-01-01';

INSERT INTO mylutece_directory_parameter VALUES ('access_failures_captcha', '1');
INSERT INTO mylutece_directory_parameter VALUES ('unblock_user_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('unblock_user_mail_subject', 'Votre IP a été bloquée');
