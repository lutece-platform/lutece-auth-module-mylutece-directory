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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.AttributeMapping;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.AttributeMappingService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IAttributeMappingService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IMyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to manage roles features ( manage, create, modify, remove )
 */
public class MyluteceDirectoryJspBean extends PluginAdminPageJspBean
{
    // Right
    public static final String RIGHT_MANAGE_MYLUTECE_DIRECTORY = "MYLUTECE_DIRECTORY_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE_DIRECTORY = "admin/plugins/mylutece/modules/directory/manage_directory.html";
    private static final String TEMPLATE_MANAGE_MAPPINGS = "admin/plugins/mylutece/modules/directory/manage_mappings.html";
    private static final String TEMPLATE_CREATE_MAPPING = "admin/plugins/mylutece/modules/directory/create_mapping.html";

    // Jsp
    private static final String JSP_URL_PREFIX = "jsp/admin/plugins/mylutece/modules/directory/";
    private static final String JSP_URL_MANAGE_DIRECTORY = "ManageDirectory.jsp";
    private static final String JSP_URL_REMOVE_DIRECTORY = "DoRemoveDirectory.jsp";
    private static final String JSP_URL_REMOVE_MAPPING = "DoRemoveMapping.jsp";
    private static final String JSP_URL_MANAGE_MAPPINGS = "ManageMappings.jsp";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "module.mylutece.directory.items_per_page";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_MAPPINGS = "module.mylutece.directory.manage_mappings.page_title";
    private static final String PROPERTY_REF_ITEM_UNASSOCIATED_WORKFLOW_ACTION = "module.mylutece.directory.manage_mapping.unassociated_workflow_action";
    private static final String PROPERTY_PAGE_TITLE_CREATE_MAPPING = "module.mylutece.directory.create_mapping.page_title";
   

    private static final String MESSAGE_CANNOT_UNASSIGN_DIRECTORY = "module.mylutece.directory.message.cannot_unassign_directory";
    private static final String MESSAGE_CONFIRMATION_REMOVE_DIRECTORY = "module.mylutece.directory.message.confirm_remove_directory";
    private static final String MESSAGE_MAPPING_EXIST = "module.mylutece.directory.message.mapping_exist";
    private static final String MESSAGE_CONFIRMATION_REMOVE_MAPPING = "module.mylutece.directory.message.confirm_remove_mapping";

    // Parameters
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_WORKFLOW_ACTION_ID = "workflow_action_id";
    
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ATTRIBUTE_KEY = "attribute_key";

    // Markers
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_DIRECTORY_DISPLAY_LIST = "directory_display_list";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_MYLUTECE_DIRECTORY = "mylutece_directory";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ID_DIRECTORY = "id_directory";
    private static final String MARK_ID_ENTRY = "id_entry";
    private static final String MARK_ATTRIBUTES_LIST = "attributes_list";
    private static final String MARK_WORKFLOW_ACTIONS_LIST = "workflow_actions_list";
    private static final String MARK_WORKFLOW_ACTION_SELECTED = "workflow_action_selected";
    private static final String MARK_MAPPINGS_DISPLAY_LIST = "mapping_display_list";
    
    private static final String MARK_MAPPING = "mapping";
    private static final String MARK_EMPTY_LIST = "empty_list";

    private static final String PREFIX_LUTECE_USER = "user.";


