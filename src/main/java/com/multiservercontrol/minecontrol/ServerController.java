package com.multiservercontrol.minecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ServerController {

    private static Logger LOGGER = Logger.getLogger(ServerController.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SHELL_BIN = "shell.bin";

    private ProcessBuilder processBuilder;
    private Configuration config;
    private String pathToShellBinary;

    public ServerController() {
	try {
	    this.config = new PropertiesConfiguration(CONFIG_FILE_NAME);
	    LOGGER.setLevel(Level.toLevel(this.config
		    .getString(CONFIG_LOGGER_LEVEL)));
	} catch (ConfigurationException e) {
	    // TODO log
	    System.out
		    .println("Class "
			    + this.getClass().getSimpleName()
			    + " can't be instantiated (error with PropertiesConfiguration): "
			    + e.getMessage());
	}
	this.processBuilder = new ProcessBuilder("");
	this.pathToShellBinary = this.config.getString(CONFIG_SHELL_BIN);
    }

    protected int getPid(String screenName) {
	int pid = 0;
	this.config.setProperty("screen.name", screenName);
	LOGGER.debug("getPid(): Set property 'screen.name' to " + screenName);
	String pidCommand = this.config.getString("command.pid");
	LOGGER.debug("Pid command: " + pidCommand);
	this.processBuilder.command(this.pathToShellBinary, "-c", pidCommand);

	try {
	    Process pidProcess = processBuilder.start();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		    pidProcess.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
		pid = Integer.parseInt(line);
		line = "";
	    }
	    // System.out.println(pid);
	    LOGGER.info("Server " + screenName + " is running under pid " + pid);
	} catch (Exception e) {
	    // System.out.println("Pid lookup failed: " + e.getMessage());
	    LOGGER.error("Pid lookup failed: " + e.getMessage());
	}
	return pid;
    }

    public boolean isRunning(String screenName) {
	if (this.getPid(screenName) != 0) {
	    System.out.println("Server " + screenName + "is running!");
	    // TODO
	    // LOGGER.info("Server " + screenName + " is online!");
	    return true;
	} else {
	    System.out.println("Server " + screenName + "isn't running!");
	    // LOGGER.info("Server " + screenName + " is offline!");
	    return false;
	}
    }

    public void start(String screenName) {
	if (!this.isRunning(screenName)) {
	    System.out.println("Starting server " + screenName + "...");
	    // LOGGER.info("Starting server " + screenName + "...");

	    this.config.setProperty("screen.name", screenName);
	    String startCommand = this.config.getString("command.start");
	    // LOGGER.debug("Start command: " + startCommand);

	    this.processBuilder.command(this.pathToShellBinary, "-c",
		    startCommand);
	    this.processBuilder.redirectErrorStream(true);

	    try {
		Process startProcess = processBuilder.start();
		System.out.println("Server " + screenName
			+ " started successfully!");
		// LOGGER.info("Server " + screenName +
		// " started successfully!");
	    } catch (IOException e) {
		System.out.println("Server " + screenName
			+ " startup failed! See logs for more information.");
		// LOGGER.error("Server " + screenName + " startup failed: "
		// + e.getMessage());
	    }
	} else {
	    System.out.println("Server " + screenName + " is already running!");
	    // LOGGER.warn("Server " + screenName + " is already running!");
	}
    }
}
