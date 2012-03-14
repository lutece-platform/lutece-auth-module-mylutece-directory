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

import javax.servlet.http.HttpServletRequest;


/**
 *
 * IMyluteceDirectoryUserKeyService
 *
 */
public interface IMyluteceDirectoryUserKeyService
{
    /**
     * Create a new user key from a given user id
     * @param nIdRecord the id record
     * @return the key
     */
    MyluteceDirectoryUserKey create( int nIdRecord );

    /**
     * Find the key
     * @param strKey the key
     * @return the key
     */
    MyluteceDirectoryUserKey findByPrimaryKey( String strKey );

    /**
     * Remove a key
     * @param strKey the key
     */
    void remove( String strKey );

    /**
     * Remove a key from a given id user
     * @param nIdRecord the id record
     */
    void removeByIdRecord( int nIdRecord );

    // GET

    /**
     * Build the validation url
     * @param strKey the key
     * @param request the HTTP request
     * @return the validation url
     */
    String getValidationUrl( String strKey, HttpServletRequest request );

    /**
     * Get reinit url
     * @param strKey the key
     * @param request the HTTP request
     * @return the url
     */
    String getReinitUrl( String strKey, HttpServletRequest request );
}
