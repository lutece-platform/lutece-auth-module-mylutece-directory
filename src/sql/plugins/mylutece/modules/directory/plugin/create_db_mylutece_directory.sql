DROP TABLE IF EXISTS mylutece_directory_directory;
DROP TABLE IF EXISTS mylutece_directory_user;
DROP TABLE IF EXISTS mylutece_directory_user_role;
DROP TABLE IF EXISTS mylutece_directory_user_group;
DROP TABLE IF EXISTS mylutece_directory_mapping;
DROP TABLE IF EXISTS mylutece_directory_parameter;
DROP TABLE IF EXISTS mylutece_directory_key;
DROP TABLE IF EXISTS mylutece_directory_user_password_history;

--
-- Table struture for mylutece_directory_directory
--
CREATE TABLE mylutece_directory_directory (
	id_directory int NOT NULL,
	PRIMARY KEY (id_directory)
);

--
-- Table struture for mylutece_directory_user
--
CREATE TABLE mylutece_directory_user (
	id_record int NOT NULL,
	user_login varchar(100) default '' NOT NULL,
	user_password varchar(100) default '' NOT NULL,
	activated SMALLINT DEFAULT '0',
	reset_password SMALLINT DEFAULT 0 NOT NULL,
	password_max_valid_date TIMESTAMP NULL,
	account_max_valid_date BIGINT NULL,
	nb_alerts_sent INTEGER DEFAULT 0 NOT NULL,
	last_login TIMESTAMP DEFAULT '1980-01-01',
	PRIMARY KEY (id_record)
);

--
-- Table struture for mylutece_directory_user_role
--
CREATE TABLE mylutece_directory_user_role (
	id_record int NOT NULL,
	role_key varchar(50) default '' NOT NULL,
	PRIMARY KEY (id_record,role_key)
);

--
-- Table struture for mylutece_directory_user_group
--
CREATE TABLE mylutece_directory_user_group (
	id_record int NOT NULL,
	group_key varchar(100) default '' NOT NULL,
	PRIMARY KEY (id_record,group_key)
);

--
-- Table struture for mylutece_directory_mapping
--
CREATE TABLE mylutece_directory_mapping (
	id_entry int NOT NULL,
	attribute_key varchar(100) default '' NOT NULL,
	PRIMARY KEY (id_entry)
);

--
-- Table structure for table mylutece_directory_parameter
--
CREATE TABLE mylutece_directory_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);

--
-- Table structure for table mylutece_directory_key
--
CREATE TABLE mylutece_directory_key(
	mylutece_directory_user_key VARCHAR(255) DEFAULT '' NOT NULL,
	id_record int NOT NULL,
	PRIMARY KEY (mylutece_directory_user_key)
);

CREATE  TABLE mylutece_directory_user_password_history (
  id_record INT NOT NULL ,
  password VARCHAR(100) NOT NULL ,
  date_password_change TIMESTAMP NOT NULL DEFAULT NOW() ,
  PRIMARY KEY (id_record, date_password_change)
  );

