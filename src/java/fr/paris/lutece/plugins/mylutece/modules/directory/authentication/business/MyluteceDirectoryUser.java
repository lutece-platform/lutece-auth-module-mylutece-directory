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

import fr.paris.lutece.plugins.directory.service.RecordRemovalListenerService;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.MyluteceDirectoryUserFieldListenerService;
import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;


/**
 * This class represents the business object DatabaseUser
 */
public class MyluteceDirectoryUser
{
    // Variables declarations
    private static MyluteceDirectoryUserRoleRemovalListener _listenerRole;
    private static MyluteceDirectoryUserRecordRemovalListener _listenerRecord;
    private static MyluteceDirectoryUserFieldListener _listenerField;
    private int _nIdRecord;
    private String _strLogin;
    private String _strEmail;
    private boolean _bIsActivated;

    /**
     * Initialize the MyluteceDirectoryUser
     */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerRole == null )
        {
            _listenerRole = new MyluteceDirectoryUserRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }

        // Create removal listeners for Record (Directory) and register them
        if ( _listenerRecord == null )
        {
            _listenerRecord = new MyluteceDirectoryUserRecordRemovalListener(  );
            RecordRemovalListenerService.getService(  ).registerListener( _listenerRecord );
        }
        
        // Create user field listeners
        if ( _listenerField == null )
    	{
        	_listenerField = new MyluteceDirectoryUserFieldListener(  );
    		MyluteceDirectoryUserFieldListenerService.getService(  ).registerListener( _listenerField );
    	}
    }

    /**
     * Returns the IdRecord
     *
     * @return The IdRecord
     */
    public int getIdRecord(  )
    {
        return _nIdRecord;
    }

    /**
     * Sets the IdRecord
     *
     * @param nIdRecord The IdRecord
     */
    public void setIdRecord( int nIdRecord )
    {
        _nIdRecord = nIdRecord;
    }

    /**
    * Returns the login
    *
    * @return The login
    */
    public String getLogin(  )
    {
        return _strLogin;
    }

    /**
     * Sets the login
     *
     * @param strLogin The login
     */
    public void setLogin( String strLogin )
    {
        _strLogin = strLogin;
    }
    
     /**
      * Set if the user is activated or not
      * @param isActivated the boolean 
      */
     public void setActivated( boolean isActivated) 
     {		
    	  _bIsActivated = isActivated;
     }
     
     /**
      * Return true if the user is validated
      * @return the boolean 
      */
     public boolean isActivated() 
     {		
    	 return _bIsActivated;
     }
}
