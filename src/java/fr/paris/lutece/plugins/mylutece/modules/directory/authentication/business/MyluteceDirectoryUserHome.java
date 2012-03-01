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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business;

import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for MyluteceDirectoryUser objects
 */
public final class MyluteceDirectoryUserHome
{
    // Static variable pointed at the DAO instance
    private static IMyluteceDirectoryUserDAO _dao = (IMyluteceDirectoryUserDAO) SpringContextService.getPluginBean( "mylutece-directory",
            "myluteceDirectoryUserDAO" );

    //Properties
    private static final String PROPERTY_MYLUTECE_LUTECE_USER_ATTRIBUTE_KEY_EMAIL = "mylutece-directory.luteceUser.attribute.email";

    // Others
    private static final String COMMA_SEPARATOR = ",";
    private static final String DEFAULT_LUTECE_USER_ATTRIBUTE_KEY_EMAIL = "user.home-info.online.email,user.business-info.online.email";

    /**
     * Private constructor - this class need not be instantiated
     */
    private MyluteceDirectoryUserHome(  )
    {
    }

    /**
     * Creation of an instance of directoryUser
     *
     * @param directoryUser The instance of the DirectoryUser which contains the informations to store
     * @param strPassword The user's password
     * @param plugin The current plugin using this method
     * @return The  instance of DirectoryUser which has been created with its primary key.
     */
    public static MyluteceDirectoryUser create( MyluteceDirectoryUser directoryUser, String strPassword, Plugin plugin )
    {
        _dao.insert( directoryUser, strPassword, plugin );

        return directoryUser;
    }

    /**
     * Update of the directoryUser which is specified in parameter
     *
     * @param directoryUser The instance of the DirectoryUser which contains the data to store
     * @param plugin The current plugin using this method
     * @return The instance of the  DirectoryUser which has been updated
     */
    public static MyluteceDirectoryUser update( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        _dao.store( directoryUser, plugin );

        return directoryUser;
    }

    /**
     * Update of the directoryUser which is specified in parameter
     *
     * @param directoryUser The instance of the DirectoryUser which contains the data to store
     * @param strNewPassword The new password to store
     * @param plugin The current plugin using this method
     * @return The instance of the  DirectoryUser which has been updated
     */
    public static MyluteceDirectoryUser updatePassword( MyluteceDirectoryUser directoryUser, String strNewPassword,
        Plugin plugin )
    {
        _dao.updatePassword( directoryUser, strNewPassword, plugin );

        return directoryUser;
    }

