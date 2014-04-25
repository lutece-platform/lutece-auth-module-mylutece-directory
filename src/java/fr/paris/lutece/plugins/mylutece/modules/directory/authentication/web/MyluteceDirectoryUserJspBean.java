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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
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
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IMyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryAnonymizationService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryResourceIdService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.IMyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.MyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.plugins.mylutece.service.RoleResourceIdService;
import fr.paris.lutece.plugins.mylutece.service.attribute.MyLuteceUserFieldService;
import fr.paris.lutece.plugins.mylutece.util.SecurityUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


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
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/mylutece/modules/directory/manage_advanced_parameters.html";
    private static final String TEMPLATE_FIELD_ANONYMIZE_USER = "admin/plugins/mylutece/modules/directory/field_anonymize_user.html";
    private static final String TEMPLATE_ACCOUNT_LIFE_TIME_EMAIL = "admin/plugins/mylutece/modules/directory/account_life_time_email.html";

    // Jsp
    private static final String JSP_URL_PREFIX = "jsp/admin/plugins/mylutece/modules/directory/";
    private static final String JSP_URL_MANAGE_DIRECTORY_RECORD = "ManageRecords.jsp";
    private static final String JSP_URL_REMOVE_USER = "DoRemoveUser.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "ManageAdvancedParameters.jsp";
    private static final String JSP_URL_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/mylutece/modules/directory/ManageAdvancedParameters.jsp";
    private static final String JSP_URL_MODIFY_PASSWORD_ENCRYPTION = "jsp/admin/plugins/mylutece/modules/directory/DoModifyPasswordEncryption.jsp";
    private static final String JSP_URL_USE_ADVANCED_SECUR_PARAM = "jsp/admin/plugins/mylutece/modules/directory/DoUseAdvancedSecurityParameters.jsp";
    private static final String JSP_URL_REMOVE_ADVANCED_SECUR_PARAM = "jsp/admin/plugins/mylutece/modules/directory/DoRemoveAdvancedSecurityParameters.jsp";
    private static final String JSP_URL_ANONYMIZE_USER = "jsp/admin/plugins/mylutece/modules/directory/DoAnonymizeUser.jsp";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "module.mylutece.directory.items_per_page";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_USER = "module.mylutece.directory.modify_user.page_title";
    private static final String PROPERTY_PAGE_TITLE_CREATE_USER = "module.mylutece.directory.create_user.page_title";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER = "module.mylutece.directory.manage_roles_user.page_title";
    private static final String MESSAGE_USER_EXIST = "module.mylutece.directory.message.user_exist";
    private static final String MESSAGE_CONFIRMATION_REMOVE_USER = "module.mylutece.directory.message.confirm_remove_user";
    private static final String MESSAGE_ERROR_MANAGE_ROLES = "module.mylutece.directory.message.manage.roles";
    private static final String PROPERTY_MESSAGE_CONFIRM_MODIFY_PASSWORD_ENCRYPTION = "module.mylutece.directory.manage_advanced_parameters.message.confirmModifyPasswordEncryption";
    private static final String PROPERTY_MESSAGE_NO_CHANGE_PASSWORD_ENCRYPTION = "module.mylutece.directory.manage_advanced_parameters.message.noChangePasswordEncryption";
    private static final String PROPERTY_MESSAGE_INVALID_ENCRYPTION_ALGORITHM = "module.mylutece.directory.manage_advanced_parameters.message.invalidEncryptionAlgorithm";
    private static final String MESSAGE_ERROR_DIRECTORY_NOT_FOUND = "module.mylutece.directory.message.error.directory_not_found";
    private static final String MESSAGE_ERROR_USER_NOT_FOUND = "module.mylutece.directory.message.error.user_not_found";
    private static final String PROPERTY_MESSAGE_CONFIRM_USE_ASP = "mylutece.manage_advanced_parameters.message.confirmUseAdvancedSecurityParameters";
    private static final String PROPERTY_MESSAGE_CONFIRM_REMOVE_ASP = "mylutece.manage_advanced_parameters.message.confirmRemoveAdvancedSecurityParameters";
    private static final String PROPERTY_MESSAGE_TITLE_CHANGE_ANONYMIZE_USER = "mylutece.anonymize_user.titleAnonymizeUser";
    private static final String PROPERTY_MESSAGE_NO_USER_SELECTED = "mylutece.message.noUserSelected";
    private static final String PROPERTY_MESSAGE_CONFIRM_ANONYMIZE_USER = "mylutece.message.confirmAnonymizeUser";
    private static final String PROPERTY_FIRST_EMAIL = "mylutece.accountLifeTime.labelFirstEmail";
    private static final String PROPERTY_OTHER_EMAIL = "mylutece.accountLifeTime.labelOtherEmail";
    private static final String PROPERTY_ACCOUNT_DEACTIVATES_EMAIL = "mylutece.accountLifeTime.labelAccountDeactivatedEmail";
    private static final String PROPERTY_ACCOUNT_UPDATED_EMAIL = "mylutece.accountLifeTime.labelAccountUpdatedEmail";
    private static final String PROPERTY_UNBLOCK_USER = "mylutece.ip.unblockUser";
	private static final String PROPERTY_NOTIFY_PASSWORD_EXPIRED = "mylutece.accountLifeTime.labelPasswordExpired";
    private static final String PROPERTY_MAIL_LOST_PASSWORD = "mylutece.accountLifeTime.labelLostPasswordMail";
    private static final String PROPERTY_MAIL_PASSWORD_ENCRYPTION_CHANGED = "mylutece.accountLifeTime.labelPasswordEncryptionChangedMail";

    // Parameters
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_LOGIN = "login";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_MODIFY_PASSWORD = "modify_password";
    private static final String PARAMETER_ACTIVATE = "activate";
    private static final String PARAMETER_ID_RECORD = "id_record";
    private static final String PARAMETER_ID_ROLE = "id_role";
    private static final String PARAMETER_ENABLE_PASSWORD_ENCRYPTION = "enable_password_encryption";
    private static final String PARAMETER_ENCRYPTION_ALGORITHM = "encryption_algorithm";
    private static final String PARAMETER_ATTRIBUTE = "attribute_";
    private static final String PARAMETER_DIRECTORY = "directory_";
    private static final String PARAMETER_USER_LOGIN = "user_login";
    private static final String PARAMETER_EMAIL_TYPE = "email_type";
    private static final String PARAMETER_FIRST_ALERT_MAIL_SENDER = "first_alert_mail_sender";
    private static final String PARAMETER_OTHER_ALERT_MAIL_SENDER = "other_alert_mail_sender";
    private static final String PARAMETER_EXPIRED_ALERT_MAIL_SENDER = "expired_alert_mail_sender";
    private static final String PARAMETER_REACTIVATED_ALERT_MAIL_SENDER = "account_reactivated_mail_sender";
    private static final String PARAMETER_FIRST_ALERT_MAIL_SUBJECT = "first_alert_mail_subject";
    private static final String PARAMETER_OTHER_ALERT_MAIL_SUBJECT = "other_alert_mail_subject";
    private static final String PARAMETER_EXPIRED_ALERT_MAIL_SUBJECT = "expired_alert_mail_subject";
    private static final String PARAMETER_REACTIVATED_ALERT_MAIL_SUBJECT = "account_reactivated_mail_subject";
    private static final String PARAMETER_FIRST_ALERT_MAIL = "mylutece_directory_first_alert_mail";
    private static final String PARAMETER_OTHER_ALERT_MAIL = "mylutece_directory_other_alert_mail";
    private static final String PARAMETER_EXPIRATION_MAIL = "mylutece_directory_expiration_mail";
    private static final String PARAMETER_ACCOUNT_REACTIVATED = "mylutece_directory_account_reactivated_mail";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_BANNED_DOMAIN_NAMES = "banned_domain_names";
	private static final String PARAMETER_UNBLOCK_USER_MAIL_SENDER = "unblock_user_mail_sender";
	private static final String PARAMETER_UNBLOCK_USER_MAIL_SUBJECT = "unblock_user_mail_subject";
	private static final String PARAMETER_UNBLOCK_USER = "mylutece_database_unblock_user";
	private static final String PARAMETER_PASSWORD_EXPIRED_MAIL_SENDER = "password_expired_mail_sender";
	private static final String PARAMETER_PASSWORD_EXPIRED_MAIL_SUBJECT = "password_expired_mail_subject";
	private static final String PARAMETER_NOTIFY_PASSWORD_EXPIRED = "mylutece_directory_password_expired";
    private static final String PARAMETER_MAIL_LOST_PASSWORD = "mylutece_directory_mailLostPassword";
    private static final String PARAMETER_MAIL_LOST_PASSWORD_SENDER = "mail_lost_password_sender";
    private static final String PARAMETER_MAIL_LOST_PASSWORD_SUBJECT = "mail_lost_password_subject";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED = "mylutece_directory_mailPasswordEncryptionChanged";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SENDER = "mail_password_encryption_changed_sender";
    private static final String PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SUBJECT = "mail_password_encryption_changed_subject";
    private static final String PARAMETER_ENABLE_CAPTCHA_AUTHENTICATION = "enable_captcha_authentication";
    // Markers
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_DIRECTORY_RECORD_LIST = "directory_record_list";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_ENTRY_LIST_FORM_MAIN_SEARCH = "entry_list_form_main_search";
    private static final String MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH = "entry_list_form_complementary_search";
    private static final String MARK_ENTRY_LIST_SEARCH_RESULT = "entry_list_search_result";
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
    private static final String MARK_ACTIVATE = "activate";
    private static final String MARK_ATTRIBUTES_LIST = "attributes_list";
    private static final String MARK_MAP_LIST_ATTRIBUTE_DEFAULT_VALUES = "map_list_attribute_default_values";
    private static final String MARK_PERMISSION_ADVANCED_PARAMETER = "permission_advanced_parameter";
    private static final String MARK_DIRECTORY_ATTRIBUTES = "directory_attributes";
    private static final String MARK_EMAIL_SENDER = "email_sender";
    private static final String MARK_EMAIL_SUBJECT = "email_subject";
    private static final String MARK_EMAIL_BODY = "email_body";
    private static final String MARK_EMAIL_LABEL = "emailLabel";
    private static final String MARK_WEBAPP_URL = "webapp_url";

    // CONSTANTS
    private static final String CONSTANT_DEFAULT_ALGORITHM = "noValue";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUAL = "=";
    private static final String CONSTANT_EMAIL_TYPE_FIRST = "first";
    private static final String CONSTANT_EMAIL_TYPE_OTHER = "other";
    private static final String CONSTANT_EMAIL_TYPE_EXPIRED = "expired";
    private static final String CONSTANT_EMAIL_TYPE_REACTIVATED = "reactivated";
    private static final String CONSTANT_EMAIL_TYPE_IP_BLOCKED = "ip_blocked";
	private static final String CONSTANT_EMAIL_PASSWORD_EXPIRED = "password_expired";
    private static final String CONSTANT_EMAIL_TYPE_LOST_PASSWORD = "lost_password";
    private static final String CONSTANT_EMAIL_PASSWORD_ENCRYPTION_CHANGED = "password_encryption_changed";

    // Session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexDirectoryRecord;
    private int _nItemsPerPageDirectoryRecord;
    private HashMap<String, List<RecordField>> _mapQuery;
    private IMyluteceDirectoryService _myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );
    private IMyluteceDirectoryParameterService _parameterService = SpringContextService.getBean( MyluteceDirectoryParameterService.BEAN_SERVICE );
    private MyluteceDirectoryAnonymizationService _anonymizationService = SpringContextService
            .getBean( MyluteceDirectoryAnonymizationService.BEAN_SERVICE );

    /**
     * Creates a new DirectoryJspBean object.
     */
    public MyluteceDirectoryUserJspBean(  )
    {
    }

    /**
     * Return management records
     *
     * @param request The Http request
     * @return Html directory
     * @throws AccessDeniedException exception if the user does not have the permission
     */
    public String getManageRecords( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = 0;
        Map<String, Object> model = new HashMap<String, Object>(  );

        boolean bNoDirectory = true;

        if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            Collection<Integer> integerList = _myluteceDirectoryService.getMappedDirectories( getPlugin(  ) );

            if ( integerList.size(  ) != 0 )
            {
                nIdDirectory = integerList.iterator(  ).next(  ).intValue(  );
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
        	String strURL = getJspManageDirectoryRecord( request, nIdDirectory );
            UrlItem url = new UrlItem( strURL );

            Directory directory = _myluteceDirectoryService.getDirectory( nIdDirectory );

            if ( directory == null )
            {
                String strErrorMessage = I18nService.getLocalizedString( MESSAGE_ERROR_DIRECTORY_NOT_FOUND,
                        getLocale(  ) );
                throw new AccessDeniedException( strErrorMessage );
            }

            if ( request.getParameter( PARAMETER_SESSION ) == null )
            {
                reInitDirectoryRecordFilter(  );
            }

            _strCurrentPageIndexDirectoryRecord = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    _strCurrentPageIndexDirectoryRecord );
            _nItemsPerPageDirectoryRecord = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    _nItemsPerPageDirectoryRecord, _nDefaultItemsPerPage );

            if ( request.getParameter( PARAMETER_SEARCH ) != null )
            {
                // get search filter
                try
                {
                    _mapQuery = DirectoryUtils.getSearchRecordData( request, nIdDirectory, getPlugin(  ), getLocale(  ) );
                }
                catch ( DirectoryErrorException error )
                {
                    AppLogService.debug( error.getErrorMessage(  ) );
                }
            }

            List<IEntry> listEntryFormMainSearch = new ArrayList<IEntry>(  );
            List<IEntry> listEntryFormComplementarySearch = new ArrayList<IEntry>(  );
            List<IEntry> listEntryResultSearch = new ArrayList<IEntry>(  );

            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( nIdDirectory );
            entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
            entryFilter.setIsComment( EntryFilter.FILTER_FALSE );

            for ( IEntry entry : _myluteceDirectoryService.getListEntries( entryFilter ) )
            {
                if ( entry.isIndexed(  ) )
                {
                    if ( !entry.isShownInAdvancedSearch(  ) )
                    {
                        listEntryFormMainSearch.add( _myluteceDirectoryService.getEntry( entry.getIdEntry(  ) ) );
                    }
                    else
                    {
                        listEntryFormComplementarySearch.add( _myluteceDirectoryService.getEntry( entry.getIdEntry(  ) ) );
                    }
                }

                if ( entry.isShownInResultList(  ) )
                {
                    listEntryResultSearch.add( entry );
                }
            }

            // call search service
            RecordFieldFilter filter = new RecordFieldFilter(  );
            filter.setIdDirectory( directory.getIdDirectory(  ) );

            Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

            List<Integer> listResultRecordId = DirectorySearchService.getInstance(  )
                                                                     .getSearchResults( directory, _mapQuery, null,
                    null, null, filter, directoryPlugin );

            listResultRecordId = _myluteceDirectoryService.getFilteredUsersInterface( listResultRecordId, request,
                    model, url );

            LocalizedPaginator<Integer> paginator = new LocalizedPaginator<Integer>( listResultRecordId,
                    _nItemsPerPageDirectoryRecord, url.getUrl(  ), PARAMETER_PAGE_INDEX,
                    _strCurrentPageIndexDirectoryRecord, getLocale(  ) );

            List<Map<String, Object>> listRecords = new ArrayList<Map<String, Object>>(  );

            for ( int nIdRecord : paginator.getPageItems(  ) )
            {
                Map<String, Object> subModel = new HashMap<String, Object>(  );

                Record record = _myluteceDirectoryService.getRecord( nIdRecord, true );

                subModel.put( MARK_RECORD, record );
                subModel.put( MARK_MYLUTECE_RECORD,
                    _myluteceDirectoryService.getMyluteceDirectoryUser( record.getIdRecord(  ), getPlugin(  ) ) );
                listRecords.add( subModel );
            }

            model.put( MARK_PAGINATOR, paginator );
            model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageDirectoryRecord );
            model.put( MARK_ENTRY_LIST_FORM_MAIN_SEARCH, listEntryFormMainSearch );
            model.put( MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH, listEntryFormComplementarySearch );
            model.put( MARK_ENTRY_LIST_SEARCH_RESULT, listEntryResultSearch );
            model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, _mapQuery );
            model.put( MARK_DIRECTORY, directory );
            model.put( MARK_DIRECTORY_RECORD_LIST, listRecords );
            model.put( MARK_NUMBER_RECORD, paginator.getItemsCount(  ) );

            boolean bPermissionAdvancedParameter = RBACService.isAuthorized(
                    MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, getUser( ) );

            model.put( MARK_PERMISSION_ADVANCED_PARAMETER, bPermissionAdvancedParameter );

            setPageTitleProperty( StringUtils.EMPTY );
        }
        else
        {
            model.put( MARK_NO_DIRECTORY, true );
        }

        model.put( MARK_LOCALE, getLocale(  ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY_RECORD, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Returns the User modification form
     *
     * @param request The Http request
     * @return Html modification form
     * @throws AccessDeniedException exception if the user does not have the permission
     */
    public String getModifyUser( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_USER );

        if ( StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( Messages.MANDATORY_FIELDS, getLocale(  ) );
            throw new AccessDeniedException( strErrorMessage );
        }

        int nIdDirectoryRecord = Integer.parseInt( strIdRecord );

        Map<String, Object> model = new HashMap<String, Object>(  );
        MyluteceDirectoryUser user = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdDirectoryRecord,
                getPlugin(  ) );

        // Specific attributes
        Plugin myLutecePlugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        List<IAttribute> listAttributes = AttributeHome.findAll( getLocale(  ), myLutecePlugin );
        Map<String, List<MyLuteceUserField>> map = new HashMap<String, List<MyLuteceUserField>>(  );

        for ( IAttribute attribute : listAttributes )
        {
            List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( attribute.getIdAttribute(  ),
                    myLutecePlugin );
            attribute.setListAttributeFields( listAttributeFields );

            List<MyLuteceUserField> listUserFields = MyLuteceUserFieldHome.selectUserFieldsByIdUserIdAttribute( user.getIdRecord(  ),
                    attribute.getIdAttribute(  ), myLutecePlugin );

            if ( listUserFields.size(  ) == 0 )
            {
                MyLuteceUserField userField = new MyLuteceUserField(  );
                userField.setValue( StringUtils.EMPTY );
                listUserFields.add( userField );
            }

            map.put( String.valueOf( attribute.getIdAttribute(  ) ), listUserFields );
        }

        model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
        model.put( MARK_ID_RECORD, nIdDirectoryRecord );
        model.put( MARK_LOGIN, ( user != null ) ? user.getLogin(  ) : StringUtils.EMPTY );
        model.put( MARK_PASSWORD, _myluteceDirectoryService.getPasswordByIdRecord( nIdDirectoryRecord, getPlugin(  ) ) );
        model.put( MARK_ACTIVATE, ( user != null ) ? user.isActivated(  ) : StringUtils.EMPTY );
        model.put( MARK_ATTRIBUTES_LIST, listAttributes );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_MAP_LIST_ATTRIBUTE_DEFAULT_VALUES, map );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_USER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the User creation form
     *
     * @param request The Http request
     * @return Html creation form
     * @throws AccessDeniedException exception if the user does not have the permission
     */
    public String getCreateUser( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_USER );

        if ( StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( Messages.MANDATORY_FIELDS, getLocale(  ) );
            throw new AccessDeniedException( strErrorMessage );
        }

        int nIdDirectoryRecord = Integer.parseInt( strIdRecord );

        Plugin myLutecePlugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        // Specific attributes
        List<IAttribute> listAttributes = AttributeHome.findAll( getLocale(  ), myLutecePlugin );

        for ( IAttribute attribute : listAttributes )
        {
            List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( attribute.getIdAttribute(  ),
                    myLutecePlugin );
            attribute.setListAttributeFields( listAttributeFields );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        MyluteceDirectoryUser user = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdDirectoryRecord,
                getPlugin(  ) );
        model.put( MARK_PLUGIN_NAME, MyluteceDirectoryPlugin.PLUGIN_NAME );
        model.put( MARK_ID_RECORD, nIdDirectoryRecord );
        model.put( MARK_LOGIN, ( user != null ) ? user.getLogin(  ) : StringUtils.EMPTY );
        model.put( MARK_PASSWORD, _myluteceDirectoryService.getPasswordByIdRecord( nIdDirectoryRecord, getPlugin(  ) ) );
        model.put( MARK_ATTRIBUTES_LIST, listAttributes );
        model.put( MARK_LOCALE, getLocale(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_USER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
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

        if ( StringUtils.isBlank( strLogin ) || StringUtils.isBlank( strPassword ) ||
                StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        String retUrl = SecurityUtils
                .checkPasswordForBackOffice( _parameterService, getPlugin( ), strPassword, request );

        if ( retUrl != null )
        {
            return retUrl;
        }

        int nIdRecord = Integer.parseInt( strIdRecord );

        Collection<MyluteceDirectoryUser> listDirectoryUsers = _myluteceDirectoryService.getMyluteceDirectoryUsersForLogin( strLogin,
                getPlugin(  ) );

        if ( ( listDirectoryUsers != null ) && !listDirectoryUsers.isEmpty(  ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
        }

        MyluteceDirectoryUser myluteceDirectoryUser = new MyluteceDirectoryUser(  );
        myluteceDirectoryUser.setLogin( strLogin );
        myluteceDirectoryUser.setIdRecord( nIdRecord );
        int nStatus = StringUtils.isNotBlank( strActivate ) ? MyluteceDirectoryUser.STATUS_ACTIVATED
                : MyluteceDirectoryUser.STATUS_NOT_ACTIVATED;
        myluteceDirectoryUser.setStatus( nStatus );

        String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );

        if ( StringUtils.isNotBlank( strError ) )
        {
            return strError;
        }

        _myluteceDirectoryService.doCreateMyluteceDirectoryUser( myluteceDirectoryUser, strPassword, getPlugin(  ) );
        _myluteceDirectoryService.doModifyResetPassword( myluteceDirectoryUser, true, getPlugin( ) );
        MyLuteceUserFieldService.doCreateUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ) );

        Record record = _myluteceDirectoryService.getRecord( nIdRecord, false );
        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );

        if ( record != null )
        {
            url.addParameter( PARAMETER_ID_DIRECTORY, record.getDirectory(  ).getIdDirectory(  ) );
        }

        return url.getUrl(  );
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
        String strModifyPassword = request.getParameter( PARAMETER_MODIFY_PASSWORD );
        boolean bModifyPassword = StringUtils.isNotBlank( strModifyPassword );

        if ( StringUtils.isBlank( strLogin ) || StringUtils.isBlank( strIdRecord )
                || !StringUtils.isNumeric( strIdRecord ) || ( bModifyPassword && StringUtils.isBlank( strPassword ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nStatus = StringUtils.isNotBlank( strActivate ) ? MyluteceDirectoryUser.STATUS_ACTIVATED
                : MyluteceDirectoryUser.STATUS_NOT_ACTIVATED;

        if ( bModifyPassword )
        {
            String retUrl = SecurityUtils
                    .checkPasswordForBackOffice( _parameterService, getPlugin( ), strPassword, request );

            if ( retUrl != null )
            {
                return retUrl;
            }
        }

        int nIdRecord = Integer.parseInt( strIdRecord );

        MyluteceDirectoryUser myluteceDirectoryUser = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdRecord,
                getPlugin(  ) );

        Collection<MyluteceDirectoryUser> listMyluteceDirectoryUsers = _myluteceDirectoryService.getMyluteceDirectoryUsersForLogin( strLogin,
                getPlugin(  ) );

        if ( myluteceDirectoryUser == null )
        {
            if ( ( listMyluteceDirectoryUsers != null ) && !listMyluteceDirectoryUsers.isEmpty(  ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
            }

            myluteceDirectoryUser = new MyluteceDirectoryUser(  );
            myluteceDirectoryUser.setLogin( strLogin );
            myluteceDirectoryUser.setIdRecord( nIdRecord );
            myluteceDirectoryUser.setStatus( nStatus );

            String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );

            if ( StringUtils.isNotBlank( strError ) )
            {
                return strError;
            }

            _myluteceDirectoryService.doCreateMyluteceDirectoryUser( myluteceDirectoryUser, strPassword, getPlugin(  ) );
            _myluteceDirectoryService.doModifyResetPassword( myluteceDirectoryUser, Boolean.TRUE, getPlugin( ) );
            MyLuteceUserFieldService.doCreateUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ) );
        }
        else
        {
            if ( !strLogin.equalsIgnoreCase( myluteceDirectoryUser.getLogin(  ) ) &&
                    ( listMyluteceDirectoryUsers != null ) && !listMyluteceDirectoryUsers.isEmpty(  ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
            }

            myluteceDirectoryUser.setLogin( strLogin );
            myluteceDirectoryUser.setIdRecord( nIdRecord );
            myluteceDirectoryUser.setStatus( nStatus );

            String strError = MyLuteceUserFieldService.checkUserFields( request, getLocale(  ) );

            if ( StringUtils.isNotBlank( strError ) )
            {
                return strError;
            }

            _myluteceDirectoryService.doModifyMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ) );

            if ( bModifyPassword )
            {
                _myluteceDirectoryService.doModifyPassword( myluteceDirectoryUser, strPassword, getPlugin(  ) );
                _myluteceDirectoryService.doModifyResetPassword( myluteceDirectoryUser, Boolean.FALSE, getPlugin( ) );
            }

            MyLuteceUserFieldService.doModifyUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ),
                getUser(  ) );
        }

        Record record = _myluteceDirectoryService.getRecord( nIdRecord, false );
        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );

        if ( record != null )
        {
            url.addParameter( PARAMETER_ID_DIRECTORY, record.getDirectory(  ).getIdDirectory(  ) );
        }

        return url.getUrl(  );
    }
    
    
    
    /**
     * Disable User
     *
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doDisableUser( HttpServletRequest request )
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
     
        if ( StringUtils.isBlank( strIdRecord )
                || !StringUtils.isNumeric( strIdRecord ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        
        
        int nIdRecord = Integer.parseInt( strIdRecord );

        MyluteceDirectoryUser myluteceDirectoryUser = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdRecord,
                getPlugin(  ) );

     

        if ( myluteceDirectoryUser != null && myluteceDirectoryUser .getStatus()==MyluteceDirectoryUser.STATUS_ACTIVATED)
        {
        		myluteceDirectoryUser.setStatus( MyluteceDirectoryUser.STATUS_NOT_ACTIVATED);
        	    _myluteceDirectoryService.doModifyMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ) );
        	
        }
        
        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );
        return url.getUrl(  );
    }
    
    
    /**
     * Enable User
     *
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doEnableUser( HttpServletRequest request )
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
     
        if ( StringUtils.isBlank( strIdRecord )
                || !StringUtils.isNumeric( strIdRecord ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        
        
        int nIdRecord = Integer.parseInt( strIdRecord );

        MyluteceDirectoryUser myluteceDirectoryUser = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdRecord,
                getPlugin(  ) );

     

        if ( myluteceDirectoryUser != null && myluteceDirectoryUser .getStatus()==MyluteceDirectoryUser.STATUS_NOT_ACTIVATED)
        {
        		myluteceDirectoryUser.setStatus( MyluteceDirectoryUser.STATUS_ACTIVATED);
        	    _myluteceDirectoryService.doModifyMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ) );
        	
        }
        
        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );
        return url.getUrl(  );
    }


    /**
     * Get the user removal message
     *
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException exception if the user does not have the permission
     */
    public String doConfirmRemoveUser( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );

        if ( StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ID_RECORD, strIdRecord );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_USER,
            JSP_URL_PREFIX + JSP_URL_REMOVE_USER, AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Processes the User deletion
     *
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException exception if the user does not have the permission
     */
    public String doRemoveUser( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );

        if ( StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdRecord = Integer.parseInt( strIdRecord );
        MyluteceDirectoryUser myluteceDirectoryUser = _myluteceDirectoryService.getMyluteceDirectoryUser( nIdRecord,
                getPlugin(  ) );

        if ( myluteceDirectoryUser != null )
        {
            _myluteceDirectoryService.doRemoveMyluteceDirectoryUser( myluteceDirectoryUser, getPlugin(  ), true );
            MyLuteceUserFieldService.doRemoveUserFields( myluteceDirectoryUser.getIdRecord(  ), request, getLocale(  ) );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_USER_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY_RECORD );

        return url.getUrl(  );
    }

    /**
     * Returns roles management form for a specified user
     *
     * @param request The Http request
     * @return Html form
     * @throws AccessDeniedException exception if the user does not have the permission
     * @throws DirectoryErrorException exception if there is an directory error
     */
    public String getManageRolesUser( HttpServletRequest request )
        throws AccessDeniedException, DirectoryErrorException
    {
        AdminUser adminUser = getUser(  );

        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER );

        MyluteceDirectoryUser user = getDirectoryUserFromRequest( request );

        if ( user == null )
        {
            return getManageRecords( request );
        }

        Collection<Role> allRoleList = RoleHome.findAll(  );
        allRoleList = RBACService.getAuthorizedCollection( allRoleList,
                RoleResourceIdService.PERMISSION_ASSIGN_ROLE, adminUser );

        List<String> userRoleKeyList = _myluteceDirectoryService.getUserRolesFromLogin( user.getLogin(  ), getPlugin(  ) );
        Collection<Role> userRoleList = new ArrayList<Role>(  );

        for ( String strRoleKey : userRoleKeyList )
        {
            for ( Role role : allRoleList )
            {
                if ( role.getRole(  ).equals( strRoleKey ) )
                {
                    userRoleList.add( RoleHome.findByPrimaryKey( strRoleKey ) );
                }
            }
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ROLES_LIST, allRoleList );
        model.put( MARK_ROLES_LIST_FOR_USER, userRoleList );
        model.put( MARK_USER, user );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ROLES_USER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
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

        _myluteceDirectoryService.doAssignRoleUser( user, roleArray, getPlugin(  ) );

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

        if ( StringUtils.isBlank( strIdRecord ) || !StringUtils.isNumeric( strIdRecord ) )
        {
            return null;
        }

        int nIdRecord = Integer.parseInt( strIdRecord );

        return _myluteceDirectoryService.getMyluteceDirectoryUser( nIdRecord, getPlugin(  ) );
    }

    /**
     * reinit directory record search
     */
    private void reInitDirectoryRecordFilter(  )
    {
        _nItemsPerPageDirectoryRecord = 0;
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
        return AppPathService.getBaseUrl( request ) + JSP_URL_PREFIX + JSP_URL_MANAGE_DIRECTORY_RECORD + QUESTION_MARK +
        PARAMETER_ID_DIRECTORY + EQUAL + nIdDirectory + AMPERSAND + PARAMETER_SESSION + EQUAL + PARAMETER_SESSION;
    }

    /**
    * Returns advanced parameters form
    *
    * @param request The Http request
    * @return Html form
    * @throws AccessDeniedException exception if the user does not have the permission
    */
    public String getManageAdvancedParameters( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
        {
            return getManageRecords( request );
        }

        Map<String, Object> model = _myluteceDirectoryService.getManageAdvancedParameters( getUser(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Do modify the directory user parameters
     * @param request the HTTP request
     * @return the JSP return
     * @throws AccessDeniedException access denied if the user does have the
     *             right
     */
	public String doModifyDirectoryUserParameters( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( MyluteceDirectoryResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                MyluteceDirectoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            throw new AccessDeniedException( );
        }

        SecurityUtils.updateSecurityParameters( _parameterService, request, getPlugin( ) );
		SecurityUtils.updateLargeParameterValue( _parameterService, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ), PARAMETER_BANNED_DOMAIN_NAMES, request
				.getParameter( PARAMETER_BANNED_DOMAIN_NAMES ) );
		SecurityUtils.updateParameterValue( _parameterService, getPlugin(  ),
				  PARAMETER_ENABLE_CAPTCHA_AUTHENTICATION,
		            request.getParameter( PARAMETER_ENABLE_CAPTCHA_AUTHENTICATION ) );
		

        return JSP_MANAGE_ADVANCED_PARAMETERS;
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
            strEncryptionAlgorithm = StringUtils.EMPTY;
        }

        String strCurrentPasswordEnableEncryption = _parameterService.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION,
                getPlugin(  ) ).getName(  );
        String strCurrentEncryptionAlgorithm = _parameterService.findByKey( PARAMETER_ENCRYPTION_ALGORITHM,
                getPlugin(  ) ).getName(  );

        String strUrl = StringUtils.EMPTY;

        if ( strEnablePasswordEncryption.equals( strCurrentPasswordEnableEncryption ) &&
                strEncryptionAlgorithm.equals( strCurrentEncryptionAlgorithm ) )
        {
            strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_CHANGE_PASSWORD_ENCRYPTION,
                    JSP_URL_MANAGE_ADVANCED_PARAMETERS, AdminMessage.TYPE_INFO );
        }
        else if ( strEnablePasswordEncryption.equals( String.valueOf( Boolean.TRUE ) ) &&
                StringUtils.isBlank( strEncryptionAlgorithm ) )
        {
            strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_INVALID_ENCRYPTION_ALGORITHM,
                    JSP_URL_MANAGE_ADVANCED_PARAMETERS, AdminMessage.TYPE_STOP );
        }
        else
        {
            if ( strEnablePasswordEncryption.equals( String.valueOf( Boolean.FALSE ) ) )
            {
                strEncryptionAlgorithm = StringUtils.EMPTY;
            }

            String strUrlModify = JSP_URL_MODIFY_PASSWORD_ENCRYPTION + QUESTION_MARK +
                PARAMETER_ENABLE_PASSWORD_ENCRYPTION + EQUAL + strEnablePasswordEncryption + AMPERSAND +
                PARAMETER_ENCRYPTION_ALGORITHM + EQUAL + strEncryptionAlgorithm;

            strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_MODIFY_PASSWORD_ENCRYPTION,
                    strUrlModify, AdminMessage.TYPE_CONFIRMATION );
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
            String strErrorMessage = I18nService.getLocalizedString( Messages.USER_ACCESS_DENIED, getLocale(  ) );
            throw new AccessDeniedException( strErrorMessage );
        }

        String strEnablePasswordEncryption = request.getParameter( PARAMETER_ENABLE_PASSWORD_ENCRYPTION );
        String strEncryptionAlgorithm = request.getParameter( PARAMETER_ENCRYPTION_ALGORITHM );

        String strCurrentPasswordEnableEncryption = _parameterService.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION,
                getPlugin(  ) ).getName(  );
        String strCurrentEncryptionAlgorithm = _parameterService.findByKey( PARAMETER_ENCRYPTION_ALGORITHM,
                getPlugin(  ) ).getName(  );

        if ( strEnablePasswordEncryption.equals( strCurrentPasswordEnableEncryption ) &&
                strEncryptionAlgorithm.equals( strCurrentEncryptionAlgorithm ) )
        {
            return JSP_MANAGE_ADVANCED_PARAMETERS;
        }

        ReferenceItem userParamEnablePwdEncryption = new ReferenceItem(  );
        userParamEnablePwdEncryption.setCode( PARAMETER_ENABLE_PASSWORD_ENCRYPTION );
        userParamEnablePwdEncryption.setName( strEnablePasswordEncryption );

        ReferenceItem userParamEncryptionAlgorithm = new ReferenceItem(  );
        userParamEncryptionAlgorithm.setCode( PARAMETER_ENCRYPTION_ALGORITHM );
        userParamEncryptionAlgorithm.setName( strEncryptionAlgorithm );

        _parameterService.update( userParamEnablePwdEncryption, getPlugin(  ) );
        _parameterService.update( userParamEncryptionAlgorithm, getPlugin(  ) );

        _myluteceDirectoryService.changeUserPasswordAndNotify( AppPathService.getBaseUrl( request ), getPlugin( ),
                request.getLocale( ) );

        return JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Get the admin message to confirm the enabling or the disabling of the
     * advanced security parameters
     * @param request The request
     * @return The url of the admin message
     */
    public String getChangeUseAdvancedSecurityParameters( HttpServletRequest request )
    {
        if ( SecurityUtils.isAdvancedSecurityParametersUsed( _parameterService, getPlugin( ) ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_REMOVE_ASP,
                    JSP_URL_REMOVE_ADVANCED_SECUR_PARAM, AdminMessage.TYPE_CONFIRMATION );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_USE_ASP,
                    JSP_URL_USE_ADVANCED_SECUR_PARAM, AdminMessage.TYPE_CONFIRMATION );
        }
    }

    /**
     * Enable advanced security parameters, and change users password if
     * password encryption change
     * @param request The request
     * @return The Jsp URL of the process result
     */
    public String doUseAdvancedSecurityParameters( HttpServletRequest request )
    {
        boolean isPwdEncryptionEnabled = _parameterService.isPasswordEncrypted( getPlugin( ) );
        String strEncryptionAlgorithm = _parameterService.getEncryptionAlgorithm( getPlugin( ) );

        SecurityUtils.useAdvancedSecurityParameters( _parameterService, getPlugin( ) );

        if ( !isPwdEncryptionEnabled
                || !StringUtils
                        .equals( strEncryptionAlgorithm, _parameterService.getEncryptionAlgorithm( getPlugin( ) ) ) )
        {
            _myluteceDirectoryService.changeUserPasswordAndNotify( AppPathService.getBaseUrl( request ), getPlugin( ),
                    request.getLocale( ) );
        }
        return JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Disable advanced security parameters
     * @param request The request
     * @return The Jsp URL of the process result
     */
    public String doRemoveAdvancedSecurityParameters( HttpServletRequest request )
    {
        SecurityUtils.removeAdvancedSecurityParameters( _parameterService, getPlugin( ) );
        return JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Get the page with the list of every anonymizable attribute
     * @param request The request
     * @return The admin page
     */
    public String getChangeFieldAnonymizeAdminUsers( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        Plugin pluginMyLutece = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        List<IAttribute> listAllAttributes = AttributeHome.findAll( getLocale( ), pluginMyLutece );
        List<IAttribute> listAttributesText = new ArrayList<IAttribute>( );
        for ( IAttribute attribut : listAllAttributes )
        {
            if ( attribut.isAnonymizable( ) )
            {
                listAttributesText.add( attribut );
            }
        }
        model.put( MARK_ATTRIBUTES_LIST, listAttributesText );

        Map<String, Boolean> staticFields = AttributeHome.getAnonymizationStatusUserStaticField( pluginMyLutece );

        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> entryList = EntryHome.getEntryListAnonymizeStatus( directoryPlugin );

        model.putAll( staticFields );
        model.put( MARK_DIRECTORY_ATTRIBUTES, entryList );


        setPageTitleProperty( PROPERTY_MESSAGE_TITLE_CHANGE_ANONYMIZE_USER );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FIELD_ANONYMIZE_USER, getLocale( ),
                model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Change the anonymization status of user parameters.
     * @param request The request
     * @return the Jsp URL of the process result
     */
    public String doChangeFieldAnonymizeUsers( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return JSP_MANAGE_ADVANCED_PARAMETERS;
        }
        Plugin pluginMyLutece = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        AttributeHome.updateAnonymizationStatusUserStaticField( PARAMETER_USER_LOGIN,
                Boolean.valueOf( request.getParameter( PARAMETER_USER_LOGIN ) ), pluginMyLutece );

        List<IAttribute> listAllAttributes = AttributeHome.findAll( getLocale( ), pluginMyLutece );
        List<IAttribute> listAttributesText = new ArrayList<IAttribute>( );
        for ( IAttribute attribut : listAllAttributes )
        {
            if ( attribut.isAnonymizable( ) )
            {
                listAttributesText.add( attribut );
            }
        }

        for ( IAttribute attribute : listAttributesText )
        {
            Boolean bNewValue = Boolean.valueOf( request.getParameter( PARAMETER_ATTRIBUTE
                    + Integer.toString( attribute.getIdAttribute( ) ) ) );
            AttributeHome.updateAttributeAnonymization( attribute.getIdAttribute( ), bNewValue, pluginMyLutece );
        }

        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> entryList = EntryHome.getEntryListAnonymizeStatus( directoryPlugin );

        for ( IEntry entry : entryList )
        {
            Boolean bNewValue = Boolean.valueOf( request.getParameter( PARAMETER_DIRECTORY
                    + Integer.toString( entry.getIdEntry( ) ) ) );
            if ( bNewValue.booleanValue( ) != entry.getAnonymize( ) )
            {
                EntryHome.updateEntryAnonymizeStatus( entry.getIdEntry( ), bNewValue, directoryPlugin );
            }
        }
        return JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Get the confirmation page before anonymizing a user.
     * @param request The request
     * @return The URL of the confirmation page
     */
    public String getAnonymizeUser( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_URL_ANONYMIZE_USER );

        String strUserId = request.getParameter( PARAMETER_ID_RECORD );
        if ( strUserId == null || strUserId.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_USER_SELECTED,
                    AdminMessage.TYPE_STOP );
        }

        url.addParameter( PARAMETER_ID_RECORD, strUserId );

        return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_ANONYMIZE_USER, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Anonymize a user
     * @param request The request
     * @return The Jsp URL of the process result
     */
    public String doAnonymizeUser( HttpServletRequest request )
    {
        String strUserId = request.getParameter( PARAMETER_ID_RECORD );
        if ( strUserId == null || strUserId.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_NO_USER_SELECTED,
                    AdminMessage.TYPE_STOP );
        }

        _anonymizationService.anonymizeUser( Integer.parseInt( strUserId ), getLocale( ) );

        return JSP_URL_MANAGE_DIRECTORY_RECORD;
    }

    /**
     * Get the modify account life time emails page
     * @param request The request
     * @return The html to display
     */
    public String getModifyAccountLifeTimeEmails( HttpServletRequest request )
    {
        String strEmailType = request.getParameter( PARAMETER_EMAIL_TYPE );

        Map<String, Object> model = new HashMap<String, Object>( );
        String strSenderKey = StringUtils.EMPTY;
        String strSubjectKey = StringUtils.EMPTY;
        String strBodyKey = StringUtils.EMPTY;
        String strTitle = StringUtils.EMPTY;

        if ( CONSTANT_EMAIL_TYPE_FIRST.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_FIRST_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_FIRST_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_FIRST_ALERT_MAIL;
            strTitle = PROPERTY_FIRST_EMAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_OTHER.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_OTHER_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_OTHER_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_OTHER_ALERT_MAIL;
            strTitle = PROPERTY_OTHER_EMAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_EXPIRED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_EXPIRED_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_EXPIRED_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_EXPIRATION_MAIL;
            strTitle = PROPERTY_ACCOUNT_DEACTIVATES_EMAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_REACTIVATED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_REACTIVATED_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_REACTIVATED_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_ACCOUNT_REACTIVATED;
            strTitle = PROPERTY_ACCOUNT_UPDATED_EMAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_LOST_PASSWORD.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_MAIL_LOST_PASSWORD_SENDER;
            strSubjectKey = PARAMETER_MAIL_LOST_PASSWORD_SUBJECT;
            strBodyKey = PARAMETER_MAIL_LOST_PASSWORD;
            strTitle = PROPERTY_MAIL_LOST_PASSWORD;
        }
		else if ( CONSTANT_EMAIL_TYPE_IP_BLOCKED.equalsIgnoreCase( strEmailType ) )
		{
			strSenderKey = PARAMETER_UNBLOCK_USER_MAIL_SENDER;
			strSubjectKey = PARAMETER_UNBLOCK_USER_MAIL_SUBJECT;
			strBodyKey = PARAMETER_UNBLOCK_USER;
			strTitle = PROPERTY_UNBLOCK_USER;
		}
		else if ( CONSTANT_EMAIL_PASSWORD_EXPIRED.equalsIgnoreCase( strEmailType ) )
		{
			strSenderKey = PARAMETER_PASSWORD_EXPIRED_MAIL_SENDER;
			strSubjectKey = PARAMETER_PASSWORD_EXPIRED_MAIL_SUBJECT;
			strBodyKey = PARAMETER_NOTIFY_PASSWORD_EXPIRED;
			strTitle = PROPERTY_NOTIFY_PASSWORD_EXPIRED;
		}
        else if ( CONSTANT_EMAIL_TYPE_LOST_PASSWORD.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_MAIL_LOST_PASSWORD_SENDER;
            strSubjectKey = PARAMETER_MAIL_LOST_PASSWORD_SUBJECT;
            strBodyKey = PARAMETER_MAIL_LOST_PASSWORD;
            strTitle = PROPERTY_MAIL_LOST_PASSWORD;
        }
        else if ( CONSTANT_EMAIL_PASSWORD_ENCRYPTION_CHANGED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SENDER;
            strSubjectKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SUBJECT;
            strBodyKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED;
            strTitle = PROPERTY_MAIL_PASSWORD_ENCRYPTION_CHANGED;
        }

        ReferenceItem referenceItem = _parameterService.findByKey( strSenderKey, getPlugin( ) );
        String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

        referenceItem = _parameterService.findByKey( strSubjectKey, getPlugin( ) );
        String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

        model.put( PARAMETER_EMAIL_TYPE, strEmailType );
        model.put( MARK_EMAIL_SENDER, strSender );
        model.put( MARK_EMAIL_SUBJECT, strSubject );
        model.put( MARK_EMAIL_BODY, DatabaseTemplateService.getTemplateFromKey( strBodyKey ) );
        model.put( MARK_EMAIL_LABEL, strTitle );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ACCOUNT_LIFE_TIME_EMAIL, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Update an account life time email
     * @param request The request
     * @return The Jsp URL of the process result
     */
    public String doModifyAccountLifeTimeEmails( HttpServletRequest request )
    {
        String strEmailType = request.getParameter( PARAMETER_EMAIL_TYPE );

        String strSenderKey = StringUtils.EMPTY;
        String strSubjectKey = StringUtils.EMPTY;
        String strBodyKey = StringUtils.EMPTY;

        if ( CONSTANT_EMAIL_TYPE_FIRST.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_FIRST_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_FIRST_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_FIRST_ALERT_MAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_OTHER.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_OTHER_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_OTHER_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_OTHER_ALERT_MAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_EXPIRED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_EXPIRED_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_EXPIRED_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_EXPIRATION_MAIL;
        }
        else if ( CONSTANT_EMAIL_TYPE_REACTIVATED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_REACTIVATED_ALERT_MAIL_SENDER;
            strSubjectKey = PARAMETER_REACTIVATED_ALERT_MAIL_SUBJECT;
            strBodyKey = PARAMETER_ACCOUNT_REACTIVATED;
        }
        else if ( CONSTANT_EMAIL_TYPE_LOST_PASSWORD.equalsIgnoreCase( strEmailType ) )
        {
        	strSenderKey = PARAMETER_MAIL_LOST_PASSWORD_SENDER;
        	strSubjectKey = PARAMETER_MAIL_LOST_PASSWORD_SUBJECT;
        	strBodyKey = PARAMETER_MAIL_LOST_PASSWORD;
        }
		else if ( CONSTANT_EMAIL_TYPE_IP_BLOCKED.equalsIgnoreCase( strEmailType ) )
		{
			strSenderKey = PARAMETER_UNBLOCK_USER_MAIL_SENDER;
			strSubjectKey = PARAMETER_UNBLOCK_USER_MAIL_SUBJECT;
			strBodyKey = PARAMETER_UNBLOCK_USER;
		}
		else if ( CONSTANT_EMAIL_PASSWORD_EXPIRED.equalsIgnoreCase( strEmailType ) )
		{
			strSenderKey = PARAMETER_PASSWORD_EXPIRED_MAIL_SENDER;
			strSubjectKey = PARAMETER_PASSWORD_EXPIRED_MAIL_SUBJECT;
			strBodyKey = PARAMETER_NOTIFY_PASSWORD_EXPIRED;
		}
        else if ( CONSTANT_EMAIL_TYPE_LOST_PASSWORD.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_MAIL_LOST_PASSWORD_SENDER;
            strSubjectKey = PARAMETER_MAIL_LOST_PASSWORD_SUBJECT;
            strBodyKey = PARAMETER_MAIL_LOST_PASSWORD;
        }
        else if ( CONSTANT_EMAIL_PASSWORD_ENCRYPTION_CHANGED.equalsIgnoreCase( strEmailType ) )
        {
            strSenderKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SENDER;
            strSubjectKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED_SUBJECT;
            strBodyKey = PARAMETER_MAIL_PASSWORD_ENCRYPTION_CHANGED;
        }

        SecurityUtils.updateParameterValue( _parameterService, getPlugin( ), strSenderKey,
                request.getParameter( MARK_EMAIL_SENDER ) );
        SecurityUtils.updateParameterValue( _parameterService, getPlugin( ), strSubjectKey,
                request.getParameter( MARK_EMAIL_SUBJECT ) );
        DatabaseTemplateService.updateTemplate( strBodyKey, request.getParameter( MARK_EMAIL_BODY ) );

        return JSP_MANAGE_ADVANCED_PARAMETERS;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Plugin getPlugin( )
	{
		Plugin plugin = super.getPlugin( );
		if ( plugin == null )
		{
			plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
		}
		return plugin;
	}
}
