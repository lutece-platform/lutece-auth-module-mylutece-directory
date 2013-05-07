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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.key;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * MyluteceDirectoryUserKeyDAO
 *
 */
public class MyluteceDirectoryUserKeyDAO implements IMyluteceDirectoryUserKeyDAO
{
    private static final String SQL_QUERY_SELECT = " SELECT mylutece_directory_user_key, id_record FROM mylutece_directory_key WHERE mylutece_directory_user_key = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_directory_key (mylutece_directory_user_key, id_record) VALUES ( ?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_directory_key ";
    private static final String SQL_WHERE = " WHERE ";
    private static final String SQL_USER_KEY = " mylutece_directory_user_key = ? ";
    private static final String SQL_ID_RECORD = " id_record = ? ";
    private static final String SQL_QUERY_SELECT_BY_LOGIN = " SELECT mdk.mylutece_directory_user_key, mdk.id_record FROM mylutece_directory_key mdk LEFT JOIN mylutece_directory_user mdu ON (mdu.id_record = mdk.id_record ) WHERE mdu.user_login = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( MyluteceDirectoryUserKey userKey, Plugin plugin )
    {
        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setString( nIndex++, userKey.getKey(  ) );
        daoUtil.setInt( nIndex++, userKey.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MyluteceDirectoryUserKey load( String strKey, Plugin plugin )
    {
        MyluteceDirectoryUserKey userKey = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setString( 1, strKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            userKey = new MyluteceDirectoryUserKey(  );
            userKey.setKey( daoUtil.getString( nIndex++ ) );
            userKey.setIdRecord( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free(  );

        return userKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE + SQL_WHERE + SQL_USER_KEY, plugin );
        daoUtil.setString( 1, strKey );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdRecord( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE + SQL_WHERE + SQL_ID_RECORD, plugin );
        daoUtil.setInt( 1, nIdRecord );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public MyluteceDirectoryUserKey selectKeyByLogin( String login, Plugin plugin )
    {
        MyluteceDirectoryUserKey userKey = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_LOGIN, plugin );
        daoUtil.setString( 1, login );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            userKey = new MyluteceDirectoryUserKey( );
            userKey.setKey( daoUtil.getString( nIndex++ ) );
            userKey.setIdRecord( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );

        return userKey;
    }
}
