/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.key.MyluteceDirectoryUserKey;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.AttributeMappingService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IAttributeMappingService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IMyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.key.IMyluteceDirectoryUserKeyService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.key.MyluteceDirectoryUserKeyService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.IMyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.MyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.IMyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.MyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.util.SecurityUtils;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.password.PasswordUtil;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides the XPageApp that manage personalization features for Mylutece Directory module : login, account management, ...
 */
public class MyLuteceDirectoryApp implements XPageApplication
{
	// Markers
	private static final String MARK_USER = "user";
	private static final String MARK_ROLES = "roles";
	private static final String MARK_GROUPS = "groups";
	private static final String MARK_PLUGIN_NAME = "plugin_name";
	private static final String MARK_ERROR_CODE = "error_code";
	private static final String MARK_ERROR_MESSAGE = "error_message";
	private static final String MARK_ERROR_FIELD = "error_field";
	private static final String MARK_ACTION_SUCCESSFUL = "action_successful";
	private static final String MARK_EMAIL = "email";
	private static final String MARK_ATTRIBUTES_LIST = "attributes_mapping";
	private static final String MARK_ENTRY_LIST = "entry_list";
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
	private static final String MARK_CAPTCHA = "captcha";
	private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
	private static final String MARK_FORM_ERROR = "form_error";
	private static final String MARK_REINIT_URL = "reinit_url";
	private static final String MARK_KEY = "key";
	private static final String MARK_PASSWORD_MINIMUM_LENGTH = "password_minimum_length";
	private static final String MARK_USER_ID = "user_id";
	private static final String MARK_REF = "ref";
	private static final String MARK_SITE_LINK = "site_link";
	private static final String MARK_LOGIN = "login";
	private static final String MARK_LOGIN_URL = "login_url";

	// Parameters
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_OLD_PASSWORD = "old_password";
	private static final String PARAMETER_NEW_PASSWORD = "new_password";
	private static final String PARAMETER_CONFIRMATION_PASSWORD = "confirmation_password";
	private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
	private static final String PARAMETER_ERROR_CODE = "error_code";
	private static final String PARAMETER_ERROR_MESSAGE = "error_message";
	private static final String PARAMETER_ERROR_FIELD = "error_field";
	private static final String PARAMETER_EMAIL = "email";
	private static final String PARAMETER_ACTION_SUCCESSFUL = "action_successful";
	private static final String PARAMETER_LOGIN = "login";
	private static final String PARAMETER_PASSWORD = "password";
	private static final String PARAMETER_DIRECTORY_RECORD = "directory_record";
	private static final String PARAMETER_KEY = "key";
	private static final String PARAMETER_FORCE_CHANGE_PASSWORD_REINIT = "force_change_password_reinit";
	private static final String PARAMETER_TIME_BEFORE_ALERT_ACCOUNT = "time_before_alert_account";
    private static final String PARAMETER_MAIL_LOST_PASSWORD_SENDER = "mail_lost_password_sender";
    private static final String PARAMETER_MAIL_LOST_PASSWORD_SUBJECT = "mail_lost_password_subject";

	// Actions
	private static final String ACTION_MODIFY_ACCOUNT = "modifyAccount";
	private static final String ACTION_VIEW_ACCOUNT = "viewAccount";
	private static final String ACTION_LOST_PASSWORD = "lostPassword";
	private static final String ACTION_LOST_LOGIN = "lostLogin";
	private static final String ACTION_ACCESS_DENIED = "accessDenied";
	private static final String ACTION_CREATE_ACCOUNT = "createAccount";
	private static final String ACTION_DO_CREATE_ACCOUNT = "doCreateAccount";
	private static final String ACTION_REINIT_PASSWORD = "reinitPassword";
	private static final String ACTION_UPDATE_ACCOUNT = "updateAccount";
	private static final String ACTION_GET_RESET_PASSWORD = "getResetPasswordPage";

	// Errors
	private static final String ERROR_OLD_PASSWORD = "error_old_password";
	private static final String ERROR_CONFIRMATION_PASSWORD = "error_confirmation_password";
	private static final String ERROR_SAME_PASSWORD = "error_same_password";
	private static final String ERROR_SYNTAX_EMAIL = "error_syntax_email";
	private static final String ERROR_SENDING_EMAIL = "error_sending_email";
	private static final String ERROR_UNKNOWN_EMAIL = "error_unknown_email";
	private static final String ERROR_MANDATORY_FIELDS = "error_mandatory_fields";
	private static final String ERROR_LOGIN_ALREADY_EXISTS = "error_login_already_exists";
	private static final String ERROR_LOGIN_ACCENTS_OR_BLANK = "error_login_accents_or_blank";
	private static final String ERROR_JCAPTCHA = "error_jcaptcha";
	private static final String ERROR_DIRECTORY_FIELD = "error_directory_field";
	private static final String ERROR_DIRECTORY_MESSAGE = "error_directory_message";
	private static final String ERROR_DIRECTORY_UNDEFINED = "error_directory_undefined";
	private static final String ERROR_UNKNOWN = "error_unknown";
	private static final String ERROR_PASSWORD_MINIMUM_LENGTH = "password_minimum_length";
	private static final String ERROR_PASSWORD_FORMAT = "password_format";
	private static final String ERROR_PASSWORD_ALREADY_USED = "password_already_used";
	private static final String ERROR_MAX_PASSWORD_CHANGE = "max_password_change";

	// Templates
	private static final String TEMPLATE_LOST_PASSWORD_PAGE = "skin/plugins/mylutece/modules/directory/lost_password.html";
	private static final String TEMPLATE_LOST_LOGIN_PAGE = "skin/plugins/mylutece/modules/directory/lost_login.html";
	private static final String TEMPLATE_VIEW_ACCOUNT_PAGE = "skin/plugins/mylutece/modules/directory/view_account.html";
	private static final String TEMPLATE_CHANGE_PASSWORD_PAGE = "skin/plugins/mylutece/modules/directory/change_password.html";
	private static final String TEMPLATE_CREATE_ACCOUNT_PAGE = "skin/plugins/mylutece/modules/directory/create_account.html";
	private static final String TEMPLATE_REINIT_PASSWORD_PAGE = "skin/plugins/mylutece/modules/directory/reinit_password.html";
	private static final String TEMPLATE_EMAIL_LOST_LOGIN = "skin/plugins/mylutece/email_lost_login.html";

