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
