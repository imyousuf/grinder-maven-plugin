package com.fides;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide methods to configure the plug-in
 * 
 * @author Giuseppe Iacono
 */
public class GrinderPropertiesConfigure extends AbstractMojo
{
	// default agent
	private static final boolean DEFAULT_DAEMON_OPTION = false;						
	
	// default agent sleep time in milliseconds
	private static final long DEFAULT_DAEMON_PERIOD = 60000;							
	
	// default local path test directory
	private static final String PATH_TEST_DIR = "src/test/jython";  		
	
	// local configuration directory
	private static final String CONFIG = "target/test/config";				
	
	// local grinder properties directory
	private static final String PATH_PROPERTIES_DIR = "src/test/config"; 	
	
	// local log directory 
	private static final String LOG_DIRECTORY = "target/test/log_files"; 			
	
	// local tcpproxy directory
	private static final String TCP_PROXY_DIRECTORY = "target/test/tcpproxy";		
	
	// grinder properties
	private Properties propertiesPlugin = new Properties();					

	// configuration file
	private File fileProperties = null;										
	
	// grinder properties file path
	private String pathProperties = null;									

	// test path 
	private String test = null;												

	// value of agent daemon option
	private boolean daemonOption = DEFAULT_DAEMON_OPTION;							
	
	// value of agent sleep time
	private long daemonPeriod = DEFAULT_DAEMON_PERIOD;								 
	
	// GrinderPropertiesConfigure logger
	private final Logger logger = LoggerFactory.getLogger("GrinderPropertiesConfigure");
	
	/**
	 * List of properties defined in the pom.xml file of Maven project.
	 * 
	 * @parameter
	 */
	private Map<String, String> properties;

	/**
	 * The grinder properties file path defined in the pom.xml file of Maven project.
	 * 
	 * @parameter
	 */
	private String path;

	/**
	 * The absolute path of test script directory defined in the pom.xml file of Maven project.
	 * 
	 * @parameter
	 */
	private String pathTest;

	/**
	 * Agent daemon option defined in the pom.xml file of Maven project.
	 * 
	 * @parameter
	 */
	private boolean daemon_option;

	/**
	 * Agent sleep time in milliseconds defined in the pom.xml file of Maven project.
	 * 
	 * @parameter
	 */
	private long daemon_period;
	
	public static boolean getDEFAULT_DAEMON_OPTION() {
		return DEFAULT_DAEMON_OPTION;
	}
	
	public static long getDEFAULT_DAEMON_PERIOD() {
		return DEFAULT_DAEMON_PERIOD;
	}
	
	public static String getPATH_TEST_DIR() {
		return PATH_TEST_DIR;
	}
	
	public static String getCONFIG() {
		return CONFIG;
	}

	public static String getPATH_PROPERTIES_DIR() {
		return PATH_PROPERTIES_DIR;
	}

	public static String getLOG_DIRECTORY() {
		return LOG_DIRECTORY;
	}

	public static String getTCP_PROXY_DIRECTORY() {
		return TCP_PROXY_DIRECTORY;
	}	
	
	public Properties getPropertiesPlugin() {
		return propertiesPlugin;
	}

	public void setPropertiesPlugin(Properties propertiesPlugin) {
		this.propertiesPlugin = propertiesPlugin;
	}

	public File getFileProperties() {
		return fileProperties;
	}

	public void setFileProperties(File fileProperties) {
		this.fileProperties = fileProperties;
	}

	public String getPathProperties() {
		return pathProperties;
	}
	
	public void setPathProperties(String pathProperties) {
		this.pathProperties = pathProperties;
	}
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
	
	public boolean isDaemonOption() {
		return daemonOption;
	}

	public boolean getdaemonOption() {
		return daemonOption;
	}
	
	public void setDaemonOption(boolean daemonOption) {
		this.daemonOption = daemonOption;
	}

	public long getDaemonPeriod() {
		return daemonPeriod;
	}
	