    /**
     * Remove the directoryUser whose identifier is specified in parameter
     *
     * @param directoryUser The DirectoryUser object to remove
     * @param plugin The current plugin using this method
     */
    public static void remove( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        _dao.delete( directoryUser, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a DirectoryUser whose identifier is specified in parameter
     *
     * @param nIdRecord The identifier of User
     * @param plugin The current plugin using this method
     * @return An instance of DirectoryUser
     */
    public static MyluteceDirectoryUser findByPrimaryKey( int nIdRecord, Plugin plugin )
    {
        return _dao.load( nIdRecord, plugin );
    }

    /**
     * Returns a collection of DirectoryUser objects
     * @param plugin The current plugin using this method
     * @return A collection of DirectoryUser
     */
    public static Collection<MyluteceDirectoryUser> findDirectoryUsersList( Plugin plugin )
    {
        return _dao.selectDirectoryUserList( plugin );
    }

    /**
     * Returns a collection of DirectoryUser objects for a login
     *
     * @param strLogin The login of the databseUser
     * @param plugin The current plugin using this method
     * @return A collection of DirectoryUser
     */
    public static Collection<MyluteceDirectoryUser> findDirectoryUsersListForLogin( String strLogin, Plugin plugin )
    {
        return _dao.selectDirectoryUserListForLogin( strLogin, plugin );
    }
   
    
    /**
     * Returns a collection of DirectoryUser objects for a LuteceUser Attribute
     *
     * @param strAttributeKeys The attribute keys list of the DirectoryUser (comma separated)
     * @param strAttributeValue The attribute value of the DirectoryUser
     * @param plugin The current plugin using this method
     * @param locale The locale
     * @return A collection of DirectoryUser
     */
    public static Collection<MyluteceDirectoryUser> findDirectoryUsersListForAttributeValue( String strAttributeKeys,
        String strAttributeValue, Plugin plugin, Locale locale )
    {
        Collection<MyluteceDirectoryUser> listDirectoryUsers = new ArrayList<MyluteceDirectoryUser>(  );
        Collection<AttributeMapping> mappinglist = new ArrayList<AttributeMapping>(  );

        // Select a mapping
        for ( String strMappingKey : strAttributeKeys.split( COMMA_SEPARATOR ) )
        {
            AttributeMapping mappingTemp = AttributeMappingHome.findByAttributeKey( strMappingKey, plugin );

            if ( mappingTemp != null )
            {
                mappinglist.add( mappingTemp );
            }
        }

        if ( mappinglist.size(  ) == 0 )
        {
            return null;
        }

        // For each mapping (of entry) search with the specified value
        for ( AttributeMapping mapping : mappinglist )
        {
            for ( Integer nIdRecord : getIdRecordListFromIdEntry( mapping, strAttributeValue, plugin, locale ) )
            {
                MyluteceDirectoryUser directoryUser = MyluteceDirectoryUserHome.findByPrimaryKey( nIdRecord, plugin );

                if ( directoryUser != null )
                {
                    listDirectoryUsers.add( directoryUser );
                }
            }
        }

        return listDirectoryUsers;
    }

    /**
     * Returns a collection of DirectoryUser objects for an email
     *
     * @param strEmail The email of the DirectoryUser
     * @param plugin The current plugin using this method
     * @param locale The locale
     * @return A collection of DirectoryUser
     */
    public static Collection<MyluteceDirectoryUser> findDirectoryUsersListForEmail( String strEmail, Plugin plugin,
        Locale locale )
    {
        String strAttributeKeys = AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LUTECE_USER_ATTRIBUTE_KEY_EMAIL,
                DEFAULT_LUTECE_USER_ATTRIBUTE_KEY_EMAIL );

        return findDirectoryUsersListForAttributeValue( strAttributeKeys, strEmail, plugin, locale );
    }

    /**
     * Load the password of the specified user
     *
     * @param nIdRecord The Primary key of the directoryUser
     * @param plugin The current plugin using this method
     * @return String the user password
     */
    public static String findPasswordByPrimaryKey( int nIdRecord, Plugin plugin )
    {
        return _dao.selectPasswordByPrimaryKey( nIdRecord, plugin );
    }

    /**
     * Check the password for a DirectoryUser
     *
     * @param strLogin The user login of DirectoryUser
     * @param strPassword The password of DirectoryUser
     * @param plugin The Plugin using this data access service
     * @return true if password is ok
     */
    public static boolean checkPassword( String strLogin, String strPassword, Plugin plugin )
    {
        return _dao.checkPassword( strLogin, strPassword, plugin );
    }
    
    /**
     * Check if user is activated
     *
     * @param strLogin The user login of DirectoryUser     
     * @param plugin The Plugin using this data access service
     * @return true if user is activated
     */
    public static boolean checkActivated( String strLogin, Plugin plugin )
    {
        return _dao.checkActivated( strLogin, plugin );
    }

    // ****************************** Private methods ******************************

    /**
     * Get the Id record list from a mapping (with Id entry) and entry value
     *
     * @param mapping The {@link AttributeMapping} object
     * @param strAttributeValue the value of entry
     * @param plugin the {@link Plugin}
     * @param locale The {@link Locale}
     * @return a {@link Collection} of {@link Integer}
     */
    private static Collection<Integer> getIdRecordListFromIdEntry( AttributeMapping mapping, String strAttributeValue,
        Plugin plugin, Locale locale )
    {
        // Get the directory id
        Collection<Integer> mappedDirectories = MyluteceDirectoryHome.findMappedDirectories( plugin );

        if ( mappedDirectories.size(  ) != 1 )
        {
            return null;
        }

        //Search the RecordField list corresponding to entry Id
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( mappedDirectories.iterator(  ).next(  ).intValue(  ) );
        filter.setIdEntry( mapping.getIdEntry(  ) );

        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( filter, directoryPlugin );

        // Set the list of Id record who entry value match with the specified attribute value
        Collection<Integer> listIdRecord = new ArrayList<Integer>(  );

        for ( RecordField field : listRecordField )
        {
            if ( field.getEntry(  ).convertRecordFieldValueToString( field, locale, false, false ).equalsIgnoreCase( strAttributeValue ) )
            {
                listIdRecord.add( field.getRecord(  ).getIdRecord(  ) );
            }
        }

        return listIdRecord;
    }
    
    /**
     * Get the list of emails given an ID record
     * @param nIdRecord the ID record
     * @param plugin Plugin
     * @return the list of emails
     */
    public static List<String> getEmails( int nIdRecord, Plugin plugin, Locale locale )
    {
    	List<String> listEmails = new ArrayList<String>(  );
    	String strAttributeKeys = AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LUTECE_USER_ATTRIBUTE_KEY_EMAIL,
                DEFAULT_LUTECE_USER_ATTRIBUTE_KEY_EMAIL );
        Collection<AttributeMapping> mappinglist = new ArrayList<AttributeMapping>(  );

        // Select a mapping
        for ( String strMappingKey : strAttributeKeys.split( COMMA_SEPARATOR ) )
        {
            AttributeMapping mappingTemp = AttributeMappingHome.findByAttributeKey( strMappingKey, plugin );

            if ( mappingTemp != null )
            {
                mappinglist.add( mappingTemp );
            }
        }
        Collection<Integer> mappedDirectories = MyluteceDirectoryHome.findMappedDirectories( plugin );

        // No mapping => no email
        if ( mappinglist.size(  ) == 0 || mappedDirectories.size(  ) != 1 )
        {
            return null;
        }
        
        for ( AttributeMapping mapping : mappinglist )
        {
        	//Search the RecordField list corresponding to entry Id
            RecordFieldFilter filter = new RecordFieldFilter(  );
            filter.setIdDirectory( mappedDirectories.iterator(  ).next(  ).intValue(  ) );
            filter.setIdEntry( mapping.getIdEntry(  ) );
            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( filter, plugin );
            for ( RecordField recordField : listRecordFields )
            {
            	if ( recordField.getRecord(  ).getIdRecord(  ) == nIdRecord )
            	{
            		listEmails.add( recordField.getEntry(  ).convertRecordFieldValueToString( recordField, locale, false, false ) );
            	}
            }
        }
        
        return listEmails;
    }
}
