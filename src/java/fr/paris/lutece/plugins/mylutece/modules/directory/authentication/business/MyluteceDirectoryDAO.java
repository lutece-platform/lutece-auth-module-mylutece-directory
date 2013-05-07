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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class provides Data Access methods for authentication (role retrieval).
 *
 */
public class MyluteceDirectoryDAO implements IMyluteceDirectoryDAO
{
    private static final String SQL_QUERY_IS_MAPPED = "SELECT count(*) FROM mylutece_directory_directory WHERE id_directory = ? ";
    private static final String SQL_QUERY_SELECT_MAPPED_DIRECTORY = "SELECT id_directory FROM mylutece_directory_directory ";
    private static final String SQL_QUERY_ASSIGN_DIRECTORY = "INSERT INTO mylutece_directory_directory ( id_directory ) VALUES ( ? ) ";
    private static final String SQL_QUERY_UNASSIGN_DIRECTORY = "DELETE FROM mylutece_directory_directory WHERE id_directory = ? ";
    private static final String SQL_QUERY_UNASSIGN_DIRECTORIES = "DELETE FROM mylutece_directory_directory ";
    private static final String SQL_QUERY_FIND_ROLES_FROM_LOGIN = "SELECT b.role_key FROM mylutece_directory_user a, mylutece_directory_user_role b" +
        " WHERE a.id_record = b.id_record AND a.user_login like ? ";
    private static final String SQL_QUERY_FIND_RESET_PASSWORD = "SELECT reset_password FROM mylutece_directory_user WHERE user_login like ? ";
    private static final String SQL_QUERY_FIND_LOGINS_FROM_ROLE = "SELECT a.user_login FROM mylutece_directory_user a, mylutece_directory_user_role b" +
        " WHERE a.id_record = b.id_record AND b.role_key = ? ";
    private static final String SQL_QUERY_DELETE_ROLES_FOR_USER = "DELETE FROM mylutece_directory_user_role WHERE id_record = ?";
    private static final String SQL_QUERY_INSERT_ROLE_FOR_USER = "INSERT INTO mylutece_directory_user_role ( id_record, role_key ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_FIND_GROUPS_FROM_LOGIN = "SELECT b.group_key FROM mylutece_directory_user a, mylutece_directory_user_group b" +
        " WHERE a.id_record = b.id_record AND a.user_login like ? ";
    private static final String SQL_QUERY_DELETE_GROUPS_FOR_USER = "DELETE FROM mylutece_directory_user_group WHERE id_record = ?";
    private static final String SQL_QUERY_INSERT_GROUP_FOR_USER = "INSERT INTO mylutece_directory_user_group ( id_record, group_key ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_FIND_PASSWORD_MAX_VALID_DATE = "SELECT password_max_valid_date FROM mylutece_directory_user WHERE user_login like ? ";
    private static final String SQL_QUERY_UPDATE_RESET_PASSWORD_FROM_LOGIN = "UPDATE mylutece_directory_user SET reset_password = ? WHERE user_login like ? ";
    private static final String SQL_QUERY_SELECT_USER_ID_FROM_LOGIN = "SELECT id_record FROM mylutece_directory_user WHERE user_login like ? ";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectUserRolesFromLogin( String strLogin, Plugin plugin )
    {
        List<String> arrayRoles = new ArrayList<String>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ROLES_FROM_LOGIN, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            arrayRoles.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free(  );

        return arrayRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selectResetPasswordFromLogin( String strLogin, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_RESET_PASSWORD, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery( );

        if ( !daoUtil.next( ) )
        {
            daoUtil.free( );

            return false;
        }

        boolean bResetPassword = daoUtil.getBoolean( 1 );
        daoUtil.free( );

        return bResetPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp selectPasswordMaxValideDateFromLogin( String strLogin, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_PASSWORD_MAX_VALID_DATE, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery( );

        Timestamp passwordMaxValideDate = null;
        if ( daoUtil.next( ) )
        {
            passwordMaxValideDate = daoUtil.getTimestamp( 1 );
        }
        daoUtil.free( );
        return passwordMaxValideDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRolesForUser( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ROLES_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRoleForUser( int nIdRecord, String strRoleKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ROLE_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.setString( 2, strRoleKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectUserGroupsFromLogin( String strLogin, Plugin plugin )
    {
        List<String> arrayGroups = new ArrayList<String>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_GROUPS_FROM_LOGIN, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            arrayGroups.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free(  );

        return arrayGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> selectLoginListForRoleKey( String strRoleKey, Plugin plugin )
    {
        Collection<String> listLogins = new ArrayList<String>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_LOGINS_FROM_ROLE, plugin );
        daoUtil.setString( 1, strRoleKey );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listLogins.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free(  );

        return listLogins;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteGroupsForUser( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_GROUPS_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createGroupForUser( int nIdRecord, String strGroupKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_GROUP_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.setString( 2, strGroupKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMapped( int nIdDirectory, Plugin plugin )
    {
        boolean bMapped = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_IS_MAPPED, plugin );
        daoUtil.setInt( 1, nIdDirectory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            if ( daoUtil.getInt( 1 ) > 0 )
            {
                bMapped = true;
            }
        }

        daoUtil.free(  );

        return bMapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Integer> selectMappedDirectories( Plugin plugin )
    {
        Collection<Integer> listIdDirectory = new ArrayList<Integer>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAPPED_DIRECTORY, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdDirectory.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIdDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assignDirectory( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ASSIGN_DIRECTORY, plugin );
        daoUtil.setInt( 1, nIdDirectory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unAssignDirectories( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN_DIRECTORIES, plugin );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unAssignDirectory( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN_DIRECTORY, plugin );
        daoUtil.setInt( 1, nIdDirectory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResetPasswordFromLogin( String strUserName, boolean bNewValue, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_RESET_PASSWORD_FROM_LOGIN, plugin );

        daoUtil.setBoolean( 1, bNewValue );
        daoUtil.setString( 2, strUserName );
        daoUtil.executeUpdate( );

        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findUserIdFromLogin( String strLogin, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_USER_ID_FROM_LOGIN, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery( );
        int nRes = -1;
        if ( daoUtil.next( ) )
        {
            nRes = daoUtil.getInt( 1 );
        }
        daoUtil.free( );
        return nRes;
    }
}
