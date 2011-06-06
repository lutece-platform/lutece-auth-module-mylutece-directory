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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * @author ELY
 *
 */
public class AttributeMappingDAO implements IAttributeMappingDAO
{
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_entry, attribute_key FROM mylutece_directory_mapping ";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = " SELECT attribute_key FROM mylutece_directory_mapping WHERE id_entry = ? ";
    private static final String SQL_QUERY_SELECT_BY_ATTRIBUTE_KEY = " SELECT id_entry FROM mylutece_directory_mapping WHERE attribute_key = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_directory_mapping ( id_entry, attribute_key ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_directory_mapping WHERE id_entry = ? ";
    private static final String SQL_QUERY_DELETE_ALL = " DELETE FROM mylutece_directory_mapping ";

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#delete(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void delete( int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdEntry );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#deleteAll(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteAll( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL, plugin );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#findAll(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<AttributeMapping> findAll( Plugin plugin )
    {
        Collection<AttributeMapping> mappingList = new ArrayList<AttributeMapping>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            AttributeMapping mapping = new AttributeMapping(  );
            mapping.setIdEntry( daoUtil.getInt( 1 ) );
            mapping.setAttributeKey( daoUtil.getString( 2 ) );
            mappingList.add( mapping );
        }

        daoUtil.free(  );

        return mappingList;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#findByPrimaryKey(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public AttributeMapping findByPrimaryKey( int nIdEntry, Plugin plugin )
    {
        AttributeMapping mapping = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdEntry );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            mapping = new AttributeMapping(  );
            mapping.setIdEntry( nIdEntry );
            mapping.setAttributeKey( daoUtil.getString( 1 ) );
        }

        daoUtil.free(  );

        return mapping;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#findByAttributeKey(java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public AttributeMapping findByAttributeKey( String strAttributeKey, Plugin plugin )
    {
        AttributeMapping mapping = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ATTRIBUTE_KEY, plugin );
        daoUtil.setString( 1, strAttributeKey );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            mapping = new AttributeMapping(  );
            mapping.setIdEntry( daoUtil.getInt( 1 ) );
            mapping.setAttributeKey( strAttributeKey );
        }

        daoUtil.free(  );

        return mapping;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.IAttributeMappingDAO#insert(fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.AttributeMapping, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void insert( AttributeMapping attributeMapping, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, attributeMapping.getIdEntry(  ) );
        daoUtil.setString( 2, attributeMapping.getAttributeKey(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
