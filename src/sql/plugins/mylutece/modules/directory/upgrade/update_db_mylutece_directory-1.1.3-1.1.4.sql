--
-- MYLUTECEDIRECTORY-19 : When the option to encrypt the password is on, the creation of a new account does not still not encrypt the password. The same goes for the "Lost password" functionnality
--
DROP TABLE IF EXISTS mylutece_directory_key;
CREATE TABLE mylutece_directory_key(
	mylutece_directory_user_key VARCHAR(255) DEFAULT '' NOT NULL,
	id_record int NOT NULL,
	PRIMARY KEY (mylutece_directory_user_key)
);
