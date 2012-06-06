package com.fides;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate grinder build reports.
 * 
 * @goal report
 * 
 * @author Giuseppe Iacono
 */
public class Report extends GrinderPropertiesConfigure
{	
	// unjar directory
	private static final String JYTHON_DIR = "jython";				

	// Grinder Analyzer 
	private static final String JYTHON_FILE_NAME = "analyzer.py";	
	
	// Report logger
	private final Logger logger = LoggerFactory.getLogger(Report.class);
	
	public static String getJythonDir() {
		return JYTHON_DIR;
	}

	public static String getJythonFileName() {
		return JYTHON_FILE_NAME;
	}
	
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Constructor
	 */
	public Report() {
		super();
	}
    
    /**
	 * Create HTML reports of LOG_DIRECTORY's file
	 */
	private void jythonInterpreter() 
	{
		// Maven repository
		String currentDir = getCurrentDir();	
		
		String analyzerPyFile = currentDir + File.separator + JYTHON_FILE_NAME;
		
		InputStream stream1 = null;
		
		try {
			stream1 = new FileInputStream(analyzerPyFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		logger.debug("Try to find the file " + analyzerPyFile);
		
		// create cache directory
		Properties propertiesJython = new Properties();
		propertiesJython.put("python.cachedir",System.getProperty("java.io.tmpdir"));
		propertiesJython.put("os.path.curdir" , "'" + currentDir + "'");
		
		PythonInterpreter.initialize(System.getProperties(),propertiesJython, null ); 
		
		// create python interpreter
		PythonInterpreter interp = new PythonInterpreter();
		
		logger.debug("fullCurrentDir = {}",  currentDir);
		
		interp.exec("import sys");
		interp.exec("sys.path.append(\"" + currentDir + "\")");
		interp.exec("sys.path.append(\"" + currentDir + File.separator + "lib\")");
		
		interp.execfile(stream1, JYTHON_FILE_NAME);
	}
    
	/**
	 * @return the absolute path of unjar directory
	 */
	private String getCurrentDir()
	{
		InputStream mavenProperties = 
				this.getClass().getClassLoader().getResourceAsStream("grinderplugin.properties");
		
		Properties propertiesMaven = new Properties();
		
		try {
			propertiesMaven.load(mavenProperties);
			mavenProperties.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String groupId = propertiesMaven.getProperty("grinderplugin.project.groupId");
		String artifactId = propertiesMaven.getProperty("grinderplugin.project.artifactId");
		String version = propertiesMaven.getProperty("grinderplugin.project.version");
		
		String jarPath = null;
		
		try {			
			jarPath = MavenUtilities.getPluginAbsolutePath(groupId, artifactId, version);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		logger.debug("Jar Absolute Path: {}", jarPath);
		
		String directory = MavenUtilities.getCurrentDir();
		
		File jar_directory = new File(directory);
		
		// Delete the last unjar directory
		if (jar_directory.exists()){
			jar_directory.delete();		
		}
		
		// make sure the jar_directory exists
		if (jar_directory != null) {
			jar_directory.mkdirs();		
		}
		
		jar_directory.setExecutable(true);
		jar_directory.setWritable(true);
		jar_directory.setReadable(true);
		
		// extract jarpath file to jar_directory/JYTHON_DIR
		try {
			FileUtil.unJarDirectory(jarPath, jar_directory, JYTHON_DIR);	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("unjar: {}", jar_directory);
		
		return jar_directory + File.separator + JYTHON_DIR;
	}

	
	public void execute()
	{	
		File logDir = new File(getLOG_DIRECTORY());
		
		if (!logDir.exists()) {
			if(logger.isDebugEnabled()){
				logger.error("");
				logger.error(" -------------------------------------------");
				logger.error("|    " + getLOG_DIRECTORY() + " do not exists!   |");
				logger.error(" -------------------------------------------");
				System.exit(0);
			}
		}
		
		int logFiles = logDir.listFiles().length;
		
		if (logFiles == 0) {
			if(logger.isDebugEnabled()){
				logger.error("");
				logger.error(" -------------------------------------------------------");
				logger.error("|    Log directory is empty!   				|");
				logger.error("|    							|");
				logger.error("|    You must copy log files to " + getLOG_DIRECTORY() + "	|");
				logger.error("|    or run agent goal   				|");
				logger.error(" -------------------------------------------------------");
				System.exit(0);
			}
		}
			
		try {
			super.execute();
		} catch (MojoExecutionException e) {
			e.printStackTrace();
		} catch (MojoFailureException e) {
			e.printStackTrace();
		}	
		
		if(logger.isDebugEnabled()){
			logger.error("");
			logger.error("-----------------------------------------");
			logger.error("log_directory: {}", getLOG_DIRECTORY());
			logger.error("-----------------------------------------");		
		}
		
		jythonInterpreter();
	}
		
}
