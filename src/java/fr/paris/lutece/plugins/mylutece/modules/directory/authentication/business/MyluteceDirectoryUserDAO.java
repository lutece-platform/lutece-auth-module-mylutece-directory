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
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class provides Data Access methods for directoryUser objects
 */
public final class MyluteceDirectoryUserDAO implements IMyluteceDirectoryUserDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT user_login, activated, password_max_valid_date, account_max_valid_date FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_SELECT_PASSWORD = " SELECT user_password FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_directory_user ( id_record, user_login, user_password, activated, password_max_valid_date, account_max_valid_date ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_directory_user WHERE id_record = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_directory_user SET user_login = ?, activated = ? WHERE id_record = ? ";
    private static final String SQL_QUERY_UPDATE_PASSWORD = " UPDATE mylutece_directory_user SET user_password = ?, password_max_valid_date = ? WHERE id_record = ? ";
    private static final String SQL_QUERY_UPDATE_RESET_PASSWORD = " UPDATE mylutece_directory_user SET reset_password = ? WHERE id_record = ? ";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_record, user_login, activated FROM mylutece_directory_user ORDER BY user_login ";
    private static final String SQL_QUERY_SELECTALL_FOR_LOGIN = " SELECT id_record, user_login, activated, password_max_valid_date FROM mylutece_directory_user WHERE user_login = ? ";
    private static final String SQL_QUERY_CHECK_PASSWORD_FOR_USER_ID = " SELECT count(*) FROM mylutece_directory_user WHERE user_login = ? AND user_password = ? ";
    private static final String SQL_QUERY_CHECK_ACTIVATED = " SELECT count(*) FROM mylutece_directory_user WHERE user_login = ? AND activated = 1 ";
    private static final String SQL_SELECT_USER_ID_FROM_LOGIN = "SELECT id_record FROM mylutece_directory_user WHERE user_login = ?";
    private static final String SQL_SELECT_USER_PASSWORD_HISTORY = "SELECT password FROM mylutece_directory_user_password_history WHERE id_record = ? ORDER BY date_password_change desc";
    private static final String SQL_COUNT_USER_PASSWORD_HISTORY = "SELECT COUNT(*) FROM mylutece_directory_user_password_history WHERE id_record = ? AND date_password_change > ?";
    private static final String SQL_INSERT_PASSWORD_HISTORY = "INSERT INTO mylutece_directory_user_password_history (id_record, password) VALUES ( ?, ? ) ";
    private static final String SQL_DELETE_PASSWORD_HISTORY = "DELETE FROM mylutece_directory_user_password_history WHERE id_record = ?";

    private static final String SQL_QUERY_SELECT_EXPIRED_USER_ID = "SELECT id_record FROM mylutece_directory_user WHERE activated = ?";

    private static final String SQL_QUERY_SELECT_EXPIRED_LIFE_TIME_USER_ID = "SELECT id_record FROM mylutece_directory_user WHERE account_max_valid_date < ? and activated < ? ";

    private static final String SQL_QUERY_SELECT_USER_ID_FIRST_ALERT = "SELECT id_record FROM mylutece_directory_user WHERE nb_alerts_sent = 0 and activated < ? and account_max_valid_date < ? ";
    private static final String SQL_QUERY_SELECT_USER_ID_OTHER_ALERT = "SELECT id_record FROM mylutece_directory_user "
            + "WHERE nb_alerts_sent > 0 and nb_alerts_sent <= ? and activated < ? and (account_max_valid_date + nb_alerts_sent * ?) < ? ";

    private static final String SQL_QUERY_UPDATE_STATUS = " UPDATE mylutece_directory_user SET activated = ? WHERE id_record IN ( ";
    private static final String SQL_QUERY_UPDATE_NB_ALERT = " UPDATE mylutece_directory_user SET nb_alerts_sent = nb_alerts_sent + 1 WHERE id_record IN ( ";

    private static final String SQL_QUERY_UPDATE_REACTIVATE_ACCOUNT = " UPDATE mylutece_directory_user SET nb_alerts_sent = 0, account_max_valid_date = ? WHERE id_record = ? ";
    private static final String SQL_SELECT_NB_ALERT_SENT = " SELECT nb_alerts_sent FROM mylutece_directory_user WHERE id_record = ? ";

    private static final String CONSTANT_CLOSE_PARENTHESIS = " ) ";
    private static final String CONSTANT_COMMA = ", ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( MyluteceDirectoryUser directoryUser, String strPassword, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, directoryUser.getIdRecord(  ) );
        daoUtil.setString( 2, directoryUser.getLogin(  ) );
        daoUtil.setString( 3, strPassword );
        daoUtil.setInt( 4, directoryUser.getStatus( ) );
        daoUtil.setTimestamp( 5, directoryUser.getPasswordMaxValidDate( ) );

        if ( directoryUser.getAccountMaxValidDate( ) == null )
        {
            daoUtil.setLongNull( 6 );
        }
        else
        {
            daoUtil.setLong( 6, directoryUser.getAccountMaxValidDate( ).getTime( ) );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            directoryUser.setStatus( daoUtil.getInt( 2 ) );
            directoryUser.setPasswordMaxValidDate( daoUtil.getTimestamp( 3 ) );
            long accountTime = daoUtil.getLong( 4 );
            if ( accountTime > 0 )
            {
                directoryUser.setAccountMaxValidDate( new Timestamp( accountTime ) );
            }
        }

        daoUtil.free(  );

        return directoryUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, directoryUser.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( MyluteceDirectoryUser directoryUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( 1, directoryUser.getLogin(  ) );
        daoUtil.setInt( 2, directoryUser.getStatus( ) );
        daoUtil.setInt( 3, directoryUser.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePassword( MyluteceDirectoryUser directoryUser, String strNewPassword, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_PASSWORD, plugin );
        daoUtil.setString( 1, strNewPassword );
        daoUtil.setTimestamp( 2, directoryUser.getPasswordMaxValidDate( ) );
        daoUtil.setInt( 3, directoryUser.getIdRecord( ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResetPassword( MyluteceDirectoryUser user, boolean bNewValue, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_RESET_PASSWORD, plugin );
        daoUtil.setBoolean( 1, bNewValue );
        daoUtil.setInt( 2, user.getIdRecord( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
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
            directoryUser.setStatus( daoUtil.getInt( 3 ) );
            listDirectoryUsers.add( directoryUser );
        }

        daoUtil.free(  );

        return listDirectoryUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            directoryUser.setStatus( daoUtil.getInt( 3 ) );
            directoryUser.setPasswordMaxValidDate( daoUtil.getTimestamp( 4 ) );
            listDirectoryUsers.add( directoryUser );
        }

        daoUtil.free(  );

        return listDirectoryUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkActivated( String strLogin, Plugin plugin )
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int findIdRecordFromLogin( String strLogin, Plugin plugin )
    {
        int nRecordId = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_USER_ID_FROM_LOGIN, plugin );
        daoUtil.setString( 1, strLogin );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nRecordId = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nRecordId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectUserPasswordHistory( int nUserID, Plugin plugin )
    {
        List<String> passwordHistory = new ArrayList<String>( );

        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_USER_PASSWORD_HISTORY, plugin );
        daoUtil.setInt( 1, nUserID );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            passwordHistory.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free( );
        return passwordHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countUserPasswordHistoryFromDate( Timestamp minDate, int nUserId, Plugin plugin )
    {
        int nNbRes = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_COUNT_USER_PASSWORD_HISTORY, plugin );
        daoUtil.setInt( 1, nUserId );
        daoUtil.setTimestamp( 2, minDate );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nNbRes = daoUtil.getInt( 1 );
        }

        daoUtil.free( );
        return nNbRes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertNewPasswordInHistory( String strPassword, int nUserId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_INSERT_PASSWORD_HISTORY, plugin );
        daoUtil.setInt( 1, nUserId );
        daoUtil.setString( 2, strPassword );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllPasswordHistoryForUser( int nUserId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_DELETE_PASSWORD_HISTORY, plugin );
        daoUtil.setInt( 1, nUserId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findAllExpiredUserId( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EXPIRED_USER_ID, plugin );
        daoUtil.setInt( 1, MyluteceDirectoryUser.STATUS_EXPIRED );
        List<Integer> idExpiredUserlist = new ArrayList<Integer>( );
        daoUtil.executeQuery( );
        while ( daoUtil.next( ) )
        {
            idExpiredUserlist.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return idExpiredUserlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersWithExpiredLifeTimeList( Timestamp currentTimestamp, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EXPIRED_LIFE_TIME_USER_ID, plugin );
        daoUtil.setLong( 1, currentTimestamp.getTime( ) );
        daoUtil.setInt( 2, MyluteceDirectoryUser.STATUS_EXPIRED );
        List<Integer> idExpiredUserlist = new ArrayList<Integer>( );
        daoUtil.executeQuery( );
        while ( daoUtil.next( ) )
        {
            idExpiredUserlist.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return idExpiredUserlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersToSendFirstAlert( Timestamp alertMaxDate, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_USER_ID_FIRST_ALERT, plugin );
        daoUtil.setInt( 1, MyluteceDirectoryUser.STATUS_EXPIRED );
        daoUtil.setLong( 2, alertMaxDate.getTime( ) );
        List<Integer> idUserFirstAlertlist = new ArrayList<Integer>( );
        daoUtil.executeQuery( );
        while ( daoUtil.next( ) )
        {
            idUserFirstAlertlist.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return idUserFirstAlertlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersToSendOtherAlert( Timestamp alertMaxDate, Timestamp timeBetweenAlerts,
            int maxNumberAlerts, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_USER_ID_OTHER_ALERT, plugin );
        daoUtil.setInt( 1, maxNumberAlerts );
        daoUtil.setInt( 2, MyluteceDirectoryUser.STATUS_EXPIRED );
        daoUtil.setLong( 3, timeBetweenAlerts.getTime( ) );
        daoUtil.setLong( 4, alertMaxDate.getTime( ) );
        List<Integer> idUserFirstAlertlist = new ArrayList<Integer>( );
        daoUtil.executeQuery( );
        while ( daoUtil.next( ) )
        {
            idUserFirstAlertlist.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return idUserFirstAlertlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserStatus( List<Integer> ndUserList, int nNewStatus, Plugin plugin )
    {
        if ( ndUserList != null && ndUserList.size( ) > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( );
            sbSQL.append( SQL_QUERY_UPDATE_STATUS );

            for ( int i = 0; i < ndUserList.size( ); i++ )
            {
                if ( i > 0 )
                {
                    sbSQL.append( CONSTANT_COMMA );
                }
                sbSQL.append( ndUserList.get( i ) );
            }
            sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );
            daoUtil.setInt( 1, nNewStatus );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNbAlert( List<Integer> listIdUser, Plugin plugin )
    {
        if ( listIdUser != null && listIdUser.size( ) > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( );
            sbSQL.append( SQL_QUERY_UPDATE_NB_ALERT );

            for ( int i = 0; i < listIdUser.size( ); i++ )
            {
                if ( i > 0 )
                {
                    sbSQL.append( CONSTANT_COMMA );
                }
                sbSQL.append( listIdUser.get( i ) );
            }
            sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserExpirationDate( int nIdUser, Timestamp newExpirationDate, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_REACTIVATE_ACCOUNT, plugin );
        if ( newExpirationDate == null )
        {
            daoUtil.setLongNull( 1 );
        }
        else
        {
            daoUtil.setLong( 1, newExpirationDate.getTime( ) );
        }
        daoUtil.setInt( 2, nIdUser );

        daoUtil.executeUpdate( );

        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbAccountLifeTimeNotification( int nIdUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_NB_ALERT_SENT, plugin );
        daoUtil.setInt( 1, nIdUser );
        daoUtil.executeQuery( );
        int nRes = 0;
        if ( daoUtil.next( ) )
        {
            nRes = daoUtil.getInt( 1 );
        }
        daoUtil.free( );
        return nRes;
    }
}
