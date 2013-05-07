/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.BaseUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.FormErrors;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;


/**
 *
 * DatabaseService
 *
 */
public interface IMyluteceDirectoryService
{
    /**
     * Find the user in the DB
     * @param strUserName the user name
     * @param authenticationService the authentication service
     * @param bGetAdditionnalInfo true if it must get the additionnal info of the user
     * @return an instance of {@link BaseUser}
     */
    BaseUser getUserByLogin( String strUserName, LuteceAuthentication authenticationService, boolean bGetAdditionnalInfo );

    /**
     * Do modify the password
     * @param user the DatabaseUser
     * @param strPassword the new password
     * @param plugin the plugin
     */
    public void doModifyResetPassword( MyluteceDirectoryUser user, boolean bNewValue, Plugin plugin );

    /**
     * Get the list of users
     * @param authenticationService the authentication service
     * @return a collection of {@link LuteceUser}
     */
    Collection<LuteceUser> getUsers( LuteceAuthentication authenticationService );

    /**
     * Find the mapped directories
     * @param plugin The Plugin using this data access service
     * @return a collection of directory
     */
    Collection<Integer> getMappedDirectories( Plugin plugin );

    /**
     * Get the directory
     * @param nIdDirectory the id directory
     * @return the id directory
     */
    Directory getDirectory( int nIdDirectory );

    /**
     * Get the list of directories from a given filter
     * @param filter the filter
     * @param adminUser the admin user for filtering list purpose
     * @return a list of {@link Directory}
     */
    List<Directory> getListDirectories( DirectoryFilter filter, AdminUser adminUser );

    /**
     * Get the list of entrie of a given filter.
     * @param entryFilter the filter
     * @return a list of {@link IEntry}
     */
    List<IEntry> getListEntries( EntryFilter entryFilter );

    /**
     * Get the entry from a given id entry
     * @param nIdEntry the id entry
     * @return an {@link IEntry}
     */
    IEntry getEntry( int nIdEntry );

    /**
     * Get the record from a given id record
     * @param nIdRecord the id record
     * @param bGetRecordFields true if it must also get the associated record fields, false otherwise
     * @return a {@link Record}
     */
    Record getRecord( int nIdRecord, boolean bGetRecordFields );

    /**
     * Get the mylutece directory user
     * @param nIdRecord the id record
     * @param plugin the plugin
     * @return a {@link MyluteceDirectoryUser}
     */
    MyluteceDirectoryUser getMyluteceDirectoryUser( int nIdRecord, Plugin plugin );

    /**
     * Get the list of mylutece directory user for login
     * @param strLogin the login
     * @param plugin the plugin
     * @return a collection of {@link MyluteceDirectoryUser}
     */
    Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsersForLogin( String strLogin, Plugin plugin );

    /**
     * Get the list of mylutece directory users
     * @param plugin the plugin
     * @return a collection of {@link MyluteceDirectoryUser}
     */
    Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsers( Plugin plugin );

    /**
     * Get the list of mylutece directory users from a given email
     * @param strEmail the email
     * @param plugin the plugin
     * @param locale the locale
     * @return a collection of {@link MyluteceDirectoryUser}
     */
    Collection<MyluteceDirectoryUser> getMyluteceDirectoryUsersForEmail( String strEmail, Plugin plugin, Locale locale );

    /**
     * Get the filtered list of admin users
     * @param listUserIds the initial list of user ids
     * @param request HttpServletRequest
     * @param model map
     * @param url URL of the current interface
     * @return The filtered list of admin users
     */
    List<Integer> getFilteredUsersInterface( List<Integer> listUserIds, HttpServletRequest request,
        Map<String, Object> model, UrlItem url );

    /**
     * Get the map of id entry - list record fields from a given record
     * @param record the record
     * @return a map of id entry - list record fields
     */
    Map<String, List<RecordField>> getMapIdEntryListRecordField( Record record );

    /**
     * Get the directory record data
     * @param request the HTTP request
     * @param record the record
     * @param pluginDirectory the plugin
     * @param locale the locale
     * @param formErrors the form errors
     */
    void getDirectoryRecordData( HttpServletRequest request, Record record, Plugin pluginDirectory, Locale locale,
        FormErrors formErrors );

