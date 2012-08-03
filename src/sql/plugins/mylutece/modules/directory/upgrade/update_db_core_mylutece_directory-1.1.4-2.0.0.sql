--
-- Add security parameters to mylutece_directory_parameter
--
INSERT INTO core_template VALUES ('mylutece_directory_first_alert_mail', 'Bonjour ${login} ! Votre compte utilisateur arrive à expiration. Pour prolonger sa validité, veuillez <a href="${url}">cliquer ici</a>.</br>Si vous ne le faites pas avant le ${date_valid}, il sera désactivé.');
INSERT INTO core_template VALUES ('mylutece_directory_expiration_mail', 'Bonjour ${login} ! Votre compte a expiré. Vous ne pourrez plus vous connecter avec, et les données vous concernant ont été anonymisées');
INSERT INTO core_template VALUES ('mylutece_directory_other_alert_mail', 'Bonjour ${login} ! Votre compte utilisateur arrive à expiration. Pour prolonger sa validité, veuillez <a href="${url}">cliquer ici</a>.</br>Si vous ne le faites pas avant le ${date_valid}, il sera désactivé.');
INSERT INTO core_template VALUES ('mylutece_directory_account_reactivated_mail', 'Bonjour ${login} ! Votre compte utilisateur a bien été réactivé. Il est désormais valable jusqu''au ${date_valid}.');