	public void setDaemonPeriod(long daemonPeriod) {
		this.daemonPeriod = daemonPeriod;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Set grinder properties
	 */
	private void setProperties() 
	{		
		if (path == null) {		// try to find grinder properties file in the PATH_PROPERTIES_DIR
			
			File[] config = new File(PATH_PROPERTIES_DIR).listFiles();
			
			if (config == null) {
				if(logger.isDebugEnabled()){
					logger.error("");
					logger.error(" --------------------------------------------------------------------------");
					logger.error("|  " + PATH_PROPERTIES_DIR + " do not exists! 				  	   |");
					logger.error("|    									   |");
					logger.error("|  Create " + PATH_PROPERTIES_DIR + " directory and copy the grinder properties file,  |");
					logger.error("|  or set the grinder properties file from the POM file                    |");
					logger.error(" --------------------------------------------------------------------------");
					System.exit(0);
				}
			}
			
			if (config.length == 0 || config.length > 1) {
				if(logger.isDebugEnabled()){
					logger.error("");
					logger.error(" -----------------------------------------------------------------");
					logger.error("|    " + PATH_PROPERTIES_DIR + " must contain grinder properties file only!   |");
					logger.error(" -----------------------------------------------------------------");
					System.exit(0);
				}
			}
			String properties = config[0].getName();
			
			if (!properties.endsWith(".properties")) {
				if(logger.isDebugEnabled()){
					logger.error("");
					logger.error(" -------------------------------------------------------------------------------------");
					logger.error("    " + PATH_PROPERTIES_DIR + File.separator + properties  + " is not a grinder properties file! ");
					logger.error(" -------------------------------------------------------------------------------------");
					System.exit(0);
				}
			}
			String pathProp = PATH_PROPERTIES_DIR + File.separator + properties;
			setPathProperties(pathProp);
		}
		else {
			setPathProperties(path);	
		}
		
		// load grinder properties from the grinder properties file
		FileInputStream is = null;
		try {
			is = new FileInputStream(pathProperties);
			propertiesPlugin.load(is); 					
		} catch (FileNotFoundException e) {
			System.out.println(pathProperties + " file does not exist!!!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}		
		
		// load grinder properties from pom.xml file of Maven project
		if (properties != null) {
			Iterator <Map.Entry <String, String>> iterator = properties.entrySet().iterator();
			Map.Entry<String, String> pair;
			while (iterator.hasNext()) {						
				pair = iterator.next();
				propertiesPlugin.setProperty(pair.getKey(), pair.getValue());	
			}
		}
	}

	/**
	 * Set test file path to execute
	 */
	private void setTest() 
	{
		String nameTest;
		String nameScript = propertiesPlugin.getProperty("grinder.script");
		
		if (pathTest == null) {		//  try to find grinder test file in the PATH_TEST_DIR 
			
			File[] jython = new File(PATH_TEST_DIR).listFiles();
			
			if (jython == null) {
				if(logger.isDebugEnabled()){
					logger.error("");
					logger.error(" ------------------------------------------------------------");
					logger.error("|  " + PATH_TEST_DIR + " do not exists!			     |");
					logger.error("|    							     |");
					logger.error("|  Create " + PATH_TEST_DIR + " directory and copy the test file,  |");
					logger.error("|  or set the test file from the POM file                    |");
					logger.error(" ------------------------------------------------------------");
					System.exit(0);
				}
			}
			
			if (jython.length == 0 || jython.length > 1) {
				if (logger.isDebugEnabled()) {
					logger.error("");
					logger.error(" --------------------------------------------------");
					logger.error("|    " + PATH_TEST_DIR + " must contain test file only!  |");
					logger.error(" --------------------------------------------------");
					System.exit(0);
				}
			}
			if (nameScript == null) {
				nameTest = PATH_TEST_DIR + File.separator + 
						   "grinder.py";
				setTest(nameTest);
			}
			else {
				nameTest = PATH_TEST_DIR + File.separator + 
					   nameScript;
				setTest(nameTest);
			}
		}
		else {
			if (nameScript == null) {
				nameTest = pathTest + File.separator + 
						   "grinder.py";
				setTest(nameTest);
			}
			else { 
				nameTest = pathTest + File.separator + 	
						nameScript;					// absolute path of test file 
				setTest(nameTest);
			}
		}		
	}
	
	/**
	 * Set log directory
	 */
	private void setLogDirectory() 
	{
		// make sure the logDirectory exists
		File logDirectory = new File(LOG_DIRECTORY);
		if (logDirectory != null && !logDirectory.exists()) {
			logDirectory.mkdirs();
		}

		// set logDirectory
		propertiesPlugin.setProperty("grinder.logDirectory", LOG_DIRECTORY);
	}
	
	/**
	 * Set agent daemon option and sleep time
	 */
	private void setAgentOption()
	{
		if (daemon_option == true) {
			daemonOption = true;
			if (daemon_period > 0) {
				daemonPeriod = daemon_period;
			} 
			else {
				daemonPeriod = DEFAULT_DAEMON_PERIOD;
			}
		}
		else {
			daemonOption = DEFAULT_DAEMON_OPTION;
			daemonPeriod = DEFAULT_DAEMON_PERIOD;
		}
	}
	
	/**
	 * Initialize configuration directory
	 */
	private void initConfigurationDirectory()
	{
		// copy grinder properties to configuration directory
		String pathProperties = CONFIG + File.separator +
								"grinder_agent.properties";
		BufferedWriter out;

		try {
			out = new BufferedWriter(new FileWriter(pathProperties));
			propertiesPlugin.store(out, "Grinder Agent Properties");
		} catch (FileNotFoundException e) {
			System.out.println("File " + pathProperties + " does not exists!!!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create configuration file
		fileProperties = new File(pathProperties); 

		// copy test file to configuration directory
		String line = null;
		String dest_test = CONFIG + File.separator +
						   propertiesPlugin.getProperty("grinder.script");
		BufferedReader source = null;
		BufferedWriter dest = null;

		try {
			source = new BufferedReader(new FileReader(test));
			dest = new BufferedWriter(new FileWriter(dest_test));
			line = source.readLine();
			while (line != null) {
				dest.write(line + "\n");
				line = source.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (source != null || dest != null) {
				try {
					source.close();
					dest.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException 
	{
		setProperties();
		
		setTest();
		
		setLogDirectory();
		
		setAgentOption();
		
		// make sure the configDirectory exists
		File configDirectory = new File(CONFIG);	
		if (configDirectory != null && !configDirectory.exists()) {
			configDirectory.mkdirs();								
		}
		
		initConfigurationDirectory();	
	}	
}
