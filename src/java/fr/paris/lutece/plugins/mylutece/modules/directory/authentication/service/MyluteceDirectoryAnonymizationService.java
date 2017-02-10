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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service;

import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserField;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryHome;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.service.IAnonymizationService;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * User anonymization service
 * 
 */
public class MyluteceDirectoryAnonymizationService implements IAnonymizationService
{

    public static final String BEAN_SERVICE = "mylutece-directory.anonymizationService";

    // PARAMETERS
    private static final String PARAMETER_USER_LOGIN = "user_login";

    // PROPERTIES
    private static final String PROPERTY_ANONYMIZATION_ENCRYPT_ALGO = "security.anonymization.encryptAlgo";

    // CONSTANTS
    private static final String CONSTANT_DEFAULT_ENCRYPT_ALGO = "SHA-256";

    /**
     * Get the plugin
     * @return The plugin
     */
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void anonymizeUser( Integer nUserId, Locale locale )
    {
        Plugin plugin = getPlugin( );
        Plugin pluginMyLutece = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nUserId, plugin );

        String strEncryptionAlgorithme = AppPropertiesService.getProperty( PROPERTY_ANONYMIZATION_ENCRYPT_ALGO,
                CONSTANT_DEFAULT_ENCRYPT_ALGO );

        Map<String, Boolean> anonymizationStatus = AttributeHome.getAnonymizationStatusUserStaticField( pluginMyLutece );

        if ( anonymizationStatus.get( PARAMETER_USER_LOGIN ) )
        {
            user.setLogin( CryptoService.encrypt( user.getLogin( ), strEncryptionAlgorithme ) );
        }
        user.setStatus( MyluteceDirectoryUser.STATUS_ANONYMIZED );

        MyluteceDirectoryHome.removeRolesForUser( nUserId, plugin );
        MyluteceDirectoryHome.removeGroupsForUser( nUserId, plugin );
        MyluteceDirectoryUserHome.update( user, plugin );

        List<IAttribute> listAllAttributes = AttributeHome.findAll( locale, pluginMyLutece );
        List<IAttribute> listAttributesText = new ArrayList<IAttribute>( );
        for ( IAttribute attribut : listAllAttributes )
        {
            if ( attribut.isAnonymizable( ) )
            {
                listAttributesText.add( attribut );
            }
        }

        for ( IAttribute attribute : listAttributesText )
        {
            List<MyLuteceUserField> listUserField = MyLuteceUserFieldHome.selectUserFieldsByIdUserIdAttribute( nUserId,
                    attribute.getIdAttribute( ), pluginMyLutece );

            for ( MyLuteceUserField userField : listUserField )
            {
                userField.setValue( CryptoService.encrypt( userField.getValue( ), strEncryptionAlgorithme ) );
                MyLuteceUserFieldHome.update( userField, pluginMyLutece );
            }
        }

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> entryList = EntryHome.getEntryListAnonymizeStatus( pluginDirectory );
        List<Integer> entryListId = new ArrayList<Integer>( );
        for ( IEntry entry : entryList )
        {
            if ( entry.getAnonymize( ) )
            {
                entryListId.add( entry.getIdEntry( ) );
            }
        }
        if ( entryListId.size( ) > 0 )
        {
            List<RecordField> recordFieldList = RecordFieldHome
                    .selectValuesList( entryListId, nUserId, pluginDirectory );
            for ( RecordField recordField : recordFieldList )
            {
                RecordFieldHome.updateValue( CryptoService.encrypt( recordField.getValue( ), strEncryptionAlgorithme ),
                        recordField.getIdRecordField( ), pluginDirectory );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getExpiredUserIdList( )
    {
        return MyluteceDirectoryUserHome.findAllExpiredUserId( getPlugin( ) );
    }
}
