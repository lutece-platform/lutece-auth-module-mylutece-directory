/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.AttributeMappingHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;

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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the XPageApp that manage personalization features for Mylutece Directory module
 * : login, account management, ...
 */
public class MyLuteceDirectoryApp implements XPageApplication
{
    // Markers
    private static final String MARK_USER = "user";
    private static final String MARK_PASSWORD = "password";
    private static final String MARK_PORTAL_URL = "portal_url";
    private static final String MARK_PORTAL_NAME = "portal_name";
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

    // Actions
    private static final String ACTION_MODIFY_ACCOUNT = "modifyAccount";
    private static final String ACTION_VIEW_ACCOUNT = "viewAccount";
    private static final String ACTION_LOST_PASSWORD = "lostPassword";
    private static final String ACTION_ACCESS_DENIED = "accessDenied";
    private static final String ACTION_CREATE_ACCOUNT = "createAccount";

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
    private static final String ERROR_EMAIL_ALREADY_EXISTS = "error_email_already_exists";
    private static final String ERROR_JCAPTCHA = "error_jcaptcha";
    private static final String ERROR_DIRECTORY_FIELD = "error_directory_field";
    private static final String ERROR_DIRECTORY_MESSAGE = "error_directory_message";
    private static final String ERROR_DIRECTORY_UNDEFINED = "error_directory_undefined";
    
    
    // Templates
    private static final String TEMPLATE_LOST_PASSWORD_PAGE = "skin/plugins/mylutece/modules/directory/lost_password.html";
    private static final String TEMPLATE_VIEW_ACCOUNT_PAGE = "skin/plugins/mylutece/modules/directory/view_account.html";
    private static final String TEMPLATE_CHANGE_PASSWORD_PAGE = "skin/plugins/mylutece/modules/directory/change_password.html";
    private static final String TEMPLATE_EMAIL_BODY = "skin/plugins/mylutece/modules/directory/email_body.html";
    private static final String TEMPLATE_CREATE_ACCOUNT_PAGE = "skin/plugins/mylutece/modules/directory/create_account.html";

    // Properties
    private static final String PROPERTY_MYLUTECE_MODIFY_ACCOUNT_URL = "mylutece-directory.url.modifyAccount.page";
    private static final String PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL = "mylutece-directory.url.viewAccount.page";
    private static final String PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL = "mylutece-directory.url.createAccount.page";
    private static final String PROPERTY_MYLUTECE_LOST_PASSWORD_URL = "mylutece-directory.url.lostPassword.page";
    private static final String PROPERTY_MYLUTECE_ACCESS_DENIED_URL = "mylutece-directory.url.accessDenied.page";
    private static final String PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL = "mylutece-directory.url.default.redirect";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED = "mylutece-directory.template.accessDenied";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED = "mylutece-directory.template.accessControled";
    private static final String PROPERTY_MAIL_HOST = "mail.server";
    private static final String PROPERTY_PORTAL_NAME = "lutece.name";
    private static final String PROPERTY_PORTAL_URL = "lutece.prod.url";
    private static final String PROPERTY_NOREPLY_EMAIL = "mail.noreply.email";
    private static final String PROPERTY_USE_CAPTCHA = "mylutece-directory.account_creation.use_jcaptcha";
    private static final String PROPERTY_NEED_ACTIVATION = "mylutece-directory.account_creation.must_validate";

    // i18n Properties
    private static final String PROPERTY_CHANGE_PASSWORD_LABEL = "module.mylutece.directory.xpage.changePassword.label";
    private static final String PROPERTY_CHANGE_PASSWORD_TITLE = "module.mylutece.directory.xpage.changePassword.title";
    private static final String PROPERTY_VIEW_ACCOUNT_LABEL = "module.mylutece.directory.xpage.viewAccount.label";
    private static final String PROPERTY_VIEW_ACCOUNT_TITLE = "module.mylutece.directory.xpage.viewAccount.title";
    private static final String PROPERTY_LOST_PASSWORD_LABEL = "module.mylutece.directory.xpage.lostPassword.label";
    private static final String PROPERTY_LOST_PASSWORD_TITLE = "module.mylutece.directory.xpage.lostPassword.title";
    private static final String PROPERTY_CREATE_ACCOUNT_LABEL = "module.mylutece.directory.xpage.createAccount.label";
    private static final String PROPERTY_CREATE_ACCOUNT_TITLE = "module.mylutece.directory.xpage.createAccount.title";
    private static final String PROPERTY_EMAIL_OBJECT = "module.mylutece.directory.email.object";
    private static final String PROPERTY_ACCESS_DENIED_ERROR_MESSAGE = "module.mylutece.directory.siteMessage.access_denied.errorMessage";
    private static final String PROPERTY_ACCESS_DENIED_TITLE_MESSAGE = "module.mylutece.directory.siteMessage.access_denied.title";
    private static final String PROPERTY_ERROR_MANDATORY_FIELD_MESSAGE="module.mylutece.directory.message.mandatory.field";
    private static final String PROPERTY_FIELD_ERROR_MESSAGE="module.mylutece.directory.message.error.field";
    
