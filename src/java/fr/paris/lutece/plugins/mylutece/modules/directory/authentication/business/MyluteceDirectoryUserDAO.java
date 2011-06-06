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
 * This class provides Data Access methods for directoryUser objects
 */
public final class MyluteceDirectoryUserDAO implements IMyluteceDirectoryUserDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT user_login, activated FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_SELECT_PASSWORD = " SELECT user_password FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_directory_user ( id_record, user_login, user_password, activated ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_directory_user SET user_login = ?, activated = ? WHERE id_record = ? ";
    private static final String SQL_QUERY_UPDATE_PASSWORD = " UPDATE mylutece_directory_user SET user_password = ? WHERE id_record = ? ";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_record, user_login, activated FROM mylutece_directory_user ORDER BY user_login ";
    private static final String SQL_QUERY_SELECTALL_FOR_LOGIN = " SELECT id_record, user_login, activated FROM mylutece_directory_user WHERE user_login = ? ";
   
    private static final String SQL_QUERY_CHECK_PASSWORD_FOR_USER_ID = " SELECT count(*) FROM mylutece_directory_user WHERE user_login = ? AND user_password = ? ";
    private static final String SQL_QUERY_CHECK_ACTIVATED = " SELECT count(*) FROM mylutece_directory_user WHERE user_login = ? AND activated = 1 ";
    /** This class implements the Singleton design pattern. */
    private static MyluteceDirectoryUserDAO _dao = new MyluteceDirectoryUserDAO(  );

    /**
     * Creates a new directoryUserDAO object.
     */
    private MyluteceDirectoryUserDAO(  )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static MyluteceDirectoryUserDAO getInstance(  )
    {
        return _dao;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#insert(fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insert( MyluteceDirectoryUser directoryUser, String strPassword, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, directoryUser.getIdRecord(  ) );
        daoUtil.setString( 2, directoryUser.getLogin(  ) );
        daoUtil.setString( 3, strPassword );
        daoUtil.setBoolean( 4, directoryUser.isActivated(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public MyluteceDirectoryUser load( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.executeQuery(  );

        MyluteceDirectoryUser directoryUser = null;

        if ( daoUtil.next(  ) )
        {
            directoryUser = new MyluteceDirectoryUser(  );
            directoryUser.setIdRecord( nIdRecord );
            directoryUser.setLogin( daoUtil.getString( 1 ) );
            directoryUser.setActivated( daoUtil.getBoolean( 2 ) );
        }

        daoUtil.free(  );

        return directoryUser;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#delete(fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, directoryUser.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#store(fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( 1, directoryUser.getLogin(  ) );
        daoUtil.setBoolean( 2, directoryUser.isActivated(  ) );        
        daoUtil.setInt( 3, directoryUser.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#updatePassword(fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void updatePassword( MyluteceDirectoryUser directoryUser, String strNewPassword, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_PASSWORD, plugin );
        daoUtil.setString( 1, strNewPassword );
        daoUtil.setInt( 2, directoryUser.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#selectPasswordByPrimaryKey(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String selectPasswordByPrimaryKey( int nIdRecord, Plugin plugin )
    {
        String strPassword = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PASSWORD, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            strPassword = daoUtil.getString( 1 );
        }

        daoUtil.free(  );

        return strPassword;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#selectDirectoryUserList(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<MyluteceDirectoryUser> selectDirectoryUserList( Plugin plugin )
    {
        Collection<MyluteceDirectoryUser> listDirectoryUsers = new ArrayList<MyluteceDirectoryUser>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            MyluteceDirectoryUser directoryUser = new MyluteceDirectoryUser(  );
            directoryUser.setIdRecord( daoUtil.getInt( 1 ) );
            directoryUser.setLogin( daoUtil.getString( 2 ) );
            directoryUser.setActivated( daoUtil.getBoolean( 3 ) );
            listDirectoryUsers.add( directoryUser );
        }

        daoUtil.free(  );

        return listDirectoryUsers;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#selectDirectoryUserListForLogin(java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<MyluteceDirectoryUser> selectDirectoryUserListForLogin( String strLogin, Plugin plugin )
    {
        Collection<MyluteceDirectoryUser> listDirectoryUsers = new ArrayList<MyluteceDirectoryUser>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_LOGIN, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            MyluteceDirectoryUser directoryUser = new MyluteceDirectoryUser(  );
            directoryUser.setIdRecord( daoUtil.getInt( 1 ) );
            directoryUser.setLogin( daoUtil.getString( 2 ) );
            directoryUser.setActivated( daoUtil.getBoolean( 3 ) );
            listDirectoryUsers.add( directoryUser );
        }

        daoUtil.free(  );

        return listDirectoryUsers;
    }    
   

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IMyluteceDirectoryUserDAO#checkPassword(java.lang.String, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public boolean checkPassword( String strLogin, String strPassword, Plugin plugin )
    {
        int nCount = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_PASSWORD_FOR_USER_ID, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.setString( 2, strPassword );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return ( nCount == 1 ) ? true : false;
    }

    public boolean checkActivated(String strLogin, Plugin plugin) 
    {    
    	int nCount = 0;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_ACTIVATED, plugin );
    	daoUtil.setString( 1, strLogin );
    	daoUtil.executeQuery(  );

    	if ( daoUtil.next(  ) )
    	{
    		nCount = daoUtil.getInt( 1 );
    	}

    	daoUtil.free(  );

    	return ( nCount == 1 ) ? true : false;
	}
}