    /**
     * Build the advanced parameters management
     * @param user the admin user
     * @return The model for the advanced parameters
     */
    Map<String, Object> getManageAdvancedParameters( AdminUser user );

    /**
     * Get the list of emails of the given user
     * @param user the user
     * @param plugin the plugin
     * @param locale the locale
     * @return the list of emails
     */
    List<String> getListEmails( MyluteceDirectoryUser user, Plugin plugin, Locale locale );

    /**
     * Get the list of user roles from a given login
     * @param strLogin the login
     * @param plugin the plugin
     * @return the list of user roles
     */
    List<String> getUserRolesFromLogin( String strLogin, Plugin plugin );

    /**
     * Find the password from a given id record
     * @param nIdRecord the id record
     * @param plugin the plugin
     * @return the password
     */
    String getPasswordByIdRecord( int nIdRecord, Plugin plugin );

    /**
     * Get the id directory from a given id record
     * @param nIdRecord the id record
     * @return the id directory
     */
    int getIdDirectoryByIdRecord( int nIdRecord );

    /**
     * Check if the given id directory is mapped to mylutece
     * @param nIdDirectory the id directory
     * @param plugin the plugin
     * @return true if it is mapped, false otherwise
     */
    boolean isDirectoryMapped( int nIdDirectory, Plugin plugin );

    /**
     * Do create a mylutece directory user
     * @param myluteceDirectoryUser the user
     * @param strUserPassword the password
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doCreateMyluteceDirectoryUser( MyluteceDirectoryUser myluteceDirectoryUser, String strUserPassword,
        Plugin plugin );

    /**
     * Do modify a mylutece directory user
     * @param myluteceDirectoryUser the user
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doModifyMyluteceDirectoryUser( MyluteceDirectoryUser myluteceDirectoryUser, Plugin plugin );

    /**
     * Do modify the user password
     * @param myluteceDirectoryUser the user
     * @param strUserPassword the password
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doModifyPassword( MyluteceDirectoryUser myluteceDirectoryUser, String strUserPassword, Plugin plugin );

    /**
     * Do assign role for user
     * @param user the user
     * @param roleArray the roles
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doAssignRoleUser( MyluteceDirectoryUser user, String[] roleArray, Plugin plugin );

    /**
     * Do remove the mylutece directory user
     * @param directoryUser the directory user
     * @param plugin the plugin
     * @param bRemoveAdditionnalInfo true if it must remove the additionnal info of the user
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doRemoveMyluteceDirectoryUser( MyluteceDirectoryUser directoryUser, Plugin plugin,
        boolean bRemoveAdditionnalInfo );

    /**
     * Do unassign the directory from MyLutece
     * @param nIdDirectory the id directory
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doUnassignDirectory( int nIdDirectory, Plugin plugin );

    /**
     * Do unassign all directories from mylutece
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doUnassignDirectories( Plugin plugin );

    /**
     * Do assign the directory
     * @param nIdDirectory the id directory
     * @param plugin the plugin
     */
    @Transactional( "mylutece-directory.transactionManager" )
    void doAssignDirectory( int nIdDirectory, Plugin plugin );

    /**
     * Change the password of every user, and email them
     * @param strBaseURL The base url of the application
     * @param plugin The plugin
     * @param locale The locale
     */
    void changeUserPasswordAndNotify( String strBaseURL, Plugin plugin, Locale locale );

    /**
     * Check whether a user must change his password
     * @param databaseUser The user to check
     * @param plugin The plugin
     * @return True if a user must change his password, false otherwise.
     */
    boolean mustUserChangePassword( LuteceUser databaseUser, Plugin plugin );

    /**
     * Log a password change in the password history
     * @param strPassword New password of the user
     * @param nUserId Id of the user
     * @param plugin The plugin
     */
    void doInsertNewPasswordInHistory( String strPassword, int nUserId, Plugin plugin );

    /**
     * Update the user expiration date with new values.
     * @param nIdUser Id of the user to update
     * @param plugin The plugin
     */
    void updateUserExpirationDate( int nIdUser, Plugin plugin );

}
