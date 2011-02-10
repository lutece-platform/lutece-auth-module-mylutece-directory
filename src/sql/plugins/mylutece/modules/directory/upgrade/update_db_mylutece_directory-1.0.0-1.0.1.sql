
 --
-- Alter table structure for table mylutece_directory_user
--

ALTER TABLE mylutece_directory_user ADD COLUMN activated SMALLINT DEFAULT '0' AFTER user_password;
