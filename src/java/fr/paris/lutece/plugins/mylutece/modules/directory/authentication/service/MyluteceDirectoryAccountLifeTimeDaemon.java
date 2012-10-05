package fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service;

import fr.paris.lutece.plugins.mylutece.modules.directory.authentication.service.parameter.MyluteceDirectoryParameterService;
import fr.paris.lutece.plugins.mylutece.service.AbstractAccountLifeTimeDaemon;
import fr.paris.lutece.plugins.mylutece.service.IAccountLifeTimeService;
import fr.paris.lutece.plugins.mylutece.service.IUserParameterService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * Account life time daemon of module mylutece directory
 */
public class MyluteceDirectoryAccountLifeTimeDaemon extends AbstractAccountLifeTimeDaemon
{
	private static final String DAEMON_NAME = "MyluteceDirectoryAccountLifeTimeDaemon";

    MyluteceDirectoryParameterService _parameterService = SpringContextService
            .<MyluteceDirectoryParameterService> getBean( MyluteceDirectoryParameterService.BEAN_SERVICE );

    /**
     * Default constructor
     */
    public MyluteceDirectoryAccountLifeTimeDaemon( )
    {
        super( );
        setPluginName( MyluteceDirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAccountLifeTimeService getAccountLifeTimeService( )
    {
        return new MyluteceDirectoryAccountLifeTimeService( );
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public IUserParameterService getParameterService( )
    {
        return _parameterService;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDaemonName( )
	{
		return DAEMON_NAME;
	}
}
