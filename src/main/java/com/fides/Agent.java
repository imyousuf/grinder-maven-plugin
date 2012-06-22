package com.fides;

import net.grinder.common.GrinderException;
import net.grinder.engine.agent.AgentDaemon;
import net.grinder.engine.agent.AgentImplementation;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run agent process.
 * 
 * @goal agent
 * 
 * @author Giuseppe Iacono
 */
public class Agent extends GrinderPropertiesConfigure
{	
	// Agent logger
	private final Logger logger = LoggerFactory.getLogger("agent");

	/**
	 * Constructor
	 */
	public Agent() {
		super();
	}

	@Override
	protected String getJythonVersion() {		
		return GrinderPropertiesConfigure.GRINDER_JYTHON_VERSION;
	}
	
	public void execute()
	{
		try {
			super.execute();
		} catch (MojoExecutionException e1) {
			e1.printStackTrace();
		} catch (MojoFailureException e1) {
			e1.printStackTrace();
		}		
				
		AgentDaemon daemon_agent;			
		AgentImplementation default_agent;	
		try {
			if (isDaemonOption()) {
				if(logger.isDebugEnabled()){
					logger.debug("");
					logger.debug(" ---------------------------");
					logger.debug("|   Create an AgentDaemon   |");
					logger.debug(" ---------------------------");
				}
				
				daemon_agent =
					new AgentDaemon(
							logger, 
							getDaemonPeriod(),
						 	new AgentImplementation(logger, getFileProperties(), false));
				daemon_agent.run();
				daemon_agent.shutdown();
			} 
			else
			{
				if(logger.isDebugEnabled()){
					logger.debug("");
					logger.debug(" -----------------------------------");
					logger.debug("|   Create an AgentImplementation   |");
					logger.debug(" -----------------------------------");
				}								
				default_agent = new AgentImplementation(logger, getFileProperties(), true);
				default_agent.run();
				default_agent.shutdown();
			}
		} catch (GrinderException e) {
			e.printStackTrace();
		}
	}
}