    //Constants
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    
    // private fields
    private static final String PREFIX_LUTECE_USER = "user.";
    private Plugin _plugin;
    private Locale _locale;

    /**
     *
     * @param request The HTTP request
     * @param plugin The plugin
     */
    public void init( HttpServletRequest request, Plugin plugin )
    {
        _locale = request.getLocale(  );
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
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws UserNotSignedException, SiteMessageException
    {
        XPage page = new XPage(  );
        String strAction = request.getParameter( PARAMETER_ACTION );
        init( request, plugin );

        if ( strAction.equals( ACTION_MODIFY_ACCOUNT ) )
        {
            page = getModifyAccountPage( page, request );
        }
        else if ( strAction.equals( ACTION_VIEW_ACCOUNT ) )
        {
            page = getViewAccountPage( page, request );
        }
        else if ( strAction.equals( ACTION_LOST_PASSWORD ) )
        {
            page = getLostPasswordPage( page, request );
        }
        else if ( strAction.equals( ACTION_CREATE_ACCOUNT ) )
        {
            page = getCreateAccountPage( page, request );
        }

        if ( strAction.equals( ACTION_ACCESS_DENIED ) || ( page == null ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_ACCESS_DENIED_ERROR_MESSAGE, null,
                PROPERTY_ACCESS_DENIED_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
        }

        return page;
    }

    /**
     * Returns the NewAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getModifyAccountUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_MODIFY_ACCOUNT_URL );
    }

    /**
     * Returns the ViewAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getViewAccountUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL );
    }

    /**
     * Returns the createAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getNewAccountUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL );
    }

    /**
     * Returns the Lost Password URL of the Authentication Service
     * @return The URL
     */
    public static String getLostPasswordUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_PASSWORD_URL );
    }

