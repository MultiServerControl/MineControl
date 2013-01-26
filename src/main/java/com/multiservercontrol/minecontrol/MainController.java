package com.multiservercontrol.minecontrol;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MainController {

    private ProcessBuilder builder = new ProcessBuilder("");
    private Configuration config = null;

    public MainController() {
	try {
	    this.config = new PropertiesConfiguration("minecontrol.properties");
	} catch (ConfigurationException e) {
	    // TODO log
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	MainController controller = new MainController();
	String server = args[0];
	String command = args[1];

	if (command.equals("start")) {
	    controller.start(server);
	} else if (command.equals("stop")) {
	    controller.stop();
	} else if (command.equals("restart")) {
	    controller.restart();
	} else if (command.equals("status")) {
	    controller.isRunning();
	}
    }

    public void start(String serverId) {
	System.out.println("Starting server...");

	this.config.setProperty("server.name", serverId);
	String startCommand = this.config.getString("command.start");
	this.builder.command(this.parseCommand(startCommand));
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

    public void isRunning() {
	System.out.println("status");
    }

    protected ArrayList<String> parseCommand(String command) {
	ArrayList<String> arguments = new ArrayList<String>();
	String[] parts = command.split(" ");
	for (String part : parts) {
	    arguments.add(part);
	}
	return arguments;
    }
}
