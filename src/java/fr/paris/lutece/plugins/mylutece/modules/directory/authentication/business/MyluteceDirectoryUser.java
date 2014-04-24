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

import java.sql.Timestamp;


/**
 * This class represents the business object DatabaseUser
 */
public class MyluteceDirectoryUser
{
	// Variables declarations
	/**
	 * Status of not activated users.
	 */
	public static final int STATUS_NOT_ACTIVATED = 0;
	/**
	 * Status of activated users.
	 */
	public static final int STATUS_ACTIVATED = 1;
	/**
	 * Status of expired users. Expired users will be anonymized.
	 */
	public static final int STATUS_EXPIRED = 5;
	/**
	 * Status of anonymized users.
	 */
	public static final int STATUS_ANONYMIZED = 10;
	private int _nIdRecord;
	private String _strLogin;
	private String _strPassword;
	private boolean _bIsPasswordReset;
	private int _nStatus;
	private Timestamp _passwordMaxValidDate;
	private Timestamp _accountMaxValidDate;
	private Timestamp _dateLastLogin;

	/**
	 * Returns the IdRecord
	 * 
	 * @return The IdRecord
	 */
	public int getIdRecord( )
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
	public String getLogin( )
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
	 * Returns the user's password
	 * 
	 * @param The password
	 */
	public String getPassword( )
	{
		return _strPassword;
	}

	/**
	 * Sets the password
	 * 
	 * @param strPassword The password
	 */
	public void setPassword( String strPassword )
	{
		_strPassword = strPassword;
	}

	/**
	 * Check if the password has been reinitialized
	 * @return true if it has been reinitialized, false otherwise
	 */
	public boolean isPasswordReset( )
	{
		return _bIsPasswordReset;
	}

	/**
	 * Set pwd reseted
	 * @param bIsPasswordReset true if it has been reinitialized, false otherwise
	 */
	public void setPasswordReset( boolean bIsPasswordReset )
	{
		_bIsPasswordReset = bIsPasswordReset;
	}

	/**
	 * Get the status of the user
	 * @return The status of the user
	 */
	public int getStatus( )
	{
		return _nStatus;
	}

	/**
	 * Set the status of the user
	 * @param nStatus The status
	 */
	public void setStatus( int nStatus )
	{
		_nStatus = nStatus;
	}

	/**
	 * Return true if the user is activated
	 * @return the boolean
	 */
	public boolean isActivated( )
	{
		return ( _nStatus >= STATUS_ACTIVATED && _nStatus < STATUS_EXPIRED );
	}

	/**
	 * Sets the activated.
	 * 
	 * @param bIsActivated the new activated
	 * @deprecated Use {@link #setStatus(int)} instead
	 */
	@Deprecated
	public void setActivated( boolean bIsActivated )
	{
		if ( bIsActivated )
		{
			_nStatus = STATUS_ACTIVATED;
		}
		else
		{
			_nStatus = STATUS_NOT_ACTIVATED;
		}
	}

	/**
	 * Get the maximum valid date of the password of the user
	 * @return The maximum valid date of the password of the user
	 */
	public Timestamp getPasswordMaxValidDate( )
	{
		return _passwordMaxValidDate;
	}

	/**
	 * Set the maximum valid date of the password of the user
	 * @param passwordMaxValidDate The maximum valid date
	 */
	public void setPasswordMaxValidDate( Timestamp passwordMaxValidDate )
	{
		_passwordMaxValidDate = passwordMaxValidDate;
	}

	/**
	 * Get the maximum valid date of the account of the user
	 * @return The maximum valid date of the account of the user
	 */
	public Timestamp getAccountMaxValidDate( )
	{
		return _accountMaxValidDate;
	}

	/**
	 * Set the maximum valid date of the account of the user
	 * @param accountMaxValidDate The maximum valid date
	 */
	public void setAccountMaxValidDate( Timestamp accountMaxValidDate )
	{
		_accountMaxValidDate = accountMaxValidDate;
	}

	/**
	 * Get the last login date of the account of the user
	 * @return The last login date of the account of the user
	 */
	public Timestamp getDateLastLogin( )
	{
		return _dateLastLogin;
	}

	/**
	 * Set the last login date of the account of the user
	 * @param dateLastLogin The last login date of the account of the user
	 */
	public void setDateLastLogin( Timestamp dateLastLogin )
	{
		_dateLastLogin = dateLastLogin;
	}
}
