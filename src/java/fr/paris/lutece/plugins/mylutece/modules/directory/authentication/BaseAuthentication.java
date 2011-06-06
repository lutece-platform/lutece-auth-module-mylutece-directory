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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.mylutece.authentication.PortalAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.parameter.MyluteceDirectoryParameterHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyLuteceDirectoryApp;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;


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
    private static final String AUTH_SERVICE_NAME = AppPropertiesService.getProperty( "mylutece-directory.service.name" );
    private static final String CONSTANT_PATH_ICON = "images/local/skin/plugins/mylutece/modules/directory/mylutece-directory.png";

    // PARAMETERS
    public static final String PARAMETER_ENABLE_PASSWORD_ENCRYPTION = "enable_password_encryption";
    public static final String PARAMETER_ENCRYPTION_ALGORITHM = "encryption_algorithm";

    // Messages properties
    private static final String PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE = "module.mylutece.directory.message.userNotFoundDirectory";
    private static final String PROPERTY_MESSAGE_USER_NOT_ACTIVATED = "module.mylutece.directory.message.userNotActivated";
    private static final String PLUGIN_NAME = "mylutece-directory";

    /**
     * Constructor
     *
     */
    public BaseAuthentication(  )
    {
        super(  );
    }

    /**
    * Gets the Authentification service name
    * @return The name of the authentication service
    */
    public String getAuthServiceName(  )
    {
        return AUTH_SERVICE_NAME;
    }

    /**
    * Gets the Authentification type
    * @param request The HTTP request
    * @return The type of authentication
    */
    public String getAuthType( HttpServletRequest request )
    {
        return HttpServletRequest.BASIC_AUTH;
    }

    /**
    * This methods checks the login info in the directory
    *
    * @param strUserName The username
    * @param strUserPassword The password
    * @param request The HttpServletRequest
    *
    * @return A LuteceUser object corresponding to the login
    * @throws LoginException The LoginException
    */
    public LuteceUser login( String strUserName, String strUserPassword, HttpServletRequest request )
        throws LoginException
    {
        Locale locale = request.getLocale(  );
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

        BaseUser user = MyluteceDirectoryHome.findLuteceUserByLogin( strUserName, plugin, this );

        //Unable to find the user
        if ( user == null )
        {
            AppLogService.info( "Unable to find user in the directory : " + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE, locale ) );
        }

        // Check if there is an encryption algorithm
        String strPassword = strUserPassword;
        if ( Boolean.valueOf( 
        		MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENABLE_PASSWORD_ENCRYPTION, plugin ).getName(  ) ) )
    	{
        	String strAlgorithm = MyluteceDirectoryParameterHome.findByKey( PARAMETER_ENCRYPTION_ALGORITHM, plugin ).getName(  );
        	strPassword = CryptoService.encrypt( strUserPassword, strAlgorithm );
    	}
        
        //Check password
        if ( !MyluteceDirectoryUserHome.checkPassword( strUserName, strPassword, plugin ) )
        {
            AppLogService.info( "User login : Incorrect login or password" + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_FOUND_DATABASE, locale ) );
        }
        
        //Check if user is activated
        if ( !MyluteceDirectoryUserHome.checkActivated( strUserName, plugin ) )
        {
            AppLogService.info( "User login : User is not activated" + strUserName );
            throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_USER_NOT_ACTIVATED, locale ) );
        }

        //Get roles
        ArrayList<String> arrayRoles = MyluteceDirectoryHome.findUserRolesFromLogin( strUserName, plugin );

        if ( !arrayRoles.isEmpty(  ) )
        {
            user.setRoles( arrayRoles );
        }

        //Get groups
        ArrayList<String> arrayGroups = MyluteceDirectoryHome.findUserGroupsFromLogin( strUserName, plugin );

        if ( !arrayGroups.isEmpty(  ) )
        {
            user.setGroups( arrayGroups );
        }

        return user;
    }

    /**
    * This methods logout the user
    * @param user The user
    */
    public void logout( LuteceUser user )
    {
    }

    /**
    * This method returns an anonymous Lutece user
    *
    * @return An anonymous Lutece user
    */
    public LuteceUser getAnonymousUser(  )
    {
        return new BaseUser( LuteceUser.ANONYMOUS_USERNAME, this );
    }

    /**
    * Checks that the current user is associated to a given role
    * @param user The user
    * @param request The HTTP request
    * @param strRole The role name
    * @return Returns true if the user is associated to the role, otherwise false
    */
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
     * Returns the View account page URL of the Authentication Service
     * @return The URL
     */
    public String getViewAccountPageUrl(  )
    {
        return MyLuteceDirectoryApp.getViewAccountUrl(  );
    }

    /**
     * Returns the New account page URL of the Authentication Service
     * @return The URL
     */
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
     * Returns the lost password URL of the Authentication Service
     * @return The URL
     */
    public String getLostPasswordPageUrl(  )
    {
        return MyLuteceDirectoryApp.getLostPasswordUrl(  );
    }

    /**
     * Returns all users managed by the authentication service if this feature is
     * available.
     * @return A collection of Lutece users or null if the service doesn't provide a users list
     */
    public Collection<LuteceUser> getUsers(  )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

        Collection<BaseUser> BaseUsers = MyluteceDirectoryHome.findDirectoryUsersList( plugin, this );
        Collection<LuteceUser> luteceUsers = new ArrayList<LuteceUser>(  );

        for ( BaseUser user : BaseUsers )
        {
            luteceUsers.add( user );
        }

        return luteceUsers;
    }

    /**
     * Returns the user managed by the authentication service if this feature is
     * available.
     * @return A Lutece users or null if the service doesn't provide a user
     */
    public LuteceUser getUser( String userLogin )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

        BaseUser user = MyluteceDirectoryHome.findLuteceUserByLogin( userLogin, plugin, this );

        return user;
    }

    /**
     * get all roles for this user :
     *    - user's roles
     *    - user's groups roles
     *
     * @param user The user
     * @return Array of roles
     */
    public String[] getRolesByUser( LuteceUser user )
    {
        Set<String> setRoles = new HashSet<String>(  );
        String[] strGroups = user.getGroups(  );
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
     * 
     *{@inheritDoc}
     */
	public String getIconUrl()
	{
		return CONSTANT_PATH_ICON;
	}

	/**
	 * 
	 * Returns {@link MyluteceDirectoryPlugin#PLUGIN_NAME}
	 * @return {@link MyluteceDirectoryPlugin#PLUGIN_NAME}
	 * 
	 */
	public String getName()
	{
		return MyluteceDirectoryPlugin.PLUGIN_NAME;
	}

	/**
	 * 
	 *{@inheritDoc}
	 */
	public String getPluginName()
	{
		return MyluteceDirectoryPlugin.PLUGIN_NAME;
	}
}
