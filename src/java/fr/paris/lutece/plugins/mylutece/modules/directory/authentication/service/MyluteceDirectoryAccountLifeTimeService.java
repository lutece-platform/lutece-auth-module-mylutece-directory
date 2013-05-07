package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUser;
import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.business.MyluteceDirectoryUserHome;
import fr.paris.lutece.plugins.mylutece.service.IAccountLifeTimeService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Account life time service
 * 
 */
public class MyluteceDirectoryAccountLifeTimeService implements IAccountLifeTimeService
{

    private static final String PARAMETER_MYLUTECE_DIRECTORY_EXPIRATION_MAIL = "mylutece_directory_expiration_mail";
    private static final String PARAMETER_MYLUTECE_DIRECTORY_FIRST_ALERT_MAIL = "mylutece_directory_first_alert_mail";
    private static final String PARAMETER_MYLUTECE_DIRECTORY_OTHER_ALERT_MAIL = "mylutece_directory_other_alert_mail";
    private static final String PARAMETER_ENTRY_TYPE_MAIL_CLASS_NAME = "mylutece-directory.entryTypeMailClassName";
	private static final String PARAMETER_NOTIFY_PASSWORD_EXPIRED = "mylutece_directory_password_expired";

    private static final String MARK_LOGIN = "login";
    private static final String MARK_DATE_VALID = "date_valid";
    private static final String MARK_URL = "url";
    private static final String MARK_USER_ID = "user_id";
    private static final String MARK_REF = "ref";

    private static final String PROPERTY_ACCOUNT_REF_ENCRYPT_ALGO = "mylutece-directory.account_life_time.refEncryptionAlgorythm";

    private static final String JSP_URL_REACTIVATE_ACCOUNT = "/jsp/site/Portal.jsp?page=mylutecedirectory&action=updateAccount";
    private static final String CONSTANT_AND = "&";
    private static final String CONSTANT_EQUAL = "=";

    private List<Integer> listMailEntryId = new ArrayList<Integer>( );

    /**
     * Get the plugin
     * @return The plugin
     */
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( MyluteceDirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * Default constructor
     */
    public MyluteceDirectoryAccountLifeTimeService( )
    {
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> entryList = EntryHome.getEntryList( new EntryFilter( ), directoryPlugin );
        String entryTypeMailClassName = AppPropertiesService.getProperty( PARAMETER_ENTRY_TYPE_MAIL_CLASS_NAME );
        for ( IEntry entry : entryList )
        {
            if ( StringUtils.equals( entry.getEntryType( ).getClassName( ), entryTypeMailClassName ) )
            {
                listMailEntryId.add( entry.getIdEntry( ) );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersWithExpiredLifeTimeList( Timestamp currentTimestamp )
    {
        return MyluteceDirectoryUserHome.getIdUsersWithExpiredLifeTimeList( currentTimestamp, getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersToSendFirstAlert( Timestamp alertMaxDate )
    {
        return MyluteceDirectoryUserHome.getIdUsersToSendFirstAlert( alertMaxDate, getPlugin( ) );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getIdUsersToSendOtherAlert( Timestamp alertMaxDate, Timestamp timeBetweenAlerts,
            int maxNumberAlerts )
    {
        return MyluteceDirectoryUserHome.getIdUsersToSendOtherAlert( alertMaxDate, timeBetweenAlerts, maxNumberAlerts,
                getPlugin( ) );
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getIdUsersWithExpiredPasswordsList( Timestamp currentTimestamp )
	{
		return MyluteceDirectoryUserHome.getIdUsersWithExpiredPasswordsList( currentTimestamp, getPlugin( ) );
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNbAlert( List<Integer> listIdUser )
    {
        MyluteceDirectoryUserHome.updateNbAlert( listIdUser, getPlugin( ) );
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateChangePassword( List<Integer> listIdUser )
	{
		MyluteceDirectoryUserHome.updateChangePassword( listIdUser, getPlugin( ) );
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserStatusExpired( List<Integer> listIdUser )
    {
        MyluteceDirectoryUserHome.updateUserStatus( listIdUser, MyluteceDirectoryUser.STATUS_EXPIRED, getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExpirationtMailBody( )
    {
        return DatabaseTemplateService.getTemplateFromKey( PARAMETER_MYLUTECE_DIRECTORY_EXPIRATION_MAIL );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstAlertMailBody( )
    {
        return DatabaseTemplateService.getTemplateFromKey( PARAMETER_MYLUTECE_DIRECTORY_FIRST_ALERT_MAIL );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOtherAlertMailBody( )
    {
        return DatabaseTemplateService.getTemplateFromKey( PARAMETER_MYLUTECE_DIRECTORY_OTHER_ALERT_MAIL );
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPasswordExpiredMailBody( )
	{
		return DatabaseTemplateService.getTemplateFromKey( PARAMETER_NOTIFY_PASSWORD_EXPIRED );
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void addParametersToModel( Map<String, String> model, Integer nIdUser )
    {
        MyluteceDirectoryUser user = MyluteceDirectoryUserHome.findByPrimaryKey( nIdUser, getPlugin( ) );
        DateFormat dateFormat = SimpleDateFormat.getDateInstance( DateFormat.SHORT, Locale.getDefault( ) );

        String accountMaxValidDate = dateFormat.format( new Date( user.getAccountMaxValidDate( ).getTime( ) ) );

        StringBuilder sbUrl = new StringBuilder( );
        // FIXME : get base URL in case the prod URL is null
        sbUrl.append( AppPathService.getProdUrl( ) );
        sbUrl.append( JSP_URL_REACTIVATE_ACCOUNT );
        sbUrl.append( CONSTANT_AND );
        sbUrl.append( MARK_USER_ID );
        sbUrl.append( CONSTANT_EQUAL );
        sbUrl.append( nIdUser.toString( ) );
        sbUrl.append( CONSTANT_AND );
        sbUrl.append( MARK_REF );
        sbUrl.append( CONSTANT_EQUAL );
        sbUrl.append( CryptoService.encrypt( Long.toString( user.getAccountMaxValidDate( ).getTime( ) ),
                AppPropertiesService.getProperty( PROPERTY_ACCOUNT_REF_ENCRYPT_ALGO ) ) );

        String activationURL = sbUrl.toString( );

        model.put( MARK_DATE_VALID, accountMaxValidDate );
        model.put( MARK_URL, activationURL );
        model.put( MARK_LOGIN, user.getLogin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserMainEmail( int nIdUser )
    {
        if ( listMailEntryId != null && listMailEntryId.size( ) > 0 )
        {
            Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            List<RecordField> recordFieldList = RecordFieldHome.selectValuesList( listMailEntryId, nIdUser,
                    directoryPlugin );
            if ( recordFieldList == null )
            {
                return null;
            }
            int i = 0;
            while ( i < recordFieldList.size( ) && StringUtils.isEmpty( recordFieldList.get( i ).getValue( ) ) )
            {
                i++;
            }
            if ( i < recordFieldList.size( ) )
            {
                return recordFieldList.get( i ).getValue( );
            }
        }
        return null;
    }

}
