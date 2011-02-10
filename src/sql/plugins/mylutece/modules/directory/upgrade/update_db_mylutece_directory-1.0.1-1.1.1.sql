--
-- Table structure for table mylutece_directory_parameter
--
DROP TABLE IF EXISTS mylutece_directory_parameter;
CREATE TABLE mylutece_directory_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);

--
-- Init  table mylutece_directory_parameter
--
INSERT INTO mylutece_directory_parameter VALUES ('enable_password_encryption', 'false');
INSERT INTO mylutece_directory_parameter VALUES ('encryption_algorithm', '');