	// Properties
	private static final String PROPERTY_MYLUTECE_MODIFY_ACCOUNT_URL = "mylutece-directory.url.modifyAccount.page";
	private static final String PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL = "mylutece-directory.url.viewAccount.page";
	private static final String PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL = "mylutece-directory.url.createAccount.page";
	private static final String PROPERTY_MYLUTECE_LOST_PASSWORD_URL = "mylutece-directory.url.lostPassword.page";
	private static final String PROPERTY_MYLUTECE_LOST_LOGIN_URL = "mylutece-directory.url.lostLogin.page";
	private static final String PROPERTY_MYLUTECE_RESET_PASSWORD_URL = "mylutece-directory.url.resetPassword.page";
	private static final String PROPERTY_MYLUTECE_ACCESS_DENIED_URL = "mylutece-directory.url.accessDenied.page";
	private static final String PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL = "mylutece-directory.url.default.redirect";
	private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED = "mylutece-directory.template.accessDenied";
	private static final String PROPERTY_MYLUTECE_REINIT_PASSWORD_URL = "mylutece-directory.url.reinitPassword.page";
	private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED = "mylutece-directory.template.accessControled";
	private static final String PROPERTY_MAIL_HOST = "mail.server";
	private static final String PROPERTY_NOREPLY_EMAIL = "mail.noreply.email";
	private static final String PROPERTY_USE_CAPTCHA = "mylutece-directory.account_creation.use_jcaptcha";
	private static final String PROPERTY_NEED_ACTIVATION = "mylutece-directory.account_creation.must_validate";
	private static final String PROPERTY_NO_REPLY_EMAIL = "mail.noreply.email";
	private static final String PROPERTY_ACCOUNT_REF_ENCRYPT_ALGO = "mylutece-directory.account_life_time.refEncryptionAlgorythm";
    private static final String PROPERTY_DIRECTORY_MAIL_LOST_PASSWORD = "mylutece_directory_mailLostPassword";

	// i18n Properties
	private static final String PROPERTY_CHANGE_PASSWORD_LABEL = "module.mylutece.directory.xpage.changePassword.label";
	private static final String PROPERTY_CHANGE_PASSWORD_TITLE = "module.mylutece.directory.xpage.changePassword.title";
	private static final String PROPERTY_VIEW_ACCOUNT_LABEL = "module.mylutece.directory.xpage.viewAccount.label";
	private static final String PROPERTY_VIEW_ACCOUNT_TITLE = "module.mylutece.directory.xpage.viewAccount.title";
	private static final String PROPERTY_LOST_PASSWORD_LABEL = "module.mylutece.directory.xpage.lostPassword.label";
	private static final String PROPERTY_LOST_PASSWORD_TITLE = "module.mylutece.directory.xpage.lostPassword.title";
	private static final String PROPERTY_LOST_LOGIN_LABEL = "module.mylutece.directory.xpage.lostLogin.label";
	private static final String PROPERTY_LOST_LOGIN_TITLE = "module.mylutece.directory.xpage.lostLogin.title";
	private static final String PROPERTY_CREATE_ACCOUNT_LABEL = "module.mylutece.directory.xpage.createAccount.label";
	private static final String PROPERTY_CREATE_ACCOUNT_TITLE = "module.mylutece.directory.xpage.createAccount.title";
	private static final String PROPERTY_CREATE_ACCOUNT_LOGIN = "module.mylutece.directory.xpage.create_account.login";
	private static final String PROPERTY_CREATE_ACCOUNT_PASSWORD = "module.mylutece.directory.xpage.create_account.password";
	private static final String PROPERTY_CREATE_ACCOUNT_CONFIRMATION = "module.mylutece.directory.xpage.create_account.confirmation";
	private static final String PROPERTY_EMAIL_OBJECT = "module.mylutece.directory.email.object";
	private static final String PROPERTY_ACCESS_DENIED_ERROR_MESSAGE = "module.mylutece.directory.siteMessage.access_denied.errorMessage";
	private static final String PROPERTY_ACCESS_DENIED_TITLE_MESSAGE = "module.mylutece.directory.siteMessage.access_denied.title";
	private static final String PROPERTY_ERROR_MANDATORY_FIELDS = "module.mylutece.directory.message.account.errorMandatoryFields";
	private static final String PROPERTY_ERROR_LOGIN_ACCENTS = "module.mylutece.directory.message.create_account.errorLogin.accents";
	private static final String PROPERTY_ERROR_BAD_JCAPTCHA = "module.mylutece.directory.message.account.errorBadJcaptcha";
	private static final String PROPERTY_ERROR_CONFIRMATION = "module.mylutece.directory.message.account.errorConfirmation";
	private static final String PROPERTY_ERROR_LOGIN = "module.mylutece.directory.message.create_account.errorLogin";
	private static final String PROPERTY_REINIT_PASSWORD_LABEL = "module.mylutece.directory.xpage.reinit_password.label";
	private static final String PROPERTY_REINIT_PASSWORD_TITLE = "module.mylutece.directory.xpage.reinit_password.title";
	private static final String PROPERTY_ERROR_GENERIC_MESSAGE = "module.mylutece.directory.message.error.genericMessage";
	private static final String PROPERTY_NO_USER_SELECTED = "mylutece.message.noUserSelected";
	private static final String PROPERTY_MESSAGE_LABEL_ERROR = "mylutece.message.labelError";
	private static final String PROPERTY_ERROR_NO_ACCOUNT_TO_REACTIVATED = "mylutece.message.error.noAccountToReactivate";
	private static final String PROPERTY_ACCOUNT_REACTIVATED = "mylutece.user.messageAccountReactivated";
	private static final String PROPERTY_ACCOUNT_REACTIVATED_TITLE = "mylutece.user.messageAccountReactivatedTitle";

	// MESSAGES
	private static final String MESSAGE_REINIT_PASSWORD_SUCCESS = "module.mylutece.directory.message.reinit_password.success";
	private static final String MESSAGE_MINIMUM_PASSWORD_LENGTH = "mylutece.message.password.minimumPasswordLength";
	private static final String MESSAGE_PASSWORD_FORMAT = "mylutece.message.password.format";
	private static final String MESSAGE_PASSWORD_ALREADY_USED = "mylutece.message.password.passwordAlreadyUsed";
	private static final String MESSAGE_MAX_PASSWORD_CHANGE = "mylutece.message.password.maxPasswordChange";
	private static final String MESSAGE_PASSWORD_EXPIRED = "module.mylutece.directory.message.passwordExpired";
	private static final String MESSAGE_MUST_CHANGE_PASSWORD = "module.mylutece.directory.message.userMustChangePassword";

	private static final String JSP_URL_GET_RESET_PASSWORD_PAGE = "jsp/site/Portal.jsp?page=mylutecedirectory&action=getResetPasswordPage";
	private static final String JSP_URL_MYLUTECE_LOGIN = "jsp/site/Portal.jsp?page=mylutece&action=login";

	// Constants
	private static final String JCAPTCHA_PLUGIN = "jcaptcha";
	private static final String REGEX_LOGIN = "[^a-zA-Z_0-9]";

