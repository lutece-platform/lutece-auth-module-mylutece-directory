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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.key;

import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.key.MyluteceDirectoryUserKey;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.key.MyluteceDirectoryUserKeyHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyLuteceDirectoryApp;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * MyluteceDirectoryUserKeyService
 *
 */
public class MyluteceDirectoryUserKeyService implements IMyluteceDirectoryUserKeyService
{
    public static final String BEAN_SERVICE = "mylutece-directory.myluteceDirectoryUserKeyService";

    // CONSTANTS
    private static final String SLASH = "/";

    // PARAMETERS
    private static final String PARAMETER_KEY = "key";

    // JSP
    private static final String JSP_VALIDATE_ACCOUNT = "jsp/site/plugins/mylutece/modules/database/DoValidateAccount.jsp";

    // CRUD

    /**
     * {@inheritDoc}
     */
    @Override
    public MyluteceDirectoryUserKey create( int nIdRecord )
    {
        MyluteceDirectoryUserKey userKey = new MyluteceDirectoryUserKey(  );
        userKey.setIdRecord( nIdRecord );
        userKey.setKey( UUID.randomUUID(  ).toString(  ) );
        MyluteceDirectoryUserKeyHome.create( userKey );

        return userKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MyluteceDirectoryUserKey findByPrimaryKey( String strKey )
    {
        return MyluteceDirectoryUserKeyHome.findByPrimaryKey( strKey );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MyluteceDirectoryUserKey findKeyByLogin( String strLogin )
    {
        return MyluteceDirectoryUserKeyHome.findKeyByLogin( strLogin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( String strKey )
    {
        MyluteceDirectoryUserKeyHome.remove( strKey );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeByIdRecord( int nIdRecord )
    {
        MyluteceDirectoryUserKeyHome.removeByIdRecord( nIdRecord );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValidationUrl( String strKey, HttpServletRequest request )
    {
        StringBuilder sbBaseUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );

        if ( ( sbBaseUrl.length(  ) > 0 ) && !sbBaseUrl.toString(  ).endsWith( SLASH ) )
        {
            sbBaseUrl.append( SLASH );
        }

        sbBaseUrl.append( JSP_VALIDATE_ACCOUNT );

        UrlItem url = new UrlItem( sbBaseUrl.toString(  ) );
        url.addParameter( PARAMETER_KEY, strKey );

        return url.getUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReinitUrl( String strKey, HttpServletRequest request )
    {
        StringBuilder sbBaseUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );

        if ( ( sbBaseUrl.length(  ) > 0 ) && !sbBaseUrl.toString(  ).endsWith( SLASH ) )
        {
            sbBaseUrl.append( SLASH );
        }

        sbBaseUrl.append( MyLuteceDirectoryApp.getReinitPageUrl(  ) );

        UrlItem url = new UrlItem( sbBaseUrl.toString(  ) );
        url.addParameter( PARAMETER_KEY, strKey );

        return url.getUrl(  );
    }
}
