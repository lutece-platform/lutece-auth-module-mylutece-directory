--
-- Add security parameters to mylutece_directory_parameter
--
INSERT INTO core_template VALUES ('mylutece_directory_mailLostPassword', '<html><head><title>#i18n{mylutece.email_reinit.object}</title></head><body><p>#i18n{mylutece.email_reinit.content.text}<br />----------------------------------------------------------</p><p>#i18n{mylutece.email_reinit.content.newPassword} : ${new_password}<br />----------------------------------------------------------</p></body></html>');
