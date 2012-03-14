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
package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * FormErrors
 *
 */
public class FormErrors
{
    private Map<String, List<String>> _mapErrors;
    private Map<String, Object> _mapLastValues;

    /**
     * Constructor
     */
    public FormErrors(  )
    {
        _mapErrors = new HashMap<String, List<String>>(  );
        _mapLastValues = new HashMap<String, Object>(  );
    }

    /**
     * Check if there are errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasError(  )
    {
        return !_mapErrors.isEmpty(  );
    }

    /**
     * Check if the given key has error
     * @param strKey the key
     * @return true if there is an error, false otherwise
     */
    public boolean hasError( String strKey )
    {
        return _mapErrors.get( strKey ) != null;
    }

    /**
     * Add error in the list
     * @param strKey the key
     * @param strMessage the message
     */
    public void addError( String strKey, String strMessage )
    {
        List<String> listErrors = _mapErrors.get( strKey );

        if ( listErrors == null )
        {
            listErrors = new ArrayList<String>(  );
        }

        listErrors.add( strMessage );
        _mapErrors.put( strKey, listErrors );
    }

    /**
     * Add last value to the map
     * @param strKey the key
     * @param lastValue the last value
     */
    public void addLastValue( String strKey, Object lastValue )
    {
        _mapLastValues.put( strKey, lastValue );
    }

    /**
     * Get the map errors
     * @return the map errors
     */
    public Map<String, List<String>> getErrors(  )
    {
        return _mapErrors;
    }

    /**
     * Get the map last values
     * @return the map last values
     */
    public Map<String, Object> getLastValues(  )
    {
        return _mapLastValues;
    }

    /**
     * Get the last value from a given key
     * @param strKey the key
     * @return the last value
     */
    public Object getLastValue( String strKey )
    {
        return _mapLastValues.get( strKey );
    }
}