    /**
     * Returns the Default redirect URL of the Authentication Service
     * @return The URL
     */
    public static String getDefaultRedirectUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL );
    }

    /**
     * Returns the NewAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getAccessDeniedUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_ACCESS_DENIED_URL );
    }

    /**
     * This method is call by the JSP named DoMyLuteceLogout.jsp
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the login.
     */
    public String doLogout( HttpServletRequest request )
    {
        SecurityService.getInstance(  ).logoutUser( request );

        return getDefaultRedirectUrl(  );
    }

    /**
     * Build the ViewAccount page
     * @param page The XPage object to fill
     * @param request The HTTP request
     * @return The XPage object containing the page content
     */
    private XPage getViewAccountPage( XPage page, HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        MyluteceDirectoryUser user = getRemoteUser( request );

        if ( user == null )
        {
            return null;
        }

        LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( luteceUser == null )
        {
            return null;
        }

        model.put( MARK_USER, luteceUser );
        model.put( MARK_ROLES, luteceUser.getRoles(  ) );
        model.put( MARK_GROUPS, luteceUser.getGroups(  ) );
        model.put( MARK_ATTRIBUTES_LIST, AttributeMappingHome.findAll( _plugin ) );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_VIEW_ACCOUNT_PAGE, _locale, model );
        page.setContent( t.getHtml(  ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the createAccount page
     * @param page The XPage object to fill
     * @param request The HTTP request
     * @return The XPage object containing the page content
     */
    private XPage getCreateAccountPage( XPage page, HttpServletRequest request )
    {
    	Plugin directoryPlugin = PluginService.getPlugin(DirectoryPlugin.PLUGIN_NAME);
    	HashMap<String,Object> model = new HashMap<String,Object>(  );
    	MyluteceDirectoryUser user = new MyluteceDirectoryUser(  );
    	
    	String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
    	String strLogin = request.getParameter( PARAMETER_LOGIN );
    	String strErrorField = request.getParameter( PARAMETER_ERROR_FIELD );
    	String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );
    	
    	List<IEntry> listEntry = new ArrayList<IEntry>();
    	String strSuccess = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );

    	if ( strLogin != null )
    	{
    		user.setLogin( strLogin );
    	}
    	Iterator<Integer> itIdDirectory = MyluteceDirectoryHome.findMappedDirectories( _plugin ).iterator(  );      
    	if(itIdDirectory.hasNext())
    	{
    		int nIdDirectory =  itIdDirectory.next(  );    	
        	EntryFilter filter = new EntryFilter(  );
        	filter.setIdDirectory(nIdDirectory);
        	
        	listEntry = DirectoryUtils.getFormEntriesByFilter( filter,directoryPlugin );
        	
    	}
    	else
    	{    		
    		strErrorCode = ERROR_DIRECTORY_UNDEFINED ;        		   		
    	}
    	

    	boolean bIsCaptchaEnabled = PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) 
    		&& Boolean.parseBoolean( AppPropertiesService.getProperty(PROPERTY_USE_CAPTCHA, "true") );
    	model.put( MARK_IS_ACTIVE_CAPTCHA, bIsCaptchaEnabled );
    	if ( bIsCaptchaEnabled )
    	{
    		CaptchaSecurityService captchaService = new CaptchaSecurityService(  );
    		model.put( MARK_CAPTCHA, captchaService.getHtmlCode(  ) );
    	}

    	model.put( MARK_ENTRY_LIST, listEntry );
    	model.put( MARK_LOCALE, _locale );

    	model.put( MARK_PLUGIN_NAME, _plugin.getName(  ) );
    	model.put( MARK_ERROR_CODE, strErrorCode );
    	model.put( MARK_ERROR_MESSAGE, strErrorMessage );    	
    	model.put( MARK_ERROR_FIELD, strErrorField );    	
    	model.put( MARK_USER, user );
    	model.put( MARK_ACTION_SUCCESSFUL, strSuccess );

    	HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CREATE_ACCOUNT_PAGE, _locale, model );
    	page.setContent( t.getHtml(  ) );
    	page.setPathLabel( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_LABEL, _locale ) );
    	page.setTitle( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_TITLE, _locale ) );

    	return page;
    }

    /**
     * This method is call by the JSP named DoCreateAccount.jsp
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the change.
     */
    public String doCreateAccount( HttpServletRequest request )
    {
    	Plugin directoryPlugin = PluginService.getPlugin(DirectoryPlugin.PLUGIN_NAME);
    	Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
    	MyluteceDirectoryUser directoryUser = new MyluteceDirectoryUser(  );
    	init( request, plugin );

    	UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getNewAccountUrl(  ) );
    	url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName(  ) );

    	String strError = null;
    	String strLogin = request.getParameter( PARAMETER_LOGIN );
    	String strPassword = request.getParameter( PARAMETER_PASSWORD );
    	String strConfirmation = request.getParameter( PARAMETER_CONFIRMATION_PASSWORD );
    	
    	url.addParameter( PARAMETER_LOGIN, strLogin );    	
    
    	if ( ( strLogin == null ) || ( strPassword == null ) || ( strConfirmation == null ) || 
    			 strLogin.equals( "" ) || strPassword.equals( "" ) ||
    			strConfirmation.equals( "" )  )
    	{    		
    		strError = ERROR_MANDATORY_FIELDS;
    	}

    	//Check if the login contain an accent
    	Pattern pattern = Pattern.compile("[^a-zA-Z_0-9]");
    	Matcher matcher = pattern.matcher(strLogin);
    	Boolean bAccentFound = false;
    	while( matcher.find(  ) ) {
    		bAccentFound = true;
    	}
    	if ( strError == null  && bAccentFound==true) {
    		strError = ERROR_LOGIN_ACCENTS_OR_BLANK;
    	}
    	
    	//Check login unique code
    	if ( ( strError == null ) && !MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( strLogin, _plugin ).isEmpty(  ) )
    	{
    		strError = ERROR_LOGIN_ALREADY_EXISTS;
    	}   	
    	

    	//Check password confirmation
    	if ( ( strError == null ) && !checkPassword( strPassword, strConfirmation ) )
    	{
    		strError = ERROR_CONFIRMATION_PASSWORD;
    	}
    	
    	   //test the captcha
        if ( PluginService.isPluginEnable( JCAPTCHA_PLUGIN )  )
        {
        	CaptchaSecurityService captchaService = new CaptchaSecurityService(  );

            if ( ( strError == null ) && ( !captchaService.validate( request ) ) )
            {
            	strError = ERROR_JCAPTCHA;
            }
        }

    	if ( strError != null )
    	{
    		url.addParameter( PARAMETER_ERROR_CODE, strError );

    		return url.getUrl(  );
    	}
    	else
    	{

    		Iterator<Integer> itIdDirectory = MyluteceDirectoryHome.findMappedDirectories( _plugin ).iterator(  );   
    		
    		if( ! itIdDirectory.hasNext(  ) )
    		{
    			url.addParameter( PARAMETER_ERROR_CODE, ERROR_DIRECTORY_UNDEFINED );

        		return url.getUrl(  );
    		}
    		int nIdDirectory =   itIdDirectory.next(  );
    		
    		Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, directoryPlugin );

    		Record record = new Record(  );
    		record.setDirectory( directory );

    		try
    		{
    			DirectoryUtils.getDirectoryRecordData( request, record, directoryPlugin, _locale );
    		}
    		catch ( DirectoryErrorException error )
    		{    			
    			if ( error.isMandatoryError(  ) )
    			{    				
    				strError = ERROR_DIRECTORY_FIELD;
    			}
    			else
    			{
    				url.addParameter( PARAMETER_ERROR_MESSAGE, error.getTitleField(  ) );    	
    				strError = ERROR_DIRECTORY_MESSAGE;
    			}
    			
    			url.addParameter( PARAMETER_ERROR_FIELD, error.getTitleField(  ) );
    			url.addParameter( PARAMETER_ERROR_CODE, strError );

        		return url.getUrl(  );
    		}

    		record.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
    		//Autopublication
    		record.setEnabled( true );
    		int nIdRecord = RecordHome.create( record, directoryPlugin );

    		if ( WorkflowService.getInstance(  ).isAvailable(  ) &&
    				( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
    		{
    			WorkflowService.getInstance(  )
    			.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
    					directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), null );
    			WorkflowService.getInstance(  )
    			.executeActionAutomatic( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
    					directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ) );
    		}
    		
    		directoryUser.setLogin( strLogin );    		
    		directoryUser.setIdRecord( nIdRecord );
    		boolean bNeedActivation = Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_NEED_ACTIVATION, "false") );
    		if( !bNeedActivation )
    		{
    			directoryUser.setActivated( true );
    		}    		
    		MyluteceDirectoryUserHome.create( directoryUser, strPassword, _plugin );
    	}

    	url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl(  ) );

    	return url.getUrl(  );
    }

    /**
     * Build the default Lost password page
     * @param page The XPage object to fill
     * @param request The HTTP request
     * @return The XPage object containing the page content
     */
    private XPage getLostPasswordPage( XPage page, HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
        String strStateSending = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );
        String strEmail = request.getParameter( PARAMETER_EMAIL );

        model.put( MARK_PLUGIN_NAME, _plugin.getName(  ) );
        model.put( MARK_ERROR_CODE, strErrorCode );
        model.put( MARK_ACTION_SUCCESSFUL, strStateSending );
        model.put( MARK_EMAIL, strEmail );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_PASSWORD_PAGE, _locale, model );
        page.setContent( t.getHtml(  ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_TITLE, _locale ) );

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
    	Plugin directoryPlugin = PluginService.getPlugin(DirectoryPlugin.PLUGIN_NAME);
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        String strErrorCode = request.getParameter( PARAMETER_ERROR_CODE );
        String strSuccess = request.getParameter( PARAMETER_ACTION_SUCCESSFUL );
        String strErrorField = request.getParameter( PARAMETER_ERROR_FIELD );
    	String strErrorMessage = request.getParameter( PARAMETER_ERROR_MESSAGE );
    	
        MyluteceDirectoryUser user = getRemoteUser( request );
        if ( user == null )
        {
            return null;
        }
        int nIdRecord = user.getIdRecord(  );
        
        int nIdDirectory = RecordHome.getDirectoryIdByRecordId(nIdRecord, directoryPlugin);
        EntryFilter filter = new EntryFilter(  );
    	filter.setIdDirectory(nIdDirectory);
    	
        List<IEntry> listEntry = DirectoryUtils.getFormEntriesByFilter( filter,directoryPlugin );
    	model.put( MARK_ENTRY_LIST, listEntry );
    	model.put( MARK_LOCALE, _locale );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
                DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdRecord, directoryPlugin ) );
        model.put( MARK_PLUGIN_NAME, _plugin.getName(  ) );
        model.put( MARK_ERROR_CODE, strErrorCode );
        model.put( MARK_ERROR_MESSAGE, strErrorMessage );    	
    	model.put( MARK_ERROR_FIELD, strErrorField );    	
        model.put( MARK_ACTION_SUCCESSFUL, strSuccess );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CHANGE_PASSWORD_PAGE, _locale, model );
        page.setContent( t.getHtml(  ) );
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
        Plugin directoryPlugin = PluginService.getPlugin(DirectoryPlugin.PLUGIN_NAME);
        init( request, plugin );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + getModifyAccountUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName(  ) );

        String strError = null;
        MyluteceDirectoryUser user = getRemoteUser( request );
        String strOldPassword = request.getParameter( PARAMETER_OLD_PASSWORD );
        String strNewPassword = request.getParameter( PARAMETER_NEW_PASSWORD );
        String strConfirmationPassword = request.getParameter( PARAMETER_CONFIRMATION_PASSWORD );

        if ( ( user == null ) )
        {
            try
            {
                SiteMessageService.setMessage( request, PROPERTY_ACCESS_DENIED_ERROR_MESSAGE, null,
                    PROPERTY_ACCESS_DENIED_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
            }
            catch ( SiteMessageException e )
            {
                return AppPathService.getBaseUrl( request );
            }
        }

        if ( ( strNewPassword != null ) &&  ( ! strNewPassword.equals( "" ) ) 
        	 && ( ( strOldPassword == null )  || ( strConfirmationPassword == null ) 
        			||  strOldPassword.equals( "" )  || strConfirmationPassword.equals( "" ) ) )
        {
            strError = ERROR_MANDATORY_FIELDS;
        }
        else if ( ( strNewPassword != null ) &&  ( ! strNewPassword.equals( "" ) ) )
        {
        	bChangePassword = true;
        }

        if ( bChangePassword && ( strError == null ) &&
                !MyluteceDirectoryUserHome.checkPassword( user.getLogin(  ), strOldPassword, _plugin ) )
        {
            strError = ERROR_OLD_PASSWORD;
        }

        if (  bChangePassword &&  ( strError == null ) && !checkPassword( strNewPassword, strConfirmationPassword ) )
        {
            strError = ERROR_CONFIRMATION_PASSWORD;
        }

        if (  bChangePassword &&  ( strError == null ) && strNewPassword.equals( strOldPassword ) )
        {
            strError = ERROR_SAME_PASSWORD;
        }

        if ( strError != null )
        {
            url.addParameter( PARAMETER_ERROR_CODE, strError );
        }
        else
        {
        	if(  bChangePassword  )
        	{
        		MyluteceDirectoryUserHome.updatePassword( user, strNewPassword, _plugin );                 
        	}

        	int nIdRecord = user.getIdRecord(  );
        	Record record = RecordHome.findByPrimaryKey(nIdRecord, directoryPlugin);
        	try
        	{
        		DirectoryUtils.getDirectoryRecordData( request, record, directoryPlugin, _locale );
        	}
        	catch ( DirectoryErrorException error )
        	{
        		if ( error.isMandatoryError(  ) )
    			{    				
    				strError = ERROR_DIRECTORY_FIELD;
    			}
    			else
    			{
    				url.addParameter( PARAMETER_ERROR_MESSAGE, error.getTitleField(  ) );    	
    				strError = ERROR_DIRECTORY_MESSAGE;
    			}
    			
    			url.addParameter( PARAMETER_ERROR_FIELD, error.getTitleField(  ) );
    			url.addParameter( PARAMETER_ERROR_CODE, strError );

        		return url.getUrl(  );
        	}

        	RecordHome.updateWidthRecordField( record, directoryPlugin );
        	url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl(  ) );
        }

        return url.getUrl(  );
    }

    /**
     * Check the password with the password confirmation string
     * Check if password is empty
     *
     * @param strPassword The password
     * @param strConfirmation The password confirmation
     * @return true if password is equal to confirmation password and not empty
     */
    private boolean checkPassword( String strPassword, String strConfirmation )
    {
        Boolean bReturn = true;

        if ( ( strPassword == null ) || ( strConfirmation == null ) || strPassword.equals( "" ) ||
                !strPassword.equals( strConfirmation ) )
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
        String strPortalUrl = AppPropertiesService.getProperty( PROPERTY_PORTAL_URL );
        Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );
        init( request, plugin );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        String strError = null;
        UrlItem url = null;
        Collection<MyluteceDirectoryUser> listUser = null;

        String strLogin = request.getParameter( PARAMETER_LOGIN );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        url = new UrlItem( AppPathService.getBaseUrl( request ) + getLostPasswordUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName(  ) );
        url.addParameter( PARAMETER_EMAIL, strEmail );

        // Check mandatory fields
        if ( ( strEmail == null ) || ( strEmail.equals( "" ) ) )
        {
            strError = ERROR_MANDATORY_FIELDS;
        }
        
        if ( ( strLogin == null ) || ( strLogin.equals( "" ) ) )
        {
            strError = ERROR_MANDATORY_FIELDS;
        }

        // Check email format
        if ( ( strError == null ) && !StringUtil.checkEmail( strEmail ) )
        {
            strError = ERROR_SYNTAX_EMAIL;
        }

        listUser = MyluteceDirectoryUserHome.findDirectoryUsersListForEmail( strEmail, _plugin, _locale );

        if ( ( strError == null ) && ( ( listUser == null ) || ( listUser.size(  ) == 0 ) ) )
        {
            strError = ERROR_UNKNOWN_EMAIL;
        }

        MyluteceDirectoryUser validUser = null;
        if ( strError == null )
        {
            for ( MyluteceDirectoryUser user : listUser )
            {
            	if(user.getLogin(  ).equals(strLogin) && user.isActivated(  ))
            	{
            		 validUser = user;
            		 String strHost = AppPropertiesService.getProperty( PROPERTY_MAIL_HOST );
                     String strName = AppPropertiesService.getProperty( PROPERTY_PORTAL_NAME );
                     String strSender = AppPropertiesService.getProperty( PROPERTY_NOREPLY_EMAIL );
                     String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_OBJECT, _locale );

                     model.put( MARK_USER, user );
                     model.put( MARK_PORTAL_URL,
                         ( ( strPortalUrl == null ) || strPortalUrl.equals( "" ) ) ? AppPathService.getBaseUrl( request )
                                                                                   : strPortalUrl );
                     model.put( MARK_PORTAL_NAME, strName );
                     model.put( MARK_PASSWORD,
                         MyluteceDirectoryUserHome.findPasswordByPrimaryKey( user.getIdRecord(  ), _plugin ) );

                     HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_BODY, _locale, model );

                     if ( ( strError == null ) &&
                             ( strHost.equals( "" ) || strName.equals( "" ) || strSender.equals( "" ) ||
                             strObject.equals( "" ) ) )
                     {
                         strError = ERROR_SENDING_EMAIL;
                     }
                     else
                     {
                         MailService.sendMailHtml( strEmail, strName, strSender, strObject, template.getHtml(  ) );
                     }
            	}            	
               
            }
            if( validUser == null )
            {
            	  url.addParameter( PARAMETER_ERROR_CODE, ERROR_UNKNOWN_EMAIL );

                  return url.getUrl(  );
            }
        }

        else
        {
            url.addParameter( PARAMETER_ERROR_CODE, strError );

            return url.getUrl(  );
        }

        url.addParameter( PARAMETER_ACTION_SUCCESSFUL, getDefaultRedirectUrl(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the template for access denied
     * @return The template path
     */
    public static String getAccessDeniedTemplate(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED );
    }

    /**
     * Returns the template for access controled
     * @return The template path
     */
    public static String getAccessControledTemplate(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED );
    }

    /**
     * Get the remote user
     *
     * @param request The HTTP request
     * @return The Directory User
     */
    private MyluteceDirectoryUser getRemoteUser( HttpServletRequest request )
    {
        LuteceUser luteceUser = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( luteceUser == null )
        {
            return null;
        }

        Collection<MyluteceDirectoryUser> listUsers = MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( luteceUser.getName(  ),
                _plugin );

        if ( listUsers.size(  ) != 1 )
        {
            return null;
        }

        MyluteceDirectoryUser user = (MyluteceDirectoryUser) listUsers.iterator(  ).next(  );

        return user;
    }
}
