--
-- Init  table mylutece_directory_parameter
--
INSERT INTO mylutece_directory_parameter VALUES ('enable_password_encryption', 'false');
INSERT INTO mylutece_directory_parameter VALUES ('encryption_algorithm', '');
INSERT INTO mylutece_directory_parameter VALUES ('force_change_password_reinit', '');
INSERT INTO mylutece_directory_parameter VALUES ('password_minimum_length', '8');
INSERT INTO mylutece_directory_parameter VALUES ('password_format', 'false');
INSERT INTO mylutece_directory_parameter VALUES ('password_duration', '');
INSERT INTO mylutece_directory_parameter VALUES ('password_history_size', '');
INSERT INTO mylutece_directory_parameter VALUES ('maximum_number_password_change', '');
INSERT INTO mylutece_directory_parameter VALUES ('tsw_size_password_change', '');
INSERT INTO mylutece_directory_parameter VALUES ('use_advanced_security_parameters', 'false');
INSERT INTO mylutece_directory_parameter VALUES ('account_life_time', '360');
INSERT INTO mylutece_directory_parameter VALUES ('time_before_alert_account', '30');
INSERT INTO mylutece_directory_parameter VALUES ('nb_alert_account', '2');
INSERT INTO mylutece_directory_parameter VALUES ('time_between_alerts_account', '10');
INSERT INTO mylutece_directory_parameter VALUES ('access_failures_max', '3');
INSERT INTO mylutece_directory_parameter VALUES ('access_failures_interval', '10');
INSERT INTO mylutece_directory_parameter VALUES ('expired_alert_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('expired_alert_mail_subject', 'Votre compte a expiré');
INSERT INTO mylutece_directory_parameter VALUES ('first_alert_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('first_alert_mail_subject', 'Votre compte va bientot expirer');
INSERT INTO mylutece_directory_parameter VALUES ('other_alert_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('other_alert_mail_subject', 'Votre compte va bientot expirer');
INSERT INTO mylutece_directory_parameter VALUES ('account_reactivated_mail_sender', 'lutece@nowhere.com');
INSERT INTO mylutece_directory_parameter VALUES ('account_reactivated_mail_subject', 'Votre compte a bien été réactivé');
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