	// Sessions
	private IMyluteceDirectoryService _myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );
	private IMyluteceDirectorySecurityService _securityService = SpringContextService.getBean( MyluteceDirectorySecurityService.BEAN_SERVICE );
	private IAttributeMappingService _attributeMappingService = SpringContextService.getBean( AttributeMappingService.BEAN_SERVICE );
	private IMyluteceDirectoryUserKeyService _userKeyService = SpringContextService.getBean( MyluteceDirectoryUserKeyService.BEAN_SERVICE );
	private IMyluteceDirectoryParameterService _parameterService = SpringContextService.getBean( MyluteceDirectoryParameterService.BEAN_SERVICE );
	private IRecordService _recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
	private Plugin _plugin;
	private Locale _locale;

	/**
	 * 
	 * @param request The HTTP request
	 * @param plugin The plugin
	 */
	public void init( HttpServletRequest request, Plugin plugin )
	{
		_locale = request.getLocale( );
		_plugin = plugin;
	}

	/**
	 * 
	 * @param request The HTTP request
	 * @param nMode The mode (admin, ...)
	 * @param plugin The plugin
	 * @return The Xpage
	 * @throws UserNotSignedException if user not signed
	 * @throws SiteMessageException Occurs when a site message need to be displayed
	 */
	@Override
	public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException, SiteMessageException
	{
		XPage page = new XPage( );
		String strAction = request.getParameter( PARAMETER_ACTION );
		init( request, plugin );

		LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

		if ( luteceUser != null && MyluteceDirectoryHome.findResetPasswordFromLogin( luteceUser.getName( ), plugin ) && !ACTION_MODIFY_ACCOUNT.equals( strAction )
				&& !ACTION_UPDATE_ACCOUNT.equals( strAction ) )
		{
			getMessageResetPassword( request );
		}
		else
		{
			if ( ACTION_MODIFY_ACCOUNT.equals( strAction ) )
			{
				page = getModifyAccountPage( page, request );
			}
			else if ( ACTION_VIEW_ACCOUNT.equals( strAction ) )
			{
				page = getViewAccountPage( page, request );
			}
			else if ( ACTION_LOST_PASSWORD.equals( strAction ) )
			{
				page = getLostPasswordPage( page, request );
			}
			else if ( ACTION_LOST_LOGIN.equals( strAction ) )
			{
				page = getLostLoginPage( page, request );
			}
			else if ( ACTION_CREATE_ACCOUNT.equals( strAction ) )
			{
				page = getCreateAccountPage( page );
			}
			else if ( ACTION_DO_CREATE_ACCOUNT.equals( strAction ) )
			{
				FormErrors formErrors = doCreateAccount( request );
				page = getCreateAccountPage( page, formErrors );
			}
			else if ( ACTION_REINIT_PASSWORD.equals( strAction ) )
			{
				page = getReinitPasswordPage( page, request );
			}
			else if ( ACTION_UPDATE_ACCOUNT.equals( strAction ) )
			{
				updateAccount( request );
			}
			else if ( ACTION_GET_RESET_PASSWORD.equals( strAction ) )
			{
				getMessageResetPassword( request );
			}
		}

		if ( strAction.equals( ACTION_ACCESS_DENIED ) || ( page == null ) )
		{
			SiteMessageService.setMessage( request, PROPERTY_ACCESS_DENIED_ERROR_MESSAGE, null, PROPERTY_ACCESS_DENIED_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
		}

		return page;
	}

	/**
	 * Returns the NewAccount URL of the Authentication Service
	 * @return The URL
	 */
	public static String getModifyAccountUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_MODIFY_ACCOUNT_URL );
	}

	/**
	 * Returns the ViewAccount URL of the Authentication Service
	 * @return The URL
	 */
	public static String getViewAccountUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL );
	}

	/**
	 * Returns the createAccount URL of the Authentication Service
	 * @return The URL
	 */
	public static String getNewAccountUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL );
	}

	/**
	 * Returns the Lost Password URL of the Authentication Service
	 * @return The URL
	 */
	public static String getLostPasswordUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_PASSWORD_URL );
	}

	/**
	 * Returns the Lost login URL of the Authentication Service
	 * @return The URL
	 */
	public static String getLostLoginUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_LOGIN_URL );
	}

	/**
	 * Returns the Reset Password URL of the Authentication Service
	 * @return The URL
	 */
	public static String getResetPasswordUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_RESET_PASSWORD_URL );
	}

	public static String getMessageResetPasswordUrl( )
	{
		return JSP_URL_GET_RESET_PASSWORD_PAGE;
	}

	/**
	 * Returns the Default redirect URL of the Authentication Service
	 * @return The URL
	 */
	public static String getDefaultRedirectUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL );
	}

	/**
	 * Returns the NewAccount URL of the Authentication Service
	 * @return The URL
	 */
	public static String getAccessDeniedUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_ACCESS_DENIED_URL );
	}

	/**
	 * Returns the Reinit password page URL of the Authentication Service
	 * @return the URL
	 */
	public static String getReinitPageUrl( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_REINIT_PASSWORD_URL );
	}

	/**
	 * This method is call by the JSP named DoMyLuteceLogout.jsp
	 * @param request The HTTP request
	 * @return The URL to forward depending of the result of the login.
	 */
	public String doLogout( HttpServletRequest request )
	{
		SecurityService.getInstance( ).logoutUser( request );

		return getDefaultRedirectUrl( );
	}

	/**
	 * Build the ViewAccount page
	 * @param page The XPage object to fill
	 * @param request The HTTP request
	 * @return The XPage object containing the page content
	 */
	private XPage getViewAccountPage( XPage page, HttpServletRequest request )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		MyluteceDirectoryUser user = getRemoteUser( request );

		if ( user == null )
		{
			return null;
		}

		LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

		if ( luteceUser == null )
		{
			return null;
		}

		model.put( MARK_USER, luteceUser );
		model.put( MARK_ROLES, luteceUser.getRoles( ) );
		model.put( MARK_GROUPS, luteceUser.getGroups( ) );
		model.put( MARK_ATTRIBUTES_LIST, _attributeMappingService.getAllAttributeMappings( _plugin ) );

		HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_VIEW_ACCOUNT_PAGE, _locale, model );
		page.setContent( t.getHtml( ) );
		page.setPathLabel( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_LABEL, _locale ) );
		page.setTitle( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_TITLE, _locale ) );

		return page;
	}

	/**
	 * Build the createAccount page
	 * @param page The XPage object to fill
	 * @return The XPage object containing the page content
	 */
	private XPage getCreateAccountPage( XPage page )
	{
		return getCreateAccountPage( page, null );
	}

	/**
	 * Build the createAccount page
	 * @param page The XPage object to fill
	 * @param formErrors the form errors
	 * @return The XPage object containing the page content
	 */
	private XPage getCreateAccountPage( XPage page, FormErrors formErrors )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		Map<String, Object> model = new HashMap<String, Object>( );
		MyluteceDirectoryUser user = new MyluteceDirectoryUser( );

		List<IEntry> listEntry = new ArrayList<IEntry>( );

		Collection<Integer> listIdsDirectory = _myluteceDirectoryService.getMappedDirectories( _plugin );

		if ( ( listIdsDirectory != null ) && !listIdsDirectory.isEmpty( ) )
		{
			for ( int nIdDirectory : listIdsDirectory )
			{
				EntryFilter filter = new EntryFilter( );
				filter.setIdDirectory( nIdDirectory );

				listEntry = DirectoryUtils.getFormEntriesByFilter( filter, directoryPlugin );
			}
		}
		else
		{
			if ( formErrors == null )
			{
				formErrors = new FormErrors( );
			}

			formErrors.addError( ERROR_DIRECTORY_UNDEFINED, ERROR_DIRECTORY_UNDEFINED );
		}

		// Add Captcha
		boolean bIsCaptchaEnabled = PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) && Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_USE_CAPTCHA, "true" ) );
		model.put( MARK_IS_ACTIVE_CAPTCHA, bIsCaptchaEnabled );

		if ( bIsCaptchaEnabled )
		{
			CaptchaSecurityService captchaService = new CaptchaSecurityService( );
			model.put( MARK_CAPTCHA, captchaService.getHtmlCode( ) );
		}

		// Get directory record data
		if ( formErrors != null )
		{
			Record record = ( Record ) formErrors.getLastValue( PARAMETER_DIRECTORY_RECORD );

			if ( record != null )
			{
				model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, _myluteceDirectoryService.getMapIdEntryListRecordField( record ) );
			}

			String strLogin = ( String ) formErrors.getLastValue( PARAMETER_LOGIN );

			if ( StringUtils.isNotBlank( strLogin ) )
			{
				user.setLogin( strLogin );
			}
		}

		// Check if the creation is successful
		String strActionSuccessful = null;

		if ( ( formErrors != null ) && !formErrors.hasError( ) )
		{
			strActionSuccessful = getDefaultRedirectUrl( );
		}

		model.put( MARK_ENTRY_LIST, listEntry );
		model.put( MARK_LOCALE, _locale );
		model.put( MARK_PLUGIN_NAME, _plugin.getName( ) );
		model.put( MARK_USER, user );
		model.put( MARK_ACTION_SUCCESSFUL, strActionSuccessful );
		model.put( MARK_FORM_ERROR, formErrors );

		HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CREATE_ACCOUNT_PAGE, _locale, model );
		page.setContent( t.getHtml( ) );
		page.setPathLabel( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_LABEL, _locale ) );
		page.setTitle( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_TITLE, _locale ) );

		return page;
	}

	/**
	 * This method is call by the JSP named DoCreateAccount.jsp
	 * @param request The HTTP request
	 * @return The URL to forward depending of the result of the change.
	 */
	public FormErrors doCreateAccount( HttpServletRequest request )
	{
		Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
		init( request, plugin );

		FormErrors formErrors = new FormErrors( );

		String strLogin = request.getParameter( PARAMETER_LOGIN );
		String strPassword = request.getParameter( PARAMETER_PASSWORD );
		String strConfirmation = request.getParameter( PARAMETER_CONFIRMATION_PASSWORD );

		// Check if classic mandatory fields
		if ( StringUtils.isBlank( strLogin ) )
		{
			String strFieldName = I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_LOGIN, _locale );
			Object[] params =
			{ strFieldName };
			formErrors.addError( PARAMETER_LOGIN, I18nService.getLocalizedString( PROPERTY_ERROR_MANDATORY_FIELDS, params, _locale ) );
		}

		if ( StringUtils.isBlank( strPassword ) )
		{
			String strFieldName = I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_PASSWORD, _locale );
			Object[] params =
			{ strFieldName };
			formErrors.addError( PARAMETER_PASSWORD, I18nService.getLocalizedString( PROPERTY_ERROR_MANDATORY_FIELDS, params, _locale ) );
		}

		if ( StringUtils.isBlank( strConfirmation ) )
		{
			String strFieldName = I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_CONFIRMATION, _locale );
			Object[] params =
			{ strFieldName };
			formErrors.addError( PARAMETER_CONFIRMATION_PASSWORD, I18nService.getLocalizedString( PROPERTY_ERROR_MANDATORY_FIELDS, params, _locale ) );
		}

		formErrors.addLastValue( PARAMETER_LOGIN, strLogin );

		// Check if the login contain an accent
		Pattern pattern = Pattern.compile( REGEX_LOGIN );
		Matcher matcher = pattern.matcher( strLogin );
		boolean bAccentFound = false;

		while ( matcher.find( ) )
		{
			bAccentFound = true;

			break;
		}

		if ( bAccentFound )
		{
			formErrors.addError( ERROR_LOGIN_ACCENTS_OR_BLANK, I18nService.getLocalizedString( PROPERTY_ERROR_LOGIN_ACCENTS, _locale ) );
		}

		// Check login unique code
		Collection<MyluteceDirectoryUser> listUsers = _myluteceDirectoryService.getMyluteceDirectoryUsersForLogin( strLogin, _plugin );

		if ( ( listUsers != null ) && !listUsers.isEmpty( ) )
		{
			formErrors.addError( ERROR_LOGIN_ALREADY_EXISTS, I18nService.getLocalizedString( PROPERTY_ERROR_LOGIN, _locale ) );
		}

		// Check password confirmation
		if ( !checkPassword( strPassword, strConfirmation ) )
		{
			formErrors.addError( ERROR_CONFIRMATION_PASSWORD, I18nService.getLocalizedString( PROPERTY_ERROR_CONFIRMATION, _locale ) );
		}

		String strError = SecurityUtils.checkPasswordForFrontOffice( _parameterService, plugin, strPassword, 0 );
		if ( StringUtils.equals( strError, ERROR_PASSWORD_MINIMUM_LENGTH ) )
		{
			Object[] param =
			{ _parameterService.findByKey( MARK_PASSWORD_MINIMUM_LENGTH, _plugin ).getName( ) };
			formErrors.addError( ERROR_PASSWORD_MINIMUM_LENGTH, I18nService.getLocalizedString( MESSAGE_MINIMUM_PASSWORD_LENGTH, param, _locale ) );
		}
		if ( StringUtils.equals( strError, ERROR_PASSWORD_FORMAT ) )
		{
			formErrors.addError( ERROR_PASSWORD_FORMAT, I18nService.getLocalizedString( MESSAGE_PASSWORD_FORMAT, _locale ) );
		}
		if ( StringUtils.equals( strError, ERROR_PASSWORD_ALREADY_USED ) )
		{
			formErrors.addError( ERROR_PASSWORD_ALREADY_USED, I18nService.getLocalizedString( MESSAGE_PASSWORD_ALREADY_USED, _locale ) );
		}
		if ( StringUtils.equals( strError, ERROR_MAX_PASSWORD_CHANGE ) )
		{
			formErrors.addError( ERROR_MAX_PASSWORD_CHANGE, I18nService.getLocalizedString( MESSAGE_MAX_PASSWORD_CHANGE, _locale ) );
		}

		// Test the captcha
		if ( PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
		{
			CaptchaSecurityService captchaService = new CaptchaSecurityService( );

			if ( !captchaService.validate( request ) )
			{
				formErrors.addError( ERROR_JCAPTCHA, I18nService.getLocalizedString( PROPERTY_ERROR_BAD_JCAPTCHA, _locale ) );
			}
		}

		// Get directory record data
		Iterator<Integer> itIdDirectory = _myluteceDirectoryService.getMappedDirectories( _plugin ).iterator( );

		if ( !itIdDirectory.hasNext( ) )
		{
			formErrors.addError( ERROR_DIRECTORY_UNDEFINED, ERROR_DIRECTORY_UNDEFINED );

			return formErrors;
		}

		int nIdDirectory = itIdDirectory.next( );

		Directory directory = _myluteceDirectoryService.getDirectory( nIdDirectory );

		Record record = new Record( );
		record.setDirectory( directory );

		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		_myluteceDirectoryService.getDirectoryRecordData( request, record, directoryPlugin, _locale, formErrors );
		formErrors.addLastValue( PARAMETER_DIRECTORY_RECORD, record );

		if ( !formErrors.hasError( ) )
		{
			record.setDateCreation( DirectoryUtils.getCurrentTimestamp( ) );

			// Autopublication
			record.setEnabled( true );

			int nIdRecord = DirectoryUtils.CONSTANT_ID_NULL;

			try
			{
				nIdRecord = _recordService.create( record, directoryPlugin );
			}
			catch ( Exception ex )
			{
				// something very wrong happened... a database check might be needed
				AppLogService.error( ex.getMessage( ) + " for Record " + record.getIdRecord( ), ex );
				// revert
				// we clear the DB form the given record
				_recordService.remove( record.getIdRecord( ), directoryPlugin );

				// throw a message to the user
				formErrors.addError( ERROR_UNKNOWN, I18nService.getLocalizedString( PROPERTY_ERROR_GENERIC_MESSAGE, _locale ) );

				return formErrors;
			}

			if ( WorkflowService.getInstance( ).isAvailable( ) && ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL ) )
			{
				WorkflowService.getInstance( ).getState( record.getIdRecord( ), Record.WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow( ), Integer.valueOf( directory.getIdDirectory( ) ) );
				WorkflowService.getInstance( )
						.executeActionAutomatic( record.getIdRecord( ), Record.WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow( ), Integer.valueOf( directory.getIdDirectory( ) ) );
			}

			boolean bNeedActivation = Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_NEED_ACTIVATION, "false" ) );
			int nStatus = bNeedActivation ? MyluteceDirectoryUser.STATUS_NOT_ACTIVATED : MyluteceDirectoryUser.STATUS_ACTIVATED;
			// Create the directory user
			MyluteceDirectoryUser directoryUser = new MyluteceDirectoryUser( );
			directoryUser.setLogin( strLogin );
			directoryUser.setIdRecord( nIdRecord );
			directoryUser.setStatus( nStatus );

			try
			{
				_myluteceDirectoryService.doCreateMyluteceDirectoryUser( directoryUser, strPassword, directoryPlugin );
				int nUserId = MyluteceDirectoryUserHome.findIdRecordFromLogin( strLogin, plugin );
				if ( nUserId > 0 )
				{
					_myluteceDirectoryService.doInsertNewPasswordInHistory( strPassword, nUserId, plugin );
				}
			}
			catch ( Exception ex )
			{
				// something very wrong happened... a database check might be needed
				AppLogService.error( ex.getMessage( ) + " for MyLutece Directory User " + strLogin, ex );
				// revert
				// we clear the DB form the given record
				_myluteceDirectoryService.doRemoveMyluteceDirectoryUser( directoryUser, directoryPlugin, true );
				_recordService.remove( record.getIdRecord( ), directoryPlugin );

				// throw a message to the user
				formErrors.addError( ERROR_UNKNOWN, I18nService.getLocalizedString( PROPERTY_ERROR_GENERIC_MESSAGE, _locale ) );

				return formErrors;
			}
		}

		return formErrors;
	}

	/**
	 * Build the default Lost password page
	 * @param page The XPage object to fill
	 * @param request The HTTP request
	 * @return The XPage object containing the page content
	 */
	private XPage getLostPasswordPage( XPage page, HttpServletRequest request )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
		String strStateSending = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );
		String strEmail = request.getParameter( PARAMETER_EMAIL );

		model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
		model.put( MARK_ERROR_CODE, strErrorCode );
		model.put( MARK_ACTION_SUCCESSFUL, strStateSending );
		model.put( MARK_EMAIL, strEmail );

		HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_PASSWORD_PAGE, _locale, model );
		page.setContent( t.getHtml( ) );
		page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_LABEL, _locale ) );
		page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_TITLE, _locale ) );

		return page;
	}

	/**
	 * Build the default Lost login page
	 * @param page The XPage object to fill
	 * @param request The HTTP request
	 * @return The XPage object containing the page content
	 */
	private XPage getLostLoginPage( XPage page, HttpServletRequest request )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
		String strStateSending = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );
		String strEmail = request.getParameter( PARAMETER_EMAIL );

		model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
		model.put( MARK_ERROR_CODE, strErrorCode );
		model.put( MARK_ACTION_SUCCESSFUL, strStateSending );
		model.put( MARK_EMAIL, strEmail );

		HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_LOGIN_PAGE, _locale, model );
		page.setContent( t.getHtml( ) );
		page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_LOGIN_LABEL, _locale ) );
		page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_LOGIN_TITLE, _locale ) );

		return page;
	}

	/**
	 * Build the default modify account page
	 * @param page The XPage object to fill
	 * @param request The HTTP request
	 * @return The XPage object containing the page content
	 */
	private XPage getModifyAccountPage( XPage page, HttpServletRequest request )
	{
		MyluteceDirectoryUser user = getRemoteUser( request );

		if ( user == null )
		{
			return null;
		}

		String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
		String strSuccess = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );
		String strErrorField = request.getParameter( PARAMETER_ERROR_FIELD );
		String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );

		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		int nIdRecord = user.getIdRecord( );
		int nIdDirectory = _myluteceDirectoryService.getIdDirectoryByIdRecord( nIdRecord );
		EntryFilter filter = new EntryFilter( );
		filter.setIdDirectory( nIdDirectory );

		List<IEntry> listEntry = DirectoryUtils.getFormEntriesByFilter( filter, directoryPlugin );
		Map<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_ENTRY_LIST, listEntry );
		model.put( MARK_LOCALE, _locale );
		model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdRecord, directoryPlugin ) );
		model.put( MARK_PLUGIN_NAME, _plugin.getName( ) );
		model.put( MARK_ERROR_CODE, strErrorCode );
		model.put( MARK_ERROR_MESSAGE, strErrorMessage );
		model.put( MARK_ERROR_FIELD, strErrorField );
		model.put( MARK_ACTION_SUCCESSFUL, strSuccess );

		Object[] param =
		{ _parameterService.findByKey( MARK_PASSWORD_MINIMUM_LENGTH, _plugin ).getName( ) };
		model.put( MARK_PASSWORD_MINIMUM_LENGTH, I18nService.getLocalizedString( MESSAGE_MINIMUM_PASSWORD_LENGTH, param, _locale ) );

		HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CHANGE_PASSWORD_PAGE, _locale, model );
		page.setContent( t.getHtml( ) );
		page.setPathLabel( I18nService.getLocalizedString( PROPERTY_CHANGE_PASSWORD_LABEL, _locale ) );
		page.setTitle( I18nService.getLocalizedString( PROPERTY_CHANGE_PASSWORD_TITLE, _locale ) );

		return page;
	}

	/**
	 * This method is call by the JSP named DoModifyAccount.jsp
	 * @param request The HTTP request
	 * @return The URL to forward depending of the result of the change.
	 */
	public String doModifyAccount( HttpServletRequest request )
	{
		boolean bChangePassword = false;
		Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		init( request, plugin );

		UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getModifyAccountUrl( ) );
		url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName( ) );

		String strError = null;
		MyluteceDirectoryUser user = getRemoteUser( request );
		String strOldPassword = request.getParameter( PARAMETER_OLD_PASSWORD );
		String strNewPassword = request.getParameter( PARAMETER_NEW_PASSWORD );
		String strConfirmationPassword = request.getParameter( PARAMETER_CONFIRMATION_PASSWORD );

		if ( ( user == null ) )
		{
			try
			{
				SiteMessageService.setMessage( request, PROPERTY_ACCESS_DENIED_ERROR_MESSAGE, null, PROPERTY_ACCESS_DENIED_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
			}
			catch ( SiteMessageException e )
			{
				return AppPathService.getBaseUrl( request );
			}
		}

		if ( StringUtils.isNotBlank( strNewPassword ) && ( StringUtils.isBlank( strOldPassword ) || StringUtils.isBlank( strConfirmationPassword ) ) )
		{
			strError = ERROR_MANDATORY_FIELDS;
		}
		else if ( StringUtils.isNotBlank( strNewPassword ) )
		{
			bChangePassword = true;
		}

		if ( bChangePassword && StringUtils.isBlank( strError ) )
		{
			if ( !_securityService.checkPassword( user.getLogin( ), strOldPassword ) )
			{
				strError = ERROR_OLD_PASSWORD;
			}
			else if ( !checkPassword( strNewPassword, strConfirmationPassword ) )
			{
				strError = ERROR_CONFIRMATION_PASSWORD;
			}
			else if ( StringUtils.equals( strOldPassword, strNewPassword ) )
			{
				strError = ERROR_SAME_PASSWORD;
			}
			else
			{
				strError = SecurityUtils.checkPasswordForFrontOffice( _parameterService, plugin, strNewPassword, user.getIdRecord( ) );
			}
		}

		if ( StringUtils.isNotBlank( strError ) )
		{
			url.addParameter( PARAMETER_ERROR_CODE, strError );
		}
		else
		{
			if ( bChangePassword )
			{
				_myluteceDirectoryService.doModifyPassword( user, strNewPassword, _plugin );
				_myluteceDirectoryService.doModifyResetPassword( user, Boolean.FALSE, _plugin );
				_myluteceDirectoryService.doInsertNewPasswordInHistory( strNewPassword, user.getIdRecord( ), plugin );
			}

			int nIdRecord = user.getIdRecord( );
			Record record = _myluteceDirectoryService.getRecord( nIdRecord, false );

			try
			{
				DirectoryUtils.getDirectoryRecordData( request, record, directoryPlugin, _locale );
			}
			catch ( DirectoryErrorException error )
			{
				if ( error.isMandatoryError( ) )
				{
					strError = ERROR_DIRECTORY_FIELD;
				}
				else
				{
					url.addParameter( PARAMETER_ERROR_MESSAGE, error.getTitleField( ) );
					strError = ERROR_DIRECTORY_MESSAGE;
				}

				url.addParameter( PARAMETER_ERROR_FIELD, error.getTitleField( ) );
				url.addParameter( PARAMETER_ERROR_CODE, strError );

				return url.getUrl( );
			}

			IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
			recordService.updateWidthRecordField( record, directoryPlugin );

			url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl( ) );
		}

		return url.getUrl( );
	}

	/**
	 * Check the password with the password confirmation string Check if password is empty
	 * 
	 * @param strPassword The password
	 * @param strConfirmation The password confirmation
	 * @return true if password is equal to confirmation password and not empty
	 */
	private boolean checkPassword( String strPassword, String strConfirmation )
	{
		Boolean bReturn = true;

		if ( ( strPassword == null ) || ( strConfirmation == null ) || strPassword.equals( "" ) || !strPassword.equals( strConfirmation ) )
		{
			bReturn = false;
		}

		return bReturn;
	}

	/**
	 * This method is call by the JSP named DoSendPassword.jsp
	 * 
	 * @param request The HTTP request
	 * @return The URL to forward depending of the result of the sending.
	 */
	public String doSendPassword( HttpServletRequest request )
	{
		Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
		init( request, plugin );

		Map<String, Object> model = new HashMap<String, Object>( );
		String strError = null;

		String strLogin = request.getParameter( PARAMETER_LOGIN );
		String strEmail = request.getParameter( PARAMETER_EMAIL );
		UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getLostPasswordUrl( ) );
		url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName( ) );
		url.addParameter( PARAMETER_EMAIL, strEmail );

		// Check mandatory fields
		if ( StringUtils.isBlank( strEmail ) || StringUtils.isBlank( strLogin ) )
		{
			strError = ERROR_MANDATORY_FIELDS;
		}

		// Check email format
		if ( ( strError == null ) && !StringUtil.checkEmail( strEmail ) )
		{
			strError = ERROR_SYNTAX_EMAIL;
		}

		Collection<MyluteceDirectoryUser> listUser = _myluteceDirectoryService.getMyluteceDirectoryUsersForEmail( strEmail, _plugin, _locale );

		if ( StringUtils.isBlank( strError ) && ( ( listUser == null ) || listUser.isEmpty( ) ) )
		{
			strError = ERROR_UNKNOWN_EMAIL;
		}

		MyluteceDirectoryUser validUser = null;

		if ( StringUtils.isBlank( strError ) )
		{
			for ( MyluteceDirectoryUser user : listUser )
			{
				if ( user.getLogin( ).equals( strLogin ) && user.isActivated( ) )
				{
					validUser = user;

					// make password
					String strPassword = PasswordUtil.makePassword( );

					_myluteceDirectoryService.doModifyPassword( user, strPassword, _plugin );

					if ( SecurityUtils.getBooleanSecurityParameter( _parameterService, plugin, PARAMETER_FORCE_CHANGE_PASSWORD_REINIT ) )
					{
						_myluteceDirectoryService.doModifyResetPassword( user, Boolean.TRUE, _plugin );
					}

					String strHost = AppPropertiesService.getProperty( PROPERTY_MAIL_HOST );

                    if ( StringUtils.isBlank( strError ) && StringUtils.isBlank( strHost ) )
					{
						strError = ERROR_SENDING_EMAIL;
					}
					else
					{
						model.put( PARAMETER_NEW_PASSWORD, strPassword );

						MyluteceDirectoryUserKey key = _userKeyService.create( user.getIdRecord( ) );
						model.put( MARK_REINIT_URL, _userKeyService.getReinitUrl( key.getKey( ), request ) );
						model.put( MARK_SITE_LINK, MailService.getSiteLink( AppPathService.getBaseUrl( request ), true ) );

                        HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl(
                                DatabaseTemplateService.getTemplateFromKey( PROPERTY_DIRECTORY_MAIL_LOST_PASSWORD ),
                                _locale, model );

                        ReferenceItem referenceItem = _parameterService.findByKey( PARAMETER_MAIL_LOST_PASSWORD_SENDER,
                                plugin );
                        String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                        referenceItem = _parameterService.findByKey( PARAMETER_MAIL_LOST_PASSWORD_SUBJECT, plugin );
                        String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                        MailService.sendMailHtml( strEmail, PROPERTY_NO_REPLY_EMAIL, strSender, strSubject,
                                template.getHtml( ) );

					}
				}
			}

			if ( validUser == null )
			{
				url.addParameter( PARAMETER_ERROR_CODE, ERROR_UNKNOWN_EMAIL );

				return url.getUrl( );
			}
		}

		else
		{
			url.addParameter( PARAMETER_ERROR_CODE, strError );

			return url.getUrl( );
		}

		url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl( ) );

		return url.getUrl( );
	}

	/**
	 * This method is call by the JSP named DoSendLogin.jsp
	 * 
	 * @param request The HTTP request
	 * @return The URL to forward depending of the result of the sending.
	 */
	public String doSendLogin( HttpServletRequest request )
	{
		Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
		init( request, plugin );

		Map<String, Object> model = new HashMap<String, Object>( );
		String strError = null;

		String strEmail = request.getParameter( PARAMETER_EMAIL );
		UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getLostLoginUrl( ) );
		url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName( ) );
		url.addParameter( PARAMETER_EMAIL, strEmail );

		// Check email format
		if ( ( strError == null ) && !StringUtil.checkEmail( strEmail ) )
		{
			strError = ERROR_SYNTAX_EMAIL;
		}

		Collection<MyluteceDirectoryUser> listUser = _myluteceDirectoryService.getMyluteceDirectoryUsersForEmail( strEmail, _plugin, _locale );

		if ( StringUtils.isBlank( strError ) && ( ( listUser == null ) || listUser.isEmpty( ) ) )
		{
			strError = ERROR_UNKNOWN_EMAIL;
		}

		MyluteceDirectoryUser validUser = null;

		if ( StringUtils.isBlank( strError ) )
		{
			for ( MyluteceDirectoryUser user : listUser )
			{
				validUser = user;

				String strHost = AppPropertiesService.getProperty( PROPERTY_MAIL_HOST );
				String strSender = AppPropertiesService.getProperty( PROPERTY_NOREPLY_EMAIL );
				String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_OBJECT, _locale );

				if ( StringUtils.isBlank( strError ) && ( StringUtils.isBlank( strHost ) || StringUtils.isBlank( strSender ) || StringUtils.isBlank( strObject ) ) )
				{
					strError = ERROR_SENDING_EMAIL;
				}
				else
				{
					MyluteceDirectoryUserKey key = _userKeyService.create( user.getIdRecord( ) );
					model.put( MARK_LOGIN, user.getLogin( ) );
					model.put( MARK_REINIT_URL, _userKeyService.getReinitUrl( key.getKey( ), request ) );
					model.put( MARK_SITE_LINK, MailService.getSiteLink( AppPathService.getBaseUrl( request ), true ) );
					model.put( MARK_LOGIN_URL, AppPathService.getBaseUrl( request ) + JSP_URL_MYLUTECE_LOGIN );

					HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_LOST_LOGIN, _locale, model );

					MailService.sendMailHtml( strEmail, PROPERTY_NO_REPLY_EMAIL, strSender, strObject, template.getHtml( ) );
				}
			}

			if ( validUser == null )
			{
				url.addParameter( PARAMETER_ERROR_CODE, ERROR_UNKNOWN_EMAIL );

				return url.getUrl( );
			}
		}
		else
		{
			url.addParameter( PARAMETER_ERROR_CODE, strError );

			return url.getUrl( );
		}

		url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl( ) );

		return url.getUrl( );
	}

	/**
	 * Returns the template for access denied
	 * @return The template path
	 */
	public static String getAccessDeniedTemplate( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED );
	}

	/**
	 * Returns the template for access controled
	 * @return The template path
	 */
	public static String getAccessControledTemplate( )
	{
		return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED );
	}

	/**
	 * Get reinit password page
	 * @param page the page
	 * @param request the HTTP servlet request
	 * @return the page
	 * @throws SiteMessageException site message if the key is wrong
	 */
	public XPage getReinitPasswordPage( XPage page, HttpServletRequest request ) throws SiteMessageException
	{
		String strActionSuccess = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );

		if ( StringUtils.isNotBlank( strActionSuccess ) )
		{
			SiteMessageService.setMessage( request, MESSAGE_REINIT_PASSWORD_SUCCESS, SiteMessage.TYPE_INFO, AppPathService.getBaseUrl( request ) + strActionSuccess );
		}

		String strKey = request.getParameter( PARAMETER_KEY );
		MyluteceDirectoryUserKey key = null;

		if ( StringUtils.isNotBlank( strKey ) )
		{
			key = _userKeyService.findByPrimaryKey( strKey );
		}

		LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

		if ( key == null )
		{
			key = _userKeyService.findKeyByLogin( luteceUser.getName( ) );
		}

		// If the user is logged in, has no key and must change his password, we generate a new key
		if ( key == null && luteceUser != null && _myluteceDirectoryService.mustUserChangePassword( luteceUser, _plugin ) )
		{
			MyluteceDirectoryUser user = getRemoteUser( request );
			key = _userKeyService.create( user.getIdRecord( ) );

		}

		if ( key != null )
		{
			strKey = key.getKey( );
			String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
			Map<String, Object> model = new HashMap<String, Object>( );
			model.put( MARK_ERROR_CODE, strErrorCode );
			model.put( MARK_KEY, key.getKey( ) );
			model.put( MARK_ACTION_SUCCESSFUL, request.getParameter( PARAMETER_ACTION_SUCCESSFUL ) );

			if ( StringUtils.equals( strErrorCode, ERROR_PASSWORD_MINIMUM_LENGTH ) )
			{
				Object[] param =
				{ _parameterService.findByKey( MARK_PASSWORD_MINIMUM_LENGTH, _plugin ).getName( ) };
				model.put( MARK_PASSWORD_MINIMUM_LENGTH, I18nService.getLocalizedString( MESSAGE_MINIMUM_PASSWORD_LENGTH, param, _locale ) );
			}

			HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_REINIT_PASSWORD_PAGE, _locale, model );
			page.setContent( t.getHtml( ) );
			page.setPathLabel( I18nService.getLocalizedString( PROPERTY_REINIT_PASSWORD_LABEL, _locale ) );
			page.setTitle( I18nService.getLocalizedString( PROPERTY_REINIT_PASSWORD_TITLE, _locale ) );
		}
		else
		{
			SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, AppPathService.getBaseUrl( request ) + getDefaultRedirectUrl( ) );
		}

		return page;
	}

	/**
	 * Do reinit the password
	 * @param request the http servlet request
	 * @return the url return
	 */
	public String doReinitPassword( HttpServletRequest request )
	{
		Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
		init( request, plugin );

		String strKey = request.getParameter( PARAMETER_KEY );

		UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getReinitPageUrl( ) );
		url.addParameter( PARAMETER_KEY, strKey );

		if ( StringUtils.isNotBlank( strKey ) )
		{
			MyluteceDirectoryUserKey userKey = _userKeyService.findByPrimaryKey( strKey );

			if ( userKey != null )
			{
				MyluteceDirectoryUser myluteceDirectoryUser = _myluteceDirectoryService.getMyluteceDirectoryUser( userKey.getIdRecord( ), plugin );

				if ( myluteceDirectoryUser != null )
				{
					String strPassword = request.getParameter( PARAMETER_PASSWORD );
					String strConfirmationPassword = request.getParameter( PARAMETER_CONFIRMATION_PASSWORD );

					if ( !( StringUtils.isNotBlank( strPassword ) && StringUtils.isNotBlank( strConfirmationPassword ) && strPassword.equals( strConfirmationPassword ) ) )
					{
						url.addParameter( PARAMETER_ERROR_CODE, ERROR_CONFIRMATION_PASSWORD );
						return url.getUrl( );
					}
					String strErrorCode = SecurityUtils.checkPasswordForFrontOffice( _parameterService, plugin, strPassword, userKey.getIdRecord( ) );
					if ( strErrorCode != null )
					{
						url.addParameter( PARAMETER_ERROR_CODE, strErrorCode );
						return url.getUrl( );
					}
					_myluteceDirectoryService.doModifyPassword( myluteceDirectoryUser, strPassword, plugin );
					_myluteceDirectoryService.doModifyResetPassword( myluteceDirectoryUser, Boolean.FALSE, _plugin );
					_myluteceDirectoryService.doInsertNewPasswordInHistory( strPassword, myluteceDirectoryUser.getIdRecord( ), plugin );
					_userKeyService.remove( userKey.getKey( ) );
					url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl( ) );
				}
			}
		}

		return url.getUrl( );
	}

	/**
	 * Get the remote user
	 * @param request The HTTP request
	 * @return The Directory User
	 */
	private MyluteceDirectoryUser getRemoteUser( HttpServletRequest request )
	{
		LuteceUser luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

		if ( luteceUser == null )
		{
			return null;
		}

		Collection<MyluteceDirectoryUser> listUsers = _myluteceDirectoryService.getMyluteceDirectoryUsersForLogin( luteceUser.getName( ), _plugin );

		if ( ( listUsers == null ) || ( listUsers.size( ) != 1 ) )
		{
			return null;
		}

		MyluteceDirectoryUser user = listUsers.iterator( ).next( );

		return user;
	}

	/**
	 * Reactivate an account if necessary
	 * @param request The request
	 * @throws SiteMessageException A SiteMessageException is ALWAYS thrown
	 */
	private void updateAccount( HttpServletRequest request ) throws SiteMessageException
	{
		String strUserId = request.getParameter( MARK_USER_ID );
		String strRef = request.getParameter( MARK_REF );

		int nUserId = -1;
		if ( strUserId != null && StringUtils.isNotBlank( strUserId ) )
		{
			try
			{
				nUserId = Integer.parseInt( strUserId );
			}
			catch ( NumberFormatException e )
			{
				nUserId = -1;
			}
		}
		if ( nUserId < 0 || StringUtils.isEmpty( strRef ) )
		{
			SiteMessageService.setMessage( request, PROPERTY_NO_USER_SELECTED, null, PROPERTY_MESSAGE_LABEL_ERROR, AppPropertiesService.getProperty( AppPathService.getBaseUrl( request ) ), null,
					SiteMessage.TYPE_ERROR );
		}
		else
		{
			MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nUserId, _plugin );
			if ( user == null
					|| user.getAccountMaxValidDate( ) == null
					|| !StringUtils.equals( CryptoService.encrypt( Long.toString( user.getAccountMaxValidDate( ).getTime( ) ), AppPropertiesService.getProperty( PROPERTY_ACCOUNT_REF_ENCRYPT_ALGO ) ),
							strRef ) )
			{
				SiteMessageService.setMessage( request, PROPERTY_NO_USER_SELECTED, null, PROPERTY_MESSAGE_LABEL_ERROR, AppPropertiesService.getProperty( AppPathService.getBaseUrl( request ) ), null,
						SiteMessage.TYPE_ERROR );
			}
			int nbDaysBeforeFirstAlert = SecurityUtils.getIntegerSecurityParameter( _parameterService, _plugin, PARAMETER_TIME_BEFORE_ALERT_ACCOUNT );
			Timestamp firstAlertMaxDate = new Timestamp( new java.util.Date( ).getTime( ) + DateUtil.convertDaysInMiliseconds( nbDaysBeforeFirstAlert ) );
			// If the account is close to expire but has not expired yet
			if ( user.getAccountMaxValidDate( ) != null )
			{
				if ( user.getAccountMaxValidDate( ).getTime( ) < firstAlertMaxDate.getTime( ) && user.getStatus( ) < MyluteceDirectoryUser.STATUS_EXPIRED )
				{
					_myluteceDirectoryService.updateUserExpirationDate( nUserId, _plugin );
				}
				SiteMessageService.setMessage( request, PROPERTY_ACCOUNT_REACTIVATED, null, PROPERTY_ACCOUNT_REACTIVATED_TITLE, AppPropertiesService
						.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL ), null, SiteMessage.TYPE_INFO );
			}
		}
		SiteMessageService.setMessage( request, PROPERTY_ERROR_NO_ACCOUNT_TO_REACTIVATED, null, PROPERTY_MESSAGE_LABEL_ERROR, AppPropertiesService.getProperty( AppPathService.getBaseUrl( request ) ),
				null, SiteMessage.TYPE_ERROR );
	}

	public void getMessageResetPassword( HttpServletRequest request ) throws SiteMessageException
	{
		SiteMessageService.setMessage( request, MESSAGE_MUST_CHANGE_PASSWORD, null, MESSAGE_PASSWORD_EXPIRED, getResetPasswordUrl( ), null, SiteMessage.TYPE_INFO );
	}
}
