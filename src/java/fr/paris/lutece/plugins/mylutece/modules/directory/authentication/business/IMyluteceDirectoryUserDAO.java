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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 *
 * @author Etienne
 */
public interface IMyluteceDirectoryUserDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param directoryUser The directoryUser object
     * @param strPassword The user password
     * @param plugin The Plugin using this data access service
     */
    void insert( MyluteceDirectoryUser directoryUser, String strPassword, Plugin plugin );

    /**
     * Load the data of DirectoryUser from the table
     *
     * @param nIdRecord The identifier of User
     * @param plugin The Plugin using this data access service
     * @return the instance of the DirectoryUser
     */
    MyluteceDirectoryUser load( int nIdRecord, Plugin plugin );

    /**
     * Delete a record from the table
     * @param directoryUser The directoryUser object
     * @param plugin The Plugin using this data access service
     */
    void delete( MyluteceDirectoryUser directoryUser, Plugin plugin );

    /**
     * Update the record in the table
     * @param directoryUser The reference of directoryUser
     * @param plugin The Plugin using this data access service
     */
    void store( MyluteceDirectoryUser directoryUser, Plugin plugin );

    /**
     * Update the record in the table
     * @param directoryUser The reference of directoryUser
     * @param strNewPassword The new password to store
     * @param plugin The Plugin using this data access service
     */
    void updatePassword( MyluteceDirectoryUser directoryUser, String strNewPassword, Plugin plugin );

    /**
     * Load the password of the specified user
     *
     * @param nIdRecord The Primary key of the directoryUser
     * @param plugin The current plugin using this method
     * @return String the user password
     */
    String selectPasswordByPrimaryKey( int nIdRecord, Plugin plugin );

    /**
     * Load the list of directoryUsers
     * @param plugin The Plugin using this data access service
     * @return The Collection of the directoryUsers
     */
    Collection<MyluteceDirectoryUser> selectDirectoryUserList( Plugin plugin );

    /**
     * Load the list of DirectoryUsers for a login
     * @param strLogin The login of DirectoryUser
     * @param plugin The Plugin using this data access service
     * @return The Collection of the DirectoryUsers
     */
    Collection<MyluteceDirectoryUser> selectDirectoryUserListForLogin( String strLogin, Plugin plugin );    
   

    /**
     * Check the password for a DirectoryUser
     *
     * @param strLogin The user login of DirectoryUser
     * @param strPassword The password of DirectoryUser
     * @param plugin The Plugin using this data access service
     * @return true if password is ok
     */
    boolean checkPassword( String strLogin, String strPassword, Plugin plugin );

    /**
     * Check if user is activated
     *
     * @param strLogin The user login of DirectoryUser     
     * @param plugin The Plugin using this data access service
     * @return true if user is activated
     */
	boolean checkActivated(String strLogin, Plugin plugin);
}
