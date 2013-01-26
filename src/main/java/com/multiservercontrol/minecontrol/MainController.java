package com.multiservercontrol.minecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MainController {

    private ProcessBuilder builder = new ProcessBuilder("");
    private Configuration config = null;
    private String pathToShellBinary = null;

    public MainController() {
	try {
	    this.config = new PropertiesConfiguration("minecontrol.properties");
	    this.pathToShellBinary = this.config.getString("shell.bin");
	} catch (ConfigurationException e) {
	    // TODO log
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	MainController controller = new MainController();
	String screenName = args[0];
	String command = args[1];

	if (command.equals("start")) {
	    controller.start(screenName);
	} else if (command.equals("stop")) {
	    controller.stop();
	} else if (command.equals("restart")) {
	    controller.restart();
	} else if (command.equals("status")) {
	    controller.isRunning(screenName);
	} else if (command.equals("pid")) {
	    controller.getPid(screenName);
	}
    }

    public void start(String screenName) {
	System.out.println("Starting server...");

	this.config.setProperty("screen.name", screenName);
	String startCommand = this.config.getString("command.start");

	this.builder.command(this.pathToShellBinary, "-c", startCommand);
	this.builder.redirectErrorStream(true);

	try {
	    Process p = builder.start();
	    System.out.println("Server startup successful!");
	} catch (IOException e) {
	    // TODO
	    System.out.println("Server startup failed!");
	}
    }

    public void stop() {
	System.out.println("stop");
    }

    public void restart() {
	System.out.println("restart");
    }

    public void isRunning(String screenName) {
	System.out.println("status");
	if (this.getPid(screenName) != 0) {
	    System.out.println("running");
	} else
	    System.out.println("stopped");
    }

    protected int getPid(String screenName) {
	int pid = 0;
	this.config.setProperty("screen.name", screenName);
	String pidCommand = this.config.getString("command.pid");
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

	    System.out.println(pid);

	} catch (Exception e) {
	    // TODO
	    System.out.println("Pid lookup failed: " + e.getMessage());
	}
	return pid;
    }
}
