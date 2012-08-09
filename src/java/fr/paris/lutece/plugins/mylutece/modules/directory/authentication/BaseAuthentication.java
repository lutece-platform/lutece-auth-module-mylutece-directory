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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication;

import fr.paris.lutece.plugins.mylutece.authentication.PortalAuthentication;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLog;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLogHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.parameter.MyluteceDirectoryParameterHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IMyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.IMyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.MyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyLuteceDirectoryApp;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;


/**
 * The Class provides an implementation of the inherited abstract class PortalAuthentication based on
 * a directory.
 *
 * @author Mairie de Paris
 * @version 2.0.0
 *
 * @since Lutece v2.0.0
 */
public class BaseAuthentication extends PortalAuthentication
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String AUTHENTICATION_BEAN_NAME = "mylutece-directory.authentication";
    private static final String AUTH_SERVICE_NAME = AppPropertiesService.getProperty( "mylutece-directory.service.name" );
    private static final String CONSTANT_PATH_ICON = "images/local/skin/plugins/mylutece/modules/directory/mylutece-directory.png";

    // PROPERTIES
    private static final String PROPERTY_MAX_ACCESS_FAILED = "access_failures_max";
    private static final String PROPERTY_INTERVAL_MINUTES = "access_failures_interval";

    // Messages properties
    private static final String PROPERTY_MESSAGE_USER_NOT_FOUND_DIRECTORY = "module.mylutece.directory.message.userNotFoundDirectory";
    private static final String PROPERTY_MESSAGE_USER_NOT_ACTIVATED = "module.mylutece.directory.message.userNotActivated";

    private static IMyluteceDirectoryService _myluteceDirectoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthServiceName(  )
    {
        return AUTH_SERVICE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthType( HttpServletRequest request )
    {
        return HttpServletRequest.BASIC_AUTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LuteceUser login( String strUserName, String strUserPassword, HttpServletRequest request )
            throws LoginException
    {

        // Creating a record of connections log
        Plugin pluginMyLutece = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
        ConnectionLog connectionLog = new ConnectionLog( );
        connectionLog.setIpAddress( request.getRemoteAddr( ) );
        connectionLog.setDateLogin( new java.sql.Timestamp( new java.util.Date( ).getTime( ) ) );

        // Test the number of errors during an interval of minutes
        int nMaxFailed = MyluteceDirectoryParameterHome
                .getIntegerSecurityParameter( PROPERTY_MAX_ACCESS_FAILED, plugin );
        int nIntervalMinutes = MyluteceDirectoryParameterHome.getIntegerSecurityParameter( PROPERTY_INTERVAL_MINUTES,
                plugin );

        if ( nMaxFailed > 0 && nIntervalMinutes > 0 )
        {
            int nNbFailed = ConnectionLogHome.getLoginErrors( connectionLog, nIntervalMinutes, pluginMyLutece );

            if ( nNbFailed > nMaxFailed )
            {
                throw new FailedLoginException( );
            }
        }

        Locale locale = request.getLocale(  );
        BaseUser user = getMyluteceDirectoryService( ).getUserByLogin( strUserName, this, true );

        // Unable to find the user
        if ( user == null )
        {
            AppLogService.info( "Unable to find user in the directory : " + strUserName );
            throw new FailedLoginException(
                    I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DIRECTORY, locale ) );
        }

        IMyluteceDirectorySecurityService securityService = SpringContextService.getBean( MyluteceDirectorySecurityService.BEAN_SERVICE );

        // Check password
        if ( !securityService.checkPassword( strUserName, strUserPassword ) )
        {
            AppLogService.info( "User login : Incorrect login or password " + strUserName );
            throw new FailedLoginException(
                    I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DIRECTORY, locale ) );
        }

        // Check if user is activated
        if ( !securityService.checkActivated( strUserName ) )
        {
            AppLogService.info( "User login : User is not activated" + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_ACTIVATED, locale ) );
        }

        // We update the status of the user if his password has become obsolete
        Timestamp passwordMaxValidDate = MyluteceDirectoryHome.findPasswordMaxValideDateFromLogin( strUserName, plugin );
        if ( passwordMaxValidDate != null && passwordMaxValidDate.getTime( ) < new java.util.Date( ).getTime( ) )
        {
            MyluteceDirectoryHome.updateResetPasswordFromLogin( strUserName, Boolean.TRUE, plugin );
        }
        int nUserId = MyluteceDirectoryHome.findUserIdFromLogin( strUserName, plugin );
        getMyluteceDirectoryService( ).updateUserExpirationDate( nUserId, plugin );

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout( LuteceUser user )
    {
    }

    /**
     * Find a user's reset password property by login
     * @param request The request
     * @param strLogin the login
     * @return DatabaseUser the user corresponding to the login
     */
    @Override
    public boolean findResetPassword( HttpServletRequest request, String strLogin )
    {
        Plugin plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
        return MyluteceDirectoryHome.findResetPasswordFromLogin( strLogin, plugin );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LuteceUser getAnonymousUser(  )
    {
        return new BaseUser( LuteceUser.ANONYMOUS_USERNAME, this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserInRole( LuteceUser user, HttpServletRequest request, String strRole )
    {
        String[] roles = getRolesByUser( user );

        if ( ( roles != null ) && ( strRole != null ) )
        {
            for ( String role : roles )
            {
                if ( strRole.equals( role ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewAccountPageUrl(  )
    {
        return MyLuteceDirectoryApp.getViewAccountUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewAccountPageUrl(  )
    {
        return MyLuteceDirectoryApp.getNewAccountUrl(  );
    }

    /**
     * Returns the Change password page URL of the Authentication Service
     * @return The URL
     */
    public String getModifyAccountPageUrl(  )
    {
        return MyLuteceDirectoryApp.getModifyAccountUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLostPasswordPageUrl(  )
    {
        return MyLuteceDirectoryApp.getLostPasswordUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<LuteceUser> getUsers(  )
    {
        return getMyluteceDirectoryService( ).getUsers( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LuteceUser getUser( String userLogin )
    {
        return getMyluteceDirectoryService( ).getUserByLogin( userLogin, this, false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getRolesByUser( LuteceUser user )
    {
        Set<String> setRoles = new HashSet<String>(  );
        String[] strRoles = user.getRoles(  );

        if ( strRoles != null )
        {
            for ( String strRole : strRoles )
            {
                setRoles.add( strRole );
            }
        }

        String[] strReturnRoles = new String[setRoles.size(  )];
        setRoles.toArray( strReturnRoles );

        return strReturnRoles;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String getIconUrl(  )
    {
        return CONSTANT_PATH_ICON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(  )
    {
        return MyluteceDirectoryPlugin.PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName(  )
    {
        return MyluteceDirectoryPlugin.PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResetPasswordPageUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + MyLuteceDirectoryApp.getMessageResetPasswordUrl( );
    }

    private static IMyluteceDirectoryService getMyluteceDirectoryService( )
    {
        if ( _myluteceDirectoryService == null )
        {
            _myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );
        }
        return _myluteceDirectoryService;
    }

}
