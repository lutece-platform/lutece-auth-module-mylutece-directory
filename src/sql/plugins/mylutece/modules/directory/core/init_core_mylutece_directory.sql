--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url,id_order) VALUES 
('MYLUTECE_DIRECTORY_MANAGEMENT', 'module.mylutece.directory.adminFeature.directory_management.name', 1, 'jsp/admin/plugins/mylutece/modules/directory/ManageDirectory.jsp', 'module.mylutece.directory.adminFeature.directory_management.description', 0, 'mylutece-directory', 'APPLICATIONS', NULL, NULL,NULL);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url,id_order) VALUES 
('MYLUTECE_DIRECTORY_MANAGEMENT_USERS', 'module.mylutece.directory.adminFeature.directory_management_user.name', 3, 'jsp/admin/plugins/mylutece/modules/directory/ManageRecords.jsp', 'module.mylutece.directory.adminFeature.directory_management_user.description', 0, 'mylutece-directory', 'USERS', NULL, NULL, NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_DIRECTORY_MANAGEMENT', 1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_DIRECTORY_MANAGEMENT_USERS', 1);

--
-- Init  table core_admin_dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('myluteceDirectoryAdminDashboardComponent', 1, 1);

--
-- Init  table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('mylutece_directory_manager', 'Mylutece Directory management');

--
-- Init  table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (360,'mylutece_directory_manager','MYLUTECE_DIRECTORY','*','*');

--
-- Init  table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('mylutece_directory_manager',1);

INSERT INTO core_template VALUES ('mylutece_directory_first_alert_mail', 'Bonjour ${login} ! Votre compte utilisateur arrive à expiration. Pour prolonger sa validité, veuillez <a href="${url}">cliquer ici</a>.</br>Si vous ne le faites pas avant le ${date_valid}, il sera désactivé.');
INSERT INTO core_template VALUES ('mylutece_directory_expiration_mail', 'Bonjour ${login} ! Votre compte a expiré. Vous ne pourrez plus vous connecter avec, et les données vous concernant ont été anonymisées');
INSERT INTO core_template VALUES ('mylutece_directory_other_alert_mail', 'Bonjour ${login} ! Votre compte utilisateur arrive à expiration. Pour prolonger sa validité, veuillez <a href="${url}">cliquer ici</a>.</br>Si vous ne le faites pas avant le ${date_valid}, il sera désactivé.');
INSERT INTO core_template VALUES ('mylutece_directory_account_reactivated_mail', 'Bonjour ${login} ! Votre compte utilisateur a bien été réactivé. Il est désormais valable jusqu''au ${date_valid}.');
INSERT INTO core_template VALUES ('mylutece_directory_unblock_user', '${site_link!}<br />Bonjour ! Votre IP a été bloquée. Pour la débloquer, vous pouvez suivre le lien suivant : <a href="${url}">debloquer</a>.');
INSERT INTO core_template VALUES ('mylutece_directory_password_expired', 'Bonjour ! Votre mot de passe a éxpiré. Lors de votre prochaine connection, vous pourrez le changer.');

INSERT INTO core_datastore VALUES ('directory_banned_domain_names', 'yopmail.com');