    // Session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexDirectory;
    private int _nItemsPerPageDirectory;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private IMyluteceDirectoryService _myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );
    private IAttributeMappingService _attributeMappingService = SpringContextService.getBean( AttributeMappingService.BEAN_SERVICE );
  
    /**
     * Creates a new DirectoryJspBean object.
     */
    public MyluteceDirectoryJspBean(  )
    {
    }

    /**
     * Return management directory ( list of directory )
     * @param request The Http request
     * @return Html directory
     */
    public String getManageDirectory( HttpServletRequest request )
    {
        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        _strCurrentPageIndexDirectory = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDirectory );
        _nItemsPerPageDirectory = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageDirectory, _nDefaultItemsPerPage );

        if ( StringUtils.isNotBlank( strWorkGroup ) )
        {
            _strWorkGroup = strWorkGroup;
        }

        //build Filter
        DirectoryFilter filter = new DirectoryFilter(  );
        filter.setWorkGroup( _strWorkGroup );

        List<Directory> listDirectory = _myluteceDirectoryService.getListDirectories( filter, getUser(  ) );

        List<Map<String, Object>> listDirectoryDisplay = new ArrayList<Map<String, Object>>(  );

        for ( Directory directory : listDirectory )
        {
            Map<String, Object> directoryDisplay = new HashMap<String, Object>(  );
            directoryDisplay.put( MARK_DIRECTORY, directory );
            directoryDisplay.put( MARK_MYLUTECE_DIRECTORY,
                _myluteceDirectoryService.isDirectoryMapped( directory.getIdDirectory(  ), getPlugin(  ) ) );
            listDirectoryDisplay.add( directoryDisplay );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        LocalizedPaginator<Map<String, Object>> paginator = new LocalizedPaginator<Map<String, Object>>( listDirectoryDisplay,
                _nItemsPerPageDirectory, getJspManageDirectory( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDirectory, getLocale(  ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageDirectory );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_DIRECTORY_DISPLAY_LIST, paginator.getPageItems(  ) );

        setPageTitleProperty( DirectoryUtils.EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return management mappings
     *
     * @param request The Http request
     * @return Html directory
     */
    public String getManageMappings( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_MAPPINGS );
        
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
          if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            return getHomeUrl( request );
        }
        
      

        int nIdDirectory = Integer.parseInt( strIdDirectory );
        
        Directory directory= _myluteceDirectoryService.getDirectory(nIdDirectory);
        
        if(directory == null)
        {
        	return getHomeUrl( request );
        }
        Map<String, Object> model = new HashMap<String, Object>(  );
        
        if ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL &&  WorkflowService.getInstance( ).isAvailable( ) )
		{
        	
        	
            Integer nIdModifyActionSelected=MyluteceDirectoryHome.findWorkflowModifyAction(nIdDirectory, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ));
        
        	List<Action> listWorkflowAction= _myluteceDirectoryService.getActionsWorkflowList(directory.getIdWorkflow( ) );
        	 if ( listWorkflowAction != null )
             {   ReferenceList refListActions=new ReferenceList();
             	refListActions.addItem(-1,  I18nService.getLocalizedString(PROPERTY_REF_ITEM_UNASSOCIATED_WORKFLOW_ACTION,getLocale()) );
                  for ( Action action : listWorkflowAction )
                 {
                    
                     refListActions.addItem(Integer.toString( action.getId(  ) ),  action.getName(  ) );
                 }
                 model.put(MARK_WORKFLOW_ACTIONS_LIST,refListActions);
                 model.put(MARK_WORKFLOW_ACTION_SELECTED,nIdModifyActionSelected !=null ? nIdModifyActionSelected:-1);
                 
             }
        	
		}
		
  
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFirstLevel = _myluteceDirectoryService.getListEntries( filter );

        filter.setIsEntryParentNull( EntryFilter.ALL_INT );

        Collection<Map<String, Object>> listEntryDisplay = new ArrayList<Map<String, Object>>(  );
        
        for ( IEntry entry : listEntryFirstLevel )
        {
            if ( !entry.getEntryType(  ).getGroup(  ) )
            {
                Map<String, Object> listEntry = new HashMap<String, Object>(  );
                listEntry.put( MARK_ENTRY, _myluteceDirectoryService.getEntry( entry.getIdEntry(  ) ) );
                listEntry.put( MARK_MAPPING,
                    _attributeMappingService.getAttributeMapping( entry.getIdEntry(  ), getPlugin(  ) ) );
                listEntryDisplay.add( listEntry );
            }

            filter.setIdEntryParent( entry.getIdEntry(  ) );

            List<IEntry> listChildren = _myluteceDirectoryService.getListEntries( filter );

            for ( IEntry entryChild : listChildren )
            {
                HashMap<String, Object> listEntryChild = new HashMap<String, Object>(  );
                listEntryChild.put( MARK_ENTRY, _myluteceDirectoryService.getEntry( entryChild.getIdEntry(  ) ) );
                listEntryChild.put( MARK_MAPPING,
                    _attributeMappingService.getAttributeMapping( entryChild.getIdEntry(  ), getPlugin(  ) ) );
                listEntryDisplay.add( listEntryChild );
            }
        }

        model.put( MARK_MAPPINGS_DISPLAY_LIST, listEntryDisplay );
        model.put( MARK_ATTRIBUTES_LIST, getAttributeList(  ) );
        model.put( MARK_ID_DIRECTORY, nIdDirectory );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_MAPPINGS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Return mapping creation page
     *
     * @param request The Http request
     * @return Html directory
     */
    public String getCreateMappings( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_MAPPING );

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            return getHomeUrl( request );
        }

        int nIdDirectory = Integer.parseInt( strIdDirectory );

        Map<String, Object> model = new HashMap<String, Object>(  );

        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntries = new ArrayList<IEntry>(  );
        List<IEntry> listEntriesFirstLevel = _myluteceDirectoryService.getListEntries( filter );

        filter.setIsEntryParentNull( EntryFilter.ALL_INT );

        for ( IEntry entry : listEntriesFirstLevel )
        {
            if ( !entry.getEntryType(  ).getGroup(  ) )
            {
                if ( _attributeMappingService.getAttributeMapping( entry.getIdEntry(  ), getPlugin(  ) ) == null )
                {
                    listEntries.add( _myluteceDirectoryService.getEntry( entry.getIdEntry(  ) ) );
                }
            }

            filter.setIdEntryParent( entry.getIdEntry(  ) );

            List<IEntry> listChildren = _myluteceDirectoryService.getListEntries( filter );

            for ( IEntry entryChild : listChildren )
            {
                if ( _attributeMappingService.getAttributeMapping( entryChild.getIdEntry(  ), getPlugin(  ) ) == null )
                {
                    listEntries.add( _myluteceDirectoryService.getEntry( entryChild.getIdEntry(  ) ) );
                }
            }
        }

        // Attribute list
        ReferenceList listAttributes = getAttributeList(  );
        boolean bIsEmptyList = ( listEntries == null ) || listEntries.isEmpty(  ) || ( listAttributes == null ) ||
            listAttributes.isEmpty(  );
        model.put( MARK_EMPTY_LIST, bIsEmptyList );
        model.put( MARK_ID_DIRECTORY, strIdDirectory );
        model.put( MARK_ENTRY_LIST, listEntries );
        model.put( MARK_ATTRIBUTES_LIST, listAttributes );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_MAPPING, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the attribute list
     * @return the attribute list
     */
    private ReferenceList getAttributeList(  )
    {
        ReferenceList attributesList = new ReferenceList(  );

        try
        {
            for ( Field field : LuteceUser.class.getFields(  ) )
            {
				try
				{
					String strValue = ( String ) field.get( LuteceUser.class );

	                if ( strValue.startsWith( PREFIX_LUTECE_USER ) && ( _attributeMappingService.getAttributeMappingByAttributeKey( strValue, getPlugin( ) ) == null ) )
					{
						attributesList.addItem( strValue, field.getName( ) );
					}
				}
				catch ( ClassCastException e )
				{
				}
            }
        }
        catch ( IllegalArgumentException e )
        {
            AppLogService.error( "Error when execute getting LuteceUser Constants : " + e.getMessage(  ) );
        }
        catch ( IllegalAccessException e )
        {
            AppLogService.error( "Error when execute getting LuteceUser Constants : " + e.getMessage(  ) );
        }

        return attributesList;
    }

    /**
     * Process mapping's creation
     *
     * @param request The Http request
     * @return The mapping's Displaying Url
     */
    public String doCreateMapping( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strAttributeKey = request.getParameter( PARAMETER_ATTRIBUTE_KEY );

        if ( StringUtils.isBlank( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) ||
                StringUtils.isBlank( strAttributeKey ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( _attributeMappingService.getAttributeMappingByAttributeKey( strAttributeKey, getPlugin(  ) ) != null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_EXIST, AdminMessage.TYPE_STOP );
        }

        AttributeMapping attributeMapping = new AttributeMapping(  );
        attributeMapping.setAttributeKey( strAttributeKey );
        attributeMapping.setIdEntry( Integer.parseInt( strIdEntry ) );
        _attributeMappingService.doCreate( attributeMapping, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_MAPPINGS );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return url.getUrl(  );
    }
    
    
    /**
     * Assign modification Action
     *
     * @param request The Http request
     * @return The mapping's Displaying Url
     */
    public String doAssignModificationAction( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdAction = request.getParameter( PARAMETER_WORKFLOW_ACTION_ID );
        int nIdDirectory=DirectoryUtils.convertStringToInt(strIdDirectory);
        int nIdAction=DirectoryUtils.convertStringToInt(strIdAction);
                		
        if(nIdDirectory != DirectoryUtils.CONSTANT_ID_NULL )
        {
        	if( nIdAction != DirectoryUtils.CONSTANT_ID_NULL)
        	{
        		MyluteceDirectoryHome.assignWorkflowModifyAction(nIdDirectory,nIdAction, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ));
        	}
        	else
        	{
        		MyluteceDirectoryHome.unAssignWorkflowModifyAction(nIdDirectory, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ));
        	}
 
        }
        
        UrlItem url = new UrlItem( JSP_URL_MANAGE_MAPPINGS );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return url.getUrl(  );
    }
    


    /**
     * Get the user removal message
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doConfirmRemoveMapping( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isBlank( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ID_DIRECTORY, request.getParameter( PARAMETER_ID_DIRECTORY ) );
        model.put( MARK_ID_ENTRY, strIdEntry );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_MAPPING,
            JSP_URL_PREFIX + JSP_URL_REMOVE_MAPPING, AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Processes the mapping deletion
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doRemoveMapping( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( StringUtils.isBlank( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        _attributeMappingService.doRemove( nIdEntry, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_MAPPINGS );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return url.getUrl(  );
    }

    /**
     * Get the directory removal message
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doConfirmRemoveDirectory( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Collection<MyluteceDirectoryUser> listMyluteceDirectoryUsers = _myluteceDirectoryService.getMyluteceDirectoryUsers( getPlugin(  ) );

        if ( ( listMyluteceDirectoryUsers != null ) && !listMyluteceDirectoryUsers.isEmpty(  ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_UNASSIGN_DIRECTORY, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ID_DIRECTORY, strIdDirectory );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_DIRECTORY,
            JSP_URL_PREFIX + JSP_URL_REMOVE_DIRECTORY, AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Processes the directory deletion
     * @param request The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doRemoveDirectory( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdDirectory = Integer.parseInt( strIdDirectory );
        _attributeMappingService.doRemoveAll( getPlugin(  ) );
        _myluteceDirectoryService.doUnassignDirectory( nIdDirectory, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE_DIRECTORY );

        return url.getUrl(  );
    }

    /**
     * Process directory assignation
     *
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doAssignDirectory( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( StringUtils.isBlank( strIdDirectory ) || !StringUtils.isNumeric( strIdDirectory ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Collection<MyluteceDirectoryUser> listMyluteceDirectoryUsers = _myluteceDirectoryService.getMyluteceDirectoryUsers( getPlugin(  ) );

        if ( ( listMyluteceDirectoryUsers != null ) && !listMyluteceDirectoryUsers.isEmpty(  ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_UNASSIGN_DIRECTORY, AdminMessage.TYPE_STOP );
        }

        int nIdDirectory = Integer.parseInt( strIdDirectory );

        _myluteceDirectoryService.doUnassignDirectories( getPlugin(  ) );
        _attributeMappingService.doRemoveAll( getPlugin(  ) );
        _myluteceDirectoryService.doAssignDirectory( nIdDirectory, getPlugin(  ) );

        return JSP_URL_MANAGE_DIRECTORY;
    }

    /**
     * return url of the jsp manage directory
     * @param request The HTTP request
     * @return url of the jsp manage directory
     */
    private String getJspManageDirectory( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_URL_PREFIX + JSP_URL_MANAGE_DIRECTORY;
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
