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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.Category;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeField;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeFieldHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserField;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.parameter.MyluteceDirectoryParameterHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryResourceIdService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.plugins.mylutece.service.RoleResourceIdService;
import fr.paris.lutece.plugins.mylutece.service.attribute.MyLuteceUserFieldService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.password.PasswordUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage roles features ( manage, create, modify, remove )
 */
public class MyluteceDirectoryUserJspBean extends PluginAdminPageJspBean
{
	// Right
	public static final String RIGHT_MANAGE_MYLUTECE_DIRECTORY_USERS = "MYLUTECE_DIRECTORY_MANAGEMENT_USERS";

	// Templates
	private static final String TEMPLATE_MANAGE_DIRECTORY_RECORD = "admin/plugins/mylutece/modules/directory/manage_directory_record.html";
	private static final String TEMPLATE_MODIFY_USER = "admin/plugins/mylutece/modules/directory/modify_user.html";
	private static final String TEMPLATE_CREATE_USER = "admin/plugins/mylutece/modules/directory/create_user.html";
	private static final String TEMPLATE_MANAGE_ROLES_USER = "admin/plugins/mylutece/modules/directory/manage_roles_user.html";
	private static final String TEMPLATE_MANAGE_GROUPS_USER = "admin/plugins/mylutece/modules/directory/manage_groups_user.html";
	private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/mylutece/modules/directory/manage_advanced_parameters.html";
    private static final String TEMPLATE_EMAIL_FORGOT_PASSWORD = "admin/plugins/mylutece/modules/directory/email_forgot_password.html";

	// Jsp
	private static final String JSP_URL_PREFIX = "jsp/admin/plugins/mylutece/modules/directory/";
	private static final String JSP_URL_MANAGE_DIRECTORY_RECORD = "ManageRecords.jsp";
	private static final String JSP_URL_REMOVE_USER = "DoRemoveUser.jsp";
	private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "ManageAdvancedParameters.jsp";
	private static final String JSP_URL_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/mylutece/modules/directory/ManageAdvancedParameters.jsp";
    private static final String JSP_URL_MODIFY_PASSWORD_ENCRYPTION = "jsp/admin/plugins/mylutece/modules/directory/DoModifyPasswordEncryption.jsp";

	// properties
	private static final String PROPERTY_ITEM_PER_PAGE = "module.mylutece.directory.items_per_page";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_USER = "module.mylutece.directory.modify_user.page_title";
	private static final String PROPERTY_PAGE_TITLE_CREATE_USER = "module.mylutece.directory.create_user.page_title";
	private static final String PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER = "module.mylutece.directory.manage_roles_user.page_title";
	private static final String PROPERTY_PAGE_TITLE_MANAGE_GROUPS_USER = "module.mylutece.directory.manage_groups_user.page_title";
	private static final String MESSAGE_USER_EXIST = "module.mylutece.directory.message.user_exist";
	private static final String MESSAGE_CONFIRMATION_REMOVE_USER = "module.mylutece.directory.message.confirm_remove_user";
	private static final String MESSAGE_ERROR_MANAGE_ROLES = "module.mylutece.directory.message.manage.roles";
	private static final String MESSAGE_ERROR_MANAGE_GROUPS = "module.mylutece.directory.message.manage.groups";
	private static final String PROPERTY_MESSAGE_CONFIRM_MODIFY_PASSWORD_ENCRYPTION = "module.mylutece.directory.manage_advanced_parameters.message.confirmModifyPasswordEncryption";
    private static final String PROPERTY_MESSAGE_NO_CHANGE_PASSWORD_ENCRYPTION = "module.mylutece.directory.manage_advanced_parameters.message.noChangePasswordEncryption";
    private static final String PROPERTY_MESSAGE_INVALID_ENCRYPTION_ALGORITHM = "module.mylutece.directory.manage_advanced_parameters.message.invalidEncryptionAlgorithm";
    private static final String PROPERTY_NO_REPLY_EMAIL = "mail.noreply.email";
    private static final String PROPERTY_MESSAGE_EMAIL_SUBJECT = "module.mylutece.directory.forgot_password.email.subject";

