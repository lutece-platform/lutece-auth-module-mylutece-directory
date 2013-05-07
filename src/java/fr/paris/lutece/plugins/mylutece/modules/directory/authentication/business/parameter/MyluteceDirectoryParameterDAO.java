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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.parameter;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * MyluteceDirectoryParameterDAO
 *
 */
public class MyluteceDirectoryParameterDAO implements IMyluteceDirectoryParameterDAO
{
    private static final String SQL_QUERY_SELECT_PARAMETERS_VALUE = " SELECT parameter_value FROM mylutece_directory_parameter WHERE parameter_key = ? ";
    private static final String SQL_QUERY_UPDATE_PARAMETERS = " UPDATE mylutece_directory_parameter SET parameter_value = ? WHERE parameter_key = ? ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT parameter_key, parameter_value FROM mylutece_directory_parameter ";
    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceItem load( String strParameterKey, Plugin plugin )
    {
        ReferenceItem userParam = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PARAMETERS_VALUE, plugin );
        daoUtil.setString( 1, strParameterKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            userParam = new ReferenceItem(  );
            userParam.setCode( strParameterKey );
            userParam.setName( daoUtil.getString( 1 ) );
            userParam.setChecked( Boolean.valueOf( userParam.getName(  ) ) );
        }

        daoUtil.free(  );

        return userParam;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( ReferenceItem param, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_PARAMETERS, plugin );

        daoUtil.setString( 1, param.getName(  ) );
        daoUtil.setString( 2, param.getCode(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public ReferenceList selectAll( Plugin plugin )
    {
        ReferenceList listUserParams = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            ReferenceItem userParam = new ReferenceItem( );
            userParam.setCode( daoUtil.getString( nIndex++ ) );
            userParam.setName( daoUtil.getString( nIndex++ ) );
            userParam.setChecked( Boolean.valueOf( userParam.getName( ) ) );
            listUserParams.add( userParam );
        }

        daoUtil.free( );

        return listUserParams;
    }
}
