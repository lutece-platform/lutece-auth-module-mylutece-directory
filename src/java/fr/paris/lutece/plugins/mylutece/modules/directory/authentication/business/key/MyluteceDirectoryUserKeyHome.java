/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 *
 * DatabaseUserKeyHome
 *
 */
public final class MyluteceDirectoryUserKeyHome
{
    private static final String BEAN_DAO = "mylutece-directory.myluteceDirectoryUserKeyDAO";
    private static Plugin _plugin = PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
    private static IMyluteceDirectoryUserKeyDAO _dao = SpringContextService.getBean( BEAN_DAO );

    /**
     * Private constructor - this class need not be instantiated
     */
    private MyluteceDirectoryUserKeyHome(  )
    {
    }

    /**
     * Create a new key
     * @param userKey the key
     */
    public static void create( MyluteceDirectoryUserKey userKey )
    {
        _dao.insert( userKey, _plugin );
    }

    /**
     * Remove a key from a given key
     * @param strKey the key
     */
    public static void remove( String strKey )
    {
        _dao.delete( strKey, _plugin );
    }

    /**
     * Remove a key from a given id user
     * @param nIdRecord the id record
     */
    public static void removeByIdRecord( int nIdRecord )
    {
        _dao.deleteByIdRecord( nIdRecord, _plugin );
    }

    /**
     * Find a key from a given key
     * @param strKey the key
     * @return a {@link MyluteceDirectoryUserKey}
     */
    public static MyluteceDirectoryUserKey findByPrimaryKey( String strKey )
    {
        return _dao.load( strKey, _plugin );
    }

    /**
     * Find a key from a given login
     * @param login the user's login
     * @return a {@link MyluteceDirectoryUserKey}
     */
    public static MyluteceDirectoryUserKey findKeyByLogin( String login )
    {
        return _dao.selectKeyByLogin( login, _plugin );
    }
}
