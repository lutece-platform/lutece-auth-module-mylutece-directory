/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * @author lyaete
 *
 */
public interface IMyluteceDirectoryDAO
{
    /**
     * Find user's roles by login
     *
     * @param strLogin the login
     * @param plugin The Plugin using this data access service
     * @return ArrayList the roles key list corresponding to the login
     */
    public abstract ArrayList<String> selectUserRolesFromLogin( String strLogin, Plugin plugin );

    /**
     * Delete roles for a user
     * @param nIdRecord The id of the user
     * @param plugin The Plugin using this data access service
     */
    public abstract void deleteRolesForUser( int nIdRecord, Plugin plugin );

    /**
     * Check if the specified directory is mapped for managing users
     * @param nIdDirectory The directory identifier
     * @param plugin The Plugin using this data access service
     * @return true if the directory is mapped, false else
     */
    public abstract boolean isMapped( int nIdDirectory, Plugin plugin );

    /**
     * Select the list of directory identifier which are mapped as user directory
     * @param plugin The Plugin using this data access service
     * @return The {@link Collection} of id directory
     */
    public abstract Collection<Integer> selectMappedDirectories( Plugin plugin );

    /**
     * Assign a directory as a user directory
     * @param nIdDirectory The directory identifier
     * @param plugin The Plugin using this data access service
     * @return true if the directory is mapped, false else
     */
    public abstract void assignDirectory( int nIdDirectory, Plugin plugin );

    /**
     * Unassign a directory
     * @param nIdDirectory The directory identifier
     * @param plugin The Plugin using this data access service
     */
    public abstract void unAssignDirectory( int nIdDirectory, Plugin plugin );

    /**
     * Unassign all directories
     * @param plugin The Plugin using this data access service
     */
    public abstract void unAssignDirectories( Plugin plugin );

    /**
     * Assign a role to user
     * @param nIdRecord The id of the user
     * @param strRoleKey The key of the role
     * @param plugin The Plugin using this data access service
     */
    public abstract void createRoleForUser( int nIdRecord, String strRoleKey, Plugin plugin );

    /**
     * Find user's groups by login
     *
     * @param strLogin The login
     * @param plugin The Plugin using this data access service
     * @return ArrayList the group key list corresponding to the login
     */
    public abstract ArrayList<String> selectUserGroupsFromLogin( String strLogin, Plugin plugin );

    /**
     * Load the list of DirectoryUsers for a Lutece role
     * @param strRoleKey The role key of DirectoryUser
     * @param plugin The Plugin using this data access service
     * @return The Collection of the DirectoryUsers
     */
    public abstract Collection<String> selectLoginListForRoleKey( String strRoleKey, Plugin plugin );

    /**
     * Delete groups for a user
     * @param nIdRecord The id of the user
     * @param plugin The Plugin using this data access service
     */
    public abstract void deleteGroupsForUser( int nIdRecord, Plugin plugin );

    /**
     * Assign a group to user
     * @param nIdRecord The id of the user
     * @param strGroupKey The key of the group
     * @param plugin The Plugin using this data access service
     */
    public abstract void createGroupForUser( int nIdRecord, String strGroupKey, Plugin plugin );
}
