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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


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
    private static final String SQL_QUERY_FIND_LOGINS_FROM_ROLE = "SELECT a.user_login FROM mylutece_directory_user a, mylutece_directory_user_role b" +
        " WHERE a.id_record = b.id_record AND b.role_key = ? ";
    private static final String SQL_QUERY_DELETE_ROLES_FOR_USER = "DELETE FROM mylutece_directory_user_role WHERE id_record = ?";
    private static final String SQL_QUERY_INSERT_ROLE_FOR_USER = "INSERT INTO mylutece_directory_user_role ( id_record, role_key ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_FIND_GROUPS_FROM_LOGIN = "SELECT b.group_key FROM mylutece_directory_user a, mylutece_directory_user_group b" +
        " WHERE a.id_record = b.id_record AND a.user_login like ? ";
    private static final String SQL_QUERY_DELETE_GROUPS_FOR_USER = "DELETE FROM mylutece_directory_user_group WHERE id_record = ?";
    private static final String SQL_QUERY_INSERT_GROUP_FOR_USER = "INSERT INTO mylutece_directory_user_group ( id_record, group_key ) VALUES ( ?, ? ) ";

    /** This class implements the Singleton design pattern. */
    private static IMyluteceDirectoryDAO _dao = new MyluteceDirectoryDAO(  );

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static IMyluteceDirectoryDAO getInstance(  )
    {
        return _dao;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#selectUserRolesFromLogin(java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public ArrayList<String> selectUserRolesFromLogin( String strLogin, Plugin plugin )
    {
        ArrayList<String> arrayRoles = new ArrayList<String>(  );
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

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#deleteRolesForUser(int, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void deleteRolesForUser( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ROLES_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#createRoleForUser(int, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void createRoleForUser( int nIdRecord, String strRoleKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ROLE_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.setString( 2, strRoleKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#selectUserGroupsFromLogin(java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public ArrayList<String> selectUserGroupsFromLogin( String strLogin, Plugin plugin )
    {
        ArrayList<String> arrayGroups = new ArrayList<String>(  );
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

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#selectLoginListForRoleKey(java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
         */
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

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#deleteGroupsForUser(int, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void deleteGroupsForUser( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_GROUPS_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#createGroupForUser(int, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void createGroupForUser( int nIdRecord, String strGroupKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_GROUP_FOR_USER, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.setString( 2, strGroupKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#isMapped(int, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#selectMappedDirectories(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<Integer> selectMappedDirectories( Plugin plugin )
    {
        Collection<Integer> listIdDirectory = new ArrayList<Integer>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAPPED_DIRECTORY, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdDirectory.add( new Integer( daoUtil.getInt( 1 ) ) );
        }

        daoUtil.free(  );

        return listIdDirectory;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#assignDirectory(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void assignDirectory( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ASSIGN_DIRECTORY, plugin );
        daoUtil.setInt( 1, nIdDirectory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#unAssignDirectories(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void unAssignDirectories( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN_DIRECTORIES, plugin );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryDAO#unAssignDirectory(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void unAssignDirectory( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UNASSIGN_DIRECTORY, plugin );
        daoUtil.setInt( 1, nIdDirectory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