	// Parameters
	private static final String PARAMETER_ID_DIRECTORY = "id_directory";
	private static final String PARAMETER_PAGE_INDEX = "page_index";
	private static final String PARAMETER_SEARCH = "search";
	private static final String PARAMETER_SESSION = "session";
	private static final String PARAMETER_LOGIN = "login";
	private static final String PARAMETER_PASSWORD = "password";
	private static final String PARAMETER_ACTIVATE = "activate";
	private static final String PARAMETER_ID_RECORD = "id_record";
	private static final String PARAMETER_ID_ROLE = "id_role";
	private static final String PARAMETER_GROUP_KEY = "group_key";
	private static final String PARAMETER_ENABLE_PASSWORD_ENCRYPTION = "enable_password_encryption";
    private static final String PARAMETER_ENCRYPTION_ALGORITHM = "encryption_algorithm";

	// Markers
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_PAGINATOR = "paginator";
	private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
	private static final String MARK_DIRECTORY_RECORD_LIST = "directory_record_list";
	private static final String MARK_DIRECTORY = "directory";
	private static final String MARK_ENTRY_LIST_FORM_MAIN_SEARCH = "entry_list_form_main_search";
	private static final String MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH = "entry_list_form_complementary_search";
	private static final String MARK_ENTRY_LIST_SEARCH_RESULT = "entry_list_search_result";
	private static final String MARK_XSL_EXPORT_LIST = "xsl_export_list";
	private static final String MARK_NUMBER_RECORD = "number_record";
	private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
	private static final String MARK_PLUGIN_NAME = "plugin_name";
	private static final String MARK_ID_RECORD = "id_record";
	private static final String MARK_LOGIN = "login";
	private static final String MARK_USER = "user";
	private static final String MARK_PASSWORD = "password";
	private static final String MARK_NO_DIRECTORY = "no_directory";
	private static final String MARK_RECORD = "record";
	private static final String MARK_MYLUTECE_RECORD = "mylutece_record";
	private static final String MARK_ROLES_LIST = "role_list";
	private static final String MARK_ROLES_LIST_FOR_USER = "user_role_list";
	private static final String MARK_GROUPS_LIST = "group_list";
	private static final String MARK_GROUPS_LIST_FOR_USER = "user_group_list";
	private static final String MARK_ACTIVATE = "activate";
	private static final String MARK_ATTRIBUTES_LIST = "attributes_list";
	private static final String MARK_MAP_LIST_ATTRIBUTE_DEFAULT_VALUES = "map_list_attribute_default_values";
	private static final String MARK_LOGIN_URL = "login_url";
    private static final String MARK_NEW_PASSWORD = "new_password";

	// 
	private static final String REGEX_ID = "^[\\d]+$";
	private static final String EMPTY_STRING = "";
	private static final String CONSTANT_DEFAULT_ALGORITHM = "noValue";
	private static final String QUESTION_MARK = "?";
	private static final String AMPERSAND = "&";
	private static final String EQUAL = "=";

	// Session fields
	private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
	private String _strCurrentPageIndexDirectory;
	private String _strCurrentPageIndexDirectoryRecord;
	private int _nItemsPerPageDirectoryRecord;
	HashMap<String, List<RecordField>> _mapQuery;

	/**
	 * Creates a new DirectoryJspBean object.
	 */
	public MyluteceDirectoryUserJspBean( )
	{
	}

