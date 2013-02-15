package com.multiservercontrol.minecontrol;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ServerMessenger {

    private static Logger LOGGER = Logger.getLogger(ServerMessenger.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SHELL_BIN = "shell.bin";

    private ProcessBuilder processBuilder;
    private Configuration config;
    private String pathToShellBinary;

    public ServerMessenger() {
	try {
	    this.config = new PropertiesConfiguration(CONFIG_FILE_NAME);
	    String logLevel = this.config.getString(CONFIG_LOGGER_LEVEL);
	    LOGGER.setLevel(Level.toLevel(logLevel));
	    LOGGER.debug("Log level: " + logLevel);
	} catch (ConfigurationException e) {
	    System.out
		    .println("Fatal error: Can't run MineControl library, see log files for more information.");
	    LOGGER.error("Class "
		    + this.getClass().getSimpleName()
		    + " can't be instantiated (error with PropertiesConfiguration): "
		    + e.getMessage());
	}
	this.processBuilder = new ProcessBuilder("");
	this.pathToShellBinary = this.config.getString(CONFIG_SHELL_BIN);
	LOGGER.debug("Path to shell binary: " + pathToShellBinary);
    }

    public void sendServerCommand(String screenName, String command) {
	this.config.setProperty("screen.name", screenName);
	LOGGER.debug("sendServerCommand(): Set property 'screen.name' to "
		+ screenName);
	this.config.setProperty("messenger.argument", command);
	LOGGER.debug("sendServerCommand(): Set property 'messenger.argument' to "
		+ command);
	String messengerCommand = this.config.getString("command.messenger");
	LOGGER.debug("messenger command: " + messengerCommand);

	System.out.println("Sending command '" + command + "' to server "
		+ screenName);
	this.processBuilder.command(this.pathToShellBinary, "-c",
		messengerCommand);
	try {
	    Process messengerProcess = this.processBuilder.start();
	} catch (IOException e) {
	    LOGGER.error("Sending server command failed: " + e.getMessage());
	}
    }
}
