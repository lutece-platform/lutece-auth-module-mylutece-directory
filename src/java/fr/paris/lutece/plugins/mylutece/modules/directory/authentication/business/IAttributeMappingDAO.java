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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 *
 * IAttributeMappingDAO
 *
 */
public interface IAttributeMappingDAO
{
    /**
     * Delete the record
     * @param nIdEntry The id of the entry
     * @param plugin The plugin
     */
    void delete( int nIdEntry, Plugin plugin );

    /**
     * Delete all records
     * @param plugin The plugin
     */
    void deleteAll( Plugin plugin );

    /**
     * Find All mappings
     * @param plugin The {@link Plugin}
     * @return The {@link Collection} of {@link AttributeMapping}
     */
    Collection<AttributeMapping> findAll( Plugin plugin );

    /**
     * Find the {@link AttributeMapping} for the specified entry id
     * @param nIdEntry The id of entry
     * @param plugin The {@link Plugin}
     * @return The {@link AttributeMapping} of null
     */
    AttributeMapping findByPrimaryKey( int nIdEntry, Plugin plugin );

    /**
     * Find the {@link AttributeMapping} list for the specified attributeKey
     * @param strAttributeKey The attribute code
     * @param plugin The {@link Plugin}
     * @return The {@link AttributeMapping} or null
     */
    AttributeMapping findByAttributeKey( String strAttributeKey, Plugin plugin );

    /**
     * Insert a new {@link AttributeMapping}
     * @param attributeMapping The new {@link AttributeMapping}
     * @param plugin The {@link Plugin}
     */
    void insert( AttributeMapping attributeMapping, Plugin plugin );
}
