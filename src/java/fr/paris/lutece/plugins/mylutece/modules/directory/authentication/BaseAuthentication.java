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
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.IMyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.IMyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.security.MyluteceDirectorySecurityService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyLuteceDirectoryApp;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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

    // Messages properties
    private static final String PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE = "module.mylutece.directory.message.userNotFoundDirectory";
    private static final String PROPERTY_MESSAGE_USER_NOT_ACTIVATED = "module.mylutece.directory.message.userNotActivated";

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
        Locale locale = request.getLocale(  );
        IMyluteceDirectoryService myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );
        BaseUser user = myluteceDirectoryService.getUserByLogin( strUserName, this, true );

        // Unable to find the user
        if ( user == null )
        {
            AppLogService.info( "Unable to find user in the directory : " + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE, locale ) );
        }

        IMyluteceDirectorySecurityService securityService = SpringContextService.getBean( MyluteceDirectorySecurityService.BEAN_SERVICE );

        // Check password
        if ( !securityService.checkPassword( strUserName, strUserPassword ) )
        {
            AppLogService.info( "User login : Incorrect login or password" + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE, locale ) );
        }

        // Check if user is activated
        if ( !securityService.checkActivated( strUserName ) )
        {
            AppLogService.info( "User login : User is not activated" + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_ACTIVATED, locale ) );
        }

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
        IMyluteceDirectoryService myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );

        return myluteceDirectoryService.getUsers( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LuteceUser getUser( String userLogin )
    {
        IMyluteceDirectoryService myluteceDirectoryService = SpringContextService.getBean( MyluteceDirectoryService.BEAN_SERVICE );

        return myluteceDirectoryService.getUserByLogin( userLogin, this, false );
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
}
