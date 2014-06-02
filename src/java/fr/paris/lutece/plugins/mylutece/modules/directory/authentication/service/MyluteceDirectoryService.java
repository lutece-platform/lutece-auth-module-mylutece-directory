/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeField;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeFieldHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldFilter;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.BaseUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.parameter.MyluteceDirectoryParameterHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.IMyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.MyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.FormErrors;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.plugins.mylutece.util.SecurityUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.password.PasswordUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * 
 * MyluteceDirectoryService
 * 
 */
public class MyluteceDirectoryService implements IMyluteceDirectoryService
{
	public static final String BEAN_SERVICE = "mylutece-directory.myluteceDirectoryService";

	// CONSTANTS
	private static final String COMMA = ",";
	private static final String PLUGIN_JCAPTCHA = "jcaptcha";

	// PROPERTIES
	private static final String PROPERTY_ENCRYPTION_ALGORITHMS_LIST = "encryption.algorithmsList";

	// PARAMETERS
	private static final String PARAMETER_SEARCH_IS_SEARCH = "search_is_search";
	private static final String PARAMETER_ENABLE_PASSWORD_ENCRYPTION = "enable_password_encryption";
	private static final String PARAMETER_ACCOUNT_REACTIVATED_MAIL_SENDER = "account_reactivated_mail_sender";
	private static final String PARAMETER_ACCOUNT_REACTIVATED_MAIL_SUBJECT = "account_reactivated_mail_subject";
	private static final String PARAMETER_ACCOUNT_REACTIVATED_MAIL_BODY = "mylutece_directory_account_reactivated_mail";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED = "mylutece_directory_mailPasswordEncryptionChanged";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SENDER = "mail_password_encryption_changed_sender";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SUBJECT = "mail_password_encryption_changed_subject";
    
   
    
	// MARKS
	private static final String MARK_SEARCH_IS_SEARCH = "search_is_search";
	private static final String MARK_SEARCH_MYLUTECE_USER_FIELD_FILTER = "search_mylutece_user_field_filter";
	private static final String MARK_ATTRIBUTES_LIST = "attributes_list";
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_SORT_SEARCH_ATTRIBUTE = "sort_search_attribute";
	private static final String MARK_ENABLE_PASSWORD_ENCRYPTION = "enable_password_encryption";
	private static final String MARK_ENABLE_CAPTCHA_AUTHENTICATION = "enable_captcha_authentication";
    private static final String MARK_ENABLE_SEND_MAIL_USER_ENABLED = "enable_send_mail_user_enabled";
    private static final String MARK_ENABLE_SEND_MAIL_USER_DISABLED = "enable_send_mail_user_disabled";
    
    private static final String MARK_MAIL_ENABLED_USER = "mylutece_directory_account_enabled_mail";
    private static final String MARK_MAIL_ENABLED_USER_SENDER = "mail_enabled_user_sender";
    private static final String MARK_MAIL_ENABLED_USER_SUBJECT = "mail_enabled_user_subject";
    private static final String MARK_MAIL_DISABLED_USER = "mylutece_directory_account_disabled_mail";
    private static final String MARK_MAIL_DISABLED_USER_SENDER = "mail_disabled_user_sender";
    private static final String MARK_MAIL_DISABLED_USER_SUBJECT = "mail_disabled_user_subject";
	
	
	private static final String MARK_ENCRYPTION_ALGORITHM = "encryption_algorithm";
	private static final String MARK_ENCRYPTION_ALGORITHMS_LIST = "encryption_algorithms_list";
	private static final String MARK_LOGIN_URL = "login_url";
	private static final String MARK_LOGIN = "login";
	private static final String MARK_NEW_PASSWORD = "new_password";
	private static final String MARK_SITE_LINK = "site_link";
	private static final String MARK_BANNED_DOMAIN_NAMES = "banned_domain_names";
	private static final String MARK_IS_PLUGIN_JCAPTCHA_ENABLE = "is_plugin_jcatpcha_enable";

	// ERRORS
	private static final String ERROR_DIRECTORY_FIELD = "error_directory_field";

	// PROPERTIES
	private static final String PROPERTY_ERROR_FIELD = "module.mylutece.directory.message.error.field";
	private static final String PROPERTY_ERROR_MANDATORY_FIELDS = "module.mylutece.directory.message.account.errorMandatoryFields";

	@Inject
	private IMyluteceDirectoryParameterService _parameterService;

	// GET

