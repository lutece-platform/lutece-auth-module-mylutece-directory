/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service;

import fr.paris.lutece.plugins.directory.business.DirectoryRemovalListenerService;
import fr.paris.lutece.plugins.directory.business.EntryRemovalListenerService;
import fr.paris.lutece.plugins.directory.service.RecordRemovalListenerService;
import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.BaseAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.AttributeMappingEntryRemovalListener;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryDirectoryRemovalListener;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserFieldListener;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserRecordRemovalListener;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserRoleRemovalListener;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;
import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;


/**
 *
 * class MyluteceDirectoryPlugin
 *
 */
public class MyluteceDirectoryPlugin extends PluginDefaultImplementation
{
    public static final String PLUGIN_NAME = "mylutece-directory";

    /**
     * Initialize the module
     */
    public void init(  )
    {
        // Initialize the Database service
        DirectoryRemovalListenerService.getService(  )
                                       .registerListener( new MyluteceDirectoryDirectoryRemovalListener(  ) );
        // Create removal listeners and register them
        RoleRemovalListenerService.getService(  ).registerListener( new MyluteceDirectoryUserRoleRemovalListener(  ) );
        RecordRemovalListenerService.getService(  ).registerListener( new MyluteceDirectoryUserRecordRemovalListener(  ) );
        MyluteceDirectoryUserFieldListenerService.getService(  )
                                                 .registerListener( new MyluteceDirectoryUserFieldListener(  ) );
        EntryRemovalListenerService.getService(  ).registerListener( new AttributeMappingEntryRemovalListener(  ) );

        // Init base authentication
        BaseAuthentication baseAuthentication = (BaseAuthentication) SpringContextService.getBean( BaseAuthentication.AUTHENTICATION_BEAN_NAME );

        if ( baseAuthentication != null )
        {
            MultiLuteceAuthentication.registerAuthentication( baseAuthentication );
        }
        else
        {
            AppLogService.error( 
                "BaseAuthentication not found, please check your mylutece-directory_context.xml configuration" );
        }
    }
}