	/**
	 * Return management records
	 * 
	 * @param request The Http request
	 * @return Html directory
	 * @throws DirectoryErrorException
	 */
	public String getManageRecords( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
		int nIdDirectory = 0;
		HashMap<String, Object> model = new HashMap<String, Object>( );
		String strURL = getJspManageDirectoryRecord( request, nIdDirectory );
        UrlItem url = new UrlItem( strURL );
		boolean bNoDirectory = true;

		if ( (strIdDirectory == null) || !strIdDirectory.matches( REGEX_ID ) )
		{
			Collection<Integer> integerList = MyluteceDirectoryHome.findMappedDirectories( getPlugin( ) );

			if ( integerList.size( ) != 0 )
			{
				nIdDirectory = integerList.iterator( ).next( ).intValue( );
				bNoDirectory = false;
			}
		}
		else
		{
			bNoDirectory = false;
			nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
		}

		if ( !bNoDirectory )
		{
			Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
			Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, directoryPlugin );

			if ( directory == null )
			{
				throw new AccessDeniedException( );
			}

			if ( request.getParameter( PARAMETER_SESSION ) == null )
			{
				reInitDirectoryRecordFilter( );
			}

			_strCurrentPageIndexDirectoryRecord = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexDirectoryRecord );
			_nItemsPerPageDirectoryRecord = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageDirectoryRecord,
					_nDefaultItemsPerPage );

			if ( request.getParameter( PARAMETER_SEARCH ) != null )
			{
				// get search filter
				try{
					_mapQuery = DirectoryUtils.getSearchRecordData( request, nIdDirectory, getPlugin( ), getLocale( ) );
				}catch(DirectoryErrorException error)
				{
					
				}
			}

			// build entryFilter
			EntryFilter entryFilter = new EntryFilter( );
			entryFilter.setIdDirectory( directory.getIdDirectory( ) );
			entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
			entryFilter.setIsComment( EntryFilter.FILTER_FALSE );

			List<IEntry> listEntryFormMainSearch = new ArrayList<IEntry>( );
			List<IEntry> listEntryFormComplementarySearch = new ArrayList<IEntry>( );
			List<IEntry> listEntryResultSearch = new ArrayList<IEntry>( );

			for( IEntry entry : EntryHome.getEntryList( entryFilter, directoryPlugin ) )
			{
				if ( entry.isIndexed( ) )
				{
					if ( !entry.isShownInAdvancedSearch( ) )
					{
						listEntryFormMainSearch.add( EntryHome.findByPrimaryKey( entry.getIdEntry( ), directoryPlugin ) );
					}
					else
					{
						listEntryFormComplementarySearch.add( EntryHome.findByPrimaryKey( entry.getIdEntry( ), directoryPlugin ) );
					}
				}

				if ( entry.isShownInResultList( ) )
				{
					listEntryResultSearch.add( entry );
				}
			}

			// call search service
			RecordFieldFilter filter = new RecordFieldFilter( );
			filter.setIdDirectory( directory.getIdDirectory( ) );

			List<Integer> listResultRecordId = DirectorySearchService.getInstance( ).getSearchResults( 
					directory, _mapQuery, null, null, null, filter, directoryPlugin );
			
			listResultRecordId = MyluteceDirectoryService.getInstance(  ).getFilteredUsersInterface( 
					listResultRecordId, request, model, url );
			
			LocalizedPaginator paginator = new LocalizedPaginator( listResultRecordId, _nItemsPerPageDirectoryRecord, url.getUrl(  ),
					PARAMETER_PAGE_INDEX, _strCurrentPageIndexDirectoryRecord, getLocale(  ) );

			Collection<HashMap<String, Object>> listRecords = new ArrayList<HashMap<String, Object>>( );
			Record record;
			RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
			recordFieldFilter.setIsEntryShownInResultList( RecordFieldFilter.FILTER_TRUE );

			for( Object idRecord : paginator.getPageItems( ) )
			{
				HashMap<String, Object> subModel = new HashMap<String, Object>( );

				record = RecordHome.findByPrimaryKey( (Integer) idRecord, directoryPlugin );

				recordFieldFilter.setIdRecord( (Integer) idRecord );
				record.setListRecordField( RecordFieldHome.getRecordFieldList( recordFieldFilter, directoryPlugin ) );
				subModel.put( MARK_RECORD, record );
				subModel.put( MARK_MYLUTECE_RECORD, MyluteceDirectoryUserHome.findByPrimaryKey( record.getIdRecord( ), getPlugin( ) ) );
				listRecords.add( subModel );
			}

			// add xslExport
			DirectoryXslFilter directoryXslFilter = new DirectoryXslFilter( );

			directoryXslFilter.setIdCategory( Category.ID_CATEGORY_EXPORT );

			ReferenceList refListXslExport = DirectoryXslHome.getRefList( directoryXslFilter, directoryPlugin );

			model.put( MARK_PAGINATOR, paginator );
			model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageDirectoryRecord );
			model.put( MARK_ENTRY_LIST_FORM_MAIN_SEARCH, listEntryFormMainSearch );
			model.put( MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH, listEntryFormComplementarySearch );
			model.put( MARK_ENTRY_LIST_SEARCH_RESULT, listEntryResultSearch );
			model.put( MARK_XSL_EXPORT_LIST, refListXslExport );
			model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, _mapQuery );
			model.put( MARK_DIRECTORY, directory );
			model.put( MARK_DIRECTORY_RECORD_LIST, listRecords );
			model.put( MARK_NUMBER_RECORD, paginator.getItemsCount( ) );

			setPageTitleProperty( DirectoryUtils.EMPTY_STRING );
		}
		else
		{
			model.put( MARK_NO_DIRECTORY, true );
		}
		
		model.put( MARK_LOCALE, getLocale( ) );

		HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY_RECORD, getLocale( ), model );

		return getAdminPage( templateList.getHtml( ) );
	}

	/**
	 * Returns the User modification form
	 * 
	 * @param request The Http request
	 * @return Html modification form
	 */
	public String getModifyUser( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
		setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_USER );

		if ( (strIdRecord == null) || !strIdRecord.matches( REGEX_ID ) )
		{
			throw new AccessDeniedException( );
		}

		int nIdDirectoryRecord = Integer.parseInt( strIdRecord );

		HashMap<String, Object> model = new HashMap<String, Object>( );
		MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin( ) );
		
		// Specific attributes
        Plugin myLutecePlugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        List<IAttribute> listAttributes = AttributeHome.findAll( getLocale(  ), myLutecePlugin );
        Map<String, List<MyLuteceUserField>> map = new HashMap<String, List<MyLuteceUserField>>(  );
        for ( IAttribute attribute : listAttributes )
        {
        	List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( 
        			attribute.getIdAttribute(  ), myLutecePlugin );
        	attribute.setListAttributeFields( listAttributeFields );
        	List<MyLuteceUserField> listUserFields = MyLuteceUserFieldHome.selectUserFieldsByIdUserIdAttribute( 
        			user.getIdRecord(  ), attribute.getIdAttribute(  ), myLutecePlugin );
        	if ( listUserFields.size(  ) == 0 )
        	{
        		MyLuteceUserField userField = new MyLuteceUserField(  );
        		userField.setValue( EMPTY_STRING );
        		listUserFields.add( userField );
        	}
        	map.put( String.valueOf( attribute.getIdAttribute(  ) ), listUserFields );
        }
        
		model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
		model.put( MARK_ID_RECORD, nIdDirectoryRecord );
		model.put( MARK_LOGIN, (user != null) ? user.getLogin( ) : null );
		model.put( MARK_PASSWORD, MyluteceDirectoryUserHome.findPasswordByPrimaryKey( nIdDirectoryRecord, getPlugin( ) ) );
		model.put( MARK_ACTIVATE, (user != null) ? user.isActivated(  ) : null );
		model.put( MARK_ATTRIBUTES_LIST, listAttributes );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_MAP_LIST_ATTRIBUTE_DEFAULT_VALUES, map );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_USER, getLocale( ), model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Returns the User creation form
	 * 
	 * @param request The Http request
	 * @return Html creation form
	 */
	public String getCreateUser( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
		setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_USER );

		if ( (strIdRecord == null) || !strIdRecord.matches( REGEX_ID ) )
		{
			throw new AccessDeniedException( );
		}

		int nIdDirectoryRecord = Integer.parseInt( strIdRecord );
		
		Plugin myLutecePlugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        // Specific attributes
        List<IAttribute> listAttributes = AttributeHome.findAll( getLocale(  ), myLutecePlugin );
        for ( IAttribute attribute : listAttributes )
        {
        	List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( 
        			attribute.getIdAttribute(  ), myLutecePlugin );
        	attribute.setListAttributeFields( listAttributeFields );
        }

		HashMap<String, Object> model = new HashMap<String, Object>( );
		MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin( ) );
		model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
		model.put( MARK_ID_RECORD, nIdDirectoryRecord );
		model.put( MARK_LOGIN, (user != null) ? user.getLogin( ) : null );
		model.put( MARK_PASSWORD, MyluteceDirectoryUserHome.findPasswordByPrimaryKey( nIdDirectoryRecord, getPlugin( ) ) );
		model.put( MARK_ATTRIBUTES_LIST, listAttributes );
        model.put( MARK_LOCALE, getLocale(  ) );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_USER, getLocale( ), model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Process user's creation
	 * 
	 * @param request The Http request
	 * @return The user's Displaying Url
	 */
	public String doCreateUser( HttpServletRequest request )
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
		String strLogin = request.getParameter( PARAMETER_LOGIN );
		String strPassword = request.getParameter( PARAMETER_PASSWORD );
		String strActivate = request.getParameter( PARAMETER_ACTIVATE );

		if ( ((strLogin == null) || (strLogin.length( ) == 0)) || ((strPassword == null) || (strPassword.length( ) == 0)) || (strIdRecord == null)
				|| !strIdRecord.matches( REGEX_ID ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		int nIdRecord = Integer.parseInt( strIdRecord );

		if ( MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( strLogin, getPlugin( ) ).size( ) != 0 )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
		}

		MyluteceDirectoryUser myluteceDirectoryUser = new MyluteceDirectoryUser( );
		myluteceDirectoryUser.setLogin( strLogin );
		myluteceDirectoryUser.setIdRecord( nIdRecord );
		if( strActivate  != null )
		{
			myluteceDirectoryUser.setActivated( true );
		}
		
		String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );
        if ( strError != null )
        {
        	return strError;
        }
        
        // Check if there is an encryption algorithm
        if ( Boolean.valueOf( 
        		MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION, getPlugin(  ) ).getName(  ) ) )
    	{
        	String strAlgorithm = MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENCRYPTION_ALGORITHM, getPlugin(  ) ).getName(  );
        	strPassword = CryptoService.encrypt( strPassword, strAlgorithm );
    	}
        
		MyluteceDirectoryUserHome.create( myluteceDirectoryUser, strPassword, getPlugin(  ) );
		MyLuteceUserFieldService.doCreateUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ) );
		
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

		Record record = RecordHome.findByPrimaryKey( nIdRecord, directoryPlugin );
		UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );
		url.addParameter( PARAMETER_ID_DIRECTORY, record.getDirectory( ).getIdDirectory( ) );

		return url.getUrl( );
	}

	/**
	 * Process user's modification
	 * 
	 * @param request The Http request
	 * @return The user's Displaying Url
	 */
	public String doModifyUser( HttpServletRequest request )
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
		String strLogin = request.getParameter( PARAMETER_LOGIN );
		String strPassword = request.getParameter( PARAMETER_PASSWORD );
		String strActivate = request.getParameter( PARAMETER_ACTIVATE );

		if ( ((strLogin == null) || (strLogin.length( ) == 0)) || ((strPassword == null) || (strPassword.length( ) == 0)) || (strIdRecord == null)
				|| !strIdRecord.matches( REGEX_ID ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}
		
		// Check if there is an encryption algorithm
        if ( Boolean.valueOf( 
        		MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION, getPlugin(  ) ).getName(  ) ) )
    	{
        	String strAlgorithm = MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENCRYPTION_ALGORITHM, getPlugin(  ) ).getName(  );
        	strPassword = CryptoService.encrypt( strPassword, strAlgorithm );
    	}

		int nIdRecord = Integer.parseInt( strIdRecord );

		MyluteceDirectoryUser myluteceDirectoryUser = MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, getPlugin( ) );

		if ( myluteceDirectoryUser == null )
		{
			if ( MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( strLogin, getPlugin( ) ).size( ) != 0 )
			{
				return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
			}

			myluteceDirectoryUser = new MyluteceDirectoryUser( );
			myluteceDirectoryUser.setLogin( strLogin );
			myluteceDirectoryUser.setIdRecord( nIdRecord );
			if( strActivate != null )
			{
				myluteceDirectoryUser.setActivated( true );
			}
			
			String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );
            if ( strError != null )
            {
            	return strError;
            }
            
			MyluteceDirectoryUserHome.create( myluteceDirectoryUser, strPassword, getPlugin( ) );
			MyLuteceUserFieldService.doModifyUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ), getUser(  ) );
		}
		else
		{
			if ( !myluteceDirectoryUser.getLogin( ).equalsIgnoreCase( strLogin )
					&& (MyluteceDirectoryUserHome.findDirectoryUsersListForLogin( strLogin, getPlugin( ) ).size( ) != 0) )
			{
				return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
			}

			myluteceDirectoryUser.setLogin( strLogin );
			myluteceDirectoryUser.setIdRecord( nIdRecord );
			if( strActivate  != null )
			{
				myluteceDirectoryUser.setActivated( true );
			}
			else
			{
				myluteceDirectoryUser.setActivated( false );
			}
			String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );
            if ( strError != null )
            {
            	return strError;
            }
			MyluteceDirectoryUserHome.update( myluteceDirectoryUser, getPlugin( ) );
			MyluteceDirectoryUserHome.updatePassword( myluteceDirectoryUser, strPassword, getPlugin( ) );
			MyLuteceUserFieldService.doModifyUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ), getUser(  ) );
		}
		
		Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

		Record record = RecordHome.findByPrimaryKey( nIdRecord, directoryPlugin );
		UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );
		url.addParameter( PARAMETER_ID_DIRECTORY, record.getDirectory( ).getIdDirectory( ) );

		return url.getUrl( );
	}

	/**
	 * Get the user removal message
	 * 
	 * @param request The HTTP servlet request
	 * @return The URL to redirect to
	 */
	public String doConfirmRemoveUser( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );

		if ( (strIdRecord == null) || !strIdRecord.matches( REGEX_ID ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		int nIdRecord = Integer.parseInt( strIdRecord );
		MyluteceDirectoryUser myluteceDirectoryUser = MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, getPlugin( ) );
		HashMap<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_ID_RECORD, String.valueOf( myluteceDirectoryUser.getIdRecord( ) ) );

		return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_USER, JSP_URL_PREFIX + JSP_URL_REMOVE_USER, AdminMessage.TYPE_QUESTION,
				model );
	}

	/**
	 * Processes the User deletion
	 * 
	 * @param request The HTTP servlet request
	 * @return The URL to redirect to
	 */
	public String doRemoveUser( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );

		if ( (strIdRecord == null) || !strIdRecord.matches( REGEX_ID ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		int nIdRecord = Integer.parseInt( strIdRecord );
		MyluteceDirectoryUser myluteceDirectoryUser = MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, getPlugin( ) );

		MyluteceDirectoryHome.removeRolesForUser( myluteceDirectoryUser.getIdRecord( ), getPlugin( ) );
		MyluteceDirectoryHome.removeGroupsForUser( myluteceDirectoryUser.getIdRecord( ), getPlugin( ) );
		MyluteceDirectoryUserHome.remove( myluteceDirectoryUser, getPlugin( ) );
		MyLuteceUserFieldService.doRemoveUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ) );

		UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );

		return url.getUrl( );
	}

	/**
	 * Returns roles management form for a specified user
	 * 
	 * @param request The Http request
	 * @return Html form
	 * @throws DirectoryErrorException
	 */
	public String getManageRolesUser( HttpServletRequest request ) throws AccessDeniedException, DirectoryErrorException
	{
		AdminUser adminUser = getUser( );

		setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER );

		MyluteceDirectoryUser user = getDirectoryUserFromRequest( request );

		if ( user == null )
		{
			return getManageRecords( request );
		}

		Collection<Role> allRoleList = RoleHome.findAll( );
		allRoleList = (ArrayList<Role>) RBACService.getAuthorizedCollection( allRoleList, RoleResourceIdService.PERMISSION_ASSIGN_ROLE, adminUser );

		ArrayList<String> userRoleKeyList = MyluteceDirectoryHome.findUserRolesFromLogin( user.getLogin( ), getPlugin( ) );
		Collection<Role> userRoleList = new ArrayList<Role>( );

		for( String strRoleKey : userRoleKeyList )
		{
			for( Role role : allRoleList )
			{
				if ( role.getRole( ).equals( strRoleKey ) )
				{
					userRoleList.add( RoleHome.findByPrimaryKey( strRoleKey ) );
				}
			}
		}

		HashMap<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_ROLES_LIST, allRoleList );
		model.put( MARK_ROLES_LIST_FOR_USER, userRoleList );
		model.put( MARK_USER, user );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ROLES_USER, getLocale( ), model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Process assignation roles for a specified user
	 * 
	 * @param request The Http request
	 * @return Html form
	 */
	public String doAssignRoleUser( HttpServletRequest request )
	{
		// get User
		MyluteceDirectoryUser user = getDirectoryUserFromRequest( request );

		if ( user == null )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MANAGE_ROLES, AdminMessage.TYPE_ERROR );
		}

		String[] roleArray = request.getParameterValues( PARAMETER_ID_ROLE );

		MyluteceDirectoryHome.removeRolesForUser( user.getIdRecord( ), getPlugin( ) );

		if ( roleArray != null )
		{
			for( int i = 0; i < roleArray.length; i++ )
			{
				MyluteceDirectoryHome.addRoleForUser( user.getIdRecord( ), roleArray[i], getPlugin( ) );
			}
		}

		return JSP_URL_MANAGE_DIRECTORY_RECORD;
	}


	/**
	 * Get the directory user from request
	 * 
	 * @param request The http request
	 * @return The Directory User
	 */
	private MyluteceDirectoryUser getDirectoryUserFromRequest( HttpServletRequest request )
	{
		String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );

		if ( (strIdRecord == null) || !strIdRecord.matches( REGEX_ID ) )
		{
			return null;
		}

		int nIdRecord = Integer.parseInt( strIdRecord );

		MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, getPlugin( ) );

		return user;
	}

	/**
	 * reinit directory record search
	 */
	private void reInitDirectoryRecordFilter( )
	{
		_nItemsPerPageDirectoryRecord = 0;
		_strCurrentPageIndexDirectory = null;
		_mapQuery = null;
	}

	/**
	 * return url of the jsp manage directory record
	 * 
	 * @param request The HTTP request
	 * @param nIdDirectory the directory id
	 * @return url of the jsp manage directory record
	 */
	private String getJspManageDirectoryRecord( HttpServletRequest request, int nIdDirectory )
	{
		return AppPathService.getBaseUrl( request ) + JSP_URL_PREFIX + JSP_URL_MANAGE_DIRECTORY_RECORD + "?" + PARAMETER_ID_DIRECTORY + "=" + nIdDirectory
				+ "&" + PARAMETER_SESSION + "=" + PARAMETER_SESSION;
	}

	/**
     * Returns advanced parameters form
     *
     * @param request The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    	throws AccessDeniedException
    {
    	if ( !RBACService.isAuthorized( MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, 
    			MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
    	{
    		return getManageRecords( request );
    	}
    	
    	Map<String, Object> model = MyluteceDirectoryService.getManageAdvancedParameters( getUser(  ) );
    	
    	HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }
    
    /**
     * Returns the page of confirmation for modifying the password
     * encryption
     *
     * @param request The Http Request
     * @return the confirmation url
     */
    public String doConfirmModifyPasswordEncryption( HttpServletRequest request )
    {
    	String strEnablePasswordEncryption = request.getParameter( PARAMETER_ENABLE_PASSWORD_ENCRYPTION );
    	String strEncryptionAlgorithm = request.getParameter( PARAMETER_ENCRYPTION_ALGORITHM );
    	
    	if ( strEncryptionAlgorithm.equals( CONSTANT_DEFAULT_ALGORITHM ) )
    	{
    		strEncryptionAlgorithm = EMPTY_STRING;
    	}
    	
    	String strCurrentPasswordEnableEncryption = MyluteceDirectoryParameterHome.findByKey( 
    			PARAMETER_ENABLE_PASSWORD_ENCRYPTION, getPlugin(  ) ).getName(  );
    	String strCurrentEncryptionAlgorithm = MyluteceDirectoryParameterHome.findByKey( 
    			PARAMETER_ENCRYPTION_ALGORITHM, getPlugin(  ) ).getName(  );
    	
    	String strUrl = EMPTY_STRING;
    	if ( strEnablePasswordEncryption.equals( strCurrentPasswordEnableEncryption ) 
    			&& strEncryptionAlgorithm.equals( strCurrentEncryptionAlgorithm ) )
    	{
    		strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_CHANGE_PASSWORD_ENCRYPTION, JSP_URL_MANAGE_ADVANCED_PARAMETERS,
                    AdminMessage.TYPE_INFO );
    	}
    	else if ( strEnablePasswordEncryption.equals( String.valueOf( Boolean.TRUE ) )  
    			&& strEncryptionAlgorithm.equals( EMPTY_STRING ) )
    	{
    		strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_INVALID_ENCRYPTION_ALGORITHM, JSP_URL_MANAGE_ADVANCED_PARAMETERS,
                    AdminMessage.TYPE_STOP );
    	}
    	else
    	{
    		if ( strEnablePasswordEncryption.equals( String.valueOf( Boolean.FALSE ) ) )
    		{
    			strEncryptionAlgorithm = EMPTY_STRING;
    		}
    		String strUrlModify = JSP_URL_MODIFY_PASSWORD_ENCRYPTION + QUESTION_MARK + PARAMETER_ENABLE_PASSWORD_ENCRYPTION + EQUAL + 
    				strEnablePasswordEncryption + AMPERSAND + PARAMETER_ENCRYPTION_ALGORITHM + EQUAL + strEncryptionAlgorithm;

    		strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_MODIFY_PASSWORD_ENCRYPTION, strUrlModify,
    				AdminMessage.TYPE_CONFIRMATION );
    	}

        return strUrl;
    }
    
    /**
     * Modify the password encryption
     * @param request HttpServletRequest
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException If the user does not have the permission
     */
    public String doModifyPasswordEncryption( HttpServletRequest request )
    	throws AccessDeniedException
    {
    	if ( !RBACService.isAuthorized( MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, 
    			MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
    	{
    		throw new AccessDeniedException(  );
    	}
    	
    	String strEnablePasswordEncryption = request.getParameter( PARAMETER_ENABLE_PASSWORD_ENCRYPTION );
    	String strEncryptionAlgorithm = request.getParameter( PARAMETER_ENCRYPTION_ALGORITHM );
    	
    	String strCurrentPasswordEnableEncryption = MyluteceDirectoryParameterHome.findByKey( 
    			PARAMETER_ENABLE_PASSWORD_ENCRYPTION, getPlugin(  ) ).getName(  );
    	String strCurrentEncryptionAlgorithm = MyluteceDirectoryParameterHome.findByKey( 
    			PARAMETER_ENCRYPTION_ALGORITHM, getPlugin(  ) ).getName(  );
    	
    	if ( strEnablePasswordEncryption.equals( strCurrentPasswordEnableEncryption ) 
    			&& strEncryptionAlgorithm.equals( strCurrentEncryptionAlgorithm ) )
    	{
    		return JSP_MANAGE_ADVANCED_PARAMETERS;
    	}
    	
    	ReferenceItem userParamEnablePwdEncryption = new ReferenceItem(  );
    	userParamEnablePwdEncryption.setCode( PARAMETER_ENABLE_PASSWORD_ENCRYPTION );
    	userParamEnablePwdEncryption.setName( strEnablePasswordEncryption );
    	ReferenceItem userParamEncryptionAlgorithm = new ReferenceItem(  );
    	userParamEncryptionAlgorithm.setCode( PARAMETER_ENCRYPTION_ALGORITHM );
    	userParamEncryptionAlgorithm.setName( strEncryptionAlgorithm );
        	
    	MyluteceDirectoryParameterHome.update( userParamEnablePwdEncryption, getPlugin(  ) );
    	MyluteceDirectoryParameterHome.update( userParamEncryptionAlgorithm, getPlugin(  ) );
        
        // Alert all users their password have been reinitialized.
    	Collection<MyluteceDirectoryUser> listUsers = MyluteceDirectoryUserHome.findDirectoryUsersList( getPlugin(  ) );
    	
    	for ( MyluteceDirectoryUser user : listUsers )
    	{   		
    		// make password
            String strPassword = PasswordUtil.makePassword(  );
            
            // update password
            if ( ( strPassword != null ) && !strPassword.equals( EMPTY_STRING ) )
            {
            	// Encrypted password
            	String strEncryptedPassword = strPassword;
            	if ( Boolean.valueOf( 
                		MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION, getPlugin(  ) ).getName(  ) ) )
            	{
            		String strAlgorithm = MyluteceDirectoryParameterHome.findByKey( 
            				PARAMETER_ENCRYPTION_ALGORITHM, getPlugin(  ) ).getName(  );
                	strEncryptedPassword = CryptoService.encrypt( strPassword, strAlgorithm );
            	}
            	MyluteceDirectoryUser userStored = MyluteceDirectoryUserHome.findByPrimaryKey( user.getIdRecord(  ), getPlugin(  ) );
            	MyluteceDirectoryUserHome.remove( userStored, getPlugin(  ) );
            	MyluteceDirectoryUserHome.create( userStored, strEncryptedPassword, getPlugin(  ) );
            }
            List<String> listEmails = MyluteceDirectoryUserHome.getEmails( user.getIdRecord(  ), getPlugin(  ), getLocale(  ) );
            
            if ( listEmails != null && listEmails.size(  ) != 0 )
            {
            	//send password by e-mail
                String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_NO_REPLY_EMAIL );
                String strEmailSubject = I18nService.getLocalizedString( PROPERTY_MESSAGE_EMAIL_SUBJECT, getLocale(  ) );
                HashMap<String, Object> model = new HashMap<String, Object>(  );
                model.put( MARK_NEW_PASSWORD, strPassword );
                model.put( MARK_LOGIN_URL,
                    AppPathService.getBaseUrl( request ) + AdminAuthenticationService.getInstance(  ).getLoginPageUrl(  ) );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_FORGOT_PASSWORD, getLocale(  ), model );

                for ( String email : listEmails )
                {
                	MailService.sendMailHtml( email, strSenderEmail, strSenderEmail, strEmailSubject,
                            template.getHtml(  ) );
                }
            }
    	}
        
    	return JSP_MANAGE_ADVANCED_PARAMETERS;
    }
}
