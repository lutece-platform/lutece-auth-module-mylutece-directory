ALTER TABLE mylutece_directory_user ADD COLUMN last_login TIMESTAMP DEFAULT '1980-01-01';

INSERT INTO mylutece_directory_parameter VALUES ('access_failures_captcha', '1');
INSERT INTO mylutece_directory_parameter VALUES ('unblock_user_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('unblock_user_mail_subject', 'Votre IP a été bloquée');
INSERT INTO mylutece_directory_parameter VALUES ('enable_unblock_ip', 'false');
INSERT INTO mylutece_directory_parameter VALUES ('notify_user_password_expired', '');
INSERT INTO mylutece_directory_parameter VALUES ('password_expired_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('password_expired_mail_subject', 'Votre mot de passe a expiré');
INSERT INTO mylutece_directory_parameter VALUES ('mail_lost_password_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('mail_lost_password_subject', 'Votre mot de passe a été réinitialisé');
INSERT INTO mylutece_directory_parameter VALUES ('mail_password_encryption_changed_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('mail_password_encryption_changed_subject', 'Votre mot de passe a été réinitialisé');