	/**
	 * Get the plugin
	 * @return The plugin
	 */
	public Plugin getPlugin( )
	{
		return PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BaseUser getUserByLogin( String strUserName, LuteceAuthentication authenticationService, boolean bGetAdditionnalInfo )
	{
		Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
		BaseUser user = MyluteceDirectoryHome.findLuteceUserByLogin( strUserName, plugin, authenticationService );

		if ( ( user != null ) && bGetAdditionnalInfo )
		{
			// Get roles
			List<String> arrayRoles = MyluteceDirectoryHome.findUserRolesFromLogin( strUserName, plugin );

			if ( ( arrayRoles != null ) && !arrayRoles.isEmpty( ) )
			{
				user.setRoles( arrayRoles );
			}

			// Get groups
			List<String> arrayGroups = MyluteceDirectoryHome.findUserGroupsFromLogin( strUserName, plugin );

			if ( ( arrayGroups != null ) && !arrayGroups.isEmpty( ) )
			{
				user.setGroups( arrayGroups );
			}
		}

		return user;
	}

	/**
	 * Do modify the password
	 * @param user the user
	 * @param bNewValue the new password
	 * @param plugin the plugin
	 */
	public void doModifyResetPassword( MyluteceDirectoryUser user, boolean bNewValue, Plugin plugin )
	{
		MyluteceDirectoryUser userStored = MyluteceDirectoryUserHome.findByPrimaryKey( user.getIdRecord( ), plugin );

		if ( userStored != null )
		{
			MyluteceDirectoryUserHome.updateResetPassword( userStored, bNewValue, plugin );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LuteceUser> getUsers( LuteceAuthentication authenticationService )
	{
		Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
		Collection<BaseUser> baseUsers = MyluteceDirectoryHome.findDirectoryUsersList( plugin, authenticationService );
		Collection<LuteceUser> luteceUsers = new ArrayList<LuteceUser>( );

		for ( BaseUser user : baseUsers )
		{
			luteceUsers.add( user );
		}

		return luteceUsers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Integer> getMappedDirectories( Plugin plugin )
	{
		return MyluteceDirectoryHome.findMappedDirectories( plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Directory getDirectory( int nIdDirectory )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

		return DirectoryHome.findByPrimaryKey( nIdDirectory, directoryPlugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Directory> getListDirectories( DirectoryFilter filter, AdminUser adminUser )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, directoryPlugin );

		return ( List<Directory> ) AdminWorkgroupService.getAuthorizedCollection( listDirectory, adminUser );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IEntry> getListEntries( EntryFilter entryFilter )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

		return EntryHome.getEntryList( entryFilter, directoryPlugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEntry getEntry( int nIdEntry )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

		return EntryHome.findByPrimaryKey( nIdEntry, directoryPlugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record getRecord( int nIdRecord, boolean bGetRecordFields )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
		Record record = recordService.findByPrimaryKey( nIdRecord, directoryPlugin );

		if ( ( record != null ) && bGetRecordFields )
		{
			RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
			recordFieldFilter.setIsEntryShownInResultList( RecordFieldFilter.FILTER_TRUE );
			recordFieldFilter.setIdRecord( nIdRecord );
			record.setListRecordField( RecordFieldHome.getRecordFieldList( recordFieldFilter, directoryPlugin ) );
		}

		return record;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MyluteceDirectoryUser getMyluteceDirectoryUser( int nIdRecord, Plugin plugin )
	{
		return MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsersForLogin( String strLogin, Plugin plugin )
	{
		return MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( strLogin, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsers( Plugin plugin )
	{
		return MyluteceDirectoryUserHome.findDirectoryUsersList( plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsersForEmail( String strEmail, Plugin plugin, Locale locale )
	{
		return MyluteceDirectoryUserHome.findDirectoryUsersListForEmail( strEmail, plugin, locale );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getListEmails( MyluteceDirectoryUser user, Plugin plugin, Locale locale )
	{
		return MyluteceDirectoryUserHome.getEmails( user.getIdRecord( ), plugin, locale );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getUserRolesFromLogin( String strLogin, Plugin plugin )
	{
		return MyluteceDirectoryHome.findUserRolesFromLogin( strLogin, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPasswordByIdRecord( int nIdRecord, Plugin plugin )
	{
		// FIXME : There should not be any method in the service or business package to get the password for security issue
		return MyluteceDirectoryUserHome.findPasswordByPrimaryKey( nIdRecord, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdDirectoryByIdRecord( int nIdRecord )
	{
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

		return recordService.getDirectoryIdByRecordId( nIdRecord, directoryPlugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getFilteredUsersInterface( List<Integer> listUserIds, HttpServletRequest request, Map<String, Object> model, UrlItem url )
	{
		String strIsSearch = request.getParameter( PARAMETER_SEARCH_IS_SEARCH );
		boolean bIsSearch = ( strIsSearch != null ) ? true : false;

		Plugin myLutecePlugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
		List<Integer> listFilteredUserIds = new ArrayList<Integer>( );

		MyLuteceUserFieldFilter mlFieldFilter = new MyLuteceUserFieldFilter( );
		mlFieldFilter.setMyLuteceUserFieldFilter( request, request.getLocale( ) );

		List<Integer> listFilteredUserIdsByUserFields = MyLuteceUserFieldHome.findUsersByFilter( mlFieldFilter, myLutecePlugin );

		if ( listFilteredUserIdsByUserFields != null )
		{
			for ( int nFilteredUserId : listUserIds )
			{
				for ( int nFilteredUserIdByUserField : listFilteredUserIdsByUserFields )
				{
					if ( nFilteredUserId == nFilteredUserIdByUserField )
					{
						listFilteredUserIds.add( nFilteredUserId );
					}
				}
			}
		}
		else
		{
			listFilteredUserIds = listUserIds;
		}

		List<IAttribute> listAttributes = AttributeHome.findAll( request.getLocale( ), myLutecePlugin );

		for ( IAttribute attribute : listAttributes )
		{
			List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( attribute.getIdAttribute( ), myLutecePlugin );
			attribute.setListAttributeFields( listAttributeFields );
		}

		String strSortSearchAttribute = StringUtils.EMPTY;

		if ( bIsSearch )
		{
			mlFieldFilter.setUrlAttributes( url );
			strSortSearchAttribute = mlFieldFilter.getUrlAttributes( );
		}

		model.put( MARK_SEARCH_IS_SEARCH, bIsSearch );
		model.put( MARK_SEARCH_MYLUTECE_USER_FIELD_FILTER, mlFieldFilter );
		model.put( MARK_LOCALE, request.getLocale( ) );
		model.put( MARK_ATTRIBUTES_LIST, listAttributes );
		model.put( MARK_SORT_SEARCH_ATTRIBUTE, strSortSearchAttribute );

		return listFilteredUserIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, List<RecordField>> getMapIdEntryListRecordField( Record record )
	{
		Map<String, List<RecordField>> map = new HashMap<String, List<RecordField>>( );
		List<RecordField> listRecordFields = record.getListRecordField( );

		if ( ( listRecordFields != null ) && !listRecordFields.isEmpty( ) )
		{
			for ( RecordField recordField : listRecordFields )
			{
				String strIdEntry = Integer.toString( recordField.getEntry( ).getIdEntry( ) );
				List<RecordField> listAssociatedRecordFields = map.get( strIdEntry );

				if ( listAssociatedRecordFields == null )
				{
					listAssociatedRecordFields = new ArrayList<RecordField>( );
				}

				listAssociatedRecordFields.add( recordField );
				map.put( strIdEntry, listAssociatedRecordFields );
			}
		}

		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getDirectoryRecordData( HttpServletRequest request, Record record, Plugin pluginDirectory, Locale locale, FormErrors formErrors )
	{
		List<RecordField> listRecordFieldResult = new ArrayList<RecordField>( );
		EntryFilter filter = new EntryFilter( );
		filter.setIdDirectory( record.getDirectory( ).getIdDirectory( ) );
		filter.setIsComment( EntryFilter.FILTER_FALSE );
		filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

		List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, pluginDirectory );

		for ( IEntry entry : listEntryFirstLevel )
		{
			entry = EntryHome.findByPrimaryKey( entry.getIdEntry( ), pluginDirectory );

			if ( entry.getEntryType( ).getGroup( ) )
			{
				for ( IEntry entryChild : entry.getChildren( ) )
				{
					getDirectoryRecordFieldData( record, request, entryChild.getIdEntry( ), listRecordFieldResult, pluginDirectory, locale, formErrors );
				}
			}
			else if ( !entry.getEntryType( ).getComment( ) )
			{
				getDirectoryRecordFieldData( record, request, entry.getIdEntry( ), listRecordFieldResult, pluginDirectory, locale, formErrors );
			}
		}

		record.setListRecordField( listRecordFieldResult );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getManageAdvancedParameters( AdminUser user )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );

		// Encryption Password
		if ( RBACService.isAuthorized( MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, user ) )
		{
			String[] listAlgorithms = AppPropertiesService.getProperty( PROPERTY_ENCRYPTION_ALGORITHMS_LIST ).split( COMMA );

			MyluteceDirectoryParameterService parameterService = new MyluteceDirectoryParameterService( );

			
			model.put( MARK_ENABLE_PASSWORD_ENCRYPTION, MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION, plugin ).getName( ) );
			model.put( MARK_ENABLE_CAPTCHA_AUTHENTICATION, MyluteceDirectoryParameterHome.findByKey( MARK_ENABLE_CAPTCHA_AUTHENTICATION, plugin ).getName( ) );
			model.put( MARK_ENABLE_SEND_MAIL_USER_ENABLED, MyluteceDirectoryParameterHome.findByKey( MARK_ENABLE_SEND_MAIL_USER_ENABLED, plugin ).getName( ) );
			model.put( MARK_ENABLE_SEND_MAIL_USER_DISABLED, MyluteceDirectoryParameterHome.findByKey( MARK_ENABLE_SEND_MAIL_USER_DISABLED, plugin ).getName( ) );
			model.put( MARK_ENCRYPTION_ALGORITHM, MyluteceDirectoryParameterHome.findByKey( MARK_ENCRYPTION_ALGORITHM, plugin ).getName( ) );
			model.put( MARK_ENCRYPTION_ALGORITHMS_LIST, listAlgorithms );
			// we save the banned domain name list with the Directory plugin so that it can get it directly.
			model.put( MARK_BANNED_DOMAIN_NAMES, SecurityUtils.getLargeSecurityParameter( parameterService, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ), MARK_BANNED_DOMAIN_NAMES ) );

			model.put( MARK_IS_PLUGIN_JCAPTCHA_ENABLE, PluginService.isPluginEnable( PLUGIN_JCAPTCHA ) );
			model = SecurityUtils.checkSecurityParameters( parameterService, model, plugin );
		}

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doCreateMyluteceDirectoryUser( MyluteceDirectoryUser myluteceDirectoryUser, String strUserPassword, Plugin plugin )
	{
		String strPassword = SecurityUtils.buildPassword( _parameterService, plugin, strUserPassword );
		myluteceDirectoryUser.setPasswordMaxValidDate( SecurityUtils.getPasswordMaxValidDate( _parameterService, plugin ) );
		myluteceDirectoryUser.setAccountMaxValidDate( SecurityUtils.getAccountMaxValidDate( _parameterService, plugin ) );
		MyluteceDirectoryUserHome.create( myluteceDirectoryUser, strPassword, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doModifyMyluteceDirectoryUser( MyluteceDirectoryUser myluteceDirectoryUser, Plugin plugin )
	{
		MyluteceDirectoryUserHome.update( myluteceDirectoryUser, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doModifyPassword( MyluteceDirectoryUser myluteceDirectoryUser, String strUserPassword, Plugin plugin )
	{
		String strPassword = SecurityUtils.buildPassword( _parameterService, plugin, strUserPassword );
		myluteceDirectoryUser.setPasswordMaxValidDate( SecurityUtils.getPasswordMaxValidDate( _parameterService, plugin ) );
		MyluteceDirectoryUserHome.updatePassword( myluteceDirectoryUser, strPassword, plugin );
	}
	
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void  doDisableUser(MyluteceDirectoryUser myluteceDirectoryUser, Plugin plugin, Locale locale)
	{
		
		if ( myluteceDirectoryUser != null )
        {
        		myluteceDirectoryUser.setStatus( MyluteceDirectoryUser.STATUS_NOT_ACTIVATED);
        	    this.doModifyMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ) );
        	    if(_parameterService.isEnableSendMailUserDisabled(getPlugin(  )))
        		{
        			notifyUserChangeState(myluteceDirectoryUser, plugin, locale, MARK_MAIL_DISABLED_USER, MARK_MAIL_DISABLED_USER_SENDER, MARK_MAIL_DISABLED_USER_SUBJECT);
        		}
        }
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void  doEnableUser(MyluteceDirectoryUser myluteceDirectoryUser, Plugin plugin, Locale locale)
	{
		

        if ( myluteceDirectoryUser != null )
        {
        		myluteceDirectoryUser.setStatus( MyluteceDirectoryUser.STATUS_ACTIVATED);
        		this.doModifyMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ) );
        		if(_parameterService.isEnableSendMailUserEnabled(getPlugin(  )))
        		{
        			notifyUserChangeState(myluteceDirectoryUser, plugin, locale, MARK_MAIL_ENABLED_USER, MARK_MAIL_ENABLED_USER_SENDER, MARK_MAIL_ENABLED_USER_SUBJECT);
        		}
        }
		
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doAssignRoleUser( MyluteceDirectoryUser user, String[] roleArray, Plugin plugin)
	{
		if ( user != null )
		{
			MyluteceDirectoryHome.removeRolesForUser( user.getIdRecord( ), plugin );

			if ( ( roleArray != null ) && ( roleArray.length > 0 ) )
			{
				for ( int i = 0; i < roleArray.length; i++ )
				{
					MyluteceDirectoryHome.addRoleForUser( user.getIdRecord( ), roleArray[i], plugin );
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doRemoveMyluteceDirectoryUser( MyluteceDirectoryUser directoryUser, Plugin plugin, boolean bRemoveAdditionnalInfo )
	{
		MyluteceDirectoryUserHome.remove( directoryUser, plugin );

		if ( bRemoveAdditionnalInfo )
		{
			MyluteceDirectoryHome.removeRolesForUser( directoryUser.getIdRecord( ), plugin );
			MyluteceDirectoryHome.removeGroupsForUser( directoryUser.getIdRecord( ), plugin );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doUnassignDirectory( int nIdDirectory, Plugin plugin )
	{
		MyluteceDirectoryHome.unAssignDirectory( nIdDirectory, plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doUnassignDirectories( Plugin plugin )
	{
		MyluteceDirectoryHome.unAssignDirectories( plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional( "mylutece-directory.transactionManager" )
	public void doAssignDirectory( int nIdDirectory, Plugin plugin )
	{
		MyluteceDirectoryHome.assignDirectory( nIdDirectory, plugin );
	}

	// CHECKS

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirectoryMapped( int nIdDirectory, Plugin plugin )
	{
		return MyluteceDirectoryHome.isMapped( nIdDirectory, plugin );
	}

	// PRIVATE METHODS

	/**
	 * Get the directory record field data
	 * @param record the record
	 * @param request the request
	 * @param nIdEntry the id entry
	 * @param listRecordFieldResult the list of record field
	 * @param pluginDirectory the plugin
	 * @param locale the locale
	 * @param formErrors the form errors
	 */
	private void getDirectoryRecordFieldData( Record record, HttpServletRequest request, int nIdEntry, List<RecordField> listRecordFieldResult, Plugin pluginDirectory, Locale locale,
			FormErrors formErrors )
	{
		try
		{
			DirectoryUtils.getDirectoryRecordFieldData( record, request, nIdEntry, true, listRecordFieldResult, pluginDirectory, locale );
		}
		catch ( DirectoryErrorException e )
		{
			if ( e.isMandatoryError( ) )
			{
				Object[] params =
				{ e.getTitleField( ) };
				String strErrorMessage = I18nService.getLocalizedString( PROPERTY_ERROR_MANDATORY_FIELDS, params, locale );
				formErrors.addError( ERROR_DIRECTORY_FIELD, strErrorMessage );
			}
			else
			{
				Object[] params =
				{ e.getTitleField( ), e.getErrorMessage( ) };
				String strErrorMessage = I18nService.getLocalizedString( PROPERTY_ERROR_FIELD, params, locale );
				formErrors.addError( ERROR_DIRECTORY_FIELD, strErrorMessage );
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeUserPasswordAndNotify( String strBaseURL, Plugin plugin, Locale locale )
	{
		// Alert all users their password have been reinitialized.
		Collection<MyluteceDirectoryUser> listUsers = getMyluteceDirectoryUsers( plugin );

		for ( MyluteceDirectoryUser user : listUsers )
		{
			// make password
			String strPassword = PasswordUtil.makePassword( );
			String strPasswordCpy = strPassword;
			MyluteceDirectoryUser userStored = getMyluteceDirectoryUser( user.getIdRecord( ), plugin );
			userStored.setPasswordMaxValidDate( SecurityUtils.getPasswordMaxValidDate( _parameterService, plugin ) );
			strPassword = SecurityUtils.buildPassword( _parameterService, plugin, strPassword );
			MyluteceDirectoryUserHome.updatePassword( userStored, strPassword, plugin );

			List<String> listEmails = getListEmails( userStored, plugin, locale );

			if ( ( listEmails != null ) && !listEmails.isEmpty( ) )
			{
				// send password by e-mail
                ReferenceItem referenceItem = _parameterService.findByKey(
                        PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SENDER, plugin );
                String strSenderEmail = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );
                referenceItem = _parameterService
                        .findByKey( PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SUBJECT, plugin );
                String strEmailSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

				Map<String, Object> model = new HashMap<String, Object>( );
				model.put( MARK_NEW_PASSWORD, strPasswordCpy );
				model.put( MARK_LOGIN_URL, strBaseURL + AdminAuthenticationService.getInstance( ).getLoginPageUrl( ) );
				model.put( MARK_SITE_LINK, MailService.getSiteLink( strBaseURL, true ) );

                String strTemplate = DatabaseTemplateService
                        .getTemplateFromKey( PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED );

                HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strTemplate, locale, model );

				for ( String email : listEmails )
				{
					MailService.sendMailHtml( email, strSenderEmail, MailService.getNoReplyEmail(), strEmailSubject, template.getHtml( ) );
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean mustUserChangePassword( LuteceUser user, Plugin plugin )
	{
		return MyluteceDirectoryHome.findResetPasswordFromLogin( user.getName( ), plugin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doInsertNewPasswordInHistory( String strPassword, int nUserId, Plugin plugin )
	{
		strPassword = SecurityUtils.buildPassword( _parameterService, plugin, strPassword );
		MyluteceDirectoryUserHome.insertNewPasswordInHistory( strPassword, nUserId, getPlugin( ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( "deprecation" )
	@Override
	public void updateUserExpirationDate( int nIdUser, Plugin plugin )
	{
		// We update the user account
		int nbMailSend = MyluteceDirectoryUserHome.getNbAccountLifeTimeNotification( nIdUser, plugin );
		Timestamp newExpirationDate = SecurityUtils.getAccountMaxValidDate( _parameterService, plugin );
		MyluteceDirectoryUserHome.updateUserExpirationDate( nIdUser, newExpirationDate, getPlugin( ) );

		// We notify the user
		MyluteceDirectoryAccountLifeTimeService accountLifeTimeService = new MyluteceDirectoryAccountLifeTimeService( );

		String strUserMail = accountLifeTimeService.getUserMainEmail( nIdUser );

		if ( nbMailSend > 0 && StringUtils.isNotBlank( strUserMail ) )
		{
			String strBody = DatabaseTemplateService.getTemplateFromKey( PARAMETER_ACCOUNT_REACTIVATED_MAIL_BODY );

			ReferenceItem referenceItem = _parameterService.findByKey( PARAMETER_ACCOUNT_REACTIVATED_MAIL_SENDER, plugin );
			String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

			referenceItem = _parameterService.findByKey( PARAMETER_ACCOUNT_REACTIVATED_MAIL_SUBJECT, plugin );
			String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

			Map<String, String> model = new HashMap<String, String>( );
			accountLifeTimeService.addParametersToModel( model, nIdUser );
			HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strBody, Locale.getDefault( ), model );
			MailService.sendMailHtml( strUserMail, strSender, MailService.getNoReplyEmail(), strSubject, template.getHtml( ) );
		}
	}
	
	
	/**
	 * notifyUserChangeState
	 * @param myluteceDirectoryUser mylutece user
	 * @param plugin the plugin	
	 * @param locale the locale
	 * @param strMailTemplateKey the mail template key
	 * @param strSenderNameKey	the sender name key
	 * @param strSubjectKey	the subject key
	 */
	private void notifyUserChangeState(MyluteceDirectoryUser myluteceDirectoryUser, Plugin plugin, Locale locale,String strMailTemplateKey,String strSenderNameKey,String strSubjectKey){
		
		
		List<String> listEmails = getListEmails( myluteceDirectoryUser, plugin, locale );
		if ( ( listEmails != null ) && !listEmails.isEmpty( ) )
		{
			// send password by e-mail
            ReferenceItem referenceItem = _parameterService.findByKey(
            		strSenderNameKey, plugin );
            String strSenderEmail = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );
            referenceItem = _parameterService
                    .findByKey( strSubjectKey, plugin );
            String strEmailSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

			Map<String, Object> model = new HashMap<String, Object>( );
			
			
			model.put(MARK_LOGIN, myluteceDirectoryUser.getLogin());

			String strTemplate = DatabaseTemplateService
                     .getTemplateFromKey( strMailTemplateKey );

             HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strTemplate, locale, model );

         
			for ( String email : listEmails )
			{
				MailService.sendMailHtml( email, strSenderEmail, MailService.getNoReplyEmail(), strEmailSubject, template.getHtml( ) );
			}
		}	
		
		
	}
	

}
