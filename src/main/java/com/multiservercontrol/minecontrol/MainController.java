package com.multiservercontrol.minecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MainController {

    private ProcessBuilder builder = new ProcessBuilder("");
    private static Logger logger = Logger.getLogger(MainController.class);
    private Configuration config = null;
    private String pathToShellBinary = null;

    public MainController() {
	try {
	    this.config = new PropertiesConfiguration("minecontrol.properties");
	} catch (ConfigurationException e) {
	    logger.error("Constructor call failed! ", e);
	}
	String loglevel = this.config.getString("log.level");
	logger.setLevel(Level.toLevel(loglevel));
	this.pathToShellBinary = this.config.getString("shell.bin");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	BasicConfigurator.configure();
	MainController controller = new MainController();
	// TODO
	String screenName = args[0];
	String command = args[1];
	String serverCommand = args[2];

	if (command.equals("start")) {
	    controller.start(screenName);
	} else if (command.equals("stop")) {
	    controller.stop(screenName);
	} else if (command.equals("restart")) {
	    controller.restart(screenName);
	} else if (command.equals("status")) {
	    controller.isRunning(screenName);
	} else if (command.equals("pid")) {
	    controller.getPid(screenName);
	} else if (command.equals("command")) {
	    controller.sendServerCommand(screenName, serverCommand);
	}
    }

    public void start(String screenName) {
	if (!this.isRunning(screenName)) {
	    // System.out.println("Starting server...");
	    logger.info("Starting server" + screenName + "...");

	    this.config.setProperty("screen.name", screenName);
	    String startCommand = this.config.getString("command.start");
	    logger.debug("Start command: " + startCommand);

	    this.builder.command(this.pathToShellBinary, "-c", startCommand);
	    this.builder.redirectErrorStream(true);

	    try {
		Process p = builder.start();
		// System.out.println("Server startup successful!");
		logger.info("Server " + screenName + " started successfully!");
	    } catch (IOException e) {
		// System.out.println("Server startup failed!");
		logger.error("Server " + screenName + " startup failed: "
			+ e.getMessage());
	    }
	} else {
	    // System.out.println("Server already running");
	    logger.warn("Server " + screenName + " is already running!");
	}
    }

    public void stop(String screenName) {
	if (this.isRunning(screenName)) {
	    // System.out.println("stop");
	    logger.info("Stopping server " + screenName + "...");
	    this.sendServerCommand(screenName, "stop");
	} else {
	    // System.out.println("no server running");
	    logger.warn("Server " + screenName + "isn't running!");
	}
    }

    public void restart(String screenName) {
	// System.out.println("restart");
	logger.info("Restarting Server " + screenName);
	this.stop(screenName);
	this.start(screenName);
    }

    public boolean isRunning(String screenName) {
	// System.out.println("status");
	if (this.getPid(screenName) != 0) {
	    logger.info("Server " + screenName + "is online!");
	    return true;
	} else {
	    logger.info("Server " + screenName + "is offline!");
	    return false;
	}
    }

    protected int getPid(String screenName) {
	int pid = 0;
	this.config.setProperty("screen.name", screenName);
	logger.debug("Set property 'screen.name' to " + screenName);
	String pidCommand = this.config.getString("command.pid");
	logger.debug("Pid command: " + pidCommand);
	this.builder.command(this.pathToShellBinary, "-c", pidCommand);

	try {
	    Process p = builder.start();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		    p.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
		pid = Integer.parseInt(line);
		line = "";
	    }
	    // System.out.println(pid);
	    logger.info("Server " + screenName + "is running under pid " + pid);
	} catch (Exception e) {
	    // System.out.println("Pid lookup failed: " + e.getMessage());
	    logger.error("Pid lookup failed: " + e.getMessage());
	}
	return pid;
    }

    protected void sendServerCommand(String screenName, String serverCommand) {
	this.config.setProperty("screen.name", screenName);
	logger.debug("Set property 'screen.name' to " + screenName);
	this.config.setProperty("transmitter.argument", serverCommand);
	logger.debug("Set property 'transmitter.argument' to " + serverCommand);

	String transmitterCommand = this.config
		.getString("command.transmitter");
	logger.debug("Command to pass through: " + transmitterCommand);
	this.builder.command(this.pathToShellBinary, "-c", transmitterCommand);

	try {
	    Process p = builder.start();
	} catch (IOException e) {
	    // System.out.println(e.getMessage());
	    logger.error("Command transmition failed: " + e.getMessage());
	}
    }
}
